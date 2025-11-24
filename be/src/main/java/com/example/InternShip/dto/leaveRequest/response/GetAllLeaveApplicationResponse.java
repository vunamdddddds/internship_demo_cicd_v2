package com.example.InternShip.dto.leaveRequest.response;

import lombok.Data;

import java.time.LocalDate;


import com.example.InternShip.entity.LeaveRequest;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class GetAllLeaveApplicationResponse {
    private Integer id;
    private String internName;
    private LeaveRequest.Type type;
    @JsonFormat(pattern="dd-MM-yyyy")
    private LocalDate date;
    private String reason;
    private String attachedFileUrl;
    private Boolean approved;
    private String reasonReject;
}
