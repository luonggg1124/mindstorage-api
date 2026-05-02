package com.server.controllers.attachment.response;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateAttachmentResponse {
    private UUID id;
    private String fileKey;
    private String fileUrl;
    private String originalName;
    private String mimeType;
    private Long fileSize;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
