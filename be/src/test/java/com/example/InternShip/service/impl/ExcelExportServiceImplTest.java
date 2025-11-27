package com.example.InternShip.service.impl;

import com.example.InternShip.entity.Intern;
import com.example.InternShip.entity.Team;
import com.example.InternShip.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class ExcelExportServiceImplTest {

    @InjectMocks
    private ExcelExportServiceImpl excelExportService;

    private Intern intern;
    private User user;
    private Team team;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setFullName("Test User");
        user.setEmail("test@example.com");

        team = new Team();
        team.setName("Test Team");

        intern = new Intern();
        intern.setId(1);
        intern.setUser(user);
        intern.setTeam(team);
    }

    @Test
    void exportInternEvaluations_happyPath() {
        List<Intern> interns = Collections.singletonList(intern);
        ByteArrayInputStream response = excelExportService.exportInternEvaluations(interns);

        assertNotNull(response);
        assertTrue(response.available() > 0);
    }
}
