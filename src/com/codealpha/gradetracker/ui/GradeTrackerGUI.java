package com.codealpha.gradetracker.ui;

import com.codealpha.gradetracker.model.GradeStats;
import com.codealpha.gradetracker.model.Student;
import com.codealpha.gradetracker.service.GradeManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;

/**
 * Modern, polished Swing Desktop GUI for Student Grade Tracker.
 */
public class GradeTrackerGUI extends JFrame {
    private final GradeManager manager;

    // UI Components
    private JTable studentTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private ChartPanel chartPanel;
    private JTextArea reportArea;

    // Stat Cards Labels
    private JLabel totalStudentsVal;
    private JLabel classAverageVal;
    private JLabel highestScoreVal;
    private JLabel lowestScoreVal;
    private JLabel passRateVal;
    private JLabel statusBar;

    // Palette
    private static final Color COLOR_PRIMARY = new Color(79, 70, 229);      // Indigo 600
    private static final Color COLOR_PRIMARY_HOVER = new Color(67, 56, 202);
    private static final Color COLOR_BG = new Color(248, 250, 252);          // Slate 50
    private static final Color COLOR_CARD_BG = Color.WHITE;
    private static final Color COLOR_BORDER = new Color(226, 232, 240);      // Slate 200
    private static final Color COLOR_TEXT_MAIN = new Color(30, 41, 59);      // Slate 800
    private static final Color COLOR_TEXT_MUTED = new Color(100, 116, 139);  // Slate 500
    private static final Color COLOR_SUCCESS = new Color(16, 185, 129);      // Emerald 500
    private static final Color COLOR_DANGER = new Color(239, 68, 68);        // Red 500

    public GradeTrackerGUI(GradeManager manager) {
        this.manager = manager;
        setTitle("CodeAlpha - Student Grade Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1240, 780);
        setMinimumSize(new Dimension(980, 640));
        setLocationRelativeTo(null);

        initUI();
        refreshAll();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout(0, 12));
        root.setBackground(COLOR_BG);
        root.setBorder(new EmptyBorder(16, 20, 16, 20));

        // 1. Top Header & Stat Cards
        JPanel topContainer = new JPanel(new BorderLayout(0, 12));
        topContainer.setOpaque(false);
        topContainer.add(createHeaderPanel(), BorderLayout.NORTH);
        topContainer.add(createStatCardsPanel(), BorderLayout.SOUTH);
        root.add(topContainer, BorderLayout.NORTH);

        // 2. Central Split Pane (Table on left, Charts & Report on right)
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, createTablePanel(), createRightPanel());
        splitPane.setResizeWeight(0.58);
        splitPane.setDividerSize(6);
        splitPane.setBorder(null);
        splitPane.setOpaque(false);
        root.add(splitPane, BorderLayout.CENTER);

        // 3. Status Bar
        statusBar = new JLabel(" Ready. Load sample data or add new students to begin.");
        statusBar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusBar.setForeground(COLOR_TEXT_MUTED);
        statusBar.setBorder(new EmptyBorder(6, 4, 0, 4));
        root.add(statusBar, BorderLayout.SOUTH);

