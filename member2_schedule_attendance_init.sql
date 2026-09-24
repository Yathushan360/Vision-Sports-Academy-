-- ============================================================
-- Sports Academy Management System
-- Module 2: Training Schedule & Attendance
-- Owner: Member 2 (Ahamed H A - IT25103183)
-- Day 1: Initial schema (depends on users/coaches/players from Module 1)
-- ============================================================

CREATE DATABASE IF NOT EXISTS sports_academy_db;
USE sports_academy_db;

-- Training session (FR1: date, start/end time, ground, coach, target group)
CREATE TABLE IF NOT EXISTS training_sessions (
    session_id    INT AUTO_INCREMENT PRIMARY KEY,
    coach_id      INT NOT NULL,
    session_date  DATE NOT NULL,
    start_time    TIME NOT NULL,
    end_time      TIME NOT NULL,
    ground        VARCHAR(50) NOT NULL,
    target_group  VARCHAR(10) NOT NULL,  -- e.g. U-08, U-12, U-18
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    -- FOREIGN KEY (coach_id) REFERENCES coaches(coach_id)
    -- ^ enable once merged with Member 1's coaches table
);

-- Attendance record (FR3, FR4, FR5: statuses, editable, auto % calculation)
CREATE TABLE IF NOT EXISTS attendance (
    attendance_id INT AUTO_INCREMENT PRIMARY KEY,
    session_id    INT NOT NULL,
    player_id     INT NOT NULL,
    status        ENUM('PRESENT', 'ABSENT', 'LATE', 'EXCUSED') NOT NULL,
    marked_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (session_id) REFERENCES training_sessions(session_id) ON DELETE CASCADE
    -- FOREIGN KEY (player_id) REFERENCES players(player_id)
    -- ^ enable once merged with Member 1's players table
);

-- Day-1 sample data for local testing
INSERT INTO training_sessions (coach_id, session_date, start_time, end_time, ground, target_group) VALUES
(1, '2026-09-22', '16:00:00', '17:30:00', 'Main Ground', 'U-12');
