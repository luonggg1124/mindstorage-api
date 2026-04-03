package com.server.controllers.group.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateGroupRequest {
    private String name;
    private String description;
    private Long spaceId;
}
