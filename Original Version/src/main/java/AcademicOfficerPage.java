import javax.swing.*;
import java.awt.*;

public class AcademicOfficerPage extends BaseHomePage {

    public AcademicOfficerPage(User user){
        super(user);
    }

    @Override
    protected void addRoleButtons(JPanel jpanel) {
        JButton aprBtn = new JButton("Performance Report");
        aprBtn.addActionListener(e -> {
            dispose();
            new PerformanceReportPage(user).setVisible(true);
        });

        JButton studentManageBtn = new JButton("Student Management");
        studentManageBtn.addActionListener(e -> {
            dispose();
            new StudentManagementPage(user).setVisible(true);
        });

        JButton eligiBtn = new JButton("Student Eligibility");
        eligiBtn.addActionListener(e -> {
            dispose();
            new StudentEligibilityPage(user).setVisible(true);
        });

        jpanel.add(aprBtn);
        jpanel.add(studentManageBtn);
        jpanel.add(eligiBtn);

        jpanel.add(new JLabel());
        jpanel.add(new JLabel());
        jpanel.add(new JLabel());
    }
}