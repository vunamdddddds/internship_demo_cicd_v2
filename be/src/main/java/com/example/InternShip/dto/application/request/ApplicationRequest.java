package com.example.InternShip.dto.application.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ApplicationRequest {
    @NotNull(message = "INTERNSHIP_TERM_INVALID")
    private Integer internshipTermId;

    @NotNull(message = "UNIVERSITY_NOTNULL")
    private Integer universityId;

    @NotNull(message = "MAJOR_NOTNULL")
    private Integer majorId;

    @NotNull(message = "FILE_NOTNULL")
    private MultipartFile cvFile;

    @NotNull(message = "FILE_NOTNULL")
    private MultipartFile internApplicationFile;
}
