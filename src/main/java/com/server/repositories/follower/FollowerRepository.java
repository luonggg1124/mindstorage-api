package com.server.repositories.follower;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.server.models.entities.Follow;
import com.server.models.entities.User;

public interface FollowerRepository extends JpaRepository<Follow, UUID> {
    @Query("""
        select f.follower
        from com.server.models.entities.Follow f
        where f.following.id = :userId
    """)
    public List<User> followersByUserId(@Param("userId") Long userId);

    @Query("""
        select f.following
        from com.server.models.entities.Follow f
        where f.follower.id = :userId
    """)
    public List<User> followingByUserId(@Param("userId") Long userId);


    public long countByFollowing_Id(Long userId);

    public long countByFollower_Id(Long userId);

}
