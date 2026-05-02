package com.server.services.attachment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PresignedUploadDto {
    private String fileKey;
    private String uploadUrl;
}
