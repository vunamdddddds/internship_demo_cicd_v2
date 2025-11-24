package com.example.InternShip.service;

import com.example.InternShip.dto.attendance.response.GetAllAttendanceResponse;
import com.example.InternShip.dto.attendance.response.GetMyScheduleResponse;
import com.example.InternShip.dto.attendance.response.GetTeamScheduleResponse;

import java.util.List;

public interface AttendanceService {
    GetMyScheduleResponse checkIn();

    GetMyScheduleResponse checkOut();

    List<GetMyScheduleResponse> getMySchedule();

    List<GetTeamScheduleResponse> getTeamSchedule(int teamId);
}
