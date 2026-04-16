package com.example.platform.opcua;

import com.example.platform.config.IotProperties;
import com.example.platform.entity.IotTagEntity;
import com.example.platform.repository.IotTagRepository;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OpcUaBootstrap {

    private static final Logger log = LoggerFactory.getLogger(OpcUaBootstrap.class);

    private final IotProperties iotProperties;
    private final OpcUaClientManager clientManager;
    private final DataChangeHandler dataChangeHandler;
    private final IotTagRepository tagRepo;

    public OpcUaBootstrap(
            IotProperties iotProperties,
            OpcUaClientManager clientManager,
            DataChangeHandler dataChangeHandler,
            IotTagRepository tagRepo
    ) {
        this.iotProperties = iotProperties;
        this.clientManager = clientManager;
        this.dataChangeHandler = dataChangeHandler;
        this.tagRepo = tagRepo;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        IotProperties.Kepserver config = iotProperties.getKepserver();
        if (!config.isEnabled()) {
            log.info("KEPServer 未启用（kepserver.enabled=false），跳过 OPC-UA 连接");
            return;
        }

        clientManager.setDataChangeListener(dataChangeHandler::onDataChange);
        clientManager.setAfterConnected(this::subscribeEnabledTags);

        clientManager.connect().exceptionally(ex -> {
            log.error("OPC-UA 启动流程出错: {}", ex.getMessage());
            return null;
        });
    }

    /**
     * 首次连接与断线重连成功后都会执行，保证订阅恢复。
     */
    private void subscribeEnabledTags() {
        if (!clientManager.isConnected()) {
            log.warn("OPC-UA 连接未成功，跳过订阅");
            return;
        }

        dataChangeHandler.refreshTagCache();

        List<IotTagEntity> enabledTags = tagRepo.findByEnabled(true);
        if (enabledTags.isEmpty()) {
            log.info("数据库中没有已启用的点位，跳过订阅。请先通过管理页面配置点位。");
            return;
        }

        int ns = iotProperties.getKepserver().getOpcuaNamespaceIndex();
        List<NodeId> nodeIds = enabledTags.stream()
                .filter(tag -> tag.getSourcePath() != null && !tag.getSourcePath().isBlank())
                .map(tag -> new NodeId(ns, tag.getSourcePath()))
                .toList();

        if (nodeIds.isEmpty()) {
            log.warn("所有启用的点位都没有 sourcePath，无法订阅");
            return;
        }

        clientManager.subscribe(nodeIds)
                .thenRun(() -> log.info("OPC-UA 订阅启动完成，共 {} 个点位", nodeIds.size()))
                .exceptionally(ex -> {
                    log.error("OPC-UA 订阅失败: {}", ex.getMessage());
                    return null;
                });
    }
}
