package com.server.services.statistics.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotesDayDataDto {
    private LocalDate date;
    private String label;
    private long noteCreated;
}

