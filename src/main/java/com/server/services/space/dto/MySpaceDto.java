package com.server.services.space.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.server.models.enums.RoleAction;
import com.server.services.user.dto.SimpleUserDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MySpaceDto {
    private UUID id;
    private String name;
    private String description;
    private String imageUrl;
    private long groupCount;
    private SimpleUserDto owner;
    private RoleAction role;
    private LocalDateTime lastActivityAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

