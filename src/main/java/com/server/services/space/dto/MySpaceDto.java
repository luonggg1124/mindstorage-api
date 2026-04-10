package com.server.services.space.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MySpaceDto {
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private long groupCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

