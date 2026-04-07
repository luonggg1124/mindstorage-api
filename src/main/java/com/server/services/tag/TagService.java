package com.server.services.tag;

import java.util.List;

import com.server.controllers.tag.request.CreateTagRequest;
import com.server.controllers.tag.response.CreateTagResponse;
import com.server.repositories.tag.dto.TagByGroupDto;

public interface TagService {
    List<TagByGroupDto> getTagsByGroup(Long groupId);
    CreateTagResponse create(CreateTagRequest request);
}

