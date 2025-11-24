package com.example.InternShip.service;

import com.example.InternShip.dto.leaveRequest.request.CreateLeaveApplicationRequest;
import com.example.InternShip.dto.leaveRequest.request.RejectLeaveApplicationRequest;
import com.example.InternShip.dto.leaveRequest.response.GetAllLeaveApplicationResponse;
import com.example.InternShip.dto.leaveRequest.response.GetLeaveApplicationResponse;
import com.example.InternShip.dto.leaveRequest.response.InternGetAllLeaveApplicationResponse;
import com.example.InternShip.dto.leaveRequest.response.InternGetAllLeaveApplicationResponseSupport;
import com.example.InternShip.dto.response.*;

public interface LeaveRequestService {

    InternGetAllLeaveApplicationResponseSupport createLeaveRequest(CreateLeaveApplicationRequest request);

    PagedResponse<GetAllLeaveApplicationResponse> getAllLeaveApplication(String status, String keyword, String type,
            int page, int size);

    GetLeaveApplicationResponse viewLeaveApplication(Integer id);

    void cancelLeaveApplication(Integer id);

    GetAllLeaveApplicationResponse approveLeaveApplication(Integer id);

    GetAllLeaveApplicationResponse rejectLeaveApplication(int id, RejectLeaveApplicationRequest request);

    InternGetAllLeaveApplicationResponse getAllLeaveApplicationByIntern(String status);
}
