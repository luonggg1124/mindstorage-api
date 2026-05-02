package com.server.services.attachment;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.server.constants.R2Clouflare;
import com.server.models.entities.Attachment;
import com.server.models.entities.User;
import com.server.repositories.attachment.AttachmentRepository;
import com.server.services.attachment.dto.PresignedUploadDto;
import com.server.services.auth.AuthService;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
@RequiredArgsConstructor
public class AttachmentServiceImplement implements AttachmentService {
    private final S3Presigner s3Presigner;
    private final AttachmentRepository attachmentRepository;
    private final AuthService authService;
    @Value("${cloudflare.r2.public-url}")
    private String publicUrl;

    @Value("${cloudflare.r2.bucket}")
    private String bucketName;

    @Override
    public String buildPublicUrl(String fileKey) {
        if (fileKey == null) {
            return null;
        }
        return publicUrl + "/" + fileKey;
    }

    @Override
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

    @Override
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

}
