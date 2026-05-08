package com.server.controllers.space.response;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateSpaceResponse {
    private UUID id;
    private String name;
    private String description;
    private String createdAt;
    private String updatedAt;
}
