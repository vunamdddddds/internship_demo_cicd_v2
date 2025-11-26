package com.example.InternShip.dto.allowancepackage.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreateAllowancePackageRequest {

    @NotBlank(message = "Tên gói phụ cấp không được để trống")
    private String name;

    @NotNull(message = "Số tiền không được để trống")
    @DecimalMin(value = "0.0", inclusive = true, message = "Số tiền phải lớn hơn hoặc bằng 0")
    private BigDecimal amount;

    @NotNull(message = "Số ngày công yêu cầu không được để trống")
    @Min(value = 0, message = "Số ngày công yêu cầu phải lớn hơn hoặc bằng 0")
    private Integer requiredWorkDays;

    @NotNull(message = "ID chương trình thực tập không được để trống")
    @Min(value = 1, message = "ID chương trình thực tập phải lớn hơn 0")
    private Integer internshipProgramId;
}
