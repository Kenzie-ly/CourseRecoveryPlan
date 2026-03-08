package repository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import classes.*;

public class EnrollmentRepository {

    public static List<Course> getAllCoursesTakenByFailedStudents(){
        Set<Course> uniqueCourse = new HashSet<>();
        String findString = "SELECT * FROM student_grades WHERE grade ~ '^[DEF][+-]?$'";

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
    
    public static List<Course> getCoursesPendingRecoveryPlan() {
        List<Course> courses = new ArrayList<>();
        String query = """
            SELECT DISTINCT c.courseid, c.coursename, c.credits,
                            c.semester, c.instructor, c.capacity, c.majorid
            FROM courses c
            JOIN student_grades sg ON c.courseid = sg.courseid
            WHERE sg.grade ~ '^[DEF][+-]?$'
            AND sg.enrollmentid NOT IN (
                SELECT enrollmentid FROM recovery_plans
            )
        """;

        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                courses.add(new Course(
                    rs.getString("courseid"),
                    rs.getString("coursename"),
                    rs.getInt("credits"),
                    rs.getString("semester"),
                    rs.getString("instructor"),
                    rs.getInt("capacity"),
                    rs.getString("majorid")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return courses;
    }

    public static List<Student> getEnrolledStudents() {
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

    public static Map<String, List<Enrollement>> getAllEnrollmentsGroupedByStudent() {
        Map<String, List<Enrollement>> grouped = new HashMap<>();

        String query = """
            SELECT sg.enrollmentid, sg.studentid, sg.grade,
                   c.courseid, c.coursename, c.credits,
                   c.semester, c.instructor, c.capacity, c.majorid
            FROM student_grades sg
            JOIN courses c ON sg.courseid = c.courseid
        """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String studentId = rs.getString("studentid");
                Course course = new Course(
                    rs.getString("courseid"),
                    rs.getString("coursename"),
                    rs.getInt("credits"),
                    rs.getString("semester"),
                    rs.getString("instructor"),
                    rs.getInt("capacity"),
                    rs.getString("majorid")
                );
                Enrollement enrollment = new Enrollement(
                    rs.getString("enrollmentid"),
                    null, // student ref not needed — we look up by studentId key
                    course,
                    rs.getString("grade")
                );
                grouped.computeIfAbsent(studentId, k -> new ArrayList<>()).add(enrollment);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return grouped;
    }

    public static List<Course> findCoursesTakenByStudents(Student student){
        List<Course> courses = new ArrayList<>();
        String findString = "SELECT * FROM student_grades WHERE studentid = ?"; // note: original had typo "studen_grades"
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
        String query = """
            SELECT sg.enrollmentid, sg.grade, c.courseid, c.coursename, c.credits, c.semester, c.instructor, c.capacity, c.majorid 
            from student_grades sg INNER JOIN courses c ON sg.courseid = c.courseid where sg.studentid = ? and c.semester = ?""";

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1,student.getStudentID());
            statement.setString(2,semester);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Enrollement enrollment = new Enrollement(
                    rs.getString("enrollmentid"),
                    student,
                    new Course(rs.getString("courseid"), rs.getString("coursename"), rs.getInt("credits"), rs.getString("semester"), rs.getString("instructor"), rs.getInt("capacity"), rs.getString("majorid")),
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