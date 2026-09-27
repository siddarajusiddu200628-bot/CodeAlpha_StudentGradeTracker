package com.codealpha.gradetracker.service;

import com.codealpha.gradetracker.model.GradeStats;
import com.codealpha.gradetracker.model.Student;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Service managing student grade records, calculations, file persistence, and reporting.
 */
public class GradeManager {
    private final List<Student> students = new ArrayList<>();

    public GradeManager() {}

    public synchronized void addStudent(Student student) {
        if (student != null) {
            students.add(student);
        }
    }

    public synchronized void updateStudent(int index, Student updatedStudent) {
        if (index >= 0 && index < students.size() && updatedStudent != null) {
            students.set(index, updatedStudent);
        }
    }

    public synchronized boolean removeStudent(int index) {
        if (index >= 0 && index < students.size()) {
            students.remove(index);
            return true;
        }
        return false;
    }

    public synchronized void clearAll() {
        students.clear();
    }

    public synchronized List<Student> getStudents() {
        return Collections.unmodifiableList(new ArrayList<>(students));
    }

    public synchronized int getStudentCount() {
        return students.size();
    }

    /**
     * Computes comprehensive statistics: average, highest, lowest, median, pass rate, distribution.
     */
    public synchronized GradeStats calculateStats() {
        if (students.isEmpty()) {
            return GradeStats.empty();
        }

        double sum = 0.0;
        double highest = Double.NEGATIVE_INFINITY;
        Student highestStudent = null;
        double lowest = Double.POSITIVE_INFINITY;
        Student lowestStudent = null;
        int passCount = 0;

        Map<String, Integer> dist = new LinkedHashMap<>();
        dist.put("A", 0);
        dist.put("B", 0);
        dist.put("C", 0);
        dist.put("D", 0);
        dist.put("F", 0);

        List<Double> scores = new ArrayList<>(students.size());

        for (Student s : students) {
            double sc = s.getScore();
            scores.add(sc);
            sum += sc;

            if (sc > highest) {
                highest = sc;
                highestStudent = s;
            }
            if (sc < lowest) {
                lowest = sc;
                lowestStudent = s;
            }

            if (s.isPassed()) {
                passCount++;
            }

            String grade = s.getLetterGrade();
            dist.put(grade, dist.getOrDefault(grade, 0) + 1);
        }

        double average = sum / students.size();
        Collections.sort(scores);
        double median;
        int n = scores.size();
        if (n % 2 == 1) {
            median = scores.get(n / 2);
        } else {
            median = (scores.get((n / 2) - 1) + scores.get(n / 2)) / 2.0;
        }

        int failCount = students.size() - passCount;
        double passRate = ((double) passCount / students.size()) * 100.0;

        return new GradeStats(
                students.size(),
                average,
                highest,
                highestStudent,
                lowest,
                lowestStudent,
                median,
                passCount,
                failCount,
                passRate,
                dist
        );
    }

