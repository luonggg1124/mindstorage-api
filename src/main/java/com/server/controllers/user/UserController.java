package com.server.controllers.user;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.server.controllers.user.request.ValidUsernamePasswordRequest;
import com.server.controllers.user.response.ValidUsernamePasswordResponse;
import com.server.models.enums.InvitationType;
import com.server.services.user.UserService;
import com.server.services.user.dto.InviteUserDto;
import com.server.services.user.dto.MyProfileDto;
import com.server.services.user.dto.SimpleUserDto;
import com.server.services.others.data.dto.PageResponse;

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

    @GetMapping("/my-profile")
    public ResponseEntity<MyProfileDto> myProfile() {
        return ResponseEntity.ok(userService.myProfile());
    }

    @GetMapping("/search")
    public ResponseEntity<PageResponse<SimpleUserDto>> search(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
        ) {
        return ResponseEntity.ok(userService.search(q, page, size));
    }

    @GetMapping("/search/invite")
    public ResponseEntity<PageResponse<InviteUserDto>> searchInvite(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam InvitationType type,
            @RequestParam UUID entityId) {
        return ResponseEntity.ok(userService.searchInvite(q, page, size, type, entityId));
    }
}
