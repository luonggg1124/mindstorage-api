package com.server.services.attachment;


import com.server.models.entities.Attachment;
import com.server.services.attachment.dto.MyAttachmentDto;
import com.server.services.attachment.dto.MyAttachmentsTotalSizeDto;
import com.server.services.attachment.dto.PresignedUploadDto;
import com.server.services.others.data.dto.PageResponse;

public interface AttachmentService {
    String buildPublicUrl(String fileKey);

    PresignedUploadDto presign(String fileName, String contentType);

    Attachment create(String fileKey, String originalName, String mimeType, Long fileSize);

    PageResponse<MyAttachmentDto> myAttachments(Integer page, Integer size);

    MyAttachmentsTotalSizeDto myAttachmentsTotalSize();
}
