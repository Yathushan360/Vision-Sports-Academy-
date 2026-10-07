package lk.sliit.visionacademy.sports_academy_management.repository;

import lk.sliit.visionacademy.sports_academy_management.model.Performance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PerformanceRepository extends JpaRepository<Performance, Long> {

    Optional<Performance> findByPlayerIdAndSessionId(Long playerId, Long sessionId);

    List<Performance> findByPlayerId(Long playerId);

    List<Performance> findBySessionId(Long sessionId);
}