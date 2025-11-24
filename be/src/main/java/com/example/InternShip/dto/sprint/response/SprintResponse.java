package com.example.InternShip.dto.sprint.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class SprintResponse {
    private Long id;
    private String name;
    private String goal;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer teamId;
    private String reportUrl;
}