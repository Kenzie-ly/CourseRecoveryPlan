package classes;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import controller.LoginController;

import java.awt.*;
import java.util.Random;

public class ForgetPassPage extends JFrame{
    private String username;
    private String email;
    private String verifCode;
    private LoginController session;

    public ForgetPassPage(String username, String email, String verifCode, LoginController session) {
        this.username = username;
        this.email = email;
        this.verifCode = verifCode;
        this.session = session;
        initFrame();
    }

    private void initFrame() {
        setTitle("Forgot Password");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(new EmptyBorder(30, 50, 30, 50));

        JLabel title_label = new JLabel("Forgot Password", SwingConstants.CENTER);
        title_label.setBorder(new EmptyBorder(0, 0, 20, 0));

        // Panel for the verification code field
        JPanel infoPanel = new JPanel(new GridLayout(5, 1, 10, 10));

        JLabel verifCodeLabel = new JLabel("Please enter the Verification Code that was sent to your email:");
        JTextField verifCodeField = new JTextField(10);  // Text field for verification code
        JLabel newPassLabel = new JLabel("New Password:");
        JPasswordField newPassField = new JPasswordField(10);  // Pass field for new password
        JLabel confirmPassLabel = new JLabel("Confirm Password:");
        JPasswordField confirmPassField = new JPasswordField(10);  // Pass field for confirm password

        // Adding components for verification code, new password, and confirm password to the panel
        infoPanel.add(verifCodeLabel);
        infoPanel.add(verifCodeField);
        infoPanel.add(newPassLabel);
        infoPanel.add(newPassField);
        infoPanel.add(confirmPassLabel);
        infoPanel.add(confirmPassField);

        // Button panel with Confirm, Back, and Resend Code buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton confirmBtn = new JButton("Confirm");
        JButton backBtn = new JButton("Back");
        JButton resendCodeBtn = new JButton("Resend Code");

        buttonPanel.add(confirmBtn);
        buttonPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        buttonPanel.add(backBtn);
        buttonPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        buttonPanel.add(resendCodeBtn);

        // Adding components to the main panel
        mainPanel.add(title_label, BorderLayout.NORTH);
        mainPanel.add(infoPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add the main panel to the frame
        add(mainPanel);

        confirmBtn.addActionListener(e -> {
            String verifCodeInput = verifCodeField.getText();
            String newPass = new String(newPassField.getPassword());
            String conNewPass = new String(confirmPassField.getPassword());
            boolean changed = false;
            if (this.verifCode.equals(verifCodeInput)) {
                if (newPass.length() >= 8) {
                    if (newPass.matches("[a-zA-Z0-9]+")) {
                        if (newPass.equals(conNewPass) && session.checkEmail(this.username, conNewPass, this.email)) {
                            JOptionPane.showMessageDialog(ForgetPassPage.this, "Your password for the user '" + this.username +"' has been changed!");
                            System.out.println("New Password: " + newPass + "\nConfirmation New Pass: ");
                            changed = true;
                        } else {
                            JOptionPane.showMessageDialog(ForgetPassPage.this, "Your New Password and Confirmation New Password does not match!");
                            System.out.println("Your New Password and Confirmation New Password does not match!");
                        }
                    } else {
                        JOptionPane.showMessageDialog(ForgetPassPage.this, "Password must not have any special characters");
                    }
                } else {
                    JOptionPane.showMessageDialog(ForgetPassPage.this, "Password must be at least 8 characters long");
                }
            } else {
                JOptionPane.showMessageDialog(ForgetPassPage.this, "Please enter the correct verification code!");
                System.out.println("Please enter the correct verification code!");
            }
            System.out.println("New Password: " + newPass + "\nConfirmation New Password: " + conNewPass + "\nGenerated Verification Code: " + this.verifCode +
                    "\nEntered Verification Code: " + verifCodeInput);
            if (changed) {
                this.dispose();
                new LoginPage().setVisible(true);
            }
        });
        resendCodeBtn.addActionListener(e -> resendCode());
        backBtn.addActionListener(e -> back());
    }

    private void resendCode() {
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        this.verifCode = String.valueOf(code);
        String subject = "New Forget Password Verification Code";
        String bodyText = "This is your new verification code: " + this.verifCode;
        EmailSender.sendEmail(this.email, subject, bodyText);
    }

    private void back() {
        this.dispose();
        new LoginPage().setVisible(true);
    }
}