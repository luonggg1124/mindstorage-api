package com.server.services.space;

import java.util.UUID;

import com.server.models.entities.Space;
import com.server.models.enums.RoleAction;
import com.server.services.others.data.dto.PageResponse;
import com.server.services.space.dto.DetailSpaceDto;
import com.server.services.space.dto.MySpaceDto;
import com.server.services.space.dto.MySpaceRoleDto;
import com.server.services.space.dto.SpaceMemberUserDto;

public interface SpaceService {

    PageResponse<MySpaceDto> mySpaces(String q, Integer page, Integer size);

    DetailSpaceDto detail(UUID id);

    MySpaceRoleDto myRoleInSpace(UUID spaceId);

    Space create(String name, String description);

    Space getById(UUID id);

    Space update(UUID id, String name, String description);

    void delete(UUID id);

    PageResponse<SpaceMemberUserDto> members(UUID spaceId, String q, Integer page, Integer size);

    SpaceMemberUserDto changeMemberRole(UUID spaceId, Long memberUserId, RoleAction newRole);
}
