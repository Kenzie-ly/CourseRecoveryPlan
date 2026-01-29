package classes;

import javax.swing.*;
import java.awt.*;

import controller.*;

public abstract class BaseHomePage extends JFrame {
    protected LoginController session;
    public BaseHomePage(LoginController session){
        this.session = session;
        initFrame();
    }

    private void initFrame(){
        setTitle(session.getUser().getRole() + " Dashboard");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        // stores logout time if session force terminate
        addWindowListener(new java.awt.event.WindowAdapter(){
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                session.logout();
                dispose();
                System.exit(0);
            }
        });
        setSize(900, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(20, 20));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Welcome, " + session.getUser().getFirstName() + " " + session.getUser().getLastName(), SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 26));

        headerPanel.add(title, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        // Grid for buttons
        JPanel buttonGrid = new JPanel(new GridLayout(2, 3, 20, 20));
        buttonGrid.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        addRoleButtons(buttonGrid);
        add(buttonGrid, BorderLayout.CENTER);

        // Footer
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 20));

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> {
            session.logout();
            dispose();
            new LoginPage().setVisible(true);
        });
        footerPanel.add(logoutBtn, BorderLayout.EAST);
        add(footerPanel, BorderLayout.SOUTH);
    }

    protected abstract void addRoleButtons(JPanel jpanel);
}
