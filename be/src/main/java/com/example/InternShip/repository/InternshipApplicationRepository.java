package com.example.InternShip.repository;

import com.example.InternShip.entity.InternshipApplication;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface InternshipApplicationRepository extends JpaRepository<InternshipApplication, Integer> {
    Optional<InternshipApplication> findByUserIdAndStatus(Integer userId, InternshipApplication.Status status);
    List<InternshipApplication> findAllByUserId(Integer userId);

     @Query("""
    SELECT a
    FROM InternshipApplication a
    JOIN a.user u
    JOIN a.internshipProgram p
    LEFT JOIN a.university uv
    LEFT JOIN a.major m
    WHERE (:internshipTerm IS NULL OR p.id = :internshipTerm)
      AND (:university IS NULL OR uv.id = :university)
      AND (:major IS NULL OR m.id = :major)
      AND (:status IS NULL OR a.status = :status)
      AND (
            :keyword IS NULL OR :keyword = '' OR
            u.fullName LIKE %:keyword% OR
            u.email LIKE %:keyword% OR
            u.phone LIKE %:keyword%
          )
        """)
        Page<InternshipApplication> searchApplications(
                @Param("internshipTerm") Integer internshipTerm,
                @Param("university") Integer university,
                @Param("major") Integer major,
                @Param("keyword") String keyword,
                @Param("status") InternshipApplication.Status status,
                Pageable pageable
                );
}
