package com.server.controllers.suggestion;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.server.services.suggestion.SuggestionService;
import com.server.services.suggestion.dto.SuggestionItemDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/suggestions")
public class SuggestionController {
    private final SuggestionService suggestionService;

    @GetMapping
    public ResponseEntity<List<SuggestionItemDto>> suggest(
            @RequestParam(required = false, defaultValue = "5") Integer limit) {
        return ResponseEntity.ok(suggestionService.suggest(limit));
    }
}

