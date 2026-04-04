package com.server.controllers.auth.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

import lombok.Getter;

import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    @NotBlank(message = "Email không được để trống")
    private String email;
    @NotEmpty(message = "Tên đăng nhập không được để trống")
    private String username;
    @NotBlank(message = "Mật khẩu không được để trống")
    private String password;

    @NotBlank(message = "Phiên xác thực không được để trống")
    private String session;

    @NotBlank(message = "Mã xác thực không được để trống")
    private String code;

    @Pattern(
            regexp = "^(MALE|FEMALE|OTHER)$",
            message = "Giới tính phải là MALE, FEMALE hoặc OTHER")
    private String gender;
     
    private String fullName;

    private String hobbies;

    @NotBlank(message = "Mục đích sử dụng không được để trống")
    private String intendedUse;

}
