package com.server.repositories.note;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.server.models.entities.Note;

public interface NoteRepository extends JpaRepository<Note, UUID> {

  List<Note> findAllByDeletedAtIsNull();

  java.util.Optional<Note> findByIdAndDeletedAtIsNull(UUID id);

  @Query(value = """
      SELECT *
      FROM notes
      WHERE topic_id = :topicId
      AND deleted_at IS NULL AND parent_id IS NULL
      ORDER BY
        CASE WHEN coalesce(:embedding, '') = '' THEN created_at END DESC,
        CASE WHEN coalesce(:embedding, '') <> '' THEN embedding <-> CAST(:embedding AS vector) END ASC
      """, countQuery = """
      SELECT COUNT(*)
      FROM notes
      WHERE topic_id = :topicId
      AND deleted_at IS NULL AND parent_id IS NULL
      """, nativeQuery = true)
  Page<Note> notesByTopic(@Param("topicId") UUID topicId, @Param("embedding") String embedding, Pageable pageable);

  Page<Note> findAllByParent_IdAndDeletedAtIsNull(UUID parentId, Pageable pageable);

  @Query(value = """
      select cast(n.created_at as date) as d, count(*) as c
      from notes n
      where n.creator_id = :userId
        and n.deleted_at is null
        and n.created_at >= :from
      group by cast(n.created_at as date)
      order by d asc
      """, nativeQuery = true)
  List<Object[]> countCreatedByDay(@Param("userId") Long userId, @Param("from") LocalDateTime from);

  @Query(value = """
      select t.id, t.name, count(n.id) as c
      from notes n
      join topics t on t.id = n.topic_id
      where n.creator_id = :userId
        and n.deleted_at is null
        and n.created_at >= :from
      group by t.id, t.name
      order by c desc
      """, nativeQuery = true)
  List<Object[]> countNotesByTopic(@Param("userId") Long userId, @Param("from") LocalDateTime from);
}
