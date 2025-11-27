package com.example.InternShip.service.impl;

import com.example.InternShip.dto.internshipProgram.request.CreateInternProgramRequest;
import com.example.InternShip.dto.internshipProgram.request.UpdateInternProgramRequest;
import com.example.InternShip.dto.internshipProgram.response.GetAllInternProgramResponse;
import com.example.InternShip.dto.internshipProgram.response.GetInternProgramResponse;
import com.example.InternShip.dto.response.PagedResponse;
import com.example.InternShip.entity.Department;
import com.example.InternShip.entity.InternshipProgram;
import com.example.InternShip.entity.User;
import com.example.InternShip.entity.enums.Role;
import com.example.InternShip.repository.DepartmentRepository;
import com.example.InternShip.repository.InternshipApplicationRepository;
import com.example.InternShip.repository.InternshipProgramRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.quartz.Scheduler;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InternshipProgramServiceImplTest {

    @Mock
    private InternshipProgramRepository internshipProgramRepository;
    @Mock
    private InternshipApplicationRepository internshipApplicationRepository;
    @Mock
    private DepartmentRepository departmentRepository;
    @Mock
    private AuthServiceImpl authService;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private Scheduler scheduler;

    @InjectMocks
    private InternshipProgramServiceImpl internshipProgramService;

    private InternshipProgram program;
    private User user;
    private Department department;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setRole(Role.HR);

        department = new Department();
        department.setId(1);
        department.setName("IT");

        program = new InternshipProgram();
        program.setId(1);
        program.setName("Test Program");
        program.setDepartment(department);
        program.setStatus(InternshipProgram.Status.DRAFT);
        program.setEndPublishedTime(LocalDateTime.now().plusDays(1));
        program.setEndReviewingTime(LocalDateTime.now().plusDays(2));
        program.setTimeStart(LocalDateTime.now().plusDays(3));
        program.setTimeEnd(LocalDateTime.now().plusDays(4));
        program.setApplications(new java.util.ArrayList<>());
    }

    @Test
    void getAllPrograms_happyPath() {
        when(authService.getUserLogin()).thenReturn(user);
        when(internshipProgramRepository.findAll()).thenReturn(Collections.singletonList(program));
        when(modelMapper.map(program, GetAllInternProgramResponse.class)).thenReturn(new GetAllInternProgramResponse());

        List<GetAllInternProgramResponse> response = internshipProgramService.getAllPrograms();

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getAllInternshipPrograms_happyPath() {
        Page<InternshipProgram> page = new PageImpl<>(Collections.singletonList(program));
        when(internshipProgramRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
        when(modelMapper.map(program, GetInternProgramResponse.class)).thenReturn(new GetInternProgramResponse());

        PagedResponse<GetInternProgramResponse> response = internshipProgramService.getAllInternshipPrograms(null, null, false, 1);

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
    }

    @Test
    void createInternProgram_happyPath() throws Exception {
        CreateInternProgramRequest request = new CreateInternProgramRequest();
        request.setDepartmentId(1);
        request.setEndPublishedTime(LocalDateTime.now().plusDays(1));
        request.setEndReviewingTime(LocalDateTime.now().plusDays(2));
        request.setTimeStart(LocalDateTime.now().plusDays(3));
        request.setDraft(false);

        when(departmentRepository.findById(1)).thenReturn(Optional.of(department));
        when(modelMapper.map(request, InternshipProgram.class)).thenReturn(program);
        when(internshipProgramRepository.save(program)).thenReturn(program);
        when(modelMapper.map(program, GetInternProgramResponse.class)).thenReturn(new GetInternProgramResponse());

        GetInternProgramResponse response = internshipProgramService.createInternProgram(request);

        assertNotNull(response);
    }

    @Test
    void updateInternProgram_happyPath() throws Exception {
        UpdateInternProgramRequest request = new UpdateInternProgramRequest();
        request.setEndPublishedTime(LocalDateTime.now().plusDays(1));
        request.setEndReviewingTime(LocalDateTime.now().plusDays(2));
        request.setTimeStart(LocalDateTime.now().plusDays(3));

        when(internshipProgramRepository.findById(1)).thenReturn(Optional.of(program));
        org.mockito.Mockito.doNothing().when(modelMapper).map(any(UpdateInternProgramRequest.class), any(InternshipProgram.class));
        when(internshipProgramRepository.save(program)).thenReturn(program);
        when(modelMapper.map(program, GetInternProgramResponse.class)).thenReturn(new GetInternProgramResponse());

        GetInternProgramResponse response = internshipProgramService.updateInternProgram(request, 1);

        assertNotNull(response);
    }

    @Test
    void cancelInternProgram_happyPath() throws Exception {
        program.setStatus(InternshipProgram.Status.DRAFT);
        when(internshipProgramRepository.findById(1)).thenReturn(Optional.of(program));
        when(internshipProgramRepository.save(program)).thenReturn(program);
        when(modelMapper.map(program, GetInternProgramResponse.class)).thenReturn(new GetInternProgramResponse());

        GetInternProgramResponse response = internshipProgramService.cancelInternProgram(1);

        assertNotNull(response);
        assertEquals(InternshipProgram.Status.CANCELLED, program.getStatus());
    }

    @Test
    void publishInternProgram_happyPath() throws Exception {
        program.setStatus(InternshipProgram.Status.DRAFT);
        when(internshipProgramRepository.findById(1)).thenReturn(Optional.of(program));
        when(internshipProgramRepository.save(program)).thenReturn(program);
        when(modelMapper.map(program, GetInternProgramResponse.class)).thenReturn(new GetInternProgramResponse());

        GetInternProgramResponse response = internshipProgramService.publishInternProgram(1);

        assertNotNull(response);
        assertEquals(InternshipProgram.Status.PUBLISHED, program.getStatus());
    }

    @Test
    void endPublish_happyPath() {
        when(internshipProgramRepository.findById(1)).thenReturn(Optional.of(program));

        assertDoesNotThrow(() -> internshipProgramService.endPublish(1));
        assertEquals(InternshipProgram.Status.REVIEWING, program.getStatus());
    }

    @Test
    void endReviewing_happyPath() {
        program.setApplications(Collections.emptyList());
        when(internshipProgramRepository.findById(1)).thenReturn(Optional.of(program));

        assertDoesNotThrow(() -> internshipProgramService.endReviewing(1));
        assertEquals(InternshipProgram.Status.PENDING, program.getStatus());
    }

    @Test
    void startInternship_happyPath() {
        program.setApplications(Collections.emptyList());
        when(internshipProgramRepository.findById(1)).thenReturn(Optional.of(program));

        assertDoesNotThrow(() -> internshipProgramService.startInternship(1));
        assertEquals(InternshipProgram.Status.ONGOING, program.getStatus());
    }
}
