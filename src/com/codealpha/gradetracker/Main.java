package com.codealpha.gradetracker;

import com.codealpha.gradetracker.model.GradeStats;
import com.codealpha.gradetracker.model.Student;
import com.codealpha.gradetracker.service.GradeManager;
import com.codealpha.gradetracker.ui.GradeTrackerGUI;

import javax.swing.*;
import java.awt.GraphicsEnvironment;
import java.util.List;
import java.util.Scanner;

/**
 * Main application entry point for CodeAlpha Student Grade Tracker.
 * Supports both rich Modern Swing GUI (default) and Terminal CLI mode.
 */
public class Main {
    public static void main(String[] args) {
        GradeManager manager = new GradeManager();
        manager.loadSampleData(); // Pre-populate with realistic data for immediate demo

        boolean forceCli = false;
        for (String arg : args) {
            if ("--cli".equalsIgnoreCase(arg) || "-c".equalsIgnoreCase(arg)) {
                forceCli = true;
                break;
            }
        }

        if (forceCli || GraphicsEnvironment.isHeadless()) {
            runCli(manager);
        } else {
            launchGui(manager);
        }
    }

    private static void launchGui(GradeManager manager) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Set system look and feel for native widgets
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Fall back to default Swing look and feel
            }

            GradeTrackerGUI gui = new GradeTrackerGUI(manager);
            gui.setVisible(true);
        });
    }

    /**
     * Interactive terminal/console mode.
     */
    private static void runCli(GradeManager manager) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("=================================================");
        System.out.println("      CODEALPHA - STUDENT GRADE TRACKER (CLI)    ");
        System.out.println("=================================================");

        while (true) {
            System.out.println("\n--- MAIN MENU ---");
            System.out.println("1. View All Students");
            System.out.println("2. Add New Student");
            System.out.println("3. View Performance Statistics");
            System.out.println("4. Generate & Display Summary Report");
            System.out.println("5. Remove Student");
            System.out.println("6. Reset with Sample Data");
            System.out.println("7. Exit");
            System.out.print("Enter your choice (1-7): ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    List<Student> students = manager.getStudents();
                    if (students.isEmpty()) {
                        System.out.println("\n[!] No student records found.");
                    } else {
                        System.out.println("\n-------------------------------------------------------------------------");
                        System.out.printf("%-10s | %-20s | %-20s | %-6s | %-5s | %-7s\n", "ID", "Name", "Subject", "Score", "Grade", "Status");
                        System.out.println("-------------------------------------------------------------------------");
                        for (Student s : students) {
                            System.out.printf("%-10s | %-20s | %-20s | %5.1f%% | %-5s | %-7s\n",
                                    s.getId(), s.getName(), s.getSubject(), s.getScore(), s.getLetterGrade(), s.getStatus());
                        }
                        System.out.println("-------------------------------------------------------------------------");
                    }
                    break;

                case "2":
                    System.out.print("Enter Student ID (e.g., STU111): ");
                    String id = scanner.nextLine().trim();
                    System.out.print("Enter Student Name: ");
                    String name = scanner.nextLine().trim();
                    System.out.print("Enter Subject / Course: ");
                    String subject = scanner.nextLine().trim();
                    System.out.print("Enter Score (0 - 100): ");
                    try {
                        double score = Double.parseDouble(scanner.nextLine().trim());
                        if (score < 0 || score > 100) {
                            System.out.println("[ERROR] Score must be between 0 and 100.");
                        } else {
                            manager.addStudent(new Student(id, name, subject, score));
                            System.out.println("[SUCCESS] Student record added successfully.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("[ERROR] Invalid number format for score.");
                    }
                    break;

                case "3":
                    GradeStats stats = manager.calculateStats();
                    System.out.println("\n--- PERFORMANCE SUMMARY ---");
                    System.out.printf("Total Students : %d\n", stats.getTotalStudents());
                    System.out.printf("Class Average  : %.2f%%\n", stats.getAverage());
                    System.out.printf("Median Score   : %.2f%%\n", stats.getMedian());
                    if (stats.getHighestStudent() != null) {
                        System.out.printf("Highest Score  : %.2f%% (%s - %s)\n",
                                stats.getHighestScore(), stats.getHighestStudent().getName(), stats.getHighestStudent().getId());
                    }
                    if (stats.getLowestStudent() != null) {
                        System.out.printf("Lowest Score   : %.2f%% (%s - %s)\n",
                                stats.getLowestScore(), stats.getLowestStudent().getName(), stats.getLowestStudent().getId());
                    }
                    System.out.printf("Passing Rate   : %.1f%% (%d passed, %d failed)\n",
                            stats.getPassRate(), stats.getPassCount(), stats.getFailCount());
                    break;

                case "4":
                    System.out.println("\n" + manager.generateSummaryReport());
                    break;

                case "5":
                    System.out.print("Enter Student ID to remove: ");
                    String removeId = scanner.nextLine().trim();
                    List<Student> current = manager.getStudents();
                    boolean removed = false;
                    for (int i = 0; i < current.size(); i++) {
                        if (current.get(i).getId().equalsIgnoreCase(removeId)) {
                            manager.removeStudent(i);
                            removed = true;
                            System.out.println("[SUCCESS] Student " + removeId + " removed.");
                            break;
                        }
                    }
                    if (!removed) {
                        System.out.println("[!] No student found with ID: " + removeId);
                    }
                    break;

                case "6":
                    manager.loadSampleData();
                    System.out.println("[SUCCESS] Sample dataset loaded (10 students).");
                    break;

                case "7":
                    System.out.println("Exiting application. Goodbye!");
                    return;

                default:
                    System.out.println("[!] Invalid choice. Please choose 1-7.");
                    break;
            }
        }
    }
}
