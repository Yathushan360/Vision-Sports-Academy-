package lk.sliit.visionacademy.sports_academy_management.repository;

import lk.sliit.visionacademy.sports_academy_management.model.TrainingSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainingSessionRepository extends JpaRepository<TrainingSession, Long> {
}