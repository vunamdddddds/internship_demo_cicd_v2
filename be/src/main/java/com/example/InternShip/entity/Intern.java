package com.example.InternShip.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
@Entity
public class Intern {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @ManyToOne
    @JoinColumn(name = "major_id", nullable = false)
    private Major major;

    @ManyToOne
    @JoinColumn(name = "school_id")
    private University university;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(length = 512)
    private String cvUrl;

    @ManyToOne
    @JoinColumn(name = "internship_program_id", nullable = false)
    private InternshipProgram internshipProgram;

    @OneToMany(mappedBy = "intern")
    private List<Attendance> attendances;

    @OneToMany(mappedBy = "intern")
    private List<LeaveRequest> leaveRequests;

    @OneToMany(mappedBy = "assignee")
    private List<Task> tasks;

    @OneToMany(mappedBy = "intern")
    private List<Allowance> allowances;

    @OneToMany(mappedBy = "intern")
    private List<SupportRequest> supportRequests;

    @Column(precision = 4, scale = 2)
    private BigDecimal expertiseScore; //điểm chuyên môn

    @Column(precision = 4, scale = 2)
    private BigDecimal qualityScore;//điểm chất lượng

    @Column(precision = 4, scale = 2)
    private BigDecimal problemSolvingScore;//điểm giải quyết vấn đề

    @Column(precision = 4, scale = 2)
    private BigDecimal technologyLearningScore;//điểm học công nghệ mới

    @Enumerated(EnumType.STRING)
    private SoftSkill softSkill;//kĩ năng mềm

    @Column(columnDefinition = "TEXT")
    private String assessment;//nhận xét

    @Column(precision = 15, scale = 0)
    private BigDecimal allowanceOverride;

    public enum SoftSkill {
        GOOD, //tốt
        FAIR, //khá
        AVERAGE, //trung bình
        POOR //kém
    }

    public enum Status {
        ACTIVE, // đang thực tập
        SUSPENDED, // tạm dừng thực tập
        COMPLETED, // hoành thành thực tập
        DROPPED // dừng thực tập
    }
}
