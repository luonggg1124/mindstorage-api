package com.server.services.space;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.server.exceptions.NotFoundException;
import com.server.models.entities.Space;
import com.server.models.entities.User;
import com.server.repositories.space.SpaceRepository;
import com.server.repositories.space.dto.DetailSpaceDto;
import com.server.repositories.space.dto.MySpaceDto;
import com.server.services.auth.AuthService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class SpaceServiceImplement implements SpaceService {
    private final SpaceRepository spaceRepository;
    private final AuthService authService;

    @Override
    public List<MySpaceDto> getAllUserSpaces() {
        User currentUser = authService.authUser();
        return spaceRepository.mySpaces(currentUser.getId());
    }

    // Detail
    @Override
    public DetailSpaceDto detailSpace(Long id) {
        Space space = spaceRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy không gian"));
        DetailSpaceDto detailSpaceDto = new DetailSpaceDto(space.getId(), space.getName(), space.getDescription(),
                space.getImageUrl(), space.getCreatedAt(), space.getUpdatedAt());
        return detailSpaceDto;
    }

    // Create
    @Override
    public Space createSpace(String name, String description) {

        User currentUser = authService.authUser();
        Space space = new Space();
        space.setName(name);
        space.setDescription(description);
        space.setCreator(currentUser);
        space.setEmbedding(new float[1536]);
        return spaceRepository.save(space);
    }

    // GetByID
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

    // Update
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

    // Delete
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