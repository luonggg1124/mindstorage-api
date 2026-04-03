package com.server.services.auth;

import java.util.Date;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.server.cache.AuthCache;
import com.server.exceptions.BadRequestException;
import com.server.exceptions.NotFoundException;
import com.server.models.entities.User;
import com.server.repositories.user.UserRepository;
import com.server.services.auth.records.GenerateTokenRecord;
import com.server.services.auth.records.LoginRecord;
import com.server.services.auth.records.VerifyEmailRecord;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImplement implements AuthService {

    @Value("${jwt.secret}")
    private String secretKey;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final RedisTemplate<String, Object> redisTemplate;
    private final Random random = new Random();

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
    

    public VerifyEmailRecord sendVerificationEmail(String email){
        // In a real implementation, you would send an email here
        String session = UUID.randomUUID().toString();
        String code = String.format("%06d", random.nextInt(1_000_000));
        redisTemplate.opsForValue().set(AuthCache.verifyEmailKey(session), code, 5, TimeUnit.MINUTES);
        return new VerifyEmailRecord(session);
    }

    
    @Override
    public LoginRecord register(
            String email,
            String username,
            String password,
            String fullName,
            String session,
            String code
    ) {
        Object cached = redisTemplate.opsForValue().get(AuthCache.verifyEmailKey(session));
        if (!(cached instanceof String cachedCode)) {
            throw new BadRequestException("Verification session expired or invalid");
        }
        if (!cachedCode.equals(code)) {
            throw new BadRequestException("Invalid verification code");
        }

        if (userRepository.findByEmailOrUsername(email, username).isPresent()) {
            throw new BadRequestException("Email or username already exists");
        }

        User user = new User();
        user.setEmail(email);
        user.setUsername(username);
        user.setFullName(fullName == null ? "" : fullName);
        user.setVerified(true);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        redisTemplate.delete(AuthCache.verifyEmailKey(session));
        GenerateTokenRecord tokenRecord = generateToken(user);
        return new LoginRecord(tokenRecord.accessToken(), tokenRecord.refreshToken(), user);
    }

    @Override
    public Claims parseToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    @Override
    public User authUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }

    @Override
    public User userFromToken(String token) {

        Claims claims = parseToken(token);
        String userId = claims.get("user_id", String.class);
        Object cached = redisTemplate.opsForValue().get(AuthCache.userKey(userId));
        if (cached instanceof User cachedUser) {
            return cachedUser;
        }

        // Fallback to database and re-cache
        User user = userRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new NotFoundException("User not found"));

        redisTemplate.opsForValue().set(
                AuthCache.userKey(userId),
                user,
                AuthCache.ACCESS_EXPIRATION,
                TimeUnit.MILLISECONDS);

        return user;
    }

    @Override
    public LoginRecord login(String usernameOrEmail, String password) {
        Optional<User> userOpt = userRepository.findByEmailOrUsername(usernameOrEmail, usernameOrEmail);
        if (userOpt.isEmpty()) {
            throw new NotFoundException("User not found");
        }
        User user = userOpt.get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadRequestException("Invalid password");
        }
        GenerateTokenRecord tokenRecord = generateToken(user);
        return new LoginRecord(tokenRecord.accessToken(), tokenRecord.refreshToken(), user);
    }

    @Override
    public void logout(String refreshToken) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            return;
        }
        String key = AuthCache.refreshKey(refreshToken);
        redisTemplate.delete(key);
    }

}
