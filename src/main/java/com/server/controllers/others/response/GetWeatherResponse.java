package com.server.controllers.others.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetWeatherResponse {
    private Location location;
    private Weather weather;

    @Getter
    @AllArgsConstructor
    public static class Location {
        private String country;
        private String city;
        private String locality;
        private String languageCode;
        private String countryCode;
        private String principalSubdivisionCode;
    }

    @Getter
    @AllArgsConstructor
    public static class Weather {
        private String lastUpdated;
        private Double tempC;
        private Integer isDay;
        private String conditionText;
        private String conditionIcon;
        private Double windKph;
        private Integer humidity;
        private Integer cloud;
        private Double feelslikeC;
    }
}
