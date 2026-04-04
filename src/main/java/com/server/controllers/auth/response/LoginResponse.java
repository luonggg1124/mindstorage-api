package com.server.controllers.auth.response;

import com.server.models.entities.User;

import lombok.AllArgsConstructor;
import lombok.Getter;




@Getter
@AllArgsConstructor
public class LoginResponse {
    private String refreshExpire;
    private String accessToken;
    private String refreshToken;
    private User user;
}
