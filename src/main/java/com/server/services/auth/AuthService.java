package com.server.services.auth;

import com.server.models.entities.User;
import com.server.services.auth.records.LoginRecord;

import io.jsonwebtoken.Claims;

public interface  AuthService {
    LoginRecord login(String email, String password);
        void logout(String refreshToken);
    LoginRecord register(String email, String password);
     Claims parseToken(String token);
     User userFromToken(String token);
     User authUser();
}
