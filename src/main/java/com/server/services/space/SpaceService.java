package com.server.services.space;

import java.util.List;

import com.server.models.entities.Space;

public interface SpaceService {
    List<Space> getAllUserSpaces();
    Space createSpace(String name);
    Space getSpaceById(Long id);
    Space updateSpace(Long id, String name);
    Space deleteSpace(Long id);
}