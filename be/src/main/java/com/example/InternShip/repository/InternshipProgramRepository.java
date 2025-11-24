package com.example.InternShip.repository;

import com.example.InternShip.entity.InternshipProgram;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InternshipProgramRepository extends JpaRepository<InternshipProgram, Integer> {
    List<InternshipProgram> findAllByStatus(InternshipProgram.Status status);
    
    // Tùng
    @Query("""
        SELECT ip
        FROM InternshipProgram ip
        LEFT JOIN ip.department d
        WHERE
            (:department IS NULL OR d.id IN :department)
            AND (
            :keyword IS NULL OR :keyword = ''
            OR LOWER(ip.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
            )
        """)
        Page<InternshipProgram> searchInternshipProgram(
            @Param("department") List<Integer> department,
            @Param("keyword") String keyword,
            Pageable pageable);
}
