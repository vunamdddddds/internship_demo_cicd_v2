package com.example.InternShip.controller;

import com.example.InternShip.dto.task.request.BatchTaskUpdateRequest;
import com.example.InternShip.dto.task.request.CreateTaskRequest;
import com.example.InternShip.dto.task.request.UpdateTaskRequest;
import com.example.InternShip.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.InternShip.entity.enums.TaskStatus;

import jakarta.validation.Valid;

@RestController

@RequestMapping("/api/v1")

@RequiredArgsConstructor

public class TaskController {
    private final TaskService taskService;

    @GetMapping("/teams/{teamId}/tasks")
    public ResponseEntity<?> getTasksByTeam(@PathVariable String teamId) {
        return ResponseEntity.ok(taskService.getTasksByTeam(teamId));
    }

    @PostMapping("/sprints/{sprintId}/tasks")
    public ResponseEntity<?> createTask(@PathVariable Long sprintId, @RequestBody @Valid CreateTaskRequest request) {
        request.setSprintId(sprintId);
        return ResponseEntity.ok(taskService.createTask(request));
    }

    @GetMapping("/sprints/{sprintId}/tasks")
    public ResponseEntity<?> getTasksBySprint(
            @PathVariable Long sprintId,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) Integer assigneeId) {
        return ResponseEntity.ok(taskService.getTasksBySprint(sprintId, status, assigneeId));
    }

    @GetMapping("/assignees/{assigneeId}/tasks")
    public ResponseEntity<?> getTasksByAssignee(@PathVariable Integer assigneeId) {
        return ResponseEntity.ok(taskService.getTasksByAssignee(assigneeId));
    }

    @PutMapping("/tasks/{taskId}")
    public ResponseEntity<?> updateTask(@PathVariable Long taskId, @RequestBody UpdateTaskRequest request) {
        return ResponseEntity.ok(taskService.updateTask(taskId, request));
    }

    @DeleteMapping("/tasks/{taskId}")
    public ResponseEntity<?> deleteTask(@PathVariable Long taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/tasks/{taskId}")
    public ResponseEntity<?> getTaskById(@PathVariable Long taskId) {
        return ResponseEntity.ok(taskService.getTaskById(taskId));
    }

    @PostMapping("/tasks/batch-update")
    public ResponseEntity<?> batchUpdateTasks(@RequestBody @Valid BatchTaskUpdateRequest request) {
        taskService.batchUpdateTasks(request);
        return ResponseEntity.ok().build();
    }
}
