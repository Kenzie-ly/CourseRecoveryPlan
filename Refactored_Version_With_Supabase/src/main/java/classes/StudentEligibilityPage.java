package classes;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import controller.LoginController;
import controller.ModuleController;
import repository.EnrollmentRepository;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class StudentEligibilityPage extends JFrame {
    private LoginController session;
    private DefaultTableModel failedModel;
    private DefaultTableModel passModel;
    private JTable jTablePass;
    private JTable jTableFailed;
    private ModuleController moduleController;
    private JLabel loadingLabel;
    private JPanel tablePanel;
    private CardLayout cardLayout;
    private List<Object[]> passedRows;
    private List<Object[]> failedRows;
    List<Student> allStudents;

    public StudentEligibilityPage(LoginController session) {
        
        this.moduleController = new ModuleController();
        this.session = session;
        this.allStudents = moduleController.getAllEnrolledStudents();
        this.initFrame(); 
        this.loadDataInBackground();
    }

    private void initFrame() {
        this.setTitle("Student Eligibility Check");
        this.setDefaultCloseOperation(3);
        this.setSize(900, 600);
        this.setLocationRelativeTo((Component)null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel headerLabel = new JLabel("Eligibility Check and Enrolment", 0);

        JButton backBtn = new JButton("Back");
        backBtn.addActionListener((e) -> this.goBack());

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener((e) -> this.refreshTable());

        headerPanel.add(headerLabel, "Center");
        headerPanel.add(backBtn, "West");
        headerPanel.add(refreshBtn, "East");
        mainPanel.add(headerPanel, "North");

        this.failedModel = new DefaultTableModel(new String[]{"Student ID", "Name", "Major", "GPA"}, 0);
        this.passModel = new DefaultTableModel(new String[]{"Student ID", "Name", "Major", "GPA"}, 0);
        this.jTableFailed = new JTable(failedModel);
        this.jTablePass = new JTable(passModel);
        JScrollPane scrollPanePass = new JScrollPane(jTablePass);
        JScrollPane scrollPaneFailed = new JScrollPane(jTableFailed);
        scrollPanePass.setVerticalScrollBarPolicy(22);
        scrollPaneFailed.setVerticalScrollBarPolicy(22);

        JPanel topButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        JButton passStudentBtn = new JButton("Passed Students");
        JButton failStudentBtn = new JButton("Failed Students");
        topButtonPanel.add(passStudentBtn);
        topButtonPanel.add(failStudentBtn);

        tablePanel = new JPanel(new CardLayout());
        tablePanel.add(scrollPanePass, "PASS");
        tablePanel.add(scrollPaneFailed, "FAIL");
        
        // Add loading label
        loadingLabel = new JLabel("Loading student data...", SwingConstants.CENTER);
        loadingLabel.setFont(new Font("Arial", Font.BOLD, 14));
        tablePanel.add(loadingLabel, "LOADING");
        
        cardLayout = (CardLayout) (tablePanel.getLayout());
        cardLayout.show(tablePanel, "LOADING");

        passStudentBtn.addActionListener(e -> cardLayout.show(tablePanel, "PASS"));
        failStudentBtn.addActionListener(e -> cardLayout.show(tablePanel, "FAIL"));

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(topButtonPanel, BorderLayout.WEST);

        JButton allowRegistrationBtn = new JButton("Allow all passed students registration");
        allowRegistrationBtn.addActionListener(e -> {
            sendEmailNotificationPass();
            JOptionPane.showMessageDialog(this, "All passed students are now allowed to register.");
        });
        bottomPanel.add(allowRegistrationBtn, BorderLayout.EAST);

        mainPanel.add(tablePanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        this.add(mainPanel);
    }

    private void loadDataInBackground() {
        cardLayout.show(tablePanel, "LOADING");

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                // DB Query 2: all enrollments at once with courses joined — no per-row lookups
                Map<String, List<Enrollement>> enrollmentMap =
                        EnrollmentRepository.getAllEnrollmentsGroupedByStudent();

                passedRows = new ArrayList<>();
                failedRows = new ArrayList<>();

                for (Student s : allStudents) {
                    List<Enrollement> enrollements =
                            enrollmentMap.getOrDefault(s.getStudentID(), List.of());

                    int failedCount = 0;
                    for (Enrollement e : enrollements) {
                        String grade = e.getGrade();
                        if (grade != null && grade.matches("[DEF][+-]?")) {
                            failedCount++;
                        }
                    }

                    double roundedCGPA = Math.round(moduleController.calcStudentGPA(s, s.getSem()) * 100.0) / 100.0;
                    boolean canProgress = failedCount <= 3;

                    String id = s.getStudentID();
                    String name = s.getFirstName() + " " + s.getLastName();
                    String majorName = s.getMajor().getMajorName();

                    if (roundedCGPA >= 2.0 && canProgress) {
                        passedRows.add(new Object[]{id, name, majorName, roundedCGPA});
                    } else {
                        failedRows.add(new Object[]{id, name, majorName, roundedCGPA});
                    }
                }

                SwingUtilities.invokeLater(() -> updateTables());
                return null;
            }
        };
        worker.execute();
    }

    private void updateTables() {
        passModel.setRowCount(0);
        failedModel.setRowCount(0);

        for (Object[] row : passedRows) {
            passModel.addRow(row);
        }

        for (Object[] row : failedRows) {
            failedModel.addRow(row);
        }

        // Switch from loading to pass tab
        cardLayout.show(tablePanel, "PASS");
    }

    private void sendEmailNotificationPass() {
        String subject = "Pass/Fail Notice";
        for (Student s : moduleController.getAllEnrolledStudents()) {
            String name = s.getFirstName() + " " + s.getLastName();
            double roundedGPA = Math.round(moduleController.calcStudentGPA(s, s.getSem()) * 100.0) / 100.0;
            
            Optional<Object[]> matchingFailedStudents = failedRows.stream()
                .filter(p -> p[0].equals(s.getStudentID()))
                .findFirst();
            

            String studentEmail = s.getEmail();
            if(matchingFailedStudents.isPresent()){
                String bodyText = "We are sorry to inform you, " + name + " that you have failed with a GPA of " + roundedGPA;
                EmailSender.sendEmail(studentEmail, subject, bodyText);
            }else{
                String bodyText = "We are glad to inform you, " + name + " that you have passed with a GPA of " + roundedGPA + ". " +
                        "You may now proceed with the registrations.";
                EmailSender.sendEmail(studentEmail, subject, bodyText);
            }
        }
    }

    private void goBack() {
        this.dispose();
        String role = session.getUser().getRole();

        if (role.equals("Admin")) {
            new CourseAdminPage(session).setVisible(true);
        } else if (role.equals("AcademicOfficer")) {
            new AcademicOfficerPage(session).setVisible(true);
        }
    }


    private void refreshTable() {
        moduleController = new ModuleController();
        loadDataInBackground();
    }
}