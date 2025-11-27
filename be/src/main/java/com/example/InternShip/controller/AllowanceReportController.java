package com.example.InternShip.controller;
import com.example.InternShip.entity.Allowance;
import com.example.InternShip.entity.MonthlyAllowanceReport;
import com.example.InternShip.service.AllowanceReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
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
@RequestMapping("/api/v1/hr/allowance/reports") // Corrected base path
public class AllowanceReportController {

    @Autowired
    private AllowanceReportService allowanceReportService;

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportAllowanceReport(@RequestParam("month") String month) {
        try {
            YearMonth yearMonth = YearMonth.parse(month, DateTimeFormatter.ofPattern("yyyy-MM"));
            byte[] excelBytes = allowanceReportService.generateAllowanceReport(yearMonth);

            String fileName = "bao_cao_chi_tiet_phu_cap_thang_" + month + ".xlsx";
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");
            headers.add(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelBytes);
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().build();
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/details")
    public ResponseEntity<List<Allowance>> getAllowanceDetails(@RequestParam("month") String month) {
        try {
            YearMonth yearMonth = YearMonth.parse(month, DateTimeFormatter.ofPattern("yyyy-MM"));
            List<Allowance> allowances = allowanceReportService.getAllowanceDetailsForMonth(yearMonth);
            return ResponseEntity.ok(allowances);
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<Page<MonthlyAllowanceReport>> getGeneratedReports(Pageable pageable) {
        Page<MonthlyAllowanceReport> reports = allowanceReportService.getAllMonthlyReports(pageable);
        return ResponseEntity.ok(reports);
    }
}
