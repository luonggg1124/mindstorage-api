package com.server.services.invitation;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.server.exceptions.ConflictException;
import com.server.exceptions.NotFoundException;
import com.server.models.entities.Invitation;
import com.server.models.entities.Notification;
import com.server.models.entities.User;
import com.server.models.entities.Group;
import com.server.models.entities.GroupMember;
import com.server.models.entities.Space;
import com.server.models.entities.SpaceMember;
import com.server.models.enums.InvitationStatus;
import com.server.models.enums.InvitationType;
import com.server.models.enums.NotificationType;
import com.server.models.enums.RoleAction;
import com.server.repositories.invitation.InvitationRepository;
import com.server.repositories.group.GroupMemberRepository;
import com.server.repositories.group.GroupRepository;
import com.server.repositories.space.SpaceMemberRepository;
import com.server.repositories.space.SpaceRepository;
import com.server.repositories.user.UserRepository;
import com.server.services.auth.AuthService;
import com.server.services.notification.NotificationService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class InvitationServiceImplement implements InvitationService {
    private final InvitationRepository invitationRepository;
    private final AuthService authService;
    private final NotificationService notificationService;
    private final UserRepository userRepository;
    private final SpaceRepository spaceRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final SpaceMemberRepository spaceMemberRepository;

    @Transactional
    @Override
    public void invite(Long inviteeId, UUID entityId, InvitationType entityType) {
        User user = authService.authUser();

        if (inviteeId == null) {
            throw new NotFoundException("Không tìm thấy người được mời");
        }
        if (entityId == null) {
            throw new NotFoundException("Không tìm thấy đối tượng được mời");
        }
        if (entityType == null) {
            throw new NotFoundException("Không hợp lệ");
        }
        if (user.getId().equals(inviteeId)) {
            throw new ConflictException("Không thể tự mời chính mình", "inviteeId");
        }
        if (!userRepository.existsById(inviteeId)) {
            throw new NotFoundException("Không tìm thấy người được mời");
        }

        boolean entityExists = switch (entityType) {
            case SPACE -> spaceRepository.existsById(entityId);
            case GROUP -> groupRepository.existsById(entityId);
        };
        if (!entityExists) {
            throw new NotFoundException("Không tìm thấy đối tượng được mời");
        }

        boolean alreadyInvited = invitationRepository.existsByInviteeIdAndEntityIdAndTypeAndStatus(
                inviteeId, entityId, entityType, InvitationStatus.PENDING);
        if (alreadyInvited) {
            throw new ConflictException("Đã gửi lời mời trước đó", "inviteeId");
        }

        Invitation invitation = new Invitation();
        invitation.setInviterId(user.getId());
        invitation.setInviteeId(inviteeId);
        invitation.setEntityId(entityId);
        invitation.setType(entityType);
        Invitation savedInvitation = invitationRepository.save(invitation);

        String entityName = resolveEntityName(entityId, entityType);
        String inviterName = resolveInviterName(user);

        String title = buildTitle(entityType);
        String content = buildContent(entityType, entityName, inviterName);
        NotificationType notificationType = entityType == InvitationType.GROUP
                ? NotificationType.INVITE_TO_JOIN_GROUP
                : NotificationType.INVITE_TO_JOIN_SPACE;
        Map<String, Object> data = Map.of(
                "invitationId", savedInvitation.getId(),
                "entityId", entityId,
                "entityType", entityType,
                "entityName", entityName,
                "senderId", user.getId(),
                "senderName", user.getFullName()
            );
        Notification notification = notificationService.create(
                savedInvitation.getInviteeId(),
                title,
                content,
                data,
                notificationType,
                entityId);

        notificationService.sendNotification(savedInvitation.getInviteeId(), notification);
    }

    @Transactional
    @Override
    public void accept(UUID id){
        User user = authService.authUser();

        Invitation inv = invitationRepository.findById(id).orElseThrow(() -> new NotFoundException("Không thể mời."));
        if(!inv.getInviteeId().equals(user.getId())){
            throw new ConflictException("Không có quyền", "invitationId");
        }
        if(inv.getStatus() != InvitationStatus.PENDING){
            throw new ConflictException("Không hợp lệ", "status");
        }
        if(isAlreadyMember(user.getId(), inv.getEntityId(), inv.getType())){
            throw new ConflictException("Đã là thành viên", "userId");
        }
        inv.setStatus(InvitationStatus.ACCEPTED);
        inv.setRespondedAt(LocalDateTime.now());
        invitationRepository.save(inv);
        
        switch(inv.getType()){
            case GROUP -> {
                Group group = groupRepository.findById(inv.getEntityId()).orElseThrow(() -> new NotFoundException("Không tìm thấy nhóm"));
                GroupMember groupMember = new GroupMember();
                groupMember.setUser(user);
                groupMember.setGroup(group);
                groupMember.setRole(RoleAction.VIEWER);
                groupMemberRepository.save(groupMember);
            }
            case SPACE -> {
                Space space = spaceRepository.findById(inv.getEntityId()).orElseThrow(() -> new NotFoundException("Không tìm thấy không gian"));
                SpaceMember spaceMember = new SpaceMember();
                spaceMember.setUser(user);
                spaceMember.setSpace(space);
                spaceMember.setRole(RoleAction.VIEWER);
                spaceMemberRepository.save(spaceMember);
            }
        }
        notificationService.sendNotification(inv.getInviterId(), 
        Map.of(
            "type", InvitationStatus.ACCEPTED,
            "invitationId", inv.getId(),
            "userId", user.getId()
        ));
    }


    @Transactional
    @Override
    public void reject(UUID id){
        User user = authService.authUser();
        Invitation inv = invitationRepository.findById(id).orElseThrow(() -> new NotFoundException("Không tìm thấy lời mời"));
        if(!inv.getInviteeId().equals(user.getId())){
            throw new ConflictException("Không có quyền","inviteId");
        }
        if(inv.getStatus() != InvitationStatus.PENDING ){
            throw new ConflictException("Không hợp lệ","status");
        }
        inv.setStatus(InvitationStatus.REJECTED);
        inv.setRespondedAt(LocalDateTime.now());
        invitationRepository.save(inv);
        notificationService.sendNotification(inv.getInviterId(), Map.of(
            "type", InvitationStatus.REJECTED,
            "invitationId", inv.getId()
        ));

    }
    private String buildTitle(InvitationType entityType) {
        return entityType == InvitationType.GROUP
                ? "Lời mời vào nhóm"
                : "Lời mời vào không gian";
    }

    private String buildContent(InvitationType entityType, String entityName, String inviterName) {
        String typeText = entityType == InvitationType.GROUP ? "nhóm" : "không gian";
        String nameText = (entityName == null || entityName.isBlank()) ? "" : (" " + entityName.trim());
        String fromText = (inviterName == null || inviterName.isBlank()) ? "" : (" từ " + inviterName.trim());
        return "Bạn có lời mời vào " + typeText + nameText + fromText;
    }

    private String resolveInviterName(User inviter) {
        if (inviter == null) {
            return null;
        }
        if (inviter.getFullName() != null && !inviter.getFullName().isBlank()) {
            return inviter.getFullName();
        }
        return inviter.getUsername();
    }

    private String resolveEntityName(UUID entityId, InvitationType entityType) {
        return switch (entityType) {
            case SPACE -> spaceRepository.findById(entityId).map(Space::getName).orElse(null);
            case GROUP -> groupRepository.findById(entityId).map(Group::getName).orElse(null);
        };
    }
    private boolean isAlreadyMember(Long userId, UUID entityId, InvitationType type){
        return switch (type){
            case GROUP -> groupMemberRepository.existsByUserIdAndGroupId(userId, entityId);
            case SPACE -> spaceMemberRepository.existsByUserIdAndSpaceId(userId, entityId);
        };
    }
}
