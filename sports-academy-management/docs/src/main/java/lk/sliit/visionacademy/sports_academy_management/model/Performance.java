package lk.sliit.visionacademy.sports_academy_management.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class Performance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long playerId;

    private Long sessionId;

    private String position;

    // Technical Skills
    private int passing;
    private int ballControl;
    private int firstTouch;
    private int dribbling;
    private int shooting;
    private int finishing;
    private int crossing;
    private int heading;
    private int tackling;
    private int marking;
    private int weakFoot;

    // Physical Skills
    private int speed;
    private int acceleration;
    private int agility;
    private int stamina;
    private int strength;
    private int balance;
    private int coordination;
    private int reaction;

    // Tactical Skills
    private int positioning;
    private int decisionMaking;
    private int vision;
    private int offTheBallMovement;
    private int teamwork;
    private int defensiveAwareness;
    private int attackingAwareness;
    private int gameUnderstanding;
    private int discipline;

    // Mental / Personal Skills
    private int concentration;
    private int confidence;
    private int communication;
    private int leadership;
    private int workRate;
    private int determination;
    private int coachability;
    private int composure;

    // Defender Skills
    private int defensivePositioning;
    private int interceptions;
    private int oneVsOneDefending;
    private int aerialAbility;
    private int clearance;

    // Midfielder Skills
    private int shortPassing;
    private int longPassing;
    private int creativity;
    private int throughBalls;
    private int ballRetention;
    private int pressing;
    private int chanceCreation;
    private int midfieldPositioning;

    // Forward Skills
    private int attackingPositioning;
    private int oneVsOneAttacking;
    private int shotAccuracy;

    // Goalkeeper Skills
    private int shotStopping;
    private int reflexes;
    private int handling;
    private int catching;
    private int diving;
    private int oneVsOneGoalkeeping;
    private int goalkeeperPositioning;
    private int distribution;
    private int kicking;
    private int throwing;
    private int commandOfArea;
    private int crossManagement;
    private int goalkeeperDecisionMaking;

    // Automatically calculated
    private double overallScore;

    private String coachComments;

    private String developmentGoals;

    private LocalDateTime assessedAt;

    public Performance() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public int getPassing() {
        return passing;
    }

    public void setPassing(int passing) {
        this.passing = passing;
    }

    public int getBallControl() {
        return ballControl;
    }

    public void setBallControl(int ballControl) {
        this.ballControl = ballControl;
    }

    public int getFirstTouch() {
        return firstTouch;
    }

    public void setFirstTouch(int firstTouch) {
        this.firstTouch = firstTouch;
    }

    public int getDribbling() {
        return dribbling;
    }

    public void setDribbling(int dribbling) {
        this.dribbling = dribbling;
    }

    public int getShooting() {
        return shooting;
    }

    public void setShooting(int shooting) {
        this.shooting = shooting;
    }

    public int getFinishing() {
        return finishing;
    }

    public void setFinishing(int finishing) {
        this.finishing = finishing;
    }

    public int getCrossing() {
        return crossing;
    }

    public void setCrossing(int crossing) {
        this.crossing = crossing;
    }

    public int getHeading() {
        return heading;
    }

    public void setHeading(int heading) {
        this.heading = heading;
    }

    public int getTackling() {
        return tackling;
    }

    public void setTackling(int tackling) {
        this.tackling = tackling;
    }

    public int getMarking() {
        return marking;
    }

    public void setMarking(int marking) {
        this.marking = marking;
    }

    public int getWeakFoot() {
        return weakFoot;
    }

    public void setWeakFoot(int weakFoot) {
        this.weakFoot = weakFoot;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(int acceleration) {
        this.acceleration = acceleration;
    }

    public int getAgility() {
        return agility;
    }

    public void setAgility(int agility) {
        this.agility = agility;
    }

    public int getStamina() {
        return stamina;
    }

    public void setStamina(int stamina) {
        this.stamina = stamina;
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getCoordination() {
        return coordination;
    }

    public void setCoordination(int coordination) {
        this.coordination = coordination;
    }

    public int getReaction() {
        return reaction;
    }

    public void setReaction(int reaction) {
        this.reaction = reaction;
    }

    public int getPositioning() {
        return positioning;
    }

    public void setPositioning(int positioning) {
        this.positioning = positioning;
    }

    public int getDecisionMaking() {
        return decisionMaking;
    }

    public void setDecisionMaking(int decisionMaking) {
        this.decisionMaking = decisionMaking;
    }

    public int getVision() {
        return vision;
    }

    public void setVision(int vision) {
        this.vision = vision;
    }

    public int getOffTheBallMovement() {
        return offTheBallMovement;
    }

    public void setOffTheBallMovement(int offTheBallMovement) {
        this.offTheBallMovement = offTheBallMovement;
    }

    public int getTeamwork() {
        return teamwork;
    }

    public void setTeamwork(int teamwork) {
        this.teamwork = teamwork;
    }

    public int getDefensiveAwareness() {
        return defensiveAwareness;
    }

    public void setDefensiveAwareness(int defensiveAwareness) {
        this.defensiveAwareness = defensiveAwareness;
    }

    public int getAttackingAwareness() {
        return attackingAwareness;
    }

    public void setAttackingAwareness(int attackingAwareness) {
        this.attackingAwareness = attackingAwareness;
    }

    public int getGameUnderstanding() {
        return gameUnderstanding;
    }

    public void setGameUnderstanding(int gameUnderstanding) {
        this.gameUnderstanding = gameUnderstanding;
    }

    public int getDiscipline() {
        return discipline;
    }

    public void setDiscipline(int discipline) {
        this.discipline = discipline;
    }

    public int getConcentration() {
        return concentration;
    }

    public void setConcentration(int concentration) {
        this.concentration = concentration;
    }

    public int getConfidence() {
        return confidence;
    }

    public void setConfidence(int confidence) {
        this.confidence = confidence;
    }

    public int getCommunication() {
        return communication;
    }

    public void setCommunication(int communication) {
        this.communication = communication;
    }

    public int getLeadership() {
        return leadership;
    }

    public void setLeadership(int leadership) {
        this.leadership = leadership;
    }

    public int getWorkRate() {
        return workRate;
    }

    public void setWorkRate(int workRate) {
        this.workRate = workRate;
    }

    public int getDetermination() {
        return determination;
    }

    public void setDetermination(int determination) {
        this.determination = determination;
    }

    public int getCoachability() {
        return coachability;
    }

    public void setCoachability(int coachability) {
        this.coachability = coachability;
    }

    public int getComposure() {
        return composure;
    }

    public void setComposure(int composure) {
        this.composure = composure;
    }

    public int getDefensivePositioning() {
        return defensivePositioning;
    }

    public void setDefensivePositioning(int defensivePositioning) {
        this.defensivePositioning = defensivePositioning;
    }

    public int getInterceptions() {
        return interceptions;
    }

    public void setInterceptions(int interceptions) {
        this.interceptions = interceptions;
    }

    public int getOneVsOneDefending() {
        return oneVsOneDefending;
    }

    public void setOneVsOneDefending(int oneVsOneDefending) {
        this.oneVsOneDefending = oneVsOneDefending;
    }

    public int getAerialAbility() {
        return aerialAbility;
    }

    public void setAerialAbility(int aerialAbility) {
        this.aerialAbility = aerialAbility;
    }

    public int getClearance() {
        return clearance;
    }

    public void setClearance(int clearance) {
        this.clearance = clearance;
    }

    public int getShortPassing() {
        return shortPassing;
    }

    public void setShortPassing(int shortPassing) {
        this.shortPassing = shortPassing;
    }

    public int getLongPassing() {
        return longPassing;
    }

    public void setLongPassing(int longPassing) {
        this.longPassing = longPassing;
    }

    public int getCreativity() {
        return creativity;
    }

    public void setCreativity(int creativity) {
        this.creativity = creativity;
    }

    public int getThroughBalls() {
        return throughBalls;
    }

    public void setThroughBalls(int throughBalls) {
        this.throughBalls = throughBalls;
    }

    public int getBallRetention() {
        return ballRetention;
    }

    public void setBallRetention(int ballRetention) {
        this.ballRetention = ballRetention;
    }

    public int getPressing() {
        return pressing;
    }

    public void setPressing(int pressing) {
        this.pressing = pressing;
    }

    public int getChanceCreation() {
        return chanceCreation;
    }

    public void setChanceCreation(int chanceCreation) {
        this.chanceCreation = chanceCreation;
    }

    public int getMidfieldPositioning() {
        return midfieldPositioning;
    }

    public void setMidfieldPositioning(int midfieldPositioning) {
        this.midfieldPositioning = midfieldPositioning;
    }

    public int getAttackingPositioning() {
        return attackingPositioning;
    }

    public void setAttackingPositioning(int attackingPositioning) {
        this.attackingPositioning = attackingPositioning;
    }

    public int getOneVsOneAttacking() {
        return oneVsOneAttacking;
    }

    public void setOneVsOneAttacking(int oneVsOneAttacking) {
        this.oneVsOneAttacking = oneVsOneAttacking;
    }

    public int getShotAccuracy() {
        return shotAccuracy;
    }

    public void setShotAccuracy(int shotAccuracy) {
        this.shotAccuracy = shotAccuracy;
    }

    public int getShotStopping() {
        return shotStopping;
    }

    public void setShotStopping(int shotStopping) {
        this.shotStopping = shotStopping;
    }

    public int getReflexes() {
        return reflexes;
    }

    public void setReflexes(int reflexes) {
        this.reflexes = reflexes;
    }

    public int getHandling() {
        return handling;
    }

    public void setHandling(int handling) {
        this.handling = handling;
    }

    public int getCatching() {
        return catching;
    }

    public void setCatching(int catching) {
        this.catching = catching;
    }

    public int getDiving() {
        return diving;
    }

    public void setDiving(int diving) {
        this.diving = diving;
    }

    public int getOneVsOneGoalkeeping() {
        return oneVsOneGoalkeeping;
    }

    public void setOneVsOneGoalkeeping(int oneVsOneGoalkeeping) {
        this.oneVsOneGoalkeeping = oneVsOneGoalkeeping;
    }

    public int getGoalkeeperPositioning() {
        return goalkeeperPositioning;
    }

    public void setGoalkeeperPositioning(int goalkeeperPositioning) {
        this.goalkeeperPositioning = goalkeeperPositioning;
    }

    public int getDistribution() {
        return distribution;
    }

    public void setDistribution(int distribution) {
        this.distribution = distribution;
    }

    public int getKicking() {
        return kicking;
    }

    public void setKicking(int kicking) {
        this.kicking = kicking;
    }

    public int getThrowing() {
        return throwing;
    }

    public void setThrowing(int throwing) {
        this.throwing = throwing;
    }

    public int getCommandOfArea() {
        return commandOfArea;
    }

    public void setCommandOfArea(int commandOfArea) {
        this.commandOfArea = commandOfArea;
    }

    public int getCrossManagement() {
        return crossManagement;
    }

    public void setCrossManagement(int crossManagement) {
        this.crossManagement = crossManagement;
    }

    public int getGoalkeeperDecisionMaking() {
        return goalkeeperDecisionMaking;
    }

    public void setGoalkeeperDecisionMaking(int goalkeeperDecisionMaking) {
        this.goalkeeperDecisionMaking = goalkeeperDecisionMaking;
    }

    public double getOverallScore() {
        return overallScore;
    }

    public void setOverallScore(double overallScore) {
        this.overallScore = overallScore;
    }

    public String getCoachComments() {
        return coachComments;
    }

    public void setCoachComments(String coachComments) {
        this.coachComments = coachComments;
    }

    public String getDevelopmentGoals() {
        return developmentGoals;
    }

    public void setDevelopmentGoals(String developmentGoals) {
        this.developmentGoals = developmentGoals;
    }

    public LocalDateTime getAssessedAt() {
        return assessedAt;
    }

    public void setAssessedAt(LocalDateTime assessedAt) {
        this.assessedAt = assessedAt;
    }
}
