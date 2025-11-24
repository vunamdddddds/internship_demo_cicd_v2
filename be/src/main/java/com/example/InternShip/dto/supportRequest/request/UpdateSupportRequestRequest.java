package com.example.InternShip.dto.supportRequest.request;

import org.springframework.web.multipart.MultipartFile;
import lombok.Data;

@Data
public class UpdateSupportRequestRequest {
    private String title;
    private String description;
    private MultipartFile evidenceFile;
}