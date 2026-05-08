package com.server.controllers.group;

import com.server.controllers.group.request.CreateGroupRequest;
import com.server.controllers.group.response.CreateGroupResponse;
import com.server.models.entities.Group;
import com.server.services.group.dto.DetailGroupDto;
import com.server.services.group.dto.GroupBySpaceDto;
import com.server.services.others.data.dto.PageResponse;

import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.server.services.group.GroupService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/group")
public class GroupController {
    private final GroupService groupService;

    @GetMapping("/by-space/{id}")
    public ResponseEntity<PageResponse<GroupBySpaceDto>> getGroupsBySpace(
            @PathVariable UUID id,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return ResponseEntity.status(HttpStatus.OK).body(groupService.getGroupsBySpace(id, q, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetailGroupDto> detailGroup(@PathVariable UUID id) {
        DetailGroupDto group = groupService.detailGroup(id);
        return ResponseEntity.status(HttpStatus.OK).body(group);
    }

    @PostMapping
    public ResponseEntity<CreateGroupResponse> create(
            @Valid @RequestBody CreateGroupRequest request) {
        Group group = groupService.create(request.getName(), request.getDescription(), request.getSpaceId());
        return ResponseEntity.status(HttpStatus.CREATED).body(new CreateGroupResponse(group.getId(), group.getName(),
                group.getDescription(), group.getCreatedAt(), group.getUpdatedAt()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CreateGroupResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody CreateGroupRequest request) {
        Group group = groupService.update(id, request.getName(), request.getDescription(), request.getSpaceId());
        return ResponseEntity.status(HttpStatus.OK).body(new CreateGroupResponse(
                group.getId(),
                group.getName(),
                group.getDescription(),
                group.getCreatedAt(),
                group.getUpdatedAt()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        groupService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
