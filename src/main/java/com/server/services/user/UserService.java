package com.server.services.user;

import com.server.services.others.data.dto.PageResponse;
import com.server.models.enums.InvitationType;
import com.server.services.user.dto.MyProfileDto;
import com.server.services.user.dto.InviteUserDto;
import com.server.services.user.dto.SimpleUserDto;

public interface  UserService {
    boolean validateUsernamePassword(String username, String password);
    MyProfileDto myProfile();
    PageResponse<SimpleUserDto> search(String q, Integer page, Integer size);

    PageResponse<InviteUserDto> searchInvite(String q, Integer page, Integer size, InvitationType type,
            java.util.UUID entityId);
}
