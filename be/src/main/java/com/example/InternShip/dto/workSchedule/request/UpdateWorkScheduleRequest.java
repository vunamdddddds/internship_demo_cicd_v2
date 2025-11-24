package com.example.InternShip.dto.workSchedule.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalTime;

@Data
public class UpdateWorkScheduleRequest {
    @NotNull(message = "TIME_START_INVALID")
    private LocalTime timeStart;

    @NotNull(message = "TIME_END_INVALID")
    private LocalTime timeEnd;
}
