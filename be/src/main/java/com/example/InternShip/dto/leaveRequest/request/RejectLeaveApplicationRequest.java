package com.example.InternShip.dto.leaveRequest.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RejectLeaveApplicationRequest {
    @NotNull(message = "REASON_REJECT_NOT_NULL")
    private String reasonReject;
}
