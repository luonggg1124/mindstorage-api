package com.server.controllers.notification;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.server.services.notification.NotificationService;
import com.server.services.notification.dto.MyNotificationDto;
import com.server.services.others.data.dto.PageResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping("/my-notifications")
    public ResponseEntity<PageResponse<MyNotificationDto>> myNotifications(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return ResponseEntity.ok(notificationService.getMyNotifications(q, page, size));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Long> unreadCount() {
        return ResponseEntity.ok(notificationService.countUnread());
    }

    @PatchMapping("/read-all")
    public ResponseEntity<Map<String, Boolean>> readAll() {
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("success", notificationService.markAllRead()));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Map<String, Boolean>> readOne(@PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("success", notificationService.markRead(id)));
    }
}
