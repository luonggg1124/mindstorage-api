package com.server.repositories.user;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.server.models.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    Optional<User> findByEmailOrUsername(String email, String username);

    @Query(value = """
            select *
            from users
            where (
                coalesce(:q, '') = ''
                or username ilike concat('%', :q, '%')
                or full_name ilike concat('%', :q, '%')
                or email ilike concat('%', :q, '%')
            )
            """,
            countQuery = """
            select count(*)
            from users
            where (
                coalesce(:q, '') = ''
                or username ilike concat('%', :q, '%')
                or full_name ilike concat('%', :q, '%')
                or email ilike concat('%', :q, '%')
            )
            """,
            nativeQuery = true)
    Page<User> searchUsers(@Param("q") String q, Pageable pageable);
}

