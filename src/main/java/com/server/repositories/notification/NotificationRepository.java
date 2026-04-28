package com.server.repositories.notification;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.server.models.entities.Notification;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    @Query(value = """
                select *
                from notifications
                where user_id = :userId
                  and deleted_at is null
                  and (
                    coalesce(:q, '') = ''
                    or title ilike concat('%', :q, '%')
                    or content ilike concat('%', :q, '%')
                  )
                order by created_at desc
            """, nativeQuery = true)
    Page<Notification> getMyNotifications(@Param("userId") Long userId, @Param("q") String q, Pageable pageable);

    int countByUserIdAndIsReadFalseAndDeletedAtIsNull(Long userId);

    @Modifying
    @Query("""
            update com.server.models.entities.Notification n
            set n.isRead = true,
                n.readAt = :readAt
            where n.userId = :userId
              and n.deletedAt is null
              and n.isRead = false
            """)
    int markAllRead(@Param("userId") Long userId, @Param("readAt") LocalDateTime readAt);

    @Modifying
    @Query("""
            update com.server.models.entities.Notification n
            set n.isRead = true,
                n.readAt = :readAt
            where n.id = :id
              and n.userId = :userId
              and n.deletedAt is null
              and n.isRead = false
            """)
    int markRead(@Param("id") UUID id, @Param("userId") Long userId, @Param("readAt") LocalDateTime readAt);
}
