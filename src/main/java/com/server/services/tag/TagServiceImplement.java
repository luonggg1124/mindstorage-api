package com.server.services.tag;

import java.util.List;

import org.springframework.stereotype.Service;

import com.server.controllers.tag.request.CreateTagRequest;
import com.server.controllers.tag.response.CreateTagResponse;
import com.server.exceptions.NotFoundException;
import com.server.models.entities.Group;
import com.server.models.entities.Tag;
import com.server.repositories.group.GroupRepository;
import com.server.repositories.tag.TagRepository;
import com.server.repositories.tag.dto.TagByGroupDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TagServiceImplement implements TagService {

    private final TagRepository tagRepository;
    private final GroupRepository groupRepository;

    @Override
    public List<TagByGroupDto> getTagsByGroup(Long groupId) {
        if (!groupRepository.existsById(groupId)) {
            throw new NotFoundException("Không tìm thấy nhóm");
        }
        return tagRepository.findTagsByGroup(groupId);
    }

    @Override
    public CreateTagResponse create(CreateTagRequest request) {
        Group group = groupRepository.findByIdAndDeletedAtIsNull(request.getGroupId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy nhóm"));

        Tag tag = new Tag();
        tag.setName(request.getName());
        tag.setGroup(group);
        tag.setEmbedding(new float[1536]);

        Tag saved = tagRepository.save(tag);
        return new CreateTagResponse(
                saved.getId(),
                saved.getName(),
                group.getId(),
                saved.getCreatedAt(),
                saved.getUpdatedAt());
    }
}

