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
        String loadString = """
            SELECT s.studentid, s.firstname, s.lastname, s.major, m.majorid, s.year, s.semester, s.email
            FROM students s
            LEFT JOIN majors m ON s.major = m.majorname
        """;
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement statement = conn.prepareStatement(loadString)) {
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String majorName = rs.getString("major");
                String majorId = rs.getString("majorid");
                Major major = (majorName != null) ? new Major(majorId, majorName) : null;
                students.add(
                    new Student(
                        rs.getString("studentid"), 
                        rs.getString("firstname"), 
                        rs.getString("lastname"), 
                        major, 
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
        String findString = """
            SELECT s.studentid, s.firstname, s.lastname, s.major, m.majorid, s.year, s.semester, s.email
            FROM students s
            LEFT JOIN majors m ON s.major = m.majorname
            WHERE s.studentid = ?
        """;
        try(Connection conn = DatabaseManager.getConnection(); PreparedStatement statement = conn.prepareStatement(findString)){
            statement.setString(1, studentID);

            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                String majorName = rs.getString("major");
                String majorId = rs.getString("majorid");
                Major major = (majorName != null) ? new Major(majorId, majorName) : null;
                Student student = new Student(
                    rs.getString("studentid"), 
                    rs.getString("firstname"), 
                    rs.getString("lastname"), 
                    major, 
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
        String deleteString = "DELETE FROM students WHERE studentid = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement statement = conn.prepareStatement(deleteString)) {
            statement.setString(1, student.getStudentID());
            statement.executeUpdate();
        }
    }

    public static void updateStudent(Student student) throws Exception {
        String updateString = """
                    UPDATE students 
                    SET firstname = ?, lastname = ?, major = ?, year = ?, email = ?, semester = ?
                    WHERE studentid = ?
                """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement statement = conn.prepareStatement(updateString)) {
            statement.setString(1, student.getFirstName());
            statement.setString(2, student.getLastName());
            statement.setString(3, student.getMajor() != null ? student.getMajor().getMajorName() : null);
            statement.setString(4, student.getYear());
            statement.setString(5, student.getEmail());
            statement.setString(6, student.getSem());
            statement.setString(7, student.getStudentID());
            statement.executeUpdate();
        }
    }

    public static void addNewStudent(Student student) throws Exception {
        String insertString = """
                INSERT INTO students (StudentID, FirstName, LastName, Major, Year, Semester, Email)
                VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement statement = conn.prepareStatement(insertString)) {
            statement.setString(1, student.getStudentID());
            statement.setString(2, student.getFirstName());
            statement.setString(3, student.getLastName());
            statement.setString(4, student.getMajor() != null ? student.getMajor().getMajorName() : null);
            statement.setString(5, student.getYear());
            statement.setString(6, student.getSem());
            statement.setString(7, student.getEmail());
            statement.executeUpdate();
        }
    }
}
