package com.server.repositories.invitation;

import com.server.models.enums.InvitationStatus;
import com.server.models.enums.InvitationType;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.server.models.entities.Invitation;

public interface InvitationRepository extends JpaRepository<Invitation, UUID> {
    boolean existsByInviteeIdAndEntityIdAndTypeAndStatus(Long inviteeId, UUID entityId, InvitationType type,
            InvitationStatus status);
}
