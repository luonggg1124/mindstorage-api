package com.server.services.notification;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import com.server.models.entities.Notification;
import com.server.models.entities.User;
import com.server.models.enums.InvitationStatus;
import com.server.models.enums.InvitationType;
import com.server.models.enums.NotificationType;
import com.server.models.enums.RoleAction;
import com.server.repositories.notification.NotificationRepository;
import com.server.repositories.notification.json.RoleChangeNotificationData;
import com.server.services.auth.AuthService;
import com.server.services.notification.dto.MyNotificationDto;
import com.server.services.others.data.DataService;
import com.server.services.others.data.dto.PageResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final AuthService authService;
    private final DataService dataService;

    public void sendNotification(Long userId, Object data) {
        log.info("Sending notification to user: {}", userId);
        simpMessagingTemplate.convertAndSendToUser(userId.toString(), "/queue/notifications", data);
    }

    public void sendCount(Long userId, long count) {
        simpMessagingTemplate.convertAndSendToUser(userId.toString(), "/queue/notifications/count", count);
    }

    public Notification create(Long userId, Object data, NotificationType type) {
        User user = authService.authUser();
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setSenderId(user.getId());
        notification.setData(dataService.objectToMap(data));
        notification.setType(type);
        notification.setRead(false);
        notificationRepository.save(notification);
        return notification;
    }

    public MyNotificationDto toMyNotificationDto(Notification notification) {
        return new MyNotificationDto(
                notification.getId(),
                notification.getUserId(),
                notification.getSenderId(),
                notification.getType(),
                notification.getData(),
                notification.isRead(),
                notification.getReadAt(),
                notification.getCreatedAt());
    }

    public PageResponse<MyNotificationDto> getMyNotifications(String keyword, Integer page, Integer size) {
        User user = authService.authUser();
        int pageIndex = page == null ? 0 : Math.max(page - 1, 0);
        int pageSize = size == null ? 10 : size;
        Pageable pageable = PageRequest.of(pageIndex, pageSize);

        String q = (keyword == null || keyword.isBlank()) ? null : keyword.trim();
        Page<Notification> notifications = notificationRepository.getMyNotifications(user.getId(), q, pageable);
        Page<MyNotificationDto> myNotificationDtos = notifications.map(this::toMyNotificationDto);
        return new PageResponse<MyNotificationDto>(myNotificationDtos.getContent(),
                myNotificationDtos.getTotalElements(), myNotificationDtos.getNumber() + 1,
                myNotificationDtos.getSize());
    }

    public long countUnread() {
        User user = authService.authUser();
        return notificationRepository.countByUserIdAndIsReadFalseAndDeletedAtIsNull(user.getId());
    }

    @Transactional
    public boolean markAllRead() {
        User user = authService.authUser();
        notificationRepository.markAllRead(user.getId(), LocalDateTime.now());
        return true;
    }

    @Transactional
    public boolean markRead(UUID id) {
        User user = authService.authUser();
        int updated = notificationRepository.markRead(id, user.getId(), LocalDateTime.now());
        return updated > 0;
    }

    @Transactional
    public void updateInvitationStatusData(UUID invitationId, InvitationStatus status, LocalDateTime respondedAt) {
        notificationRepository.updateInvitationStatusData(
                invitationId,
                status.name(),
                respondedAt);
    }

    @Transactional
    public void upsertRoleChangeNotification(
            Long recipientUserId,
            InvitationType entityType,
            UUID entityId,
            String entityName,
            RoleAction oldRoleBeforeChange,
            RoleAction newRole,
            Long targetUserId,
            String targetUserName,
            Long actorUserId,
            String actorName) {
        LocalDateTime since = LocalDateTime.now().minusMinutes(Math.max(1, 5));
        List<Notification> latest = notificationRepository.findLatestForDedup(
                recipientUserId,
                NotificationType.ROLE_CHANGED.name(),
                entityId,
                since,
                PageRequest.of(0, 1));
        Optional<Notification> recent = latest.stream().findFirst();

        String en = entityName != null ? entityName : "";

        if (recent.isEmpty()) {
            Object data = RoleChangeNotificationData.toMap(
                    entityType,
                    entityId,
                    en,
                    targetUserId,
                    targetUserName,
                    oldRoleBeforeChange,
                    newRole,
                    actorUserId,
                    actorName);
            Notification n = create(recipientUserId, data, NotificationType.ROLE_CHANGED);
            sendNotification(recipientUserId, toMyNotificationDto(n));
            sendCount(recipientUserId, countUnreadForUser(recipientUserId));
            return;
        }

        Notification n = recent.get();
        Map<String, Object> parsed = n.getData();
        if (parsed == null) {
            parsed = new HashMap<>();
        }
        Map<String, Object> map = RoleChangeNotificationData.mergeDedup(
                parsed,
                entityType,
                entityId,
                en,
                targetUserId,
                targetUserName,
                oldRoleBeforeChange,
                newRole,
                actorUserId,
                actorName);
        n.setData(map);
        n.setSenderId(actorUserId);
        n.setRead(false);
        n.setReadAt(null);
        notificationRepository.save(n);
        sendNotification(recipientUserId, toMyNotificationDto(n));
        sendCount(recipientUserId, countUnreadForUser(recipientUserId));
    }

    private long countUnreadForUser(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalseAndDeletedAtIsNull(userId);
    }
}
