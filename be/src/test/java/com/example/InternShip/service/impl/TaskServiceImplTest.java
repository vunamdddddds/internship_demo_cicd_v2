package com.example.InternShip.service.impl;

import com.example.InternShip.dto.task.request.BatchTaskUpdateRequest;
import com.example.InternShip.dto.task.request.CreateTaskRequest;
import com.example.InternShip.dto.task.request.UpdateTaskRequest;
import com.example.InternShip.dto.task.response.TaskResponse;
import com.example.InternShip.entity.*;
import com.example.InternShip.entity.enums.Role;
import com.example.InternShip.entity.enums.TaskStatus;
import com.example.InternShip.repository.InternRepository;
import com.example.InternShip.repository.MentorRepository;
import com.example.InternShip.repository.SprintRepository;
import com.example.InternShip.repository.TaskRepository;
import com.example.InternShip.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private SprintRepository sprintRepository;
    @Mock
    private InternRepository internRepository;
    @Mock
    private MentorRepository mentorRepository;
    @Mock
    private AuthService authService;

    @InjectMocks
    private TaskServiceImpl taskService;

    private User mentorUser;
    private Mentor mentor;
    private Intern intern;
    private Team team;
    private Sprint sprint;
    private Task task;

    @BeforeEach
    void setUp() {
        // 1. Common objects setup
        mentorUser = new User();
        mentorUser.setId(101); // User ID is Integer
        mentorUser.setRole(Role.MENTOR);
        mentorUser.setFullName("Mentor Name");

        mentor = new Mentor();
        mentor.setId(1); // Mentor ID is Integer
        mentor.setUser(mentorUser);

        team = new Team();
        team.setId(1); // Team ID is Integer (Corrected)
        team.setMentor(mentor);

        // Associate team with mentor
        mentor.setTeams(List.of(team)); // Teams is List<Team>

        sprint = new Sprint();
        sprint.setId(1L); // Sprint ID is Long
        sprint.setTeam(team);
        sprint.setStartDate(LocalDate.now().minusDays(5));
        sprint.setEndDate(LocalDate.now().plusDays(5));

        intern = new Intern();
        intern.setId(1); // Intern ID is Integer
        intern.setTeam(team);
        User internUser = new User();
        internUser.setId(102); // User ID is Integer
        internUser.setFullName("Intern Name");
        intern.setUser(internUser);

        task = new Task();
        task.setId(1L); // Task ID is Long
        task.setName("Existing Task");
        task.setStatus(TaskStatus.TODO);
        task.setSprint(sprint);
        task.setTeam(team);
        task.setCreatedBy(mentorUser);
    }

    /**
     * Test case for creating a task successfully as a Mentor.
     */
    @Test
    void testCreateTask_Success_WhenUserIsMentor() {
        // Arrange
        CreateTaskRequest request = new CreateTaskRequest();
        request.setName("New Task");
        request.setSprintId(sprint.getId());
        request.setAssigneeId(intern.getId());
        request.setDeadline(LocalDate.now().plusDays(2));

        when(authService.getUserLogin()).thenReturn(mentorUser);
        when(sprintRepository.findById(sprint.getId())).thenReturn(Optional.of(sprint));
        when(internRepository.findById(intern.getId())).thenReturn(Optional.of(intern));
        when(mentorRepository.findByUser(any(User.class))).thenReturn(Optional.of(mentor));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        TaskResponse response = taskService.createTask(request);

        // Assert
        assertNotNull(response);
        assertEquals(request.getName(), response.getName());
        assertEquals(TaskStatus.TODO, response.getStatus()); // Should default to TODO
        assertEquals(sprint.getId(), response.getSprint_Id());
        assertEquals(intern.getId(), response.getAssignee_Id());

        // Verify that the save method was called on the repository
        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository, times(1)).save(taskCaptor.capture());

        // Assert properties of the saved task
        Task savedTask = taskCaptor.getValue();
        assertEquals("New Task", savedTask.getName());
        assertEquals(sprint, savedTask.getSprint());
        assertEquals(intern, savedTask.getAssignee());
        assertEquals(mentorUser, savedTask.getCreatedBy());
        assertEquals(team, savedTask.getTeam());
    }

    /**
     * Test case for partially updating a task successfully.
     */
    @Test
    void testUpdateTask_Success_PartialUpdate() {
        // Arrange
        Long taskId = task.getId();
        UpdateTaskRequest request = new UpdateTaskRequest();
        request.setName("Updated Task Name");
        request.setStatus(TaskStatus.IN_PROGRESS);
        // Other fields are null to test partial update

        when(authService.getUserLogin()).thenReturn(mentorUser);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(mentorRepository.findByUser(any(User.class))).thenReturn(Optional.of(mentor));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        TaskResponse response = taskService.updateTask(taskId, request);

        // Assert
        assertNotNull(response);
        assertEquals(request.getName(), response.getName());
        assertEquals(request.getStatus(), response.getStatus());

        // Verify that the save method was called
        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository, times(1)).save(taskCaptor.capture());

        // Assert that the captured task has the updated fields
        Task savedTask = taskCaptor.getValue();
        assertEquals("Updated Task Name", savedTask.getName());
        assertEquals(TaskStatus.IN_PROGRESS, savedTask.getStatus());
        assertNull(savedTask.getDescription()); // Should remain null as it was not updated
    }

    /**
     * Test case for deleting a task successfully.
     */
    @Test
    void testDeleteTask_Success() {
        // Arrange
        Long taskId = task.getId();
        when(authService.getUserLogin()).thenReturn(mentorUser);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(mentorRepository.findByUser(any(User.class))).thenReturn(Optional.of(mentor));

        // Act
        taskService.deleteTask(taskId);

        // Assert
        // Verify that delete was called exactly once with the correct task object
        verify(taskRepository, times(1)).delete(task);
    }

    /**
     * Test case for batch updating tasks to move them to a new sprint.
     */
    @Test
    void testBatchUpdate_MoveToSprint_Success() {
        // Arrange
        Sprint newSprint = new Sprint();
        newSprint.setId(2L);
        newSprint.setTeam(team);

        BatchTaskUpdateRequest request = new BatchTaskUpdateRequest();
        request.setTaskIds(List.of(task.getId()));
        request.setAction("MOVE_TO_SPRINT");
        request.setTargetSprintId(newSprint.getId());

        when(authService.getUserLogin()).thenReturn(mentorUser);
        when(taskRepository.findAllById(request.getTaskIds())).thenReturn(List.of(task));
        when(sprintRepository.findById(newSprint.getId())).thenReturn(Optional.of(newSprint));
        when(mentorRepository.findByUser(any(User.class))).thenReturn(Optional.of(mentor));

        // Act
        taskService.batchUpdateTasks(request);

        // Assert
        ArgumentCaptor<List<Task>> tasksCaptor = ArgumentCaptor.forClass(List.class);
        verify(taskRepository, times(1)).saveAll(tasksCaptor.capture());

        List<Task> savedTasks = tasksCaptor.getValue();
        assertEquals(1, savedTasks.size());
        assertEquals(newSprint, savedTasks.getFirst().getSprint());
    }

    /**
     * Test case for getting a task by its ID successfully.
     */
    @Test
    void testGetTaskById_Success() {
        // Arrange
        Long taskId = task.getId();
        when(authService.getUserLogin()).thenReturn(mentorUser);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(mentorRepository.findByUser(any(User.class))).thenReturn(Optional.of(mentor));

        // Act
        TaskResponse response = taskService.getTaskById(taskId);

        // Assert
        assertNotNull(response);
        assertEquals(task.getId(), response.getId());
        assertEquals(task.getName(), response.getName());
        assertEquals(mentorUser.getFullName(), response.getCreatedByName());
    }

    /**
     * Test case for getting tasks by sprint successfully.
     */
    @Test
    void testGetTasksBySprint_Success() {
        // Arrange
        Long sprintId = sprint.getId();
        when(authService.getUserLogin()).thenReturn(mentorUser);
        when(sprintRepository.findById(sprintId)).thenReturn(Optional.of(sprint));
        when(mentorRepository.findByUser(any(User.class))).thenReturn(Optional.of(mentor));
        when(taskRepository.findAll(any(Specification.class))).thenReturn(Collections.singletonList(task));

        // Act
        List<TaskResponse> responses = taskService.getTasksBySprint(sprintId, null, null);

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(task.getId(), responses.getFirst().getId());
        verify(taskRepository, times(1)).findAll(any(Specification.class));
    }
}
