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
    exit("Database connection failed.");
}

$conn->set_charset("utf8mb4");

// Accept POST requests only
if ($_SERVER["REQUEST_METHOD"] !== "POST") {
    http_response_code(405);
    exit("Invalid request method.");
}

// Get values from Android
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

// Validate the input
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
    exit("Please enter valid student information.");
}

// Check if the student ID already exists
$check = $conn->prepare(
    "SELECT student_id FROM students WHERE student_id = ?"
);

if (!$check) {
    http_response_code(500);
    exit("Failed to check student ID.");
}

$check->bind_param("i", $student_id);
$check->execute();
$check->store_result();

if ($check->num_rows > 0) {
    $check->close();
    $conn->close();

    exit("Error: Student ID already exists!");
}

$check->close();

// Insert the new student
$sql = "INSERT INTO students
        (student_id, name, course, year_level)
        VALUES (?, ?, ?, ?)";

$stmt = $conn->prepare($sql);

if (!$stmt) {
    http_response_code(500);
    $conn->close();
    exit("Failed to prepare insert statement.");
}

$stmt->bind_param(
    "issi",
    $student_id,
    $name,
    $course,
    $year_level
);

try {
    if ($stmt->execute()) {
        echo "Student added successfully!";
    } else {
        echo "Error: Failed to add student.";
    }
} catch (mysqli_sql_exception $e) {
    // Handle duplicate IDs even if another request inserts the ID
    // after the initial existence check.
    if ($e->getCode() == 1062) {
        echo "Error: Student ID already exists!";
    } else {
        http_response_code(500);
        echo "Error: Failed to add student.";
    }
}

$stmt->close();
$conn->close();
?>