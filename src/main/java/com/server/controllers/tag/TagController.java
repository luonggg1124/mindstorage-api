package com.server.controllers.tag;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.server.controllers.tag.request.CreateTagRequest;
import com.server.controllers.tag.response.CreateTagResponse;
import com.server.repositories.tag.dto.TagByGroupDto;
import com.server.services.tag.TagService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tag")
public class TagController {
    private final TagService tagService;

    @GetMapping("/by-group/{id}")
    public ResponseEntity<List<TagByGroupDto>> getTagsByGroup(
            @PathVariable @Positive(message = "Url không hợp lệ") Long id) {
        List<TagByGroupDto> tags = tagService.getTagsByGroup(id);
        return ResponseEntity.status(HttpStatus.OK).body(tags);
    }

    @PostMapping
    public ResponseEntity<CreateTagResponse> create(
            @Valid @RequestBody CreateTagRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tagService.create(request));
    }
}
