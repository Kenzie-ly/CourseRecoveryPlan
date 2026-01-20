import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Major
{
    private String name;
    private List<String> courseList = new ArrayList<>();

    public Major(String name){
        assignCourse();
    }

    public String getName()
    {
        return name;
    }

    public List<String> getCourseList() {
        return courseList;
    }

    public static Map<String, List<String>> coursesMap() {
        return Map.ofEntries(
                Map.entry("Computer Science", List.of("Algorithms", "Artificial Intelligence", "Operating Systems", "Database Systems", "Data Structures", "Cybersecurity", "Machine Learning")),
                Map.entry("Engineering", List.of("Software Engineering", "Networking")),
                Map.entry("Mathematics", List.of("Mathematics", "Statistics")),
                Map.entry("Literature", List.of("Literature", "English Composition")),
                Map.entry("Philosophy", List.of("Philosophy")),
                Map.entry("Biology", List.of("Biology", "Chemistry"))
        );
    }

    private void assignCourse(){
        Map<String, List<String>> majorCourses = coursesMap();
        courseList.addAll(majorCourses.getOrDefault(name, List.of("No courses assigned")));
    }
}