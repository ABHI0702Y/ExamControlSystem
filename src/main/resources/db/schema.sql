-- ============================================================
--  Exam Control System — Database Schema
--  MySQL 8.x
-- ============================================================

CREATE DATABASE IF NOT EXISTS exam_control_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE exam_control_db;

-- -------------------------------------------------------
-- users
-- -------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    id           INT          NOT NULL AUTO_INCREMENT,
    username     VARCHAR(50)  NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name    VARCHAR(100) NOT NULL,
    email        VARCHAR(100) NOT NULL,
    role         ENUM('ADMIN','TEACHER','STUDENT') NOT NULL,
    is_active    TINYINT(1)   NOT NULL DEFAULT 1,
    created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_username (username),
    UNIQUE KEY uq_email    (email)
) ENGINE=InnoDB;

-- -------------------------------------------------------
-- questions
-- -------------------------------------------------------
CREATE TABLE IF NOT EXISTS questions (
    id             INT          NOT NULL AUTO_INCREMENT,
    question_text  TEXT         NOT NULL,
    option_a       VARCHAR(500) NOT NULL,
    option_b       VARCHAR(500) NOT NULL,
    option_c       VARCHAR(500) NOT NULL,
    option_d       VARCHAR(500) NOT NULL,
    correct_option ENUM('A','B','C','D') NOT NULL,
    marks          INT          NOT NULL DEFAULT 1,
    difficulty     ENUM('EASY','MEDIUM','HARD') NOT NULL DEFAULT 'MEDIUM',
    subject        VARCHAR(100),
    created_by     INT,
    created_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY fk_q_user (created_by) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- -------------------------------------------------------
-- exams
-- -------------------------------------------------------
CREATE TABLE IF NOT EXISTS exams (
    id               INT          NOT NULL AUTO_INCREMENT,
    title            VARCHAR(200) NOT NULL,
    description      TEXT,
    duration_minutes INT          NOT NULL,
    total_marks      INT          NOT NULL DEFAULT 0,
    passing_marks    INT          NOT NULL DEFAULT 0,
    start_time       DATETIME,
    end_time         DATETIME,
    is_active        TINYINT(1)   NOT NULL DEFAULT 1,
    created_by       INT,
    created_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY fk_e_user (created_by) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- -------------------------------------------------------
-- exam_questions  (many-to-many)
-- -------------------------------------------------------
CREATE TABLE IF NOT EXISTS exam_questions (
    exam_id         INT NOT NULL,
    question_id     INT NOT NULL,
    question_order  INT NOT NULL DEFAULT 0,
    PRIMARY KEY (exam_id, question_id),
    FOREIGN KEY fk_eq_exam (exam_id)     REFERENCES exams(id)     ON DELETE CASCADE,
    FOREIGN KEY fk_eq_q    (question_id) REFERENCES questions(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- -------------------------------------------------------
-- results
-- -------------------------------------------------------
CREATE TABLE IF NOT EXISTS results (
    id           INT            NOT NULL AUTO_INCREMENT,
    student_id   INT            NOT NULL,
    exam_id      INT            NOT NULL,
    score        INT            NOT NULL DEFAULT 0,
    total_marks  INT            NOT NULL DEFAULT 0,
    percentage   DECIMAL(5,2)   NOT NULL DEFAULT 0.00,
    passed       TINYINT(1)     NOT NULL DEFAULT 0,
    submitted_at TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_attempt (student_id, exam_id),
    FOREIGN KEY fk_r_student (student_id) REFERENCES users(id)  ON DELETE CASCADE,
    FOREIGN KEY fk_r_exam    (exam_id)    REFERENCES exams(id)  ON DELETE CASCADE
) ENGINE=InnoDB;

-- -------------------------------------------------------
-- student_answers
-- -------------------------------------------------------
CREATE TABLE IF NOT EXISTS student_answers (
    id              INT  NOT NULL AUTO_INCREMENT,
    result_id       INT  NOT NULL,
    question_id     INT  NOT NULL,
    selected_option ENUM('A','B','C','D'),
    is_correct      TINYINT(1) NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    FOREIGN KEY fk_sa_result (result_id)   REFERENCES results(id)   ON DELETE CASCADE,
    FOREIGN KEY fk_sa_q      (question_id) REFERENCES questions(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- -------------------------------------------------------
-- Seed: default admin account  (password: Admin@123)
-- -------------------------------------------------------
INSERT IGNORE INTO users (username, password_hash, full_name, email, role)
VALUES (
    'admin',
    '$2a$12$hHCjBVzA0sMXRhG3LDphOuM0J3KCt2xH5SKQl8K3O5VJt8GWjP.4O',
    'System Administrator',
    'admin@examcontrol.com',
    'ADMIN'
);
