package com.example.InternShip.service;

import com.example.InternShip.entity.Allowance;

import com.example.InternShip.entity.MonthlyAllowanceReport;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.YearMonth;
import java.util.List;


public interface AllowanceReportService {
    Page<MonthlyAllowanceReport> getAllMonthlyReports(Pageable pageable) ;
    byte[] generateAllowanceReport(YearMonth yearMonth) throws IOException;
    List<Allowance> getAllowanceDetailsForMonth(YearMonth yearMonth);
    byte[] generateMonthlySummaryReport(List<Allowance> generatedAllowances, YearMonth yearMonth) throws IOException;
}
