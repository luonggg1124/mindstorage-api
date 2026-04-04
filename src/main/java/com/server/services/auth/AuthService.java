package com.server.services.auth;

import java.util.List;

import com.server.models.entities.User;
import com.server.services.auth.records.LoginRecord;
import com.server.services.auth.records.VerifyEmailRecord;

import io.jsonwebtoken.Claims;

public interface  AuthService {
    VerifyEmailRecord sendVerificationEmail(String email);
    LoginRecord login(String usernameOrEmail, String password);
    void logout(String refreshToken);
    LoginRecord register(
        String email,
        String username,
        String password,
        String fullName,
        String session,
        List<String> hobbies,
        String code
    );
     Claims parseToken(String token);
     User userFromToken(String token);
     User authUser();

}
