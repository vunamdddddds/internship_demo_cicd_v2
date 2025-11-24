package com.example.InternShip.dto.task.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateTaskRequest {
    @NotBlank(message = "Task name is mandatory.")
    private String name;

    @NotBlank(message = "Task description is mandatory.")
    private String description;

    private Long sprintId;
    private Integer assigneeId;
    private LocalDate deadline;
}
