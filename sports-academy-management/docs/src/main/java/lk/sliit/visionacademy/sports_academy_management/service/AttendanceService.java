package lk.sliit.visionacademy.sports_academy_management.service;

import lk.sliit.visionacademy.sports_academy_management.model.Attendance;
import lk.sliit.visionacademy.sports_academy_management.repository.AttendanceRepository;
import lk.sliit.visionacademy.sports_academy_management.repository.PlayerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AttendanceService {

    private final AttendanceRepository repository;
    private final PlayerRepository playerRepository;

    public AttendanceService(AttendanceRepository repository, PlayerRepository playerRepository) {
        this.repository = repository;
        this.playerRepository = playerRepository;
    }

    public List<Attendance> getAllAttendance() {
        return repository.findAll();
    }

    public Optional<Attendance> getAttendanceById(Long id) {
        return repository.findById(id);
    }

    public List<Attendance> getAttendanceBySessionId(Long sessionId) {
        return repository.findBySessionId(sessionId);
    }

    public List<Attendance> getAttendanceByPlayerId(Long playerId) {
        return repository.findByPlayerId(playerId);
    }

    public Attendance saveAttendance(Attendance attendance) {

        if (attendance == null || attendance.getPlayerId() == null) {
            throw new IllegalArgumentException("Select a player from Member 1's Player Management module.");
        }
        if (!playerRepository.existsById(attendance.getPlayerId())) {
            throw new IllegalArgumentException("Player ID " + attendance.getPlayerId() + " does not exist in Member 1's player profiles.");
        }

        /*
         * Check whether this player already has
         * attendance for this training session.
         */
        Attendance existingAttendance =
                repository.findBySessionIdAndPlayerId(
                        attendance.getSessionId(),
                        attendance.getPlayerId()
                );

        /*
         * If attendance already exists,
         * update the existing record instead
         * of creating a duplicate.
         */
        if (existingAttendance != null) {

            existingAttendance.setStatus(
                    attendance.getStatus()
            );

            existingAttendance.setRemarks(
                    attendance.getRemarks()
            );

            existingAttendance.setMarkedBy(
                    attendance.getMarkedBy()
            );

            existingAttendance.setMarkedAt(
                    java.time.LocalDateTime.now()
            );

            return repository.save(existingAttendance);
        }

        /*
         * If no attendance exists,
         * create a new record.
         */
        if (attendance.getMarkedAt() == null) {

            attendance.setMarkedAt(
                    java.time.LocalDateTime.now()
            );
        }

        return repository.save(attendance);
    }

    public void deleteAttendance(Long id) {
        repository.deleteById(id);
    }
}