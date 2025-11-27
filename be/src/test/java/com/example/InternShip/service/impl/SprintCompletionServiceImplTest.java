package com.example.InternShip.service.impl;

import com.example.InternShip.entity.Sprint;
import com.example.InternShip.entity.Task;
import com.example.InternShip.entity.enums.TaskStatus;
import com.example.InternShip.repository.SprintRepository;
import com.example.InternShip.service.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SprintCompletionServiceImplTest {

    @Mock
    private SprintRepository sprintRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private SprintCompletionServicelmpl sprintCompletionService;

    @Test
    void processAndNotifyFinishedSprints_happyPath() {
        Sprint sprint = new Sprint();
        sprint.setId(1L);
        sprint.setName("Test Sprint");
        Task task = new Task();
        task.setStatus(TaskStatus.DONE);
        sprint.setTasks(Collections.singletonList(task));

        when(sprintRepository.findByEndDate(LocalDate.now())).thenReturn(Collections.singletonList(sprint));

        sprintCompletionService.processAndNotifyFinishedSprints();

        verify(emailService).sendSprintCompletionEmail(sprint, Collections.singletonList(task), Collections.emptyList());
    }
}
