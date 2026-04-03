package com.server.services.group;

import com.server.controllers.group.request.CreateGroupRequest;
import com.server.controllers.group.request.UpdateGroupRequest;
import com.server.controllers.group.response.CreateGroupResponse;
import com.server.controllers.group.response.GroupsBySpaceResponse;
import com.server.controllers.group.response.UpdateGroupResponse;

import java.util.List;

public interface GroupService {
    List<GroupsBySpaceResponse> getAllBySpace(Long spaceId, int page, int size);

    CreateGroupResponse create(CreateGroupRequest group);

    UpdateGroupResponse update(Long id,UpdateGroupRequest group);

    void delete(Long groupId);
}