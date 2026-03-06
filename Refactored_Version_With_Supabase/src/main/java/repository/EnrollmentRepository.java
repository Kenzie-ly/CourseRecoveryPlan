package repository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import classes.*;

public class EnrollmentRepository {

    public static List<Course> getAllCoursesTakenByFailedStudents(){
        Set<Course> uniqueCourse = new HashSet<>();
        String findString = "SELECT * FROM student_grades WHERE grade ~ '^[DEF][+-]$'";

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement statement = conn.prepareStatement(findString)){
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Course course = CourseRepository.findCoursesByID(rs.getString("courseid"));
                uniqueCourse.add(course);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return new ArrayList<>(uniqueCourse);
    }

    public static List<Student> getEnrolledStudents() {
        System.out.print("Hello...., is it working?");
        List<Student> students = new ArrayList<>();
        String query = """
            SELECT DISTINCT s.studentid, s.firstname, s.lastname, 
                            s.major, s.year, s.semester, s.email
            FROM students s
            INNER JOIN student_grades sg ON s.studentid = sg.studentid
        """;

        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement statement = conn.prepareStatement(query)) {

            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Student student = new Student(
                    rs.getString("studentid"),
                    rs.getString("firstname"),
                    rs.getString("lastname"),
                    CourseRepository.findMajorByName(rs.getString("major")),
                    rs.getString("year"),
                    rs.getString("semester"),
                    rs.getString("email")
                );
                students.add(student);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return students;
    }

    public static List<Course> findCoursesTakenByStudents(Student student){
        List<Course> courses = new ArrayList<>();
        String findString = "SELECT * FROM studen_grades WHERE studentid = ?";
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement statement = conn.prepareStatement(findString)) {
            statement.setString(1, student.getStudentID());
            ResultSet rs = statement.executeQuery(); 
            while (rs.next()) {
                courses.add(CourseRepository.findCoursesByID(rs.getString("courseid")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return courses;
    }

    public static List<Enrollement> getStudentEnrollments(Student student){
        List<Enrollement> enrollements = new ArrayList<>();
        String findString = "SELECT * FROM student_grades WHERE studentid = ?";
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement statement = conn.prepareStatement(findString)) {
            statement.setString(1, student.getStudentID());
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                    enrollements.add(new Enrollement(rs.getString("enrollmentid"), student, CourseRepository.findCoursesByID(rs.getString("courseid")), rs.getString("grade")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return enrollements;
    }

    public static List<Enrollement> getStudentEnrollmentsBySem(Student student,String semester) {
        List<Enrollement> enrollments = new ArrayList<>();
        String query = "SELECT * FROM student_grades WHERE semester = ? AND student = ?";

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, semester);
            statement.setString(2,student.getStudentID());
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Enrollement enrollment = new Enrollement(
                    rs.getString("enrollmentid"),
                    student,
                    CourseRepository.findCoursesByID(rs.getString("courseid")),
                    rs.getString("grade")
                );
                enrollments.add(enrollment);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return enrollments;
    }

    public static List<Enrollement> findGradeBasedOnCourse(Course course) {
        List<Enrollement> enrollments = new ArrayList<>();
        String query = "SELECT * FROM student_grades WHERE courseid = ?";

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, course.getCourseID());
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Enrollement enrollment = new Enrollement(
                    rs.getString("enrollmentid"),
                    StudentRepository.findStudentByStudentID(rs.getString("studentid")),
                    CourseRepository.findCoursesByID(rs.getString("courseid")),
                    rs.getString("grade")
                );
                enrollments.add(enrollment);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return enrollments;
    }

    public static Enrollement findEnrollmentByID(String id) {
        String query = "SELECT * FROM student_grades WHERE enrollmentid = ?";

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, id);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return new Enrollement(
                    rs.getString("enrollmentid"),
                    StudentRepository.findStudentByStudentID(rs.getString("studentid")),
                    CourseRepository.findCoursesByID(rs.getString("courseid")),
                    rs.getString("grade")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
