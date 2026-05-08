package com.server.controllers.attachment.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAttachmentRequest {

    @NotBlank(message = "fileKey không được để trống")
    private String fileKey;

    @NotBlank(message = "originalName không được để trống")
    private String originalName;

    @NotBlank(message = "mimeType không được để trống")
    private String mimeType;

    @NotNull(message = "fileSize không được để trống")
    @Positive(message = "fileSize phải lớn hơn 0")
    private Long fileSize;
}
