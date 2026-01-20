import java.io.*;
import java.net.URL;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class Main {
    public static void main(String[] args){
        List<Student> students = Student.loadData(ResourceManager.getStudentDataPath());
        List<Course> courses = Course.loadData(ResourceManager.getCourseDataPath());

        //for (Student val : students) {val.printInfo();}
        //for (Course val : courses) {val.printInfo();}

        //Student student02 = students.get(1);
        //System.out.println(student02.getLastName());

        //loginlogout();

        SwingUtilities.invokeLater(() -> {
            LoginPage loginPage = new LoginPage();
            loginPage.setVisible(true);
        });
    }

    private static void mainPage(){
        JFrame frame = new JFrame("Student Management");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);


        frame.setVisible(true);
    }

    private static void loginlogout(){
        Scanner scanner = new Scanner(System.in);
        List<User> users = User.loadData("user_account.txt");

        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        User UserWhoLog = null;

        // goes through all users to find same creds
        for (User user : users) {
            if (user.login(username, password)) {
                UserWhoLog = user;
                break;
            }
        }

        if (UserWhoLog == null) {
            System.out.println("invalid");
        } else {
            System.out.println("it works");
            System.out.println(UserWhoLog.getUsername() + " (" + UserWhoLog.getRole() + ")");
            System.out.println("last login: " + UserWhoLog.getLastLogin());

            scanner.nextLine();

            UserWhoLog.logout();
            System.out.println("last logout: " + UserWhoLog.getLastLogout());

            scanner.close();
        }
    }
}
