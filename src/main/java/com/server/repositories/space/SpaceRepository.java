package com.server.repositories.space;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.server.models.entities.Space;

public interface SpaceRepository extends JpaRepository<Space, Long> {
    List<Space> findByCreator_Id(Long creatorId);
}
