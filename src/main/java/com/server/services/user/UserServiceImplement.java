package com.server.services.user;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.server.exceptions.BadRequestException;
import com.server.exceptions.NotFoundException;
import com.server.models.entities.User;
import com.server.models.enums.InvitationType;
import com.server.repositories.group.GroupMemberRepository;
import com.server.repositories.group.GroupRepository;
import com.server.repositories.follower.FollowerRepository;
import com.server.repositories.space.SpaceMemberRepository;
import com.server.repositories.space.SpaceRepository;
import com.server.repositories.user.UserRepository;
import com.server.services.auth.AuthService;
import com.server.services.others.data.dto.PageResponse;
import com.server.services.user.dto.InviteUserDto;
import com.server.services.user.dto.MyProfileDto;
import com.server.services.user.dto.SimpleUserDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserServiceImplement implements UserService {
    private final AuthService authService;
    private final UserRepository userRepository;
    private final FollowerRepository followerRepository;
    private final SpaceRepository spaceRepository;
    private final SpaceMemberRepository spaceMemberRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;

    @Override
    public boolean validateUsernamePassword(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            throw new BadRequestException("Tên đăng nhập không được để trống", "username");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new BadRequestException("Mật khẩu không được để trống", "password");
        }
        if (password.length() < 8) {
            throw new BadRequestException("Mật khẩu phải có ít nhất 8 ký tự", "password");
        }
        if (userRepository.existsByUsername(username)) {
            throw new BadRequestException("Tên đăng nhập đã tồn tại", "username");
        }
        return true;
    }

    public MyProfileDto myProfile() {
        User user = authService.authUser();
        long followersCount = followerRepository.countByFollower_Id(user.getId());
        long followingCount = followerRepository.countByFollowing_Id(user.getId());
            long spacesCount = spaceRepository.countByCreator_IdAndDeletedAtIsNull(user.getId());
            long spaceMembersCount = spaceMemberRepository.countByUser_IdAndSpace_DeletedAtIsNull(user.getId());
        return new MyProfileDto(user.getId(), user.getUsername(), user.getFullName(), user.getAvatarUrl(),
                user.getEmail(), user.getHobbies(), user.getGender(), followersCount, followingCount, spacesCount, spaceMembersCount);
    }

    @Override
    public PageResponse<SimpleUserDto> search(String q, Integer page, Integer size) {
        int pageIndex = page == null ? 0 : Math.max(page - 1, 0);
        int pageSize = size == null ? 10 : size;
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        String search = (q == null || q.isBlank()) ? null : q.trim();

        Page<User> users = userRepository.searchUsers(search, pageable);
        var data = users.getContent().stream()
                .map(u -> new SimpleUserDto(
                        u.getId(),
                        u.getUsername(),
                        u.getAvatarUrl(),
                        u.getFullName(),
                        u.getGender()))
                .collect(Collectors.toList());

        return new PageResponse<>(data, users.getTotalElements(), users.getNumber() + 1, users.getSize());
    }

    @Override
    public PageResponse<InviteUserDto> searchInvite(String q, Integer page, Integer size, InvitationType type,
            UUID entityId) {
        User currentUser = authService.authUser();

        if (type == null) {
            throw new BadRequestException("type không hợp lệ", "type");
        }
        if (entityId == null) {
            throw new BadRequestException("entityId không hợp lệ", "entityId");
        }

        boolean entityExists = switch (type) {
            case SPACE -> spaceRepository.existsById(entityId);
            case GROUP -> groupRepository.existsById(entityId);
        };
        if (!entityExists) {
            throw new NotFoundException("Không tìm thấy đối tượng");
        }

        int pageIndex = page == null ? 0 : Math.max(page - 1, 0);
        int pageSize = size == null ? 10 : size;
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        String search = (q == null || q.isBlank()) ? null : q.trim();

        Page<User> users = userRepository.searchUsers(search, pageable);

        Set<Long> memberUserIds = new HashSet<>(switch (type) {
            case SPACE -> spaceMemberRepository.findMemberUserIdsBySpaceId(entityId);
            case GROUP -> groupMemberRepository.findMemberUserIdsByGroupId(entityId);
        });

        var data = users.getContent().stream()
                .filter(u -> !u.getId().equals(currentUser.getId()))
                .map(u -> new InviteUserDto(
                        u.getId(),
                        u.getUsername(),
                        u.getAvatarUrl(),
                        u.getFullName(),
                        u.getGender(),
                        memberUserIds.contains(u.getId())))
                .collect(Collectors.toList());

        return new PageResponse<>(data, users.getTotalElements(), users.getNumber() + 1, users.getSize());
    }

}
