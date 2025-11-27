package com.example.InternShip.service.impl;

import com.example.InternShip.dto.evaluation.request.EvaluateInternRequest;
import com.example.InternShip.dto.evaluation.response.EvaluationResponse;
import com.example.InternShip.entity.Intern;
import com.example.InternShip.entity.Mentor;
import com.example.InternShip.entity.Team;
import com.example.InternShip.entity.User;
import com.example.InternShip.repository.InternRepository;
import com.example.InternShip.repository.MentorRepository;
import com.example.InternShip.service.AuthService;
import com.example.InternShip.service.ExcelExportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EvaluationServiceImplTest {

    @Mock
    private InternRepository internRepository;
    @Mock
    private MentorRepository mentorRepository;
    @Mock
    private AuthService authService;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private ExcelExportService excelExportService;

    @InjectMocks
    private EvaluationServiceImpl evaluationService;

    private User mentorUser;
    private Mentor mentor;
    private Intern intern;
    private Team team;

    @BeforeEach
    void setUp() {
        mentorUser = new User();
        mentorUser.setId(1);

        mentor = new Mentor();
        mentor.setId(1);
        mentor.setUser(mentorUser);

        team = new Team();
        team.setId(1);
        team.setMentor(mentor);

        intern = new Intern();
        intern.setId(1);
        intern.setTeam(team);
    }

    @Test
    void evaluateIntern_happyPath() {
        EvaluateInternRequest request = new EvaluateInternRequest();
        request.setExpertiseScore(BigDecimal.valueOf(4.5));
        request.setAssessment("Good job");

        EvaluationResponse evaluationResponse = new EvaluationResponse();
        evaluationResponse.setInternId(1);
        evaluationResponse.setAssessment("Good job");

        when(authService.getUserLogin()).thenReturn(mentorUser);
        when(mentorRepository.findByUser(mentorUser)).thenReturn(Optional.of(mentor));
        when(internRepository.findById(1)).thenReturn(Optional.of(intern));
        when(internRepository.save(any(Intern.class))).thenReturn(intern);
        when(modelMapper.map(intern, EvaluationResponse.class)).thenReturn(evaluationResponse);

        EvaluationResponse response = evaluationService.evaluateIntern(1, request);

        assertNotNull(response);
        assertEquals("Good job", response.getAssessment());
    }

    @Test
    void getEvaluation_happyPath() {
        EvaluationResponse evaluationResponse = new EvaluationResponse();
        evaluationResponse.setInternId(1);

        when(internRepository.findById(1)).thenReturn(Optional.of(intern));
        when(modelMapper.map(intern, EvaluationResponse.class)).thenReturn(evaluationResponse);

        EvaluationResponse response = evaluationService.getEvaluation(1);

        assertNotNull(response);
        assertEquals(1, response.getInternId());
    }

    @Test
    void exportEvaluations_happyPath() {
        List<Intern> interns = Collections.singletonList(intern);
        when(internRepository.findForEvaluationReport(1, 1)).thenReturn(interns);
        when(excelExportService.exportInternEvaluations(interns)).thenReturn(new ByteArrayInputStream(new byte[0]));

        ByteArrayInputStream response = evaluationService.exportEvaluations(1, 1);

        assertNotNull(response);
    }
}
