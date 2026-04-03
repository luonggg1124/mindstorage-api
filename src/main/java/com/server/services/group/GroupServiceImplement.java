package com.server.services.group;

import com.server.controllers.group.request.CreateGroupRequest;
import com.server.controllers.group.request.UpdateGroupRequest;
import com.server.controllers.group.response.CreateGroupResponse;
import com.server.controllers.group.response.GroupsBySpaceResponse;
import com.server.controllers.group.response.UpdateGroupResponse;
import com.server.exceptions.NotFoundException;
import com.server.models.entities.Group;
import com.server.models.entities.Space;
import com.server.repositories.space.SpaceRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.server.repositories.group.GroupRepository;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class GroupServiceImplement implements GroupService {
    private final GroupRepository groupRepository;
    private final SpaceRepository spaceRepository;

    @Override
    public List<GroupsBySpaceResponse> getAllBySpace(Long spaceId, int page, int size) {
        if(!spaceRepository.existsById(spaceId)) {
            throw new NotFoundException("Không tìm thấy Space");
        }
        Pageable pageable = PageRequest.of(page, size);
        List<Group> groups = groupRepository.getAllBySpace_Id(spaceId, pageable);
        List<GroupsBySpaceResponse> groupsBySpaces = new ArrayList<>();
        BeanUtils.copyProperties(groups, groupsBySpaces);
        return groupsBySpaces;
    }

    @Override
    public CreateGroupResponse create(CreateGroupRequest groupRequest) {
        Space space = spaceRepository.findById(groupRequest.getSpaceId()).orElseThrow(
                () -> new NotFoundException("Không tìm thấy Space")
        );
        Group newGroup = new Group();
        BeanUtils.copyProperties(groupRequest, newGroup);
        newGroup.setSpace(space);
        CreateGroupResponse createGroupResponse = new CreateGroupResponse();
        BeanUtils.copyProperties(groupRepository.save(newGroup), createGroupResponse);
        return createGroupResponse;
    }

    @Override
    public UpdateGroupResponse update(Long id ,UpdateGroupRequest groupRequest) {
        Space space = spaceRepository.findById(groupRequest.getSpaceId()).orElseThrow(
                () -> new NotFoundException("Không tìm thấy Space")
        );
        Group group = groupRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Không tìm thấy Group")
        );
        BeanUtils.copyProperties(groupRequest, group);
        group.setSpace(space);
        UpdateGroupResponse updateGroupResponse = new UpdateGroupResponse();
        BeanUtils.copyProperties(groupRepository.save(group), updateGroupResponse);
        return updateGroupResponse;
    }

    @Override
    public void delete(Long groupId) {
        Group group = groupRepository.findById(groupId).orElseThrow(
                () -> new NotFoundException("Không tìm thấy Group")
        );
        group.setIsDeleted(!group.getIsDeleted());
        groupRepository.save(group);
    }
}
