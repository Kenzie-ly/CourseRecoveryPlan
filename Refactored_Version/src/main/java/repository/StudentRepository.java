package repository;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import classes.*;

public class StudentRepository {

    public static List<Student> loadAllStudents() {
        List<Student> students = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(ResourceManager.getStudentDataPath()))) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] value = line.split("\t");

                students.add(new Student(value[0], value[1], value[2], CourseRepository.findMajorByName(value[3]), value[4], value[5]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return students;
    }

    public static Student findStudentByStudentID(String studentID) {
        try (BufferedReader reader = new BufferedReader(new FileReader(ResourceManager.getStudentDataPath()))) {
            reader.readLine(); // for skipping the header
            String line;
            while ((line = reader.readLine()) != null) {
                // System.out.println("Processing line: " + line); // Log the line for debugging

                if (line.trim().isEmpty()) {
                    System.out.println("Skipping empty line");
                    continue; // Skip empty lines
                }

                String[] studentData = line.split("\t");
                if (studentData.length < 6) {
                    System.out.println("Malformed line: " + line);
                    continue; // Skip malformed lines
                }

                if (studentData[0].equals(studentID)) {
                    return new Student(studentData[0], studentData[1], studentData[2], CourseRepository.findMajorByName(studentData[3]), studentData[4], studentData[5]);
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        return null;
    }


}
