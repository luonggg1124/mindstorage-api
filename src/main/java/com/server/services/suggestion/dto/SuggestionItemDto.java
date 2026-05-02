package com.server.services.suggestion.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.server.models.enums.SuggestionType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SuggestionItemDto {
    private SuggestionType type; // GROUP | SPACE
    private UUID id;
    private String name;
    private LocalDateTime lastActivityAt;
}

