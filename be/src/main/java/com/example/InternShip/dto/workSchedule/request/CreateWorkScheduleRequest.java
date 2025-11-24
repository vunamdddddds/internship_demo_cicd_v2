package com.example.InternShip.dto.workSchedule.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
public class CreateWorkScheduleRequest {
    @NotNull(message = "TEAM_NOT_NULL")
    private Integer idTeam;

    @NotNull(message = "DAY_OF_WEEK_NOT_NULL")
    private DayOfWeek dayOfWeek;

    @NotNull(message = "TIME_START_INVALID")
    private LocalTime timeStart;

    @NotNull(message = "TIME_END_INVALID")
    private LocalTime timeEnd;
}
