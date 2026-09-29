package lk.sliit.visionacademy.sports_academy_management.service;

import lk.sliit.visionacademy.sports_academy_management.model.Attendance;
import lk.sliit.visionacademy.sports_academy_management.repository.AttendanceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AttendanceService {

    private final AttendanceRepository repository;

    public AttendanceService(AttendanceRepository repository) {
        this.repository = repository;
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

        if (attendance.getMarkedAt() == null) {
            attendance.setMarkedAt(java.time.LocalDateTime.now());
        }

        return repository.save(attendance);
    }

    public void deleteAttendance(Long id) {
        repository.deleteById(id);
    }
}