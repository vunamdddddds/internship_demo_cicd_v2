package com.example.InternShip.dto.evaluation.response;

import com.example.InternShip.entity.Intern;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class EvaluationResponse {
    private Integer internId;
    private BigDecimal expertiseScore;
    private BigDecimal qualityScore;
    private BigDecimal problemSolvingScore;
    private BigDecimal technologyLearningScore;
    private Intern.SoftSkill softSkill;
    private String assessment;
}