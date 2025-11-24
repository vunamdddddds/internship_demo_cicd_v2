package com.example.InternShip.dto.report.response;

import lombok.Data;

@Data
public class AttendanceSummaryResponse {
    private Integer internId;
    private String fullName;
    private String teamName;

    //Tổng ngày làm (đủ, sớm, muộn)
    private long totalWorkingDays;
    //Tổng ngày nghỉ có phép
    private long totalOnLeaveDays;
    //Tổng ngày nghỉ không phép
    private long totalAbsentDays;
}