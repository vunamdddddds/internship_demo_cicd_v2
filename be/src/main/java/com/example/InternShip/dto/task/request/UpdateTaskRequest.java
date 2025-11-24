package com.example.InternShip.dto.task.request;

import com.example.InternShip.entity.enums.TaskStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateTaskRequest {
    private String name;
    private String description;
    private TaskStatus status;
    private LocalDate deadline;
    private Integer assigneeId;
}
