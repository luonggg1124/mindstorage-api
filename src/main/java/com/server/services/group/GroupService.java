package com.server.services.group;

import com.server.models.entities.Group;
import com.server.services.group.dto.DetailGroupDto;
import com.server.services.group.dto.GroupBySpaceDto;
import com.server.services.others.data.dto.PageResponse;

public interface GroupService {
    PageResponse<GroupBySpaceDto> getGroupsBySpace(Long spaceId, String q, Integer page, Integer size);

    Group create(String name, String description, Long spaceId);

    DetailGroupDto detailGroup(Long id);

    Group update(Long id, String name, String description, Long spaceId);

    void delete(Long groupId);
}