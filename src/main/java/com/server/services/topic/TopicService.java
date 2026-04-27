package com.server.services.topic;

import java.util.List;
import java.util.UUID;

import com.server.controllers.topic.request.CreateTopicRequest;
import com.server.models.entities.Topic;
import com.server.services.topic.dto.TopicByGroupDto;

public interface TopicService {
    List<TopicByGroupDto> getTopicsByGroup(UUID groupId);

    Topic create(CreateTopicRequest request);

    Topic update(UUID id, CreateTopicRequest request);
}
