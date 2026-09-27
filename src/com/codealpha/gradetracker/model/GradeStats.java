package com.codealpha.gradetracker.model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Encapsulates computed statistics for a collection of students.
 */
public class GradeStats {
    private final int totalStudents;
    private final double average;
    private final double highestScore;
    private final Student highestStudent;
    private final double lowestScore;
    private final Student lowestStudent;
    private final double median;
    private final int passCount;
    private final int failCount;
    private final double passRate;
    private final Map<String, Integer> distribution;

    public GradeStats(int totalStudents, double average, double highestScore, Student highestStudent,
                      double lowestScore, Student lowestStudent, double median,
                      int passCount, int failCount, double passRate, Map<String, Integer> distribution) {
        this.totalStudents = totalStudents;
        this.average = average;
        this.highestScore = highestScore;
        this.highestStudent = highestStudent;
        this.lowestScore = lowestScore;
        this.lowestStudent = lowestStudent;
        this.median = median;
        this.passCount = passCount;
        this.failCount = failCount;
        this.passRate = passRate;
        this.distribution = Collections.unmodifiableMap(new LinkedHashMap<>(distribution));
    }

    public static GradeStats empty() {
        Map<String, Integer> dist = new LinkedHashMap<>();
        dist.put("A", 0);
        dist.put("B", 0);
        dist.put("C", 0);
        dist.put("D", 0);
        dist.put("F", 0);
        return new GradeStats(0, 0.0, 0.0, null, 0.0, null, 0.0, 0, 0, 0.0, dist);
    }

    public int getTotalStudents() {
        return totalStudents;
    }

    public double getAverage() {
        return average;
    }

    public double getHighestScore() {
        return highestScore;
    }

    public Student getHighestStudent() {
        return highestStudent;
    }

    public double getLowestScore() {
        return lowestScore;
    }

    public Student getLowestStudent() {
        return lowestStudent;
    }

    public double getMedian() {
        return median;
    }

    public int getPassCount() {
        return passCount;
    }

    public int getFailCount() {
        return failCount;
    }

    public double getPassRate() {
        return passRate;
    }

    public Map<String, Integer> getDistribution() {
        return distribution;
    }
}
