import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserRepository {

    public static User findUserByUsername(String username) {
        try (BufferedReader reader = new BufferedReader(new FileReader(ResourceManager.getUserDataPath()))) {
            reader.readLine();//for skipping the header
            String line;
            while ((line = reader.readLine()) != null){
                String[] userData = line.split("\t");
                if (userData[0].equals(username)){
                    User user = new User(
                    userData[0], userData[1], 
                    userData[2], userData[3], 
                    userData[4], userData[5], 
                    new Date(Long.parseLong(userData[6].trim())), new Date(Long.parseLong(userData[7].trim())));
                    
                    return user;
                }
                
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    public static Student findStudentByStudentID(String studentID) {
        try (BufferedReader reader = new BufferedReader(new FileReader(ResourceManager.getStudentDataPath()))) {
            reader.readLine();//for skipping the header
            String line;
            while ((line = reader.readLine()) != null){
                String[] studentData = line.split("\t");
                if (studentData[0].equals(studentID)){
                    return new Student(studentData[0], studentData[1], studentData[2], studentData[3], studentData[4], studentData[5]);
                }
                
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    public static List<Student> loadAllStudents() {
        List<Student> students = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(ResourceManager.getStudentDataPath()))) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] value = line.split("\t");

                students.add(new Student(value[0], value[1], value[2], value[3], value[4], value[5]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return students;
    }

    public static Course findCoursesByID(String courseID) {
        Course course = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(ResourceManager.getCourseDataPath()))) {
            reader.readLine(); // skip header
            String line;
            while ((line = reader.readLine()) != null) {
                String[] value = line.split("\t");
                if(value[0].equalsIgnoreCase(courseID)){
                    course = new Course(value[0], value[1], Integer.parseInt(value[2]), value[3], value[4], Integer.parseInt(value[5]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return course;
    }

    public static CourseGrade findGradeBasedOnCourse(Course course){
        CourseGrade grade = null;
        HashMap<Student, String> grades = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(ResourceManager.getGradeDataPath()))) {
            reader.readLine(); // skip header
            String line;
            while ((line = reader.readLine()) != null) {
                String[] value = line.split("\t");
                if (course.getCourseID().equals(value[1])){
                    grades.put(findStudentByStudentID(value[0]), value[2]);
                    grade = new CourseGrade(course, grades);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return grade;
    }

    public static void updateUser(User modifiedUser) {
        File originalFile = new File("users.txt");
        File tempFile = new File("users_temp.txt");

        try (
            BufferedReader reader = new BufferedReader(new FileReader(originalFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split("\t");
                
                // CHECK: Is this the user we are updating?
                if (data[0].equals(modifiedUser.getUsername())) {
                    
                    // YES: Write the NEW data instead of the old line
                    writer.write(formatUserToString(modifiedUser));
                    
                } else {
                    
                    // NO: Just copy the existing line exactly as is
                    writer.write(line);
                }
                writer.newLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // SWAP: Delete old file, rename temp file to "users.txt"
        originalFile.delete();
        tempFile.renameTo(originalFile);
    }

    private static String formatUserToString(User user){
        return user.getUsername() + "\t" +
           user.getPassword() + "\t" +
           user.getFirstName() + "\t" +
           user.getLastName() + "\t" +
           user.getEmail() + "\t" +
           user.getRole() + "\t" +
           user.getLastLoginFormatted() + "\t" +
           user.getLastLogoutFormatted();
    }
}
