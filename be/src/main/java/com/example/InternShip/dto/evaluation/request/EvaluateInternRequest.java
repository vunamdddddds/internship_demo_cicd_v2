package com.example.InternShip.dto.evaluation.request;

import com.example.InternShip.entity.Intern;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class EvaluateInternRequest {

    @NotNull
    @Min(0) @Max(10)
    private BigDecimal expertiseScore; //điểm chuyên môn

    @NotNull
    @Min(0) @Max(10)
    private BigDecimal qualityScore; //điểm chất lượng

    @NotNull
    @Min(0) @Max(10)
    private BigDecimal problemSolvingScore; //điểm giải quyết vấn đè

    @NotNull
    @Min(0) @Max(10)
    private BigDecimal technologyLearningScore; //điểm công nghệ mới

    @NotNull
    private Intern.SoftSkill softSkill; //kỹ năng mềm

    private String assessment; //nhận xét của mentor
}