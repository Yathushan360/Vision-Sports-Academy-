CREATE DATABASE IF NOT EXISTS sports_academy_db;
USE sports_academy_db;

CREATE TABLE fee_structure (
    fee_id INT,
    fee_type ENUM('MONTHLY_TRAINING', 'REGISTRATION') NOT NULL,
    amount DECIMAL(10,2) NOT NULL, 
    age_group VARCHAR(10), 
    effective_from DATE NOT NULL, 
    CONSTRAINT pk_fee PRIMARY KEY (fee_id)
);

CREATE TABLE payments (
    payment_id INT, 
    player_id INT NOT NULL, 
    fee_id INT NOT NULL, 
    amount DECIMAL(10,2) NOT NULL, 
    due_date DATE NOT NULL, 
    paid_date DATE, 
    status ENUM('PENDING', 'PAID', 'OVERDUE') DEFAULT 'PENDING' NOT NULL, 
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_payments PRIMARY KEY (payment_id), 
    CONSTRAINT fk_payments_fee FOREIGN KEY (fee_id) REFERENCES fee_structure(fee_id)
);

CREATE TABLE payment_reminders (
    reminder_id    INT,
    payment_id     INT NOT NULL,
    sent_at        DATETIME DEFAULT CURRENT_TIMESTAMP,
    channel        ENUM('EMAIL', 'SMS', 'APP') DEFAULT 'SMS',
    CONSTRAINT pk_reminders PRIMARY KEY (reminder_id),
    CONSTRAINT fk_reminders_payment FOREIGN KEY (payment_id) REFERENCES payments(payment_id) ON DELETE CASCADE
);