package com.server.services.notification;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.models.entities.Notification;
import com.server.models.entities.User;
import com.server.models.enums.NotificationType;
import com.server.repositories.notification.NotificationRepository;
import com.server.services.auth.AuthService;
import com.server.services.notification.dto.MyNotificationDto;
import com.server.services.others.data.dto.PageResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImplement implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final AuthService authService;
    private final ObjectMapper objectMapper;

    @Override
    public void sendNotification(Long userId, Object data) {
        log.info("Sending notification to user: {}", userId);
        simpMessagingTemplate.convertAndSendToUser(userId.toString(), "/queue/notifications", data);
    }

    @Override
    public void sendCount(Long userId, long count) {
        simpMessagingTemplate.convertAndSendToUser(userId.toString(), "/queue/notifications/count", count);
    }
    
    @Override
    public Notification create(Long userId, String title, String content, Object data, NotificationType type,
            UUID entityId) {
        User user = authService.authUser();
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setSenderId(user.getId());
        notification.setData(toJsonSafely(data));
        notification.setType(type);
        notification.setEntityId(entityId);
        notification.setRead(false);
        notificationRepository.save(notification);
        return notification;
    }

    private String toJsonSafely(Object data) {
        if (data == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Không thể serialize notification data", e);
        }
    }
    public MyNotificationDto toMyNotificationDto(Notification notification){
        return new MyNotificationDto(
           notification.getId(),
           notification.getUserId(),
           notification.getSenderId(),
           notification.getTitle(),
           notification.getContent(),
           notification.getType(),
           notification.getData(),
           notification.isRead(),
           notification.getReadAt(),
           notification.getCreatedAt()
        );
    }

    @Override
    public PageResponse<MyNotificationDto> getMyNotifications(String keyword, Integer page, Integer size){
        User user = authService.authUser();
        int pageIndex = page == null ? 0 : Math.max(page - 1, 0);
        int pageSize = size == null ? 10 : size;
        Pageable pageable = PageRequest.of(pageIndex, pageSize);

        String q = (keyword == null || keyword.isBlank()) ? null : keyword.trim();
        Page<Notification> notifications = notificationRepository.getMyNotifications(user.getId(), q, pageable);
        Page<MyNotificationDto> myNotificationDtos = notifications.map(this::toMyNotificationDto);
        return new PageResponse<MyNotificationDto>(myNotificationDtos.getContent(), myNotificationDtos.getTotalElements(), myNotificationDtos.getNumber() + 1, myNotificationDtos.getSize());
    }

    @Override
    public long countUnread() {
        User user = authService.authUser();
        return notificationRepository.countByUserIdAndIsReadFalseAndDeletedAtIsNull(user.getId());
    }

    @Override
    @Transactional
    public boolean markAllRead() {
        User user = authService.authUser();
        notificationRepository.markAllRead(user.getId(), LocalDateTime.now());
        return true;
    }

    @Override
    @Transactional
    public boolean markRead(UUID id) {
        User user = authService.authUser();
        int updated = notificationRepository.markRead(id, user.getId(), LocalDateTime.now());
        return updated > 0;
    }
}
