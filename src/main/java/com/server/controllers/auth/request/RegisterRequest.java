package com.server.controllers.auth.request;
import java.util.List;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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

    private String fullName;

    private List<String> hobbies;

}
