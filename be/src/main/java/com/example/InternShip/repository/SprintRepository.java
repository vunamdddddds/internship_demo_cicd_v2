package com.example.InternShip.repository;

import com.example.InternShip.entity.Sprint;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;


@Repository
public interface SprintRepository extends JpaRepository<Sprint, Long> {
    List<Sprint> findByTeamId(Integer teamId);

    Optional<Sprint> findById(Long id);
    List<Sprint> findByEndDate(LocalDate endDate);
}
