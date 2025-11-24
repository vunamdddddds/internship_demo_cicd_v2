package com.example.InternShip.dto.leaveRequest.response;

import java.time.LocalDate;

import com.example.InternShip.entity.LeaveRequest;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class InternGetAllLeaveApplicationResponseSupport {
    private Integer id;
    private LeaveRequest.Type type;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate date;
    private String reason;
    private Boolean approved;
}
