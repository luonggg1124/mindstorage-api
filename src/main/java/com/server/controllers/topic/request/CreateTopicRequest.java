package com.server.controllers.topic.request;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTopicRequest {
    @NotBlank(message = "Không được để trống tên")
    private String name;

    @NotNull(message = "groupId không được để trống")
    private UUID groupId;
}
