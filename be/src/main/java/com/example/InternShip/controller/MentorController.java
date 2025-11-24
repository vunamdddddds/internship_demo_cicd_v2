package com.example.InternShip.controller;

import com.example.InternShip.dto.mentor.request.CreateMentorRequest;
import com.example.InternShip.dto.mentor.request.UpdateMentorRequest;
import com.example.InternShip.dto.mentor.response.GetMentorResponse;
import com.example.InternShip.dto.mentor.response.TeamResponse;
import com.example.InternShip.dto.sprint.response.SprintResponse;
import com.example.InternShip.service.MentorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/mentors")
@RequiredArgsConstructor
public class MentorController {
    private final MentorService mentorService;

    @PostMapping
    public ResponseEntity<GetMentorResponse> createMentor(@Valid @RequestBody CreateMentorRequest request) {
        return ResponseEntity.ok(mentorService.createMentor(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GetMentorResponse> updateMentorDepartment(
            @PathVariable("id") Integer id,
            @RequestBody @Valid UpdateMentorRequest request) {
        return ResponseEntity.ok(mentorService.updateMentorDepartment(id, request));
    }

    @GetMapping // Hàm lấy ra danh sách mentor
    public ResponseEntity<?> getAll(
            @RequestParam(required = false, defaultValue = "") List<Integer> department,
            @RequestParam(required = false, defaultValue = "") String keyword, // fullName, Email, Phone, Address
            @RequestParam(required = false, defaultValue = "1") int page) {

        return ResponseEntity.ok(mentorService.getAll(
                department,
                keyword,
                page));
    }

    @GetMapping("/getAll")
    public ResponseEntity<?> getAllMentor() {
        return ResponseEntity.ok(mentorService.getAllMentor());
    }

    @GetMapping("/me/sprints")
    public ResponseEntity<List<SprintResponse>> getSprintsForCurrentUser() {
        return ResponseEntity.ok(mentorService.getSprintsForCurrentUser());
    }

    @GetMapping("/me/teams")
    public ResponseEntity<List<TeamResponse>> getTeamsForCurrentUser() {
        return ResponseEntity.ok(mentorService.getTeamsForCurrentUser());
    }
}