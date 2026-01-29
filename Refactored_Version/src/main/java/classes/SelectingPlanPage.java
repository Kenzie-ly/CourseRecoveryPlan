package classes;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import controller.*;

public class SelectingPlanPage extends JFrame {
    private ModuleController moduleController;
    private JPanel topPanel;
    private JButton targetButton;
    private JPanel studentPanel;
    private Course course;
    private JPanel mainPanel = new JPanel(new BorderLayout());

    public SelectingPlanPage(ModuleController moduleController, Course course){
        this.moduleController = moduleController;
        this.course = course;
        this.setTitle("Editing A Plan");
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setSize(700, 400);
        this.setLocationRelativeTo(null);
        editTaskUI();
    }

    public SelectingPlanPage(ModuleController moduleController, JPanel topPanel, JButton targetButton){
        this.moduleController = moduleController;
        this.topPanel = topPanel;
        this.targetButton = targetButton; // Correct assignment
        this.setTitle("Select A Course");
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setSize(700, 400);
        this.setLocationRelativeTo(null);
        selectTaskUI();
    }

    public SelectingPlanPage(ModuleController moduleController, JPanel topPanel, JButton targetButton, JPanel studentPanel){
        this.moduleController = moduleController;
        this.topPanel = topPanel;
        this.targetButton = targetButton;
        this.studentPanel = studentPanel;
        this.setTitle("Select A Course");
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setSize(800, 500);
        this.setLocationRelativeTo(null);
        createRecoveryPlanUI();
    }

    private void editTaskUI(){
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS)); // Use vertical layout for tasks
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        List<RecoveryTask> recoveryTasks = moduleController.getAllRecoveryTask();
        List<RecoveryPlan> existingRecoveryPlans = moduleController.getRecoveryPlansByCourse(course);

        for(var task: recoveryTasks){
            JPanel rowPanel = new JPanel();
            rowPanel.setLayout(new BoxLayout(rowPanel, BoxLayout.X_AXIS));

            JLabel taskLabel = new JLabel(task.getTaskID()+ " - " +task.getTask());
            JCheckBox checkBox = new JCheckBox();

            if(existingRecoveryPlans.stream().anyMatch(p -> p.getTaskID().equals(task.getTaskID()))){
                checkBox.setSelected(true);
            }

            rowPanel.add(taskLabel);
            rowPanel.add(Box.createHorizontalGlue());
            rowPanel.add(checkBox);

             // 3. Add Padding and Rounded-look border
            rowPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 70, 70), 1, true),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
            ));

            mainPanel.add(rowPanel);
            mainPanel.add(Box.createVerticalStrut(10)); 
        }

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            
        });

        mainPanel.add(saveButton);
        this.add(mainPanel);
    }

    private void createRecoveryPlanUI(){
        List<Course> allCourses = moduleController.getAllCourses();
        JPanel createRecoveryPlanPanel = new JPanel(new GridLayout(0, 4, 10, 10));
    
        for (var course : allCourses){
            JPanel wrapper = new JPanel();
            JButton button = new JButton(course.getCourseID() + " " + course.getCourseName());
            button.setPreferredSize(new Dimension(170, 100));
            wrapper.add(button);
            createRecoveryPlanPanel.add(wrapper);
            button.addActionListener(e -> selectCourse(e, course));
        }

        JScrollPane scrollPanel = new JScrollPane(createRecoveryPlanPanel);
        scrollPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        mainPanel.add(scrollPanel, BorderLayout.CENTER);
        this.add(mainPanel);
    }

    private void selectCourse(ActionEvent e, Course course){
        int index = -1;
        Component[] components = topPanel.getComponents();
        for (int i = 0; i < components.length; i++) {
            if (components[i] == targetButton) { // Fixed reference to targetButton
                index = i;
                break;
            }
        }

        if (index != -1) {
            topPanel.remove(targetButton); // Fixed reference to targetButton

            JLabel targetLabel = new JLabel(course.getCourseID());
            topPanel.add(targetLabel, index); // Add it back at the same spot
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
                    studentPanel.add(l);
                }
            }

            // 4. Refresh the UI
            studentPanel.revalidate();
            studentPanel.repaint();
            topPanel.revalidate();
            topPanel.repaint();
            this.dispose();
        }
    }

    private void selectTaskUI(){
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS)); // Use vertical layout for tasks
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        List<String> taskList = showListOfTasks();

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            // Save the selected tasks and close the window
            saveSelectedTask(e, taskList);
            this.dispose();
        });

        mainPanel.add(saveButton);
        this.add(mainPanel);
    }

    private void saveSelectedTask(ActionEvent e, List<String> taskList){
        int index = -1;
        Component[] components = topPanel.getComponents();
        
        for (int i = 0; i < components.length; i++) {
            if (components[i] == this.targetButton) { 
                index = i;
                break;
            }
        }

        if(index != -1){
            String task = String.join(", ", taskList);
            JLabel taskLabel = new JLabel(task); 

            topPanel.remove(targetButton);
            topPanel.add(taskLabel, index);

            topPanel.revalidate();
            topPanel.repaint();
            this.dispose();
        }
    }


    private List<String> showListOfTasks(){
        List<RecoveryTask> recoveryTasks = moduleController.getAllRecoveryTask();
        List<String> taskList = new ArrayList<>();

        for (var task : recoveryTasks) {
            JPanel taskRow = new JPanel();
            taskRow.setLayout(new BoxLayout(taskRow, BoxLayout.X_AXIS));
            
            // 3. Add Padding and Rounded-look border
            taskRow.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 70, 70), 1, true),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
            ));

            // 4. Task Label (Left Aligned)
            JLabel taskLabel = new JLabel(task.getTaskID() + " - " + task.getTask());
            taskRow.add(taskLabel);
            taskRow.add(Box.createHorizontalGlue()); 


            JCheckBox selectButton = new JCheckBox();            
            selectButton.addActionListener(e -> taskList.add(task.getTaskID()));

            taskRow.add(selectButton);
            
            // 7. Add Row to Main and add a gap between bars
            mainPanel.add(taskRow);
            mainPanel.add(Box.createVerticalStrut(10)); 
        }
        return taskList;
    }
}
