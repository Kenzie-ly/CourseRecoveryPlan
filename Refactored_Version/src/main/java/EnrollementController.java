import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EnrollementController {
    private List<Student> studentsList = UserRepository.loadAllStudents(); 

    public CourseGrade listPassedStudents(String courseID){
        Course course;
        if ((course = UserRepository.findCoursesByID(courseID)) != null){
            CourseGrade courseGrade = UserRepository.findGradeBasedOnCourse(course);
            return courseGrade;
        }
        return null;
    }
}
