package com.server.services.attachment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MyAttachmentsTotalSizeDto {
    private long totalSizeBytes;
}
