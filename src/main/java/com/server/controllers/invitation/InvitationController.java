package com.server.controllers.invitation;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.server.controllers.invitation.request.InvitationRequest;
import com.server.services.invitation.InvitationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/invitations")
@RequiredArgsConstructor
public class InvitationController {
    private final InvitationService invitationService;

    @PostMapping
    public ResponseEntity<?> invite(
            @Valid @RequestBody InvitationRequest request) {
        invitationService.invite(request.getInviteeId(), request.getEntityId(), request.getEntityType());
        return ResponseEntity.ok().body(Map.of("success", true));
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<?> accept(@PathVariable UUID id) {
        invitationService.accept(id);
        return ResponseEntity.ok().body(Map.of("success", true));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<?> reject(
        @PathVariable UUID id
    ){
        invitationService.reject(id);
        return ResponseEntity.ok().body(Map.of("success", true));
    }
    
}
