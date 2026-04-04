package com.server.repositories.group;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.server.models.entities.Group;

import java.util.List;

public interface GroupRepository extends JpaRepository<Group, Long> {
    boolean existsById(Long id);
    List<Group> getAllBySpace_Id(Long spaceId, Pageable pageable);
}
