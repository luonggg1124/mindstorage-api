package com.server.services.space.dto;

import com.server.models.enums.RoleAction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MySpaceRoleDto {
    private RoleAction role;
}
