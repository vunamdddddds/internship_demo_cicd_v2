package com.example.InternShip.service.impl;

import com.example.InternShip.dto.leaveRequest.request.CreateLeaveApplicationRequest;
import com.example.InternShip.dto.leaveRequest.request.RejectLeaveApplicationRequest;
import com.example.InternShip.dto.leaveRequest.response.GetAllLeaveApplicationResponse;
import com.example.InternShip.dto.leaveRequest.response.GetLeaveApplicationResponse;
import com.example.InternShip.dto.leaveRequest.response.InternGetAllLeaveApplicationResponse;
import com.example.InternShip.dto.response.PagedResponse;
import com.example.InternShip.entity.Intern;
import com.example.InternShip.entity.LeaveRequest;
import com.example.InternShip.entity.User;
import com.example.InternShip.repository.LeaveRequestRepository;
import com.example.InternShip.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LeaveRequestServiceImplTest {

    @Mock
    private LeaveRequestRepository leaveRequestRepository;
    @Mock
    private AuthService authService;
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private LeaveRequestServiceImpl leaveRequestService;

    private User user;
    private Intern intern;
    private LeaveRequest leaveRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setFullName("Test User");
        intern = new Intern();
        intern.setId(1);
        intern.setUser(user);
        user.setIntern(intern);

        leaveRequest = new LeaveRequest();
        leaveRequest.setId(1);
        leaveRequest.setIntern(intern);
        leaveRequest.setApproved(null); // Pending
    }

    @Test
    void createLeaveRequest_happyPath() {
        CreateLeaveApplicationRequest request = new CreateLeaveApplicationRequest();
        request.setType("ON_LEAVE");
        request.setDate(LocalDate.now().plusDays(1));
        request.setReason("Vacation");

        when(authService.getUserLogin()).thenReturn(user);
        when(leaveRequestRepository.save(any(LeaveRequest.class))).thenReturn(leaveRequest);

        assertNotNull(leaveRequestService.createLeaveRequest(request));
    }

    @Test
    void getAllLeaveApplication_happyPath() {
        Page<LeaveRequest> page = new PageImpl<>(Collections.singletonList(leaveRequest));
        when(leaveRequestRepository.searchLeaveApplication(any(), any(), any(), any(Pageable.class))).thenReturn(page);

        PagedResponse<GetAllLeaveApplicationResponse> response = leaveRequestService.getAllLeaveApplication(null, null, null, 1, 10);

        assertNotNull(response);
    }

    @Test
    void getAllLeaveApplicationByIntern_happyPath() {
        when(authService.getUserLogin()).thenReturn(user);
        when(leaveRequestRepository.findAllByInternIdAndApproved(1, null)).thenReturn(Collections.singletonList(leaveRequest));

        InternGetAllLeaveApplicationResponse response = leaveRequestService.getAllLeaveApplicationByIntern(null);

        assertNotNull(response);
        assertEquals(1, response.getLeaveApplications().size());
    }

    @Test
    void viewLeaveApplication_happyPath() {
        User internUser = new User();
        internUser.setFullName("Intern Name");
        intern.setUser(internUser);
        when(leaveRequestRepository.findById(1)).thenReturn(Optional.of(leaveRequest));
        when(modelMapper.map(leaveRequest, GetLeaveApplicationResponse.class)).thenReturn(new GetLeaveApplicationResponse());

        GetLeaveApplicationResponse response = leaveRequestService.viewLeaveApplication(1);

        assertNotNull(response);
    }

    @Test
    void cancelLeaveApplication_happyPath() {
        when(leaveRequestRepository.findById(1)).thenReturn(Optional.of(leaveRequest));
        assertDoesNotThrow(() -> leaveRequestService.cancelLeaveApplication(1));
    }

    @Test
    void approveLeaveApplication_happyPath() {
        User internUser = new User();
        internUser.setFullName("Intern Name");
        intern.setUser(internUser);
        when(leaveRequestRepository.findById(1)).thenReturn(Optional.of(leaveRequest));
        when(leaveRequestRepository.save(any(LeaveRequest.class))).thenReturn(leaveRequest);

        GetAllLeaveApplicationResponse response = leaveRequestService.approveLeaveApplication(1);
        assertNotNull(response);
        assertTrue(response.getApproved());
    }

    @Test
    void rejectLeaveApplication_happyPath() {
        User internUser = new User();
        internUser.setFullName("Intern Name");
        intern.setUser(internUser);
        RejectLeaveApplicationRequest request = new RejectLeaveApplicationRequest();
        request.setReasonReject("Reason");

        when(leaveRequestRepository.findById(1)).thenReturn(Optional.of(leaveRequest));
        when(leaveRequestRepository.save(any(LeaveRequest.class))).thenReturn(leaveRequest);

        GetAllLeaveApplicationResponse response = leaveRequestService.rejectLeaveApplication(1, request);

        assertNotNull(response);
        assertFalse(response.getApproved());
    }
}
