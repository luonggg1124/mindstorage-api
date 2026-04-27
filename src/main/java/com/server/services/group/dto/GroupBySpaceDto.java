package com.server.services.group.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupBySpaceDto {
    private UUID id;
    private String name;
    private String description;
    private long topicCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

