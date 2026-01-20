import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class Course {
    private String courseID;
    private String courseName;
    private int credits;
    private String semester;
    private String instructor;
    private int capacity;

    public Course(String courseID, String courseName, int credits, String semester, String instructor, int capacity) {
        this.courseID = courseID;
        this.courseName = courseName;
        this.credits = credits;
        this.semester = semester;
        this.instructor = instructor;
        this.capacity = capacity;
    }

    // getter
    public String getCourseID() {
        return courseID;
    }

    public String getCourseName() {
        return courseName;
    }

    public int getCredits() {
        return credits;
    }

    public String getSemester() {
        return semester;
    }

    public String getInstructor() {
        return instructor;
    }

    public int getCapacity() {
        return capacity;
    }

    // setter
    public void setCourseID(String courseID) {
        this.courseID = courseID;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void printInfo() {
        System.out.println("CourseID: " + courseID);
        System.out.println("Name: " + courseName);
        System.out.println("Credits: " + credits);
        System.out.println("Semester: " + semester);
        System.out.println("Instructor: " + instructor);
        System.out.println("Capacity: " + capacity);
        System.out.println("---------------------------------");
    }

}
