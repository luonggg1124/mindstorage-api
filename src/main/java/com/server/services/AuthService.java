package com.server.services;

import com.server.services.records.LoginRecord;

public interface  AuthService {
    LoginRecord login(String email, String password);
        void logout(String refreshToken);
    LoginRecord register(String email, String password);
}
