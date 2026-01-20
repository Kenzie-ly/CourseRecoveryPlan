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
}
