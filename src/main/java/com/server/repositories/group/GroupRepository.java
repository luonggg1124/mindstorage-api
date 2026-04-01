package com.server.repositories.group;

import org.springframework.data.jpa.repository.JpaRepository;

import com.server.models.entities.Group;

public interface GroupRepository extends JpaRepository<Group, Long> {
    
}
