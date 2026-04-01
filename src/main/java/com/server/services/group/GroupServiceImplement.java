package com.server.services.group;

import org.springframework.stereotype.Service;

import com.server.repositories.group.GroupRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class GroupServiceImplement implements GroupService {
    private final GroupRepository groupRepository;
}
