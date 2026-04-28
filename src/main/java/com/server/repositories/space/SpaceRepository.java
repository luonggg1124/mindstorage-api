package com.server.repositories.space;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.server.models.entities.Space;

public interface SpaceRepository extends JpaRepository<Space, UUID> {
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
            order by updated_at desc nulls last, created_at desc
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


    long countByCreator_IdAndDeletedAtIsNull(Long creatorId);
    
    Optional<Space> findByIdAndDeletedAtIsNull(UUID id);

    boolean existsById(UUID id);
}
