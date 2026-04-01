package com.server.services;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.server.cache.AuthCache;
import com.server.exceptions.BadRequestException;
import com.server.exceptions.NotFoundException;
import com.server.models.entities.User;
import com.server.repositories.user.UserRepository;
import com.server.services.records.GenerateTokenRecord;
import com.server.services.records.LoginRecord;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AuthServiceImplement implements AuthService {

    @Value("${jwt.secret}")
    private String secretKey;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final RedisTemplate<String, Object> redisTemplate;

    private GenerateTokenRecord generateToken(User user) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + AuthCache.ACCESS_EXPIRATION);
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());
        String token = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(AuthCache.refreshKey(token), user.getId().toString(),
                AuthCache.REFRESH_EXPIRATION, TimeUnit.MILLISECONDS);
        redisTemplate.opsForValue().set(AuthCache.userKey(user.getId().toString()), user, AuthCache.ACCESS_EXPIRATION, TimeUnit.MILLISECONDS);
        String accessToken = Jwts.builder().claim("user_id", user.getId().toString()).issuedAt(now).expiration(expiry).signWith(key).compact();
        return new GenerateTokenRecord(accessToken, token);

    }
    @Override
    public LoginRecord register(String email, String password) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        GenerateTokenRecord tokenRecord = generateToken(user);
        return new LoginRecord(tokenRecord.accessToken(), tokenRecord.refreshToken(), user);
    }

    @Override
    public LoginRecord login(String email, String password){
        Optional<User> userOpt = userRepository.findByEmail(email);
        if(userOpt.isEmpty()){
            throw new NotFoundException("User not found");
        }
        User user = userOpt.get();
        if(!passwordEncoder.matches(password, user.getPassword())){
            throw new BadRequestException("Invalid password");
        }
        GenerateTokenRecord tokenRecord = generateToken(user);
        return new LoginRecord(tokenRecord.accessToken(), tokenRecord.refreshToken(), user);
    }
    @Override
    public void logout(String refreshToken) {
        if(refreshToken == null || refreshToken.isEmpty()) {
            return;
        }
        String key = AuthCache.refreshKey(refreshToken);
        redisTemplate.delete(key);
    }

}
