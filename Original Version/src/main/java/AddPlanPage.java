import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

public class AddPlanPage extends JFrame {
    private static final int MAX_TASKS = 12;
    private final Student student;

    private final DefaultListModel<RecoveryTask> listModel = new DefaultListModel<>();
    private final JList<RecoveryTask> taskList = new JList<>(listModel);

    private final String[] TABLE_COLS = {"Week", "Task", "Component", "Deadline", "Completed", "Grade"};

    private final DefaultTableModel tableModel =
            new DefaultTableModel(TABLE_COLS, 0)
            {
                public Class<?> getColumnClass(int col) {return col == 4 ? Boolean.class : String.class;}

                public boolean isCellEditable(int row, int col) {return col == 4 || col == 5;}  // completed + grade editable
            };
    private final JTable progressTable = new JTable(tableModel);

    private JTextField weekField, taskField, deadlineField;
    private JComboBox<String> componentBox;

    public AddPlanPage(Student student) {
        this.student = student;
        setTitle("Add Recovery Plan - "+student.getStudentID());
        setSize(750, 550);
        setLocationRelativeTo(null);
        initUI();
        loadExistingPlan();
    }
    private void initUI() {
        JPanel main = new JPanel(new BorderLayout(8,8));
        main.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));

        JPanel form = new JPanel(new GridLayout(4,2,6,6)); // the INPUT FORM
        weekField = new JTextField();
        taskField = new JTextField();
        deadlineField = new JTextField();
        componentBox = new JComboBox<>(new String[]{"Exam","Assignment"});

        form.add(new JLabel("Week (eg: Week 1 or Week 1-2):")); form.add(weekField);
        form.add(new JLabel("Task:")); form.add(taskField);
        form.add(new JLabel("Component:")); form.add(componentBox);
        form.add(new JLabel("Deadline (YYYY-MM-DD):")); form.add(deadlineField);
        main.add(form, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane(); // the TAB PANEL
        tabs.add("Tasks", new JScrollPane(taskList));
        tabs.add("Progress", new JScrollPane(progressTable));
        main.add(tabs, BorderLayout.CENTER);

        JButton addBtn = new JButton("Add Task"); // moore BUTTONS
        JButton removeBtn = new JButton("Remove");
        JButton clearBtn = new JButton("Clear");
        JButton saveBtn = new JButton("Save Plan");
        JButton saveProgressBtn = new JButton("Save Progress");

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT,8,8));
        btns.add(addBtn);
        btns.add(removeBtn);
        btns.add(clearBtn);
        btns.add(saveBtn);
        btns.add(saveProgressBtn);

        main.add(btns, BorderLayout.SOUTH);

        add(main);

        addBtn.addActionListener(e -> addTask()); // action lsteners
        removeBtn.addActionListener(e -> removeTask());
        clearBtn.addActionListener(e -> clearTasks());
        saveBtn.addActionListener(e -> savePlan());
        saveProgressBtn.addActionListener(e -> saveProgress());
    }
    private void addTask() {
        if (listModel.size() >= MAX_TASKS) {
            JOptionPane.showMessageDialog(this, "Max 12 tasks allowed");
            return;
        }
        String w = weekField.getText().trim();
        String t = taskField.getText().trim();
        String d = deadlineField.getText().trim();
        String c = (String) componentBox.getSelectedItem();
        //chceking if it is empty or not
        if (w.isEmpty() || t.isEmpty() || d.isEmpty()) {JOptionPane.showMessageDialog(this, "Week, Task, and Deadline cannot be empty.");return;
        }
        // checking if it starts with week or not
        if (!w.startsWith("Week")) {
            JOptionPane.showMessageDialog(this,
                    "Week must start with: Week (example: Week 1)");
            return;
        }//checking if date exceeds
        if (d.length() != 10 || d.charAt(4) != '-' || d.charAt(7) != '-') {
            JOptionPane.showMessageDialog(this,
                    "Date must be in format YYYY-MM-DD.");
            return;
        }// if it parses it adds task
        RecoveryTask rt = new RecoveryTask(w, t, c, d, "", false, "");
        listModel.addElement(rt);
        weekField.setText("");
        taskField.setText("");
        deadlineField.setText("");
        rebuildTable();
    }

    private void removeTask()
    {
        int index = taskList.getSelectedIndex();
        if (index >= 0)
        {
            listModel.remove(index);
            rebuildTable();
        }
    }
    private void clearTasks()
    {
        if (!listModel.isEmpty())
        {
            listModel.clear();
            rebuildTable();
        }
    }
    private void savePlan() {
        try
        {
            RecoveryPlan plan = new RecoveryPlan(student.getStudentID());
            for (int i = 0; i < listModel.size(); i++)
                plan.addTask(listModel.get(i));

            RecoveryPlanIO.savePlan(plan);

            JOptionPane.showMessageDialog(this,"Plan saved.");
        }
        catch (Exception ex)
        {
            JOptionPane.showMessageDialog(this,"Error: " + ex.getMessage());
        }
    }
    private void rebuildTable() {
        tableModel.setRowCount(0);

        for (int i = 0; i < listModel.size(); i++) {
            RecoveryTask t = listModel.get(i);

            tableModel.addRow(new Object[]{t.getWeek(), t.getTask(), t.getComponent(), t.getDeadline(), t.isCompleted(), t.getGrade()});
        }
    }
    private void loadExistingPlan()
    {
        try
        {RecoveryPlan plan = RecoveryPlanIO.loadPlan(student.getStudentID());
            for (RecoveryTask t : plan.getTasks()) listModel.addElement(t);

            rebuildTable();
        }
        catch (Exception ignored) {}
    }
    private void saveProgress()
    {
        if (progressTable.isEditing())
        {
            progressTable.getCellEditor().stopCellEditing();
            for (int r = 0; r < tableModel.getRowCount(); r++)
            {
                String week = (String) tableModel.getValueAt(r, 0);
                String task = (String) tableModel.getValueAt(r, 1);
                String comp = (String) tableModel.getValueAt(r, 2);
                String deadline = (String) tableModel.getValueAt(r, 3);
                boolean completed = (Boolean) tableModel.getValueAt(r, 4);
                String grade = (String) tableModel.getValueAt(r, 5);

                RecoveryTask old = listModel.get(r);

                RecoveryTask updated = new RecoveryTask(week, task, comp, deadline, old.getCourseID(), completed, grade);
                listModel.set(r, updated);
            }
            savePlan(); // reuses save logic
        }
    }
}
