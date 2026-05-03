package com.server.services.notification;

import java.time.LocalDateTime;
import java.util.UUID;

import com.server.models.entities.Notification;
import com.server.models.enums.InvitationStatus;
import com.server.models.enums.InvitationType;
import com.server.models.enums.NotificationType;
import com.server.models.enums.RoleAction;
import com.server.services.notification.dto.MyNotificationDto;
import com.server.services.others.data.dto.PageResponse;

public interface NotificationService {
    void sendNotification(Long userId, Object data);

    void sendCount(Long userId, long count);

    Notification create(Long userId, Object data, NotificationType type);

    PageResponse<MyNotificationDto> getMyNotifications(String q, Integer page, Integer size);

    long countUnread();

    boolean markAllRead();

    boolean markRead(UUID id);

    void updateInvitationStatusData(UUID invitationId, InvitationStatus status, LocalDateTime respondedAt);

    void upsertRoleChangeNotification(
            Long recipientUserId,
            InvitationType entityType,
            UUID entityId,
            String entityName,
            RoleAction oldRoleBeforeChange,
            RoleAction newRole,
            Long targetUserId,
            String targetUserName,
            Long actorUserId,
            String actorName);
}
