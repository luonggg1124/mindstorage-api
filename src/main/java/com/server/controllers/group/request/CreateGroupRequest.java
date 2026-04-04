package com.server.controllers.group.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateGroupRequest {
    @NotBlank(message = "Không được để trống tên")
    private String name;
    @NotBlank(message = "Không được để trống mô tả")
    private String description;
    @NotBlank(message = "Không được để trống Space Id")
    private Long spaceId;
}
