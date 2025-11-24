package com.example.InternShip.dto.attendance.response;

public interface GetAllAttendanceResponse {
    Integer getInternId();
    String getInternName();
    String getTeamName();
    Integer getPresentDay();
    Integer getAbsentDay();
    Integer getLateAndLeaveDay();
    Integer getInternshipProgramId();
}
