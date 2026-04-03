package com.server.controllers.group.response;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class CreateGroupResponse {
    private Long id;
    private String name;
    private String description;
}
