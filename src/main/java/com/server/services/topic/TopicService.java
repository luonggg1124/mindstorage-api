package com.server.services.topic;

import java.util.List;

import com.server.controllers.topic.request.CreateTopicRequest;
import com.server.models.entities.Topic;
import com.server.repositories.topic.dto.TopicByGroupDto;

public interface TopicService {
    List<TopicByGroupDto> getTopicsByGroup(Long groupId);

    Topic create(CreateTopicRequest request);

    Topic update(Long id, CreateTopicRequest request);
}
