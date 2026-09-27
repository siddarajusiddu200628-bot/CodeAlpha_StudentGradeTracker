package com.codealpha.gradetracker.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Model representing a student and their academic performance record.
 */
public class Student implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String subject;
    private double score;

    public Student(String id, String name, String subject, double score) {
        this.id = id != null ? id.trim() : "";
        this.name = name != null ? name.trim() : "";
        this.subject = subject != null ? subject.trim() : "General";
        this.score = Math.max(0.0, Math.min(100.0, score));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id != null ? id.trim() : "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name != null ? name.trim() : "";
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject != null ? subject.trim() : "General";
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = Math.max(0.0, Math.min(100.0, score));
    }

    /**
     * Determines letter grade based on standard grading scale.
     */
    public String getLetterGrade() {
        if (score >= 90.0) return "A";
        if (score >= 80.0) return "B";
        if (score >= 70.0) return "C";
        if (score >= 60.0) return "D";
        return "F";
    }

    /**
     * Calculates 4.0 scale GPA equivalent.
     */
    public double getGpa() {
        if (score >= 93.0) return 4.0;
        if (score >= 90.0) return 3.7;
        if (score >= 87.0) return 3.3;
        if (score >= 83.0) return 3.0;
        if (score >= 80.0) return 2.7;
        if (score >= 77.0) return 2.3;
        if (score >= 73.0) return 2.0;
        if (score >= 70.0) return 1.7;
        if (score >= 67.0) return 1.3;
        if (score >= 60.0) return 1.0;
        return 0.0;
    }

    /**
     * Passing criteria: score >= 60.0
     */
    public boolean isPassed() {
        return score >= 60.0;
    }

    public String getStatus() {
        return isPassed() ? "Passed" : "Failed";
    }

    /**
     * Converts to CSV string: id,name,subject,score
     */
    public String toCsvRow() {
        return String.format("\"%s\",\"%s\",\"%s\",%.2f",
                escapeCsv(id), escapeCsv(name), escapeCsv(subject), score);
    }

    private String escapeCsv(String str) {
        return str.replace("\"", "\"\"");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return Double.compare(student.score, score) == 0 &&
                Objects.equals(id, student.id) &&
                Objects.equals(name, student.name) &&
                Objects.equals(subject, student.subject);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, subject, score);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s - %s: %.1f (%s)", id, name, subject, score, getLetterGrade());
    }
}
