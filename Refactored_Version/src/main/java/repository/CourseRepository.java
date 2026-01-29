package repository;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import classes.*;

public class CourseRepository {

    public static List<Course> findCoursesByMajor(Major major){
        List<Course> courses = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(ResourceManager.getCourseDataPath()))) {
            reader.readLine(); // skip header
            String line;
            while ((line = reader.readLine()) != null) {
                String[] value = line.split("\t");
                if(value[6].equalsIgnoreCase(major.getMajorID())){
                    Course course = new Course(value[0], value[1], Integer.parseInt(value[2]), value[3], value[4], Integer.parseInt(value[5]),value[6]);
                    courses.add(course);
                }
            }
        } catch (IOException e) {
            System.err.println(e);
        }
        return courses;
    }

    public static Course findCoursesByID(String courseID) {
        Course course = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(ResourceManager.getCourseDataPath()))) {
            reader.readLine(); // skip header
            String line;
            while ((line = reader.readLine()) != null) {
                String[] value = line.split("\t");
                if(value[0].equalsIgnoreCase(courseID)){
                    course = new Course(value[0], value[1], Integer.parseInt(value[2]), value[3], value[4], Integer.parseInt(value[5]),value[6]);
                }
            }
        } catch (IOException e) {
            System.err.println(e);
        }
        return course;
    }

    public static List<Course> loadAllCourses(){
        List<Course> courses = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(ResourceManager.getCourseDataPath()))) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] value = line.split("\t");
                courses.add(new Course(value[0], value[1], Integer.parseInt(value[2]),value[3],value[4],Integer.parseInt(value[5]),value[6]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return courses;
    }

    public static Major findMajorByID(String id){
        try (BufferedReader reader = new BufferedReader(new FileReader(ResourceManager.getMajorDataPath()))) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] value = line.split("\t");
                if(value[0].equalsIgnoreCase(id)){
                    Major major = new Major(value[0], value[1]);
                    return major;
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    public static Major findMajorByName(String name){
        try (BufferedReader reader = new BufferedReader(new FileReader(ResourceManager.getMajorDataPath()))) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] value = line.split("\t");
                if(value[1].equalsIgnoreCase(name)){
                    Major major = new Major(value[0], value[1]);
                    return major;
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }
}
