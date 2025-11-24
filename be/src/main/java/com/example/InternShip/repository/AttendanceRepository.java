package com.example.InternShip.repository;

import com.example.InternShip.dto.attendance.response.GetAllAttendanceResponse;
import com.example.InternShip.entity.Attendance;
import com.example.InternShip.entity.Intern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {
    // Tìm bản ghi chấm công của 1 intern, trong 1 ngày cụ thể
    Optional<Attendance> findByInternAndDate(Intern intern, LocalDate date);

    List<Attendance> findAllByTeamId(int teamId);

    List<Attendance> findByInternAndDateBetweenOrderByDateAsc(Intern intern, LocalDate startDate, LocalDate endDate);

    public interface AttendanceSummaryProjection {
        Integer getInternId();

        long getTotalWorkingDays();

        long getTotalOnLeaveDays();

        long getTotalAbsentDays();
    }

    @Query("SELECT " +
            "    a.intern.id as internId, " +
            "    SUM(CASE WHEN a.status IN ('PRESENT', 'LATE', 'EARLY_LEAVE', 'LATE_AND_EARLY_LEAVE') THEN 1 ELSE 0 END) as totalWorkingDays, " +
            "    SUM(CASE WHEN a.status = 'ON_LEAVE' THEN 1 ELSE 0 END) as totalOnLeaveDays, " +
            "    SUM(CASE WHEN a.status = 'ABSENT' THEN 1 ELSE 0 END) as totalAbsentDays " +
            "FROM Attendance a " +
            "JOIN a.team t " +
            "WHERE (:teamId IS NULL OR t.id = :teamId) " +
            "AND (:internshipProgramId IS NULL OR t.internshipProgram.id = :internshipProgramId) " +
            "GROUP BY a.intern.id")
    List<AttendanceSummaryProjection> getAttendanceSummary(
            @Param("teamId") Integer teamId,
            @Param("internshipProgramId") Integer internshipProgramId);

    @Query(value = """
                SELECT
                    i.id AS internId,
                    i.internship_program_id AS internship_program_id,
                    u.full_name AS internName,
                    t.name AS teamName,
                    SUM(CASE WHEN a.status = 'PRESENT' THEN 1 ELSE 0 END) AS presentDay,
                    SUM(CASE WHEN a.status = 'ABSENT' THEN 1 ELSE 0 END) AS absentDay,
                    SUM(CASE WHEN a.status = 'LATE_AND_EARLY_LEAVE' THEN 1 ELSE 0 END) AS lateAndLeaveDay
                FROM attendance a
                JOIN intern i ON a.intern_id = i.id
                JOIN `user` u ON i.user_id = u.id
                JOIN team t ON a.team_id = t.id
                WHERE i.id = :internId
                GROUP BY i.id, u.full_name, t.name
            """, nativeQuery = true)
    GetAllAttendanceResponse findAttendanceSummaryByInternId(@Param("internId") Integer internId);
}
