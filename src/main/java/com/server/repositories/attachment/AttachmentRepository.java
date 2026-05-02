package com.server.repositories.attachment;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.server.models.entities.Attachment;

public interface AttachmentRepository extends JpaRepository<Attachment, UUID> {
    
}
