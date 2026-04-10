package com.server.repositories.space;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.server.models.entities.Space;

public interface SpaceRepository extends JpaRepository<Space, Long> {
    @Query(value = """
            select *
            from spaces
            where creator_id = :creatorId
              and deleted_at is null
              and (
                coalesce(:q, '') = ''
                or name ilike concat('%', :q, '%')
                or description ilike concat('%', :q, '%')
              )
            """,
            countQuery = """
            select count(*)
            from spaces
            where creator_id = :creatorId
              and deleted_at is null
              and (
                coalesce(:q, '') = ''
                or name ilike concat('%', :q, '%')
                or description ilike concat('%', :q, '%')
              )
            """,
            nativeQuery = true)
    Page<Space> mySpaces(@Param("creatorId") Long creatorId, @Param("q") String q, Pageable pageable);

    Optional<Space> findByIdAndDeletedAtIsNull(Long id);

    boolean existsById(Long id);
}
