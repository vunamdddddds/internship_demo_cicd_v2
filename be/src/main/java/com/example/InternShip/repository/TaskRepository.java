package com.example.InternShip.repository;

import com.example.InternShip.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {
    List<Task> findByAssigneeId(Integer assigneeId);

    @Query("SELECT t FROM Task t WHERE t.team.id = :teamId")
    List<Task> findByTeamId(@Param("teamId") String teamId);
}
