package com.example.InternShip.dto.attendance.response;
import com.example.InternShip.entity.Attendance;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class GetMyScheduleResponse {
    private LocalTime timeStart;
    private LocalTime timeEnd;
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime checkIn;
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime checkOut;
    private LocalDate date;
    private Attendance.Status status;
    private String team;
}
