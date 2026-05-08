package com.server.services.space.dto;

import java.time.LocalDateTime;

import com.server.models.enums.RoleAction;
import com.server.models.enums.UserGender;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpaceMemberUserDto {
    private Long id;
    private String username;
    private String avatarUrl;
    private String fullName;
    private UserGender gender;
    private LocalDateTime joinedAt;
    private RoleAction role;
}

