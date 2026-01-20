import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StudentEligibilityPage extends JFrame {
    private User user;
    private DefaultTableModel failedModel;
    private DefaultTableModel passModel;
    private JTable jTablePass;
    private JTable jTableFailed;
    private CourseGrade courseGrade;
    List<Student> students = Student.loadData(ResourceManager.getStudentDataPath());

    public StudentEligibilityPage(User user) {
        this.user = user;
        this.initFrame();
        this.loadPassedStudentData();
        this.loadFailedStudentData();
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

        this.failedModel = new DefaultTableModel(new String[]{"Student ID", "Name", "Major", "CGPA"}, 0);
        this.passModel = new DefaultTableModel(new String[]{"Student ID", "Name", "Major", "CGPA"}, 0);
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

        JPanel tablePanel = new JPanel(new CardLayout());
        tablePanel.add(scrollPanePass, "PASS");
        tablePanel.add(scrollPaneFailed, "FAIL");
        CardLayout cl = (CardLayout) (tablePanel.getLayout());

        passStudentBtn.addActionListener(e -> cl.show(tablePanel, "PASS"));
        failStudentBtn.addActionListener(e -> cl.show(tablePanel, "FAIL"));

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

    private void sendEmailNotificationPass() {
        for (int i = 0; i < passModel.getRowCount(); i++) {
            Object studentID = passModel.getValueAt(i, 0);
            Object studentName = passModel.getValueAt(i, 1);
            Object studentCgpa = passModel.getValueAt(i, 3);
            System.out.println(studentID);
            for (Student student : students) {
                try {
                    if (student.getStudentID().equals(studentID)) {
                        String studentEmail = student.getEmail();
                        String subject = "Pass/Fail Notice";
                        String bodyText = "We are glad to inform you, " + studentName + " that you have passed with a CGPA of " + studentCgpa + ". " +
                                "You may now proceed with the registrations.";
                        EmailSender.sendEmail(studentEmail, subject, bodyText);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(StudentEligibilityPage.this, "Failed to send email to students who passed");
                    e.printStackTrace();
                }
            }
        }
        for (int i = 0; i < failedModel.getRowCount(); i++) {
            Object studentID = failedModel.getValueAt(i, 0);
            Object studentName = failedModel.getValueAt(i, 1);
            Object studentCgpa = failedModel.getValueAt(i, 3);
            System.out.println(studentID);
            for (Student student : students) {
                try {
                    if (student.getStudentID().equals(studentID)) {
                        String studentEmail = student.getEmail();
                        String subject = "Pass/Fail Notice";
                        String bodyText = "We are sorry to inform you, " + studentName + " that you have failed with a CGPA of " + studentCgpa;
                        EmailSender.sendEmail(studentEmail, subject, bodyText);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(StudentEligibilityPage.this, "Failed to send email to students who failed");
                    e.printStackTrace();
                }
            }
        }
        JOptionPane.showMessageDialog(StudentEligibilityPage.this, "All emails have been sent successfully!");
    }

    private void goBack() {
        this.dispose();
        String role = user.getRole();

        if (role.equals("Admin")) {
            new CourseAdminPage(user).setVisible(true);
        } else if (role.equals("AcademicOfficer")) {
            new AcademicOfficerPage(user).setVisible(true);
        }
    }

    private void loadPassedStudentData(){
        passModel.setRowCount(0);

        for (Student s : students) {
            this.courseGrade = new CourseGrade(s.getStudentID());
            String id = s.getStudentID();
            String name = s.getFirstName() + " " + s.getLastName();
            String major = s.getMajor();
            
            double CGPA = courseGrade.calStudentCGPA();

            double roundedCGPA = Math.round(CGPA * 100.0) / 100.0;

            //if (courseGrade.canProgressToNextLevel()) {
            //    passModel.addRow(new Object[]{id, name, major, roundedCGPA});
            //}

            if (roundedCGPA >= 2.0 && courseGrade.hasGrades() && courseGrade.canProgressToNextLevel()) {
                passModel.addRow(new Object[]{id, name, major, roundedCGPA});
            }
        }
    }

    private void loadFailedStudentData() {
        // Clear existing rows
        failedModel.setRowCount(0);

        // Add new rows
        for (Student s : students) {
            this.courseGrade = new CourseGrade(s.getStudentID());
            String id = s.getStudentID();
            String name = s.getFirstName() + " " + s.getLastName();
            String major = s.getMajor();
            double CGPA = courseGrade.calStudentCGPA();

            double roundedCGPA = Math.round(CGPA * 100.0) / 100.0;

            //if (roundedCGPA < 2.0 || !courseGrade.canProgressToNextLevel()) {
            //    failedModel.addRow(new Object[]{id, name, major, roundedCGPA});
            //}

            if (courseGrade.hasGrades() && roundedCGPA < 2.0 || !courseGrade.canProgressToNextLevel()) {
                failedModel.addRow(new Object[]{id, name, major, roundedCGPA});
            }
        }
    }

    private void refreshTable() {
        courseGrade = new CourseGrade();

        loadPassedStudentData();
        loadFailedStudentData();
        JOptionPane.showMessageDialog(this, "Table refreshed!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}