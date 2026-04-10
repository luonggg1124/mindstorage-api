package com.server.services.group;

import com.server.exceptions.NotFoundException;
import com.server.models.entities.Group;
import com.server.models.entities.Space;
import com.server.repositories.space.SpaceRepository;
import com.server.repositories.topic.TopicRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.server.repositories.group.GroupRepository;
import com.server.services.group.dto.DetailGroupDto;
import com.server.services.group.dto.GroupBySpaceDto;
import com.server.services.others.data.dto.PageResponse;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class GroupServiceImplement implements GroupService {
    private final GroupRepository groupRepository;
    private final SpaceRepository spaceRepository;
    private final TopicRepository topicRepository;

    @Override
    public PageResponse<GroupBySpaceDto> getGroupsBySpace(Long spaceId, String q, Integer page, Integer size) {
        if (!spaceRepository.existsById(spaceId)) {
            throw new NotFoundException("Không tìm thấy không gian");
        }
        int pageIndex = page == null ? 0 : Math.max(page - 1, 0);
        Pageable pageable = PageRequest.of(pageIndex, size);
        String search = (q == null || q.isBlank()) ? null : q.trim();
        Page<Group> groups = groupRepository.groupsBySpace(spaceId, search, pageable);

        List<Long> groupIds = groups.getContent().stream().map(Group::getId).toList();
        Map<Long, Long> topicCounts = new HashMap<>();
        if (!groupIds.isEmpty()) {
            for (Object[] row : topicRepository.countByGroupIds(groupIds)) {
                topicCounts.put((Long) row[0], (Long) row[1]);
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
       
        return groupRepository.save(newGroup);
    }

    @Override
    public Group update(Long id, String name, String description, Long spaceId) {
        Group group = groupRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy nhóm"));
        Space space = spaceRepository.findByIdAndDeletedAtIsNull(spaceId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy không gian"));

        group.setName(name);
        group.setDescription(description);
        group.setSpace(space);

        return groupRepository.save(group);
    }

    @Override
    public void delete(Long groupId) {
        Group group = groupRepository.findByIdAndDeletedAtIsNull(groupId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy nhóm"));
        group.setDeletedAt(LocalDateTime.now());
        groupRepository.save(group);
    }
}
