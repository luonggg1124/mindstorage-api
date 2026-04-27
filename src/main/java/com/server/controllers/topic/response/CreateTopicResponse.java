package com.server.controllers.topic.response;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateTopicResponse {
    private UUID id;
    private String name;
    private UUID groupId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
