package com.example.InternShip.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Setter
@Getter
@Entity
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private LocalTime timeStart;
    private LocalTime timeEnd;

    private LocalTime checkIn;
    private LocalTime checkOut;

    private LocalDate date;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne
    @JoinColumn(name = "intern_id", nullable = false)
    private Intern intern;

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    public enum Status {
        CHECKED_IN,  // Mới check-in
        PRESENT,     // Hiện diện (check-out đúng giờ)
        LATE,        // Đi muộn
        EARLY_LEAVE, // Về sớm
        LATE_AND_EARLY_LEAVE, // Muộn và sớm
        ON_LEAVE,    // Nghỉ có phép (từ bảng LeaveRequest)
        ABSENT       // Vắng (sẽ được set bởi service tự động)
    }
}
