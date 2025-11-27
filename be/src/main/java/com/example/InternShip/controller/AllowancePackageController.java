package com.example.InternShip.controller;

import com.example.InternShip.dto.allowancepackage.request.CreateAllowancePackageRequest;
import com.example.InternShip.dto.allowancepackage.response.AllowancePackageResponse;
import com.example.InternShip.service.AllowancePackageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/api/v1/hr/allowance-packages")
public class AllowancePackageController {

    @Autowired
    private AllowancePackageService allowancePackageService;

    @PostMapping
    public ResponseEntity<AllowancePackageResponse> createAllowancePackage(@Valid @RequestBody CreateAllowancePackageRequest request) {
        AllowancePackageResponse createdPackage = allowancePackageService.createAllowancePackage(request);
        return new ResponseEntity<>(createdPackage, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<AllowancePackageResponse>> getAllAllowancePackages(
            @RequestParam(required = false) String keyword,
            Pageable pageable) {
        Page<AllowancePackageResponse> allowancePackages = allowancePackageService.getAllAllowancePackages(keyword, pageable);
        return ResponseEntity.ok(allowancePackages);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AllowancePackageResponse> getAllowancePackageById(@PathVariable Integer id) {
        AllowancePackageResponse allowancePackage = allowancePackageService.getAllowancePackageById(id);
        return ResponseEntity.ok(allowancePackage);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AllowancePackageResponse> updateAllowancePackage(@PathVariable Integer id, @Valid @RequestBody CreateAllowancePackageRequest request) {
        AllowancePackageResponse updatedPackage = allowancePackageService.updateAllowancePackage(id, request);
        return ResponseEntity.ok(updatedPackage);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAllowancePackage(@PathVariable Integer id) {
        allowancePackageService.deleteAllowancePackage(id);
        return ResponseEntity.noContent().build();
    }
}
