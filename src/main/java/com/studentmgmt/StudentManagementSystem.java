package com.studentmgmt;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class StudentManagementSystem {
    private final Scanner scanner = new Scanner(System.in);
    private final StudentDAO studentDAO = new StudentDAO();

    public static void main(String[] args) {
        DatabaseConnection.initializeDatabase();
        StudentManagementSystem app = new StudentManagementSystem();
        app.run();
    }

    private void run() {
        while (true) {
            printMenu();
            int choice = readInt("Enter your choice");
            switch (choice) {
                case 1 -> addStudent();
                case 2 -> viewAllStudents();
                case 3 -> searchStudents();
                case 4 -> updateStudent();
                case 5 -> deleteStudent();
                case 6 -> importStudentsFromCsv();
                case 7 -> {
                    System.out.println("Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void printMenu() {
        System.out.println("\n===== Student Management System =====");
        System.out.println("1. Add Student");
        System.out.println("2. View All Students");
        System.out.println("3. Search Students");
        System.out.println("4. Update Student");
        System.out.println("5. Delete Student");
        System.out.println("6. Import Students from CSV");
        System.out.println("7. Exit");
    }

    private void addStudent() {
        try {
            Student student = new Student();
            student.setName(readRequired("Enter student name"));
            student.setAge(readInt("Enter student age"));
            student.setCourse(readRequired("Enter course"));
            student.setEmail(readRequired("Enter email"));
            studentDAO.addStudent(student);
            System.out.println("Student added successfully.");
        } catch (Exception e) {
            System.out.println("Failed to add student: " + e.getMessage());
        }
    }

    private void viewAllStudents() {
        try {
            List<Student> students = studentDAO.getAllStudents();
            if (students.isEmpty()) {
                System.out.println("No students found.");
                return;
            }
            students.forEach(System.out::println);
        } catch (Exception e) {
            System.out.println("Failed to load students: " + e.getMessage());
        }
    }

    private void searchStudents() {
        String keyword = readRequired("Enter name/course/email to search");
        try {
            List<Student> students = studentDAO.searchStudents(keyword);
            if (students.isEmpty()) {
                System.out.println("No matching students found.");
            } else {
                students.forEach(System.out::println);
            }
        } catch (Exception e) {
            System.out.println("Search failed: " + e.getMessage());
        }
    }

    private void updateStudent() {
        try {
            int id = readInt("Enter student ID to update");
            Optional<Student> existing = studentDAO.getStudentById(id);
            if (existing.isEmpty()) {
                System.out.println("Student not found.");
                return;
            }

            Student student = existing.get();
            System.out.println("Current details: " + student);
            student.setName(readOptional("New name (leave blank to keep current)", student.getName()));
            student.setAge(readOptionalInt("New age (leave blank to keep current)", student.getAge()));
            student.setCourse(readOptional("New course (leave blank to keep current)", student.getCourse()));
            student.setEmail(readOptional("New email (leave blank to keep current)", student.getEmail()));
            studentDAO.updateStudent(student);
            System.out.println("Student updated successfully.");
        } catch (Exception e) {
            System.out.println("Update failed: " + e.getMessage());
        }
    }

    private void deleteStudent() {
        try {
            int id = readInt("Enter student ID to delete");
            studentDAO.deleteStudent(id);
            System.out.println("Student deleted successfully.");
        } catch (Exception e) {
            System.out.println("Delete failed: " + e.getMessage());
        }
    }

    private void importStudentsFromCsv() {
        String filePath = readRequired("Enter CSV file path");
        try {
            int imported = studentDAO.importFromCsv(filePath);
            System.out.println("Imported " + imported + " students from CSV.");
        } catch (Exception e) {
            System.out.println("CSV import failed: " + e.getMessage());
        }
    }

    private String readRequired(String prompt) {
        while (true) {
            System.out.print(prompt + ": ");
            String value = scanner.nextLine().trim();
            if (!value.isBlank()) {
                return value;
            }
            System.out.println("Value cannot be empty.");
        }
    }

    private String readOptional(String prompt, String currentValue) {
        System.out.print(prompt + " [" + currentValue + "]: ");
        String value = scanner.nextLine().trim();
        return value.isBlank() ? currentValue : value;
    }

    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt + ": ");
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private int readOptionalInt(String prompt, int currentValue) {
        System.out.print(prompt + " [" + currentValue + "]: ");
        String input = scanner.nextLine().trim();
        if (input.isBlank()) {
            return currentValue;
        }
        return Integer.parseInt(input);
    }
}
