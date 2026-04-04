package com.server.controllers.auth.response;

import com.server.models.entities.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String refreshExpire;
    private String accessToken;
    private String refreshToken;
    private User user;
}
