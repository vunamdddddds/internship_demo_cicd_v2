package com.example.InternShip.service;

import java.util.List;

import com.example.InternShip.entity.InternshipApplication;
import com.example.InternShip.entity.Sprint;
import com.example.InternShip.entity.Task;

public interface EmailService {
    void sendApplicationStatusEmail(InternshipApplication application);
    void sendSprintCompletionEmail(Sprint sprint,List<Task> completedTasks, List<Task> incompleteTasks);    
}
