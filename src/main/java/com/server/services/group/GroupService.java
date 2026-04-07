package com.server.services.group;

import com.server.controllers.group.request.UpdateGroupRequest;
import com.server.controllers.group.response.UpdateGroupResponse;
import com.server.models.entities.Group;
import com.server.repositories.group.dto.DetailGroupDto;
import com.server.repositories.group.dto.GroupBySpaceDto;

import java.util.List;

public interface GroupService {
    List<GroupBySpaceDto> getGroupsBySpace(Long spaceId);

    Group create(String name, String description, Long spaceId);

    DetailGroupDto detailGroup(Long id);

    UpdateGroupResponse update(Long id,UpdateGroupRequest group);

    void delete(Long groupId);
}