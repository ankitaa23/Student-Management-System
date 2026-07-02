package com.studentmgmt;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.io.File;
import java.sql.SQLException;
import java.util.List;

public class StudentManagementGUI extends JFrame {
    private final StudentDAO studentDAO = new StudentDAO();
    private final JTextField nameField = new JTextField();
    private final JTextField ageField = new JTextField();
    private final JTextField courseField = new JTextField();
    private final JTextField emailField = new JTextField();
    private final JTextField searchField = new JTextField();
    private final JLabel statusLabel = new JLabel("Ready");
    private final JTable studentTable = new JTable();
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"ID", "Name", "Age", "Course", "Email"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private Integer selectedStudentId;

    public static void main(String[] args) {
        DatabaseConnection.initializeDatabase();
        SwingUtilities.invokeLater(() -> {
            StudentManagementGUI frame = new StudentManagementGUI();
            frame.setVisible(true);
        });
    }

    public StudentManagementGUI() {
        initUI();
        loadStudents();
    }

    private void initUI() {
        setTitle("Student Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        createMenuBar();
        JPanel searchPanel = createSearchPanel();
        JPanel formPanel = createFormPanel();
        JPanel buttonPanel = createButtonPanel();
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.add(formPanel, BorderLayout.NORTH);
        leftPanel.add(buttonPanel, BorderLayout.SOUTH);

        studentTable.setModel(tableModel);
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentTable.setAutoCreateRowSorter(true);
        studentTable.getTableHeader().setReorderingAllowed(false);
        studentTable.setSelectionBackground(new Color(184, 207, 229));
        studentTable.setFillsViewportHeight(true);
        studentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                fillFormFromSelection();
            }
        });

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        studentTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        studentTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);

        JScrollPane tableScrollPane = new JScrollPane(studentTable);
        tableScrollPane.setBorder(BorderFactory.createTitledBorder("Students"));
        tableScrollPane.setPreferredSize(new Dimension(550, 400));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, tableScrollPane);
        splitPane.setResizeWeight(0.35);
        splitPane.setDividerLocation(360);
        splitPane.setOneTouchExpandable(true);

        add(searchPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
        add(createStatusPanel(), BorderLayout.SOUTH);
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem importMenuItem = new JMenuItem("Import CSV...");
        JMenuItem exitMenuItem = new JMenuItem("Exit");

        importMenuItem.addActionListener(e -> importStudentsFromCsv());
        exitMenuItem.addActionListener(e -> dispose());

        fileMenu.add(importMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(exitMenuItem);
        menuBar.add(fileMenu);

        setJMenuBar(menuBar);
    }

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        searchField.setColumns(30);
        JButton searchButton = new JButton("Search");
        JButton refreshButton = new JButton("Refresh");

        searchButton.addActionListener(e -> searchStudents());
        refreshButton.addActionListener(e -> {
            searchField.setText("");
            loadStudents();
        });

        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(refreshButton);

        return searchPanel;
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Student Details"));
        formPanel.add(new JLabel("Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Age:"));
        formPanel.add(ageField);
        formPanel.add(new JLabel("Course:"));
        formPanel.add(courseField);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        return formPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        JButton addButton = new JButton("Add Student");
        JButton updateButton = new JButton("Update Student");
        JButton deleteButton = new JButton("Delete Student");
        JButton importButton = new JButton("Import CSV");
        JButton clearButton = new JButton("Clear Form");

        addButton.addActionListener(e -> addStudent());
        updateButton.addActionListener(e -> updateStudent());
        deleteButton.addActionListener(e -> deleteStudent());
        importButton.addActionListener(e -> importStudentsFromCsv());
        clearButton.addActionListener(e -> clearForm());

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(importButton);
        buttonPanel.add(clearButton);

        return buttonPanel;
    }

    private JPanel createStatusPanel() {
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        statusLabel.setForeground(Color.DARK_GRAY);
        statusPanel.add(statusLabel, BorderLayout.WEST);
        return statusPanel;
    }

    private void loadStudents() {
        try {
            showStatus("Loading students...");
            List<Student> students = studentDAO.getAllStudents();
            updateTable(students);
            showStatus("Loaded " + students.size() + " students.");
        } catch (SQLException e) {
            showError("Failed to load students: " + e.getMessage());
        }
    }

    private void searchStudents() {
        String keyword = searchField.getText().trim();
        try {
            List<Student> students = keyword.isEmpty() ? studentDAO.getAllStudents() : studentDAO.searchStudents(keyword);
            updateTable(students);
            showStatus(keyword.isEmpty() ? "Showing all students." : "Search results for: " + keyword);
        } catch (SQLException e) {
            showError("Search failed: " + e.getMessage());
        }
    }

    private void addStudent() {
        try {
            Student student = buildStudentFromForm();
            studentDAO.addStudent(student);
            clearForm();
            loadStudents();
            showInfo("Student added successfully.");
        } catch (Exception e) {
            showError("Failed to add student: " + e.getMessage());
        }
    }

    private void updateStudent() {
        if (selectedStudentId == null) {
            showError("Please select a student first.");
            return;
        }
        try {
            Student student = buildStudentFromForm();
            student.setId(selectedStudentId);
            studentDAO.updateStudent(student);
            clearForm();
            loadStudents();
            showInfo("Student updated successfully.");
        } catch (Exception e) {
            showError("Failed to update student: " + e.getMessage());
        }
    }

    private void deleteStudent() {
        if (selectedStudentId == null) {
            showError("Please select a student first.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this student?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            showStatus("Delete canceled.");
            return;
        }
        try {
            studentDAO.deleteStudent(selectedStudentId);
            clearForm();
            loadStudents();
            showInfo("Student deleted successfully.");
        } catch (Exception e) {
            showError("Failed to delete student: " + e.getMessage());
        }
    }

    private void importStudentsFromCsv() {
        JFileChooser chooser = new JFileChooser(new File("."));
        chooser.setDialogTitle("Select CSV file");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int result = chooser.showOpenDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
            showStatus("CSV import canceled.");
            return;
        }
        File selectedFile = chooser.getSelectedFile();
        try {
            int imported = studentDAO.importFromCsv(selectedFile.getAbsolutePath());
            loadStudents();
            showInfo("Imported " + imported + " students from CSV.");
        } catch (Exception e) {
            showError("Failed to import CSV: " + e.getMessage());
        }
    }

    private void clearForm() {
        nameField.setText("");
        ageField.setText("");
        courseField.setText("");
        emailField.setText("");
        selectedStudentId = null;
        studentTable.clearSelection();
        showStatus("Form cleared.");
    }

    private Student buildStudentFromForm() {
        String name = nameField.getText().trim();
        String ageText = ageField.getText().trim();
        String course = courseField.getText().trim();
        String email = emailField.getText().trim();

        if (name.isEmpty() || ageText.isEmpty() || course.isEmpty() || email.isEmpty()) {
            throw new IllegalArgumentException("All fields are required.");
        }

        int age;
        try {
            age = Integer.parseInt(ageText);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Age must be a valid number.");
        }
        if (age <= 0) {
            throw new IllegalArgumentException("Age must be greater than zero.");
        }

        Student student = new Student();
        student.setName(name);
        student.setAge(age);
        student.setCourse(course);
        student.setEmail(email);
        return student;
    }

    private void fillFormFromSelection() {
        int row = studentTable.getSelectedRow();
        if (row < 0) {
            return;
        }
        int modelRow = studentTable.convertRowIndexToModel(row);
        selectedStudentId = (Integer) tableModel.getValueAt(modelRow, 0);
        nameField.setText(String.valueOf(tableModel.getValueAt(modelRow, 1)));
        ageField.setText(String.valueOf(tableModel.getValueAt(modelRow, 2)));
        courseField.setText(String.valueOf(tableModel.getValueAt(modelRow, 3)));
        emailField.setText(String.valueOf(tableModel.getValueAt(modelRow, 4)));
        showStatus("Selected student ID: " + selectedStudentId);
    }

    private void updateTable(List<Student> students) {
        tableModel.setRowCount(0);
        for (Student student : students) {
            tableModel.addRow(new Object[]{student.getId(), student.getName(), student.getAge(), student.getCourse(), student.getEmail()});
        }
    }

    private void showError(String message) {
        statusLabel.setText("Error: " + message);
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showInfo(String message) {
        statusLabel.setText(message);
        JOptionPane.showMessageDialog(this, message, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showStatus(String message) {
        statusLabel.setText(message);
    }
}
