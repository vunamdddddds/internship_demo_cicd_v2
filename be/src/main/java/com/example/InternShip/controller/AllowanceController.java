package com.example.InternShip.controller;

import com.example.InternShip.dto.AllowanceResponse;
import com.example.InternShip.dto.request.AllowanceRequest;
import com.example.InternShip.service.AllowanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.InternShip.dto.response.PagedResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/allowances")
@RequiredArgsConstructor
public class AllowanceController {

    private final AllowanceService allowanceService;

    @GetMapping
    public ResponseEntity<PagedResponse<AllowanceResponse>> getAllAllowances(
            @RequestParam(required = false) Long internshipProgramId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            Pageable pageable) {
        PagedResponse<AllowanceResponse> allowances = allowanceService.getAllAllowances(internshipProgramId, keyword, status, pageable);
        return ResponseEntity.ok(allowances);
    }

    @PutMapping("/{id}/transfer")
    public ResponseEntity<AllowanceResponse> transferAllowance(@PathVariable("id") long id) {
        AllowanceResponse updatedAllowance = allowanceService.transferAllowance(id);
        return ResponseEntity.ok(updatedAllowance);
    }

    @GetMapping("/my-history")
    public ResponseEntity<PagedResponse<AllowanceResponse>> getMyHistory(
            Pageable pageable) {
        PagedResponse<AllowanceResponse> allowances = allowanceService.getMyAllowances( pageable);
        return ResponseEntity.ok(allowances);
    }

    @PostMapping
    public ResponseEntity<AllowanceResponse> createAllowance(@Valid @RequestBody AllowanceRequest request) {
        AllowanceResponse newAllowance = allowanceService.createAllowance(request);
        return new ResponseEntity<>(newAllowance, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelAllowance(@PathVariable("id") long id) {
        allowanceService.cancelAllowance(id);
        return ResponseEntity.noContent().build();
    }
}
