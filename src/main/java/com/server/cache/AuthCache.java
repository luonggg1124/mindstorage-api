package com.server.cache;

import org.springframework.beans.factory.annotation.Value;

import jakarta.annotation.PostConstruct;

public class AuthCache {
    public static Long ACCESS_EXPIRATION;
    public static Long REFRESH_EXPIRATION;
    public static final String TOKEN_ACCESS_KEY = "t_a";
    public static final String TOKEN_REFRESH_KEY= "t_r";
    public static final String USER_KEY = "user";
    @Value("${jwt.access.expiration}")
    private Long accessExpiration;

    @Value("${jwt.refresh.expiration}")
    private Long refreshExpiration;
    @PostConstruct
    private void init(){
        ACCESS_EXPIRATION = accessExpiration;
        REFRESH_EXPIRATION = refreshExpiration;
    }

    public static String refreshKey(String refreshToken){
        return TOKEN_REFRESH_KEY + ":" + refreshToken;
    }

    public static String userKey(String userId){
        return USER_KEY + ":" + userId;
    }
}
