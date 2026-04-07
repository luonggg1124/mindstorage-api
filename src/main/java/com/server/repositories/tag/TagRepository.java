package com.server.repositories.tag;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.server.models.entities.Tag;
import com.server.repositories.tag.dto.TagByGroupDto;

public interface TagRepository extends JpaRepository<Tag, Long> {

    @Query("""
            select new com.server.repositories.tag.dto.TagByGroupDto(
                t.id,
                t.name,
                t.createdAt,
                t.updatedAt
            )
            from Tag t
            where t.group.id = :groupId
              and t.deletedAt is null
            order by t.createdAt desc
            """)
    List<TagByGroupDto> findTagsByGroup(@Param("groupId") Long groupId);
}

