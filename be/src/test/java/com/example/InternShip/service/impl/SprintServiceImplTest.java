package com.example.InternShip.service.impl;

import com.example.InternShip.dto.cloudinary.response.FileResponse;
import com.example.InternShip.dto.sprint.request.CreateSprintRequest;
import com.example.InternShip.dto.sprint.request.EvaluateSprintRequest;
import com.example.InternShip.dto.sprint.request.UpdateSprintRequest;
import com.example.InternShip.dto.sprint.response.GetEvaluateSprintResponse;
import com.example.InternShip.dto.sprint.response.SprintReportResponse;
import com.example.InternShip.dto.sprint.response.SprintResponse;
import com.example.InternShip.entity.*;
import com.example.InternShip.entity.enums.Role;
import com.example.InternShip.repository.InternRepository;
import com.example.InternShip.repository.SprintRepository;
import com.example.InternShip.repository.TeamRepository;
import com.example.InternShip.service.AuthService;
import com.example.InternShip.service.CloudinaryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SprintServiceImplTest {

    @Mock
    private SprintRepository sprintRepository;
    @Mock
    private TeamRepository teamRepository;
    @Mock
    private AuthService authService;
    @Mock
    private InternRepository internRepository;
    @Mock
    private CloudinaryService cloudinaryService;
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private SprintServiceImpl sprintService;

    private User mentorUser;
    private User internUser;
    private Mentor mentor;
    private Intern intern;
    private Team team;
    private Sprint sprint;

    @BeforeEach
    void setUp() {
        mentorUser = new User();
        mentorUser.setId(101);
        mentorUser.setRole(Role.MENTOR);

        mentor = new Mentor();
        mentor.setUser(mentorUser);

        team = new Team();
        team.setId(1);
        team.setMentor(mentor);
        team.setSprints(List.of());

        internUser = new User();
        internUser.setId(102);
        internUser.setRole(Role.INTERN);
        
        intern = new Intern();
        intern.setUser(internUser);
        intern.setTeam(team);

        sprint = new Sprint();
        sprint.setId(1L);
        sprint.setName("Test Sprint");
        sprint.setGoal("Test Goal");
        sprint.setStartDate(LocalDate.now().plusDays(1));
        sprint.setEndDate(LocalDate.now().plusDays(8));
        sprint.setTeam(team);
        sprint.setReportStatus(Sprint.ReportStatus.PENDING);
    }

    @Test
    void testCreateSprint_Success() {
        // Arrange
        CreateSprintRequest request = new CreateSprintRequest();
        request.setName("New Sprint");
        request.setGoal("New Goal");
        request.setStartDate(LocalDate.now().plusDays(10));
        request.setEndDate(LocalDate.now().plusDays(20));

        when(authService.getUserLogin()).thenReturn(mentorUser);
        when(teamRepository.findById(team.getId())).thenReturn(Optional.of(team));
        when(sprintRepository.save(any(Sprint.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // Act
        sprintService.createSprint(team.getId(), request);

        // Assert
        ArgumentCaptor<Sprint> sprintCaptor = ArgumentCaptor.forClass(Sprint.class);
        verify(sprintRepository, times(1)).save(sprintCaptor.capture());
        
        Sprint savedSprint = sprintCaptor.getValue();
        assertEquals(request.getName(), savedSprint.getName());
        assertEquals(request.getGoal(), savedSprint.getGoal());
        assertEquals(team, savedSprint.getTeam());
    }

    @Test
    void testUpdateSprint_Success_BeforeItStarts() {
        // Arrange
        UpdateSprintRequest request = new UpdateSprintRequest();
        request.setName("Updated Sprint Name");
        request.setGoal("Updated Goal");
        request.setEndDate(LocalDate.now().plusDays(15));
        
        when(authService.getUserLogin()).thenReturn(mentorUser);
        when(sprintRepository.findById(sprint.getId())).thenReturn(Optional.of(sprint));
        when(sprintRepository.save(any(Sprint.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        SprintResponse response = sprintService.updateSprint(sprint.getId(), request);

        // Assert
        assertNotNull(response);
        assertEquals(request.getName(), response.getName());
        assertEquals(request.getGoal(), response.getGoal());
        assertEquals(request.getEndDate(), response.getEndDate());
        verify(sprintRepository, times(1)).save(any(Sprint.class));
    }

    @Test
    void testDeleteSprint_Success() {
        // Arrange
        when(authService.getUserLogin()).thenReturn(mentorUser);
        when(sprintRepository.findById(sprint.getId())).thenReturn(Optional.of(sprint));
        
        // Act
        sprintService.deleteSprint(sprint.getId());

        // Assert
        verify(sprintRepository, times(1)).deleteById(sprint.getId());
    }

    @Test
    void testGetSprintsByTeam_Success() {
        // Arrange
        when(authService.getUserLogin()).thenReturn(mentorUser);
        when(teamRepository.findById(team.getId())).thenReturn(Optional.of(team));
        when(sprintRepository.findByTeamId(team.getId())).thenReturn(List.of(sprint));

        // Act
        List<SprintResponse> responses = sprintService.getSprintsByTeam(team.getId());

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(sprint.getName(), responses.get(0).getName());
    }

    @Test
    void testGetSprintById_Success() {
        // Arrange
        when(authService.getUserLogin()).thenReturn(mentorUser);
        when(sprintRepository.findById(sprint.getId())).thenReturn(Optional.of(sprint));
        
        // Act
        SprintResponse response = sprintService.getSprintById(sprint.getId());

        // Assert
        assertNotNull(response);
        assertEquals(sprint.getName(), response.getName());
    }

    @Test
    void testSubmitReport_Success() {
        // Arrange
        MultipartFile mockFile = new MockMultipartFile("file", "report.pdf", "application/pdf", "some-pdf-bytes".getBytes());
        FileResponse fileResponse = new FileResponse();
        fileResponse.setFileUrl("http://cloudinary.com/report.pdf");
        SprintReportResponse reportResponse = new SprintReportResponse();

        when(authService.getUserLogin()).thenReturn(internUser);
        when(internRepository.findByUser(internUser)).thenReturn(Optional.of(intern));
        when(sprintRepository.findById(sprint.getId())).thenReturn(Optional.of(sprint));
        when(cloudinaryService.uploadFile(any(MultipartFile.class), anyString())).thenReturn(fileResponse);
        when(sprintRepository.save(any(Sprint.class))).thenReturn(sprint);
        when(modelMapper.map(sprint, SprintReportResponse.class)).thenReturn(reportResponse);
        
        // Act
        sprintService.submitReport(sprint.getId(), mockFile);

        // Assert
        ArgumentCaptor<Sprint> sprintCaptor = ArgumentCaptor.forClass(Sprint.class);
        verify(sprintRepository, times(1)).save(sprintCaptor.capture());
        
        Sprint savedSprint = sprintCaptor.getValue();
        assertEquals("http://cloudinary.com/report.pdf", savedSprint.getReportUrl());
        assertEquals(Sprint.ReportStatus.SUBMITTED, savedSprint.getReportStatus());
    }

    @Test
    void testEvaluateSprint_Success() {
        // Arrange
        sprint.setReportUrl("http://report.url"); // Prerequisite for evaluation
        EvaluateSprintRequest request = new EvaluateSprintRequest();
        request.setFeedbackGood("Good work");
        request.setFeedbackBad("Could be better");
        request.setFeedbackImprove("Improve on testing");
        
        when(sprintRepository.findById(sprint.getId())).thenReturn(Optional.of(sprint));

        // Act
        sprintService.evaluateSprint(sprint.getId(), request);

        // Assert
        ArgumentCaptor<Sprint> sprintCaptor = ArgumentCaptor.forClass(Sprint.class);
        verify(sprintRepository, times(1)).save(sprintCaptor.capture());

        Sprint evaluatedSprint = sprintCaptor.getValue();
        assertEquals(request.getFeedbackGood(), evaluatedSprint.getFeedbackGood());
        assertEquals(Sprint.ReportStatus.REVIEWED, evaluatedSprint.getReportStatus());
    }
    
    @Test
    void testGetEvaluateSprint_Success() {
        // Arrange
        GetEvaluateSprintResponse evalResponse = new GetEvaluateSprintResponse();
        evalResponse.setFeedbackGood("Good stuff");

        when(sprintRepository.findById(sprint.getId())).thenReturn(Optional.of(sprint));
        when(modelMapper.map(sprint, GetEvaluateSprintResponse.class)).thenReturn(evalResponse);

        // Act
        GetEvaluateSprintResponse response = sprintService.getEvaluateSprint(sprint.getId());

        // Assert
        assertNotNull(response);
        assertEquals(evalResponse.getFeedbackGood(), response.getFeedbackGood());
    }
}
