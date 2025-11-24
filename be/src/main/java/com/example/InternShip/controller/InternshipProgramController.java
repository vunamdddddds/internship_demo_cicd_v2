package com.example.InternShip.controller;

import jakarta.validation.Valid;
import org.quartz.SchedulerException;

import com.example.InternShip.dto.internshipProgram.request.CreateInternProgramRequest;
import com.example.InternShip.dto.internshipProgram.request.UpdateInternProgramRequest;
import com.example.InternShip.service.InternshipProgramService;
import lombok.RequiredArgsConstructor;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/internship-programs")
@RequiredArgsConstructor
public class InternshipProgramController {

    private final InternshipProgramService internshipProgramService;

    @GetMapping // Cái này chắc là cho bên client
    public ResponseEntity<?> getAllPrograms() {
        return ResponseEntity.ok(internshipProgramService.getAllPrograms());
    }

    @GetMapping("/get") // Hàm lấy ra các chương trình thực tập (Cái này cho bên Manager)
    public ResponseEntity<?> getAllInternshipPrograms(
            @RequestParam(required = false, defaultValue = "") List<Integer> department,
            @RequestParam(required = false, defaultValue = "") String keyword,
            @RequestParam(required = false, defaultValue = "1") int page) {

        return ResponseEntity.ok(internshipProgramService.getAllInternshipPrograms(
                department,
                keyword,
                page));
    }

    @PostMapping
    public ResponseEntity<?> createInternProgram(@RequestBody @Valid CreateInternProgramRequest request)
            throws SchedulerException {
        return ResponseEntity.ok(internshipProgramService.createInternProgram(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateInternProgram(@RequestBody @Valid UpdateInternProgramRequest request,
            @PathVariable int id) throws SchedulerException {
        return ResponseEntity.ok(internshipProgramService.updateInternProgram(request, id));
    }

    @PatchMapping("/cancel/{id}")
    public ResponseEntity<?> cancelInternProgram(@PathVariable int id) throws SchedulerException {
        return ResponseEntity.ok(internshipProgramService.cancelInternProgram(id));
    }

    @PatchMapping("/publish/{id}")
    public ResponseEntity<?> publishInternProgram(@PathVariable int id) throws SchedulerException {
        return ResponseEntity.ok(internshipProgramService.publishInternProgram(id));
    }
}
