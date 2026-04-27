package com.server.repositories.group;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.server.models.entities.GroupMember;

public interface GroupMemberRepository extends JpaRepository<GroupMember, UUID> {
    boolean existsByUserIdAndGroupId(Long userId, UUID groupId);

    @Query("""
            select gm.user.id
            from com.server.models.entities.GroupMember gm
            where gm.group.id = :groupId
            """)
    List<Long> findMemberUserIdsByGroupId(@Param("groupId") UUID groupId);
}
