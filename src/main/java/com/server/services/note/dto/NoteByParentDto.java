package com.server.services.note.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.server.services.user.dto.SimpleUserDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoteByParentDto {
    private UUID id;
    private String title;
    private String content;
    private SimpleUserDto creator;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
