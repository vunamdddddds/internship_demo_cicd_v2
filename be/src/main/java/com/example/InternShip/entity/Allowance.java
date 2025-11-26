package com.example.InternShip.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
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

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "intern_id", nullable = false)
    private Intern intern;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "remitter_id")
    private User remitter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "allowance_package_id")
    private AllowancePackage allowancePackage;

    @Column(name = "allowance_month")
    private LocalDate allowanceMonth;

    @Column(columnDefinition = "TEXT") // Use TEXT for longer notes
    private String notes; // Renamed from 'note' to 'notes' to match schema

    @Column(name = "period")
    private LocalDate period; // Using LocalDate for date type in schema

    @Column(name = "work_days")
    private Integer workDays;

    @UpdateTimestamp
    @Column(name = "last_updated_at") // Renamed from 'updated_at' to 'last_updated_at' to match schema
    private LocalDateTime lastUpdatedAt;

    public enum Status{
        PENDING,
        PAID,
        CANCELED,
        DRAFT,
    }
}
