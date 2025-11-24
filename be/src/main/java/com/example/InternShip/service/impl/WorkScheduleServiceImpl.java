package com.example.InternShip.service.impl;

import com.example.InternShip.dto.workSchedule.request.CreateWorkScheduleRequest;
import com.example.InternShip.dto.workSchedule.request.UpdateWorkScheduleRequest;
import com.example.InternShip.dto.workSchedule.response.WorkScheduleResponse;
import com.example.InternShip.entity.Team;
import com.example.InternShip.entity.WorkSchedule;
import com.example.InternShip.exception.ErrorCode;
import com.example.InternShip.repository.TeamRepository;
import com.example.InternShip.repository.WorkScheduleRepository;
import com.example.InternShip.service.WorkScheduleService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkScheduleServiceImpl implements WorkScheduleService {
    private final WorkScheduleRepository workScheduleRepository;
    private final TeamRepository teamRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<WorkScheduleResponse> getWorkSchedule(Integer teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.TEAM_NOT_EXISTED.getMessage()));

        return team.getWorkSchedules().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public WorkScheduleResponse updateSchedule(Integer id, UpdateWorkScheduleRequest request) {
        WorkSchedule workSchedule = workScheduleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.WORK_SCHEDULE_NOT_EXISTED.getMessage()));

        workSchedule.setTimeStart(request.getTimeStart());
        workSchedule.setTimeEnd(request.getTimeEnd());
        workScheduleRepository.save(workSchedule);

        return mapToResponse(workSchedule);
    }

    public WorkScheduleResponse createSchedule(CreateWorkScheduleRequest request){
        Team team = teamRepository.findById(request.getIdTeam())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.TEAM_NOT_EXISTED.getMessage()));

        WorkSchedule workSchedule = modelMapper.map(request, WorkSchedule.class);
        workSchedule.setTeam(team);

        try {
            workScheduleRepository.save(workSchedule);
        } catch (DataIntegrityViolationException ex) {
            throw new RuntimeException(ErrorCode.SCHEDULER_FAILED.getMessage());
        }

        return mapToResponse(workSchedule);
    }

    public void deleteSchedule (int id){
        workScheduleRepository.deleteById(id);
    }

    private WorkScheduleResponse mapToResponse(WorkSchedule workSchedule) {
        return modelMapper.map(workSchedule, WorkScheduleResponse.class);
    }

}