package com.example.InternShip.dto.attendance.response;

import com.example.InternShip.entity.Attendance;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class GetTeamScheduleResponse {
    private LocalTime timeStart;
    private LocalTime timeEnd;
    private LocalDate date;
    private List<DetailTeamSchedule> detailTeamSchedules;

    @Data
    public static class DetailTeamSchedule {
        private int id;
        private LocalTime checkIn;
        private LocalTime checkOut;
        private Attendance.Status status;
        private String fullName;
        private String email;
    }
}
