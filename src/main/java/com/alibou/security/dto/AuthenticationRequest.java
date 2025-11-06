package com.alibou.security.dto;

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
public class AuthenticationRequest {
    @NotBlank
    @Email(message = "Invalid email format")
    private String email;
    @NotBlank
    @Size(min = 8, message = "password must have at least 8 characters")
    private String password;
}
