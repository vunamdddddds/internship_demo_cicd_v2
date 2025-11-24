package com.example.InternShip.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Entity
@Getter
@Setter
@Table(name = "internship_program")
public class InternshipProgram {
   @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private LocalDateTime endPublishedTime;
    private LocalDateTime endReviewingTime;
    private LocalDateTime timeStart;
    private LocalDateTime timeEnd;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    @OneToMany(mappedBy = "internshipProgram")
    private List<InternshipApplication> applications;

    @OneToMany(mappedBy = "internshipProgram")
    private List<Team> teams;

    @OneToMany(mappedBy = "internshipProgram")
    private List<Intern> interns;

    @Column(precision = 15, scale = 0, nullable = false)
    private BigDecimal defaultAllowance = BigDecimal.valueOf(0);

    public enum Status {
        DRAFT, // bản nháp
        PUBLISHED, // xuất bản
        REVIEWING, // Đang trong quá trình xem xét
        PENDING, // Thời gian nộp hợp đồng
        ONGOING, // đang thực hiện
        COMPLETED, // hoàn thành
        CANCELLED // huỷ bỏ
    }
}

