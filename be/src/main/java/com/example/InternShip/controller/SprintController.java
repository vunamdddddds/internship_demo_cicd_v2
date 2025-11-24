package com.example.InternShip.controller;

import com.example.InternShip.dto.sprint.request.CreateSprintRequest;
import com.example.InternShip.dto.sprint.request.EvaluateSprintRequest;
import com.example.InternShip.dto.sprint.request.UpdateSprintRequest;
import com.example.InternShip.dto.sprint.response.SprintReportResponse;
import com.example.InternShip.service.SprintService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class SprintController {
    private final SprintService sprintService;

    @PostMapping("/teams/{teamId}/sprints")
    public ResponseEntity<?> createSprint(@PathVariable Integer teamId, @RequestBody CreateSprintRequest request) {
        return ResponseEntity.ok(sprintService.createSprint(teamId, request));
    }

    @GetMapping("/teams/{teamId}/sprints")
    public ResponseEntity<?> getSprintsByTeam(@PathVariable Integer teamId) {
        return ResponseEntity.ok(sprintService.getSprintsByTeam(teamId));
    }

    @GetMapping("/sprints/{sprintId}")
    public ResponseEntity<?> getSprintById(@PathVariable Long sprintId) {
        return ResponseEntity.ok(sprintService.getSprintById(sprintId));
    }

    @PutMapping("/sprints/{sprintId}")
    public ResponseEntity<?> updateSprint(@PathVariable Long sprintId, @RequestBody UpdateSprintRequest request) {
        return ResponseEntity.ok(sprintService.updateSprint(sprintId, request));
    }

    @DeleteMapping("/sprints/{sprintId}")
    public ResponseEntity<?> deleteSprint(@PathVariable Long sprintId) {
        sprintService.deleteSprint(sprintId);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/{id}/report", consumes = "multipart/form-data")
    public ResponseEntity<SprintReportResponse> submitSprintReport(
            @PathVariable("id") Long id,
            @RequestParam("file") MultipartFile file) {

        SprintReportResponse response = sprintService.submitReport(id, file);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/sprints/evaluate/{sprintId}")
    public ResponseEntity<Void> evaluateSprint(
            @PathVariable Long sprintId,
            @RequestBody @Valid EvaluateSprintRequest request) {

        sprintService.evaluateSprint(sprintId, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/sprints/evaluate/{sprintId}")
    public ResponseEntity<?> getEvaluateSprint(@PathVariable Long sprintId) {
        return ResponseEntity.ok(sprintService.getEvaluateSprint(sprintId));
    }
}
