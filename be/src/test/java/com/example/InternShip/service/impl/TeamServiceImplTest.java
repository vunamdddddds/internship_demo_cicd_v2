package com.example.InternShip.service.impl;

import com.example.InternShip.dto.team.request.CreateTeamRequest;
import com.example.InternShip.dto.team.request.UpdateTeamRequest;
import com.example.InternShip.dto.team.request.AddMemberRequest;
import com.example.InternShip.dto.team.response.TeamDetailResponse;
import com.example.InternShip.dto.response.PagedResponse;
import com.example.InternShip.dto.team.response.GetAllTeamResponse;
import com.example.InternShip.entity.*;
import com.example.InternShip.entity.enums.Role;
import com.example.InternShip.repository.*;
import com.example.InternShip.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeamServiceImplTest {

    @Mock
    private TeamRepository teamRepository;
    @Mock
    private InternRepository internRepository;
    @Mock
    private InternshipProgramRepository programRepository;
    @Mock
    private MentorRepository mentorRepository;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private AuthService authService;

    @InjectMocks
    private TeamServiceImpl teamService;

    private InternshipProgram program;
    private Mentor mentor;
    private User mentorUser;
    private Team team;
    private Intern intern;
    private Major major;
    private University university;


    @BeforeEach
    void setUp() {
        program = new InternshipProgram();
        program.setId(1);
        program.setName("Spring Internship 2025");
        program.setStatus(InternshipProgram.Status.ONGOING);

        mentorUser = new User();
        mentorUser.setId(101);
        mentorUser.setFullName("Mentor Name");
        mentorUser.setRole(Role.MENTOR);

        mentor = new Mentor();
        mentor.setId(1);
        mentor.setUser(mentorUser);
        mentor.setTeams(new ArrayList<>());

        major = new Major();
        major.setName("IT");

        university = new University();
        university.setName("UET");

        team = new Team();
        team.setId(1);
        team.setName("Team A");
        team.setInternshipProgram(program);
        team.setMentor(mentor);
        team.setInterns(new ArrayList<>());
        
        mentor.getTeams().add(team);

        intern = new Intern();
        intern.setId(1);
        intern.setUser(new User());
        intern.getUser().setFullName("Intern Name");
        intern.setTeam(null);
        intern.setMajor(major);
        intern.setUniversity(university);

    }

    @Test
    void testCreateTeam_Success() {
        // Arrange
        CreateTeamRequest request = new CreateTeamRequest();
        request.setName("Team A");
        request.setInternshipProgramId(program.getId());
        request.setMentorId(mentor.getId());

        when(programRepository.findById(program.getId())).thenReturn(Optional.of(program));
        when(mentorRepository.findById(mentor.getId())).thenReturn(Optional.of(mentor));
        when(teamRepository.existsByNameAndInternshipProgram(request.getName(), program)).thenReturn(false);
        when(teamRepository.save(any(Team.class))).thenReturn(team);

        // Act
        TeamDetailResponse response = teamService.createTeam(request);

        // Assert
        assertNotNull(response);
        assertEquals(team.getName(), response.getTeamName());
        assertEquals(program.getName(), response.getInternshipProgramName());
        assertEquals(mentor.getUser().getFullName(), response.getMentorName());
        verify(teamRepository, times(1)).save(any(Team.class));
    }

    @Test
    void testGetTeamById_Success() {
        // Arrange
        when(teamRepository.findById(team.getId())).thenReturn(Optional.of(team));

        // Act
        TeamDetailResponse response = teamService.getTeamById(team.getId());

        // Assert
        assertNotNull(response);
        assertEquals(team.getId(), response.getId());
        assertEquals(team.getName(), response.getTeamName());
    }

    @Test
    void testUpdateTeam_Success() {
        // Arrange
        UpdateTeamRequest request = new UpdateTeamRequest();
        request.setName("New Team Name");
        request.setMentorId(mentor.getId());
        
        Team updatedTeam = new Team();
        updatedTeam.setId(team.getId());
        updatedTeam.setName(request.getName());
        updatedTeam.setMentor(mentor);
        updatedTeam.setInternshipProgram(program);
        updatedTeam.setInterns(new ArrayList<>());


        when(teamRepository.findById(team.getId())).thenReturn(Optional.of(team));
        when(mentorRepository.findById(mentor.getId())).thenReturn(Optional.of(mentor));
        when(teamRepository.existsByNameAndInternshipProgram(request.getName().trim(), team.getInternshipProgram())).thenReturn(false);
        when(teamRepository.save(any(Team.class))).thenReturn(updatedTeam);

        // Act
        TeamDetailResponse response = teamService.updateTeam(team.getId(), request);

        // Assert
        assertNotNull(response);
        assertEquals(request.getName(), response.getTeamName());
        verify(teamRepository, times(1)).save(any(Team.class));
    }

    @Test
    void testAddMember_Success() {
        // Arrange
        AddMemberRequest request = new AddMemberRequest();
        request.setInternIds(Set.of(intern.getId()));

        when(teamRepository.findById(team.getId())).thenReturn(Optional.of(team));
        when(internRepository.findById(intern.getId())).thenReturn(Optional.of(intern));

        // Act
        teamService.addMember(team.getId(), request);

        // Assert
        ArgumentCaptor<List<Intern>> internsCaptor = ArgumentCaptor.forClass(List.class);
        verify(internRepository, times(1)).saveAll(internsCaptor.capture());
        
        List<Intern> savedInterns = internsCaptor.getValue();
        assertEquals(1, savedInterns.size());
        assertEquals(team, savedInterns.get(0).getTeam());
    }

    @Test
    void testRemoveMember_Success() {
        // Arrange
        intern.setTeam(team); // Intern is already in the team
        when(internRepository.findById(intern.getId())).thenReturn(Optional.of(intern));

        // Act
        teamService.removeMember(intern.getId());

        // Assert
        ArgumentCaptor<Intern> internCaptor = ArgumentCaptor.forClass(Intern.class);
        verify(internRepository, times(1)).save(internCaptor.capture());

        Intern savedIntern = internCaptor.getValue();
        assertNull(savedIntern.getTeam());
    }

    @Test
    void testGetTeams_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Team> teamPage = new PageImpl<>(List.of(team), pageable, 1);

        when(teamRepository.searchTeam(any(), any(), any(), any(Pageable.class))).thenReturn(teamPage);

        // Act
        PagedResponse<TeamDetailResponse> response = teamService.getTeams(1, 1, "keyword", 1);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        assertEquals(1, response.getTotalPages());
        assertEquals(team.getName(), response.getContent().get(0).getTeamName());
    }
    
    @Test
    void testGetAllTeam_Success() {
        // Arrange
        GetAllTeamResponse teamResponse = new GetAllTeamResponse();
        teamResponse.setId(team.getId());
        teamResponse.setName(team.getName());
        teamResponse.setInternshipProgramName(program.getName());

        when(teamRepository.findAll()).thenReturn(List.of(team));
        when(modelMapper.map(team, GetAllTeamResponse.class)).thenReturn(teamResponse);
        
        // Act
        List<GetAllTeamResponse> responses = teamService.getAllTeam();

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(team.getName(), responses.get(0).getName());
    }

    @Test
    void testGetTeamsByCurrentMentor_Success() {
        // Arrange
        when(authService.getUserLogin()).thenReturn(mentorUser);
        when(mentorRepository.findByUser(mentorUser)).thenReturn(Optional.of(mentor));
        
        // Act
        List<TeamDetailResponse> responses = teamService.getTeamsByCurrentMentor();
        
        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(team.getName(), responses.get(0).getTeamName());
    }
}
