package com.alibou.security.dto;

import com.alibou.security.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    @NotBlank
    @Size(min = 3, message = "first name must have at least 3 characters")
    private String firstName;
    @NotBlank
    private String lastName;
    @NotBlank
    @Email(message = "Invalid email format")
    private String email;
    @NotBlank
    private String password;
    private Role role;
}
