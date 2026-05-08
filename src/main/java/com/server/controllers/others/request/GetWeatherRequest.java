package com.server.controllers.others.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetWeatherRequest {
    @NotBlank(message = "Query latitude is required")
    private String latitude;
    @NotBlank(message = "Query longitude is required")
    private String longitude;

    private String lang;
}
