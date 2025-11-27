package com.example.InternShip.service.impl;

import com.example.InternShip.dto.supportRequest.request.CreateSupportRequestRequest;
import com.example.InternShip.dto.supportRequest.request.RejectSupportRequestRequest;
import com.example.InternShip.dto.supportRequest.request.UpdateSupportRequestRequest;
import com.example.InternShip.dto.supportRequest.response.GetSupportRequestResponse;
import com.example.InternShip.dto.response.PagedResponse;
import com.example.InternShip.entity.Intern;
import com.example.InternShip.entity.SupportRequest;
import com.example.InternShip.entity.User;
import com.example.InternShip.repository.SupportRequestRepository;
import com.example.InternShip.service.AuthService;
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
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SupportRequestServiceImplTest {

    @Mock
    private SupportRequestRepository supportRequestRepository;
    @Mock
    private CloudinaryService cloudinaryService;
    @Mock
    private AuthService authService;
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private SupportRequestServiceImpl supportRequestService;

    private User user;
    private Intern intern;
    private SupportRequest supportRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        intern = new Intern();
        intern.setId(1);
        user.setIntern(intern);

        supportRequest = new SupportRequest();
        supportRequest.setId(1);
        supportRequest.setIntern(intern);
        supportRequest.setStatus(SupportRequest.Status.PENDING);
    }

    @Test
    void createSupportRequest_happyPath() {
        CreateSupportRequestRequest request = new CreateSupportRequestRequest();
        when(authService.getUserLogin()).thenReturn(user);
        when(supportRequestRepository.save(any(SupportRequest.class))).thenReturn(supportRequest);
        when(modelMapper.map(any(SupportRequest.class), any())).thenReturn(new GetSupportRequestResponse());

        GetSupportRequestResponse response = supportRequestService.createSupportRequest(request);

        assertNotNull(response);
    }

    @Test
    void getMyList_happyPath() {
        when(authService.getUserLogin()).thenReturn(user);
        when(supportRequestRepository.findAllByInternId(1)).thenReturn(Collections.singletonList(supportRequest));
        when(modelMapper.map(any(SupportRequest.class), any())).thenReturn(new GetSupportRequestResponse());


        List<GetSupportRequestResponse> response = supportRequestService.getMyList();

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void updateRequest_happyPath() {
        UpdateSupportRequestRequest request = new UpdateSupportRequestRequest();
        when(authService.getUserLogin()).thenReturn(user);
        when(supportRequestRepository.findById(1)).thenReturn(Optional.of(supportRequest));
        when(supportRequestRepository.save(any(SupportRequest.class))).thenReturn(supportRequest);
        when(modelMapper.map(any(SupportRequest.class), any())).thenReturn(new GetSupportRequestResponse());

        GetSupportRequestResponse response = supportRequestService.updateRequest(1, request);

        assertNotNull(response);
    }

    @Test
    void cancelSupportRequest_happyPath() {
        when(supportRequestRepository.findById(1)).thenReturn(Optional.of(supportRequest));
        assertDoesNotThrow(() -> supportRequestService.cancelSupportRequest(1));
    }

    @Test
    void getAllSupportRequest_happyPath() {
        Page<SupportRequest> page = new PageImpl<>(Collections.singletonList(supportRequest));
        when(supportRequestRepository.searchSupportRequest(any(), any(), any(Pageable.class))).thenReturn(page);
        when(modelMapper.map(any(SupportRequest.class), any())).thenReturn(new GetSupportRequestResponse());


        PagedResponse<GetSupportRequestResponse> response = supportRequestService.getAllSupportRequest(null, null, 1, 10);

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
    }

    @Test
    void approveSupportRequest_happyPath() {
        when(supportRequestRepository.findById(1)).thenReturn(Optional.of(supportRequest));
        when(supportRequestRepository.save(any(SupportRequest.class))).thenReturn(supportRequest);
        when(modelMapper.map(any(SupportRequest.class), any())).thenReturn(new GetSupportRequestResponse());


        GetSupportRequestResponse response = supportRequestService.approveSupportRequest(1);

        assertNotNull(response);
    }

    @Test
    void inProgressSupportRequest_happyPath() {
        supportRequest.setStatus(SupportRequest.Status.IN_PROGRESS);
        when(supportRequestRepository.findById(1)).thenReturn(Optional.of(supportRequest));
        when(supportRequestRepository.save(any(SupportRequest.class))).thenReturn(supportRequest);
        when(modelMapper.map(any(SupportRequest.class), any())).thenReturn(new GetSupportRequestResponse());

        GetSupportRequestResponse response = supportRequestService.inProgressSupportRequest(1);

        assertNotNull(response);
    }

    @Test
    void rejectSupportRequest_happyPath() {
        RejectSupportRequestRequest request = new RejectSupportRequestRequest();
        when(supportRequestRepository.findById(1)).thenReturn(Optional.of(supportRequest));
        when(supportRequestRepository.save(any(SupportRequest.class))).thenReturn(supportRequest);
        when(modelMapper.map(any(SupportRequest.class), any())).thenReturn(new GetSupportRequestResponse());

        GetSupportRequestResponse response = supportRequestService.rejectSupportRequest(1, request);

        assertNotNull(response);
    }
}
