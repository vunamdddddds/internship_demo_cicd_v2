package com.example.InternShip.service.impl;

import com.example.InternShip.dto.cloudinary.response.FileResponse;
import com.example.InternShip.dto.leaveRequest.request.CreateLeaveApplicationRequest;
import com.example.InternShip.dto.leaveRequest.request.RejectLeaveApplicationRequest;
import com.example.InternShip.dto.leaveRequest.response.GetAllLeaveApplicationResponse;
import com.example.InternShip.dto.leaveRequest.response.GetLeaveApplicationResponse;
import com.example.InternShip.dto.leaveRequest.response.InternGetAllLeaveApplicationResponse;
import com.example.InternShip.dto.leaveRequest.response.InternGetAllLeaveApplicationResponseSupport;
import com.example.InternShip.dto.response.PagedResponse;
import com.example.InternShip.entity.Intern;
import com.example.InternShip.entity.LeaveRequest;
import com.example.InternShip.entity.User;
import com.example.InternShip.exception.ErrorCode;
import com.example.InternShip.repository.LeaveRequestRepository;
import com.example.InternShip.service.AuthService;
import com.example.InternShip.service.CloudinaryService;
import com.example.InternShip.service.LeaveRequestService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LeaveRequestServiceImpl implements LeaveRequestService {
    private final LeaveRequestRepository leaveRequestRepository;

    private final AuthService authService;

    private final CloudinaryService cloudinaryService;

    private final ModelMapper modelMapper;

        @Override
    public InternGetAllLeaveApplicationResponseSupport createLeaveRequest(CreateLeaveApplicationRequest request) {
        // Lấy ra thằng intern request
        User user = authService.getUserLogin();
        Intern intern = user.getIntern();

        // Kiểm tra type hợp lệ
        if (!request.getType().equals(LeaveRequest.Type.EARLY_LEAVE.toString()) &&
                !request.getType().equals(LeaveRequest.Type.LATE.toString()) &&
                !request.getType().equals(LeaveRequest.Type.ON_LEAVE.toString())) {
            throw new IllegalArgumentException(ErrorCode.TYPE_LEAVE_APPLICATION_INVALID.getMessage());
        }
        // Kiểm tra date
        if (request.getDate() == null || request.getDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Ngày nghỉ không hợp lệ");
        }

        // Tạo LeaveRequest
        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setType(LeaveRequest.Type.valueOf(request.getType()));
        leaveRequest.setDate(request.getDate());
        leaveRequest.setReason(request.getReason());
        if (request.getAttachedFile() != null && !request.getAttachedFile().isEmpty()) {
            FileResponse fileResponse = cloudinaryService.uploadFile(request.getAttachedFile(),
                    "Leave Application Attached File");
            leaveRequest.setAttachedFileUrl(fileResponse.getFileUrl());
        }
        leaveRequest.setIntern(intern);
        leaveRequestRepository.save(leaveRequest);

        InternGetAllLeaveApplicationResponseSupport response = new InternGetAllLeaveApplicationResponseSupport();
        response.setId(leaveRequest.getId());
        response.setType(leaveRequest.getType());
        response.setDate(leaveRequest.getDate());
        response.setReason(leaveRequest.getReason());
        response.setApproved(leaveRequest.getApproved());

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<GetAllLeaveApplicationResponse> getAllLeaveApplication(
            String status, String keyword, String type, int page, int size) {

        page = Math.max(0, page - 1);
        Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());

        LeaveRequest.Type leaveType = null;
        if (type != null && !type.isEmpty()) {
            try {
                leaveType = LeaveRequest.Type.valueOf(type.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(ErrorCode.TYPE_LEAVE_APPLICATION_INVALID.getMessage());
            }
        }

        Page<LeaveRequest> leaveApplications = leaveRequestRepository
                .searchLeaveApplication(status, leaveType, keyword, pageable);

        // Map entity → DTO
        List<GetAllLeaveApplicationResponse> res = leaveApplications.getContent().stream()
                .map(this::mapLeaveApplicationToGetAllLeaveApplicationResponse).toList();

        return new PagedResponse<>(
                res,
                page + 1,
                leaveApplications.getTotalElements(),
                leaveApplications.getTotalPages(),
                leaveApplications.hasNext(),
                leaveApplications.hasPrevious());
    }

    @Override
    public InternGetAllLeaveApplicationResponse getAllLeaveApplicationByIntern(String status) {
        User user = authService.getUserLogin();
        Intern intern = user.getIntern();

        InternGetAllLeaveApplicationResponse response = new InternGetAllLeaveApplicationResponse();

        response.setCountLeaveApplication(leaveRequestRepository.countAllByInternId(intern.getId()));
        response.setCountPendingApprove(leaveRequestRepository.countPendingByInternId(intern.getId()));
        response.setCountApprove(leaveRequestRepository.countApprovedByInternId(intern.getId()));
        response.setCountReject(leaveRequestRepository.countRejectedByInternId(intern.getId()));

        List<InternGetAllLeaveApplicationResponseSupport> leaveApps = leaveRequestRepository
                .findAllByInternIdAndApproved(intern.getId(), status)
                .stream()
                .map(l -> {
                    InternGetAllLeaveApplicationResponseSupport dto = new InternGetAllLeaveApplicationResponseSupport();
                    dto.setId(l.getId());
                    dto.setType(l.getType());
                    dto.setDate(l.getDate());
                    dto.setReason(l.getReason());
                    dto.setApproved(l.getApproved());
                    return dto;
                })
                .toList();

        response.setLeaveApplications(leaveApps);
        return response;
    }

    @Override
    public GetLeaveApplicationResponse viewLeaveApplication(Integer id) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.LEAVE_APPLICATION_NOT_EXISTS.getMessage()));
        GetLeaveApplicationResponse res = modelMapper.map(leaveRequest, GetLeaveApplicationResponse.class);
        res.setInternName(leaveRequest.getIntern().getUser().getFullName());
        return res;
    }

    @Override
    public void cancelLeaveApplication(Integer id) {
        // Tính làm cái check đơn của người dùng nhưng mà thôi
        LeaveRequest leaveRequest = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.LEAVE_APPLICATION_NOT_EXISTS.getMessage()));
        if (leaveRequest.getApproved() != null) {
            throw new RuntimeException(ErrorCode.CANCEL_REQUEST_FAILED.getMessage());
        }
        leaveRequestRepository.delete(leaveRequest);
    }

    @Override
    public GetAllLeaveApplicationResponse approveLeaveApplication(Integer id) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.LEAVE_APPLICATION_NOT_EXISTS.getMessage()));
        if (leaveRequest.getApproved() == null) {
            leaveRequest.setApproved(true);
            leaveRequestRepository.save(leaveRequest);
        } else {
            throw new IllegalArgumentException(ErrorCode.ACTION_INVALID.getMessage());
        }
        return mapLeaveApplicationToGetAllLeaveApplicationResponse(leaveRequest);
    }

    @Override
    public GetAllLeaveApplicationResponse rejectLeaveApplication(int id, RejectLeaveApplicationRequest request) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.LEAVE_APPLICATION_NOT_EXISTS.getMessage()));
        if (leaveRequest.getApproved() == null) {
            leaveRequest.setApproved(false);
            leaveRequest.setReasonReject(request.getReasonReject());
            leaveRequestRepository.save(leaveRequest);
        } else {
            throw new IllegalArgumentException(ErrorCode.ACTION_INVALID.getMessage());
        }
        return mapLeaveApplicationToGetAllLeaveApplicationResponse(leaveRequest);
    }

    public GetAllLeaveApplicationResponse mapLeaveApplicationToGetAllLeaveApplicationResponse(LeaveRequest lr){
        GetAllLeaveApplicationResponse dto = new GetAllLeaveApplicationResponse();
        dto.setId(lr.getId());
        dto.setInternName(lr.getIntern().getUser().getFullName());
        dto.setType(lr.getType());
        dto.setDate(lr.getDate());
        dto.setReason(lr.getReason());
        dto.setAttachedFileUrl(lr.getAttachedFileUrl());
        if (lr.getApproved() == null) {
            dto.setApproved(null);
        } else {
            dto.setApproved(lr.getApproved());
        }
        dto.setReasonReject(lr.getReasonReject());
        return dto;
    }
}
