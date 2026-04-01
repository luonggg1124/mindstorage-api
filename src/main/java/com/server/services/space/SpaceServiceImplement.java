package com.server.services.space;

import java.util.List;

import org.springframework.stereotype.Service;

import com.server.models.entities.Space;
import com.server.models.entities.User;
import com.server.repositories.space.SpaceRepository;
import com.server.services.auth.AuthService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class SpaceServiceImplement implements SpaceService {
    private final SpaceRepository spaceRepository;
    private final AuthService authService;
    @Override
    public List<Space> getAllUserSpaces() {
        User currentUser = authService.authUser();
        return spaceRepository.findByOwnerId(currentUser.getId());
    }
}
