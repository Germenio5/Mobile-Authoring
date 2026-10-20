CREATE DATABASE IF NOT EXISTS student_records_db;
USE student_records_db;

CREATE TABLE IF NOT EXISTS students (
    student_id INT NOT NULL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    course VARCHAR(100) NOT NULL,
    year_level INT NOT NULL
);