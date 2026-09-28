package lk.sliit.visionacademy.sports_academy_management.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lk.sliit.visionacademy.sports_academy_management.model.TrainingSession;
import lk.sliit.visionacademy.sports_academy_management.service.ServiceTrainingSession;

@RestController
@RequestMapping("/api/training-sessions")
public class TrainingSessionController {

    private final ServiceTrainingSession serviceTrainingSession;

    public TrainingSessionController(ServiceTrainingSession serviceTrainingSession) {
        this.serviceTrainingSession = serviceTrainingSession;
    }

    @GetMapping
    public List<TrainingSession> getAllTrainingSessions() {
        return serviceTrainingSession.getAllTrainingSessions();
    }

    @GetMapping("/{id}")
    public TrainingSession getTrainingSessionById(@PathVariable Long id) {
        return serviceTrainingSession.getTrainingSessionById(id);
    }

    @PostMapping
    public TrainingSession createTrainingSession(@RequestBody TrainingSession trainingSession) {
        return serviceTrainingSession.saveTrainingSession(trainingSession);
    }

    @PutMapping("/{id}")
    public TrainingSession updateTrainingSession(
            @PathVariable Long id,
            @RequestBody TrainingSession trainingSession) {

        trainingSession.setId(id);
        return serviceTrainingSession.updateTrainingSession(trainingSession);
    }

    @DeleteMapping("/{id}")
    public void deleteTrainingSession(@PathVariable Long id) {
        serviceTrainingSession.deleteTrainingSession(id);
    }
}