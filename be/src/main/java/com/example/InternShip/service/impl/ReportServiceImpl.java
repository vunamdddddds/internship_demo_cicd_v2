package com.example.InternShip.service.impl;

import com.example.InternShip.dto.report.response.AttendanceSummaryResponse;
import com.example.InternShip.dto.report.response.FinalAttendanceResponse;
import com.example.InternShip.dto.report.response.FinalReportResponse;
import com.example.InternShip.dto.report.response.InternAttendanceDetailResponse;
import com.example.InternShip.entity.Attendance;
import com.example.InternShip.entity.Intern;
import com.example.InternShip.entity.InternshipProgram;
import com.example.InternShip.entity.LeaveRequest;
import com.example.InternShip.exception.ErrorCode;
import com.example.InternShip.repository.AttendanceRepository;
import com.example.InternShip.repository.InternRepository;
import com.example.InternShip.repository.InternshipProgramRepository;
import com.example.InternShip.repository.LeaveRequestRepository;
import com.example.InternShip.service.ReportService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final AttendanceRepository attendanceRepository;
    private final InternRepository internRepository;
    private final InternshipProgramRepository internshipProgramRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<AttendanceSummaryResponse> getAttendanceSummaryReport(Integer teamId, Integer internshipProgramId) {

        return this.fetchAndMapSummaryData(teamId, internshipProgramId);
    }

    @Override
    public InternAttendanceDetailResponse getInternAttendanceDetail(Integer internId, Integer internshipProgramId) {

        //Lấy thông tin Intern
        Intern intern = internRepository.findById(internId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.INTERN_NOT_FOUND.getMessage()));

        //Lấy thông tin Kỳ thực tập để có startDate và endDate
        InternshipProgram program = internshipProgramRepository.findById(internshipProgramId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.INTERNSHIP_PROGRAM_NOT_EXISTED.getMessage()));

        //Lấy ngày bắt đầu và kết thúc từ kỳ thực tập
        LocalDate startDate = program.getTimeStart().toLocalDate();
        LocalDate endDate = program.getTimeEnd().toLocalDate();

        //Lịch sử chấm công (dùng startDate, endDate)
        List<Attendance> attendanceLogs = attendanceRepository.findByInternAndDateBetweenOrderByDateAsc(intern, startDate, endDate);

        //Lịch sử nghỉ phép (dùng startDate, endDate)
        List<LeaveRequest> leaveRequestLogs = leaveRequestRepository.findByInternAndDateBetweenOrderByDateAsc(intern, startDate, endDate);

        //Map dữ liệu Lịch sử chấm công
        List<InternAttendanceDetailResponse.DailyLogEntry> dailyLogs = attendanceLogs.stream()
                .map(log -> {
                    InternAttendanceDetailResponse.DailyLogEntry entry = new InternAttendanceDetailResponse.DailyLogEntry();
                    entry.setDate(log.getDate());
                    entry.setExpectedTimeStart(log.getTimeStart());
                    entry.setExpectedTimeEnd(log.getTimeEnd());
                    entry.setActualCheckIn(log.getCheckIn());
                    entry.setActualCheckOut(log.getCheckOut());
                    entry.setStatus(log.getStatus().name());
                    return entry;
                }).collect(Collectors.toList());

        //Map dữ liệu Lịch sử nghỉ phép
        List<InternAttendanceDetailResponse.LeaveLogEntry> leaveLogs = leaveRequestLogs.stream()
                .map(log -> {
                    InternAttendanceDetailResponse.LeaveLogEntry entry = new InternAttendanceDetailResponse.LeaveLogEntry();
                    entry.setDate(log.getDate());
                    entry.setType(log.getType().name());
                    entry.setReason(log.getReason());

                    if (log.getApproved() == null) {
                        entry.setLeaveStatus("Chờ duyệt");
                    } else if (log.getApproved() == true) {
                        entry.setLeaveStatus("Đã duyệt");
                    } else {
                        entry.setLeaveStatus("Đã từ chối" + (log.getReasonReject() != null ? ": " + log.getReasonReject() : ""));
                    }
                    return entry;
                }).collect(Collectors.toList());

        //Tạo đối tượng Response hoàn chỉnh
        InternAttendanceDetailResponse response = new InternAttendanceDetailResponse();
        response.setInternId(intern.getId());
        response.setFullName(intern.getUser().getFullName());
        response.setEmail(intern.getUser().getEmail());
        response.setTeamName(intern.getTeam() != null ? intern.getTeam().getName() : ErrorCode.INTERN_NOT_IN_TEAM.getMessage());
        response.setDailyLogs(dailyLogs);
        response.setLeaveLogs(leaveLogs);

        return response;
    }

    private List<AttendanceSummaryResponse> fetchAndMapSummaryData(Integer teamId, Integer internshipProgramId) {

        //Lấy dữ liệu tổng hợp từ CSDL
        List<AttendanceRepository.AttendanceSummaryProjection> projections =
                attendanceRepository.getAttendanceSummary(teamId, internshipProgramId);

        List<Integer> internIds = projections.stream()
                .map(AttendanceRepository.AttendanceSummaryProjection::getInternId)
                .collect(Collectors.toList());

        if (internIds.isEmpty()) {
            return new ArrayList<>();
        }

        Map<Integer, Intern> internMap = internRepository.findAllById(internIds).stream()
                .collect(Collectors.toMap(Intern::getId, Function.identity()));

        return projections.stream().map(p -> {
            Intern intern = internMap.get(p.getInternId());
            AttendanceSummaryResponse res = new AttendanceSummaryResponse();
            res.setInternId(intern.getId());
            res.setFullName(intern.getUser().getFullName());
            res.setTeamName(intern.getTeam() != null ? intern.getTeam().getName() : ErrorCode.INTERN_NOT_IN_TEAM.getMessage());
            res.setTotalWorkingDays(p.getTotalWorkingDays());
            res.setTotalOnLeaveDays(p.getTotalOnLeaveDays());
            res.setTotalAbsentDays(p.getTotalAbsentDays());
            return res;
        }).collect(Collectors.toList());
    }

    @Override
    public Page<FinalReportResponse> getFinalReport(Integer programId, Integer universityId, Pageable pageable) {
        Page<Intern> internPage = internRepository.findFinalReport(programId, universityId, pageable);
        Map<Integer, AttendanceRepository.AttendanceSummaryProjection> summaryMap = getAttendanceSummaryMap(programId, null);
        return internPage.map(intern -> mapToFinalResponse(intern, summaryMap.get(intern.getId())));
    }

    private Map<Integer, AttendanceRepository.AttendanceSummaryProjection> getAttendanceSummaryMap(Integer programId, Integer teamId) {
        return attendanceRepository.getAttendanceSummary(teamId, programId)
                .stream()
                .collect(Collectors.toMap(
                        AttendanceRepository.AttendanceSummaryProjection::getInternId,
                        Function.identity(),
                        (existing, replacement) -> existing // Xử lý nếu có trùng lặp
                ));
    }

    @Override
    public List<FinalAttendanceResponse> getFinalAttendance(Integer internId, Integer internshipProgramId) {
        Intern intern = internRepository.findById(internId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.INTERN_NOT_FOUND.getMessage()));

        InternshipProgram program = internshipProgramRepository.findById(internshipProgramId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.INTERNSHIP_PROGRAM_NOT_EXISTED.getMessage()));

        LocalDate startDate = program.getTimeStart().toLocalDate();
        LocalDate endDate = program.getTimeEnd().toLocalDate();

        List<Attendance> attendanceLogs = attendanceRepository.findByInternAndDateBetweenOrderByDateAsc(intern, startDate, endDate);

        List<LeaveRequest> leaveRequestLogs = leaveRequestRepository.findByInternAndDateBetweenOrderByDateAsc(intern, startDate, endDate);

        long totalWorking = 0;
        long totalOnLeave = 0;
        long totalAbsent = 0;

        for (Attendance att : attendanceLogs) {
            String status = att.getStatus().name();

            if (status.equals("PRESENT") || status.equals("LATE") ||
                    status.equals("EARLY_LEAVE") || status.equals("LATE_AND_EARLY_LEAVE")) {
                totalWorking++;
            }
            if (status.equals("ON_LEAVE")) {
                totalOnLeave++;
            }
            if (status.equals("ABSENT")) {
                totalAbsent++;
            }
        }

        for(LeaveRequest lr : leaveRequestLogs) {
            String status = lr.getType().name();
            if(status.equals("LATE") || status.equals("EARLY_LEAVE")){
                totalWorking++;
            }
            if(status.equals("ON_LEAVE")){
                totalOnLeave++;
            }
        }

        Map<LocalDate, Attendance> attendanceMap = attendanceLogs.stream()
                .collect(Collectors.toMap(Attendance::getDate, Function.identity(), (a1, a2) -> a1));

        Map<LocalDate, LeaveRequest> leaveMap = leaveRequestLogs.stream()
                .collect(Collectors.toMap(LeaveRequest::getDate, Function.identity(), (l1, l2) -> l1));

        List<LocalDate> allDates = new ArrayList<>(attendanceMap.keySet());
        for (LocalDate date : leaveMap.keySet()) {
            if (!attendanceMap.containsKey(date)) {
                allDates.add(date);
            }
        }
        Collections.sort(allDates);

        long finalTotalWorking = totalWorking;
        long finalTotalOnLeave = totalOnLeave;
        long finalTotalAbsent = totalAbsent;

        return allDates.stream().map(date -> {
            FinalAttendanceResponse res = new FinalAttendanceResponse();
            res.setDate(date);

            Attendance att = attendanceMap.get(date);
            LeaveRequest leave = leaveMap.get(date);

            if (att != null) {
                res.setStatus(att.getStatus().name());
            } else if (leave != null) {
                res.setStatus(leave.getType().name());
            } else {
                res.setStatus("ABSENT");
            }

            if (leave != null) {
                String reason = leave.getReason();
                if (leave.getApproved() != null && !leave.getApproved()) {
                    reason += " (Từ chối: " + leave.getReasonReject() + ")";
                }
                res.setReason(reason);
            }

            res.setTotalWorkingDays(finalTotalWorking);
            res.setTotalOnLeaveDays(finalTotalOnLeave);
            res.setTotalAbsentDays(finalTotalAbsent);

            return res;
        }).collect(Collectors.toList());
    }

    private FinalReportResponse mapToFinalResponse(Intern intern, AttendanceRepository.AttendanceSummaryProjection summary) {
        FinalReportResponse res = modelMapper.map(intern, FinalReportResponse.class);
        res.setInternId(intern.getId());
        res.setFullName(intern.getUser().getFullName());
        res.setEmail(intern.getUser().getEmail());

        if (intern.getInternshipProgram() != null) {
            res.setInternshipProgramId(intern.getInternshipProgram().getId());
        }

        return res;
    }
}