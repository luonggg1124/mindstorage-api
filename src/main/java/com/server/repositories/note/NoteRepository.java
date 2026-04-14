package com.server.repositories.note;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.server.models.entities.Note;

public interface NoteRepository extends JpaRepository<Note, Long> {

  List<Note> findAllByDeletedAtIsNull();

  java.util.Optional<Note> findByIdAndDeletedAtIsNull(Long id);

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
  Page<Note> notesByTopic(@Param("topicId") Long topicId, @Param("embedding") String embedding, Pageable pageable);

  Page<Note> findAllByParent_IdAndDeletedAtIsNull(Long parentId, Pageable pageable);
}
