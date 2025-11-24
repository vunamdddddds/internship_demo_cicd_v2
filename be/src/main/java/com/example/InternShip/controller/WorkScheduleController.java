package com.example.InternShip.controller;

import com.example.InternShip.dto.workSchedule.request.CreateWorkScheduleRequest;
import com.example.InternShip.dto.workSchedule.request.UpdateWorkScheduleRequest;
import com.example.InternShip.service.WorkScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/workSchedules")
@RequiredArgsConstructor
public class WorkScheduleController {
    private final WorkScheduleService workScheduleService;

    @GetMapping("/{teamId}")
    public ResponseEntity<?> getWorkSchedule(@PathVariable Integer teamId) {
        return ResponseEntity.ok(workScheduleService.getWorkSchedule(teamId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSchedule(
            @PathVariable Integer id,
            @RequestBody @Valid UpdateWorkScheduleRequest request) {
        return ResponseEntity.ok(workScheduleService.updateSchedule(id, request));
    }

    @PostMapping
    public ResponseEntity<?> createSchedule(@RequestBody @Valid CreateWorkScheduleRequest request) {
        return ResponseEntity.ok(workScheduleService.createSchedule(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable int id) {
        workScheduleService.deleteSchedule(id);
        return ResponseEntity.ok().build();
    }
}