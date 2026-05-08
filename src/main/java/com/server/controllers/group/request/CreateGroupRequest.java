package com.server.controllers.group.request;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateGroupRequest {
    @NotBlank(message = "Không được để trống tên")
    private String name;
    private String description;
    @NotNull(message = "Không hợp lệ.")
    private UUID spaceId;
}
