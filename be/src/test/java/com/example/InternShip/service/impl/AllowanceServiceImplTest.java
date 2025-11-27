package com.example.InternShip.service.impl;

import com.example.InternShip.dto.AllowanceResponse;
import com.example.InternShip.dto.response.PagedResponse;
import com.example.InternShip.entity.Allowance;
import com.example.InternShip.entity.Intern;
import com.example.InternShip.entity.InternshipProgram;
import com.example.InternShip.entity.User;
import com.example.InternShip.repository.AllowanceRepository;
import com.example.InternShip.repository.InternRepository;
import com.example.InternShip.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AllowanceServiceImplTest {

    @Mock
    private AllowanceRepository allowanceRepository;

    @Mock
    private InternRepository internRepository;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AllowanceServiceImpl allowanceService;

    private Allowance allowance;
    private Intern intern;
    private User user;
    private InternshipProgram internshipProgram;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setFullName("Test User");
        user.setEmail("test@example.com");

        internshipProgram = new InternshipProgram();
        internshipProgram.setName("Test Program");

        intern = new Intern();
        intern.setId(1);
        intern.setUser(user);
        intern.setInternshipProgram(internshipProgram);

        allowance = new Allowance();
        allowance.setId(1);
        allowance.setIntern(intern);
        allowance.setAmount(BigDecimal.valueOf(1000));
        allowance.setStatus(Allowance.Status.PENDING);
    }

    @Test
    void getAllAllowances_happyPath() {
        Page<Allowance> page = new PageImpl<>(new ArrayList<>(List.of(allowance)));
        when(allowanceRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        PagedResponse<AllowanceResponse> response = allowanceService.getAllAllowances(1L, null, null, PageRequest.of(0, 10));

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
    }

    @Test
    void getMyAllowances_happyPath() {
        when(authService.getUserLogin()).thenReturn(user);
        when(internRepository.findByUser(user)).thenReturn(Optional.of(intern));
        Page<Allowance> page = new PageImpl<>(List.of(allowance));
        when(allowanceRepository.findByIntern_Id(intern.getId(), Pageable.unpaged())).thenReturn(page);

        PagedResponse<AllowanceResponse> response = allowanceService.getMyAllowances(Pageable.unpaged());

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        assertEquals("Test User", response.getContent().get(0).getInternName());
    }
}
