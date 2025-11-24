package com.example.InternShip.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.example.InternShip.dto.response.PagedResponse;
import com.example.InternShip.dto.supportRequest.request.UpdateSupportRequestRequest;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.InternShip.dto.cloudinary.response.FileResponse;
import com.example.InternShip.dto.supportRequest.request.CreateSupportRequestRequest;
import com.example.InternShip.dto.supportRequest.request.RejectSupportRequestRequest;
import com.example.InternShip.dto.supportRequest.response.GetSupportRequestResponse;
import com.example.InternShip.entity.SupportRequest;
import com.example.InternShip.entity.User;
import com.example.InternShip.exception.ErrorCode;
import com.example.InternShip.repository.SupportRequestRepository;
import com.example.InternShip.service.AuthService;
import com.example.InternShip.service.CloudinaryService;
import com.example.InternShip.service.SupportRequestService;
import com.example.InternShip.entity.SupportRequest.Status;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SupportRequestServiceImpl implements SupportRequestService {
    private final SupportRequestRepository supportRequestRepository;
    private final CloudinaryService cloudinaryService;
    private final AuthService authService;
    private final ModelMapper modelMapper;

    //Intern gửi yêu cầu
    @Override
    public GetSupportRequestResponse createSupportRequest(CreateSupportRequestRequest request) {
        User user = authService.getUserLogin();
        SupportRequest supportRequest = new SupportRequest();
        supportRequest.setIntern(user.getIntern());
        supportRequest.setTitle(request.getTitle());
        supportRequest.setDescription(request.getDescription());

        if (request.getEvidenceFile() != null) {
            FileResponse fileResponse = cloudinaryService.uploadFile(request.getEvidenceFile(), "Evidence");
            supportRequest.setEvidenceFile(fileResponse.getFileUrl());
        }

        supportRequestRepository.save(supportRequest);
        return mapToResponse(supportRequest);
    }

    //Intern lấy danh sách của chính mình
    @Override
    public List<GetSupportRequestResponse> getMyList() {
        User user = authService.getUserLogin();
        List<SupportRequest> list = supportRequestRepository.findAllByInternId(user.getIntern().getId());

        return list.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    //Intern sửa yêu cầu
    @Override
    public GetSupportRequestResponse updateRequest(Integer id, UpdateSupportRequestRequest request) {
        User user = authService.getUserLogin();
        SupportRequest req = supportRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_SUPPORT_REQUEST.getMessage()));

        if (!req.getIntern().getId().equals(user.getIntern().getId())) {
            throw new IllegalArgumentException(ErrorCode.NOT_PERMISSION.getMessage());
        }

        if (req.getStatus() != SupportRequest.Status.PENDING) {
            throw new IllegalArgumentException("Chỉ được sửa khi yêu cầu chưa được xử lý");
        }

        if (request.getTitle() != null) req.setTitle(request.getTitle());
        if (request.getDescription() != null) req.setDescription(request.getDescription());

        if (request.getEvidenceFile() != null && !request.getEvidenceFile().isEmpty()) {
            FileResponse fileResponse = cloudinaryService.uploadFile(request.getEvidenceFile(), "Evidence");
            req.setEvidenceFile(fileResponse.getFileUrl());
        }

        supportRequestRepository.save(req);
        return mapToResponse(req);
    }

    //Intern hủy yêu cầu
    @Override
    public void cancelSupportRequest(Integer supportId) {
        SupportRequest supportRequest = supportRequestRepository.findById(supportId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.SUPPORT_REQUEST_NOT_EXISTS.getMessage()));
        if (supportRequest.getStatus() != SupportRequest.Status.PENDING) {
            throw new IllegalArgumentException(ErrorCode.SUPPORT_REQUEST_STATUS_INVALID.getMessage());
        }
        supportRequestRepository.delete(supportRequest);
    }

    //HR lấy tất cả yêu cầu
    @Override
    public PagedResponse<GetSupportRequestResponse> getAllSupportRequest(String keyword, String status, int page, int size) {
        int pageNo = Math.max(0, page - 1);

        Pageable pageable = PageRequest.of(pageNo, size, Sort.by("createdAt").descending());

        Status statusEnum = (status == null || status.isEmpty()) ? null : SupportRequest.Status.valueOf(status);

        Page<SupportRequest> pageResult = supportRequestRepository.searchSupportRequest(keyword, statusEnum, pageable);

        List<GetSupportRequestResponse> content = pageResult.getContent().stream()
                .map(this::mapToResponse)
                .toList();

        return new PagedResponse<>(
                content,
                pageNo + 1,
                pageResult.getTotalElements(),
                pageResult.getTotalPages(),
                pageResult.hasNext(),
                pageResult.hasPrevious()
        );
    }

    //HR duyệt yêu cầu
    @Override
    public GetSupportRequestResponse approveSupportRequest(Integer supportId) {
        SupportRequest supportRequest = supportRequestRepository.findById(supportId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.SUPPORT_REQUEST_NOT_EXISTS.getMessage()));

        if (supportRequest.getStatus() == SupportRequest.Status.REJECTED) {
            throw new IllegalArgumentException(ErrorCode.SUPPORT_REQUEST_STATUS_INVALID.getMessage());
        }

        supportRequest.setStatus(SupportRequest.Status.RESOLVED);
        supportRequest.setResolvedAt(LocalDateTime.now());
        supportRequestRepository.save(supportRequest);
        return mapToResponse(supportRequest);
    }

    //HR nhận yêu cầu
    @Override
    public GetSupportRequestResponse inProgressSupportRequest(Integer supportId) {
        SupportRequest supportRequest = supportRequestRepository.findById(supportId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.SUPPORT_REQUEST_NOT_EXISTS.getMessage()));

        if (supportRequest.getStatus() == SupportRequest.Status.PENDING) {
            throw new IllegalArgumentException(ErrorCode.SUPPORT_REQUEST_STATUS_INVALID.getMessage());
        }

        supportRequest.setStatus(SupportRequest.Status.IN_PROGRESS);
        supportRequestRepository.save(supportRequest);
        return mapToResponse(supportRequest);
    }

    //HR từ chối yêu cầu
    @Override
    public GetSupportRequestResponse rejectSupportRequest(Integer supportId, RejectSupportRequestRequest request) {
        SupportRequest supportRequest = supportRequestRepository.findById(supportId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.SUPPORT_REQUEST_NOT_EXISTS.getMessage()));

        if (supportRequest.getStatus() == SupportRequest.Status.RESOLVED) {
            throw new IllegalArgumentException(ErrorCode.SUPPORT_REQUEST_STATUS_INVALID.getMessage());
        }

        supportRequest.setStatus(SupportRequest.Status.REJECTED);
        supportRequestRepository.save(supportRequest);
        return mapToResponse(supportRequest);
    }

    private GetSupportRequestResponse mapToResponse(SupportRequest supportRequest) {
        GetSupportRequestResponse res = modelMapper.map(supportRequest, GetSupportRequestResponse.class);

        res.setEvidenceFileUrl(supportRequest.getEvidenceFile());

        if (supportRequest.getIntern() != null && supportRequest.getIntern().getUser() != null) {
            res.setInternName(supportRequest.getIntern().getUser().getFullName());
            res.setInternEmail(supportRequest.getIntern().getUser().getEmail());
        }

        // Map thông tin Handler (HR xử lý)
        if (supportRequest.getHandler() != null) {
            res.setHandlerName(supportRequest.getHandler().getFullName());
        }

        return res;
    }
}