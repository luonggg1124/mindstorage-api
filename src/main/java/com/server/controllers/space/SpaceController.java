package com.server.controllers.space;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.server.services.space.SpaceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/space")
public class SpaceController {
    private final SpaceService spaceService;
}
