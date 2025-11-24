package com.example.InternShip.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AllowanceResponse {
    private Long id;
    private String internName;
    private String email;
    private String internshipProgramName;
    private BigDecimal amount;
    private String remiter;
    private LocalDateTime paidAt;
    private String status;
}
