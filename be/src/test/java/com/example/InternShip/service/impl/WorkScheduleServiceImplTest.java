package com.example.InternShip.service.impl;

import com.example.InternShip.dto.workSchedule.request.CreateWorkScheduleRequest;
import com.example.InternShip.dto.workSchedule.request.UpdateWorkScheduleRequest;
import com.example.InternShip.dto.workSchedule.response.WorkScheduleResponse;
import com.example.InternShip.entity.Team;
import com.example.InternShip.entity.WorkSchedule;
import com.example.InternShip.repository.TeamRepository;
import com.example.InternShip.repository.WorkScheduleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkScheduleServiceImplTest {

    @Mock
    private WorkScheduleRepository workScheduleRepository;
    @Mock
    private TeamRepository teamRepository;
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private WorkScheduleServiceImpl workScheduleService;

    private Team team;
    private WorkSchedule workSchedule;
    private WorkScheduleResponse workScheduleResponse;

    @BeforeEach
    void setUp() {
        team = new Team();
        team.setId(1);


        workSchedule = new WorkSchedule();
        workSchedule.setId(1);
        workSchedule.setTeam(team);
        workSchedule.setDayOfWeek(DayOfWeek.MONDAY);
        workSchedule.setTimeStart(LocalTime.of(9, 0));
        workSchedule.setTimeEnd(LocalTime.of(17, 0));

        team.setWorkSchedules(Collections.singletonList(workSchedule));

        workScheduleResponse = new WorkScheduleResponse();
        workScheduleResponse.setId(1);
    }

    @Test
    void getWorkSchedule_happyPath() {
        when(teamRepository.findById(1)).thenReturn(Optional.of(team));
        when(modelMapper.map(any(WorkSchedule.class), any())).thenReturn(workScheduleResponse);

        List<WorkScheduleResponse> response = workScheduleService.getWorkSchedule(1);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void updateSchedule_happyPath() {
        UpdateWorkScheduleRequest request = new UpdateWorkScheduleRequest();
        request.setTimeStart(LocalTime.of(10, 0));
        request.setTimeEnd(LocalTime.of(18, 0));

        when(workScheduleRepository.findById(1)).thenReturn(Optional.of(workSchedule));
        when(workScheduleRepository.save(any(WorkSchedule.class))).thenReturn(workSchedule);
        when(modelMapper.map(any(WorkSchedule.class), any())).thenReturn(workScheduleResponse);


        WorkScheduleResponse response = workScheduleService.updateSchedule(1, request);

        assertNotNull(response);
        assertEquals(1, response.getId());
    }

    @Test
    void createSchedule_happyPath() {
        CreateWorkScheduleRequest request = new CreateWorkScheduleRequest();
        request.setIdTeam(1);

        when(teamRepository.findById(1)).thenReturn(Optional.of(team));
        when(modelMapper.map(request, WorkSchedule.class)).thenReturn(workSchedule);
        when(workScheduleRepository.save(any(WorkSchedule.class))).thenReturn(workSchedule);
        when(modelMapper.map(any(WorkSchedule.class), any())).thenReturn(workScheduleResponse);


        WorkScheduleResponse response = workScheduleService.createSchedule(request);

        assertNotNull(response);
        assertEquals(1, response.getId());
    }

    @Test
    void deleteSchedule_happyPath() {
        assertDoesNotThrow(() -> workScheduleService.deleteSchedule(1));
    }
}
