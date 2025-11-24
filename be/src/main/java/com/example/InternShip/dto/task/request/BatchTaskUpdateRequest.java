package com.example.InternShip.dto.task.request;

import lombok.Data;
import java.util.List;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Data
public class BatchTaskUpdateRequest {
    @NotNull
    private String action; // "MOVE_TO_SPRINT", "MOVE_TO_BACKLOG", "CANCEL"

    @NotEmpty
    private List<Long> taskIds;
    
    private Long targetSprintId; // Only required for "MOVE_TO_SPRINT" action
}
