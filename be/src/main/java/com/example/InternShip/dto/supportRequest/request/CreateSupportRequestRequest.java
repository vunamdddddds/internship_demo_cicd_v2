package com.example.InternShip.dto.supportRequest.request;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateSupportRequestRequest {
    @NotBlank(message = "SUPPORT_REQUEST_TITLE_NOT_NULL")
    @NotNull(message = "SUPPORT_REQUEST_TITLE_NOT_NULL")
    private String title;

    @NotBlank(message = "SUPPORT_REQUEST_DESCRIPTION_NOT_NULL")
    @NotNull(message = "SUPPORT_REQUEST_DESCRIPTION_NOT_NULL")
    private String description;

    private MultipartFile evidenceFile;
}
