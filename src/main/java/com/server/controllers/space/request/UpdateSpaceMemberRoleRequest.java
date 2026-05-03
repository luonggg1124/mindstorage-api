package com.server.controllers.space.request;

import com.server.models.enums.RoleAction;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateSpaceMemberRoleRequest {

    @NotNull(message = "role không được để trống")
    private RoleAction role;
}
