DROP DATABASE IF EXISTS hub_db;
CREATE DATABASE hub_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE hub_db;

CREATE TABLE users (
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50)  NOT NULL UNIQUE,
    email    VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
) ENGINE=InnoDB;

CREATE TABLE notes (
    id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    title   VARCHAR(200) NOT NULL,
    content TEXT         NOT NULL,
    user_id BIGINT       NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE wishes (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(200) NOT NULL,
    description TEXT,
    achieved    BOOLEAN NOT NULL DEFAULT FALSE,
    user_id     BIGINT  NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

INSERT INTO users (id, username, email, password) VALUES
    (1, 'One', '1@gmail.com', 'user1'),
    (2, 'Two', '2@gmail.com', 'user2');

INSERT INTO notes (title, content, user_id) VALUES
    ('Доробити проект', 'До кінця місяця', 2),
    ('Здати лабораторну', 'Завершити Notes Wishboard Hub', 1);

INSERT INTO wishes (name, description, achieved, user_id) VALUES
    ('Зарядна станція купити', '2кВт', FALSE, 1),
    ('Докупити ОЗП', 'DDR5 - 6400', FALSE, 1);