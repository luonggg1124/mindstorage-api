package com.server.controllers.group;

import com.server.controllers.group.request.CreateGroupRequest;
import com.server.controllers.group.request.UpdateGroupRequest;
import com.server.controllers.group.response.CreateGroupResponse;
import com.server.controllers.group.response.GroupsBySpaceResponse;
import com.server.controllers.group.response.UpdateGroupResponse;
import jakarta.validation.Valid;
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
    public ResponseEntity<List<GroupsBySpaceResponse>> getBySpace(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        List<GroupsBySpaceResponse> groups = groupService.getAllBySpace(id, page, size);
        return ResponseEntity.status(HttpStatus.OK).body(groups);
    }

    @PostMapping
    public ResponseEntity<CreateGroupResponse> create(
            @Valid @RequestBody CreateGroupRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(groupService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UpdateGroupResponse> update(@PathVariable Long id, @RequestBody UpdateGroupRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(groupService.update(id,request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        groupService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
