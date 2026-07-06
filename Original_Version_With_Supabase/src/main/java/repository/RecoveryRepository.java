package repository;

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;


import classes.Course;
import classes.Enrollement;
import classes.Major;
import classes.RecoveryPlan;
import classes.RecoveryTask;
import classes.Student;

public class RecoveryRepository {

    public static void deleteRecoveryPlanByEnrollment(List<Enrollement> enrollements) {
        String query = "DELETE FROM recovery_plans WHERE enrollmentid = ?";

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement statement = conn.prepareStatement(query)) {
            for (Enrollement enrollment : enrollements) {
                statement.setString(1, enrollment.getEnrollmentID());
                statement.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static RecoveryTask findRecoveryTaskByID(String id) {
        String query = "SELECT * FROM recovery_tasks WHERE taskid = ?";

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, id);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return new RecoveryTask(
                    rs.getString("taskid"),
                    rs.getString("phase"),
                    rs.getString("description")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void updateRecoveryPlan(List<RecoveryPlan> modifiedPlans) {
        String deleteQuery = "DELETE FROM recovery_plans WHERE enrollmentid = ?";
        String insertQuery = "INSERT INTO recovery_plans (taskid, enrollmentid) VALUES (?, ?)";

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery); PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
            // Delete all plans that are NOT in modifiedPlans
            for (RecoveryPlan plan : modifiedPlans) {
                System.out.println(plan.getEnrollmentID());
                deleteStmt.setString(1, plan.getEnrollmentID());
                deleteStmt.executeUpdate();
            }

            // Insert the modified plans
            for (RecoveryPlan plan : modifiedPlans) {
                insertStmt.setString(1, plan.getTaskID());
                insertStmt.setString(2, plan.getEnrollmentID());
                insertStmt.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateRecoveryTask(RecoveryTask modifiedTask) {
        String query = "UPDATE recovery_tasks SET phase = ?, description = ? WHERE taskid = ?";

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, modifiedTask.getPhase());
            statement.setString(2, modifiedTask.getTask());
            statement.setString(3, modifiedTask.getTaskID());
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addRecoveryTask(RecoveryTask newRecoveryTask) {
        String query = "INSERT INTO recovery_tasks (taskid, phase, description) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, newRecoveryTask.getTaskID());
            statement.setString(2, newRecoveryTask.getPhase());
            statement.setString(3, newRecoveryTask.getTask());
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addRecoveryPlan(List<RecoveryPlan> recoveryPlans) {
        String query = "INSERT INTO recovery_plans (taskid, enrollmentid) VALUES (?, ?)";

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement statement = conn.prepareStatement(query)) {
            for (RecoveryPlan plan : recoveryPlans) {
                statement.setString(1, plan.getTaskID());
                statement.setString(2, plan.getEnrollmentID());
                statement.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<RecoveryTask> loadAllRecoveryTask() {
        List<RecoveryTask> recoveryTasks = new ArrayList<>();
        String query = "SELECT * FROM recovery_tasks";

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement statement = conn.prepareStatement(query)) {
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                recoveryTasks.add(new RecoveryTask(
                    rs.getString("taskid"),
                    rs.getString("phase"),
                    rs.getString("description")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return recoveryTasks;
    }

    public static List<RecoveryPlan> loadAllRecoveryPlan() {
        List<RecoveryPlan> recoveryPlans = new ArrayList<>();
        String query = """
            SELECT rp.enrollmentid, rp.taskid,
                   rt.phase, rt.description,
                   sg.grade, sg.studentid, sg.courseid,
                   s.firstname AS student_firstname, s.lastname AS student_lastname, s.major AS student_major, sm.majorid AS student_majorid, s.year AS student_year, s.semester AS student_semester, s.email AS student_email,
                   c.coursename, c.credits, c.semester AS course_semester, c.instructor, c.capacity, c.majorid AS course_majorid
            FROM recovery_plans rp
            JOIN recovery_tasks rt ON rp.taskid = rt.taskid
            JOIN student_grades sg ON rp.enrollmentid = sg.enrollmentid
            JOIN students s ON sg.studentid = s.studentid
            LEFT JOIN majors sm ON s.major = sm.majorname
            JOIN courses c ON sg.courseid = c.courseid
        """;

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement statement = conn.prepareStatement(query)) {
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                RecoveryTask task = new RecoveryTask(
                    rs.getString("taskid"),
                    rs.getString("phase"),
                    rs.getString("description")
                );
                
                String studentMajorName = rs.getString("student_major");
                String studentMajorId = rs.getString("student_majorid");
                Major studentMajor = (studentMajorName != null) ? new Major(studentMajorId, studentMajorName) : null;
                
                Student student = new Student(
                    rs.getString("studentid"),
                    rs.getString("student_firstname"),
                    rs.getString("student_lastname"),
                    studentMajor,
                    rs.getString("student_year"),
                    rs.getString("student_semester"),
                    rs.getString("student_email")
                );
                
                Course course = new Course(
                    rs.getString("courseid"),
                    rs.getString("coursename"),
                    rs.getInt("credits"),
                    rs.getString("course_semester"),
                    rs.getString("instructor"),
                    rs.getInt("capacity"),
                    rs.getString("course_majorid")
                );
                
                Enrollement enrollment = new Enrollement(
                    rs.getString("enrollmentid"),
                    student,
                    course,
                    rs.getString("grade")
                );
                
                recoveryPlans.add(new RecoveryPlan(task, enrollment));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return recoveryPlans;
    }
}
