package com.example.InternShip.dto.auth.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterRequest {
    @NotBlank(message = "USERNAME_INVALID")
    @Size(max = 50, message = "USERNAME_INVALID")
    private String username;

    @Email(message = "EMAIL_INVALID")
    @NotBlank(message = "EMAIL_INVALID")
    private String email;

    @NotBlank(message = "PASSWORD_INVALID")
    @Size(min = 8,max = 20, message = "PASSWORD_INVALID")
    private String password;

    @NotBlank(message = "FULL_NAME_INVALID")
    private String fullName;
}
