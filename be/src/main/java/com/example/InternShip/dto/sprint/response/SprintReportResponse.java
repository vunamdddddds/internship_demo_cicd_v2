package com.example.InternShip.dto.sprint.response;

import lombok.Data;
import java.time.LocalDate;

@Data
public class SprintReportResponse {
    private Long id;
    private String name;
    private String reportUrl;
    private String reportStatus;
    private String mentorFeedback;
    private LocalDate endDate;
}