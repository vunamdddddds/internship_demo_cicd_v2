package com.example.InternShip.service;

import com.example.InternShip.dto.evaluation.request.EvaluateInternRequest;
import com.example.InternShip.dto.evaluation.response.EvaluationResponse;

import java.io.ByteArrayInputStream;

public interface EvaluationService {
    EvaluationResponse evaluateIntern(Integer internId, EvaluateInternRequest request);

    EvaluationResponse getEvaluation(Integer internId);

    ByteArrayInputStream exportEvaluations(Integer teamId, Integer programId);
}