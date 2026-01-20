import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

public class CourseRecoveryPage extends JFrame {
    private User user;
    private List<Student> students;
    private JPanel studentPanel;
    //using to calc cgpa and filter failed students

    public CourseRecoveryPage(User user) { // Load all students first
        this.user = user;

        List<Student> allStudents = Student.loadData(ResourceManager.getStudentDataPath());
        // Filtering FAILED students
        this.students = allStudents.stream().filter(s ->
                {
                    CourseGrade courseGrade = new CourseGrade(s.getStudentID());
                    double cgpa = courseGrade.calStudentCGPA();
                    double rounded = Math.round(cgpa * 100.0) / 100.0;
                    return (rounded < 2.0 || !courseGrade.canProgressToNextLevel());
                }).toList();
        this.initFrame();
    }
    private void initFrame() { // The page layout setup
        this.setTitle("Course Recovery Plan");
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setSize(900, 600);
        this.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
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
        studentPanel.setLayout(new BoxLayout(studentPanel, BoxLayout.Y_AXIS));

        loadStudentList();

        JScrollPane scrollPane = new JScrollPane(studentPanel);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        this.add(mainPanel);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));// search bar
        JTextField searchField = new JTextField(20);
        JButton searchBtn = new JButton("Search");
        JButton clearBtn = new JButton("Clear");

        searchPanel.add(new JLabel("Search Student: "));
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        searchPanel.add(clearBtn);

        mainPanel.add(searchPanel, BorderLayout.SOUTH);
        //search button
        searchBtn.addActionListener(e ->
        {
            String keyword = searchField.getText().trim().toLowerCase();

            if (keyword.isEmpty())
            {
                JOptionPane.showMessageDialog(this, "Please enter a search keyword.");
                return;
            }// Filtering the students
            List<Student> filtered = students.stream().filter(s -> s.getStudentID().toLowerCase().contains(keyword) || s.getFirstName().toLowerCase().contains(keyword) || s.getLastName().toLowerCase().contains(keyword)).toList();
            updateStudentList(filtered);
        });
        clearBtn.addActionListener(e ->
        {
            searchField.setText("");
            updateStudentList(students);
        });
    }
    private void loadStudentList()
    {
        studentPanel.removeAll();

        for (Student student : students)
        { //studentlistbox
            addStudentBoxToPanel(student);
        }
        refreshStudentPanel();
    }
    private JPanel createStudentBox(Student student) {
        JPanel box = new JPanel(new GridLayout(0, 2, 5, 2));
        box.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.GRAY, 1), BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        box.setBackground(Color.WHITE);

        box.add(new JLabel("Student ID:"));
        box.add(new JLabel(student.getStudentID()));
        box.add(new JLabel("Name:"));
        box.add(new JLabel(student.getFirstName()+" "+student.getLastName()));
        box.add(new JLabel("Major:"));
        box.add(new JLabel(student.getMajor()));
        box.add(new JLabel("Year:"));
        box.add(new JLabel(student.getYear()));
        box.add(new JLabel("Email:"));
        box.add(new JLabel(student.getEmail()));

        List<String> failed = getFailedComponentsFor(student);

        if (!failed.isEmpty())
        {
            JLabel header = new JLabel("Failed Components:");
            header.setFont(header.getFont().deriveFont(Font.BOLD));
            box.add(header);

            StringBuilder sb = new StringBuilder("<html>");
            for (String s : failed)
            {
                sb.append("• ").append(s).append("<br>");
            }
            sb.append("</html>");

            JLabel listLabel = new JLabel(sb.toString());
            listLabel.setForeground(Color.RED);
            box.add(listLabel);
        }
        else
        {
            box.add(new JLabel("Failed Components:"));
            box.add(new JLabel("None"));
        }
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));         //Action Buttonss
        JButton addPlanBtn = new JButton("Add Plan");
        JButton viewPlanBtn = new JButton("Generate PDF");
        JButton sendEmailBtn = new JButton("Send Email");

        addPlanBtn.addActionListener(e -> openAddPlanPage(student)); // Placeholder actions (to be implemented later)
        viewPlanBtn.addActionListener(e -> generateRecoveryPdf(student));
        sendEmailBtn.addActionListener(e -> {
            String folderPath = "src/main/data";
            sendEmailRecoveryPlan(folderPath, student);
        });

        buttonPanel.add(addPlanBtn);
        buttonPanel.add(viewPlanBtn);
        buttonPanel.add(sendEmailBtn);

        box.add(new JLabel("Actions:"));         // Add the button panel across both grid columns
        box.add(buttonPanel);

        return box;
    }

    // Send Course Recovery Plan to Student via Email
    private void sendEmailRecoveryPlan(String folderPath, Student student) {
        File folder = new File(folderPath);
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".pdf"));
        String studentID = student.getStudentID();

        if (files != null) {
            for (File file : files) {
                String fileName = file.getName();
                // Get the Student ID from the file name
                String studentIDFile = fileName.split("_")[1].split("\\.")[0];

                if (studentIDFile.equals(studentID)) {
                    if (student != null) {
                        try {
                            String studentEmail = student.getEmail();
                            String subject = "Course Recovery Plan";
                            String bodyText = "Attached is your Course Recovery Plan";
                            EmailSender.sendEmail(studentEmail, subject, bodyText, file.getAbsolutePath());
                            JOptionPane.showMessageDialog(CourseRecoveryPage.this, "Email sent successfully!");
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(CourseRecoveryPage.this, "Failed to send email!");
                            e.printStackTrace();
                        }
                    } else {
                        JOptionPane.showMessageDialog(CourseRecoveryPage.this, "Student with the Student ID '" + studentID + "' not found!");
                    }
                    break;
                } else {
                    JOptionPane.showMessageDialog(CourseRecoveryPage.this, "Recovery Plan File for the student with the Student ID '" + studentID + "' have not been generated.");
                    System.out.println("Recovery Plan File for the student with the Student ID " + studentID + " have not been generated.");
                }
            }
        } else {
            JOptionPane.showMessageDialog(CourseRecoveryPage.this, "No files found in the folder.");
            System.out.println("No files found in the folder: " + folderPath);
        }
    }

    private void goBack() //Navigation
    {
        this.dispose();
        String role = user.getRole();

        if (role.equals("Admin"))
        {
            new CourseAdminPage(user).setVisible(true);
        }
        else if (role.equals("AcademicOfficer"))
        {
            new AcademicOfficerPage(user).setVisible(true);
        }
    }
    private void openAddPlanPage(Student student)  //try1
    {
        new AddPlanPage(student).setVisible(true);
    }

    private void generateRecoveryPdf(Student student)
    {
        try
        {
            RecoveryPlan plan = RecoveryPlanIO.loadPlan(student.getStudentID());
            System.out.println("Generate Recovery PDF: " + student.getStudentID());
            List<RecoveryTask> tasks = plan.getTasks();

            if (tasks.isEmpty()) //checkin if its left emty
            {
                JOptionPane.showMessageDialog(this, "No recovery plan found for this student."+"\nPlease add relevant tasks.", "No plan was found.", JOptionPane.WARNING_MESSAGE
                );
            return;
            }

            String logoPath = ResourceManager.getDefultLogoDataPath();

            String outputPath = "src/main/data/RecoveryPlan_" + student.getStudentID() + ".pdf"; //outputting the pdf file to data folder.
            PdfFile pdfFile = new CourseRecoveryReport(tasks);//fixed by adding tasks
            DocumentService documentService = new DocumentService(pdfFile,true);
            documentService.generateRecoveryPlan(outputPath, student, logoPath);
            JOptionPane.showMessageDialog(this, "PDF Generated:\n" + outputPath, "Success", JOptionPane.INFORMATION_MESSAGE);
        }
        catch (Exception ex) //try catch error handle, if the file is open or file cannot be accessed the following errors popup
        {
            if (ex.getMessage() != null && ex.getMessage().contains("Cannot access the file"))
            {
                JOptionPane.showMessageDialog(this,
                        "The PDF is currently open.\nPlease close it and try again.",
                        "File In Use",
                        JOptionPane.ERROR_MESSAGE);
            }
            else
            {// handling Other unknown errors
                JOptionPane.showMessageDialog(this,
                        "Error generating PDF:\n" + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
            ex.printStackTrace(); //print the stack trace of an exception or error to a specified output,
        } //typically the standard error stream
    }

    private void updateStudentList(List<Student> studentList)   //helper method
    {
        studentPanel.removeAll(); //clears all present student list display

        for (Student student : studentList) { // creates new student box for every each student
            addStudentBoxToPanel(student);
        }
        refreshStudentPanel();//basically redraws the panel recalculates layout
    } // reloading the screen
    private List<String> getFailedComponentsFor(Student student) {
        CourseGrade cg = new CourseGrade(student.getStudentID());
        return cg.getFailedComponentForStudent();
    }//helper method for revalidanting and repainting
    private void refreshStudentPanel() {
        studentPanel.revalidate();
        studentPanel.repaint();
    }//studentbox (cause i dont wanna repeate it 3 times)
    private void addStudentBoxToPanel(Student student) {
        JPanel box = createStudentBox(student);
        studentPanel.add(box);
        studentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
    }
}
