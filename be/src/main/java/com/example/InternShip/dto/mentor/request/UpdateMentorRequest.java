package com.example.InternShip.dto.mentor.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateMentorRequest {

    @NotNull(message = "DEPARTMENT_ID_INVALID")
    private Integer departmentId;
}