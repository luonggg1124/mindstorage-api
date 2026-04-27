package com.server.controllers.topic;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.server.controllers.topic.request.CreateTopicRequest;
import com.server.controllers.topic.response.CreateTopicResponse;
import com.server.models.entities.Topic;
import com.server.services.topic.dto.TopicByGroupDto;
import com.server.services.topic.TopicService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/topic")
public class TopicController {
    private final TopicService topicService;

    @GetMapping("/by-group/{id}")
    public ResponseEntity<List<TopicByGroupDto>> getTopicsByGroup(
            @PathVariable UUID id) {
        List<TopicByGroupDto> topics = topicService.getTopicsByGroup(id);
        return ResponseEntity.status(HttpStatus.OK).body(topics);
    }

    @PostMapping
    public ResponseEntity<CreateTopicResponse> create(
            @Valid @RequestBody CreateTopicRequest request) {
        Topic topic = topicService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new CreateTopicResponse(
                topic.getId(),
                topic.getName(),
                topic.getGroup().getId(),
                topic.getCreatedAt(),
                topic.getUpdatedAt()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CreateTopicResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody CreateTopicRequest request) {
        Topic topic = topicService.update(id, request);
        return ResponseEntity.status(HttpStatus.OK).body(new CreateTopicResponse(
                topic.getId(),
                topic.getName(),
                topic.getGroup().getId(),
                topic.getCreatedAt(),
                topic.getUpdatedAt()));
    }
}
