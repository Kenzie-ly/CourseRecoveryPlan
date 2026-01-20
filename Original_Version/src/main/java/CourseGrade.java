import java.io.*;
import java.nio.file.*;
import java.util.*;

public class CourseGrade
{
    private Map<String, Integer> creditHour = new HashMap<>();
    private List<StudentGradeRecord> studentGradeRecord = new ArrayList<>(); 
    private static final Map<String, Double> gradePoints = initGradePoints();
    private static final List<Course> courseList = Course.loadData(ResourceManager.getCourseDataPath());
    private String studentID;

    CourseGrade()
    {
        getCreditHour();
        getStudentGrade();
    }

    CourseGrade(String sID)
    {
        getCreditHour();
        getStudentGrade();
        this.studentID = sID;
    }

    public Map<Course,Map<String, Double>> getCourseAndGradeByStudentID(List<Course> courseList){
        Map<String,Map<String, Double>> courseAndGradeMap = new HashMap<>();
        for (StudentGradeRecord gradeRecord : studentGradeRecord) {
            if(gradeRecord.getstudentID().equals(this.studentID)){
                courseAndGradeMap.put(
                    gradeRecord.getCourseID(),
                    new HashMap<>(Map.of(gradeRecord.getGrade(),gradePoints.get(gradeRecord.getGrade())))
                );
            }
        }

        Map<Course,Map<String, Double>> courseRecordAndGradeMap = new HashMap<>();
        for (Course course : courseList) {
            if(courseAndGradeMap.containsKey(course.getCourseID())){
                courseRecordAndGradeMap.put(course, courseAndGradeMap.get(course.getCourseID()));
            }
        }
        
        return courseRecordAndGradeMap;
    }

    public Map<Course,Map<String,Double>> getCourseAndGradeBySemester(String sem, List<Course> courseList){
        Map<Course,Map<String,Double>> courseAndGradeMap = this.getCourseAndGradeByStudentID(courseList);

        courseAndGradeMap.entrySet().removeIf(entry ->
                !entry.getKey().getSemester().equals(sem)
        );

        return courseAndGradeMap;
    }

    private void getCreditHour()
    {
        List<Course> courseList = Course.loadData(ResourceManager.getCourseDataPath());
        creditHour.clear();
        for(Course c : courseList)
        {
            creditHour.put(c.getCourseID(), c.getCredits());
        }
    }

    public double calcGPA(String sem, List<Course> courseList){
        double totalGrades = 0;
        int numberOfCourses = 0;
        Map<Course, Map<String,Double>> courseAndGradeMap = this.getCourseAndGradeBySemester(sem, courseList);
        for (Map<String,Double> gradeMap : courseAndGradeMap.values()) {
            totalGrades += gradeMap.values().iterator().next();
            numberOfCourses++;
        }
        return totalGrades/numberOfCourses;
    }

    public Double calStudentCGPA()
    {
        // Validate input
        if (studentID == null || studentID.trim().isEmpty()) {
            System.err.println("Error: Invalid student ID");
            return 0.0;
        }

        int totalCreditHour = 0;
        double totalGradePoint = 0.0;
        for(StudentGradeRecord student : studentGradeRecord)
        {
            // Skip if not the student we're looking for
            if(!student.studentID.trim().equals(studentID)) continue;

            String courseID = student.courseID.trim();
            Integer credits = creditHour.get(courseID);

            if (credits == null) {
                System.err.println("Warning: No credit hours found for course " + courseID);
                continue;
            }

            String gradeLetter = student.grade.trim().toUpperCase();
            Double gradePoint = convertGradeToPoint(gradeLetter);

            if (gradePoint == null) {
                System.err.println("Warning: Invalid Grade '" + student.grade +
                        "' for student " + studentID + ".");
                continue;
            }

            double tPoint =  gradePoint * credits;
            totalCreditHour += credits;
            totalGradePoint += tPoint;

        }
        if ( totalCreditHour == 0 )
        {
            System.out.println("No valid grades found for student: " + studentID);
            return 0.0;
        }
        else
        {
            return totalGradePoint / totalCreditHour;
        }
    }


    public boolean canProgressToNextLevel() {
        int failedCourseCount = 0;

        for(StudentGradeRecord student : studentGradeRecord) {
            if(!student.studentID.trim().equals(studentID)) continue;

            String gradeLetter = student.grade.trim().toUpperCase();
            Double gradePoint = convertGradeToPoint(gradeLetter);

            if (gradePoint == null) continue;

            if (gradePoint < 2.0) {
                failedCourseCount++;
            }
        }

        // Can progress if 3 or fewer failed courses
        return failedCourseCount <= 3;
    }

    public void getStudentGrade()
    {
        studentGradeRecord.clear();
        File file = new File(ResourceManager.getGradeDataPath());

        try(BufferedReader reader = new BufferedReader(new FileReader(file)))
        {;
            // skip header
            reader.readLine();

            String line;
            while((line = reader.readLine()) != null)
            {
                String[] info = line.split(",");
                if (info.length < 3)
                {
                    System.err.println("Skipping Incomplete record: " + line);
                    continue;
                }
                // add the infos into StudentGradeRecord Object while looping | edit this for including ASSIgnment and EXAm
                studentGradeRecord.add(new StudentGradeRecord(info[0], info[1], info[2], true));
            }

        }
        catch (IOException e)
        {
            System.out.println("Invalid file path");
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            System.out.println("Array Index out of Bounds");
        }

    }
    //this is to detect which component th student failed either exam or assignment
    public List<String> getFailedComponentForStudent() {
        List<String> failedComponents = new ArrayList<>();

        for (StudentGradeRecord record : studentGradeRecord) {
            if (!record.getstudentID().equals(studentID)) continue;
            String grade = record.getGrade().trim().toUpperCase();
            String courseID = record.getCourseID();
            // F means Exam failed
            if (grade.equals("F")) {
                failedComponents.add("Exam failed|" + courseID);
            }
            // D or C means Assignment failed
            if (grade.equals("D") || grade.equals("C-")) {
                failedComponents.add("Assignment failed|" + courseID);
            }
        }
        return failedComponents;
    }

    private static Map<String, Double> initGradePoints() {
        Map<String, Double> points = new HashMap<>();
        points.put("A", 4.0);
        points.put("A-", 3.7);
        points.put("B+", 3.3);
        points.put("B", 3.0);
        points.put("B-", 2.7);
        points.put("C+", 2.3);
        points.put("C", 2.0);
        points.put("D", 1.0);
        points.put("F", 0.0);
        return points;
    }

    private Double convertGradeToPoint(String grade) {
        if (grade == null) {
            return null;
        }
        return gradePoints.get(grade.trim().toUpperCase());
    }

    private static class StudentGradeRecord{
        String studentID;
        String courseID;
        String grade;

        StudentGradeRecord(String sID, String cID, String grade, Boolean status)
        {
            this.studentID = sID;
            this.courseID = cID;
            this.grade = grade;
        }

        String getstudentID(){
            return studentID;
        }

        String getGrade(){return grade;}
        String getCourseID(){return courseID;}
    }

    public boolean hasGrades() {
        for (StudentGradeRecord student : studentGradeRecord) {
            if (student.studentID.trim().equals(studentID)) {
                return true;
            }
        }
        return false;
    }
}