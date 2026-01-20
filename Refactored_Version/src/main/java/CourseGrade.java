import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CourseGrade {
    private HashMap<Student, String> studentGrades = new HashMap<>();
    private Course course;

    public CourseGrade(Course course, HashMap<Student, String> grades){
        this.course = course;
        this.studentGrades = grades;
    }

    public Course getCourse(){
        return course;
    }

    public List<Student> getPassedStudents(){
        List<Student> passedStudents = new ArrayList<>();
        for (Map.Entry<Student, String> entry : studentGrades.entrySet()) {
            Student currentStudent = entry.getKey();
            String grade = entry.getValue();

            // You need logic here to decide what string counts as passing
            if (grade.matches("[ABC][+-]?")) {
                passedStudents.add(currentStudent);
            }
        }

        return passedStudents;
    }

    public List<Student> getFailedStudents(){
        List<Student> faildStudents = new ArrayList<>();
        for (Map.Entry<Student, String> entry : studentGrades.entrySet()) {
            Student currentStudent = entry.getKey();
            String grade = entry.getValue();

            // You need logic here to decide what string counts as passing
            if (grade.matches("[DEF][+-]?")) {
                faildStudents.add(currentStudent);
            }
        }

        return faildStudents;
    }
    
}
