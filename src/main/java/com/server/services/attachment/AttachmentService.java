package com.server.services.attachment;

import java.time.Duration;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.server.constants.R2Clouflare;
import com.server.models.entities.Attachment;
import com.server.models.entities.User;
import com.server.repositories.attachment.AttachmentRepository;
import com.server.services.attachment.dto.MyAttachmentDto;
import com.server.services.attachment.dto.MyAttachmentsTotalSizeDto;
import com.server.services.attachment.dto.PresignedUploadDto;
import com.server.services.auth.AuthService;
import com.server.services.others.data.dto.PageResponse;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
@RequiredArgsConstructor
public class AttachmentService {
    private final S3Presigner s3Presigner;
    private final AttachmentRepository attachmentRepository;
    private final AuthService authService;
    @Value("${cloudflare.r2.public-url}")
    private String publicUrl;

    @Value("${cloudflare.r2.bucket}")
    private String bucketName;

    public String buildPublicUrl(String fileKey) {
        if (fileKey == null) {
            return null;
        }
        return publicUrl + "/" + fileKey;
    }

    public PresignedUploadDto presign(

            String fileName,
            String contentType) {
        User user = authService.authUser();
        String fileKey = R2Clouflare.getFileKeyString(
                R2Clouflare.getFolder(R2Clouflare.ATTACHMENTS_FOLDER, user.getId()), fileName);
        PutObjectRequest putObjectRequest = PutObjectRequest.builder().bucket(bucketName).key(fileKey)
                .contentType(contentType).build();

        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(
                PutObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofMinutes(10)).putObjectRequest(putObjectRequest).build());
        return new PresignedUploadDto(fileKey, presignedRequest.url().toString());
    }

    public Attachment create(String fileKey, String originalName, String mimeType, Long fileSize) {
        User user = authService.authUser();
        Attachment attachment = new Attachment();
        attachment.setCreatorId(user.getId());
        attachment.setFileKey(fileKey);
        attachment.setFileUrl(buildPublicUrl(fileKey));
        attachment.setOriginalName(originalName);
        attachment.setMimeType(mimeType);
        attachment.setFileSize(fileSize);
        return attachmentRepository.save(attachment);
    }

    public PageResponse<MyAttachmentDto> myAttachments(Integer page, Integer size) {
        User user = authService.authUser();
        int pageIndex = page == null ? 0 : Math.max(page - 1, 0);
        int pageSize = size == null ? 10 : size;
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        Page<Attachment> attachments = attachmentRepository.findByCreatorIdOrderByCreatedAtDesc(user.getId(), pageable);
        List<MyAttachmentDto> data = attachments.getContent().stream()
                .map(a -> new MyAttachmentDto(
                        a.getId(),
                        a.getFileKey(),
                        a.getFileUrl(),
                        a.getOriginalName(),
                        a.getMimeType(),
                        a.getFileSize(),
                        a.getCreatedAt(),
                        a.getUpdatedAt()))
                .toList();
        return new PageResponse<>(data, attachments.getTotalElements(), attachments.getNumber() + 1, attachments.getSize());
    }

    public MyAttachmentsTotalSizeDto myAttachmentsTotalSize() {
        User user = authService.authUser();
        long total = attachmentRepository.sumFileSizeByCreatorId(user.getId());
        return new MyAttachmentsTotalSizeDto(total);
    }

}
