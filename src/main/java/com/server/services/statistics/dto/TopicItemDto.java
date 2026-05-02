package com.server.services.statistics.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopicItemDto {
    private UUID id;
    private String name;
    private long count;
    private double percentage;
}

