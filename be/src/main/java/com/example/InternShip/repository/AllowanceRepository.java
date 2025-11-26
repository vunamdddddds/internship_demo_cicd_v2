package com.example.InternShip.repository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.example.InternShip.entity.Allowance;

public interface AllowanceRepository extends JpaRepository<Allowance, Integer>, JpaSpecificationExecutor<Allowance> {
    Page<Allowance> findByIntern_Id(Integer internId, Pageable pageable);

    List<Allowance> findByAllowanceMonthBetween(LocalDate startDate, LocalDate endDate);
}
