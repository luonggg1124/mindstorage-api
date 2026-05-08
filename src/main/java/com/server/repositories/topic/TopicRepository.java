package com.server.repositories.topic;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.server.models.entities.Topic;

public interface TopicRepository extends JpaRepository<Topic, UUID> {

    @Query(value = """
            select *
            from topics
            where group_id = :groupId
              and deleted_at is null
            order by created_at desc
            """, nativeQuery = true)
    List<Topic> findTopicsByGroup(@Param("groupId") UUID groupId);

    boolean existsByIdAndDeletedAtIsNull(UUID id);

    Optional<Topic> findByIdAndDeletedAtIsNull(UUID id);

    @Query(value = """
            select group_id, count(id)
            from topics
            where group_id in (:groupIds)
              and deleted_at is null
            group by group_id
            """, nativeQuery = true)
    List<Object[]> countByGroupIds(@Param("groupIds") List<UUID> groupIds);
}
