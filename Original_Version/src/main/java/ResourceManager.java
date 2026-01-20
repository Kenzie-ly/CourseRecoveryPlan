import java.net.URL;

public class ResourceManager {

    public static String getStudentDataPath() {
        return "src/main/data/student_information_test.txt";
    }

    public static String getUserDataPath() {
        return "src/main/data/user_account.txt";
    }

    public static String getRecoveryPlanPath(){
        return "src/main/data/recovery_plans/";
    }

    public static String getCourseDataPath() {
        URL resource = ResourceManager.class.getClassLoader().getResource ("course_information_test.txt");
        return resource.getPath();
    }

    public static String getGradeDataPath(){
        URL resource = ResourceManager.class.getClassLoader().getResource ("student_grade_test.csv");
        return resource.getPath();
    }
    public static String getDefultLogoDataPath(){
        URL resource = ResourceManager.class.getClassLoader().getResource("defaultLogo.png");
        return resource.getPath();
    }   
}
