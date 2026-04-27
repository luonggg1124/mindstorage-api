package com.server.services.user.dto;

import com.server.models.enums.UserGender;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InviteUserDto {
    private Long id;
    private String username;
    private String avatarUrl;
    private String fullName;
    private UserGender gender;
    private boolean isMember;
}

