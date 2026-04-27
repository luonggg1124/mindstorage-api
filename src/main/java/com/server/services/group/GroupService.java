package com.server.services.group;

import java.util.UUID;

import com.server.models.entities.Group;
import com.server.services.group.dto.DetailGroupDto;
import com.server.services.group.dto.GroupBySpaceDto;
import com.server.services.others.data.dto.PageResponse;

public interface GroupService {
    PageResponse<GroupBySpaceDto> getGroupsBySpace(UUID spaceId, String q, Integer page, Integer size);

    Group create(String name, String description, UUID spaceId);

    DetailGroupDto detailGroup(UUID id);

    Group update(UUID id, String name, String description, UUID spaceId);

    void delete(UUID groupId);
}