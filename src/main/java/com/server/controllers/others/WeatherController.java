package com.server.controllers.others;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.server.controllers.others.request.GetWeatherRequest;
import com.server.controllers.others.response.GetWeatherResponse;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/weather")
@Slf4j
@Validated
public class WeatherController {
    @Value("${weather.api.key}")
    private String weatherApiKey;
    @Value("${weather.api.url}")
    private String weatherApiUrl;
    @GetMapping
    public ResponseEntity<GetWeatherResponse> getWeather(
            @Valid GetWeatherRequest request) {

        StringBuilder locationUrl = new StringBuilder();
        locationUrl.append("https://api.bigdatacloud.net/data/reverse-geocode-client");
        locationUrl.append("?latitude=").append(request.getLatitude());
        locationUrl.append("&longitude=").append(request.getLongitude());
        locationUrl.append("&localityLanguage=").append(request.getLang() != null ? request.getLang() : "vi");
        RestTemplate locationRestTemplate = new RestTemplate();
        String responseLocation = locationRestTemplate.getForObject(locationUrl.toString(), String.class);
        

        GetWeatherResponse.Location location = parseLocation(responseLocation);
    
        StringBuilder url = new StringBuilder();
        url.append(weatherApiUrl);
        url.append("?key=").append(weatherApiKey);
        url.append("&q=").append(request.getLatitude()).append(",").append(request.getLongitude());
        url.append("&lang=").append(request.getLang() != null ? request.getLang() : "vi");
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(url.toString(), String.class);
       
        GetWeatherResponse.Weather weather = parseWeather(response);
        return ResponseEntity.ok(new GetWeatherResponse(location, weather));
    }

    private GetWeatherResponse.Location parseLocation(String responseLocation) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(responseLocation);
            String country = root.path("countryName").asText(null);
            String city = root.path("city").asText(null);
            String locality = root.path("locality").asText(null);
            String languageCode = root.path("localityLanguageRequested").asText(null);
            String countryCode = root.path("countryCode").asText(null);
            String principalSubdivisionCode = root.path("principalSubdivisionCode").asText(null);

            return new GetWeatherResponse.Location(
                    country,
                    city,
                    locality,
                    languageCode,
                    countryCode,
                    principalSubdivisionCode);
        } catch (Exception e) {
            log.warn("Failed to parse location response", e);
            return new GetWeatherResponse.Location(null, null, null, null, null, null);
        }
    }

    private GetWeatherResponse.Weather parseWeather(String responseWeather) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(responseWeather);

            JsonNode cur = root.path("current");
            String lastUpdated = cur.path("last_updated").asText(null);
            Double tempC = cur.path("temp_c").isMissingNode() ? null : cur.path("temp_c").asDouble();
            Integer isDay = cur.path("is_day").isMissingNode() ? null : cur.path("is_day").asInt();
            String conditionText = cur.path("condition").path("text").asText(null);
            String conditionIcon = cur.path("condition").path("icon").asText(null);
            Double windKph = cur.path("wind_kph").isMissingNode() ? null : cur.path("wind_kph").asDouble();
            Integer humidity = cur.path("humidity").isMissingNode() ? null : cur.path("humidity").asInt();
            Integer cloud = cur.path("cloud").isMissingNode() ? null : cur.path("cloud").asInt();
            Double feelslikeC = cur.path("feelslike_c").isMissingNode() ? null : cur.path("feelslike_c").asDouble();

            return new GetWeatherResponse.Weather(
                    lastUpdated,
                    tempC,
                    isDay,
                    conditionText,
                    conditionIcon,
                    windKph,
                    humidity,
                    cloud,
                    feelslikeC);
        } catch (Exception e) {
            log.warn("Failed to parse weather response", e);
            return new GetWeatherResponse.Weather(null, null, null, null, null, null, null, null, null);
        }
    }
}
