package com.server.services.space;



import com.server.models.entities.Space;
import com.server.services.others.data.dto.PageResponse;
import com.server.services.space.dto.DetailSpaceDto;
import com.server.services.space.dto.MySpaceDto;

public interface SpaceService {
    PageResponse<MySpaceDto> mySpaces(String q, Integer page, Integer size);
    DetailSpaceDto detail(Long id);
    Space create(String name,String description);
    Space getById(Long id);
    Space update(Long id, String name, String description);
    void delete(Long id);
}