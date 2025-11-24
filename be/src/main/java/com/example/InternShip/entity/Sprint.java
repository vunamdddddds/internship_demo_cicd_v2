package com.example.InternShip.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sprint")
public class Sprint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String goal;

    private LocalDate startDate;

    private LocalDate endDate;

    @Column(length = 512)
    private String reportUrl;

    private String feedbackGood;

    private String feedbackBad;

    private String feedbackImprove;

    @Enumerated(EnumType.STRING)
    private ReportStatus reportStatus;

    public enum ReportStatus {
        PENDING,   // Chưa nộp
        SUBMITTED, // Đã nộp (chờ mentor review)
        REVIEWED   // Mentor đã review
    }

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @OneToMany(mappedBy = "sprint", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks;
}
