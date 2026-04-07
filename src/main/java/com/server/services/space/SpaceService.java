package com.server.services.space;

import java.util.List;


import com.server.models.entities.Space;
import com.server.repositories.space.dto.DetailSpaceDto;
import com.server.repositories.space.dto.MySpaceDto;

public interface SpaceService {
    List<MySpaceDto> getAllUserSpaces();
    DetailSpaceDto detailSpace(Long id);
    Space createSpace(String name,String description);
    Space getSpaceById(Long id);
    Space updateSpace(Long id, String name);
    Space deleteSpace(Long id);
}