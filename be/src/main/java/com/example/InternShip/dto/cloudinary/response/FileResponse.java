package com.example.InternShip.dto.cloudinary.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileResponse {
    private String fileName;
    private String fileUrl;
    private String publicId;
    private Long fileSize;
    private String fileType;
    private String message;
    private LocalDateTime uploadDate;
}
