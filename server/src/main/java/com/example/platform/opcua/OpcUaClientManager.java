package com.example.platform.opcua;

import com.example.platform.config.IotProperties;
import jakarta.annotation.PreDestroy;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.config.OpcUaClientConfigBuilder;
import org.eclipse.milo.opcua.sdk.client.api.identity.AnonymousProvider;
import org.eclipse.milo.opcua.sdk.client.api.identity.UsernameProvider;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaMonitoredItem;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaSubscription;
import org.eclipse.milo.opcua.stack.client.DiscoveryClient;
import org.eclipse.milo.opcua.stack.core.AttributeId;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.types.builtin.*;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.enumerated.*;
import org.eclipse.milo.opcua.stack.core.types.structured.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;

import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;

@Component
public class OpcUaClientManager {

    private static final Logger log = LoggerFactory.getLogger(OpcUaClientManager.class);

    private final IotProperties iotProperties;
    private volatile OpcUaClient client;
    private volatile UaSubscription subscription;
    private final AtomicBoolean connected = new AtomicBoolean(false);
    private final ScheduledExecutorService reconnectExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "opcua-reconnect");
        t.setDaemon(true);
        return t;
    });
    private BiConsumer<String, DataValue> dataChangeListener;
    /** 每次连接成功（含重连）后执行，用于重新订阅等 */
    private volatile Runnable afterConnected;

    public OpcUaClientManager(IotProperties iotProperties) {
        this.iotProperties = iotProperties;
    }

    public void setDataChangeListener(BiConsumer<String, DataValue> listener) {
        this.dataChangeListener = listener;
    }

    public void setAfterConnected(Runnable afterConnected) {
        this.afterConnected = afterConnected;
    }

    public boolean isConnected() {
        return connected.get();
    }

    public CompletableFuture<Void> connect() {
        IotProperties.Kepserver config = iotProperties.getKepserver();
        if (!config.isEnabled() || config.getEndpoint() == null || config.getEndpoint().isBlank()) {
            log.warn("KEPServer 未启用或 endpoint 为空，跳过 OPC-UA 连接");
            return CompletableFuture.completedFuture(null);
        }

        return CompletableFuture.runAsync(() -> {
            try {
                doConnect(config);
            } catch (Exception e) {
                log.error("OPC-UA 连接失败: {}", e.getMessage(), e);
                scheduleReconnect();
            }
        });
    }

    private void doConnect(IotProperties.Kepserver config) throws Exception {
        log.info("正在连接 KEPServer OPC-UA: {}", config.getEndpoint());

        var endpoints = DiscoveryClient.getEndpoints(config.getEndpoint()).get(config.getTimeoutMs(), TimeUnit.MILLISECONDS);
        var endpoint = endpoints.stream()
                .filter(e -> e.getSecurityPolicyUri().equals("http://opcfoundation.org/UA/SecurityPolicy#None"))
                .findFirst()
                .orElse(endpoints.get(0));

        var configBuilder = new OpcUaClientConfigBuilder()
                .setApplicationName(LocalizedText.english("CoalPlatform"))
                .setApplicationUri("urn:coal-platform:client")
                .setEndpoint(endpoint)
                .setRequestTimeout(uint(config.getTimeoutMs()));

        if (config.getUsername() != null && !config.getUsername().isBlank()) {
            configBuilder.setIdentityProvider(new UsernameProvider(config.getUsername(), config.getPassword()));
        } else {
            configBuilder.setIdentityProvider(new AnonymousProvider());
        }

        client = OpcUaClient.create(configBuilder.build());
        client.connect().get(config.getTimeoutMs(), TimeUnit.MILLISECONDS);
        connected.set(true);
        log.info("OPC-UA 连接成功: {}", config.getEndpoint());
        Runnable task = afterConnected;
        if (task != null) {
            try {
                task.run();
            } catch (Exception e) {
                log.error("OPC-UA 连接成功后的初始化任务失败: {}", e.getMessage(), e);
            }
        }
    }

    public CompletableFuture<List<ReferenceDescription>> browse(NodeId nodeId) {
        if (!connected.get() || client == null) {
            return CompletableFuture.completedFuture(Collections.emptyList());
        }

        var browseDescription = new BrowseDescription(
                nodeId,
                BrowseDirection.Forward,
                Identifiers.References,
                true,
                uint(NodeClass.Object.getValue() | NodeClass.Variable.getValue()),
                uint(BrowseResultMask.All.getValue())
        );

        return client.browse(browseDescription).thenApply(result -> {
            if (result.getReferences() == null) return Collections.<ReferenceDescription>emptyList();
            return List.of(result.getReferences());
        });
    }

    public CompletableFuture<List<ReferenceDescription>> browseRecursive(NodeId rootId, int maxDepth) {
        List<ReferenceDescription> allRefs = new CopyOnWriteArrayList<>();
        return browseRecursiveInternal(rootId, maxDepth, 0, allRefs)
                .thenApply(v -> allRefs);
    }

    private CompletableFuture<Void> browseRecursiveInternal(NodeId nodeId, int maxDepth, int currentDepth, List<ReferenceDescription> collector) {
        if (currentDepth >= maxDepth) return CompletableFuture.completedFuture(null);

        return browse(nodeId).thenCompose(refs -> {
            collector.addAll(refs);
            List<CompletableFuture<Void>> childFutures = new ArrayList<>();
            for (ReferenceDescription ref : refs) {
                if (ref.getNodeClass() == NodeClass.Object) {
                    ref.getNodeId().toNodeId(client.getNamespaceTable())
                            .ifPresent(childNodeId ->
                                childFutures.add(browseRecursiveInternal(childNodeId, maxDepth, currentDepth + 1, collector))
                            );
                }
            }
            return CompletableFuture.allOf(childFutures.toArray(new CompletableFuture[0]));
        });
    }

    public CompletableFuture<Void> subscribe(List<NodeId> nodeIds) {
        if (!connected.get() || client == null || nodeIds.isEmpty()) {
            return CompletableFuture.completedFuture(null);
        }

        int interval = iotProperties.getKepserver().getSubscriptionIntervalMs();
        return client.getSubscriptionManager()
                .createSubscription(interval)
                .thenCompose(sub -> {
                    this.subscription = sub;
                    List<MonitoredItemCreateRequest> requests = nodeIds.stream()
                            .map(nodeId -> new MonitoredItemCreateRequest(
                                    new ReadValueId(nodeId, AttributeId.Value.uid(), null, QualifiedName.NULL_VALUE),
                                    MonitoringMode.Reporting,
                                    new MonitoringParameters(
                                            sub.nextClientHandle(),
                                            (double) interval,
                                            null,
                                            uint(1),
                                            true
                                    )
                            ))
                            .toList();

                    UaSubscription.ItemCreationCallback onItemCreated = (item, idx) ->
                            item.setValueConsumer(this::onDataChange);

                    return sub.createMonitoredItems(TimestampsToReturn.Both, requests, onItemCreated);
                })
                .thenAccept(items -> log.info("已订阅点位完成"));
    }

    private void onDataChange(UaMonitoredItem item, DataValue value) {
        String nodeIdStr = item.getReadValueId().getNodeId().toParseableString();
        if (dataChangeListener != null) {
            try {
                dataChangeListener.accept(nodeIdStr, value);
            } catch (Exception e) {
                log.error("处理数据变化回调出错 [{}]: {}", nodeIdStr, e.getMessage());
            }
        }
    }

    public CompletableFuture<DataValue> readValue(NodeId nodeId) {
        if (!connected.get() || client == null) {
            return CompletableFuture.completedFuture(new DataValue(StatusCode.BAD));
        }
        return client.readValue(0, TimestampsToReturn.Both, nodeId);
    }

    public CompletableFuture<List<DataValue>> readValues(List<NodeId> nodeIds) {
        if (!connected.get() || client == null) {
            return CompletableFuture.completedFuture(Collections.emptyList());
        }
        return client.readValues(0, TimestampsToReturn.Both, nodeIds);
    }

    private void scheduleReconnect() {
        int delay = iotProperties.getKepserver().getReconnectDelayMs();
        log.info("将在 {}ms 后尝试重新连接 OPC-UA...", delay);
        reconnectExecutor.schedule(() -> {
            try {
                doConnect(iotProperties.getKepserver());
            } catch (Exception e) {
                log.error("OPC-UA 重连失败: {}", e.getMessage());
                scheduleReconnect();
            }
        }, delay, TimeUnit.MILLISECONDS);
    }

    @PreDestroy
    public void shutdown() {
        connected.set(false);
        reconnectExecutor.shutdownNow();
        if (client != null) {
            try {
                client.disconnect().get(3, TimeUnit.SECONDS);
                log.info("OPC-UA 连接已断开");
            } catch (Exception e) {
                log.warn("OPC-UA 断开连接时出错: {}", e.getMessage());
            }
        }
    }

    public OpcUaClient getClient() {
        return client;
    }
}
