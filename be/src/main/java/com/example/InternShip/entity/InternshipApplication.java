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
@Table(name = "internship_application")
public class InternshipApplication {

      @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "internship_term_id", nullable = false)
    private InternshipProgram internshipProgram;

    @ManyToOne
    @JoinColumn(name = "school_id")
    private University university;

    @ManyToOne
    @JoinColumn(name = "major_id")
    private Major major;

    @Column(length = 512)
    private String cvUrl;

    @Column(length = 512)
    private String internshipContractUrl;

    @Column(length = 512)
    private String internshipApplicationtUrl;

    private BigDecimal score;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.SUBMITTED;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public enum Status {
        SUBMITTED, // đã nộp
        UNDER_REVIEW, // đang xem xét
        APPROVED, // đã duyệt
        CONFIRM, // đã xác nhận hợp đồng thực tập
        REJECTED, // bị từ chối
        WITHDRAWN, // rút lại
        NOT_CONTRACT // không nộp hợp đồng
    }
}
