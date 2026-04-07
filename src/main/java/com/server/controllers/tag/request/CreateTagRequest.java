package com.server.controllers.tag.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTagRequest {
    @NotBlank(message = "Không được để trống tên")
    private String name;

    @NotNull(message = "groupId không được để trống")
    @Positive(message = "groupId phải là số dương")
    private Long groupId;
}

