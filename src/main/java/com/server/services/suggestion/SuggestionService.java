package com.server.services.suggestion;

import java.util.List;

import com.server.services.suggestion.dto.SuggestionItemDto;

public interface SuggestionService {
    List<SuggestionItemDto> suggest(Integer limit);
}

