package com.example.InternShip.controller;

import com.example.InternShip.dto.intern.request.CreateInternRequest;
import com.example.InternShip.dto.intern.request.GetAllInternRequest;
import com.example.InternShip.dto.intern.request.UpdateInternRequest;
import com.example.InternShip.dto.intern.response.GetInternResponse;
import com.example.InternShip.dto.intern.response.MyProfileResponse;
import com.example.InternShip.dto.response.PagedResponse;
import com.example.InternShip.dto.workSchedule.response.WorkScheduleResponse;
import com.example.InternShip.service.InternService;
import com.example.InternShip.service.WorkScheduleService; // Added

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;

import org.springframework.web.bind.annotation.PathVariable;

import java.util.List; // Added

@RestController
@RequestMapping("/api/v1/interns")
@RequiredArgsConstructor
public class InternController {
    private final InternService internService;
    private final WorkScheduleService workScheduleService; // Added

    @PutMapping("/{id}")
    public ResponseEntity<GetInternResponse> UpdateInternById(@PathVariable Integer id,
            @RequestBody @Valid UpdateInternRequest updateInternRequest) {
        return ResponseEntity.ok(internService.updateIntern(id, updateInternRequest));
    }

    @PostMapping
    public ResponseEntity<GetInternResponse> createIntern(@Valid @RequestBody CreateInternRequest request) {
        return ResponseEntity.ok(internService.createIntern(request));
    }

    @GetMapping
    public ResponseEntity<PagedResponse<GetInternResponse>> getAllIntern(GetAllInternRequest request) {
        return ResponseEntity.ok(internService.getAllIntern(request));
    }

    @GetMapping("/{teamId}")
    public ResponseEntity<?> getAllInternNoTeam(@PathVariable Integer teamId) {
        return ResponseEntity.ok(internService.getAllInternNoTeam(teamId));
    }

    @GetMapping("/in/{teamId}")
    public ResponseEntity<?> getAllInternByTeamId(@PathVariable Integer teamId) {
        return ResponseEntity.ok(internService.getAllInternByTeamId(teamId));
    }

    @GetMapping("/my-team-schedule") // New endpoint
    public ResponseEntity<List<WorkScheduleResponse>> getMyTeamSchedule() {
        Integer internTeamId = internService.getAuthenticatedInternTeamId();

        if (internTeamId == null) {
            // If the intern is not associated with a team or not found, return 404 Not
            // Found
            // Or 403 Forbidden, depending on desired error handling
            return ResponseEntity.notFound().build();
        }

        List<WorkScheduleResponse> teamSchedule = workScheduleService.getWorkSchedule(internTeamId);
        return ResponseEntity.ok(teamSchedule);
    }

    @GetMapping("/me")
    // @PreAuthorize("hasAuthority('SCOPE_INTERN')")
    public ResponseEntity<MyProfileResponse> getMyProfile() {
        return ResponseEntity.ok(internService.getMyProfile());
    }
}
