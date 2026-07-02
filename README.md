# Student Management System

A simple Java console application that uses SQLite to manage students.

## Features
- Add a student
- View all students
- Search students by name, course, or email
- Update student details
- Delete a student

## Run on Windows
1. Make sure Java 21+ is installed.
2. Place the SQLite JDBC jar in the project root.
3. Double-click run.bat or run it from the command line.

## Manual build
```bash
javac -cp sqlite-jdbc.jar -d out $(find src/main/java -name "*.java")
java -cp out:sqlite-jdbc.jar com.studentmgmt.StudentManagementSystem
```
