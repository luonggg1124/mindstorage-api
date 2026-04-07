package com.server.controllers.group;

import com.server.controllers.group.request.CreateGroupRequest;

import com.server.controllers.group.request.UpdateGroupRequest;
import com.server.controllers.group.response.CreateGroupResponse;
import com.server.controllers.group.response.UpdateGroupResponse;
import com.server.models.entities.Group;
import com.server.repositories.group.dto.DetailGroupDto;
import com.server.repositories.group.dto.GroupBySpaceDto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.server.services.group.GroupService;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/group")
public class GroupController {
    private final GroupService groupService;

    @GetMapping("/by-space/{id}")
    public ResponseEntity<List<GroupBySpaceDto>> getGroupsBySpace(
            @PathVariable @Positive(message = "Url không hợp lệ") Long id) {
        List<GroupBySpaceDto> groups = groupService.getGroupsBySpace(id);
        return ResponseEntity.status(HttpStatus.OK).body(groups);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetailGroupDto> detailGroup(@PathVariable @Positive(message = "Url không hợp lệ") Long id) {
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
    public ResponseEntity<UpdateGroupResponse> update(@PathVariable Long id, @RequestBody UpdateGroupRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(groupService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        groupService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
