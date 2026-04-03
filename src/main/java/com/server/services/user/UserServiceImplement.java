package com.server.services.user;

import org.springframework.stereotype.Service;

import com.server.repositories.user.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserServiceImplement implements UserService {
    private final UserRepository userRepository;

    @Override
    public boolean existsByUsername(String username){
        return userRepository.existsByUsername(username);
    }
}