    /**
     * Filters students based on a search term across ID, Name, Subject, or Letter Grade.
     */
    public synchronized List<Student> filter(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getStudents();
        }
        String q = query.trim().toLowerCase();
        List<Student> results = new ArrayList<>();
        for (Student s : students) {
            if (s.getId().toLowerCase().contains(q) ||
                s.getName().toLowerCase().contains(q) ||
                s.getSubject().toLowerCase().contains(q) ||
                s.getLetterGrade().equalsIgnoreCase(q) ||
                s.getStatus().toLowerCase().contains(q)) {
                results.add(s);
            }
        }
        return results;
    }

    /**
     * Loads predefined realistic sample data for quick demonstration.
     */
    public synchronized void loadSampleData() {
        students.clear();
        students.add(new Student("STU101", "Emma Watson", "Computer Science", 96.5));
        students.add(new Student("STU102", "Liam Smith", "Data Structures", 88.0));
        students.add(new Student("STU103", "Sophia Chen", "Database Systems", 92.0));
        students.add(new Student("STU104", "Noah Williams", "Software Engineering", 74.5));
        students.add(new Student("STU105", "Olivia Martinez", "Computer Science", 85.0));
        students.add(new Student("STU106", "Lucas Brown", "Data Structures", 63.0));
        students.add(new Student("STU107", "Ava Taylor", "Database Systems", 79.5));
        students.add(new Student("STU108", "Mason Miller", "Operating Systems", 54.0));
        students.add(new Student("STU109", "Isabella Davis", "Software Engineering", 91.5));
        students.add(new Student("STU110", "Ethan Garcia", "Computer Networks", 82.0));
    }

    /**
     * Exports current student records to a CSV file.
     */
    public synchronized void saveToCsv(File file) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(file), StandardCharsets.UTF_8))) {
            writer.write("ID,Name,Subject,Score,Letter Grade,GPA,Status");
            writer.newLine();
            for (Student s : students) {
                writer.write(String.format("\"%s\",\"%s\",\"%s\",%.2f,\"%s\",%.1f,\"%s\"",
                        s.getId().replace("\"", "\"\""),
                        s.getName().replace("\"", "\"\""),
                        s.getSubject().replace("\"", "\"\""),
                        s.getScore(),
                        s.getLetterGrade(),
                        s.getGpa(),
                        s.getStatus()));
                writer.newLine();
            }
        }
    }

    /**
     * Imports student records from a CSV file.
     */
    public synchronized int loadFromCsv(File file) throws IOException {
        List<Student> loaded = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(file), StandardCharsets.UTF_8))) {
            String header = reader.readLine(); // skip header
            if (header == null) return 0;

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                List<String> tokens = parseCsvLine(line);
                if (tokens.size() >= 4) {
                    String id = tokens.get(0).trim();
                    String name = tokens.get(1).trim();
                    String subject = tokens.get(2).trim();
                    try {
                        double score = Double.parseDouble(tokens.get(3).trim());
                        loaded.add(new Student(id, name, subject, score));
                    } catch (NumberFormatException ignored) {}
                }
            }
        }
        students.clear();
        students.addAll(loaded);
        return loaded.size();
    }

    private List<String> parseCsvLine(String line) {
        List<String> tokens = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '\"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '\"') {
                    sb.append('\"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                tokens.add(sb.toString().trim());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        tokens.add(sb.toString().trim());
        return tokens;
    }

    /**
     * Generates a detailed text summary report suitable for exporting and displaying.
     */
    public synchronized String generateSummaryReport() {
        GradeStats stats = calculateStats();
        StringBuilder sb = new StringBuilder();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        sb.append("========================================================================================\n");
        sb.append("                       CODEALPHA - STUDENT GRADE TRACKER REPORT                         \n");
        sb.append("========================================================================================\n");
        sb.append("Generated on : ").append(timestamp).append("\n");
        sb.append("Total Records: ").append(stats.getTotalStudents()).append("\n\n");

        sb.append("----------------------------------------------------------------------------------------\n");
        sb.append(" 1. PERFORMANCE SUMMARY                                                                 \n");
        sb.append("----------------------------------------------------------------------------------------\n");
        sb.append(String.format("  * Class Average Score : %.2f%%\n", stats.getAverage()));
        sb.append(String.format("  * Median Score        : %.2f%%\n", stats.getMedian()));
        if (stats.getHighestStudent() != null) {
            sb.append(String.format("  * Highest Score       : %.2f%% (%s - %s)\n",
                    stats.getHighestScore(), stats.getHighestStudent().getName(), stats.getHighestStudent().getId()));
        } else {
            sb.append("  * Highest Score       : N/A\n");
        }
        if (stats.getLowestStudent() != null) {
            sb.append(String.format("  * Lowest Score        : %.2f%% (%s - %s)\n",
                    stats.getLowestScore(), stats.getLowestStudent().getName(), stats.getLowestStudent().getId()));
        } else {
            sb.append("  * Lowest Score        : N/A\n");
        }
        sb.append(String.format("  * Students Passed     : %d\n", stats.getPassCount()));
        sb.append(String.format("  * Students Failed     : %d\n", stats.getFailCount()));
        sb.append(String.format("  * Passing Rate        : %.1f%%\n\n", stats.getPassRate()));

        sb.append("----------------------------------------------------------------------------------------\n");
        sb.append(" 2. GRADE DISTRIBUTION                                                                 \n");
        sb.append("----------------------------------------------------------------------------------------\n");
        Map<String, Integer> dist = stats.getDistribution();
        for (Map.Entry<String, Integer> entry : dist.entrySet()) {
            double pct = stats.getTotalStudents() > 0 ? (entry.getValue() * 100.0 / stats.getTotalStudents()) : 0.0;
            sb.append(String.format("  * Grade %-2s : %3d student(s) (%5.1f%%)\n", entry.getKey(), entry.getValue(), pct));
        }
        sb.append("\n");

        sb.append("----------------------------------------------------------------------------------------\n");
        sb.append(" 3. COMPLETE STUDENT ROSTER                                                             \n");
        sb.append("----------------------------------------------------------------------------------------\n");
        sb.append(String.format("%-10s | %-22s | %-20s | %-7s | %-5s | %-5s | %-7s\n",
                "ID", "Student Name", "Subject", "Score", "Grade", "GPA", "Status"));
        sb.append("----------------------------------------------------------------------------------------\n");
        for (Student s : students) {
            sb.append(String.format("%-10s | %-22s | %-20s | %6.1f%% | %-5s | %-5.1f | %-7s\n",
                    s.getId(),
                    s.getName(),
                    s.getSubject(),
                    s.getScore(),
                    s.getLetterGrade(),
                    s.getGpa(),
                    s.getStatus()));
        }
        sb.append("========================================================================================\n");
        sb.append("End of Report\n");

        return sb.toString();
    }
}
