package com.example.InternShip.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@Entity
public class LeaveRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "intern_id", nullable = false)
    private Intern intern;

    private LocalDate date;

    private String reason;

    private String attachedFileUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type;

    private Boolean approved;

    private String reasonReject;

    public enum Type {
        LATE, // Xin đi muộn
        EARLY_LEAVE, // Xin về sớm
        ON_LEAVE // Xin nghỉ phép
    }
}
