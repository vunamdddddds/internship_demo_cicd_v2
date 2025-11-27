package com.example.InternShip.service.impl;

import com.example.InternShip.dto.mentor.request.CreateMentorRequest;
import com.example.InternShip.dto.mentor.response.GetMentorResponse;
import com.example.InternShip.entity.Department;
import com.example.InternShip.entity.Mentor;
import com.example.InternShip.entity.User;
import com.example.InternShip.entity.enums.Role;
import com.example.InternShip.repository.DepartmentRepository;
import com.example.InternShip.repository.MentorRepository;
import com.example.InternShip.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.example.InternShip.dto.mentor.request.UpdateMentorRequest;
import com.example.InternShip.dto.mentor.response.GetAllMentorResponse;
import com.example.InternShip.dto.response.PagedResponse;
import com.example.InternShip.dto.sprint.response.SprintResponse;
import com.example.InternShip.dto.team.response.TeamMemberResponse;
import com.example.InternShip.dto.mentor.response.TeamResponse;
import com.example.InternShip.entity.InternshipProgram;
import com.example.InternShip.entity.Sprint;
import com.example.InternShip.entity.Team;
import com.example.InternShip.service.AuthService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private MentorRepository mentorRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private AuthService authService;

    @InjectMocks
    private MentorServiceImpl mentorService;

    private User user;
    private Mentor mentor;
    private Department department;
    private CreateMentorRequest createMentorRequest;

    @BeforeEach
    void setUp() {
        department = new Department();
        department.setId(1);
        department.setName("Engineering");

        user = new User();
        user.setId(1);
        user.setFullName("Mentor Name");
        user.setEmail("mentor@example.com");
        user.setRole(Role.MENTOR);

        mentor = new Mentor();
        mentor.setId(1);
        mentor.setUser(user);
        mentor.setDepartment(department);
        mentor.setTeams(new ArrayList<>());


        createMentorRequest = new CreateMentorRequest();
        createMentorRequest.setEmail("mentor@example.com");
        createMentorRequest.setDepartmentId(1);
        createMentorRequest.setFullName("Mentor Name");
    }

    @Test
    void createMentor_WhenRequestIsValid_ShouldReturnMentorResponse() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(departmentRepository.findById(1)).thenReturn(Optional.of(department));
        when(modelMapper.map(createMentorRequest, User.class)).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(mentorRepository.save(any(Mentor.class))).thenReturn(mentor);

        GetMentorResponse expectedResponse = new GetMentorResponse();
        expectedResponse.setId(1);
        expectedResponse.setFullName("Mentor Name");
        expectedResponse.setDepartmentName("Engineering");
        when(modelMapper.map(user, GetMentorResponse.class)).thenReturn(expectedResponse);

        // Act
        GetMentorResponse actualResponse = mentorService.createMentor(createMentorRequest);

        // Assert
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getId(), actualResponse.getId());
        assertEquals(expectedResponse.getFullName(), actualResponse.getFullName());
        assertEquals(expectedResponse.getDepartmentName(), actualResponse.getDepartmentName());
    }

    @Test
    void createMentor_WhenEmailExists_ShouldThrowException() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> mentorService.createMentor(createMentorRequest));
    }

    @Test
    void createMentor_WhenDepartmentNotFound_ShouldThrowException() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(departmentRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> mentorService.createMentor(createMentorRequest));
    }

    @Test
    void updateMentorDepartment_WhenMentorAndDepartmentExist_ShouldUpdateAndReturnResponse() {
        // Arrange
        UpdateMentorRequest request = new UpdateMentorRequest();
        request.setDepartmentId(2);

        Department newDepartment = new Department();
        newDepartment.setId(2);
        newDepartment.setName("New Engineering");

        when(mentorRepository.findById(1)).thenReturn(Optional.of(mentor));
        when(departmentRepository.findById(2)).thenReturn(Optional.of(newDepartment));
        when(mentorRepository.save(any(Mentor.class))).thenReturn(mentor);

        GetMentorResponse expectedResponse = new GetMentorResponse();
        expectedResponse.setDepartmentName("New Engineering");
        when(modelMapper.map(user, GetMentorResponse.class)).thenReturn(new GetMentorResponse());

        // Act
        GetMentorResponse actualResponse = mentorService.updateMentorDepartment(1, request);

        // Assert
        assertNotNull(actualResponse);
        assertEquals(newDepartment.getName(), actualResponse.getDepartmentName());
    }

    @Test
    void updateMentorDepartment_WhenMentorNotFound_ShouldThrowException() {
        // Arrange
        UpdateMentorRequest request = new UpdateMentorRequest();
        request.setDepartmentId(1);
        when(mentorRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> mentorService.updateMentorDepartment(1, request));
    }

    @Test
    void updateMentorDepartment_WhenDepartmentNotFound_ShouldThrowException() {
        // Arrange
        UpdateMentorRequest request = new UpdateMentorRequest();
        request.setDepartmentId(1);
        when(mentorRepository.findById(1)).thenReturn(Optional.of(mentor));
        when(departmentRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> mentorService.updateMentorDepartment(1, request));
    }

    @Test
    void getAll_ShouldReturnPagedResponseOfMentors() {
        // Arrange
        Page<Mentor> pagedMentors = new PageImpl<>(List.of(mentor));
        when(mentorRepository.searchMentor(any(), any(), any(PageRequest.class))).thenReturn(pagedMentors);
        when(modelMapper.map(user, GetMentorResponse.class)).thenReturn(new GetMentorResponse());

        // Act
        PagedResponse<GetMentorResponse> response = mentorService.getAll(null, null, 1);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getContent().size());
    }

    @Test
    void getAllMentor_ShouldReturnListOfAllMentors() {
        // Arrange
        when(mentorRepository.findAll()).thenReturn(List.of(mentor));

        // Act
        List<GetAllMentorResponse> response = mentorService.getAllMentor();

        // Assert
        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getSprintsForCurrentUser_WhenUserIsMentor_ShouldReturnSprints() {
        // Arrange
        Team team = new Team();
        team.setId(1);
        Sprint sprint = new Sprint();
        sprint.setId(1L);
        sprint.setTeam(team);
        team.setSprints(List.of(sprint));
        mentor.setTeams(List.of(team));
        when(authService.getUserLogin()).thenReturn(user);
        when(mentorRepository.findByUser(user)).thenReturn(Optional.of(mentor));

        // Act
        List<SprintResponse> response = mentorService.getSprintsForCurrentUser();

        // Assert
        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getSprintsForCurrentUser_WhenUserIsNotMentor_ShouldThrowException() {
        // Arrange
        user.setRole(Role.INTERN);
        when(authService.getUserLogin()).thenReturn(user);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> mentorService.getSprintsForCurrentUser());
    }

    @Test
    void getTeamsForCurrentUser_WhenUserIsMentor_ShouldReturnTeams() {
        // Arrange
        Team team = new Team();
        team.setId(1);
        team.setName("Test Team");
        team.setInterns(Collections.emptyList());
        mentor.setTeams(List.of(team));
        when(authService.getUserLogin()).thenReturn(user);
        when(mentorRepository.findByUser(user)).thenReturn(Optional.of(mentor));
        // Act
        List<TeamResponse> response = mentorService.getTeamsForCurrentUser();

        // Assert
        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getTeamsForCurrentUser_WhenUserIsNotMentor_ShouldThrowException() {
        // Arrange
        user.setRole(Role.INTERN);
        when(authService.getUserLogin()).thenReturn(user);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> mentorService.getTeamsForCurrentUser());
    }
}
