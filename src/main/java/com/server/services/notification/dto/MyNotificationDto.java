package com.server.services.notification.dto;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import com.server.models.enums.NotificationType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MyNotificationDto {
    private UUID id;
    private Long userId;
    private Long senderId;
    private NotificationType type;
    private Map<String, Object> data;
    private boolean isRead;
    private LocalDateTime readAt;
    private LocalDateTime createdAt;

}
