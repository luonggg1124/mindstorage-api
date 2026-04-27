package com.server.services.invitation;

import java.util.UUID;

import com.server.models.enums.InvitationType;

public interface InvitationService {
    void invite(Long inviteeId, UUID entityId, InvitationType entityType);
    void accept(UUID id);
    void reject(UUID id);
}
