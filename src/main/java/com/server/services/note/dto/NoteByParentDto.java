package com.server.services.note.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoteByParentDto {
    private Long id;
    private String title;
    private String content;
    private String topicName;
    private String parentName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
