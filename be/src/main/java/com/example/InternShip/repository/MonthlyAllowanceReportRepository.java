package com.example.InternShip.repository;

import com.example.InternShip.entity.MonthlyAllowanceReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MonthlyAllowanceReportRepository extends JpaRepository<MonthlyAllowanceReport, Integer> {
}
