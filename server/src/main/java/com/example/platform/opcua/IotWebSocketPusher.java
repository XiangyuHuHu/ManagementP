package com.example.platform.opcua;

import com.example.platform.entity.AlarmRecord;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
public class IotWebSocketPusher extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(IotWebSocketPusher.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final Set<WebSocketSession> sessions = new CopyOnWriteArraySet<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
        log.info("WebSocket 客户端连接: {} (当前 {} 个连接)", session.getId(), sessions.size());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
        log.info("WebSocket 客户端断开: {} (当前 {} 个连接)", session.getId(), sessions.size());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        // 客户端发来的消息（可用于订阅特定 tag）暂不处理
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.warn("WebSocket 传输错误 [{}]: {}", session.getId(), exception.getMessage());
        sessions.remove(session);
    }

    public void pushRealtimeValue(String tagCode, Double value, short quality, String unit, String deviceCode, OffsetDateTime time) {
        if (sessions.isEmpty()) return;

        try {
            String json = objectMapper.writeValueAsString(Map.of(
                    "type", "realtime",
                    "tagCode", tagCode,
                    "value", value,
                    "quality", quality,
                    "unit", unit != null ? unit : "",
                    "deviceCode", deviceCode != null ? deviceCode : "",
                    "time", time.toString()
            ));
            broadcast(json);
        } catch (Exception e) {
            log.error("推送实时数据失败: {}", e.getMessage());
        }
    }

    public void pushAlarm(AlarmRecord alarm) {
        if (sessions.isEmpty()) return;

        try {
            String json = objectMapper.writeValueAsString(Map.of(
                    "type", "alarm",
                    "alarmId", alarm.getAlarmId() != null ? alarm.getAlarmId() : "",
                    "deviceId", alarm.getDeviceId() != null ? alarm.getDeviceId() : "",
                    "pointId", alarm.getPointId() != null ? alarm.getPointId() : "",
                    "level", alarm.getAlarmLevel() != null ? alarm.getAlarmLevel() : "",
                    "message", alarm.getAlarmMessage() != null ? alarm.getAlarmMessage() : "",
                    "value", alarm.getCurrentValue() != null ? alarm.getCurrentValue() : 0,
                    "time", alarm.getStartTime() != null ? alarm.getStartTime() : ""
            ));
            broadcast(json);
        } catch (Exception e) {
            log.error("推送报警数据失败: {}", e.getMessage());
        }
    }

    private void broadcast(String json) {
        TextMessage message = new TextMessage(json);
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(message);
                } catch (Exception e) {
                    log.warn("发送 WebSocket 消息失败 [{}]", session.getId());
                    sessions.remove(session);
                }
            }
        }
    }

    public int getActiveSessionCount() {
        return sessions.size();
    }
}
