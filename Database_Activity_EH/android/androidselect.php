<?php
header("Content-Type: text/plain; charset=UTF-8");

$servername = "localhost";
$username = "root";
$password = "";
$dbname = "student_records_db";

$conn = new mysqli($servername, $username, $password, $dbname);

if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

$conn->set_charset("utf8mb4");

$sql = "SELECT student_id, name, course, year_level FROM students ORDER BY student_id ASC";
$result = $conn->query($sql);

if ($result === false) {
    die("Error retrieving records: " . $conn->error);
}

if ($result->num_rows > 0) {
    while ($row = $result->fetch_assoc()) {
        echo "Student ID: " . $row["student_id"] . "\n";
        echo "Name: " . $row["name"] . "\n";
        echo "Course: " . $row["course"] . "\n";
        echo "Year Level: " . $row["year_level"] . "\n";
        echo "------------------------------\n";
    }
} else {
    echo "No student records found.";
}

$conn->close();
?>
