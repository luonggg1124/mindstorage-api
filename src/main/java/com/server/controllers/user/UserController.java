package com.server.controllers.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.server.controllers.user.request.ValidUsernamePasswordRequest;
import com.server.controllers.user.response.ValidUsernamePasswordResponse;
import com.server.services.user.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    @PostMapping("/valid-username-password")
    public ResponseEntity<ValidUsernamePasswordResponse> validUsernamePassword(@Valid @RequestBody ValidUsernamePasswordRequest request) {
        boolean isValid = userService.validateUsernamePassword(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(new ValidUsernamePasswordResponse(isValid));
    }
}
