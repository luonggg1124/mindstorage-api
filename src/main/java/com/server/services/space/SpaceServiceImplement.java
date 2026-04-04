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
        return spaceRepository.findByCreator_Id(currentUser.getId());
    }
    @Override
    //Creat
    
    public Space createSpace(String name) {

        User currentUser = authService.authUser();

        Space space = new Space();
        space.setName(name);
        space.setCreator(currentUser);
        return spaceRepository.save(space);
    }
    //GetByID
    @Override
    public Space getSpaceById(Long id) {

        User currentUser = authService.authUser();
        Space space = spaceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Space not found"));
        if (!space.getCreator().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Access denied");
        }
        return space;
    }
    //Update
     @Override
    public Space updateSpace(Long id, String name) {
        User currentUser = authService.authUser();
        Space space = spaceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Space not found"));

        if (!space.getCreator().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Access denied");
        }
        space.setName(name);
        return spaceRepository.save(space);
    }
    //Delete    
     @Override
    public Space deleteSpace(Long id) {
        User currentUser = authService.authUser();
        Space space = spaceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Space not found"));
        if (!space.getCreator().getId().equals(currentUser.getId())) {
            throw new RuntimeException("failed to delete space");
        }

        spaceRepository.delete(space);
        return space;
    }

    //
}