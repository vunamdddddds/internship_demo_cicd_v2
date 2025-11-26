package com.example.InternShip.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "allowance_package")
public class AllowancePackage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, precision = 15, scale = 0)
    private BigDecimal amount;

    @Column(name = "required_work_days", columnDefinition = "int default 0")
    private Integer requiredWorkDays = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "internship_program_id", nullable = false)
    private InternshipProgram internshipProgram;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(20) default 'ACTIVE'")
    private Status status = Status.ACTIVE;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public enum Status {
        ACTIVE, INACTIVE
    }
}
