package com.example.InternShip.dto.report.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.util.List;

@Data
public class InternAttendanceDetailResponse {

    private Integer internId;
    private String fullName;
    private String email;
    private String teamName;

    private List<DailyLogEntry> dailyLogs;

    private List<LeaveLogEntry> leaveLogs;

    @Data
    public static class LeaveLogEntry {
        @JsonFormat(pattern="dd-MM-yyyy")
        private java.time.LocalDate date;
        private String type;
        private String reason;
        private String leaveStatus;
        private String approverName;
    }

    @Data
    public static class DailyLogEntry {
        @JsonFormat(pattern="dd-MM-yyyy")
        private java.time.LocalDate date;
        @JsonFormat(pattern="HH:mm:ss")
        private java.time.LocalTime expectedTimeStart;
        @JsonFormat(pattern="HH:mm:ss")
        private java.time.LocalTime expectedTimeEnd;
        @JsonFormat(pattern="HH:mm:ss")
        private java.time.LocalTime actualCheckIn;
        @JsonFormat(pattern="HH:mm:ss")
        private java.time.LocalTime actualCheckOut;
        private String status;
    }
}