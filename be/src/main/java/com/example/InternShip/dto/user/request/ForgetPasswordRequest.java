package com.example.InternShip.dto.user.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ForgetPasswordRequest {
    @Email
    @NotBlank(message = "EMAIL_INVALID")
    private String email;
    @NotBlank(message = "PASSWORD_INVALID")
    @Size(min = 8, max = 30, message = "PASSWORD_INVALID")
    private String password;
}
