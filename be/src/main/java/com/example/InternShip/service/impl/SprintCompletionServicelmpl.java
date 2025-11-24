package com.example.InternShip.service.impl;

import com.example.InternShip.entity.Sprint;
import com.example.InternShip.entity.Task;
import com.example.InternShip.entity.enums.TaskStatus;
import com.example.InternShip.repository.SprintRepository;
import com.example.InternShip.service.EmailService;
import com.example.InternShip.service.SprintCompletionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SprintCompletionServicelmpl implements SprintCompletionService {
   private final SprintRepository sprintRepository;
    private final EmailService emailService;

    @Override
    @Transactional(readOnly = true)
    public void processAndNotifyFinishedSprints() {
        LocalDate today = LocalDate.now();
        log.info("Bắt đầu quá trình kiểm tra các sprint kết thúc vào ngày: {}", today);

        List<Sprint> finishedSprints = sprintRepository.findByEndDate(today);

        if (finishedSprints.isEmpty()) {
            log.info("Không có sprint nào kết thúc vào ngày hôm nay.");
            return;
        }

        log.info("Tìm thấy {} sprint kết thúc hôm nay. Đang xử lý...", finishedSprints.size());

        for (Sprint sprint : finishedSprints) {
            try {
                List<Task> allTasks = sprint.getTasks();
                if (allTasks == null || allTasks.isEmpty()) {
                    log.info("Sprint '{}' (ID: {}) không có task nào. Bỏ qua việc gửi email.", sprint.getName(), sprint.getId());
                    continue;
                }

                List<Task> completedTasks = allTasks.stream()
                        .filter(task -> task.getStatus() == TaskStatus.DONE)
                        .collect(Collectors.toList());

                List<Task> incompleteTasks = allTasks.stream()
                        .filter(task -> task.getStatus() != TaskStatus.DONE)
                        .collect(Collectors.toList());

                emailService.sendSprintCompletionEmail(sprint, completedTasks, incompleteTasks);

            } catch (Exception e) {
                log.error("Đã xảy ra lỗi khi xử lý sprint ID {}: {}", sprint.getId(), e.getMessage(), e);
            }
        }
        log.info("Hoàn tất quá trình xử lý các sprint kết thúc.");
    }
    
}
