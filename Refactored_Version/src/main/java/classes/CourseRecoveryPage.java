package classes;

import javax.swing.*;

import controller.LoginController;
import controller.ModuleController;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class CourseRecoveryPage extends JFrame {
    private LoginController session;
    private JPanel studentPanel;
    private ModuleController moduleController;
    private boolean newRecoveryPlan = false;
    private JPanel mainPanel;
    private JPanel inputPanel;

    public CourseRecoveryPage(LoginController session) { // Load all students first
        this.session = session;
        this.moduleController = new ModuleController();
        this.initFrame();
    }

    public CourseRecoveryPage(LoginController session, boolean newRecoveryPlan) { // Load all students first
        this.session = session;
        this.moduleController = new ModuleController();
        this.newRecoveryPlan = newRecoveryPlan;
        this.initFrame();
    }

    private void createAction(ActionEvent e) {
        Component[] components = inputPanel.getComponents();
        String courseID = null;
        String taskID = null;

        if (components[1] instanceof JLabel && components[3] instanceof JLabel) {
            courseID = ((JLabel) components[1]).getText();
            taskID = ((JLabel) components[3]).getText();
        } else {
            System.err.println("Expected JLabel at indices 1 and 3, but found different components.");
        }

        if(courseID!= null && taskID != null){
            String[] taskIDs = taskID.split(", ");
            
            for(String task:taskIDs){
                moduleController.createRecoveryPlan(courseID, task);
            }

            JOptionPane.showMessageDialog(this, "Sucessfully creating new recovery plans", "", JOptionPane.INFORMATION_MESSAGE);
            goBack();
        }else{
            JOptionPane.showMessageDialog(this, "Unsuccessfuly creating new recovery plans", "", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void createRecoveryPlan(){
        studentPanel.setLayout(new BorderLayout(0,0));

        inputPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.GRAY, 1), BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        inputPanel.setBackground(Color.white);
        JLabel courseLabel, taskLabel, studentLabel;

        courseLabel = new JLabel("Course: ");
        taskLabel = new JLabel("Task: ");
        studentLabel = new JLabel("Student: ");

        JButton selectCourseButton = new JButton("Select course");
        JButton selectTaskButton = new JButton("Select task");
        JPanel listOfStudentsPanel = new JPanel(new GridLayout(0, 5));
         

        inputPanel.add(courseLabel);
        inputPanel.add(selectCourseButton);
        inputPanel.add(taskLabel);
        inputPanel.add(selectTaskButton);
        inputPanel.add(studentLabel);

        studentPanel.add(inputPanel, BorderLayout.NORTH);
        studentPanel.add(listOfStudentsPanel, BorderLayout.CENTER);

        selectCourseButton.addActionListener(e->new SelectingPlanPage(moduleController, inputPanel, selectCourseButton, listOfStudentsPanel).setVisible(true));
        selectTaskButton.addActionListener(e -> new SelectingPlanPage(moduleController, inputPanel, selectTaskButton).setVisible(true));

        JPanel createPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));// search bar
        JButton createBtn = new JButton("Create New Recovery Plan");
        createBtn.setPreferredSize(new Dimension(200, 25));
        createBtn.addActionListener(e -> createAction(e));

        createPanel.add(createBtn);
        mainPanel.add(createPanel, BorderLayout.SOUTH);
    }

    private void showAction(ActionEvent event, Course course){
        //clear panel
        studentPanel.setLayout(new BorderLayout());
        studentPanel.removeAll();

        JPanel topPanel = new JPanel(new GridLayout(0,2, 5,10));
        topPanel.setBackground(Color.WHITE);
        JPanel centerPanel = new JPanel(new GridLayout(0, 5));
        
        studentPanel.add(topPanel, BorderLayout.NORTH);
        studentPanel.add(centerPanel, BorderLayout.CENTER);

        JLabel courseIDLabel = new JLabel("Course ID: ");
        JLabel courseNameLabel = new JLabel("Course Name: ");
        JLabel instructorLabel = new JLabel("Instructor Name");
        JLabel assignedTaskLabel = new JLabel("Assigned tasks: ");

        JLabel courseID = new JLabel(course.getCourseID());
        JLabel courseName = new JLabel(course.getCourseName());
        JLabel instructor = new JLabel(course.getInstructor());
        JPanel buttonWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton assignedTask = new JButton("View Assigned tasks");
        assignedTask.setPreferredSize(new Dimension(200, 25));

        List<Enrollement> enrollments = moduleController.getFailedEnrollmentBasedOnCourse(course);

        for (var enrollment : enrollments) {
            // 2. Create the labels with white texts
            JLabel[] labels = {
                new JLabel(enrollment.getStudent().getStudentID()),
                new JLabel(enrollment.getStudent().getFirstName()),
                new JLabel(enrollment.getStudent().getLastName()),
                new JLabel(enrollment.getStudent().getMajor().getMajorName()),
                new JLabel(enrollment.getGrade())
            };

            for (JLabel l : labels) {
                l.setHorizontalAlignment(JLabel.CENTER);
                l.setFont(new Font("SansSerif", Font.PLAIN, 14));
                centerPanel.add(l);
            }
        }

        buttonWrapper.setOpaque(false);
        buttonWrapper.add(assignedTask);

        topPanel.add(courseIDLabel);
        topPanel.add(courseID);
        topPanel.add(courseNameLabel);
        topPanel.add(courseName);
        topPanel.add(instructorLabel);
        topPanel.add(instructor);
        topPanel.add(assignedTaskLabel);
        topPanel.add(buttonWrapper);

        assignedTask.addActionListener(e -> new SelectingPlanPage(moduleController, course).setVisible(true));

        studentPanel.revalidate();
        studentPanel.repaint();
    }

    private void showRecoveryPlans(){
        List<Course> courses = moduleController.getCourseOfRecoveryPlans();
        studentPanel.setLayout(new GridLayout(courses.size()/4, 4, 10, 10));
        for(var course : courses){
            JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
            JButton button = new JButton(course.getCourseName());
            button.setPreferredSize(new Dimension(180, 100));
            wrapper.add(button);
            studentPanel.add(wrapper);
            button.addActionListener(e -> showAction(e, course));
        }

        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton createButton = new JButton("Create Recovery Plan");
        createButton.setPreferredSize(new Dimension(180, 100));
        wrapper.add(createButton);
        studentPanel.add(wrapper);

        createButton.addActionListener(e -> {
            new CourseRecoveryPage(session, true).setVisible(true);
            this.dispose();
        });

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));// search bar
        JTextField searchField = new JTextField(20);
        JButton searchBtn = new JButton("Search");
        JButton clearBtn = new JButton("Clear");

        searchPanel.add(new JLabel("Search Course: "));
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        searchPanel.add(clearBtn);

        mainPanel.add(searchPanel, BorderLayout.SOUTH);
    }

    private void goBack() //Navigation
    {
        this.dispose();
        String role = session.getUser().getRole();

        if (role.equals("Admin"))
        {
            if(newRecoveryPlan){
                new CourseRecoveryPage(session).setVisible(true);
                this.dispose();
            }else{
                new CourseAdminPage(session).setVisible(true);
                this.dispose();
            }  
        }
        // else if (role.equals("AcademicOfficer"))
        // {
        //     new AcademicOfficerPage(session).setVisible(true);
        // }
    }

    private void initFrame() { // The page layout setup
        this.setTitle("Course Recovery Plan");
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setSize(900, 600);
        this.setLocationRelativeTo(null);

        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel headerPanel = new JPanel(new BorderLayout()); //header: shows the title and back button at the top
        JLabel headerLabel = new JLabel("Course Recovery Management", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(e -> goBack());

        headerPanel.add(backBtn, BorderLayout.WEST);
        headerPanel.add(headerLabel, BorderLayout.CENTER);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        studentPanel = new JPanel(); // Student List
        if(this.newRecoveryPlan){
            headerLabel.setText("Create Recovery Plan");
            createRecoveryPlan();
        }else{
            showRecoveryPlans();
        }

        JScrollPane scrollPane = new JScrollPane(studentPanel);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        this.add(mainPanel);
    }
}
