package com.example.InternShip.controller;

import com.example.InternShip.dto.evaluation.request.EvaluateInternRequest;
import com.example.InternShip.dto.evaluation.response.EvaluationResponse;
import com.example.InternShip.service.EvaluationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.ByteArrayInputStream;

@RestController
@RequestMapping("/api/v1/evaluations")
@RequiredArgsConstructor
public class EvaluationController {

    private final EvaluationService evaluationService;

    @PutMapping("/interns/{id}")
    // @PreAuthorize("hasAuthority('SCOPE_MENTOR')")
    public ResponseEntity<EvaluationResponse> evaluateIntern(
            @PathVariable("id") Integer internId,
            @RequestBody @Valid EvaluateInternRequest request) {

        EvaluationResponse response = evaluationService.evaluateIntern(internId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/interns/{id}")
    // @PreAuthorize("hasAuthority('SCOPE_MENTOR') or hasAuthority('SCOPE_HR') or
    // @authService.isSelf(authentication, #internId)")
    public ResponseEntity<EvaluationResponse> getEvaluation(
            @PathVariable("id") Integer internId) {

        EvaluationResponse response = evaluationService.getEvaluation(internId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/export")
    // @PreAuthorize("hasAuthority('SCOPE_MENTOR') or hasAuthority('SCOPE_HR')")
    public ResponseEntity<Resource> exportEvaluations(
            @RequestParam(required = false) Integer teamId,
            @RequestParam(required = false) Integer programId) {

        ByteArrayInputStream data = evaluationService.exportEvaluations(teamId, programId);

        String filename = "BaoCaoDanhGiaThucTapSinh.xlsx";

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(data));
    }
}