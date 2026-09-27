# CodeAlpha - Student Grade Tracker

![Java](https://img.shields.io/badge/Java-SE%208%2B-orange?logo=java)
![GUI](https://img.shields.io/badge/GUI-Java%20Swing-blue)
![CodeAlpha](https://img.shields.io/badge/Internship-CodeAlpha-success)
![Status](https://img.shields.io/badge/Status-Completed-brightgreen)

A modern, feature-rich Java desktop application built for the **CodeAlpha Java Programming Internship (Task 1)**. This application enables educators and administrators to manage student grades, perform in-depth statistical analysis (average, median, high/low scores, passing rate), visualize grade distribution curves via a custom graphics engine, and export reports and CSV data.

---

## 📋 Features

- **Student Performance Records**:
  - Add, edit, remove, and manage student grades.
  - Automatically computes Letter Grades (`A`, `B`, `C`, `D`, `F`), 4.0 GPA equivalence, and Pass/Fail statuses.
  - Quick-search and real-time filtering across Student ID, Name, Subject, and Grade.

- **Statistical Analysis**:
  - **Class Average**: Real-time overall percentage calculation.
  - **Highest & Lowest Scorers**: Identifies top performers and students requiring attention.
  - **Median Score**: Accurate statistical median handling even and odd roster lengths.
  - **Passing Rate**: Percentage and ratio of passing students.

- **Modern Desktop GUI (Java Swing)**:
  - Clean card-based KPI metrics banner (Total Students, Class Avg, Highest, Lowest, Pass Rate).
  - High-DPI antialiased **Grade Distribution Bar Chart** rendered with custom 2D vector graphics.
  - Formatted text report generator with real-time preview and one-click `.txt` export.
  - Double-click row editing and modal dialog input validation.

- **Data Persistence & File I/O**:
  - **Export to CSV**: Save rosters for Excel/Google Sheets.
  - **Import from CSV**: Load existing rosters with automatic format validation.
  - **Load Sample Data**: One-click dataset loading for instant live demonstrations.

- **Dual Mode Support**:
  - Default: Modern graphical desktop interface.
  - Terminal/Headless: Fully functional interactive CLI mode via `--cli` flag.

---

## 🏗️ Architecture & OOP Design

The codebase strictly adheres to Object-Oriented Programming (OOP) principles, clean separation of concerns, and Java best practices:

```
CodeAlpha_StudentGradeTracker/
├── src/
│   └── com/codealpha/gradetracker/
│       ├── Main.java               # Application bootstrap & CLI/GUI router
│       ├── model/
│       │   ├── Student.java        # Student domain entity (grades, GPA, CSV)
│       │   └── GradeStats.java     # Immutable statistical summary record
│       ├── service/
│       │   └── GradeManager.java   # Business logic, calculations, and CSV I/O
│       └── ui/
│           ├── GradeTrackerGUI.java# Modern Swing application window & tables
│           └── ChartPanel.java     # Custom 2D graphics grade distribution chart
├── bin/                            # Compiled bytecode
├── CodeAlpha_StudentGradeTracker.jar # Pre-packaged executable JAR
├── run.bat                         # 1-Click Windows Batch launcher
├── run.ps1                         # PowerShell compilation & launcher script
└── README.md
```

---

## 🚀 How to Run

### Option 1: Double-Click the Executable JAR
If you have Java installed:
```bash
java -jar CodeAlpha_StudentGradeTracker.jar
```
Or simply double-click **`CodeAlpha_StudentGradeTracker.jar`** in Windows File Explorer.

---

### Option 2: Using the Windows Launcher Script
Double-click **`run.bat`** (or right-click `run.ps1` -> *Run with PowerShell*). It will automatically compile all source files and open the GUI.

---

### Option 3: Manual Compilation via Command Line
```bash
# Compile
javac -encoding UTF-8 -d bin src/com/codealpha/gradetracker/model/*.java src/com/codealpha/gradetracker/service/*.java src/com/codealpha/gradetracker/ui/*.java src/com/codealpha/gradetracker/*.java

# Run Modern GUI
java -cp bin com.codealpha.gradetracker.Main

# Run Terminal CLI mode (optional)
java -cp bin com.codealpha.gradetracker.Main --cli
```

---

## 📌 CodeAlpha Submission Guidelines

1. **Repository Name**:
   - Push this directory to your GitHub account under the exact repository name:
     `CodeAlpha_StudentGradeTracker`
2. **Video Demonstration**:
   - Record a 1-2 minute video walk-through demonstrating:
     - Launching the application.
     - Adding and editing a student score.
     - Viewing the real-time average, highest, and lowest statistics update.
     - Viewing the custom Grade Distribution bar chart.
     - Exporting the CSV / Text Summary report.
3. **LinkedIn Post**:
   - Post your video recording tagging **@CodeAlpha** with your GitHub repository link.
4. **Submission Form**:
   - Submit your GitHub repository URL and LinkedIn post link using the CodeAlpha submission form shared in your WhatsApp group.

---

## 📄 License
Created as part of the CodeAlpha Java Programming Internship.
