package com.server.services.topic;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.server.controllers.topic.request.CreateTopicRequest;
import com.server.exceptions.ConflictException;
import com.server.exceptions.NotFoundException;
import com.server.models.entities.Group;
import com.server.models.entities.Space;
import com.server.models.entities.SpaceMember;
import com.server.models.entities.Topic;
import com.server.models.entities.User;
import com.server.models.enums.RoleAction;
import com.server.repositories.group.GroupRepository;
import com.server.repositories.space.SpaceRepository;
import com.server.repositories.space.SpaceMemberRepository;
import com.server.repositories.topic.TopicRepository;
import com.server.services.auth.AuthService;
import com.server.services.topic.dto.TopicByGroupDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TopicService {

    private final TopicRepository topicRepository;
    private final GroupRepository groupRepository;
    private final SpaceRepository spaceRepository;
    private final SpaceMemberRepository spaceMemberRepository;
    private final AuthService authService;

    public List<TopicByGroupDto> getTopicsByGroup(UUID groupId) {
        if (!groupRepository.existsById(groupId)) {
            throw new NotFoundException("Không tìm thấy nhóm");
        }
        return topicRepository.findTopicsByGroup(groupId).stream()
                .map(t -> new TopicByGroupDto(t.getId(), t.getName(), t.getCreatedAt(), t.getUpdatedAt()))
                .toList();
    }

    @Transactional
    public Topic create(CreateTopicRequest request) {
        User actor = authService.authUser();
        Group group = groupRepository.findByIdAndDeletedAtIsNull(request.getGroupId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy nhóm"));
        Space space = group.getSpace();
        if (space == null) {
            throw new ConflictException("Nhóm không thuộc không gian nào", "spaceId");
        }
        if (!canEditTopicInSpace(actor, space)) {
            throw new ConflictException("Không có quyền tạo chủ đề", "groupId");
        }

        Topic topic = new Topic();
        topic.setName(request.getName());
        topic.setGroup(group);
        

        Topic saved = topicRepository.save(topic);
        LocalDateTime now = LocalDateTime.now();
        groupRepository.touchLastActivityAt(group.getId(), now);
        if (group.getSpace() != null) {
            spaceRepository.touchLastActivityAt(group.getSpace().getId(), now);
        }
        return saved;
    }

    @Transactional
    public Topic update(UUID id, CreateTopicRequest request) {
        User actor = authService.authUser();
        Topic topic = topicRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy chủ đề"));
        Group group = groupRepository.findByIdAndDeletedAtIsNull(request.getGroupId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy nhóm"));
        Space currentSpace = topic.getGroup() == null ? null : topic.getGroup().getSpace();
        if (currentSpace == null) {
            throw new ConflictException("Nhóm không thuộc không gian nào", "spaceId");
        }
        if (!canEditTopicInSpace(actor, currentSpace)) {
            throw new ConflictException("Không có quyền cập nhật chủ đề", "groupId");
        }
        Space targetSpace = group.getSpace();
        if (targetSpace == null) {
            throw new ConflictException("Nhóm không thuộc không gian nào", "spaceId");
        }
        if (!currentSpace.getId().equals(targetSpace.getId()) && !canEditTopicInSpace(actor, targetSpace)) {
            throw new ConflictException("Không có quyền chuyển chủ đề sang nhóm này", "groupId");
        }

        topic.setName(request.getName());
        topic.setGroup(group);

        Topic saved = topicRepository.save(topic);
        LocalDateTime now = LocalDateTime.now();
        groupRepository.touchLastActivityAt(group.getId(), now);
        if (group.getSpace() != null) {
            spaceRepository.touchLastActivityAt(group.getSpace().getId(), now);
        }
        return saved;
    }

    @Transactional
    public void delete(UUID id) {
        User actor = authService.authUser();
        Topic topic = topicRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy chủ đề"));
        Group group = topic.getGroup();
        if (group == null) {
            throw new ConflictException("Chủ đề không thuộc nhóm nào", "groupId");
        }
        Space space = group.getSpace();
        if (space == null) {
            throw new ConflictException("Nhóm không thuộc không gian nào", "spaceId");
        }
        if (!canDeleteTopicInSpace(actor, space)) {
            throw new ConflictException("Không có quyền xoá chủ đề", "groupId");
        }

        LocalDateTime now = LocalDateTime.now();
        topic.setDeletedAt(now);
        topicRepository.save(topic);
        groupRepository.touchLastActivityAt(group.getId(), now);
        if (space != null) {
            spaceRepository.touchLastActivityAt(space.getId(), now);
        }
    }

    private boolean canEditTopicInSpace(User actor, Space space) {
        if (space.getCreator() != null && space.getCreator().getId().equals(actor.getId())) {
            return true;
        }
        RoleAction role = spaceMemberRepository.findBySpace_IdAndUser_Id(space.getId(), actor.getId())
                .map(SpaceMember::getRole)
                .orElseThrow(() -> new ConflictException("Bạn không thuộc không gian này", "spaceId"));
        return role == RoleAction.OWNER || role == RoleAction.EDITOR;
    }

    private boolean canDeleteTopicInSpace(User actor, Space space) {
        if (space.getCreator() != null && space.getCreator().getId().equals(actor.getId())) {
            return true;
        }
        RoleAction role = spaceMemberRepository.findBySpace_IdAndUser_Id(space.getId(), actor.getId())
                .map(SpaceMember::getRole)
                .orElseThrow(() -> new ConflictException("Bạn không thuộc không gian này", "spaceId"));
        return role == RoleAction.OWNER;
    }
}
