package com.server.repositories.space;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.server.models.entities.Space;

public interface SpaceRepository extends JpaRepository<Space, UUID> {
    @Query(value = """
            select distinct s.*
            from spaces s
            left join space_members sm
              on sm.space_id = s.id
            where (s.creator_id = :userId or sm.user_id = :userId)
              and s.deleted_at is null
              and (
                coalesce(:q, '') = ''
                or s.name ilike concat('%', :q, '%')
                or s.description ilike concat('%', :q, '%')
              )
            order by s.last_activity_at desc nulls last,
                     s.updated_at desc nulls last,
                     s.created_at desc
            """,
            countQuery = """
            select count(distinct s.id)
            from spaces s
            left join space_members sm
              on sm.space_id = s.id
            where (s.creator_id = :userId or sm.user_id = :userId)
              and s.deleted_at is null
              and (
                coalesce(:q, '') = ''
                or s.name ilike concat('%', :q, '%')
                or s.description ilike concat('%', :q, '%')
              )
            """,
            nativeQuery = true)
    Page<Space> mySpaces(@Param("userId") Long userId, @Param("q") String q, Pageable pageable);

    long countByCreator_IdAndDeletedAtIsNull(Long creatorId);
    
    Optional<Space> findByIdAndDeletedAtIsNull(UUID id);

    boolean existsById(UUID id);

    @Modifying
    @Query("""
            update com.server.models.entities.Space s
            set s.lastActivityAt = :at
            where s.id = :id
            """)
    int touchLastActivityAt(@Param("id") UUID id, @Param("at") LocalDateTime at);

    @Query(value = """
            select distinct s.*
            from spaces s
            left join space_members sm on sm.space_id = s.id
            where (s.creator_id = :userId or sm.user_id = :userId)
              and s.deleted_at is null
            order by s.last_activity_at desc nulls last, s.updated_at desc nulls last, s.created_at desc
            limit :limit
            """, nativeQuery = true)
    List<Space> suggestSpaces(@Param("userId") Long userId, @Param("limit") int limit);
}
