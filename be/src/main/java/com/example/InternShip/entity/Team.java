package com.example.InternShip.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "internshipProgram_id", nullable = false)
    private InternshipProgram internshipProgram;

    @ManyToOne
    @JoinColumn(name = "mentor_id", nullable = false)
    private Mentor mentor;

    @OneToMany(mappedBy = "team")
    private List<Intern> interns = new ArrayList<>();

    @OneToMany(mappedBy = "team")
    private List<WorkSchedule> workSchedules;

    @OneToMany(mappedBy = "team")
    private List<Attendance> attendances;

    @OneToMany(mappedBy = "team")
    private List<Sprint> sprints;

}
