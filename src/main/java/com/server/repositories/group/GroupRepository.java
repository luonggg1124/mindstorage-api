package com.server.repositories.group;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.server.models.entities.Group;

public interface GroupRepository extends JpaRepository<Group, UUID> {

    @Query(value = """
            select *
            from groups
            where space_id = :spaceId
              and deleted_at is null
              and (
                :q is null
                or name ilike concat('%', :q, '%')
                or description ilike concat('%', :q, '%')
              )
            """,
            countQuery = """
            select count(*)
            from groups
            where space_id = :spaceId
              and deleted_at is null
              and (
                :q is null
                or name ilike concat('%', :q, '%')
                or description ilike concat('%', :q, '%')
              )
            """,
            nativeQuery = true)
    Page<Group> groupsBySpace(
            @Param("spaceId") UUID spaceId,
            @Param("q") String q,
            Pageable pageable);

    @Query("""
            select g.space.id, count(g.id)
            from com.server.models.entities.Group g
            where g.space.id in :spaceIds
              and g.deletedAt is null
            group by g.space.id
            """)
    List<Object[]> countBySpaceIds(@Param("spaceIds") List<UUID> spaceIds);

    Optional<Group> findByIdAndDeletedAtIsNull(UUID id);
    boolean existsById(UUID id);

    @Modifying
    @Query("""
            update com.server.models.entities.Group g
            set g.lastActivityAt = :at
            where g.id = :id
            """)
    int touchLastActivityAt(@Param("id") UUID id, @Param("at") LocalDateTime at);

    @Query(value = """
            select distinct g.*
            from groups g
            left join spaces s on s.id = g.space_id
            left join space_members sm on sm.space_id = s.id
            where g.deleted_at is null
              and s.deleted_at is null
              and (
                s.creator_id = :userId
                or sm.user_id = :userId
              )
            order by g.last_activity_at desc nulls last, g.updated_at desc nulls last, g.created_at desc
            limit :limit
            """, nativeQuery = true)
    List<Group> suggestGroups(@Param("userId") Long userId, @Param("limit") int limit);
}
