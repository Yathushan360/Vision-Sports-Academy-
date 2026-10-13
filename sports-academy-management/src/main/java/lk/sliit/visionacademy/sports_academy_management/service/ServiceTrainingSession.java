package lk.sliit.visionacademy.sports_academy_management.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lk.sliit.visionacademy.sports_academy_management.model.TrainingSession;
import lk.sliit.visionacademy.sports_academy_management.repository.TrainingSessionRepository;

@Service
public class ServiceTrainingSession {

    private final TrainingSessionRepository trainingSessionRepository;

    public ServiceTrainingSession(TrainingSessionRepository trainingSessionRepository) {
        this.trainingSessionRepository = trainingSessionRepository;
    }

    public List<TrainingSession> getAllTrainingSessions() {
        return trainingSessionRepository.findAll();
    }

    public TrainingSession getTrainingSessionById(Long id) {
        return trainingSessionRepository.findById(id).orElse(null);
    }

    public TrainingSession saveTrainingSession(TrainingSession trainingSession) {
        return trainingSessionRepository.save(trainingSession);
    }

    public TrainingSession updateTrainingSession(TrainingSession trainingSession) {
        return trainingSessionRepository.save(trainingSession);
    }

    public void deleteTrainingSession(Long id) {
        trainingSessionRepository.deleteById(id);
    }
}