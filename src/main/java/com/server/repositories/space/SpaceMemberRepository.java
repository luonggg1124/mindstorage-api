package com.server.repositories.space;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.server.models.entities.SpaceMember;

public interface SpaceMemberRepository extends JpaRepository<SpaceMember, UUID> {
    long countByUser_IdAndSpace_DeletedAtIsNull(Long userId);
    boolean existsByUserIdAndSpaceId(Long userId, UUID spaceId);

    @Query("""
            select sm.user.id
            from com.server.models.entities.SpaceMember sm
            where sm.space.id = :spaceId
            """)
    List<Long> findMemberUserIdsBySpaceId(@Param("spaceId") UUID spaceId);

    @Query(value = """
            select sm.*
            from space_members sm
            join users u on u.id = sm.user_id
            where sm.space_id = :spaceId
              and (
                :q is null
                or lower(cast(u.username as text)) like lower(concat('%', :q, '%'))
                or lower(cast(u.full_name as text)) like lower(concat('%', :q, '%'))
                or lower(cast(u.email as text)) like lower(concat('%', :q, '%'))
              )
            order by sm.created_at desc
            """,
            countQuery = """
            select count(*)
            from space_members sm
            join users u on u.id = sm.user_id
            where sm.space_id = :spaceId
              and (
                :q is null
                or lower(cast(u.username as text)) like lower(concat('%', :q, '%'))
                or lower(cast(u.full_name as text)) like lower(concat('%', :q, '%'))
                or lower(cast(u.email as text)) like lower(concat('%', :q, '%'))
              )
            """,
            nativeQuery = true)
    Page<SpaceMember> membersBySpaceId(@Param("spaceId") UUID spaceId, @Param("q") String q, Pageable pageable);
}
