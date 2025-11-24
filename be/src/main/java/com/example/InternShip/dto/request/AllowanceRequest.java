package com.example.InternShip.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for creating a new allowance record.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AllowanceRequest {

    @NotNull(message = "Intern ID cannot be null")
    private Integer internId;

    @Min(value = 0, message = "Amount must be non-negative")
    private BigDecimal amount;

    private String description;
}
