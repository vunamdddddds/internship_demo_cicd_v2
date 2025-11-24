package com.example.InternShip.dto.task.response;

import com.example.InternShip.entity.enums.TaskStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TaskResponse {
    private Long id;
    private String name;
    private String description;
    private TaskStatus status;
    private LocalDate deadline;
    private Long sprint_Id;
    private Integer assignee_Id;
    private String assigneeName;
    private Integer mentorId;
    private String mentorName;
    private Integer createdById;
    private String createdByName;
}
