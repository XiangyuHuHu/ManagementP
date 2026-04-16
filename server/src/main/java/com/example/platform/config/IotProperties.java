package com.example.platform.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "platform.iot")
public class IotProperties {

    private String provider = "mock";
    private boolean fallbackToMock = true;
    private final Kepserver kepserver = new Kepserver();

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public boolean isFallbackToMock() {
        return fallbackToMock;
    }

    public void setFallbackToMock(boolean fallbackToMock) {
        this.fallbackToMock = fallbackToMock;
    }

    public Kepserver getKepserver() {
        return kepserver;
    }

    public static class Kepserver {

        private boolean enabled = false;
        private String endpoint = "";
        private String username = "";
        private String password = "";
        private String sourceType = "KEPSERVER";
        private String channelPrefix = "Channel1";
        private Integer timeoutMs = 5000;
        private Integer subscriptionIntervalMs = 1000;
        private Integer reconnectDelayMs = 5000;
        private Integer maxReconnectAttempts = 0;
        private Integer batchWriteSize = 500;
        private Integer batchWriteIntervalMs = 5000;
        /** OPC-UA NodeId 命名空间索引，与 KEPServer / 停送电项目 OPCUA_NS 一致，默认 2 */
        private int opcuaNamespaceIndex = 2;

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }

        public String getEndpoint() { return endpoint; }
        public void setEndpoint(String endpoint) { this.endpoint = endpoint; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }

        public String getSourceType() { return sourceType; }
        public void setSourceType(String sourceType) { this.sourceType = sourceType; }

        public String getChannelPrefix() { return channelPrefix; }
        public void setChannelPrefix(String channelPrefix) { this.channelPrefix = channelPrefix; }

        public Integer getTimeoutMs() { return timeoutMs; }
        public void setTimeoutMs(Integer timeoutMs) { this.timeoutMs = timeoutMs; }

        public Integer getSubscriptionIntervalMs() { return subscriptionIntervalMs; }
        public void setSubscriptionIntervalMs(Integer subscriptionIntervalMs) { this.subscriptionIntervalMs = subscriptionIntervalMs; }

        public Integer getReconnectDelayMs() { return reconnectDelayMs; }
        public void setReconnectDelayMs(Integer reconnectDelayMs) { this.reconnectDelayMs = reconnectDelayMs; }

        public Integer getMaxReconnectAttempts() { return maxReconnectAttempts; }
        public void setMaxReconnectAttempts(Integer maxReconnectAttempts) { this.maxReconnectAttempts = maxReconnectAttempts; }

        public Integer getBatchWriteSize() { return batchWriteSize; }
        public void setBatchWriteSize(Integer batchWriteSize) { this.batchWriteSize = batchWriteSize; }

        public Integer getBatchWriteIntervalMs() { return batchWriteIntervalMs; }
        public void setBatchWriteIntervalMs(Integer batchWriteIntervalMs) { this.batchWriteIntervalMs = batchWriteIntervalMs; }

        public int getOpcuaNamespaceIndex() { return opcuaNamespaceIndex; }
        public void setOpcuaNamespaceIndex(int opcuaNamespaceIndex) { this.opcuaNamespaceIndex = opcuaNamespaceIndex; }
    }
}
