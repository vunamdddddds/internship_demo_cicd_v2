package com.example.InternShip.dto.user.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ChangeMyPasswordRequest {
    private String oldPassword;
    private String newPassword;
}
