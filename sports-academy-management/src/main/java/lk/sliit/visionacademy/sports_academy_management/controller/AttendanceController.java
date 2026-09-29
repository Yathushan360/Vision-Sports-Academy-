package lk.sliit.visionacademy.sports_academy_management.controller;

import lk.sliit.visionacademy.sports_academy_management.model.Attendance;
import lk.sliit.visionacademy.sports_academy_management.service.AttendanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@CrossOrigin
public class AttendanceController {

    private final AttendanceService service;

    public AttendanceController(AttendanceService service) {
        this.service = service;
    }

    @GetMapping
    public List<Attendance> getAllAttendance() {
        return service.getAllAttendance();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Attendance> getAttendanceById(@PathVariable Long id) {

        return service.getAttendanceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/session/{sessionId}")
    public List<Attendance> getAttendanceBySession(
            @PathVariable Long sessionId) {

        return service.getAttendanceBySessionId(sessionId);
    }

    @GetMapping("/player/{playerId}")
    public List<Attendance> getAttendanceByPlayer(
            @PathVariable Long playerId) {

        return service.getAttendanceByPlayerId(playerId);
    }

    @PostMapping
    public Attendance createAttendance(
            @RequestBody Attendance attendance) {

        return service.saveAttendance(attendance);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Attendance> updateAttendance(
            @PathVariable Long id,
            @RequestBody Attendance attendance) {

        return service.getAttendanceById(id)
                .map(existingAttendance -> {

                    existingAttendance.setSessionId(
                            attendance.getSessionId());

                    existingAttendance.setPlayerId(
                            attendance.getPlayerId());

                    existingAttendance.setStatus(
                            attendance.getStatus());

                    existingAttendance.setRemarks(
                            attendance.getRemarks());

                    existingAttendance.setMarkedBy(
                            attendance.getMarkedBy());

                    return ResponseEntity.ok(
                            service.saveAttendance(existingAttendance));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAttendance(
            @PathVariable Long id) {

        if (service.getAttendanceById(id).isPresent()) {

            service.deleteAttendance(id);

            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
}