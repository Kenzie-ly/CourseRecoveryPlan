package classes;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.swing.*;

import controller.*;

public class SelectingPlanPage extends JFrame {
    private ModuleController moduleController;
    private JPanel topPanel;
    private JButton targetComponent;
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

    public SelectingPlanPage(ModuleController moduleController, JPanel topPanel, JButton targetComponent){
        this.moduleController = moduleController;
        this.topPanel = topPanel;
        this.targetComponent = targetComponent; // Correct assignment
        this.setTitle("Select A Course");
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setSize(700, 400);
        this.setLocationRelativeTo(null);
        selectTaskUI();
    }

    public SelectingPlanPage(ModuleController moduleController, JPanel topPanel, JButton targetComponent, JPanel studentPanel){
        this.moduleController = moduleController;
        this.topPanel = topPanel;
        this.targetComponent = targetComponent;
        this.studentPanel = studentPanel;
        this.setTitle("Select A Course");
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setSize(800, 500);
        this.setLocationRelativeTo(null);
        selectCourseUI();
    }

    private void editTaskUI(){
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS)); // Use vertical layout for tasks
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        List<RecoveryTask> recoveryTasks = moduleController.getAllRecoveryTask();
        List<RecoveryPlan> existingRecoveryPlans = moduleController.getRecoveryPlansByCourse(course);

        List<RecoveryTask> editedTasks = new ArrayList<>();

        for(var task: recoveryTasks){
            List<RecoveryPlan> newRecoveryPlans = new ArrayList<>();
            JPanel rowPanel = new JPanel();
            rowPanel.setLayout(new BoxLayout(rowPanel, BoxLayout.X_AXIS));

            JLabel taskLabel = new JLabel(task.getTaskID()+ " - " +task.getTask() +"("+ task.getPhase() + ")");
            JCheckBox checkBox = new JCheckBox();

            Optional<RecoveryPlan> matchingPlan = existingRecoveryPlans.stream()
                .filter(p -> p.getTaskID().equals(task.getTaskID()))
                .findFirst();
            
            if(matchingPlan.isPresent()){
                checkBox.setSelected(true);
                editedTasks.add(task);
            }

            checkBox.addItemListener(e -> {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    if(matchingPlan.isPresent()){
                        editedTasks.add(task);
                    }else{
                        editedTasks.add(task);
                    }
                } else {
                    if(matchingPlan.isPresent()){
                        editedTasks.remove(task);
                    }else{
                        editedTasks.remove(task);
                    }
                }
            });

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
            if(checkTask(editedTasks)){
                moduleController.updateRecoveryPlan(editedTasks, course);
                JOptionPane.showMessageDialog(this, "Successfully updated!");
                this.dispose();
            }
        });

        mainPanel.add(saveButton);
        this.add(mainPanel);
    }

    private void selectCourseUI(){
        List<Course> allCourses = moduleController.getAllCoursesTakenByFailedStudents();
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
            if (components[i] == targetComponent) { // Fixed reference to targetComponent
                index = i;
                break;
            }
        }

        if (index != -1) {
            topPanel.remove(targetComponent); // Fixed reference to targetComponent
            List<Enrollement> enrollments = moduleController.getFailedEnrollmentBasedOnCourse(course);
            JLabel targetLabel = new JLabel(course.getCourseID());
            topPanel.add(targetLabel, index); // Add it back at the same spot

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

        List<RecoveryTask> taskList = showListOfTasks();

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            saveSelectedTask(e, taskList);
        });

        mainPanel.add(saveButton);
        this.add(mainPanel);
    }

    private void saveSelectedTask(ActionEvent e, List<RecoveryTask> taskList){
        //check task
        if(!(checkTask(taskList))){
            return;
        }


        int index = -1;
        Component[] components = topPanel.getComponents();
        
        for (int i = 0; i < components.length; i++) {
            if (components[i] == this.targetComponent) { 
                index = i;
                break;
            }
        }

        if(index != -1){
            String task = taskList.stream()
                .map(t -> t.getTaskID())
                .collect(Collectors.joining(", "));

            JLabel taskLabel = new JLabel(task); 

            topPanel.remove(targetComponent);
            topPanel.add(taskLabel, index);

            topPanel.revalidate();
            topPanel.repaint();
            this.dispose();
        }
    }

    private boolean checkTask(List<RecoveryTask> taskList){
        Set<String> phases = taskList.stream()
            .map(RecoveryTask::getPhase)
            .collect(Collectors.toSet());
        if(taskList.isEmpty()){
            this.dispose();
            return false;
        }else if (!(phases.equals(Set.of("Phase1", "Phase2", "Phase3")))){
            JOptionPane.showMessageDialog(this, "Please choose one task for each phase!");
            return false;
        }else if(taskList.size() > 3 && phases.equals(Set.of("Phase1", "Phase2", "Phase3"))){
            JOptionPane.showMessageDialog(this, "Please choose ONLY one task for each phase!");
            return false;
        }
        return true;
    }

    private List<RecoveryTask> showListOfTasks(){
        List<RecoveryTask> recoveryTasks = moduleController.getAllRecoveryTask();
        List<RecoveryTask> selectedTask = new ArrayList<>();

        for (var task : recoveryTasks) {
            JPanel taskRow = new JPanel();
            taskRow.setLayout(new BoxLayout(taskRow, BoxLayout.X_AXIS));
            
            // 3. Add Padding and Rounded-look border
            taskRow.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 70, 70), 1, true),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
            ));

            // 4. Task Label (Left Aligned)
            JLabel taskLabel = new JLabel(task.getTaskID() + " - " + task.getTask() + "(" + task.getPhase() + ")");
            taskRow.add(taskLabel);
            taskRow.add(Box.createHorizontalGlue()); 


            JCheckBox selectButton = new JCheckBox();            
            selectButton.addItemListener(e -> {
                if(e.getStateChange() == ItemEvent.SELECTED){
                    selectedTask.add(task);
                }else{
                    selectedTask.remove(task);
                }
            });

            taskRow.add(selectButton);
            
            // 7. Add Row to Main and add a gap between bars
            mainPanel.add(taskRow);
            mainPanel.add(Box.createVerticalStrut(10)); 
        }
        return selectedTask;
    }
}
