package com.example.InternShip.service;

import java.util.List;

import com.example.InternShip.dto.report.response.AttendanceSummaryResponse;
import com.example.InternShip.dto.report.response.FinalAttendanceResponse;
import com.example.InternShip.dto.report.response.FinalReportResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.example.InternShip.dto.report.response.InternAttendanceDetailResponse;

public interface ReportService {
    List<AttendanceSummaryResponse> getAttendanceSummaryReport(Integer teamId, Integer internshipProgramId);

    InternAttendanceDetailResponse getInternAttendanceDetail(Integer internId, Integer internshipProgramId);

    Page<FinalReportResponse> getFinalReport(Integer programId, Integer universityId, Pageable pageable);

    List<FinalAttendanceResponse> getFinalAttendance(Integer internId, Integer internshipProgramId);
}