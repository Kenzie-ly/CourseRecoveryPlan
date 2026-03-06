package repository;

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;


import classes.Enrollement;
import classes.RecoveryPlan;
import classes.RecoveryTask;

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
        String deleteQuery = "DELETE FROM recovery_plans WHERE taskid = ? AND enrollmentid = ?";
        String insertQuery = "INSERT INTO recovery_plans (taskid, enrollmentid) VALUES (?, ?)";

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery); PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
            for (RecoveryPlan plan : modifiedPlans) {
                deleteStmt.setString(1, plan.getTaskID());
                deleteStmt.setString(2, plan.getEnrollmentID());
                deleteStmt.executeUpdate();

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
        String query = "SELECT * FROM recovery_plans";

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement statement = conn.prepareStatement(query)) {
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                recoveryPlans.add(new RecoveryPlan(
                    findRecoveryTaskByID(rs.getString("taskid")),
                    EnrollmentRepository.findEnrollmentByID(rs.getString("enrollmentid"))
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return recoveryPlans;
    }
}
