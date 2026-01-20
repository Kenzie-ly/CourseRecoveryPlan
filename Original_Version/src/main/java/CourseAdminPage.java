import javax.swing.*;
import java.awt.*;

public class CourseAdminPage extends BaseHomePage {

    public CourseAdminPage(User user){
        super(user);
    }

    @Override
    protected void addRoleButtons(JPanel jpanel) {
        JButton userManageBtn = new JButton("User Management");
        userManageBtn.addActionListener(e -> {
            dispose();
            new UserManagementPage(user).setVisible(true);
        });

        JButton eligiBtn = new JButton("Check Student Eligibility");
        eligiBtn.addActionListener(e -> {
            dispose();
            new StudentEligibilityPage(user).setVisible(true);
        });

        JButton courseRecoBtn = new JButton("Student Course Recovery");
        courseRecoBtn.addActionListener(e -> {
            dispose();
            new CourseRecoveryPage(user).setVisible(true);
        });

        jpanel.add(userManageBtn);
        jpanel.add(eligiBtn);
        jpanel.add(courseRecoBtn);

        jpanel.add(new JLabel());
        jpanel.add(new JLabel());
        jpanel.add(new JLabel());
    }
}

