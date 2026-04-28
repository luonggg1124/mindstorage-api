package com.server.services.notification;

import java.util.UUID;

import com.server.models.entities.Notification;
import com.server.models.enums.NotificationType;
import com.server.services.notification.dto.MyNotificationDto;
import com.server.services.others.data.dto.PageResponse;

public interface NotificationService {
    void sendNotification(Long userId, Object data);

    void sendCount(Long userId, long count);

    Notification create(Long userId, String title, String content, Object data, NotificationType type,
            UUID entityId);

    PageResponse<MyNotificationDto> getMyNotifications(String q, Integer page, Integer size);

    long countUnread();

    boolean markAllRead();

    boolean markRead(UUID id);
}
