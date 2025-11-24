package com.example.InternShip.dto.leaveRequest.response;

import java.time.LocalDate;

import com.example.InternShip.entity.LeaveRequest;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class GetLeaveApplicationResponse {
    private String internName;
    private LeaveRequest.Type type;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate date;
    private String reason;
    private String attachedFileUrl;
    private Boolean approved;
    private String reasonReject;
}
