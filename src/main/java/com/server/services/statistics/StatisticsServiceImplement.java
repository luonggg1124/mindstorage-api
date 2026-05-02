package com.server.services.statistics;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.server.exceptions.BadRequestException;
import com.server.models.entities.User;
import com.server.repositories.note.NoteRepository;
import com.server.services.auth.AuthService;
import com.server.services.statistics.dto.NotesDayDataDto;
import com.server.services.statistics.dto.StatisticsResponseDto;
import com.server.services.statistics.dto.TopicItemDto;
import com.server.services.statistics.dto.TopicsDataDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImplement implements StatisticsService {
    private final AuthService authService;
    private final NoteRepository noteRepository;

    @Override
    public StatisticsResponseDto myActivities(Integer range) {
        int days = (range == null) ? 7 : range;
        if (days != 7 && days != 30) {
            throw new BadRequestException("range chỉ nhận 7 hoặc 30", "range");
        }

        User user = authService.authUser();
        LocalDate today = LocalDate.now();
        LocalDate fromDate = today.minusDays(days - 1L);
        LocalDateTime from = fromDate.atStartOfDay();

        Map<LocalDate, Long> countByDay = new HashMap<>();
        for (Object[] row : noteRepository.countCreatedByDay(user.getId(), from)) {
            LocalDate d = (LocalDate) row[0];
            long c = ((Number) row[1]).longValue();
            countByDay.put(d, c);
        }

        List<NotesDayDataDto> notesData = new ArrayList<>(days);
        for (int i = 0; i < days; i++) {
            LocalDate d = fromDate.plusDays(i);
            notesData.add(new NotesDayDataDto(d, toLabel(d.getDayOfWeek()), countByDay.getOrDefault(d, 0L)));
        }

        List<Object[]> topicRows = noteRepository.countNotesByTopic(user.getId(), from);
        long total = topicRows.stream().mapToLong(r -> ((Number) r[2]).longValue()).sum();
        List<TopicItemDto> items = new ArrayList<>(topicRows.size());
        for (Object[] row : topicRows) {
            UUID id = (UUID) row[0];
            String name = (String) row[1];
            long count = ((Number) row[2]).longValue();
            double percentage = total == 0 ? 0 : (count * 100.0) / total;
            items.add(new TopicItemDto(id, name, count, percentage));
        }

        return new StatisticsResponseDto(notesData, new TopicsDataDto(total, items));
    }

    private String toLabel(DayOfWeek dow) {
        return switch (dow) {
            case MONDAY -> "T2";
            case TUESDAY -> "T3";
            case WEDNESDAY -> "T4";
            case THURSDAY -> "T5";
            case FRIDAY -> "T6";
            case SATURDAY -> "T7";
            case SUNDAY -> "CN";
        };
    }
}

