package com.server.controllers.attachment;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.server.controllers.attachment.request.CreateAttachmentRequest;
import com.server.controllers.attachment.request.PresignedUploadRequest;
import com.server.controllers.attachment.response.CreateAttachmentResponse;
import com.server.models.entities.Attachment;
import com.server.services.attachment.AttachmentService;
import com.server.services.attachment.dto.MyAttachmentDto;
import com.server.services.attachment.dto.MyAttachmentsTotalSizeDto;
import com.server.services.attachment.dto.PresignedUploadDto;
import com.server.services.others.data.dto.PageResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/attachment")
@RequiredArgsConstructor
public class AttachmentController {
    private final AttachmentService attachmentService;

    @GetMapping("/my-attachments/total-size")
    public ResponseEntity<MyAttachmentsTotalSizeDto> myAttachmentsTotalSize() {
        return ResponseEntity.ok(attachmentService.myAttachmentsTotalSize());
    }

    @GetMapping("/my-attachments")
    public ResponseEntity<PageResponse<MyAttachmentDto>> myAttachments(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return ResponseEntity.ok(attachmentService.myAttachments(page, size));
    }

    @PostMapping
    public ResponseEntity<CreateAttachmentResponse> create(@Valid @RequestBody CreateAttachmentRequest request) {
        Attachment attachment = attachmentService.create(
                request.getFileKey(),
                request.getOriginalName(),
                request.getMimeType(),
                request.getFileSize());
        return ResponseEntity.status(HttpStatus.CREATED).body(new CreateAttachmentResponse(
                attachment.getId(),
                attachment.getFileKey(),
                attachment.getFileUrl(),
                attachment.getOriginalName(),
                attachment.getMimeType(),
                attachment.getFileSize(),
                attachment.getCreatedAt(),
                attachment.getUpdatedAt()));
    }

    @PostMapping("/presign")
    public ResponseEntity<PresignedUploadDto> presign(@Valid @RequestBody PresignedUploadRequest request) {
        return ResponseEntity.ok(attachmentService.presign(request.getFileName(), request.getContentType()));
    }
}
