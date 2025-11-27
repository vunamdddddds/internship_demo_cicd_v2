package com.example.InternShip.service.impl;

import com.example.InternShip.dto.application.request.ApplicationRequest;
import com.example.InternShip.dto.application.request.HandleApplicationRequest;
import com.example.InternShip.dto.application.request.SubmitApplicationContractRequest;
import com.example.InternShip.dto.application.response.ApplicationResponse;
import com.example.InternShip.dto.cloudinary.response.FileResponse;
import com.example.InternShip.dto.response.PagedResponse;
import com.example.InternShip.entity.*;
import com.example.InternShip.entity.enums.Role;
import com.example.InternShip.repository.*;
import com.example.InternShip.service.CloudinaryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceImplTest {

    @Mock
    private InternshipApplicationRepository applicationRepository;
    @Mock
    private InternshipProgramRepository programRepository;
    @Mock
    private UniversityRepository universityRepository;
    @Mock
    private MajorRepository majorRepository;
    @Mock
    private AuthServiceImpl authService;
    @Mock
    private CloudinaryService cloudinaryService;
    @Mock
    private InternRepository internRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ApplicationServiceImpl applicationService;

    private User user;
    private InternshipProgram program;
    private University university;
    private Major major;
    private InternshipApplication application;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);
        user.setRole(Role.VISITOR);

        program = new InternshipProgram();
        program.setId(1);
        program.setStatus(InternshipProgram.Status.PUBLISHED);
        program.setName("Test Program");

        university = new University();
        university.setId(1);
        university.setName("Test University");

        major = new Major();
        major.setId(1);
        major.setName("Test Major");

        application = new InternshipApplication();
        application.setId(1);
        application.setUser(user);
        application.setInternshipProgram(program);
        application.setUniversity(university);
        application.setMajor(major);
        application.setStatus(InternshipApplication.Status.SUBMITTED);
    }

    @Test
    void submitApplication_happyPath() {
        ApplicationRequest request = new ApplicationRequest();
        request.setInternshipTermId(1);
        request.setUniversityId(1);
        request.setMajorId(1);
        request.setCvFile(mock(MultipartFile.class));
        request.setInternApplicationFile(mock(MultipartFile.class));

        when(authService.getUserLogin()).thenReturn(user);
        when(applicationRepository.findAllByUserId(1)).thenReturn(Collections.emptyList());
        when(programRepository.findById(1)).thenReturn(Optional.of(program));
        when(universityRepository.findById(1)).thenReturn(Optional.of(university));
        when(majorRepository.findById(1)).thenReturn(Optional.of(major));
        when(cloudinaryService.uploadFile(any(), any())).thenReturn(FileResponse.builder().fileUrl("test_url").publicId("test_id").build());
        when(applicationRepository.save(any(InternshipApplication.class))).thenReturn(application);
        when(modelMapper.map(any(User.class), any())).thenReturn(new ApplicationResponse());


        ApplicationResponse response = applicationService.submitApplication(request);

        assertNotNull(response);
    }

    @Test
    void getMyApplication_happyPath() {
        when(authService.getUserLogin()).thenReturn(user);
        when(applicationRepository.findAllByUserId(1)).thenReturn(Collections.singletonList(application));
        when(modelMapper.map(any(User.class), any())).thenReturn(new ApplicationResponse());


        List<ApplicationResponse> response = applicationService.getMyApplication();

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getAllApplication_happyPath() {
        Page<InternshipApplication> page = new PageImpl<>(Collections.singletonList(application));
        when(applicationRepository.searchApplications(any(), any(), any(), any(), any(), any(PageRequest.class))).thenReturn(page);
        when(modelMapper.map(any(User.class), any())).thenReturn(new ApplicationResponse());

        PagedResponse<ApplicationResponse> response = applicationService.getAllApplication(1, 1, 1, "keyword", "SUBMITTED", 1);

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
    }

    @Test
    void submitApplicationContract_happyPath() {
        SubmitApplicationContractRequest request = new SubmitApplicationContractRequest(mock(MultipartFile.class));
        application.setStatus(InternshipApplication.Status.APPROVED);

        when(authService.getUserLogin()).thenReturn(user);
        when(applicationRepository.findByUserIdAndStatus(1, InternshipApplication.Status.APPROVED)).thenReturn(Optional.of(application));
        when(cloudinaryService.uploadFile(any(), any())).thenReturn(FileResponse.builder().fileUrl("test_url").publicId("test_id").build());

        applicationService.submitApplicationContract(request);

        assertEquals(InternshipApplication.Status.CONFIRM, application.getStatus());
    }

    @Test
    void withdrawApplication_happyPath() {
        when(authService.getUserLogin()).thenReturn(user);
        when(applicationRepository.findById(1)).thenReturn(Optional.of(application));

        applicationService.withdrawApplication(1);

        assertEquals(InternshipApplication.Status.WITHDRAWN, application.getStatus());
    }

    @Test
    void handleApplicationAction_happyPath() {
        HandleApplicationRequest request = new HandleApplicationRequest();
        request.setApplicationIds(java.util.Set.of(1));
        request.setAction("approve");
        application.setStatus(InternshipApplication.Status.UNDER_REVIEW);
        program.setStatus(InternshipProgram.Status.REVIEWING);

        when(applicationRepository.findAllById(request.getApplicationIds())).thenReturn(Collections.singletonList(application));

        applicationService.handleApplicationAction(request);

        assertEquals(InternshipApplication.Status.APPROVED, application.getStatus());
    }
}
