package com.server.controllers.space.request;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SpaceRequest {

    @NotBlank
    private String name;
}