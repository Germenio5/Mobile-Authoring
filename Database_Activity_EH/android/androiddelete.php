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
    exit("Please use POST to delete a student.");
}

// Get the student ID sent by Android
$student_id = filter_var(
    $_POST["student_id"] ?? "",
    FILTER_VALIDATE_INT
);

// Validate student ID
if (
    $student_id === false ||
    $student_id === null ||
    $student_id < 1
) {
    http_response_code(400);
    exit("Enter a valid student ID.");
}

// Delete the matching student
$sql = "DELETE FROM students WHERE student_id = ?";

$stmt = $conn->prepare($sql);

if (!$stmt) {
    http_response_code(500);
    exit("Delete error: " . $conn->error);
}

$stmt->bind_param("i", $student_id);

if ($stmt->execute()) {
    if ($stmt->affected_rows > 0) {
        echo "Student record deleted successfully.";
    } else {
        echo "No student found with ID " . $student_id . ".";
    }
} else {
    http_response_code(500);
    echo "Failed to delete: " . $stmt->error;
}

$stmt->close();
$conn->close();
?>