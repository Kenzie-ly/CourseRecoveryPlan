package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import classes.*;

public class StudentRepository {

    public static List<Student> loadAllStudents() {
        List<Student> students = new ArrayList<>();
        String loadString = "Select * FROM students";
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement statement = conn.prepareStatement(loadString)) {
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                students.add(
                    new Student(
                        rs.getString("studentid"), 
                        rs.getString("firstname"), 
                        rs.getString("lastname"), 
                        CourseRepository.findMajorByName(rs.getString("major")), 
                        rs.getString("year"), 
                        rs.getString("semester"), 
                        rs.getString("email")
                    )
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return students;
    }

    public static Student findStudentByStudentID(String studentID) {
        String findString = "SELECT * FROM students where studentID = ?";
        try(Connection conn = DatabaseManager.getConnection(); PreparedStatement statement = conn.prepareStatement(findString)){
            statement.setString(1, studentID);

            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                Student student = new Student(
                    rs.getString("studentid"), 
                    rs.getString("firstname"), 
                    rs.getString("lastname"), 
                    CourseRepository.findMajorByName(rs.getString("major")), 
                    rs.getString("year"), 
                    rs.getString("semester"), 
                    rs.getString("email")
                );
                return student;
            }
        }catch(Exception e){
            System.err.println(e);
        }
        return null;
    }

    public static void deleteStudent(Student student) throws Exception {
        String deleteString = "DELETE FROM courses WHERE studentid = ?";

        Connection conn = DatabaseManager.getConnection();
        PreparedStatement statement = conn.prepareStatement(deleteString);

        statement.setString(1, student.getStudentID());
        statement.executeUpdate();
    }

    public static void updateStudent(Student student) throws Exception {
        String updateString = """
                    UPDATE students SET (
                        studentid = ?,
                        firstname = ?,
                        lastname = ?,
                        major = ?,
                        year = ?,
                        email = ?,
                        semester = ?    
                    )
                """;

        editAllStudentAttributes(student, updateString);
    }
    

    public static void addNewStudent(Student student) throws Exception{
        String insertString = """
                INSERT INTO students (StudentID, FirstName, LastName, Major, Year, Semester, Email)
                VALUES 
                (?, ?, ?, ?, ?, ?, ?)
            """;

        editAllStudentAttributes(student, insertString);
    }

    private static void editAllStudentAttributes(Student student, String sqlString) throws Exception{
        Connection conn = DatabaseManager.getConnection();
        PreparedStatement statement = conn.prepareStatement(sqlString);

        statement.setString(1, student.getStudentID());
        statement.setString(2, student.getFirstName());
        statement.setString(3, student.getLastName());
        statement.setString(3, student.getMajor().getMajorName());
        statement.setString(3, student.getYear());
        statement.setString(3, student.getEmail());
        statement.setString(3, student.getSem());
        statement.executeUpdate();
    }
}
