package com.server.services.space;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.server.exceptions.BadRequestException;
import com.server.exceptions.ConflictException;
import com.server.exceptions.NotFoundException;
import com.server.models.entities.Space;
import com.server.models.entities.SpaceMember;
import com.server.models.entities.User;
import com.server.models.enums.InvitationType;
import com.server.models.enums.RoleAction;
import com.server.repositories.group.GroupRepository;
import com.server.repositories.space.SpaceMemberRepository;
import com.server.repositories.space.SpaceRepository;
import com.server.repositories.user.UserRepository;
import com.server.services.attachment.AttachmentService;
import com.server.services.auth.AuthService;
import com.server.services.notification.NotificationService;
import com.server.services.others.data.dto.PageResponse;
import com.server.services.space.dto.DetailSpaceDto;
import com.server.services.space.dto.MySpaceDto;
import com.server.services.space.dto.MySpaceRoleDto;
import com.server.services.space.dto.SpaceMemberUserDto;
import com.server.services.user.dto.SimpleUserDto;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class SpaceServiceImplement implements SpaceService {
    private final SpaceRepository spaceRepository;
    private final AuthService authService;
    private final GroupRepository groupRepository;
    private final SpaceMemberRepository spaceMemberRepository;
    private final UserRepository userRepository;
    private final AttachmentService fileService;
    private final NotificationService notificationService;

    @Override
    public PageResponse<MySpaceDto> mySpaces(String q, Integer page, Integer size) {
        User currentUser = authService.authUser();
        int pageIndex = page == null ? 0 : Math.max(page - 1, 0);
        Pageable pageable = PageRequest.of(pageIndex, size);
        String search = (q == null || q.isBlank()) ? null : q.trim();
        Page<Space> spaces = spaceRepository.mySpaces(currentUser.getId(), search, pageable);

        List<UUID> spaceIds = spaces.getContent().stream().map(Space::getId).toList();
        Map<UUID, Long> groupCounts = new HashMap<>();
        if (!spaceIds.isEmpty()) {
            for (Object[] row : groupRepository.countBySpaceIds(spaceIds)) {
                groupCounts.put((UUID) row[0], (Long) row[1]);
            }
        }

        Map<UUID, RoleAction> rolesBySpaceId = new HashMap<>();
        if (!spaceIds.isEmpty()) {
            for (Object[] row : spaceMemberRepository.rolesByUserAndSpaceIds(currentUser.getId(), spaceIds)) {
                rolesBySpaceId.put((UUID) row[0], (RoleAction) row[1]);
            }
        }

        List<Long> ownerIds = spaces.getContent().stream()
                .map(s -> s.getCreator() == null ? null : s.getCreator().getId())
                .filter(id -> id != null)
                .distinct()
                .toList();
        final Map<Long, SimpleUserDto> ownersById = ownerIds.isEmpty()
                ? Map.of()
                : userRepository.findAllById(ownerIds).stream()
                        .collect(Collectors.toMap(
                                User::getId,
                                u -> new SimpleUserDto(
                                        u.getId(),
                                        u.getUsername(),
                                        fileService.buildPublicUrl(u.getAvatarFileKey()),
                                        u.getFullName(),
                                        u.getGender())));

        List<MySpaceDto> data = spaces.getContent().stream()
                .map(s -> new MySpaceDto(
                        s.getId(),
                        s.getName(),
                        s.getDescription(),
                        fileService.buildPublicUrl(s.getImageFileKey()),
                        groupCounts.getOrDefault(s.getId(), 0L),
                        s.getCreator() == null ? null : ownersById.get(s.getCreator().getId()),
                        s.getCreator() != null && currentUser.getId().equals(s.getCreator().getId())
                                ? RoleAction.OWNER
                                : rolesBySpaceId.getOrDefault(s.getId(), RoleAction.VIEWER),
                        s.getLastActivityAt(),
                        s.getCreatedAt(),
                        s.getUpdatedAt()))
                .collect(Collectors.toList());

        return new PageResponse<>(data, spaces.getTotalElements(), spaces.getNumber() + 1, spaces.getSize());
    }

    // Detail
    @Override
    public DetailSpaceDto detail(UUID id) {
        Space space = spaceRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy không gian"));
        return new DetailSpaceDto(
                space.getId(),
                space.getName(),
                space.getDescription(),
                fileService.buildPublicUrl(space.getImageFileKey()),
                space.getCreatedAt(),
                space.getUpdatedAt());
    }

    @Override
    @Transactional(readOnly = true)
    public MySpaceRoleDto myRoleInSpace(UUID spaceId) {
        User currentUser = authService.authUser();
        Space space = spaceRepository.findByIdAndDeletedAtIsNull(spaceId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy không gian"));
        if (space.getCreator() != null && space.getCreator().getId().equals(currentUser.getId())) {
            return new MySpaceRoleDto(RoleAction.OWNER);
        }
        RoleAction role = spaceMemberRepository.findBySpace_IdAndUser_Id(spaceId, currentUser.getId())
                .map(SpaceMember::getRole)
                .orElseThrow(() -> new ConflictException("Bạn không thuộc không gian này", "spaceId"));
        return new MySpaceRoleDto(role);
    }

    // Create
    @Override
    public Space create(String name, String description) {
        User currentUser = authService.authUser();
        Space space = new Space();
        space.setName(name);
        space.setDescription(description);
        space.setCreator(currentUser);
        return spaceRepository.save(space);
    }

    // GetByID
    @Override
    public Space getById(UUID id) {

        Space space = spaceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Space not found"));

        return space;
    }

    // Update
    @Override
    public Space update(UUID id, String name, String description) {

        Space space = spaceRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy không gian"));
        space.setName(name);
        space.setDescription(description);
        return spaceRepository.save(space);
    }

    // Delete
    @Override
    public void delete(UUID id) {
        User currentUser = authService.authUser();
        Space space = spaceRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy không gian"));
        space.setDeletedAt(LocalDateTime.now());
        space.setDeletedBy(currentUser);
        spaceRepository.save(space);
    }

    @Override
    public PageResponse<SpaceMemberUserDto> members(UUID spaceId, String q, Integer page, Integer size) {
        if (!spaceRepository.existsById(spaceId)) {
            throw new NotFoundException("Không tìm thấy không gian");
        }

        int pageIndex = page == null ? 0 : Math.max(page - 1, 0);
        int pageSize = size == null ? 10 : size;
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        String search = (q == null || q.isBlank()) ? null : q.trim();

        Page<SpaceMember> members = spaceMemberRepository.membersBySpaceId(spaceId, search, pageable);
        List<SpaceMemberUserDto> data = members.getContent().stream()
                .map(sm -> {
                    User u = sm.getUser();
                    return new SpaceMemberUserDto(
                            u.getId(),
                            u.getUsername(),
                            fileService.buildPublicUrl(u.getAvatarFileKey()),
                            u.getFullName(),
                            u.getGender(),
                            sm.getCreatedAt(),
                            sm.getRole());
                })
                .toList();

        return new PageResponse<>(data, members.getTotalElements(), members.getNumber() + 1, members.getSize());
    }

    @Override
    @Transactional
    public SpaceMemberUserDto changeMemberRole(UUID spaceId, Long memberUserId, RoleAction newRole) {
        User actor = authService.authUser();
        Space space = spaceRepository.findByIdAndDeletedAtIsNull(spaceId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy không gian"));
        if (!canManageSpaceRoles(actor, space)) {
            throw new ConflictException("Không có quyền thay đổi vai trò", "role");
        }
        SpaceMember member = spaceMemberRepository.findBySpace_IdAndUser_Id(spaceId, memberUserId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy thành viên"));
        RoleAction previous = member.getRole();
        if (previous == newRole) {
            throw new BadRequestException("Vai trò không thay đổi", "role");
        }
        member.setRole(newRole);
        spaceMemberRepository.save(member);

        User target = member.getUser();
        notificationService.upsertRoleChangeNotification(
                memberUserId,
                InvitationType.SPACE,
                spaceId,
                space.getName(),
                previous,
                newRole,
                memberUserId,
                target.getFullName(),
                actor.getId(),
                actor.getFullName());

        return new SpaceMemberUserDto(
                target.getId(),
                target.getUsername(),
                fileService.buildPublicUrl(target.getAvatarFileKey()),
                target.getFullName(),
                target.getGender(),
                member.getCreatedAt(),
                member.getRole());
    }

    private boolean canManageSpaceRoles(User actor, Space space) {
        if (space.getCreator() != null && space.getCreator().getId().equals(actor.getId())) {
            return true;
        }
        return spaceMemberRepository.findBySpace_IdAndUser_Id(space.getId(), actor.getId())
                .map(sm -> sm.getRole() == RoleAction.OWNER)
                .orElse(false);
    }
}