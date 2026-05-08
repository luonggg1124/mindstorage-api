package com.server.services.group;

import com.server.exceptions.NotFoundException;
import com.server.exceptions.ConflictException;
import com.server.models.entities.Group;
import com.server.models.entities.Space;
import com.server.models.entities.SpaceMember;
import com.server.models.entities.User;
import com.server.models.enums.RoleAction;
import com.server.repositories.space.SpaceRepository;
import com.server.repositories.topic.TopicRepository;
import com.server.repositories.space.SpaceMemberRepository;
import com.server.services.auth.AuthService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.server.repositories.group.GroupRepository;
import com.server.services.group.dto.DetailGroupDto;
import com.server.services.group.dto.GroupBySpaceDto;
import com.server.services.others.data.dto.PageResponse;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class GroupService {
    private final GroupRepository groupRepository;
    private final SpaceRepository spaceRepository;
    private final TopicRepository topicRepository;
    private final SpaceMemberRepository spaceMemberRepository;
    private final AuthService authService;

    public PageResponse<GroupBySpaceDto> getGroupsBySpace(UUID spaceId, String q, Integer page, Integer size) {
        if (!spaceRepository.existsById(spaceId)) {
            throw new NotFoundException("Không tìm thấy không gian");
        }
        int pageIndex = page == null ? 0 : Math.max(page - 1, 0);
        Pageable pageable = PageRequest.of(pageIndex, size);
        String search = (q == null || q.isBlank()) ? null : q.trim();
        Page<Group> groups = groupRepository.groupsBySpace(spaceId, search, pageable);

        List<UUID> groupIds = groups.getContent().stream().map(Group::getId).toList();
        Map<UUID, Long> topicCounts = new HashMap<>();
        if (!groupIds.isEmpty()) {
            for (Object[] row : topicRepository.countByGroupIds(groupIds)) {
                topicCounts.put((UUID) row[0], (Long) row[1]);
            }
        }

        List<GroupBySpaceDto> data = groups.getContent().stream()
                .map(g -> new GroupBySpaceDto(
                        g.getId(),
                        g.getName(),
                        g.getDescription(),
                        topicCounts.getOrDefault(g.getId(), 0L),
                        g.getCreatedAt(),
                        g.getUpdatedAt()))
                .collect(Collectors.toList());

        return new PageResponse<>(data, groups.getTotalElements(), groups.getNumber() + 1, groups.getSize());
    }
   

    public DetailGroupDto detailGroup(UUID id) {
        Group group = groupRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(
                () -> new NotFoundException("Không tìm thấy nhóm"));
        return new DetailGroupDto(group.getId(), group.getName(), group.getDescription(), group.getCreatedAt(), group.getUpdatedAt());
    }

    @Transactional
    public Group create(String name, String description, UUID spaceId) {
        User actor = authService.authUser();
        Space space = spaceRepository.findById(spaceId).orElseThrow(
                () -> new NotFoundException("Không tìm thấy không gian"));
        if (!canManageGroupInSpace(actor, space)) {
            throw new ConflictException("Không có quyền tạo nhóm", "spaceId");
        }
        Group newGroup = new Group();
        newGroup.setName(name);
        newGroup.setDescription(description);
        newGroup.setSpace(space);
        LocalDateTime now = LocalDateTime.now();
        newGroup.setLastActivityAt(now);
        spaceRepository.touchLastActivityAt(spaceId, now);
       
        return groupRepository.save(newGroup);
    }

    @Transactional
    public Group update(UUID id, String name, String description, UUID spaceId) {
        User actor = authService.authUser();
        Group group = groupRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy nhóm"));
        Space currentSpace = group.getSpace();
        if (currentSpace == null) {
            throw new ConflictException("Nhóm không thuộc không gian nào", "spaceId");
        }
        if (!canManageGroupInSpace(actor, currentSpace)) {
            throw new ConflictException("Không có quyền cập nhật nhóm", "spaceId");
        }
        Space space = spaceRepository.findByIdAndDeletedAtIsNull(spaceId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy không gian"));
        if (!currentSpace.getId().equals(space.getId()) && !canManageGroupInSpace(actor, space)) {
            throw new ConflictException("Không có quyền chuyển nhóm sang không gian này", "spaceId");
        }

        group.setName(name);
        group.setDescription(description);
        group.setSpace(space);
        LocalDateTime now = LocalDateTime.now();
        group.setLastActivityAt(now);
        groupRepository.touchLastActivityAt(id, now);
        spaceRepository.touchLastActivityAt(spaceId, now);

        return groupRepository.save(group);
    }

    @Transactional
    public void delete(UUID groupId) {
        User actor = authService.authUser();
        Group group = groupRepository.findByIdAndDeletedAtIsNull(groupId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy nhóm"));
        Space space = group.getSpace();
        if (space == null) {
            throw new ConflictException("Nhóm không thuộc không gian nào", "spaceId");
        }
        if (!canDeleteGroupInSpace(actor, space)) {
            throw new ConflictException("Không có quyền xoá nhóm", "spaceId");
        }
        LocalDateTime now = LocalDateTime.now();
        group.setDeletedAt(now);
        group.setLastActivityAt(now);
        groupRepository.save(group);
        if (group.getSpace() != null) {
            spaceRepository.touchLastActivityAt(group.getSpace().getId(), now);
        }
    }

    private boolean canManageGroupInSpace(User actor, Space space) {
        if (space.getCreator() != null && space.getCreator().getId().equals(actor.getId())) {
            return true;
        }
        RoleAction role = spaceMemberRepository.findBySpace_IdAndUser_Id(space.getId(), actor.getId())
                .map(SpaceMember::getRole)
                .orElseThrow(() -> new ConflictException("Bạn không thuộc không gian này", "spaceId"));
        return role == RoleAction.OWNER || role == RoleAction.EDITOR;
    }

    private boolean canDeleteGroupInSpace(User actor, Space space) {
        if (space.getCreator() != null && space.getCreator().getId().equals(actor.getId())) {
            return true;
        }
        RoleAction role = spaceMemberRepository.findBySpace_IdAndUser_Id(space.getId(), actor.getId())
                .map(SpaceMember::getRole)
                .orElseThrow(() -> new ConflictException("Bạn không thuộc không gian này", "spaceId"));
        return role == RoleAction.OWNER;
    }
}
