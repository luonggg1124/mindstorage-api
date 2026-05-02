package com.server.services.attachment;


import com.server.models.entities.Attachment;
import com.server.services.attachment.dto.PresignedUploadDto;

public interface AttachmentService {
    String buildPublicUrl(String fileKey);

    PresignedUploadDto presign(String fileName, String contentType);

    Attachment create(String fileKey, String originalName, String mimeType, Long fileSize);
}
