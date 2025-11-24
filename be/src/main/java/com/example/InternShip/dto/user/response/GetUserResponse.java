package com.example.InternShip.dto.user.response;

import com.example.InternShip.entity.enums.Role;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GetUserResponse {
    private int id;
    private String fullName;
    private String username;
    private String email;
    private String phone;
    private String address;
    private String avatarUrl; // Added this field
    private boolean isActive;
    private Role role;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime updatedAt;
}