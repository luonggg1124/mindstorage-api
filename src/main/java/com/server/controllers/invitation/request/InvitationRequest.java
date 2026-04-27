package com.server.controllers.invitation.request;

import java.util.UUID;

import com.server.models.enums.InvitationType;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvitationRequest {
    @NotNull(message = "Không được để trống")
    private Long inviteeId;
    @NotNull(message = "Không được để trống")
    private UUID entityId;
    @NotNull(message = "Không được để trống")
    private InvitationType entityType;
}
