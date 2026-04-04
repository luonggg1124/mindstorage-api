package com.server.services.user;

import org.springframework.stereotype.Service;

import com.server.exceptions.BadRequestException;
import com.server.repositories.user.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserServiceImplement implements UserService {
    private final UserRepository userRepository;

    @Override
    public boolean validateUsernamePassword(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            throw new BadRequestException("Tên đăng nhập không được để trống", "username");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new BadRequestException("Mật khẩu không được để trống", "password");
        }
        if (password.length() < 8) {
            throw new BadRequestException("Mật khẩu phải có ít nhất 8 ký tự", "password");
        }
        if (userRepository.existsByUsername(username)) {
            throw new BadRequestException("Tên đăng nhập đã tồn tại", "username");
        }
        return true;
    }
}
