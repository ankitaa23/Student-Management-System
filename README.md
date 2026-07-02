# Student Management System

A simple Java application that uses SQLite to manage students.

## Features
- Add a student
- View all students
- Search students by name, course, or email
- Update student details
- Delete a student
- Import students from CSV
- Graphical user interface (GUI)

## GitHub repository
https://github.com/ankitaa23/Student-Management-System

## Requirements
- Java 21 or newer
- SQLite JDBC driver jar (`sqlite-jdbc.jar`) in the project root

## Run the GUI on Windows
1. Open PowerShell or Command Prompt in the project folder.
2. Run:
   ```powershell
   .\run-gui.bat
   ```
3. The GUI will open and allow you to add, update, delete, search, and import students.

## Run the console app on Windows
1. Open PowerShell or Command Prompt in the project folder.
2. Run:
   ```powershell
   .\run.bat
   ```
3. Follow the menu prompts in the console.

## Manual build and run
```powershell
javac -cp sqlite-jdbc.jar -d out src\main\java\com\studentmgmt\*.java
java -cp out;sqlite-jdbc.jar com.studentmgmt.StudentManagementSystem
```

## Notes
- The `run-gui.bat` script launches the Swing-based GUI.
- The CSV import is available from the GUI menu or from the console app prompt.
- The `out/` folder and local database file are ignored by git.
