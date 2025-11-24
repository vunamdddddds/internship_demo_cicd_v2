package com.example.InternShip.dto.user.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CreateUserRequest {
    @NotBlank(message = "FULL_NAME_INVALID")
    private String fullName;
    @NotBlank(message = "EMAIL_INVALID")
    @Email(message = "EMAIL_INVALID")
    private String email;
    @Pattern(regexp = "^$|0\\d{9}", message = "PHONE_INVALID")
    private String phone;
    private String address;
    @NotBlank(message = "ROLE_INVALID")
    private String role;
}
