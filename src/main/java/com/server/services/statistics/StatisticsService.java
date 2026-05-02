package com.server.services.statistics;

import com.server.services.statistics.dto.StatisticsResponseDto;

public interface StatisticsService {
    StatisticsResponseDto myActivities(Integer range);
}

