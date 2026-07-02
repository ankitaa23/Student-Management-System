package com.studentmgmt;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String URL = "jdbc:sqlite:student_management.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void initializeDatabase() {
        String createTableSql = """
                CREATE TABLE IF NOT EXISTS students (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    age INTEGER NOT NULL,
                    course TEXT NOT NULL,
                    email TEXT NOT NULL UNIQUE
                );
                """;

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSql);
            seedSampleData(conn);
        } catch (SQLException e) {
            System.err.println("Database initialization failed: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private static void seedSampleData(Connection conn) throws SQLException {
        String countSql = "SELECT COUNT(*) FROM students";
        try (Statement countStmt = conn.createStatement(); ResultSet rs = countStmt.executeQuery(countSql)) {
            if (rs.next() && rs.getInt(1) > 0) {
                return;
            }
        }

        String insertSql = "INSERT INTO students (name, age, course, email) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
            Object[][] sampleStudents = {
                    {"Ava Johnson", 20, "Computer Science", "ava.johnson@example.com"},
                    {"Noah Smith", 21, "Business Administration", "noah.smith@example.com"},
                    {"Mia Chen", 19, "Electrical Engineering", "mia.chen@example.com"},
                    {"Liam Patel", 22, "Data Science", "liam.patel@example.com"},
                    {"Sophia Brown", 20, "Mathematics", "sophia.brown@example.com"}
            };

            for (Object[] student : sampleStudents) {
                ps.setString(1, (String) student[0]);
                ps.setInt(2, (Integer) student[1]);
                ps.setString(3, (String) student[2]);
                ps.setString(4, (String) student[3]);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }
}
