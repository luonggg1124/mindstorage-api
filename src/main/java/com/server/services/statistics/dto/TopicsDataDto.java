package com.server.services.statistics.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopicsDataDto {
    private long total;
    private List<TopicItemDto> items;
}

