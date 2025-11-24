package com.example.InternShip.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.InternShip.entity.University;

public interface UniversityRepository extends JpaRepository<University,Integer> {
}
