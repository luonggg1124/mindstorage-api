package com.server.repositories.notification;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
