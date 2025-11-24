package com.example.InternShip.dto.user.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateUserRequest {
    private boolean isActive;
    @NotBlank(message = "ROLE_INVALID")
    private String role;
}



