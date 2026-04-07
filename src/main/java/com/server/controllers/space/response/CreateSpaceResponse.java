package com.server.controllers.space.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateSpaceResponse {
    private Long id;
    private String name;
    private String description;
    private String createdAt;
    private String updatedAt;
}
