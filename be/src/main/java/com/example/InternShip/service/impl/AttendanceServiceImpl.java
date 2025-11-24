package com.example.InternShip.service.impl;

import com.example.InternShip.dto.attendance.response.GetAllAttendanceResponse;
import com.example.InternShip.dto.attendance.response.GetMyScheduleResponse;
import com.example.InternShip.dto.attendance.response.GetTeamScheduleResponse;
import com.example.InternShip.entity.*;
import com.example.InternShip.exception.ErrorCode;
import com.example.InternShip.repository.AttendanceRepository;
import com.example.InternShip.repository.InternRepository;
import com.example.InternShip.repository.LeaveRequestRepository;
import com.example.InternShip.repository.TeamRepository;
import com.example.InternShip.repository.WorkScheduleRepository;
import com.example.InternShip.service.AttendanceService;
import com.example.InternShip.service.AuthService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final InternRepository internRepository;
    private final WorkScheduleRepository workScheduleRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final TeamRepository teamRepository;
    private final AuthService authService;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public GetMyScheduleResponse checkIn() {
        User user = authService.getUserLogin();

        Intern intern = internRepository.findByUser(user)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.INTERN_NOT_FOUND.getMessage()));

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        // Kiểm tra xem đã check-in hôm nay chưa
        if (attendanceRepository.findByInternAndDate(intern, today).isPresent()) {
            throw new IllegalStateException(ErrorCode.ALREADY_CHECKED_IN_TODAY.getMessage());
        }
        // Lấy lịch làm việc của nhóm
        WorkSchedule schedule = getWorkScheduleForIntern(intern, today.getDayOfWeek());

        if (now.isAfter(schedule.getTimeEnd()) ||
                now.isBefore(schedule.getTimeStart().minusMinutes(15))) {
            throw new IllegalStateException(ErrorCode.CANNOT_CHECK_IN.getMessage());
        }

        // Tạo bản ghi chấm công mới
        Attendance newAttendance = new Attendance();
        newAttendance.setIntern(intern);
        newAttendance.setTeam(intern.getTeam());
        newAttendance.setDate(today);
        newAttendance.setCheckIn(now);
        newAttendance.setStatus(Attendance.Status.CHECKED_IN); // Trạng thái ban đầu

        // Lưu lại giờ làm việc dự kiến
        newAttendance.setTimeStart(schedule.getTimeStart());
        newAttendance.setTimeEnd(schedule.getTimeEnd());

        Attendance savedAttendance = attendanceRepository.save(newAttendance);
        return mapToResponse(savedAttendance);
    }

    @Override
    @Transactional
    public GetMyScheduleResponse checkOut() {
        User user = authService.getUserLogin();

        Intern intern = internRepository.findByUser(user)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.INTERN_NOT_FOUND.getMessage()));

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        // Tìm bản ghi check-in của hôm nay
        Attendance attendance = attendanceRepository.findByInternAndDate(intern, today)
                .orElseThrow(() -> new IllegalStateException(ErrorCode.NOT_CHECKED_IN_TODAY.getMessage()));

        // Cập nhật tg check-out
        attendance.setCheckOut(now);

        // Tính toán và xét trạng thái cuối cùng
        Attendance.Status finalStatus = calculateAttendanceStatus(intern, attendance);
        attendance.setStatus(finalStatus);

        Attendance savedAttendance = attendanceRepository.save(attendance);
        return mapToResponse(savedAttendance);
    }

    // Lấy lịch làm việc của Intern (theo nhóm)
    private WorkSchedule getWorkScheduleForIntern(Intern intern, DayOfWeek day) {
        if (intern.getTeam() == null) {
            throw new IllegalStateException(ErrorCode.INTERN_NOT_TEAM.getMessage());
        }
        return workScheduleRepository.findByTeamAndDayOfWeek(intern.getTeam(), day)
                .orElseThrow(() -> new IllegalStateException(ErrorCode.SCHEDULE_NOT_SET_TODAY.getMessage()));
    }

    private Attendance.Status calculateAttendanceStatus(Intern intern, Attendance attendance) {
        LocalTime checkIn = attendance.getCheckIn();
        LocalTime checkOut = attendance.getCheckOut();

        // Lấy giờ làm việc dự kiến đã được lưu lúc check-in
        LocalTime expectedStartTime = attendance.getTimeStart();
        LocalTime expectedEndTime = attendance.getTimeEnd();

        LocalTime allowedCheckInTime = expectedStartTime.plusMinutes(30);

        // Kiểm tra đơn nghỉ phép (chỉ lấy đơn đã APPROVED)
        Optional<LeaveRequest> leaveOpt = leaveRequestRepository.findByInternAndDateAndApproved(
                intern, attendance.getDate(), true);

        if (leaveOpt.isPresent()) {
            LeaveRequest leave = leaveOpt.get();
            // Nghỉ cả ngày
            if (leave.getType() == LeaveRequest.Type.ON_LEAVE) {
                return Attendance.Status.ON_LEAVE;
            }
            // Xin đi muộn
            if (leave.getType() == LeaveRequest.Type.LATE) {
                // kiểm tra có về sớm không
                boolean isEarlyLeave = checkOut.isBefore(expectedEndTime);
                return isEarlyLeave ? Attendance.Status.EARLY_LEAVE : Attendance.Status.LATE;
            }
            // Xin về sớm
            if (leave.getType() == LeaveRequest.Type.EARLY_LEAVE) {
                // kiểm tra có đi muộn không
                boolean isLate = checkIn.isAfter(allowedCheckInTime);
                return isLate ? Attendance.Status.LATE : Attendance.Status.EARLY_LEAVE;
            }
        }

        // Nếu không có đơn, xét trạng thái dựa trên giờ
        boolean isLate = checkIn.isAfter(allowedCheckInTime);
        boolean isEarlyLeave = checkOut.isBefore(expectedEndTime);

        if (isLate && isEarlyLeave) {
            return Attendance.Status.LATE_AND_EARLY_LEAVE;
        } else if (isLate) {
            return Attendance.Status.LATE;
        } else if (isEarlyLeave) {
            return Attendance.Status.EARLY_LEAVE;
        } else {
            return Attendance.Status.PRESENT;
        }
    }

    // Phương thức tiện ích để map sang DTO
    private GetMyScheduleResponse mapToResponse(Attendance attendance) {
        GetMyScheduleResponse res = modelMapper.map(attendance, GetMyScheduleResponse.class);
        res.setTeam(attendance.getTeam().getName());
        return res;
    }

    public List<GetMyScheduleResponse> getMySchedule() {
        Intern intern = authService.getUserLogin().getIntern();
        if (intern == null) {
            throw new EntityNotFoundException(ErrorCode.INTERN_NOT_EXISTED.getMessage());
        }

        List<Attendance> attendances = intern.getAttendances();
        AtomicReference<LocalDate> date = new AtomicReference<>(LocalDate.now());

        // map attendance vào GetMyScheduleResponse
        List<GetMyScheduleResponse> responses = attendances.stream()
                .map(attendance -> {
                    if (attendance.getDate().isEqual(LocalDate.now())) {
                        date.set(LocalDate.now().plusDays(1));
                    }
                    return mapToResponse(attendance);
                }).collect(Collectors.toList());

        if (intern.getInternshipProgram().getStatus() == InternshipProgram.Status.ONGOING &&
                intern.getStatus() == Intern.Status.ACTIVE &&
                intern.getTeam() != null) {
            List<WorkSchedule> workSchedules = intern.getTeam().getWorkSchedules();

            // thêm dữ liệu giả vào responses
            for (int i = 0; i < 20; i++) {
                LocalDate currentDate = date.get().plusDays(i);

                // tìm ngày có thứ nằm trong workSchedules thì map và add vào responses
                workSchedules.stream()
                        .filter(ws -> ws.getDayOfWeek() == currentDate.getDayOfWeek())
                        .findFirst()
                        .ifPresent(ws -> {
                            GetMyScheduleResponse response = modelMapper.map(ws, GetMyScheduleResponse.class);
                            response.setTeam(ws.getTeam().getName());
                            response.setDate(currentDate);
                            responses.add(response);
                        });
            }
        }
        return responses;
    }

    public List<GetTeamScheduleResponse> getTeamSchedule(int teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.TEAM_NOT_EXISTED.getMessage()));

        List<WorkSchedule> workSchedules = team.getWorkSchedules();

        List<Attendance> attendances = attendanceRepository.findAllByTeamId(teamId);
        LocalDate date = LocalDate.now();

        Map<List<Object>, List<Attendance>> grouped = attendances.stream()
                .collect(Collectors.groupingBy(a -> List.of(a.getTimeStart(), a.getTimeEnd(), a.getDate())));

        List<GetTeamScheduleResponse> responses = new ArrayList<>();

        for (var entry : grouped.entrySet()) {
            List<Object> key = entry.getKey();
            List<Attendance> group = entry.getValue();

            GetTeamScheduleResponse response = new GetTeamScheduleResponse();
            response.setTimeStart((LocalTime) key.get(0));
            response.setTimeEnd((LocalTime) key.get(1));
            response.setDate((LocalDate) key.get(2));

            if (response.getDate().equals(date)) {
                date = date.plusDays(1);
            }

            // Map từng Attendance trong nhóm sang DetailTeamSchedule
            List<GetTeamScheduleResponse.DetailTeamSchedule> detailList = group.stream()
                    .map(attendance -> {
                        GetTeamScheduleResponse.DetailTeamSchedule detailTeamSchedule = modelMapper.map(attendance,
                                GetTeamScheduleResponse.DetailTeamSchedule.class);
                        detailTeamSchedule.setFullName(attendance.getIntern().getUser().getFullName());
                        detailTeamSchedule.setEmail(attendance.getIntern().getUser().getEmail());
                        return detailTeamSchedule;
                    })
                    .collect(Collectors.toList());

            response.setDetailTeamSchedules(detailList);
            responses.add(response);
        }

        if (team.getInternshipProgram().getStatus() == InternshipProgram.Status.ONGOING) {
            // thêm dữ liệu giả vào responses
            for (int i = 0; i < 20; i++) {
                LocalDate currentDate = date.plusDays(i);

                // tìm ngày có thứ nằm trong workSchedules thì map và add vào responses
                workSchedules.stream()
                        .filter(ws -> ws.getDayOfWeek() == currentDate.getDayOfWeek())
                        .findFirst()
                        .ifPresent(ws -> {
                            GetTeamScheduleResponse response = modelMapper.map(ws, GetTeamScheduleResponse.class);
                            response.setDate(currentDate);
                            responses.add(response);
                        });
            }
        }
        return responses;
    }

    @Scheduled(cron = "0 30 17 * * ?")
    public void checkAttendance() {
        LocalDate today = LocalDate.now();
        List<Intern> interns = internRepository.findAllByStatus(Intern.Status.ACTIVE);

        for (Intern intern : interns) {
            if (intern.getTeam() == null || intern.getTeam().getWorkSchedules() == null ||
                    intern.getTeam().getWorkSchedules().isEmpty()) {
                continue;
            }

            List<WorkSchedule> workSchedules = intern.getTeam().getWorkSchedules();

            // Tìm xem hôm nay có lịch ko
            WorkSchedule todaySchedule = workSchedules.stream()
                    .filter(ws -> ws.getDayOfWeek() == today.getDayOfWeek())
                    .findFirst()
                    .orElse(null);
            if (todaySchedule == null)
                continue;

            // Kiểm tra xem intern đã có attendance hôm nay chưa
            boolean hasAttendanceToday = intern.getAttendances().stream()
                    .anyMatch(at -> today.equals(at.getDate()));

            if (!hasAttendanceToday) {
                Attendance attendance = new Attendance();
                attendance.setDate(today);
                attendance.setIntern(intern);
                attendance.setStatus(Attendance.Status.ABSENT);
                attendance.setTimeStart(todaySchedule.getTimeStart());
                attendance.setTimeEnd(todaySchedule.getTimeEnd());
                attendanceRepository.save(attendance);
            }
        }
    }
}
