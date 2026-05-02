package com.server.services.suggestion;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.server.models.entities.Group;
import com.server.models.entities.Space;
import com.server.models.entities.User;
import com.server.models.enums.SuggestionType;
import com.server.repositories.group.GroupRepository;
import com.server.repositories.space.SpaceRepository;
import com.server.services.auth.AuthService;
import com.server.services.suggestion.dto.SuggestionItemDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SuggestionServiceImplement implements SuggestionService {
    private final AuthService authService;
    private final SpaceRepository spaceRepository;
    private final GroupRepository groupRepository;

    @Override
    public List<SuggestionItemDto> suggest(Integer limit) {
        int size = (limit == null) ? 5 : Math.max(1, Math.min(limit, 20));
        User user = authService.authUser();

        int fetch = Math.max(size, 5);
        List<SuggestionItemDto> results = new java.util.ArrayList<>(fetch * 2);

        for (Space s : spaceRepository.suggestSpaces(user.getId(), fetch)) {
            results.add(new SuggestionItemDto(SuggestionType.SPACE, s.getId(), s.getName(), s.getLastActivityAt()));
        }
        for (Group g : groupRepository.suggestGroups(user.getId(), fetch)) {
            results.add(new SuggestionItemDto(SuggestionType.GROUP, g.getId(), g.getName(), g.getLastActivityAt()));
        }

        results.sort((a, b) -> {
            LocalDateTime la = a.getLastActivityAt();
            LocalDateTime lb = b.getLastActivityAt();
            if (la == null && lb == null) return 0;
            if (la == null) return 1;
            if (lb == null) return -1;
            return lb.compareTo(la);
        });

        return results.stream().limit(size).toList();
    }
}

