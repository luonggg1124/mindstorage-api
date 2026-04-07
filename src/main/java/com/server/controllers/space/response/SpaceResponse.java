package com.server.controllers.space.response;

import lombok.AllArgsConstructor;

import lombok.Getter;

@Getter
@AllArgsConstructor
public class SpaceResponse {
    private Long id;
    private String name;
    private String createdAt;
    private String updatedAt;

}
