package com.example.InternShip.controller;

import com.example.InternShip.entity.MonthlyAllowanceReport;
import com.example.InternShip.service.AllowanceReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/api/hr/allowance/reports")
public class AllowanceReportController {

    @Autowired
    private AllowanceReportService allowanceReportService;

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportAllowanceReport(
            @RequestParam(name = "month") String monthYearString) {
        try {
            YearMonth yearMonth = YearMonth.parse(monthYearString, DateTimeFormatter.ofPattern("MM-yyyy"));
            byte[] excelBytes = allowanceReportService.generateAllowanceReport(yearMonth);

            String fileName = "AllowanceReport_" + monthYearString + ".xlsx";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(excelBytes);

        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body(("Invalid month format. Please use MM-yyyy (e.g., 11-2025). Error: " + e.getMessage()).getBytes());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(("Error generating Excel report: " + e.getMessage()).getBytes());
        }
    }

    @GetMapping
    public ResponseEntity<List<MonthlyAllowanceReport>> getGeneratedReports() {
        List<MonthlyAllowanceReport> reports = allowanceReportService.getAllMonthlyReports();
        return ResponseEntity.ok(reports);
    }
}
