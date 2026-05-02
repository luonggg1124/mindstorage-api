package com.server.controllers.statistics;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.server.services.statistics.StatisticsService;
import com.server.services.statistics.dto.StatisticsResponseDto;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/statistics")
public class StatisticsController {
    private final StatisticsService statisticsService;

    @GetMapping("/my-activities")
    public ResponseEntity<StatisticsResponseDto> myActivities(
            @RequestParam(required = false, defaultValue = "7") @Positive(message = "Sang định dạng(số).")  Integer range) {
        return ResponseEntity.ok(statisticsService.myActivities(range));
    }
}

