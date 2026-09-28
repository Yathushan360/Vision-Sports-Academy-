CREATE DATABASE IF NOT EXISTS vision_academy_db;
USE vision_academy_db;

-- Member 1: User & Profile Management
CREATE TABLE IF NOT EXISTS user_accounts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS parent_guardians (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    parent_code VARCHAR(20) NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(30) NOT NULL,
    email VARCHAR(120),
    address VARCHAR(255),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    user_account_id BIGINT UNIQUE,
    CONSTRAINT fk_parent_user FOREIGN KEY (user_account_id) REFERENCES user_accounts(id)
);

CREATE TABLE IF NOT EXISTS coaches (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    coach_code VARCHAR(20) NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(30),
    email VARCHAR(120),
    coaching_license VARCHAR(100),
    assigned_group VARCHAR(50),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    user_account_id BIGINT UNIQUE,
    CONSTRAINT fk_coach_user FOREIGN KEY (user_account_id) REFERENCES user_accounts(id)
);

CREATE TABLE IF NOT EXISTS players (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    player_code VARCHAR(20) NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    date_of_birth DATE,
    age_group VARCHAR(20) NOT NULL,
    medical_information VARCHAR(500),
    kit_size VARCHAR(20),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    parent_id BIGINT,
    user_account_id BIGINT UNIQUE,
    CONSTRAINT fk_player_parent FOREIGN KEY (parent_id) REFERENCES parent_guardians(id),
    CONSTRAINT fk_player_user FOREIGN KEY (user_account_id) REFERENCES user_accounts(id)
);

-- Demo administrator. The application also ensures this account exists.
-- Login: admin / admin123
