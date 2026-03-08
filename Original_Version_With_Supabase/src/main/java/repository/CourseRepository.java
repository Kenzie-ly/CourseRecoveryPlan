package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import classes.*;

public class CourseRepository {

    public static Course findCoursesByID(String courseID) {
        Course course = null;

        String findString = "SELECT * FROM courses where courseid = ?";
        try(Connection conn = DatabaseManager.getConnection(); PreparedStatement statement = conn.prepareStatement(findString)){
            statement.setString(1, courseID);

            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                course = new Course(
                    rs.getString("courseid"),
                    rs.getString("coursename"),
                    rs.getInt("credits"),
                    rs.getString("semester"),
                    rs.getString("instructor"),
                    rs.getInt("capacity"),
                    rs.getString("majorid")
                );
            }
        }catch(Exception e){
            System.err.println(e);
        }

        return course;
    }

    public static List<Course> loadAllCourses(){
        List<Course> courses = new ArrayList<>();
        String loadString = "SELECT * FROM courses";

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement statement = conn.prepareStatement(loadString)) {
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                courses.add(
                    new Course(
                        rs.getString("courseid"),
                        rs.getString("coursename"),
                        rs.getInt("credits"),
                        rs.getString("semester"),
                        rs.getString("instructor"),
                        rs.getInt("capacity"),
                        rs.getString("majorid"))    
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return courses;
    }

    public static Major findMajorByID(String id){
        String findMajorString = "SELECT * FROM majors WHERE majorid = ?";
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement statement = conn.prepareStatement(findMajorString)) {
            statement.setString(1, id);
            ResultSet rs = statement.executeQuery();
            if(rs.next()){
                Major major = new Major(rs.getString("majorid"), rs.getString("majorname"));
                return major;
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    public static Major findMajorByName(String name){
        String findMajorString = "SELECT * FROM majors WHERE majorname = ?";
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement statement = conn.prepareStatement(findMajorString)) {
            statement.setString(1, name);
            ResultSet rs = statement.executeQuery();
            if(rs.next()){
                Major major = new Major(rs.getString("majorid"), rs.getString("majorname"));
                return major;
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }
}
