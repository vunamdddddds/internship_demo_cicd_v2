package com.example.InternShip.dto.internshipProgram.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CreateInternProgramRequest {
    @NotBlank(message = "NAME_INTERN_PROGRAM_INVALID")
    private String name;
    @NotNull(message = "TIME_INVALID")
    private LocalDateTime endPublishedTime;
    @NotNull(message = "TIME_INVALID")
    private LocalDateTime endReviewingTime;
    @NotNull(message = "TIME_INVALID")
    private LocalDateTime timeStart;
    @NotNull(message = "DEPARTMENT_INVALID")
    private Integer departmentId;
    private boolean isDraft;
}
