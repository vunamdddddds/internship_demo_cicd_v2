package com.example.InternShip.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Allowance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(precision = 15, scale = 0)
    private BigDecimal amount;

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime paidAt;

    @ManyToOne
    @JoinColumn(name = "intern_id", nullable = false)
    private Intern intern;

    @ManyToOne
    @JoinColumn(name = "remitter_id")
    private User remitter;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    public enum Status{
        PENDING,
        PAID,
        CANCELED,
        DRAFT,
    }
}
