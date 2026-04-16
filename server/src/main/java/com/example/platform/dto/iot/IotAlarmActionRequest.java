package com.example.platform.dto.iot;

public record IotAlarmActionRequest(String alarmId, String operator, String remark) {
}
