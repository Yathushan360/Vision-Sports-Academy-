package lk.sliit.visionacademy.sports_academy_management.repository;

import lk.sliit.visionacademy.sports_academy_management.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    List<Attendance> findBySessionId(Long sessionId);

    List<Attendance> findByPlayerId(Long playerId);
}
