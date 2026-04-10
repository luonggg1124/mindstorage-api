package com.server.services.note.dto;

import java.time.LocalDateTime;

import com.server.services.user.dto.SimpleUserDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoteByTopicDto {
    private Long id;
    private String title;
    private String content;
    private SimpleUserDto creator;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

