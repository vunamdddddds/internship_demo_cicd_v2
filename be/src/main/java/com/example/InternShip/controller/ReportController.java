package com.example.InternShip.controller;

import com.example.InternShip.dto.report.response.AttendanceSummaryResponse;
import com.example.InternShip.dto.report.response.FinalAttendanceResponse;
import com.example.InternShip.dto.report.response.FinalReportResponse;
import com.example.InternShip.dto.report.response.InternAttendanceDetailResponse;
import com.example.InternShip.dto.response.PagedResponse;
import com.example.InternShip.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/attendance-summary")
    public ResponseEntity<List<AttendanceSummaryResponse>> getAttendanceSummaryReport(
            @RequestParam(required = false) Integer teamId,
            @RequestParam(required = false) Integer internshipProgramId) {

        List<AttendanceSummaryResponse> report = reportService.getAttendanceSummaryReport(teamId, internshipProgramId);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/interns/{internId}/attendance")
    public ResponseEntity<InternAttendanceDetailResponse> getInternAttendanceDetail(
            @PathVariable Integer internId,
            @RequestParam("internshipProgramId") Integer internshipProgramId) {

        // Đã bỏ startDate và endDate
        InternAttendanceDetailResponse report = reportService.getInternAttendanceDetail(internId, internshipProgramId);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/final-report")
    public ResponseEntity<PagedResponse<FinalReportResponse>> getFinalReport(
            @RequestParam(required = false) Integer internshipProgramId,
            @RequestParam(required = false) Integer universityId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<FinalReportResponse> reportPage = reportService.getFinalReport(internshipProgramId, universityId, pageable);

        PagedResponse<FinalReportResponse> response = new PagedResponse<>(
                reportPage.getContent(),
                reportPage.getNumber(),
                reportPage.getTotalElements(),
                reportPage.getTotalPages(),
                reportPage.hasNext(),
                reportPage.hasPrevious()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/interns/{internId}/full-history")
    public ResponseEntity<List<FinalAttendanceResponse>> getFinalAttendance(
            @PathVariable Integer internId,
            @RequestParam("internshipProgramId") Integer internshipProgramId) {

        List<FinalAttendanceResponse> history = reportService.getFinalAttendance(internId, internshipProgramId);
        return ResponseEntity.ok(history);
    }
}