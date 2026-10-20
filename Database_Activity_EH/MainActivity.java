package com.example.database_activity;

import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.text.method.ScrollingMovementMethod;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    TextView txtRecords;
    EditText edtStudentId;
    EditText edtName;
    EditText edtCourse;
    EditText edtYearLevel;
    Button btnAdd;
    Button btnUpdate;
    Button btnDelete;
    Button btnView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        edtStudentId = findViewById(R.id.editstudentId);
        edtName = findViewById(R.id.editName);
        edtCourse = findViewById(R.id.editCourse);
        edtYearLevel = findViewById(R.id.editYearLevel);
        btnAdd = findViewById(R.id.btnAdd);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);
        btnView = findViewById(R.id.btnView);
        txtRecords = findViewById(R.id.txtRecords);
        txtRecords.setMovementMethod(new ScrollingMovementMethod());

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toFetch();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertData();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteData();
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateData();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void toFetch() {
        try {
            URL url = new URL("http://192.168.254.107/android/androidselect.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream())
            );

            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }

            reader.close();

            txtRecords.setText(sb.toString());
            conn.disconnect();

        } catch (Exception e) {
            txtRecords.setText("Error: " + e.getMessage());
        }
    }


    private void insertData() {
        String studentId = edtStudentId.getText().toString().trim();
        String name = edtName.getText().toString().trim();
        String course = edtCourse.getText().toString().trim();
        String yearLevel = edtYearLevel.getText().toString().trim();

        // Check if all fields are filled
        if (studentId.isEmpty() || name.isEmpty()
                || course.isEmpty() || yearLevel.isEmpty()) {
            txtRecords.setText("Please fill in all fields before inserting.");
            return;
        }

        // Validate numeric inputs
        try {
            if (Integer.parseInt(studentId) < 1) {
                txtRecords.setText("Student ID must be greater than zero.");
                return;
            }

            if (Integer.parseInt(yearLevel) < 1) {
                txtRecords.setText("Year Level must be greater than zero.");
                return;
            }
        } catch (NumberFormatException e) {
            txtRecords.setText("Student ID and Year Level must be numbers.");
            return;
        }

        HttpURLConnection conn = null;

        try {
            URL url = new URL(
                    "http://192.168.254.107/android/androidinsert.php"
            );

            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setDoOutput(true);

            String postData =
                    "student_id=" + URLEncoder.encode(studentId, "UTF-8")
                            + "&name=" + URLEncoder.encode(name, "UTF-8")
                            + "&course=" + URLEncoder.encode(course, "UTF-8")
                            + "&year_level=" + URLEncoder.encode(yearLevel, "UTF-8");

            conn.setRequestProperty(
                    "Content-Type",
                    "application/x-www-form-urlencoded; charset=UTF-8"
            );

            OutputStream os = conn.getOutputStream();

            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8")
            );

            writer.write(postData);
            writer.flush();
            writer.close();
            os.close();

            // Read normal or error response
            int responseCode = conn.getResponseCode();

            InputStream inputStream;

            if (responseCode >= 200 && responseCode < 400) {
                inputStream = conn.getInputStream();
            } else {
                inputStream = conn.getErrorStream();
            }

            StringBuilder sb = new StringBuilder();

            if (inputStream != null) {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(inputStream, "UTF-8")
                );

                String line;

                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }

                reader.close();
            }

            if (responseCode >= 400) {
                txtRecords.setText(
                        "Server Error (" + responseCode + "):\n" + sb.toString().trim());
            } else {
                txtRecords.setText(sb.toString().trim());
            }

        } catch (Exception e) {
            txtRecords.setText("Insert Error: " + e.getMessage());
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }


    private void deleteData() {
        String studentId = edtStudentId.getText().toString().trim();

        if (studentId.isEmpty()) {
            txtRecords.setText("Please enter a Student ID to delete.");
            return;
        }

        try {
            if (Integer.parseInt(studentId) < 1) {
                txtRecords.setText("Student ID must be greater than zero.");
                return;
            }
        } catch (NumberFormatException e) {
            txtRecords.setText("Student ID must be a valid number.");
            return;
        }

        HttpURLConnection conn = null;

        try {
            URL url = new URL(
                    "http://192.168.254.107/android/androiddelete.php"
            );

            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setDoOutput(true);

            conn.setRequestProperty(
                    "Content-Type",
                    "application/x-www-form-urlencoded; charset=UTF-8"
            );

            String postData =
                    "student_id=" + URLEncoder.encode(studentId, "UTF-8");

            OutputStream os = conn.getOutputStream();

            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8")
            );

            writer.write(postData);
            writer.flush();
            writer.close();
            os.close();

            int responseCode = conn.getResponseCode();

            InputStream inputStream;

            if (responseCode >= 200 && responseCode < 400) {
                inputStream = conn.getInputStream();
            } else {
                inputStream = conn.getErrorStream();
            }

            StringBuilder sb = new StringBuilder();

            if (inputStream != null) {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(inputStream, "UTF-8")
                );

                String line;
                while ((line = reader.readLine()) != null) {sb.append(line).append("\n");}
                reader.close();
            }

            if (responseCode >= 400) {
                txtRecords.setText("Server Error (" + responseCode + "):\n" + sb.toString().trim());
            } else {
                txtRecords.setText(sb.toString().trim());
            }

        } catch (Exception e) {
            txtRecords.setText("Delete Error: " + e.getMessage());
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private void updateData() {
        String studentId = edtStudentId.getText().toString().trim();
        String name = edtName.getText().toString().trim();
        String course = edtCourse.getText().toString().trim();
        String yearLevel = edtYearLevel.getText().toString().trim();

        if (studentId.isEmpty() || name.isEmpty()
                || course.isEmpty() || yearLevel.isEmpty()) {
            txtRecords.setText("Please fill in all fields before updating.");
            return;
        }

        try {
            if (Integer.parseInt(studentId) < 1) {
                txtRecords.setText("Student ID must be greater than zero.");
                return;
            }

            if (Integer.parseInt(yearLevel) < 1) {
                txtRecords.setText("Year Level must be greater than zero.");
                return;
            }
        } catch (NumberFormatException e) {
            txtRecords.setText("Student ID and Year Level must be numbers.");
            return;
        }

        HttpURLConnection conn = null;

        try {
            URL url = new URL(
                    "http://192.168.254.107/android/androidupdate.php"
            );

            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setDoOutput(true);

            conn.setRequestProperty(
                    "Content-Type",
                    "application/x-www-form-urlencoded; charset=UTF-8"
            );

            String postData =
                    "student_id=" + URLEncoder.encode(studentId, "UTF-8")
                            + "&name=" + URLEncoder.encode(name, "UTF-8")
                            + "&course=" + URLEncoder.encode(course, "UTF-8")
                            + "&year_level=" + URLEncoder.encode(yearLevel, "UTF-8");

            OutputStream os = conn.getOutputStream();

            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8")
            );

            writer.write(postData);
            writer.flush();
            writer.close();
            os.close();

            int responseCode = conn.getResponseCode();

            InputStream inputStream;

            if (responseCode >= 200 && responseCode < 400) {
                inputStream = conn.getInputStream();
            } else {
                inputStream = conn.getErrorStream();
            }

            StringBuilder sb = new StringBuilder();

            if (inputStream != null) {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(inputStream, "UTF-8")
                );

                String line;

                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }

                reader.close();
            }

            if (responseCode >= 400) {
                txtRecords.setText(
                        "Server Error (" + responseCode + "):\n"
                                + sb.toString().trim()
                );
            } else {
                txtRecords.setText(sb.toString().trim());
            }

        } catch (Exception e) {
            txtRecords.setText("Update Error: " + e.getMessage());
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}