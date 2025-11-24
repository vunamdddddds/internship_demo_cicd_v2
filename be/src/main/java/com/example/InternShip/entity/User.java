package com.example.InternShip.entity;

import com.example.InternShip.entity.enums.Role;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "`user`")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true)
    private String googleId;

    private String password;

    @Column(nullable = false)
    private String fullName;

    @OneToOne(mappedBy = "user")
    private Intern intern;

    @OneToOne(mappedBy = "user")
    private Mentor mentor;

    @OneToMany(mappedBy = "user")
    private List<InternshipApplication> internshipApplication;

    @OneToMany(mappedBy = "hr")
    private List<Conversation> hrConversations;

    @OneToMany(mappedBy = "remitter")
    private List<Allowance> allowances;

    @OneToMany(mappedBy = "handler")
    private List<SupportRequest> supportRequests;

    private String phone;

    private String address;

    private String avatarUrl;

    private boolean isActive = true;

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
}
