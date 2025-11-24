package com.example.InternShip.service;

import java.util.List;

import com.example.InternShip.dto.sprint.request.CreateSprintRequest;
import com.example.InternShip.dto.sprint.request.EvaluateSprintRequest;
import com.example.InternShip.dto.sprint.request.UpdateSprintRequest;
import com.example.InternShip.dto.sprint.response.SprintReportResponse;
import com.example.InternShip.dto.sprint.response.SprintResponse;

import org.springframework.web.multipart.MultipartFile;

public interface SprintService {
    SprintResponse createSprint(Integer teamId, CreateSprintRequest request);

    SprintResponse updateSprint(Long sprintId, UpdateSprintRequest request);

    void deleteSprint(Long sprintId);

    List<SprintResponse> getSprintsByTeam(Integer teamId);

    SprintResponse getSprintById(Long sprintId);

    SprintReportResponse submitReport(Long sprintId, MultipartFile file);

    void evaluateSprint(Long sprintId, EvaluateSprintRequest request);

    Object getEvaluateSprint(Long sprintId);
}