        setContentPane(root);
    }

    /**
     * Header panel with title, subtitle, and primary quick actions.
     */
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel titleBlock = new JPanel(new GridLayout(2, 1, 0, 2));
        titleBlock.setOpaque(false);

        JLabel title = new JLabel("🎓 Student Grade Tracker");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(COLOR_TEXT_MAIN);

        JLabel subtitle = new JLabel("CodeAlpha Java Programming Internship — Task 1");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(COLOR_TEXT_MUTED);

        titleBlock.add(title);
        titleBlock.add(subtitle);
        header.add(titleBlock, BorderLayout.WEST);

        // Header buttons
        JPanel actionBlock = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actionBlock.setOpaque(false);

        JButton sampleBtn = createSecondaryButton("⚡ Load Sample Data");
        sampleBtn.addActionListener(e -> {
            manager.loadSampleData();
            refreshAll();
            setStatus("Loaded sample student data successfully.");
        });

        JButton importBtn = createSecondaryButton("📥 Import CSV");
        importBtn.addActionListener(e -> importCsvAction());

        JButton exportBtn = createSecondaryButton("📤 Export CSV");
        exportBtn.addActionListener(e -> exportCsvAction());

        actionBlock.add(sampleBtn);
        actionBlock.add(importBtn);
        actionBlock.add(exportBtn);
        header.add(actionBlock, BorderLayout.EAST);

        return header;
    }

    /**
     * KPI Stat Cards for at-a-glance analytics.
     */
    private JPanel createStatCardsPanel() {
        JPanel cards = new JPanel(new GridLayout(1, 5, 12, 0));
        cards.setOpaque(false);

        totalStudentsVal = new JLabel("0", SwingConstants.CENTER);
        classAverageVal = new JLabel("0.0%", SwingConstants.CENTER);
        highestScoreVal = new JLabel("N/A", SwingConstants.CENTER);
        lowestScoreVal = new JLabel("N/A", SwingConstants.CENTER);
        passRateVal = new JLabel("0.0%", SwingConstants.CENTER);

        cards.add(createCard("Total Students", totalStudentsVal, new Color(59, 130, 246)));
        cards.add(createCard("Class Average", classAverageVal, COLOR_PRIMARY));
        cards.add(createCard("Highest Score", highestScoreVal, COLOR_SUCCESS));
        cards.add(createCard("Lowest Score", lowestScoreVal, COLOR_DANGER));
        cards.add(createCard("Pass Rate", passRateVal, new Color(14, 165, 233)));

        return cards;
    }

    private JPanel createCard(String title, JLabel valueLabel, Color accent) {
        JPanel card = new JPanel(new BorderLayout(0, 4));
        card.setBackground(COLOR_CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(10, 12, 10, 12)
        ));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        titleLbl.setForeground(COLOR_TEXT_MUTED);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 17));
        valueLabel.setForeground(accent);

        card.add(titleLbl, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    /**
     * Left panel containing student table, search bar, and CRUD operations.
     */
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(COLOR_CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(12, 14, 12, 14)
        ));

        // Search & Filter Toolbar
        JPanel searchBar = new JPanel(new BorderLayout(8, 0));
        searchBar.setOpaque(false);

        JLabel searchIcon = new JLabel("🔍 Search:");
        searchIcon.setFont(new Font("Segoe UI", Font.BOLD, 12));
        searchIcon.setForeground(COLOR_TEXT_MUTED);

        searchField = new JTextField();
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDER, 1),
                new EmptyBorder(6, 8, 6, 8)
        ));
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterTable(); }
            public void removeUpdate(DocumentEvent e) { filterTable(); }
            public void changedUpdate(DocumentEvent e) { filterTable(); }
        });

        searchBar.add(searchIcon, BorderLayout.WEST);
        searchBar.add(searchField, BorderLayout.CENTER);

        // CRUD Button Bar
        JPanel crudBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        crudBar.setOpaque(false);
        crudBar.setBorder(new EmptyBorder(6, 0, 0, 0));

        JButton addBtn = createPrimaryButton("+ Add Student");
        addBtn.addActionListener(e -> openAddDialog());

        JButton editBtn = createSecondaryButton("✏️ Edit");
        editBtn.addActionListener(e -> openEditDialog());

        JButton deleteBtn = createSecondaryButton("🗑️ Delete");
        deleteBtn.addActionListener(e -> deleteSelectedStudent());

        JButton clearBtn = createSecondaryButton("Clear All");
        clearBtn.addActionListener(e -> clearAllStudents());

        crudBar.add(addBtn);
        crudBar.add(editBtn);
        crudBar.add(deleteBtn);
        crudBar.add(clearBtn);

        JPanel topTools = new JPanel(new BorderLayout(0, 8));
        topTools.setOpaque(false);
        topTools.add(searchBar, BorderLayout.NORTH);
        topTools.add(crudBar, BorderLayout.SOUTH);

        // Table Setup
        String[] columnNames = {"ID", "Student Name", "Subject", "Score (%)", "Grade", "GPA", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        studentTable = new JTable(tableModel);
        studentTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        studentTable.setRowHeight(28);
        studentTable.setShowVerticalLines(false);
        studentTable.setGridColor(new Color(241, 245, 249));
        studentTable.setSelectionBackground(new Color(238, 242, 255));
        studentTable.setSelectionForeground(COLOR_TEXT_MAIN);

        JTableHeader header = studentTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(new Color(241, 245, 249));
        header.setForeground(COLOR_TEXT_MAIN);
        header.setPreferredSize(new Dimension(0, 32));

        // Custom Renderers for Badges
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        studentTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        studentTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        studentTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        studentTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);

        studentTable.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                setFont(new Font("Segoe UI", Font.BOLD, 12));
                if ("Passed".equals(value)) {
                    setForeground(isSelected ? new Color(6, 95, 70) : new Color(16, 185, 129));
                } else {
                    setForeground(isSelected ? new Color(153, 27, 27) : new Color(239, 68, 68));
                }
                return c;
            }
        });

        // Double click to edit
        studentTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    openEditDialog();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.setBorder(new LineBorder(COLOR_BORDER, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        panel.add(topTools, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Right panel containing Grade Distribution Chart and Full Report views.
     */
    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(8, 8, 8, 8)
        ));

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 12));

        // Tab 1: Chart
        chartPanel = new ChartPanel();
        tabbedPane.addTab("📊 Grade Distribution", chartPanel);

        // Tab 2: Text Report
        JPanel reportContainer = new JPanel(new BorderLayout(0, 8));
        reportContainer.setBackground(Color.WHITE);
        reportContainer.setBorder(new EmptyBorder(8, 8, 8, 8));

        reportArea = new JTextArea();
        reportArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        reportArea.setEditable(false);
        reportArea.setBackground(new Color(250, 250, 252));
        reportArea.setBorder(new EmptyBorder(8, 8, 8, 8));

        JScrollPane reportScroll = new JScrollPane(reportArea);
        reportScroll.setBorder(new LineBorder(COLOR_BORDER, 1));

        JPanel reportBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        reportBar.setOpaque(false);
        JButton exportReportBtn = createSecondaryButton("💾 Export Report (.txt)");
        exportReportBtn.addActionListener(e -> exportReportAction());
        reportBar.add(exportReportBtn);

        reportContainer.add(reportScroll, BorderLayout.CENTER);
        reportContainer.add(reportBar, BorderLayout.SOUTH);

        tabbedPane.addTab("📄 Summary Report", reportContainer);

        panel.add(tabbedPane, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Synchronizes UI state with the GradeManager model.
     */
    public void refreshAll() {
        filterTable();
        GradeStats stats = manager.calculateStats();

        // Update cards
        totalStudentsVal.setText(String.valueOf(stats.getTotalStudents()));
        classAverageVal.setText(String.format("%.1f%%", stats.getAverage()));

        if (stats.getHighestStudent() != null) {
            highestScoreVal.setText(String.format("%.1f%% (%s)", stats.getHighestScore(), stats.getHighestStudent().getName()));
            highestScoreVal.setToolTipText(stats.getHighestStudent().getName() + " - " + stats.getHighestStudent().getSubject());
        } else {
            highestScoreVal.setText("N/A");
        }

        if (stats.getLowestStudent() != null) {
            lowestScoreVal.setText(String.format("%.1f%% (%s)", stats.getLowestScore(), stats.getLowestStudent().getName()));
            lowestScoreVal.setToolTipText(stats.getLowestStudent().getName() + " - " + stats.getLowestStudent().getSubject());
        } else {
            lowestScoreVal.setText("N/A");
        }

        passRateVal.setText(String.format("%.1f%% (%d/%d)", stats.getPassRate(), stats.getPassCount(), stats.getTotalStudents()));

        // Update Chart
        chartPanel.updateStats(stats);

        // Update Report Text
        reportArea.setText(manager.generateSummaryReport());
        reportArea.setCaretPosition(0);
    }

    private void filterTable() {
        String query = searchField != null ? searchField.getText() : "";
        List<Student> filtered = manager.filter(query);

        tableModel.setRowCount(0);
        for (Student s : filtered) {
            tableModel.addRow(new Object[]{
                    s.getId(),
                    s.getName(),
                    s.getSubject(),
                    String.format("%.1f", s.getScore()),
                    s.getLetterGrade(),
                    String.format("%.1f", s.getGpa()),
                    s.getStatus()
            });
        }
    }

    private void openAddDialog() {
        StudentDialog dialog = new StudentDialog(this, null);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            manager.addStudent(dialog.getStudent());
            refreshAll();
            setStatus("Added student: " + dialog.getStudent().getName());
        }
    }

    private void openEditDialog() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a student from the table to edit.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String studentId = (String) tableModel.getValueAt(selectedRow, 0);
        List<Student> all = manager.getStudents();
        int targetIndex = -1;
        Student targetStudent = null;

        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getId().equalsIgnoreCase(studentId)) {
                targetIndex = i;
                targetStudent = all.get(i);
                break;
            }
        }

        if (targetStudent == null) return;

        StudentDialog dialog = new StudentDialog(this, targetStudent);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            manager.updateStudent(targetIndex, dialog.getStudent());
            refreshAll();
            setStatus("Updated student: " + dialog.getStudent().getName());
        }
    }

    private void deleteSelectedStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a student from the table to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String studentId = (String) tableModel.getValueAt(selectedRow, 0);
        String studentName = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete student " + studentName + " (" + studentId + ")?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            List<Student> all = manager.getStudents();
            for (int i = 0; i < all.size(); i++) {
                if (all.get(i).getId().equalsIgnoreCase(studentId)) {
                    manager.removeStudent(i);
                    break;
                }
            }
            refreshAll();
            setStatus("Deleted student: " + studentName);
        }
    }

    private void clearAllStudents() {
        if (manager.getStudentCount() == 0) return;
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to clear ALL student records? This cannot be undone.",
                "Confirm Clear All", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            manager.clearAll();
            refreshAll();
            setStatus("Cleared all student records.");
        }
    }

    private void exportCsvAction() {
        if (manager.getStudentCount() == 0) {
            JOptionPane.showMessageDialog(this, "No records to export.", "Empty Roster", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("student_grades.csv"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                manager.saveToCsv(chooser.getSelectedFile());
                JOptionPane.showMessageDialog(this, "Data exported successfully to " + chooser.getSelectedFile().getName(), "Export Succeeded", JOptionPane.INFORMATION_MESSAGE);
                setStatus("Exported data to " + chooser.getSelectedFile().getAbsolutePath());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Failed to export CSV: " + ex.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void importCsvAction() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                int count = manager.loadFromCsv(chooser.getSelectedFile());
                refreshAll();
                JOptionPane.showMessageDialog(this, "Successfully imported " + count + " student records.", "Import Succeeded", JOptionPane.INFORMATION_MESSAGE);
                setStatus("Imported " + count + " records from " + chooser.getSelectedFile().getName());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Failed to import CSV: " + ex.getMessage(), "Import Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exportReportAction() {
        if (manager.getStudentCount() == 0) {
            JOptionPane.showMessageDialog(this, "No data available to generate report.", "Empty Roster", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("Grade_Tracker_Report.txt"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                Files.write(chooser.getSelectedFile().toPath(), reportArea.getText().getBytes());
                JOptionPane.showMessageDialog(this, "Report saved successfully!", "Report Saved", JOptionPane.INFORMATION_MESSAGE);
                setStatus("Saved report to " + chooser.getSelectedFile().getAbsolutePath());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Failed to save report: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void setStatus(String msg) {
        statusBar.setText(" " + msg);
    }

    // Modern Button Helpers
    private JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(COLOR_PRIMARY);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_PRIMARY.darker(), 1, true),
                new EmptyBorder(6, 14, 6, 14)
        ));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton createSecondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setBackground(Color.WHITE);
        btn.setForeground(COLOR_TEXT_MAIN);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(6, 12, 6, 12)
        ));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    /**
     * Modal Dialog for Adding / Editing Student.
     */
    private static class StudentDialog extends JDialog {
        private final JTextField idField = new JTextField();
        private final JTextField nameField = new JTextField();
        private final JComboBox<String> subjectCombo = new JComboBox<>(new String[]{
                "Computer Science", "Data Structures", "Database Systems",
                "Software Engineering", "Operating Systems", "Computer Networks", "Mathematics"
        });
        private final JTextField scoreField = new JTextField();
        private boolean saved = false;
        private Student student;

        public StudentDialog(JFrame parent, Student existing) {
            super(parent, existing == null ? "Add New Student" : "Edit Student", true);
            setSize(420, 320);
            setLocationRelativeTo(parent);
            setResizable(false);

            subjectCombo.setEditable(true);

            if (existing != null) {
                idField.setText(existing.getId());
                nameField.setText(existing.getName());
                subjectCombo.setSelectedItem(existing.getSubject());
                scoreField.setText(String.valueOf(existing.getScore()));
            }

            initDialog();
        }

        private void initDialog() {
            JPanel form = new JPanel(new GridLayout(4, 2, 10, 14));
            form.setBorder(new EmptyBorder(20, 24, 10, 24));

            form.add(new JLabel("Student ID:"));
            form.add(idField);

            form.add(new JLabel("Student Name:"));
            form.add(nameField);

            form.add(new JLabel("Subject / Course:"));
            form.add(subjectCombo);

            form.add(new JLabel("Score (0.0 - 100.0):"));
            form.add(scoreField);

            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
            JButton cancelBtn = new JButton("Cancel");
            cancelBtn.addActionListener(e -> dispose());

            JButton saveBtn = new JButton("Save Record");
            saveBtn.setBackground(new Color(79, 70, 229));
            saveBtn.setForeground(Color.WHITE);
            saveBtn.setFocusPainted(false);
            saveBtn.addActionListener(e -> onSave());

            btnPanel.add(cancelBtn);
            btnPanel.add(saveBtn);

            getContentPane().setLayout(new BorderLayout());
            getContentPane().add(form, BorderLayout.CENTER);
            getContentPane().add(btnPanel, BorderLayout.SOUTH);
        }

        private void onSave() {
            String id = idField.getText().trim();
            String name = nameField.getText().trim();
            String subject = Objects.toString(subjectCombo.getSelectedItem(), "").trim();
            String scoreStr = scoreField.getText().trim();

            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Student ID is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                idField.requestFocus();
                return;
            }
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Student Name is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                nameField.requestFocus();
                return;
            }
            if (subject.isEmpty()) {
                subject = "General";
            }

            double score;
            try {
                score = Double.parseDouble(scoreStr);
                if (score < 0.0 || score > 100.0) {
                    JOptionPane.showMessageDialog(this, "Score must be between 0.0 and 100.0.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    scoreField.requestFocus();
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid score number format.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                scoreField.requestFocus();
                return;
            }

            this.student = new Student(id, name, subject, score);
            this.saved = true;
            dispose();
        }

        public boolean isSaved() {
            return saved;
        }

        public Student getStudent() {
            return student;
        }
    }
}
