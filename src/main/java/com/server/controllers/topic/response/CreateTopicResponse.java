package com.server.controllers.topic.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateTopicResponse {
    private Long id;
    private String name;
    private Long groupId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
