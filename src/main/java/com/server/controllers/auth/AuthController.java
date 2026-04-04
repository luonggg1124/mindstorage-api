package com.server.controllers.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.server.cache.AuthCache;
import com.server.controllers.auth.request.LoginRequest;
import com.server.controllers.auth.request.RegisterRequest;
import com.server.controllers.auth.request.VerifyEmailRequest;
import com.server.controllers.auth.response.LoginResponse;
import com.server.controllers.auth.response.RegisterResponse;
import com.server.controllers.auth.response.VerifyEmailResponse;
import com.server.models.enums.UserGender;
import com.server.services.auth.AuthService;
import com.server.services.auth.records.LoginRecord;
import com.server.services.auth.records.VerifyEmailRecord;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request){
        LoginRecord auth = authService.login(request.getUsername(), request.getPassword());
        LoginResponse response = new LoginResponse(
            AuthCache.REFRESH_EXPIRATION.toString(),
            auth.accessToken(),
            auth.refreshToken(),
            auth.user()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/verify-email")
    public ResponseEntity<VerifyEmailResponse> verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
        VerifyEmailRecord record = authService.sendVerificationEmail(request.getEmail());
        return ResponseEntity.ok(new VerifyEmailResponse(record.session()));
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        UserGender gender = request.getGender() == null
                ? null
                : UserGender.valueOf(request.getGender());
        LoginRecord auth = authService.register(
            request.getEmail(),
            request.getUsername(),
            request.getPassword(),
            request.getFullName(),
            request.getSession(),
            gender,
            request.getHobbies(),
            request.getIntendedUse(),
            request.getCode()
        );
        RegisterResponse response = new RegisterResponse(
            AuthCache.REFRESH_EXPIRATION,
            auth.accessToken(),
            auth.refreshToken(),
            auth.user()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
