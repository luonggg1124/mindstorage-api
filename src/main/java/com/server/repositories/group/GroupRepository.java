package com.server.repositories.group;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.server.models.entities.Group;

public interface GroupRepository extends JpaRepository<Group, Long> {

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
            @Param("spaceId") Long spaceId,
            @Param("q") String q,
            Pageable pageable);

    @Query("""
            select g.space.id, count(g.id)
            from com.server.models.entities.Group g
            where g.space.id in :spaceIds
              and g.deletedAt is null
            group by g.space.id
            """)
    List<Object[]> countBySpaceIds(@Param("spaceIds") List<Long> spaceIds);

    Optional<Group> findByIdAndDeletedAtIsNull(Long id);
    boolean existsById(Long id);
}
