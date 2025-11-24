package com.example.InternShip.dto.report.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class FinalAttendanceResponse {

    @JsonFormat(pattern = "dd-MM-yyyy")
    private java.time.LocalDate date;
    private String reason;
    private String status;

    private long totalWorkingDays;
    private long totalOnLeaveDays;
    private long totalAbsentDays;
}
