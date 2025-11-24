package com.example.InternShip.service;

import com.example.InternShip.dto.workSchedule.request.CreateWorkScheduleRequest;
import com.example.InternShip.dto.workSchedule.request.UpdateWorkScheduleRequest;
import com.example.InternShip.dto.workSchedule.response.WorkScheduleResponse;

import java.util.List;

public interface WorkScheduleService {
    List<WorkScheduleResponse> getWorkSchedule(Integer teamId);
    WorkScheduleResponse updateSchedule(Integer id, UpdateWorkScheduleRequest request);
    WorkScheduleResponse createSchedule(CreateWorkScheduleRequest request);
    void deleteSchedule (int id);
}