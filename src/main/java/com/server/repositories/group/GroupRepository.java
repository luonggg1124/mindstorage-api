package com.server.repositories.group;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.server.models.entities.Group;
import com.server.repositories.group.dto.GroupBySpaceDto;

import java.util.List;
import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Long> {

    @Query("""
            select new com.server.repositories.group.dto.GroupBySpaceDto(
                g.id,
                g.name,
                g.description,
                (select count(n.id) from Note n
                 where n.group.id = g.id and n.deletedAt is null),
                g.createdAt,
                g.updatedAt
            )
            from Group g
            where g.space.id = :spaceId
              and g.deletedAt is null
            """)
    List<GroupBySpaceDto> getGroupBySpace(@Param("spaceId") Long spaceId);

    Optional<Group> findByIdAndDeletedAtIsNull(Long id);
    boolean existsById(Long id);
    List<Group> getAllBySpace_Id(Long spaceId, Pageable pageable);
}
