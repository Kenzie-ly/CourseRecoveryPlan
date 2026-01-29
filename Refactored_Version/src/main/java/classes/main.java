package classes;
import javax.swing.SwingUtilities;

public class main {
    public static void main() {
        System.out.println("Hello World");

        // Scanner input = new Scanner(System.in);
        // String name = input.next();
        // String password = input.next();
        // User tempUser1 = new User("admin1", "abc123");
        // User tempUser = new User(name, password);

        SwingUtilities.invokeLater(() -> {
            LoginPage loginPage = new LoginPage();
            loginPage.setVisible(true);
        });


        // if (session.authenticateUser(tempUser)){
        //     System.out.print("Success, Session is established");
            
        //     User actualUser = session.getLoggedInUser();
        //     System.out.println("Welcome, " + actualUser.getFirstName());
        //     System.out.println("Your Role: " + actualUser.getRole());

        //     if(actualUser.getRole().equalsIgnoreCase("admin")){
        //         ModuleController moduleController = new ModuleController();


                // for (var course : moduleController.getAllCourses()){
                //     if(course.getCourseID().equals("C119")){
                //         RecoveryTask selectedRecoveryTask = null;
                //         System.out.println("List all recovery task");
                //         for (var task : moduleController.getAllRecoveryTask()){
                //             System.out.println(task.getTaskID() + task.getPhase() + task.getTask());

                //             System.out.println("Do u want to add this task: (y/N)");
                //             Scanner scanner = new Scanner(System.in);
                //             if (scanner.next().equalsIgnoreCase("y")){
                //                 selectedRecoveryTask = task;
                //             }
                //         }

                //         List<Enrollement> failedEnrollList = moduleController.getFailedEnrollmentBasedOnCourse(course);
                //         if (selectedRecoveryTask != null){
                //             if (moduleController.createRecoveryPlan(failedEnrollList, selectedRecoveryTask) != null){
                //                 System.out.println("Sucessfully added a recovery plan for courseID called" + "C119");
                //             }else{
                //                 System.out.println("Unsuccessfull");
                //             }
                //         }
                //     }

                    // List<Enrollement> passedEnrollList = moduleController.getPassedEnrollmentBasedOnCourse(course);
                    
                    // //List all passed students
                    // System.out.println("Course Name: " + "C101");

                    // for (Enrollement enrollement: passedEnrollList){
                    //     Student student = enrollement.getStudent();
                    //     System.out.println(student.getFirstName() + student.getLastName());
                    // }
                // }
                    
        //     }else if (actualUser.getRole().equals("AcademicOfficer")){
        //         ModuleController moduleController = new ModuleController();
        //         moduleController.createRecoveryTask("T04", "Phase1", "Review_All_Modules");

        //         System.out.println("List all recovery task");
        //         for (var task : moduleController.getAllRecoveryTask()){
        //             System.out.print("Do u want to edit the task? (y/N): ");
                    
        //             Scanner scanner = new Scanner(System.in);
        //             if(scanner.next().equals("y")){
        //                 System.out.print(task.getTask() + " Task: ");
        //                 task.setTask(scanner.next());
        //                 if (moduleController.updateRecoveryTask(task)){
        //                     System.out.println("Success");
        //                 }else{
        //                     System.out.println("Unsucessful request");
        //                 }
        //             }
        //         }
        //     }   
        // }else{
        //     System.out.print("Unsucessful, Invalid username or password");
        // }
    }
}
