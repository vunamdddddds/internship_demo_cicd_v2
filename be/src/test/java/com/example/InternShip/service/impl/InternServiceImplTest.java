package com.example.InternShip.service.impl;

import com.example.InternShip.dto.intern.response.GetInternResponse;
import com.example.InternShip.dto.intern.response.MyProfileResponse;
import com.example.InternShip.dto.team.response.TeamDetailResponse;
import com.example.InternShip.dto.intern.request.CreateInternRequest;
import com.example.InternShip.dto.intern.request.GetAllInternRequest;
import com.example.InternShip.dto.intern.request.UpdateInternRequest;
import com.example.InternShip.dto.intern.response.GetAllInternNoTeamResponse;
import com.example.InternShip.dto.response.PagedResponse;
import com.example.InternShip.repository.InternshipProgramRepository;
import com.example.InternShip.repository.MajorRepository;
import com.example.InternShip.repository.TeamRepository;
import com.example.InternShip.repository.UniversityRepository;
import com.example.InternShip.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import com.example.InternShip.entity.*;
import com.example.InternShip.repository.InternRepository;
import com.example.InternShip.service.AuthService;
import com.example.InternShip.service.TeamService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InternServiceImplTest {

    @Mock
    private InternRepository internRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UniversityRepository universityRepository;

    @Mock
    private MajorRepository majorRepository;

    @Mock
    private InternshipProgramRepository internshipProgramRepository;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private AuthService authService;

    @Mock
    private TeamService teamService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private InternServiceImpl internService;

    private User user;
    private Intern intern;
    private Team team;
    private University university;
    private Major major;
    private InternshipProgram internshipProgram;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);
        user.setFullName("John Doe");

        university = new University();
        university.setId(1);
        university.setName("Test University");

        major = new Major();
        major.setId(1);
        major.setName("Computer Science");
        
        internshipProgram = new InternshipProgram();
        internshipProgram.setId(1);
        internshipProgram.setName("Summer Internship");


        team = new Team();
        team.setId(1);
        team.setName("Test Team");

        intern = new Intern();
        intern.setId(1);
        intern.setUser(user);
        intern.setTeam(team);
        intern.setUniversity(university);
        intern.setMajor(major);
        intern.setInternshipProgram(internshipProgram);
        intern.setStatus(Intern.Status.ACTIVE);
    }

    @Test
    void getMyProfile_WhenInternExists_ShouldReturnProfile() {
        // Arrange
        when(authService.getUserLogin()).thenReturn(user);
        when(internRepository.findByUser(user)).thenReturn(Optional.of(intern));

        GetInternResponse internResponse = new GetInternResponse();
        internResponse.setId(intern.getId());
        internResponse.setFullName(user.getFullName());
        internResponse.setUniversity(university.getName());
        internResponse.setMajor(major.getName());
        internResponse.setInternshipProgram(internshipProgram.getName());
        internResponse.setStatus(intern.getStatus());


        TeamDetailResponse teamDetailResponse = new TeamDetailResponse();
        teamDetailResponse.setId(team.getId());
        teamDetailResponse.setTeamName(team.getName());

        when(modelMapper.map(user, GetInternResponse.class)).thenReturn(internResponse);
        when(teamService.mapToTeamDetailResponse(team)).thenReturn(teamDetailResponse);

        // Act
        MyProfileResponse myProfileResponse = internService.getMyProfile();

        // Assert
        assertNotNull(myProfileResponse);
        assertEquals(intern.getId(), myProfileResponse.getInternDetails().getId());
        assertEquals(user.getFullName(), myProfileResponse.getInternDetails().getFullName());
        assertEquals(team.getId(), myProfileResponse.getTeamDetails().getId());
    }

    @Test
    void getMyProfile_WhenInternDoesNotExist_ShouldThrowException() {
        // Arrange
        when(authService.getUserLogin()).thenReturn(user);
        when(internRepository.findByUser(user)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> internService.getMyProfile());
    }

    @Test
    void updateIntern_WhenInternExists_ShouldUpdateAndReturnIntern() {
        // Arrange
        UpdateInternRequest request = new UpdateInternRequest();
        request.setUniversityId(university.getId());
        request.setMajorId(major.getId());
        request.setStatus("SUSPENDED");

        when(universityRepository.findById(university.getId())).thenReturn(Optional.of(university));
        when(majorRepository.findById(major.getId())).thenReturn(Optional.of(major));
        when(internRepository.findById(intern.getId())).thenReturn(Optional.of(intern));
        when(internRepository.save(any(Intern.class))).thenReturn(intern);

        GetInternResponse internResponse = new GetInternResponse();
        when(modelMapper.map(user, GetInternResponse.class)).thenReturn(internResponse);

        // Act
        GetInternResponse response = internService.updateIntern(intern.getId(), request);

        // Assert
        assertNotNull(response);
        assertEquals(Intern.Status.SUSPENDED, intern.getStatus());
    }

    @Test
    void updateIntern_WhenUniversityNotFound_ShouldThrowException() {
        // Arrange
        UpdateInternRequest request = new UpdateInternRequest();
        request.setUniversityId(99);
        request.setMajorId(major.getId());
        request.setStatus("ACTIVE");

        when(universityRepository.findById(99)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> internService.updateIntern(intern.getId(), request));
    }

    @Test
    void updateIntern_WhenMajorNotFound_ShouldThrowException() {
        // Arrange
        UpdateInternRequest request = new UpdateInternRequest();
        request.setUniversityId(university.getId());
        request.setMajorId(99);
        request.setStatus("ACTIVE");

        when(universityRepository.findById(university.getId())).thenReturn(Optional.of(university));
        when(majorRepository.findById(99)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> internService.updateIntern(intern.getId(), request));
    }

    @Test
    void updateIntern_WhenInternNotFound_ShouldThrowException() {
        // Arrange
        UpdateInternRequest request = new UpdateInternRequest();
        request.setUniversityId(university.getId());
        request.setMajorId(major.getId());
        request.setStatus("ACTIVE");

        when(universityRepository.findById(university.getId())).thenReturn(Optional.of(university));
        when(majorRepository.findById(major.getId())).thenReturn(Optional.of(major));
        when(internRepository.findById(99)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> internService.updateIntern(99, request));
    }

    @Test
    void updateIntern_WhenStatusIsInvalid_ShouldThrowException() {
        // Arrange
        UpdateInternRequest request = new UpdateInternRequest();
        request.setUniversityId(university.getId());
        request.setMajorId(major.getId());
        request.setStatus("INVALID_STATUS");

        when(universityRepository.findById(university.getId())).thenReturn(Optional.of(university));
        when(majorRepository.findById(major.getId())).thenReturn(Optional.of(major));
        when(internRepository.findById(intern.getId())).thenReturn(Optional.of(intern));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> internService.updateIntern(intern.getId(), request));
    }

    @Test
    void createIntern_WhenRequestIsValid_ShouldCreateAndReturnIntern() {
        // Arrange
        CreateInternRequest request = new CreateInternRequest();
        request.setEmail("test@example.com");
        request.setInternshipProgramId(internshipProgram.getId());
        request.setUniversityId(university.getId());
        request.setMajorId(major.getId());

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        internshipProgram.setStatus(InternshipProgram.Status.PUBLISHED);
        when(internshipProgramRepository.findById(request.getInternshipProgramId())).thenReturn(Optional.of(internshipProgram));
        when(universityRepository.findById(request.getUniversityId())).thenReturn(Optional.of(university));
        when(majorRepository.findById(request.getMajorId())).thenReturn(Optional.of(major));
        when(modelMapper.map(any(CreateInternRequest.class), eq(User.class))).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(internRepository.save(any(Intern.class))).thenReturn(intern);
        when(modelMapper.map(any(User.class), eq(GetInternResponse.class))).thenReturn(new GetInternResponse());

        // Act
        GetInternResponse response = internService.createIntern(request);

        // Assert
        assertNotNull(response);
    }

    @Test
    void createIntern_WhenEmailExists_ShouldThrowException() {
        // Arrange
        CreateInternRequest request = new CreateInternRequest();
        request.setEmail("test@example.com");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> internService.createIntern(request));
    }

    @Test
    void createIntern_WhenInternshipProgramNotFound_ShouldThrowException() {
        // Arrange
        CreateInternRequest request = new CreateInternRequest();
        request.setEmail("test@example.com");
        request.setInternshipProgramId(99);

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(internshipProgramRepository.findById(99)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> internService.createIntern(request));
    }

    @Test
    void createIntern_WhenInternshipProgramNotPublished_ShouldThrowException() {
        // Arrange
        CreateInternRequest request = new CreateInternRequest();
        request.setEmail("test@example.com");
        request.setInternshipProgramId(internshipProgram.getId());

        internshipProgram.setStatus(InternshipProgram.Status.DRAFT);
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(internshipProgramRepository.findById(internshipProgram.getId())).thenReturn(Optional.of(internshipProgram));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> internService.createIntern(request));
    }

    @Test
    void createIntern_WhenUniversityNotFound_ShouldThrowException() {
        // Arrange
        CreateInternRequest request = new CreateInternRequest();
        request.setEmail("test@example.com");
        request.setInternshipProgramId(internshipProgram.getId());
        request.setUniversityId(99);

        internshipProgram.setStatus(InternshipProgram.Status.PUBLISHED);
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(internshipProgramRepository.findById(internshipProgram.getId())).thenReturn(Optional.of(internshipProgram));
        when(universityRepository.findById(99)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> internService.createIntern(request));
    }

    @Test
    void createIntern_WhenMajorNotFound_ShouldThrowException() {
        // Arrange
        CreateInternRequest request = new CreateInternRequest();
        request.setEmail("test@example.com");
        request.setInternshipProgramId(internshipProgram.getId());
        request.setUniversityId(university.getId());
        request.setMajorId(99);

        internshipProgram.setStatus(InternshipProgram.Status.PUBLISHED);
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(internshipProgramRepository.findById(internshipProgram.getId())).thenReturn(Optional.of(internshipProgram));
        when(universityRepository.findById(university.getId())).thenReturn(Optional.of(university));
        when(majorRepository.findById(99)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> internService.createIntern(request));
    }
    
    @Test
    void getAllIntern_ShouldReturnPagedResponse() {
        // Arrange
        GetAllInternRequest request = new GetAllInternRequest();
        request.setPage(1);
        Page<Intern> pagedInterns = new PageImpl<>(List.of(intern));
        when(internRepository.searchInterns(any(), any(), any(), any())).thenReturn(pagedInterns);
        when(modelMapper.map(any(User.class), eq(GetInternResponse.class))).thenReturn(new GetInternResponse());

        // Act
        PagedResponse<GetInternResponse> response = internService.getAllIntern(request);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getContent().size());
    }

    @Test
    void getAllInternNoTeam_WhenTeamExists_ShouldReturnListOfInterns() {
        // Arrange
        team.setInternshipProgram(internshipProgram);
        when(teamRepository.findById(team.getId())).thenReturn(Optional.of(team));
        Intern intern2 = new Intern();
        intern2.setUser(new User());
        intern2.setInternshipProgram(internshipProgram);
        intern2.setStatus(Intern.Status.ACTIVE);
        intern2.setTeam(null);
        Intern internWithTeam = new Intern();
        internWithTeam.setUser(new User());
        internWithTeam.setInternshipProgram(internshipProgram);
        internWithTeam.setStatus(Intern.Status.ACTIVE);
        internWithTeam.setTeam(new Team());
        when(internRepository.findAll()).thenReturn(List.of(internWithTeam, intern2));
        when(modelMapper.map(any(User.class), eq(GetAllInternNoTeamResponse.class))).thenReturn(new GetAllInternNoTeamResponse());

        // Act
        List<GetAllInternNoTeamResponse> response = internService.getAllInternNoTeam(team.getId());

        // Assert
        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getAllInternNoTeam_WhenTeamDoesNotExist_ShouldThrowException() {
        // Arrange
        when(teamRepository.findById(99)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> internService.getAllInternNoTeam(99));
    }

    @Test
    void getAuthenticatedInternTeamId_WhenInternInTeam_ShouldReturnTeamId() {
        // Arrange
        when(authService.getUserLogin()).thenReturn(user);
        when(internRepository.findByUser(user)).thenReturn(Optional.of(intern));

        // Act
        Integer teamId = internService.getAuthenticatedInternTeamId();

        // Assert
        assertEquals(team.getId(), teamId);
    }

    @Test
    void getAuthenticatedInternTeamId_WhenInternNotInTeam_ShouldReturnNull() {
        // Arrange
        intern.setTeam(null);
        when(authService.getUserLogin()).thenReturn(user);
        when(internRepository.findByUser(user)).thenReturn(Optional.of(intern));

        // Act
        Integer teamId = internService.getAuthenticatedInternTeamId();

        // Assert
        assertNull(teamId);
    }

    @Test
    void getAllInternByTeamId_WhenTeamExists_ShouldReturnListOfInterns() {
        // Arrange
        team.setInterns(List.of(intern));
        when(teamRepository.findById(team.getId())).thenReturn(Optional.of(team));
        when(modelMapper.map(any(User.class), eq(GetInternResponse.class))).thenReturn(new GetInternResponse());

        // Act
        List<GetInternResponse> response = internService.getAllInternByTeamId(team.getId());

        // Assert
        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getAllInternByTeamId_WhenTeamDoesNotExist_ShouldThrowException() {
        // Arrange
        when(teamRepository.findById(99)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> internService.getAllInternByTeamId(99));
    }
}
