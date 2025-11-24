package com.example.InternShip.repository;

import com.example.InternShip.entity.Team;
import com.example.InternShip.entity.WorkSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;
import java.util.Optional;

public interface WorkScheduleRepository extends JpaRepository<WorkSchedule, Integer> {
    Optional<WorkSchedule> findByTeamAndDayOfWeek(Team team, DayOfWeek dayOfWeek);
}
