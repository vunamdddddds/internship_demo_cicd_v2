package com.example.InternShip.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Setter
@Getter
@Entity
@Table(
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"team_id", "day_of_week"})
    }
)
public class WorkSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    private LocalTime timeStart;
    private LocalTime timeEnd;

    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;
}