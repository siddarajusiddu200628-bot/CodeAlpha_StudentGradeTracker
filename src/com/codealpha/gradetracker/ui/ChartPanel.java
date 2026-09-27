package com.codealpha.gradetracker.ui;

import com.codealpha.gradetracker.model.GradeStats;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A custom Swing JPanel that draws a modern bar chart for grade distribution.
 */
public class ChartPanel extends JPanel {
    private GradeStats stats;

    private static final Map<String, Color> GRADE_COLORS = new LinkedHashMap<>();
    private static final Map<String, String> GRADE_LABELS = new LinkedHashMap<>();

    static {
        GRADE_COLORS.put("A", new Color(16, 185, 129));  // Emerald
        GRADE_COLORS.put("B", new Color(59, 130, 246));  // Blue
        GRADE_COLORS.put("C", new Color(245, 158, 11));  // Amber
        GRADE_COLORS.put("D", new Color(249, 115, 22));  // Orange
        GRADE_COLORS.put("F", new Color(239, 68, 68));   // Red

        GRADE_LABELS.put("A", "Grade A (90-100)");
        GRADE_LABELS.put("B", "Grade B (80-89)");
        GRADE_LABELS.put("C", "Grade C (70-79)");
        GRADE_LABELS.put("D", "Grade D (60-69)");
        GRADE_LABELS.put("F", "Grade F (<60)");
    }

    public ChartPanel() {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(500, 320));
        stats = GradeStats.empty();
    }

    public void updateStats(GradeStats stats) {
        this.stats = stats != null ? stats : GradeStats.empty();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();

        // Enable high-quality anti-aliasing
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // Draw card background
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, width, height);

        if (stats == null || stats.getTotalStudents() == 0) {
            drawEmptyState(g2, width, height);
            g2.dispose();
            return;
        }

        // Title
        g2.setColor(new Color(30, 41, 59));
        g2.setFont(new Font("Segoe UI", Font.BOLD, 15));
        g2.drawString("Grade Distribution Overview", 24, 30);

        g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        g2.setColor(new Color(100, 116, 139));
        g2.drawString(String.format("Total Students: %d   |   Class Avg: %.1f%%   |   Pass Rate: %.1f%%",
                stats.getTotalStudents(), stats.getAverage(), stats.getPassRate()), 24, 50);

        // Chart area bounds
        int paddingLeft = 60;
        int paddingRight = 40;
        int paddingTop = 80;
        int paddingBottom = 60;

        int chartWidth = width - paddingLeft - paddingRight;
        int chartHeight = height - paddingTop - paddingBottom;

        if (chartWidth <= 0 || chartHeight <= 0) {
            g2.dispose();
            return;
        }

        Map<String, Integer> dist = stats.getDistribution();
        int maxCount = 1;
        for (int count : dist.values()) {
            if (count > maxCount) maxCount = count;
        }

        // Draw Y-axis grid lines
        int yTicks = Math.min(maxCount, 5);
        if (yTicks < 1) yTicks = 1;

        g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        Stroke dashed = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{4.0f}, 0.0f);
        Stroke solid = new BasicStroke(1.0f);

        for (int i = 0; i <= yTicks; i++) {
            int val = (int) Math.round(((double) i / yTicks) * maxCount);
            int y = paddingTop + chartHeight - (int) Math.round(((double) val / maxCount) * chartHeight);

            // Gridline
            g2.setColor(new Color(241, 245, 249));
            g2.setStroke(dashed);
            g2.drawLine(paddingLeft, y, paddingLeft + chartWidth, y);

            // Label
            g2.setColor(new Color(148, 163, 184));
            String valStr = String.valueOf(val);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(valStr, paddingLeft - fm.stringWidth(valStr) - 10, y + 4);
        }

        // Base axis line
        g2.setStroke(solid);
        g2.setColor(new Color(203, 213, 225));
        g2.drawLine(paddingLeft, paddingTop + chartHeight, paddingLeft + chartWidth, paddingTop + chartHeight);

        // Draw Bars
        int barSlotWidth = chartWidth / dist.size();
        int barWidth = Math.min(54, (int) (barSlotWidth * 0.65));
        int index = 0;

        for (Map.Entry<String, Integer> entry : dist.entrySet()) {
            String grade = entry.getKey();
            int count = entry.getValue();

            int slotX = paddingLeft + (index * barSlotWidth);
            int barX = slotX + (barSlotWidth - barWidth) / 2;

            double ratio = (double) count / maxCount;
            int barHeight = (int) (ratio * chartHeight);
            int barY = paddingTop + chartHeight - barHeight;

            Color barColor = GRADE_COLORS.getOrDefault(grade, new Color(99, 102, 241));

            // Bar fill with slight gradient
            GradientPaint gp = new GradientPaint(
                    barX, barY, barColor,
                    barX, barY + barHeight, barColor.darker()
            );
            g2.setPaint(gp);
            g2.fillRoundRect(barX, barY, barWidth, Math.max(barHeight, 2), 8, 8);

            // Top count value
            g2.setColor(new Color(30, 41, 59));
            g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
            String countLabel = String.valueOf(count);
            FontMetrics fm = g2.getFontMetrics();
            int countX = barX + (barWidth - fm.stringWidth(countLabel)) / 2;
            g2.drawString(countLabel, countX, Math.max(paddingTop + 14, barY - 6));

            // Percentage sub-label
            if (stats.getTotalStudents() > 0 && count > 0) {
                double pct = (count * 100.0) / stats.getTotalStudents();
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                g2.setColor(new Color(100, 116, 139));
                String pctLabel = String.format("%.0f%%", pct);
                int pctX = barX + (barWidth - g2.getFontMetrics().stringWidth(pctLabel)) / 2;
                g2.drawString(pctLabel, pctX, Math.max(paddingTop + 26, barY - 20));
            }

            // X-Axis labels
            g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
            g2.setColor(new Color(51, 65, 85));
            int labelX = barX + (barWidth - g2.getFontMetrics().stringWidth(grade)) / 2;
            g2.drawString(grade, labelX, paddingTop + chartHeight + 20);

            // Range label
            String rangeLabel = getScoreRange(grade);
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            g2.setColor(new Color(148, 163, 184));
            int rangeX = barX + (barWidth - g2.getFontMetrics().stringWidth(rangeLabel)) / 2;
            g2.drawString(rangeLabel, rangeX, paddingTop + chartHeight + 35);

            index++;
        }

        g2.dispose();
    }

    private String getScoreRange(String grade) {
        switch (grade) {
            case "A": return "90-100";
            case "B": return "80-89";
            case "C": return "70-79";
            case "D": return "60-69";
            case "F": return "<60";
            default: return "";
        }
    }

    private void drawEmptyState(Graphics2D g2, int width, int height) {
        g2.setColor(new Color(148, 163, 184));
        g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
        String text1 = "No student records to display";
        FontMetrics fm1 = g2.getFontMetrics();
        g2.drawString(text1, (width - fm1.stringWidth(text1)) / 2, height / 2 - 10);

        g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        g2.setColor(new Color(160, 174, 192));
        String text2 = "Add new students or click 'Load Sample Data' above.";
        FontMetrics fm2 = g2.getFontMetrics();
        g2.drawString(text2, (width - fm2.stringWidth(text2)) / 2, height / 2 + 15);
    }
}
