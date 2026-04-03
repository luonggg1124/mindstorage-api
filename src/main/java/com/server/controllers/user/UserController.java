package com.server.controllers.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.server.controllers.user.request.ExistsUsernameRequest;
import com.server.controllers.user.response.ExistsUsernameReponse;
import com.server.services.user.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    @PostMapping("/exists-username")
    public ResponseEntity<ExistsUsernameReponse> existsUsername(@Valid @RequestBody ExistsUsernameRequest request) {
        return ResponseEntity.ok(new ExistsUsernameReponse(userService.existsByUsername(request.getUsername())));
    }
}
