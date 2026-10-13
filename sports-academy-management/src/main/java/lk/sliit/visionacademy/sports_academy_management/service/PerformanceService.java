package lk.sliit.visionacademy.sports_academy_management.service;

import lk.sliit.visionacademy.sports_academy_management.model.Performance;
import lk.sliit.visionacademy.sports_academy_management.repository.PerformanceRepository;
import lk.sliit.visionacademy.sports_academy_management.repository.PlayerRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PerformanceService {

    private final PerformanceRepository performanceRepository;
    private final PlayerRepository playerRepository;

    public PerformanceService(
            PerformanceRepository performanceRepository,
            PlayerRepository playerRepository) {
        this.performanceRepository = performanceRepository;
        this.playerRepository = playerRepository;
    }

    // ============================================================
    // SAVE NEW PERFORMANCE
    // ============================================================

    public Performance savePerformance(Performance performance) {

        if (performance == null) {
            throw new RuntimeException("Performance data is required.");
        }

        validatePerformance(performance);
        performance.setPosition(performance.getPosition().trim());

        Optional<Performance> existing =
                performanceRepository.findByPlayerIdAndSessionId(
                        performance.getPlayerId(),
                        performance.getSessionId()
                );

        if (existing.isPresent()) {
            throw new RuntimeException(
                    "Performance already exists for this player and training session."
            );
        }

        calculateOverallScore(performance);
        performance.setAssessedAt(LocalDateTime.now());

        return performanceRepository.save(performance);
    }

    // ============================================================
    // UPDATE EXISTING PERFORMANCE
    // ============================================================

    public Performance updatePerformance(Long id, Performance performance) {

        if (id == null) {
            throw new RuntimeException("Performance ID is required.");
        }

        if (performance == null) {
            throw new RuntimeException("Performance data is required.");
        }

        Performance existing = performanceRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Performance not found with id: " + id
                        )
                );

        validatePerformance(performance);
        performance.setPosition(performance.getPosition().trim());

        Optional<Performance> duplicate =
                performanceRepository.findByPlayerIdAndSessionId(
                        performance.getPlayerId(),
                        performance.getSessionId()
                );

        if (duplicate.isPresent()
                && duplicate.get().getId() != null
                && !duplicate.get().getId().equals(id)) {
            throw new RuntimeException(
                    "Another performance already exists for this player and training session."
            );
        }

        // Basic information
        existing.setPlayerId(performance.getPlayerId());
        existing.setSessionId(performance.getSessionId());
        existing.setPosition(performance.getPosition());

        // Common technical skills
        existing.setPassing(performance.getPassing());
        existing.setBallControl(performance.getBallControl());
        existing.setFirstTouch(performance.getFirstTouch());
        existing.setDribbling(performance.getDribbling());
        existing.setShooting(performance.getShooting());
        existing.setFinishing(performance.getFinishing());
        existing.setCrossing(performance.getCrossing());
        existing.setHeading(performance.getHeading());
        existing.setTackling(performance.getTackling());
        existing.setMarking(performance.getMarking());
        existing.setWeakFoot(performance.getWeakFoot());

        // Common physical skills
        existing.setSpeed(performance.getSpeed());
        existing.setAcceleration(performance.getAcceleration());
        existing.setAgility(performance.getAgility());
        existing.setStamina(performance.getStamina());
        existing.setStrength(performance.getStrength());
        existing.setBalance(performance.getBalance());
        existing.setCoordination(performance.getCoordination());
        existing.setReaction(performance.getReaction());

        // Common tactical skills
        existing.setPositioning(performance.getPositioning());
        existing.setDecisionMaking(performance.getDecisionMaking());
        existing.setVision(performance.getVision());
        existing.setOffTheBallMovement(performance.getOffTheBallMovement());
        existing.setTeamwork(performance.getTeamwork());
        existing.setDefensiveAwareness(performance.getDefensiveAwareness());
        existing.setAttackingAwareness(performance.getAttackingAwareness());
        existing.setGameUnderstanding(performance.getGameUnderstanding());
        existing.setDiscipline(performance.getDiscipline());

        // Common mental skills
        existing.setConcentration(performance.getConcentration());
        existing.setConfidence(performance.getConfidence());
        existing.setCommunication(performance.getCommunication());
        existing.setLeadership(performance.getLeadership());
        existing.setWorkRate(performance.getWorkRate());
        existing.setDetermination(performance.getDetermination());
        existing.setCoachability(performance.getCoachability());
        existing.setComposure(performance.getComposure());

        // Defender skills
        existing.setDefensivePositioning(performance.getDefensivePositioning());
        existing.setInterceptions(performance.getInterceptions());
        existing.setOneVsOneDefending(performance.getOneVsOneDefending());
        existing.setAerialAbility(performance.getAerialAbility());
        existing.setClearance(performance.getClearance());

        // Midfielder skills
        existing.setShortPassing(performance.getShortPassing());
        existing.setLongPassing(performance.getLongPassing());
        existing.setCreativity(performance.getCreativity());
        existing.setThroughBalls(performance.getThroughBalls());
        existing.setBallRetention(performance.getBallRetention());
        existing.setPressing(performance.getPressing());
        existing.setChanceCreation(performance.getChanceCreation());
        existing.setMidfieldPositioning(performance.getMidfieldPositioning());

        // Forward skills
        existing.setAttackingPositioning(performance.getAttackingPositioning());
        existing.setOneVsOneAttacking(performance.getOneVsOneAttacking());
        existing.setShotAccuracy(performance.getShotAccuracy());

        // Goalkeeper skills
        existing.setShotStopping(performance.getShotStopping());
        existing.setReflexes(performance.getReflexes());
        existing.setHandling(performance.getHandling());
        existing.setCatching(performance.getCatching());
        existing.setDiving(performance.getDiving());
        existing.setOneVsOneGoalkeeping(performance.getOneVsOneGoalkeeping());
        existing.setGoalkeeperPositioning(performance.getGoalkeeperPositioning());
        existing.setDistribution(performance.getDistribution());
        existing.setKicking(performance.getKicking());
        existing.setThrowing(performance.getThrowing());
        existing.setCommandOfArea(performance.getCommandOfArea());
        existing.setCrossManagement(performance.getCrossManagement());
        existing.setGoalkeeperDecisionMaking(
                performance.getGoalkeeperDecisionMaking()
        );

        // Coach comments and development goals
        existing.setCoachComments(performance.getCoachComments());
        existing.setDevelopmentGoals(performance.getDevelopmentGoals());

        calculateOverallScore(existing);
        existing.setAssessedAt(LocalDateTime.now());

        return performanceRepository.save(existing);
    }

    // ============================================================
    // GET ALL PERFORMANCES
    // ============================================================

    public List<Performance> getAllPerformances() {
        return performanceRepository.findAll();
    }

    // ============================================================
    // GET PERFORMANCE BY ID
    // ============================================================

    public Optional<Performance> getPerformanceById(Long id) {
        if (id == null) {
            return Optional.empty();
        }

        return performanceRepository.findById(id);
    }

    // ============================================================
    // GET PERFORMANCES BY PLAYER
    // ============================================================

    public List<Performance> getPlayerPerformances(Long playerId) {
        if (playerId == null) {
            throw new RuntimeException("Player ID is required.");
        }

        return performanceRepository.findByPlayerId(playerId);
    }

    // ============================================================
    // GET PERFORMANCES BY TRAINING SESSION
    // ============================================================

    public List<Performance> getSessionPerformances(Long sessionId) {
        if (sessionId == null) {
            throw new RuntimeException("Training session ID is required.");
        }

        return performanceRepository.findBySessionId(sessionId);
    }

    // ============================================================
    // DELETE PERFORMANCE
    // ============================================================

    public void deletePerformance(Long id) {
        if (id == null) {
            throw new RuntimeException("Performance ID is required.");
        }

        if (!performanceRepository.existsById(id)) {
            throw new RuntimeException(
                    "Performance not found with id: " + id
            );
        }

        performanceRepository.deleteById(id);
    }

    // ============================================================
    // VALIDATION
    // ============================================================

    private void validatePerformance(Performance performance) {

        if (performance == null) {
            throw new RuntimeException("Performance data is required.");
        }

        if (performance.getPlayerId() == null) {
            throw new RuntimeException("Player is required.");
        }

        if (!playerRepository.existsById(performance.getPlayerId())) {
            throw new RuntimeException(
                    "Player ID " + performance.getPlayerId() + " does not exist."
            );
        }

        if (performance.getSessionId() == null) {
            throw new RuntimeException("Training session is required.");
        }

        if (performance.getPosition() == null
                || performance.getPosition().trim().isEmpty()) {
            throw new RuntimeException("Player position is required.");
        }

        // Common technical skills
        checkScore(performance.getPassing(), "Passing");
        checkScore(performance.getBallControl(), "Ball Control");
        checkScore(performance.getFirstTouch(), "First Touch");
        checkScore(performance.getDribbling(), "Dribbling");
        checkScore(performance.getShooting(), "Shooting");
        checkScore(performance.getFinishing(), "Finishing");
        checkScore(performance.getCrossing(), "Crossing");
        checkScore(performance.getHeading(), "Heading");
        checkScore(performance.getTackling(), "Tackling");
        checkScore(performance.getMarking(), "Marking");
        checkScore(performance.getWeakFoot(), "Weak Foot");

        // Common physical skills
        checkScore(performance.getSpeed(), "Speed");
        checkScore(performance.getAcceleration(), "Acceleration");
        checkScore(performance.getAgility(), "Agility");
        checkScore(performance.getStamina(), "Stamina");
        checkScore(performance.getStrength(), "Strength");
        checkScore(performance.getBalance(), "Balance");
        checkScore(performance.getCoordination(), "Coordination");
        checkScore(performance.getReaction(), "Reaction");

        // Common tactical skills
        checkScore(performance.getPositioning(), "Positioning");
        checkScore(performance.getDecisionMaking(), "Decision Making");
        checkScore(performance.getVision(), "Vision");
        checkScore(
                performance.getOffTheBallMovement(),
                "Off The Ball Movement"
        );
        checkScore(performance.getTeamwork(), "Teamwork");
        checkScore(
                performance.getDefensiveAwareness(),
                "Defensive Awareness"
        );
        checkScore(
                performance.getAttackingAwareness(),
                "Attacking Awareness"
        );
        checkScore(
                performance.getGameUnderstanding(),
                "Game Understanding"
        );
        checkScore(performance.getDiscipline(), "Discipline");

        // Common mental skills
        checkScore(performance.getConcentration(), "Concentration");
        checkScore(performance.getConfidence(), "Confidence");
        checkScore(performance.getCommunication(), "Communication");
        checkScore(performance.getLeadership(), "Leadership");
        checkScore(performance.getWorkRate(), "Work Rate");
        checkScore(performance.getDetermination(), "Determination");
        checkScore(performance.getCoachability(), "Coachability");
        checkScore(performance.getComposure(), "Composure");

        // Position-specific skills
        String position = performance.getPosition().trim();

        if (position.equalsIgnoreCase("Defender")) {
            checkScore(
                    performance.getDefensivePositioning(),
                    "Defensive Positioning"
            );
            checkScore(performance.getInterceptions(), "Interceptions");
            checkScore(
                    performance.getOneVsOneDefending(),
                    "1v1 Defending"
            );
            checkScore(performance.getAerialAbility(), "Aerial Ability");
            checkScore(performance.getClearance(), "Clearance");

        } else if (position.equalsIgnoreCase("Midfielder")) {
            checkScore(performance.getShortPassing(), "Short Passing");
            checkScore(performance.getLongPassing(), "Long Passing");
            checkScore(performance.getCreativity(), "Creativity");
            checkScore(performance.getThroughBalls(), "Through Balls");
            checkScore(performance.getBallRetention(), "Ball Retention");
            checkScore(performance.getPressing(), "Pressing");
            checkScore(performance.getChanceCreation(), "Chance Creation");
            checkScore(
                    performance.getMidfieldPositioning(),
                    "Midfield Positioning"
            );

        } else if (position.equalsIgnoreCase("Forward")) {
            checkScore(
                    performance.getAttackingPositioning(),
                    "Attacking Positioning"
            );
            checkScore(
                    performance.getOneVsOneAttacking(),
                    "1v1 Attacking"
            );
            checkScore(performance.getShotAccuracy(), "Shot Accuracy");

        } else if (position.equalsIgnoreCase("Goalkeeper")) {
            checkScore(performance.getShotStopping(), "Shot Stopping");
            checkScore(performance.getReflexes(), "Reflexes");
            checkScore(performance.getHandling(), "Handling");
            checkScore(performance.getCatching(), "Catching");
            checkScore(performance.getDiving(), "Diving");
            checkScore(
                    performance.getOneVsOneGoalkeeping(),
                    "1v1 Goalkeeping"
            );
            checkScore(
                    performance.getGoalkeeperPositioning(),
                    "Goalkeeper Positioning"
            );
            checkScore(performance.getDistribution(), "Distribution");
            checkScore(performance.getKicking(), "Kicking");
            checkScore(performance.getThrowing(), "Throwing");
            checkScore(performance.getCommandOfArea(), "Command of Area");
            checkScore(
                    performance.getCrossManagement(),
                    "Cross Management"
            );
            checkScore(
                    performance.getGoalkeeperDecisionMaking(),
                    "Goalkeeper Decision Making"
            );

        } else {
            throw new RuntimeException(
                    "Invalid player position. Please select Defender, Midfielder, Forward, or Goalkeeper."
            );
        }
    }

    // ============================================================
    // SCORE VALIDATION
    // ============================================================

    private void checkScore(int score, String skillName) {
        if (score < 1 || score > 10) {
            throw new RuntimeException(
                    skillName + " score must be between 1 and 10."
            );
        }
    }

    // ============================================================
    // CALCULATE OVERALL SCORE
    // Common skills: 75%; position-specific skills: 25%.
    // ============================================================

    private void calculateOverallScore(Performance performance) {

        double technical = (
                performance.getPassing()
                        + performance.getBallControl()
                        + performance.getFirstTouch()
                        + performance.getDribbling()
                        + performance.getShooting()
                        + performance.getFinishing()
                        + performance.getCrossing()
                        + performance.getHeading()
                        + performance.getTackling()
                        + performance.getMarking()
                        + performance.getWeakFoot()
        ) / 11.0;

        double physical = (
                performance.getSpeed()
                        + performance.getAcceleration()
                        + performance.getAgility()
                        + performance.getStamina()
                        + performance.getStrength()
                        + performance.getBalance()
                        + performance.getCoordination()
                        + performance.getReaction()
        ) / 8.0;

        double tactical = (
                performance.getPositioning()
                        + performance.getDecisionMaking()
                        + performance.getVision()
                        + performance.getOffTheBallMovement()
                        + performance.getTeamwork()
                        + performance.getDefensiveAwareness()
                        + performance.getAttackingAwareness()
                        + performance.getGameUnderstanding()
                        + performance.getDiscipline()
        ) / 9.0;

        double mental = (
                performance.getConcentration()
                        + performance.getConfidence()
                        + performance.getCommunication()
                        + performance.getLeadership()
                        + performance.getWorkRate()
                        + performance.getDetermination()
                        + performance.getCoachability()
                        + performance.getComposure()
        ) / 8.0;

        double commonScore = (technical + physical + tactical + mental) / 4.0;

        String position = performance.getPosition();

        if (position == null || position.trim().isEmpty()) {
            throw new RuntimeException(
                    "Player position is required to calculate overall score."
            );
        }

        position = position.trim();

        double positionScore;

        if (position.equalsIgnoreCase("Defender")) {
            positionScore = (
                    performance.getDefensivePositioning()
                            + performance.getInterceptions()
                            + performance.getOneVsOneDefending()
                            + performance.getAerialAbility()
                            + performance.getClearance()
            ) / 5.0;

        } else if (position.equalsIgnoreCase("Midfielder")) {
            positionScore = (
                    performance.getShortPassing()
                            + performance.getLongPassing()
                            + performance.getCreativity()
                            + performance.getThroughBalls()
                            + performance.getBallRetention()
                            + performance.getPressing()
                            + performance.getChanceCreation()
                            + performance.getMidfieldPositioning()
            ) / 8.0;

        } else if (position.equalsIgnoreCase("Forward")) {
            positionScore = (
                    performance.getAttackingPositioning()
                            + performance.getOneVsOneAttacking()
                            + performance.getShotAccuracy()
            ) / 3.0;

        } else if (position.equalsIgnoreCase("Goalkeeper")) {
            positionScore = (
                    performance.getShotStopping()
                            + performance.getReflexes()
                            + performance.getHandling()
                            + performance.getCatching()
                            + performance.getDiving()
                            + performance.getOneVsOneGoalkeeping()
                            + performance.getGoalkeeperPositioning()
                            + performance.getDistribution()
                            + performance.getKicking()
                            + performance.getThrowing()
                            + performance.getCommandOfArea()
                            + performance.getCrossManagement()
                            + performance.getGoalkeeperDecisionMaking()
            ) / 13.0;

        } else {
            throw new RuntimeException(
                    "Invalid player position. Please select Defender, Midfielder, Forward, or Goalkeeper."
            );
        }

        double overall = (commonScore * 0.75) + (positionScore * 0.25);

        performance.setOverallScore(
                Math.round(overall * 10.0) / 10.0
        );
    }
}
