CREATE DATABASE IF NOT EXISTS sports_academy_db;
USE sports_academy_db;

CREATE TABLE fee_structure (
    fee_id INT,
    fee_type ENUM('MONTHLY_TRAINING', 'REGISTRATION') NOT NULL, amount DECIMAL(10,2) NOT NULL, age_group VARCHAR(10), effective_from DATE NOT NULL, CONSTRAINT pk_fee PRIMARY KEY (fee_id)
);