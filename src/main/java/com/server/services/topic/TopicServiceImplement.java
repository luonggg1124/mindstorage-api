package com.server.services.topic;

import java.util.List;

import org.springframework.stereotype.Service;

import com.server.controllers.topic.request.CreateTopicRequest;
import com.server.exceptions.NotFoundException;
import com.server.models.entities.Group;
import com.server.models.entities.Topic;
import com.server.repositories.group.GroupRepository;
import com.server.repositories.topic.TopicRepository;
import com.server.repositories.topic.dto.TopicByGroupDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TopicServiceImplement implements TopicService {

    private final TopicRepository topicRepository;
    private final GroupRepository groupRepository;

    @Override
    public List<TopicByGroupDto> getTopicsByGroup(Long groupId) {
        if (!groupRepository.existsById(groupId)) {
            throw new NotFoundException("Không tìm thấy nhóm");
        }
        return topicRepository.findTopicsByGroup(groupId);
    }

    @Override
    public Topic create(CreateTopicRequest request) {
        Group group = groupRepository.findByIdAndDeletedAtIsNull(request.getGroupId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy nhóm"));

        Topic topic = new Topic();
        topic.setName(request.getName());
        topic.setGroup(group);
        

        return topicRepository.save(topic);
    }

    @Override
    public Topic update(Long id, CreateTopicRequest request) {
        Topic topic = topicRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy chủ đề"));
        Group group = groupRepository.findByIdAndDeletedAtIsNull(request.getGroupId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy nhóm"));

        topic.setName(request.getName());
        topic.setGroup(group);

        return topicRepository.save(topic);
    }
}
