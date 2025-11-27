package com.example.InternShip.service.impl;

import com.example.InternShip.entity.*;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailServiceImpl emailService;

    private InternshipApplication application;
    private User user;
    private InternshipProgram program;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("test@example.com");
        user.setFullName("Test User");

        program = new InternshipProgram();
        program.setName("Test Program");

        application = new InternshipApplication();
        application.setUser(user);
        application.setInternshipProgram(program);
    }

    @Test
    void sendApplicationStatusEmail_approved_happyPath() throws Exception {
        application.setStatus(InternshipApplication.Status.APPROVED);
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendApplicationStatusEmail(application);

        ArgumentCaptor<MimeMessage> messageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender).send(messageCaptor.capture());

        // Further assertions can be made on the captured message if needed
    }

    @Test
    void sendApplicationStatusEmail_rejected_happyPath() {
        application.setStatus(InternshipApplication.Status.REJECTED);
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendApplicationStatusEmail(application);

        ArgumentCaptor<MimeMessage> messageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender).send(messageCaptor.capture());
    }

    @Test
    void sendSprintCompletionEmail_happyPath() {
        Sprint sprint = new Sprint();
        sprint.setName("Test Sprint");
        Team team = new Team();
        team.setName("Test Team");
        sprint.setTeam(team);

        Mentor mentor = new Mentor();
        mentor.setUser(user);
        team.setMentor(mentor);

        List<Task> completedTasks = Collections.singletonList(new Task());
        List<Task> incompleteTasks = Collections.emptyList();

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendSprintCompletionEmail(sprint, completedTasks, incompleteTasks);

        ArgumentCaptor<MimeMessage> messageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender).send(messageCaptor.capture());
    }
}
