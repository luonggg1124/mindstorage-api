package com.server.services.user.dto;

import com.server.models.enums.UserGender;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class MyProfileDto {
    private Long id;
    private String username;
    private String fullName;
    private String avatarUrl;
    private String email;
    private String hobbies;
    private UserGender gender;
    private long followersCount;
    private long followingCount;
    private long spacesCount;
    private long spaceMembersCount;
}
