package com.server.controllers.auth.request;



import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    @NotBlank(message = "Email is required")
    private String email;
    @NotEmpty(message = "Username is required")
    private String username;
    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Session is required")
    private String session;

    @NotBlank(message = "Code is required")
    private String code;

    private String fullName;

    private String hobbies;

    private MultipartFile avatar_file;

    @Pattern(
        regexp = "^\\d{4}-\\d{2}-\\d{2}$",
        message = "Date of Birth must be in the format yyyy-MM-dd"
    )
    private String dob;


    
}
