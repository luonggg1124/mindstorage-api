package com.server.controllers.space;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.server.controllers.space.request.SpaceRequest;
import com.server.controllers.space.response.SpaceResponse;
import com.server.models.entities.Space;
import com.server.services.space.SpaceService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/space")
public class SpaceController {
    private final SpaceService spaceService;

    @GetMapping("/my-spaces")
    public ResponseEntity<List<Space>> getAllSpaces() {
        List<Space> spaces = spaceService.getAllUserSpaces();
        return ResponseEntity.ok(spaces);
    }
    
     // CREATE
    @PostMapping
    public ResponseEntity<SpaceResponse> create(
            @Valid @RequestBody SpaceRequest request
    ) {

        Space space = spaceService.createSpace(request.getName());

        SpaceResponse response = SpaceResponse.builder()
                .id(space.getId())
                .name(space.getName())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<SpaceResponse> update(
            @PathVariable Long id,
            @RequestBody SpaceRequest request
    ) {

        Space space = spaceService.updateSpace(id, request.getName());

        SpaceResponse response = SpaceResponse.builder()
                .id(space.getId())
                .name(space.getName())
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

     // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {

        spaceService.deleteSpace(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
