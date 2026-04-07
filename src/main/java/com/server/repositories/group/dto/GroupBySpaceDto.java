package com.server.repositories.group.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupBySpaceDto {
    private Long id;
    private String name;
    private String description;
    private long noteCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
