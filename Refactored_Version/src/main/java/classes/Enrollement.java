package classes;

public class Enrollement {
    private Student student;
    private String studentGrades;
    private Course course;
    private String enrollementID;

    public Enrollement(String enrollementID, Student student, Course course,String grades){
        this.enrollementID = enrollementID;
        this.course = course;
        this.student = student;
        this.studentGrades = grades;
    }

    public Course getCourse(){
        return course;
    }

    public String getEnrollmentID(){
        return enrollementID;
    }

    public Student getStudent(){
        return student;
    }

    public String getGrade(){
        return studentGrades;
    }
    

    // public List<Student> getFailedStudents(){
    //     List<Student> faildStudents = new ArrayList<>();
    //     for (Map.Entry<Student, String> entry : studentGrades.entrySet()) {
    //         Student currentStudent = entry.getKey();
    //         String grade = entry.getValue();

    //         // You need logic here to decide what string counts as passing
    //         if (grade.matches("[DEF][+-]?")) {
    //             faildStudents.add(currentStudent);
    //         }
    //     }

    //     return faildStudents;
    // }
    
}
