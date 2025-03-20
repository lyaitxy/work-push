package com.example.workpush.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

public class TimestampToLocalDateTimeDeserializer  {
    public static LocalDate deserialize(String timestamp) {
        // 将毫秒数转换为LocalDateTime
        if (timestamp == null || timestamp.isEmpty()) {
            return null;
        }
        return LocalDate.ofInstant(Instant.ofEpochMilli(Long.parseLong(timestamp)), ZoneId.systemDefault());
    }
}
