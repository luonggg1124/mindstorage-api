package com.server.controllers.others.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetWeatherRequest {
    @NotBlank(message = "Latitude is required")
    private String latitude;
    @NotBlank(message = "Longitude is required")
    private String longitude;

    private String lang;
}
