package com.example.platform.config;

import com.example.platform.opcua.IotWebSocketPusher;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final IotWebSocketPusher iotWebSocketPusher;

    public WebSocketConfig(IotWebSocketPusher iotWebSocketPusher) {
        this.iotWebSocketPusher = iotWebSocketPusher;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(iotWebSocketPusher, "/api/ws/iot")
                .setAllowedOrigins("*");
    }
}
