package com.server.controllers.attachment.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PresignedUploadRequest {
    @NotBlank(message = "fileName không được để trống")
    private String fileName;
    @NotBlank(message = "contentType không được để trống")
    private String contentType;
   
}
