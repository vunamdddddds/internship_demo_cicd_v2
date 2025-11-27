package com.example.InternShip.service.impl;

import com.example.InternShip.entity.Allowance;
import com.example.InternShip.entity.Intern;
import com.example.InternShip.entity.MonthlyAllowanceReport;
import com.example.InternShip.entity.User;
import com.example.InternShip.repository.AllowanceRepository;
import com.example.InternShip.repository.MonthlyAllowanceReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AllowanceReportServiceImplTest {

    @Mock
    private AllowanceRepository allowanceRepository;

    @Mock
    private MonthlyAllowanceReportRepository monthlyAllowanceReportRepository;

    @InjectMocks
    private AllowanceReportServiceImpl allowanceReportService;

    private Allowance allowance;
    private MonthlyAllowanceReport monthlyAllowanceReport;
    private Intern intern;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setFullName("Test User");

        intern = new Intern();
        intern.setId(1); // Fix: Set intern ID
        intern.setUser(user);

        allowance = new Allowance();
        allowance.setId(1);
        allowance.setIntern(intern);
        allowance.setAmount(BigDecimal.valueOf(1000));
        allowance.setAllowanceMonth(LocalDate.now());

        monthlyAllowanceReport = new MonthlyAllowanceReport();
        monthlyAllowanceReport.setId(1);
        monthlyAllowanceReport.setReportMonth(LocalDate.now());
    }

    @Test
    void getAllMonthlyReports_happyPath() {
        Page<MonthlyAllowanceReport> page = new PageImpl<>(Collections.singletonList(monthlyAllowanceReport));
        when(monthlyAllowanceReportRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<MonthlyAllowanceReport> response = allowanceReportService.getAllMonthlyReports(Pageable.unpaged());

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
    }

    @Test
    void generateAllowanceReport_happyPath() throws IOException {
        when(allowanceRepository.findByAllowanceMonthBetween(any(), any())).thenReturn(Collections.singletonList(allowance));

        byte[] report = allowanceReportService.generateAllowanceReport(YearMonth.now());

        assertNotNull(report);
        assertTrue(report.length > 0);
    }

    @Test
    void getAllowanceDetailsForMonth_happyPath() {
        when(allowanceRepository.findByAllowanceMonthBetween(any(), any())).thenReturn(Collections.singletonList(allowance));

        List<Allowance> response = allowanceReportService.getAllowanceDetailsForMonth(YearMonth.now());

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void generateMonthlySummaryReport_happyPath() throws IOException {
        List<Allowance> allowances = Collections.singletonList(allowance);

        byte[] report = allowanceReportService.generateMonthlySummaryReport(allowances, YearMonth.now());

        assertNotNull(report);
        assertTrue(report.length > 0);
    }
}
