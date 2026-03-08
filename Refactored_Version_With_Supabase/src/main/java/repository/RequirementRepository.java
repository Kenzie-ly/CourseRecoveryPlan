package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import classes.*;

public class RequirementRepository {

    public static Requirement findRequirement(Major major, String year, String sem) {
        String query = "SELECT r.requirementid, r.year, r.semester, r.requiredcoursecount, r.requiredcredits " +
                       "FROM requirements r " +
                       "JOIN requirement_mapping rm ON r.requirementid = rm.requirementid " +
                       "WHERE rm.majorid = ? AND r.year = ? AND r.semester = ?";

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, major.getMajorID());
            statement.setString(2, year);
            statement.setString(3, sem);

            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                Requirement requirement = new Requirement(
                    rs.getString("requirementid"),
                    rs.getString("year"),
                    rs.getString("semester"),
                    rs.getInt("requiredcoursecount"),
                    rs.getInt("requiredcredits")
                );
                return requirement;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Requirement> findRequirementByYearandSem(String year, String sem) {
        List<Requirement> requirements = new ArrayList<>();
        String query = "SELECT requirementid, year, semester, requiredcoursecount, requiredcredits " +
                       "FROM requirements WHERE year = ? AND semester = ?";

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, year);
            statement.setString(2, sem);

            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                requirements.add(new Requirement(
                    rs.getString("requirementid"),
                    rs.getString("year"),
                    rs.getString("semester"),
                    rs.getInt("requiredcoursecount"),
                    rs.getInt("requiredcredits")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return requirements;
    }
}