package com.example.InternShip.dto.user.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UpdateInfoRequest {
    @NotBlank(message = "FULL_NAME_INVALID")
    private String fullName;
    private String phone;
    private String address;
    private MultipartFile avatarFile;
}
