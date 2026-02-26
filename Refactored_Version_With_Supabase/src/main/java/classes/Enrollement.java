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
}
