package com.example.InternShip.service.impl;

import com.example.InternShip.entity.Allowance;
import com.example.InternShip.repository.AllowanceRepository;
import com.example.InternShip.repository.MonthlyAllowanceReportRepository;
import com.example.InternShip.service.AllowanceReportService;
import com.example.InternShip.entity.Intern;
import com.example.InternShip.entity.MonthlyAllowanceReport;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AllowanceReportServiceImpl implements AllowanceReportService {
    @Autowired
    private AllowanceRepository allowanceRepository;

    @Autowired
    private MonthlyAllowanceReportRepository monthlyAllowanceReportRepository;

    public Page<MonthlyAllowanceReport> getAllMonthlyReports(Pageable pageable) {
        return monthlyAllowanceReportRepository.findAll(pageable);
    }

    public byte[] generateAllowanceReport(YearMonth yearMonth) throws IOException {
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        // Fetch allowances for the given month
        // You might need a custom query in AllowanceRepository to fetch by
        // allowanceMonth range
        // For simplicity, let's fetch all and filter in memory for now, but a
        // repository query is better
        List<Allowance> allowances = allowanceRepository.findByAllowanceMonthBetween(startDate, endDate);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook
                .createSheet("Allowance Report " + yearMonth.format(DateTimeFormatter.ofPattern("MM-yyyy")));

        // Create header row
        String[] headers = { "ID", "Intern Name", "Internship Program", "Allowance Package", "Amount", "Work Days",
                "Period", "Status", "Paid By", "Paid At", "Notes" };
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        // Populate data rows
        int rowNum = 1;
        for (Allowance allowance : allowances) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(allowance.getId());
            row.createCell(1)
                    .setCellValue(allowance.getIntern() != null && allowance.getIntern().getUser() != null
                            ? allowance.getIntern().getUser().getFullName()
                            : "N/A");
            row.createCell(2)
                    .setCellValue(allowance.getIntern() != null && allowance.getIntern().getInternshipProgram() != null
                            ? allowance.getIntern().getInternshipProgram().getName()
                            : "N/A");
            row.createCell(3).setCellValue(
                    allowance.getAllowancePackage() != null ? allowance.getAllowancePackage().getName() : "N/A");
            row.createCell(4).setCellValue(allowance.getAmount().doubleValue());
            row.createCell(5).setCellValue(allowance.getWorkDays() != null ? allowance.getWorkDays() : 0);
            row.createCell(6)
                    .setCellValue(allowance.getPeriod() != null
                            ? allowance.getPeriod().format(DateTimeFormatter.ISO_LOCAL_DATE)
                            : "N/A");
            row.createCell(7).setCellValue(allowance.getStatus() != null ? allowance.getStatus().name() : "N/A");
            row.createCell(8)
                    .setCellValue(allowance.getRemitter() != null ? allowance.getRemitter().getFullName() : "N/A");
            row.createCell(9)
                    .setCellValue(allowance.getPaidAt() != null
                            ? allowance.getPaidAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                            : "N/A");
            row.createCell(10).setCellValue(allowance.getNotes() != null ? allowance.getNotes() : "");
        }

        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        return outputStream.toByteArray();
    }

    public List<Allowance> getAllowanceDetailsForMonth(YearMonth yearMonth) {
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        // This reuses the existing repository method to fetch detailed data for the
        // JSON API
        return allowanceRepository.findByAllowanceMonthBetween(startDate, endDate);
    }

    public byte[] generateMonthlySummaryReport(List<Allowance> generatedAllowances, YearMonth yearMonth)
            throws IOException {
        if (generatedAllowances == null || generatedAllowances.isEmpty()) {
            return new byte[0]; // Return empty byte array if no allowances were generated
        }

        // Group allowances by intern and sum the amounts
        Map<Intern, BigDecimal> summaryMap = generatedAllowances.stream()
                .collect(Collectors.groupingBy(
                        Allowance::getIntern,
                        Collectors.mapping(Allowance::getAmount,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook
                .createSheet("Allowance Summary " + yearMonth.format(DateTimeFormatter.ofPattern("MM-yyyy")));

        // Create header row
        String[] headers = { "Intern ID", "Intern Name", "Team Name", "Total Allowance" };
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        // Populate data rows
        int rowNum = 1;
        for (Map.Entry<Intern, BigDecimal> entry : summaryMap.entrySet()) {
            Intern intern = entry.getKey();
            BigDecimal totalAmount = entry.getValue();

            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(intern.getId());
            row.createCell(1).setCellValue(intern.getUser() != null ? intern.getUser().getFullName() : "N/A");
            row.createCell(2).setCellValue(intern.getTeam() != null ? intern.getTeam().getName() : "N/A");
            row.createCell(3).setCellValue(totalAmount.doubleValue());
        }

        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        return outputStream.toByteArray();
    }

}
