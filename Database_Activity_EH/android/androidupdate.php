<?php
header("Content-Type: text/plain; charset=UTF-8");

$servername = "localhost";
$username = "root";
$password = "";
$dbname = "student_records_db";

$conn = new mysqli(
    $servername,
    $username,
    $password,
    $dbname
);

if ($conn->connect_error) {
    http_response_code(500);
    exit("Database connection failed: " . $conn->connect_error);
}

$conn->set_charset("utf8mb4");

// Only accept POST requests
if ($_SERVER["REQUEST_METHOD"] !== "POST") {
    http_response_code(405);
    exit("Please use POST to update a student.");
}

// Get values sent by Android
$student_id = filter_var(
    $_POST["student_id"] ?? "",
    FILTER_VALIDATE_INT
);

$name = trim($_POST["name"] ?? "");
$course = trim($_POST["course"] ?? "");

$year_level = filter_var(
    $_POST["year_level"] ?? "",
    FILTER_VALIDATE_INT
);

// Validate input
if (
    $student_id === false ||
    $student_id === null ||
    $student_id < 1 ||
    $name === "" ||
    $course === "" ||
    $year_level === false ||
    $year_level === null ||
    $year_level < 1
) {
    http_response_code(400);
    exit("Invalid data. Enter a valid ID, name, course, and year level.");
}

// Check if the student exists
$check = $conn->prepare(
    "SELECT student_id FROM students WHERE student_id = ?"
);

if (!$check) {
    http_response_code(500);
    exit("Check error: " . $conn->error);
}

$check->bind_param("i", $student_id);
$check->execute();
$check->store_result();

$exists = $check->num_rows > 0;

$check->close();

if (!$exists) {
    exit("No student found with ID " . $student_id . ".");
}

// Update the student record
$sql = "UPDATE students
        SET name = ?, course = ?, year_level = ?
        WHERE student_id = ?";

$stmt = $conn->prepare($sql);

if (!$stmt) {
    http_response_code(500);
    exit("Update error: " . $conn->error);
}

$stmt->bind_param(
    "ssii",
    $name,
    $course,
    $year_level,
    $student_id
);

if ($stmt->execute()) {
    if ($stmt->affected_rows > 0) {
        echo "Student record updated successfully.";
    } else {
        echo "Student exists, but the values are unchanged.";
    }
} else {
    http_response_code(500);
    echo "Failed to update: " . $stmt->error;
}

$stmt->close();
$conn->close();
?>