package com.server.repositories.space;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.server.models.entities.Space;
import com.server.repositories.space.dto.MySpaceDto;

public interface SpaceRepository extends JpaRepository<Space, Long> {
    @Query("""
            select new com.server.repositories.space.dto.MySpaceDto(
                s.id,
                s.name,
                s.description,
                s.imageUrl,
                (select count(gr.id) from Group gr
                 where gr.space.id = s.id and gr.deletedAt is null),
                s.createdAt,
                s.updatedAt
            )
            from Space s
            where s.creator.id = :creatorId
              and s.deletedAt is null
            """)
    List<MySpaceDto> mySpaces(@Param("creatorId") Long creatorId);

    Optional<Space> findByIdAndDeletedAtIsNull(Long id);

    boolean existsById(Long id);
}
