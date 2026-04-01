package com.server.services.space;

import org.springframework.stereotype.Service;

import com.server.repositories.space.SpaceRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class SpaceServiceImplement implements SpaceService {
    private final SpaceRepository spaceRepository;
}
