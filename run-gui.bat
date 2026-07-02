@echo off
setlocal
cd /d "%~dp0"
if not exist out mkdir out
javac -cp sqlite-jdbc.jar -d out src\main\java\com\studentmgmt\*.java
java -cp out;sqlite-jdbc.jar com.studentmgmt.StudentManagementGUI
