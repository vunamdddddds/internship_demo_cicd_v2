package com.example.InternShip.service.impl;

import com.example.InternShip.dto.evaluation.request.EvaluateInternRequest;
import com.example.InternShip.dto.evaluation.response.EvaluationResponse;
import com.example.InternShip.entity.Intern;
import com.example.InternShip.entity.Mentor;
import com.example.InternShip.entity.User;
import com.example.InternShip.exception.ErrorCode;
import com.example.InternShip.repository.InternRepository;
import com.example.InternShip.repository.MentorRepository;
import com.example.InternShip.service.AuthService;
import com.example.InternShip.service.EvaluationService;
import com.example.InternShip.service.ExcelExportService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EvaluationServiceImpl implements EvaluationService {

    private final InternRepository internRepository;
    private final MentorRepository mentorRepository;
    private final AuthService authService;
    private final ModelMapper modelMapper;
    private final ExcelExportService excelExportService;

    @Override
    @Transactional
    public EvaluationResponse evaluateIntern(Integer internId, EvaluateInternRequest request) {
        User mentorUser = authService.getUserLogin();
        Mentor mentor = mentorRepository.findByUser(mentorUser)
                .orElseThrow(() -> new AccessDeniedException(ErrorCode.EVALUATION_BY_MENTOR_ONLY.getMessage()));

        //Lấy thông tin Intern được đánh giá
        Intern intern = internRepository.findById(internId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.INTERN_NOT_FOUND.getMessage()));

        //Mentor này có phải là mentor của Intern này không
        if (intern.getTeam() == null || !intern.getTeam().getMentor().getId().equals(mentor.getId())) {
            throw new AccessDeniedException(ErrorCode.MENTOR_INVALID.getMessage());
        }

        //Ghi đè/Cập nhật thông tin
        intern.setExpertiseScore(request.getExpertiseScore());
        intern.setQualityScore(request.getQualityScore());
        intern.setProblemSolvingScore(request.getProblemSolvingScore());
        intern.setTechnologyLearningScore(request.getTechnologyLearningScore());
        intern.setSoftSkill(request.getSoftSkill());
        intern.setAssessment(request.getAssessment());

        Intern savedIntern = internRepository.save(intern);

        //Map và trả về
        EvaluationResponse response = modelMapper.map(savedIntern, EvaluationResponse.class);
        response.setInternId(savedIntern.getId());
        return response;
    }

    @Override
    public EvaluationResponse getEvaluation(Integer internId) {
        Intern intern = internRepository.findById(internId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.INTERN_NOT_FOUND.getMessage()));
        EvaluationResponse response = modelMapper.map(intern, EvaluationResponse.class);
        response.setInternId(intern.getId());
        return response;
    }

    @Override
    public ByteArrayInputStream exportEvaluations(Integer teamId, Integer programId) {

        //Lấy danh sách Intern đã lọc
        List<Intern> internsToExport = internRepository.findForEvaluationReport(programId, teamId);

        return excelExportService.exportInternEvaluations(internsToExport);
    }
}