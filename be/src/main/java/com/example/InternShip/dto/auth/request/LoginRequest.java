package com.example.InternShip.dto.auth.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "USERNAME_INVALID")
    private String identifier;
    @NotBlank(message = "PASSWORD_INVALID")
    @Size(min = 8, max = 30, message = "PASSWORD_INVALID")
    private String password;
}
