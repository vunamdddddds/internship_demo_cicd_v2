package com.example.InternShip.controller;

import com.example.InternShip.dto.application.request.ApplicationRequest;
import com.example.InternShip.dto.application.request.HandleApplicationRequest;
import com.example.InternShip.dto.application.request.SubmitApplicationContractRequest;
import com.example.InternShip.dto.application.response.ApplicationResponse;
import com.example.InternShip.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping(consumes = "multipart/form-data")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApplicationResponse> submit(
            @ModelAttribute @Valid ApplicationRequest request) {
        return ResponseEntity.ok(applicationService.submitApplication(request));
    }

    @PutMapping(consumes = "multipart/form-data", value = "/submit-contract")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> submitApplicationContract(
            @ModelAttribute @Valid SubmitApplicationContractRequest request) {
        applicationService.submitApplicationContract(request);
        return ResponseEntity.ok("Success");
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ApplicationResponse>> getMyApplication() {
        List<ApplicationResponse> resp = applicationService.getMyApplication();
        if (resp == null) {
            return ResponseEntity.ok().build(); // 200 với body rỗng (frontend nhận null/undefined)
        }
        return ResponseEntity.ok(resp);
    }

    @GetMapping // Hàm lấy ra danh sách đơn xin thực tập
    // @PreAuthorize("hasAuthority('SCOPE_HR', 'SCOPE_ADMIN')")
    public ResponseEntity<?> getAllApplication(
            @RequestParam(required = false, defaultValue = "") Integer internshipTerm,
            @RequestParam(required = false, defaultValue = "") Integer university,
            @RequestParam(required = false, defaultValue = "") Integer major,
            @RequestParam(required = false, defaultValue = "") String keyword,
            @RequestParam(required = false, defaultValue = "") String status,
            @RequestParam(required = false, defaultValue = "1") int page) {

        return ResponseEntity.ok(applicationService.getAllApplication(
                internshipTerm,
                university,
                major,
                keyword,
                status,
                page));
    }

    @PutMapping("/withdraw/{applicationId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> withdrawApplication(@PathVariable Integer applicationId) {
        applicationService.withdrawApplication(applicationId);
        return ResponseEntity.ok("Application withdrawn successfully");
    }

    @PatchMapping("/status")
    public ResponseEntity<Void> handleApplicationAction(@RequestBody @Valid HandleApplicationRequest request) {
        applicationService.handleApplicationAction(request);
        return ResponseEntity.ok(null);
    }
}
