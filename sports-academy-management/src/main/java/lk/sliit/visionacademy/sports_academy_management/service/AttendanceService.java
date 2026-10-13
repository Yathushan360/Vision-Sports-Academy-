package lk.sliit.visionacademy.sports_academy_management.service;

import lk.sliit.visionacademy.sports_academy_management.model.Attendance;
import lk.sliit.visionacademy.sports_academy_management.repository.AttendanceRepository;
import lk.sliit.visionacademy.sports_academy_management.repository.PlayerRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AttendanceService {

    private final AttendanceRepository repository;
    private final PlayerRepository playerRepository;

    public AttendanceService(
            AttendanceRepository repository,
            PlayerRepository playerRepository) {
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

        if (attendance == null) {
            throw new IllegalArgumentException(
                    "Attendance information is required."
            );
        }

        if (attendance.getPlayerId() == null) {
            throw new IllegalArgumentException(
                    "Select a player before marking attendance."
            );
        }

        if (!playerRepository.existsById(attendance.getPlayerId())) {
            throw new IllegalArgumentException(
                    "Player ID " + attendance.getPlayerId()
                            + " does not exist."
            );
        }

        if (attendance.getSessionId() == null) {
            throw new IllegalArgumentException(
                    "Select a training session before marking attendance."
            );
        }

        /*
         * Check whether this player already has attendance
         * recorded for this training session.
         */
        Attendance existingAttendance =
                repository.findBySessionIdAndPlayerId(
                        attendance.getSessionId(),
                        attendance.getPlayerId()
                );

        /*
         * Update the existing record instead of creating
         * a duplicate attendance record.
         */
        if (existingAttendance != null) {
            existingAttendance.setStatus(attendance.getStatus());
            existingAttendance.setRemarks(attendance.getRemarks());
            existingAttendance.setMarkedBy(attendance.getMarkedBy());
            existingAttendance.setMarkedAt(LocalDateTime.now());

            return repository.save(existingAttendance);
        }

        /*
         * Create a new attendance record when one
         * does not already exist.
         */
        if (attendance.getMarkedAt() == null) {
            attendance.setMarkedAt(LocalDateTime.now());
        }

        return repository.save(attendance);
    }

    public void deleteAttendance(Long id) {
        repository.deleteById(id);
    }
}