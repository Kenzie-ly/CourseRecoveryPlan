package classes;
import javax.swing.*;
import controller.LoginController;


public class CourseAdminPage extends BaseHomePage {

    public CourseAdminPage(LoginController session){
        super(session);
    }

    @Override
    protected void addRoleButtons(JPanel jpanel) {
        JButton userManageBtn = new JButton("User Management");
        userManageBtn.addActionListener(e -> {
            dispose();
            new UserManagementPage(session).setVisible(true);
        });

        JButton eligiBtn = new JButton("Check Student Eligibility");
        eligiBtn.addActionListener(e -> {
            dispose();
            new StudentEligibilityPage(session).setVisible(true);
        });

        JButton courseRecoBtn = new JButton("Student Course Recovery");
        courseRecoBtn.addActionListener(e -> {
            dispose();
            new CourseRecoveryPage(session).setVisible(true);
        });

        jpanel.add(userManageBtn);
        jpanel.add(eligiBtn);
        jpanel.add(courseRecoBtn);

        jpanel.add(new JLabel());
        jpanel.add(new JLabel());
        jpanel.add(new JLabel());
    }
}

