package com.example.InternShip.dto.allowancepackage.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class AllowancePackageResponse {
    private int id;
    private String name;
    private BigDecimal amount;
    private int requiredWorkDays;
    private InternshipProgramInfo internshipProgram;
    private String status;
    private LocalDateTime createdAt;
}
