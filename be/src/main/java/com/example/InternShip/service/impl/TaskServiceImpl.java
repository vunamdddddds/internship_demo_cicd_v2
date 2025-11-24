package com.example.InternShip.service.impl;

import com.example.InternShip.dto.task.request.BatchTaskUpdateRequest;
import com.example.InternShip.dto.task.request.CreateTaskRequest;
import com.example.InternShip.dto.task.request.UpdateTaskRequest;
import com.example.InternShip.dto.task.response.TaskResponse;
import com.example.InternShip.entity.Intern;
import com.example.InternShip.entity.Mentor;
import com.example.InternShip.entity.Sprint;
import com.example.InternShip.entity.Task;
import com.example.InternShip.entity.User;
import com.example.InternShip.entity.enums.Role;
import com.example.InternShip.entity.enums.TaskStatus;
import com.example.InternShip.exception.ErrorCode;
import com.example.InternShip.repository.InternRepository;
import com.example.InternShip.repository.MentorRepository;
import com.example.InternShip.repository.SprintRepository;
import com.example.InternShip.repository.TaskRepository;
import com.example.InternShip.service.AuthService;
import com.example.InternShip.service.TaskService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final SprintRepository sprintRepository;
    private final InternRepository internRepository;
    private final MentorRepository mentorRepository;
    private final AuthService authService;

    @Override
    @Transactional
    public void batchUpdateTasks(BatchTaskUpdateRequest request) {
        List<Task> tasksToUpdate = taskRepository.findAllById(request.getTaskIds());
        if (tasksToUpdate.size() != request.getTaskIds().size()) {
            throw new IllegalArgumentException(ErrorCode.LIST_TASK_INVALID.getMessage());
        }

        // Simple permission check: for now, only the mentor of the first task's team
        // can perform this.
        // A more robust check might be needed depending on requirements.
        if (!tasksToUpdate.isEmpty()) {
            User user = authService.getUserLogin();
            Sprint firstTaskSprint = tasksToUpdate.getFirst().getSprint();
            if (firstTaskSprint != null) { // Tasks might be in backlog (sprint is null)
                checkTaskManagementPermission(user, firstTaskSprint, "manage");
            } else if (user.getRole() != Role.MENTOR) {
                throw new AccessDeniedException(ErrorCode.NOT_PERMISSION.getMessage());
            }
        }

        switch (request.getAction()) {
            case "MOVE_TO_SPRINT":
                if (request.getTargetSprintId() == null) {
                    throw new IllegalArgumentException(ErrorCode.SPRINT_NOT_EXISTS.getMessage());
                }
                Sprint targetSprint = sprintRepository.findById(request.getTargetSprintId())
                        .orElseThrow(() -> new EntityNotFoundException(ErrorCode.SPRINT_NOT_EXISTS.getMessage()));
                tasksToUpdate.forEach(task -> task.setSprint(targetSprint));
                break;
            case "MOVE_TO_BACKLOG":
                tasksToUpdate.forEach(task -> task.setSprint(null));
                break;
            case "CANCEL":
                tasksToUpdate.forEach(task -> task.setStatus(TaskStatus.CANCELLED));
                break;
            default:
                throw new IllegalArgumentException("Invalid action: " + request.getAction());
        }

        taskRepository.saveAll(tasksToUpdate);
    }

    @Override
    public TaskResponse createTask(CreateTaskRequest request) {
        User creator = authService.getUserLogin();

        Sprint sprint = sprintRepository.findById(request.getSprintId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.SPRINT_NOT_EXISTS.getMessage()));
        // kiểm tra spirnt đã hết hạn chưa
        if (!sprint.getEndDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException(ErrorCode.SPRINT_INVALID.getMessage());
        }
        checkTaskManagementPermission(creator, sprint, "create");

        validateTaskDeadline(request.getDeadline(), sprint);

        Intern assignedIntern = null; // Assignee is optional
        if (request.getAssigneeId() != null) {
            assignedIntern = internRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new EntityNotFoundException(ErrorCode.INTERN_NOT_EXISTED.getMessage()));

            // Validate that the assigned intern is part of the sprint's team
            if (assignedIntern.getTeam() == null
                    || !assignedIntern.getTeam().getId().equals(sprint.getTeam().getId())) {
                throw new RuntimeException(ErrorCode.INTERN_NOT_IN_THIS_TEAM.getMessage());
            }
        }

        Mentor teamMentor = sprint.getTeam().getMentor();
        if (teamMentor == null) {
            throw new IllegalStateException("The team for this sprint does not have an assigned mentor.");
        }

        Task task = new Task();
        task.setName(request.getName());
        task.setDescription(request.getDescription());
        task.setSprint(sprint);
        task.setTeam(sprint.getTeam()); // Permanently stamp the team
        task.setAssignee(assignedIntern); // Can be null
        task.setMentor(teamMentor);
        task.setCreatedBy(creator);
        task.setDeadline(request.getDeadline() == null ? sprint.getEndDate() : request.getDeadline());
        task.setStatus(TaskStatus.TODO);
        Task savedTask = taskRepository.save(task);
        return mapToTaskResponse(savedTask);
    }

    // hàm kiểm tra người dùng hiện tại có quyền không
    private void checkTaskManagementPermission(User user, Sprint sprint, String action) {
        if (!user.getRole().equals(Role.INTERN) && !user.getRole().equals(Role.MENTOR)) {
            throw new AccessDeniedException(ErrorCode.NOT_PERMISSION.getMessage());
        }

        if (user.getRole().equals(Role.INTERN)) {
            Intern intern = internRepository.findByUser(user)
                    .orElseThrow(() -> new EntityNotFoundException(ErrorCode.INTERN_NOT_EXISTED.getMessage()));

            if (!sprint.getTeam().getId().equals(intern.getTeam().getId())) {
                throw new AccessDeniedException(ErrorCode.NOT_PERMISSION.getMessage());
            }
        }

        if (user.getRole().equals(Role.MENTOR)) {
            Mentor mentor = mentorRepository.findByUser(user)
                    .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MENTOR_NOT_EXISTED.getMessage()));

            if (mentor.getTeams().stream().noneMatch(team -> team.getId().equals(sprint.getTeam().getId()))) {
                throw new AccessDeniedException(ErrorCode.NOT_PERMISSION.getMessage());
            }
        }
    }

    @Override
    public TaskResponse updateTask(Long taskId, UpdateTaskRequest request) {
        User user = authService.getUserLogin();

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.TASK_NOT_EXISTS.getMessage()));

        Sprint sprint = task.getSprint();

        // Check if the sprint has expired
        if (!sprint.getEndDate().isAfter(LocalDate.now())) {
            throw new RuntimeException(ErrorCode.UPDATE_TASK_FAILED.getMessage());
        }

        checkTaskManagementPermission(user, sprint, "update");

        // Validate deadline only if it's provided in the request
        if (request.getDeadline() != null) {
            validateTaskDeadline(request.getDeadline(), sprint);
        }

        // Apply partial updates: only update fields that are not null in the request
        if (request.getName() != null) {
            task.setName(request.getName());
        }
        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }
        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
        }
        if (request.getDeadline() != null) {
            validateTaskDeadline(request.getDeadline(), sprint);
            task.setDeadline(request.getDeadline());
        }

        // Handle assignee update
        if (request.getAssigneeId() != null) {
            Intern assignedIntern = internRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new EntityNotFoundException(ErrorCode.INTERN_NOT_EXISTED.getMessage()));
            // Validate that the assigned intern is part of the sprint's team
            if (assignedIntern.getTeam() == null
                    || !assignedIntern.getTeam().getId().equals(sprint.getTeam().getId())) {
                throw new RuntimeException(ErrorCode.INTERN_NOT_IN_THIS_TEAM.getMessage());
            }
            task.setAssignee(assignedIntern);
        } else {
            // If the assigneeId is explicitly passed as null, un-assign the task
            task.setAssignee(null);
        }

        Task updatedTask = taskRepository.save(task);
        return mapToTaskResponse(updatedTask);
    }

    // hàm kiểm tra deadline của task có hợp lệ không
    private void validateTaskDeadline(java.time.LocalDate deadline, Sprint sprint) {
        if (deadline != null) {
            if (deadline.isBefore(sprint.getStartDate()) || deadline.isAfter(sprint.getEndDate())) {
                throw new IllegalArgumentException(ErrorCode.TIME_END_INVALID.getMessage());
            }
        }
    }

    @Override
    public void deleteTask(Long taskId) {
        User user = authService.getUserLogin();

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.TASK_NOT_EXISTS.getMessage()));

        Sprint sprint = task.getSprint();

        if (!sprint.getEndDate().isAfter(LocalDate.now())) {
            throw new RuntimeException(ErrorCode.DELETE_TASK_FAILED.getMessage());
        }

        checkTaskManagementPermission(user, sprint, "delete");

        taskRepository.delete(task);
    }

    @Override
    public List<TaskResponse> getTasksBySprint(Long sprintId, TaskStatus status, Integer assigneeId) {
        User user = authService.getUserLogin();
        Sprint sprint = sprintRepository.findById(sprintId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.SPRINT_NOT_EXISTS.getMessage()));

        checkTaskManagementPermission(user, sprint, "view");

        Specification<Task> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("sprint").get("id"), sprintId));

            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }
            if (assigneeId != null) {
                predicates.add(criteriaBuilder.equal(root.get("assignee").get("id"), assigneeId));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        List<Task> tasks = taskRepository.findAll(spec);
        return tasks.stream()
                .map(this::mapToTaskResponse)
                .collect(Collectors.toList());
    }

    public TaskResponse mapToTaskResponse(Task task) {
        TaskResponse response = new TaskResponse();

        System.out.println("=== Debug Task ID: " + task.getId() + " ===");
        System.out.println("Name: " + task.getName());
        System.out.println("Description: " + task.getDescription());
        System.out.println("Status: " + task.getStatus());
        System.out.println("Deadline: " + task.getDeadline());
        System.out.println("Sprint: " + (task.getSprint() == null ? "null" : task.getSprint().getId()));

        if (task.getAssignee() != null) {
            System.out.println("Assignee ID: " + task.getAssignee().getId());
            System.out.println("Assignee User: "
                    + (task.getAssignee().getUser() == null ? "null" : task.getAssignee().getUser().getFullName()));
        } else {
            System.out.println("Assignee: null");
        }

        if (task.getMentor() != null) {
            System.out.println("Mentor ID: " + task.getMentor().getId());
            System.out.println("Mentor User: "
                    + (task.getMentor().getUser() == null ? "null" : task.getMentor().getUser().getFullName()));
        } else {
            System.out.println("Mentor: null");
        }

        if (task.getCreatedBy() != null) {
            System.out.println("CreatedBy ID: " + task.getCreatedBy().getId());
            System.out.println("CreatedBy Name: " + task.getCreatedBy().getFullName());
        } else {
            System.out.println("CreatedBy: null");
        }

        // Gán dữ liệu vào response
        response.setId(task.getId());
        response.setName(task.getName());
        response.setDescription(task.getDescription());
        response.setStatus(task.getStatus());
        response.setDeadline(task.getDeadline());
        response.setSprint_Id(task.getSprint() == null ? null : task.getSprint().getId());

        if (task.getAssignee() != null) {
            response.setAssignee_Id(task.getAssignee().getId());
            if (task.getAssignee().getUser() != null) {
                response.setAssigneeName(task.getAssignee().getUser().getFullName());
            }
        }

        if (task.getMentor() != null) {
            response.setMentorId(task.getMentor().getId());
            if (task.getMentor().getUser() != null) {
                response.setMentorName(task.getMentor().getUser().getFullName());
            }
        }

        if (task.getCreatedBy() != null) {
            response.setCreatedById(task.getCreatedBy().getId());
            response.setCreatedByName(task.getCreatedBy().getFullName());
        }

        return response;
    }

    @Override
    public List<TaskResponse> getTasksByAssignee(Integer assigneeId) {
        List<Task> tasks = taskRepository.findByAssigneeId(assigneeId);
        return tasks.stream()
                .map(this::mapToTaskResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TaskResponse getTaskById(Long taskId) {
        User user = authService.getUserLogin();

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.SPRINT_NOT_EXISTS.getMessage()));

        Sprint sprint = task.getSprint();

        checkTaskManagementPermission(user, sprint, "view");

        return mapToTaskResponse(task);
    }

    @Override
    public List<TaskResponse> getTasksByTeam(String teamId) {
        // Optional: Add permission check to ensure user is part of the team
        List<Task> tasks = taskRepository.findByTeamId(teamId);
        return tasks.stream()
                .map(this::mapToTaskResponse)
                .collect(Collectors.toList());
    }
}
