package com.server.controllers.space.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SpaceResponse {

    private Long id;
    private String name;

}
