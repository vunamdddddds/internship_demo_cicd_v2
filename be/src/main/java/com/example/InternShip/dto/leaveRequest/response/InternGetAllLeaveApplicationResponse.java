package com.example.InternShip.dto.leaveRequest.response;

import java.util.List;

import lombok.Data;

@Data
public class InternGetAllLeaveApplicationResponse {
    private int countLeaveApplication;
    private int countPendingApprove;
    private int countApprove;
    private int countReject;

    List<InternGetAllLeaveApplicationResponseSupport> leaveApplications;
}
