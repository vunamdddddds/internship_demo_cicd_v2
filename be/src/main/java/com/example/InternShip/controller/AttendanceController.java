package com.example.InternShip.controller;

import com.example.InternShip.dto.attendance.response.GetMyScheduleResponse;
import com.example.InternShip.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/attendances")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    // @PreAuthorize("hasAuthority('SCOPE_INTERN')")
    @PostMapping("/check-in")
    public ResponseEntity<GetMyScheduleResponse> checkIn() {
        return ResponseEntity.ok(attendanceService.checkIn());
    }

    @PutMapping("/check-out")
    // @PreAuthorize("hasAuthority('SCOPE_INTERN')")
    public ResponseEntity<GetMyScheduleResponse> checkOut() {
        return ResponseEntity.ok(attendanceService.checkOut());
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMySchedule() {
        return ResponseEntity.ok(attendanceService.getMySchedule());
    }

    @GetMapping("/{teamId}")
    public ResponseEntity<?> getTeamSchedule(@PathVariable int teamId) {
        return ResponseEntity.ok(attendanceService.getTeamSchedule(teamId));
    }
}
