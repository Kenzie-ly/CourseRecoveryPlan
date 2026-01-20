import java.io.*;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Student {
    private String studentID;
    private String firstName;
    private String lastName;
    private String major;
    private String year;
    private String email;
    // private Map<String, String> Grades;

    public Student(String studentID, String firstName, String lastName, String major, String year, String email){
        this.studentID = studentID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.major = major;
        this.year = year;
        this.email = email;
    }

    // getter
    public String getStudentID() {return studentID;}
    public String getFirstName() {return firstName;}
    public String getLastName() {return lastName;}
    public String getMajor() {return major;}
    public String getYear() {return year;}
    public String getEmail() {return email;}

    // public double getCGPA(){
    //     return new CourseGrade().calStudentCGPA(studentID);
    // }

    public String getCurrentSemester(List<Course> courseList){
        Map<Course, Map<String,Double>> courseAndGradeMap = new CourseGrade(studentID).getCourseAndGradeByStudentID(courseList);
        for (Course course : courseAndGradeMap.keySet()) {
            if (course.getSemester().equals("Semester 2")){
                return "Semester 2";
            }
        }
        return "Semester 1";
    }

    // public double getGPA(String sem, List<Course> courseList, CourseGrade courseGrade){
    //     double totalGrades = 0;
    //     int numberOfCourses = 0;
    //     Map<Course, Map<String,Double>> courseAndGradeMap = courseGrade.getCourseAndGradeBySemester(studentID, sem, courseList);
    //     for (Map<String,Double> gradeMap : courseAndGradeMap.values()) {
    //         totalGrades += gradeMap.values().iterator().next();
    //         numberOfCourses++;
    //     }
    //     return totalGrades/numberOfCourses;
    // }

    // setter
    public void setStudentID(String studentID) {this.studentID = studentID;}
    public void setFirstName(String firstName) {this.firstName = firstName;}
    public void setLastName(String lastName) {this.lastName = lastName;}
    public void setMajor(String major) {this.major = major;}
    public void setYear(String year) {this.year = year;}
    public void setEmail(String email) {this.email = email;}

    public void printInfo() {
        System.out.println("StudentID: " + studentID);
        System.out.println("Full Name: " + firstName + " " + lastName);
        System.out.println("Major: " + major);
        System.out.println("Year: " + year);
        System.out.println("Email: " + email);
        System.out.println("---------------------------------");
    }

    public static List<Student> loadData(String filePath) {
        List<Student> students = new ArrayList<>();
        File file = new File(filePath);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] value = line.split("\t");

                students.add(new Student(value[0], value[1], value[2], value[3], value[4], value[5]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return students;
    }
}
