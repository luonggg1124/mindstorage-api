package com.server.controllers.auth.response;

import com.server.models.entities.User;

import lombok.AllArgsConstructor;
import lombok.Getter;




@Getter
@AllArgsConstructor
public class LoginResponse {
    private Long refreshTokenExpiresIn;
    private String accessToken;
    private String refreshToken;
    private User user;
}
