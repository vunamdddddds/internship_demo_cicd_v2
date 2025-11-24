package com.example.InternShip.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class SupportRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    private String evidenceFile;

    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "intern_id", nullable = false)
    private Intern intern;

    @ManyToOne
    @JoinColumn(name = "handler_id")
    private User handler;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.PENDING;

    @Column(columnDefinition = "TEXT")
    private String hrResponse;

    private LocalDateTime resolvedAt;

    public enum Status {
        PENDING,     // HR chưa xem / chưa xử lý
        IN_PROGRESS, // HR đang xử lý
        RESOLVED,    // Hoàn thành và đã phản hồi
        REJECTED     // Từ chối
    }
}
