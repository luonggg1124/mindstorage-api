package com.server.repositories.topic;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.server.models.entities.Topic;
import com.server.repositories.topic.dto.TopicByGroupDto;

public interface TopicRepository extends JpaRepository<Topic, Long> {

    @Query("""
            select new com.server.repositories.topic.dto.TopicByGroupDto(
                t.id,
                t.name,
                t.createdAt,
                t.updatedAt
            )
            from com.server.models.entities.Topic t
            where t.group.id = :groupId
              and t.deletedAt is null
            order by t.createdAt desc
            """)
    List<TopicByGroupDto> findTopicsByGroup(@Param("groupId") Long groupId);

    boolean existsByIdAndDeletedAtIsNull(Long id);

    Optional<Topic> findByIdAndDeletedAtIsNull(Long id);

    @Query("""
            select t.group.id, count(t.id)
            from com.server.models.entities.Topic t
            where t.group.id in :groupIds
              and t.deletedAt is null
            group by t.group.id
            """)
    List<Object[]> countByGroupIds(@Param("groupIds") List<Long> groupIds);
}
