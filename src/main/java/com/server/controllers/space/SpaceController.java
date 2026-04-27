package com.server.controllers.space;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.server.controllers.space.request.CreateSpaceRequest;
import com.server.controllers.space.response.CreateSpaceResponse;
import com.server.models.entities.Space;
import com.server.services.others.data.dto.PageResponse;
import com.server.services.space.dto.DetailSpaceDto;
import com.server.services.space.dto.MySpaceDto;
import com.server.services.space.dto.SpaceMemberUserDto;
import com.server.services.space.SpaceService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/space")
public class SpaceController {
    private final SpaceService spaceService;

    @GetMapping("/my-spaces")
    public ResponseEntity<PageResponse<MySpaceDto>> mySpaces(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return ResponseEntity.ok(spaceService.mySpaces(q, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetailSpaceDto> detail(
            @PathVariable UUID id) {
        DetailSpaceDto space = spaceService.detail(id);
        return ResponseEntity.ok(space);
    }

    // CREATE
    @PostMapping
    public ResponseEntity<CreateSpaceResponse> create(
            @Valid @RequestBody CreateSpaceRequest request) {

        Space space = spaceService.create(request.getName(), request.getDescription());

        CreateSpaceResponse response = new CreateSpaceResponse(space.getId(), space.getName(),
                space.getDescription(), space.getCreatedAt().toString(), space.getUpdatedAt().toString());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<CreateSpaceResponse> update(
            @PathVariable UUID id,
            @RequestBody CreateSpaceRequest request) {

        Space space = spaceService.update(id, request.getName(), request.getDescription());

        CreateSpaceResponse response = new CreateSpaceResponse(space.getId(), space.getName(),
        space.getDescription(),
                space.getCreatedAt().toString(), space.getUpdatedAt().toString());

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        spaceService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/{id}/members")
    public ResponseEntity<PageResponse<SpaceMemberUserDto>> members(
            @PathVariable UUID id,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return ResponseEntity.ok(spaceService.members(id, q, page, size));
    }

}
