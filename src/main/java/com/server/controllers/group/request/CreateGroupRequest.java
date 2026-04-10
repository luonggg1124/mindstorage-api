package com.server.controllers.group.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateGroupRequest {
    @NotBlank(message = "Không được để trống tên")
    private String name;
    private String description;
    @NotNull(message = "Không hợp lệ.")
    @Positive(message = "Không hợp lệ.")
    private Long spaceId;
}
