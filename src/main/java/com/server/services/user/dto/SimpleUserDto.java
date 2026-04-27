package com.server.services.user.dto;

import com.server.models.enums.UserGender;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleUserDto {
    private Long id;
    private String username;
    private String avatarUrl;
    private String fullName;
    private UserGender gender;
}

