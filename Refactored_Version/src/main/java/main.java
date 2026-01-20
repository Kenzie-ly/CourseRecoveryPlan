import java.util.HashMap;
import java.util.Scanner;

public class main {
    public static void main(String[] args) {
        System.out.println("Hello World");

        Scanner input = new Scanner(System.in);
        String name = input.next();
        String password = input.next();
        User tempUser1 = new User("admin1", "abc123");
        User tempUser = new User(name, password);

        System.out.println(tempUser1.getEmail());
        tempUser1.setEmail("password");
        System.out.println(tempUser1.getEmail());
        System.out.println(tempUser.getEmail());
        

        LoginController session = new LoginController();
        if (session.authenticateUser(tempUser)){
            System.out.print("Success, Session is established");
            
            User actualUser = session.getLoggedInUser();
            System.out.println("Welcome, " + actualUser.getFirstName());
            System.out.println("Your Role: " + actualUser.getRole());

            if(actualUser.getRole().equalsIgnoreCase("admin")){
                EnrollementController enrollment = new EnrollementController();
                CourseGrade courseGrade = enrollment.listPassedStudents("C101");
                Course selectedCourse = courseGrade.getCourse();
                for (Student student: courseGrade.getPassedStudents()){
                    System.out.println(student.getFirstName() + student.getLastName());
                }

            }else if (actualUser.getRole().equals("AcademicOfficer")){

            }


        }else{
            System.out.print("Unsucessful, Invalid username or password");
        }
    }
}
