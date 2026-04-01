package com.server.repositories.space;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.server.models.entities.Space;

public interface SpaceRepository extends JpaRepository<Space, Long> {
    public List<Space> findByOwnerId(Long ownerId);
}
