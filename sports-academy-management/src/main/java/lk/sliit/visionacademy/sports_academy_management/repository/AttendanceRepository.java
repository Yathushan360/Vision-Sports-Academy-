package lk.sliit.visionacademy.sports_academy_management.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import lk.sliit.visionacademy.sports_academy_management.model.Attendance;

public interface AttendanceRepository
        extends JpaRepository<Attendance, Long> {

    List<Attendance> findBySessionId(Long sessionId);

    List<Attendance> findByPlayerId(Long playerId);

    boolean existsBySessionIdAndPlayerId(
            Long sessionId,
            Long playerId
    );

    Attendance findBySessionIdAndPlayerId(
            Long sessionId,
            Long playerId
    );
}