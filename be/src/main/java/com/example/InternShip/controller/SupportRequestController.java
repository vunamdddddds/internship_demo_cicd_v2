package com.example.InternShip.controller;

import com.example.InternShip.dto.supportRequest.request.UpdateSupportRequestRequest;
import com.example.InternShip.dto.supportRequest.response.GetSupportRequestResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.InternShip.dto.supportRequest.request.CreateSupportRequestRequest;
import com.example.InternShip.dto.supportRequest.request.RejectSupportRequestRequest;
import com.example.InternShip.service.SupportRequestService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/api/v1/support-request")
@RequiredArgsConstructor
public class SupportRequestController {
    private final SupportRequestService supportRequestService;

    // Tạo đơn hỗ trợ
    @PostMapping(consumes = "multipart/form-data")
    //@PreAuthorize("hasAuthority('SCOPE_INTERN')")
    public ResponseEntity<?> createSupportRequest(@ModelAttribute @Valid CreateSupportRequestRequest request) {
        return ResponseEntity.ok(supportRequestService.createSupportRequest(request));
    }

    //Lấy danh sách yêu cầu
    @GetMapping("/me")
    //@PreAuthorize("hasAuthority('SCOPE_INTERN')")
    public ResponseEntity<List<GetSupportRequestResponse>> getMyList() {
        return ResponseEntity.ok(supportRequestService.getMyList());
    }

    //Sửa yêu cầu
    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    //@PreAuthorize("hasAuthority('SCOPE_INTERN')")
    public ResponseEntity<?> updateSupportRequest(
            @PathVariable Integer id,
            @ModelAttribute @Valid UpdateSupportRequestRequest request) {
        return ResponseEntity.ok(supportRequestService.updateRequest(id, request));
    }

    //Huỷ yêu cầu
    @DeleteMapping("/cancel/{supportId}")
    //@PreAuthorize("hasAuthority('SCOPE_INTERN')")
    public ResponseEntity<String> cancelSupportRequest(@PathVariable Integer supportId) {
        supportRequestService.cancelSupportRequest(supportId);
        return ResponseEntity.ok("OK");
    }

    // Lấy toàn bộ đơn hỗ trợ
    @GetMapping
    //@PreAuthorize("hasAuthority('SCOPE_HR')")
    public ResponseEntity<?> getAllSupportRequest(
            @RequestParam(required = false, defaultValue = "") String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(supportRequestService.getAllSupportRequest(keyword, status, page, size));
    }

    // Duyệt đơn hỗ trợ
    @PutMapping("/approve/{supportId}")
    //@PreAuthorize("hasAuthority('SCOPE_HR')")
    public ResponseEntity<?> approveSupportRequest(@PathVariable Integer supportId) {
        return ResponseEntity.ok(supportRequestService.approveSupportRequest(supportId));
    }

    // Chuyển trạng thái đơn hỗ trợ thành đang xử lý
    @PutMapping("/inProgress/{supportId}")
    //@PreAuthorize("hasAuthority('SCOPE_HR')")
    public ResponseEntity<?> inProgressSupportRequest(@PathVariable Integer supportId) {
        return ResponseEntity.ok(supportRequestService.inProgressSupportRequest(supportId));
    }

    // Từ chối đơn hỗ trợ thành đang xử lý
    @PutMapping("/reject/{supportId}")
    //@PreAuthorize("hasAuthority('SCOPE_HR')")
    public ResponseEntity<?> rejectSupportRequest(@PathVariable Integer supportId,
                                                  @RequestBody RejectSupportRequestRequest request) {
        return ResponseEntity.ok(supportRequestService.rejectSupportRequest(supportId, request));
    }
}
