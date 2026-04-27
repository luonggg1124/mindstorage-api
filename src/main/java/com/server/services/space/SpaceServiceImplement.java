package com.server.services.space;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.server.exceptions.NotFoundException;
import com.server.models.entities.Space;
import com.server.models.entities.SpaceMember;
import com.server.models.entities.User;
import com.server.repositories.group.GroupRepository;
import com.server.repositories.space.SpaceMemberRepository;
import com.server.repositories.space.SpaceRepository;
import com.server.services.auth.AuthService;
import com.server.services.others.data.dto.PageResponse;
import com.server.services.space.dto.DetailSpaceDto;
import com.server.services.space.dto.MySpaceDto;
import com.server.services.space.dto.SpaceMemberUserDto;

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

        List<MySpaceDto> data = spaces.getContent().stream()
                .map(s -> new MySpaceDto(
                        s.getId(),
                        s.getName(),
                        s.getDescription(),
                        s.getImageUrl(),
                        groupCounts.getOrDefault(s.getId(), 0L),
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
        DetailSpaceDto detailSpaceDto = new DetailSpaceDto(space.getId(), space.getName(), space.getDescription(),
                space.getImageUrl(), space.getCreatedAt(), space.getUpdatedAt());
        return detailSpaceDto;
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
                            u.getAvatarUrl(),
                            u.getFullName(),
                            u.getGender(),
                            sm.getCreatedAt(),
                            sm.getRole());
                })
                .toList();

        return new PageResponse<>(data, members.getTotalElements(), members.getNumber() + 1, members.getSize());
    }

    //
}