package lk.sliit.visionacademy.sports_academy_management.controller;

import lk.sliit.visionacademy.sports_academy_management.model.Performance;
import lk.sliit.visionacademy.sports_academy_management.service.PerformanceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/performances")
@CrossOrigin
public class PerformanceController {

    private final PerformanceService performanceService;

    public PerformanceController(PerformanceService performanceService) {
        this.performanceService = performanceService;
    }

    // Get all performances
    @GetMapping
    public ResponseEntity<List<Performance>> getAllPerformances() {
        return ResponseEntity.ok(
                performanceService.getAllPerformances()
        );
    }

    // Get one performance
    @GetMapping("/{id}")
    public ResponseEntity<Performance> getPerformanceById(
            @PathVariable Long id) {

        return performanceService.getPerformanceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Get performances of a player
    @GetMapping("/player/{playerId}")
    public ResponseEntity<List<Performance>> getPlayerPerformances(
            @PathVariable Long playerId) {

        return ResponseEntity.ok(
                performanceService.getPlayerPerformances(playerId)
        );
    }

    // Get performances of a training session
    @GetMapping("/session/{sessionId}")
    public ResponseEntity<List<Performance>> getSessionPerformances(
            @PathVariable Long sessionId) {

        return ResponseEntity.ok(
                performanceService.getSessionPerformances(sessionId)
        );
    }

    // Create performance
    @PostMapping
    public ResponseEntity<?> createPerformance(
            @RequestBody Performance performance) {

        try {

            Performance savedPerformance =
                    performanceService.savePerformance(performance);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(savedPerformance);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    // Update performance
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePerformance(
            @PathVariable Long id,
            @RequestBody Performance performance) {

        try {

            Performance updatedPerformance =
                    performanceService.updatePerformance(
                            id,
                            performance
                    );

            return ResponseEntity.ok(updatedPerformance);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    // Delete performance
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePerformance(
            @PathVariable Long id) {

        try {

            performanceService.deletePerformance(id);

            return ResponseEntity.ok(
                    "Performance deleted successfully."
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }
}