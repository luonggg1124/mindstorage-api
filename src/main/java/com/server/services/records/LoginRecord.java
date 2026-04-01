package com.server.services.records;

import com.server.models.entities.User;

public record LoginRecord(String accessToken, String refreshToken, User user) {
    
}
