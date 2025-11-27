package com.example.InternShip.service.impl;

import com.example.InternShip.dto.attendance.response.GetMyScheduleResponse;
import com.example.InternShip.dto.attendance.response.GetTeamScheduleResponse;
import com.example.InternShip.entity.*;
import com.example.InternShip.repository.*;
import com.example.InternShip.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AttendanceServiceImplTest {

    @Mock
    private AttendanceRepository attendanceRepository;
    @Mock
    private InternRepository internRepository;
    @Mock
    private WorkScheduleRepository workScheduleRepository;
    @Mock
    private LeaveRequestRepository leaveRequestRepository;
    @Mock
    private TeamRepository teamRepository;
    @Mock
    private AuthService authService;
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private AttendanceServiceImpl attendanceService;

    private User user;
    private Intern intern;
    private Team team;
    private WorkSchedule workSchedule;
    private Attendance attendance;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);

        team = new Team();
        team.setId(1);
        team.setName("Test Team");
        team.setWorkSchedules(Collections.emptyList());

        intern = new Intern();
        intern.setId(1);
        intern.setUser(user);
        intern.setTeam(team);

        user.setIntern(intern);

        workSchedule = new WorkSchedule();
        workSchedule.setDayOfWeek(LocalDate.now().getDayOfWeek());
        workSchedule.setTimeStart(LocalTime.MIN);
        workSchedule.setTimeEnd(LocalTime.MAX);

        attendance = new Attendance();
        attendance.setId(1);
        attendance.setIntern(intern);
        attendance.setTeam(team);
        attendance.setDate(LocalDate.now());
        attendance.setCheckIn(LocalTime.of(9, 0));
        attendance.setTimeStart(LocalTime.of(9, 0));
        attendance.setTimeEnd(LocalTime.of(17, 0));

    }


    @Test
    void checkOut_happyPath() {
        attendance.setCheckIn(LocalTime.now().minusHours(1));
        when(authService.getUserLogin()).thenReturn(user);
        when(internRepository.findByUser(user)).thenReturn(Optional.of(intern));
        when(attendanceRepository.findByInternAndDate(intern, LocalDate.now())).thenReturn(Optional.of(attendance));
        when(leaveRequestRepository.findByInternAndDateAndApproved(intern, LocalDate.now(), true)).thenReturn(Optional.empty());
        when(attendanceRepository.save(any(Attendance.class))).thenReturn(attendance);
        when(modelMapper.map(any(Attendance.class), any())).thenReturn(new GetMyScheduleResponse());


        GetMyScheduleResponse response = attendanceService.checkOut();

        assertNotNull(response);
    }

    @Test
    void getMySchedule_happyPath() {
        intern.setAttendances(Collections.singletonList(attendance));
        InternshipProgram ip = new InternshipProgram();
        ip.setStatus(InternshipProgram.Status.ONGOING);
        intern.setInternshipProgram(ip);
        intern.setStatus(Intern.Status.ACTIVE);
        when(authService.getUserLogin()).thenReturn(user);
        when(modelMapper.map(any(Attendance.class), any())).thenReturn(new GetMyScheduleResponse());

        List<GetMyScheduleResponse> response = attendanceService.getMySchedule();

        assertNotNull(response);
    }

    @Test
    void getTeamSchedule_happyPath() {
        team.setInternshipProgram(new InternshipProgram());
        team.getInternshipProgram().setStatus(InternshipProgram.Status.ONGOING);
        when(teamRepository.findById(1)).thenReturn(Optional.of(team));
        when(attendanceRepository.findAllByTeamId(1)).thenReturn(Collections.singletonList(attendance));
        when(modelMapper.map(any(Attendance.class), any())).thenReturn(new GetTeamScheduleResponse.DetailTeamSchedule());


        List<GetTeamScheduleResponse> response = attendanceService.getTeamSchedule(1);

        assertNotNull(response);
    }
}
