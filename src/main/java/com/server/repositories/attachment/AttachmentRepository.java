package com.server.repositories.attachment;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.server.models.entities.Attachment;

public interface AttachmentRepository extends JpaRepository<Attachment, UUID> {

    Page<Attachment> findByCreatorIdOrderByCreatedAtDesc(Long creatorId, Pageable pageable);

    @Query("""
            select coalesce(sum(a.fileSize), 0)
            from com.server.models.entities.Attachment a
            where a.creatorId = :creatorId
            """)
    long sumFileSizeByCreatorId(@Param("creatorId") Long creatorId);
}
