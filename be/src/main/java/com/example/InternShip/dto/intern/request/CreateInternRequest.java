package com.example.InternShip.dto.intern.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CreateInternRequest {

    @NotBlank(message = "FULL_NAME_INVALID")
    private String fullName;

    @NotBlank(message = "EMAIL_INVALID")
    @Email(message = "EMAIL_INVALID")
    private String email;

    @Pattern(regexp = "^$|0\\d{9}", message = "PHONE_INVALID")
    private String phone;

    private String address;

    @NotNull(message = "MAJOR_INVALID")
    private Integer majorId;

    @NotNull(message = "UNIVERSITY_INVALID")
    private Integer universityId;

    @NotNull(message = "INTERNSHIP_PROGRAM_INVALID")
    private Integer internshipProgramId;
}
