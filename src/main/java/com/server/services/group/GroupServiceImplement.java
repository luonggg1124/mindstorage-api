package com.server.services.group;

import com.server.controllers.group.request.CreateGroupRequest;
import com.server.controllers.group.request.UpdateGroupRequest;
import com.server.controllers.group.response.CreateGroupResponse;
import com.server.controllers.group.response.UpdateGroupResponse;
import com.server.exceptions.NotFoundException;
import com.server.models.entities.Group;
import com.server.models.entities.Space;
import com.server.repositories.space.SpaceRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.server.repositories.group.GroupRepository;
import com.server.repositories.group.dto.DetailGroupDto;
import com.server.repositories.group.dto.GroupBySpaceDto;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class GroupServiceImplement implements GroupService {
    private final GroupRepository groupRepository;
    private final SpaceRepository spaceRepository;

    @Override
    public List<GroupBySpaceDto> getGroupsBySpace(Long spaceId) {
        if (!spaceRepository.existsById(spaceId)) {
            throw new NotFoundException("Không tìm thấy không gian");
        }
        List<GroupBySpaceDto> groups = groupRepository.getGroupBySpace(spaceId);

        return groups;
    }
   

    public DetailGroupDto detailGroup(Long id) {
        Group group = groupRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(
                () -> new NotFoundException("Không tìm thấy nhóm"));
        return new DetailGroupDto(group.getId(), group.getName(), group.getDescription(), group.getCreatedAt(), group.getUpdatedAt());
    }

    @Override
    public Group create(String name, String description, Long spaceId) {
        Space space = spaceRepository.findById(spaceId).orElseThrow(
                () -> new NotFoundException("Không tìm thấy không gian"));
        Group newGroup = new Group();
        newGroup.setName(name);
        newGroup.setDescription(description);
        newGroup.setSpace(space);
        newGroup.setEmbedding(new float[1536]);
        return groupRepository.save(newGroup);
    }

    @Override
    public UpdateGroupResponse update(Long id, UpdateGroupRequest groupRequest) {
        Space space = spaceRepository.findById(groupRequest.getSpaceId()).orElseThrow(
                () -> new NotFoundException("Không tìm thấy không gian"));
        Group group = groupRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Không tìm thấy nhóm"));
        BeanUtils.copyProperties(groupRequest, group);
        group.setSpace(space);
        UpdateGroupResponse updateGroupResponse = new UpdateGroupResponse();
        BeanUtils.copyProperties(groupRepository.save(group), updateGroupResponse);
        return updateGroupResponse;
    }

    @Override
    public void delete(Long groupId) {
        Group group = groupRepository.findById(groupId).orElseThrow(
                () -> new NotFoundException("Không tìm thấy nhóm"));
        LocalDateTime current = group.getDeletedAt();
        group.setDeletedAt(current == null ? LocalDateTime.now() : null);
        groupRepository.save(group);
    }
}
