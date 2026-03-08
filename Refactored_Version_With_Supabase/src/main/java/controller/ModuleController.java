package controller;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import classes.*;
import repository.*;

public class ModuleController {
    private final List<Course> allCourses = CourseRepository.loadAllCourses();
    private static final Map<String, Double> points = new HashMap<>();

    public ModuleController(){
        initGradePoints();
    }

    private static void initGradePoints() {
        points.put("A",  4.0);
        points.put("A-", 3.7);
        points.put("B+", 3.3);
        points.put("B",  3.0);
        points.put("B-", 2.7);
        points.put("C+", 2.3);
        points.put("C",  2.0);
        points.put("C-", 2.0);
        points.put("D",  1.0);
        points.put("D-", 1.0);
        points.put("F",  0.0);
    }

    /**
     * Exposed as static so callers (e.g. StudentEligibilityPage) can use
     * the grade map directly without going through a controller instance.
     */
    public static double getGradePoint(String grade) {
        return points.getOrDefault(grade, 0.0);
    }

    public List<Student> getAllEnrolledStudents(){
        return EnrollmentRepository.getEnrolledStudents();
    }

    public List<Enrollement> getEnrollementBySemOfStudent(Student s, String sem){
        return EnrollmentRepository.getStudentEnrollmentsBySem(s, sem);        
    }

    public List<Course> getAllCoursesTakenByFailedStudents(){
        return EnrollmentRepository.getCoursesPendingRecoveryPlan();
    }

    // --- Original versions (kept for backward compatibility with other callers) ---

    public boolean canProgressToNextLevel(Student student) {
        List<Enrollement> enrollements = EnrollmentRepository.getStudentEnrollments(student);
        return canProgressToNextLevel(student, enrollements);
    }

    public double calcStudentCGPA(Student student){
        List<Enrollement> enrollements = EnrollmentRepository.getStudentEnrollments(student);
        return calcStudentCGPA(student, enrollements);
    }

    // --- Optimized overloads that accept pre-fetched enrollments (avoid extra DB hit) ---

    public boolean canProgressToNextLevel(Student student, List<Enrollement> enrollements) {
        Requirement req = RequirementRepository.findRequirement(
                student.getMajor(), student.getYear(), student.getSem());
        if (req == null) return false;

        int failedCourseCount = Math.abs(req.getRequiredCourseCount() - enrollements.size());
        for (Enrollement enrollement : enrollements) {
            if (enrollement.getGrade().matches("[DEF][+-]?")) {
                failedCourseCount++;
            }
        }
        return failedCourseCount <= 3;
    }

    public double calcStudentCGPA(Student student, List<Enrollement> enrollements) {
        Requirement req = RequirementRepository.findRequirement(
                student.getMajor(), student.getYear(), student.getSem());

        double grades = 0;
        for (Enrollement enrollement : enrollements) {
            grades += points.getOrDefault(enrollement.getGrade(), 0.0) * enrollement.getCourse().getCredits();
        }
        
        if (student.getSem().equalsIgnoreCase("Semester 2")) {
            Requirement secondRequirement = RequirementRepository.findRequirement(
                student.getMajor(), student.getYear(), "Semester 1");
            return grades/(req.getRequiredCredits() + secondRequirement.getRequiredCredits());
        }
        return grades/req.getRequiredCredits();
    }

    public double calcStudentGPA(Student student, String sem){
        double grades = 0;
        for(var enrollement : EnrollmentRepository.getStudentEnrollmentsBySem(student, sem)){
            grades = grades + (points.getOrDefault(enrollement.getGrade(), 0.0) * enrollement.getCourse().getCredits());
        }
        Requirement req = RequirementRepository.findRequirement(
                student.getMajor(), student.getYear(), student.getSem());
        
        return grades / req.getRequiredCredits();
    }

    public List<Enrollement> getPassedEnrollmentBasedOnCourse(Course course){
        List<Enrollement> enrollements = EnrollmentRepository.findGradeBasedOnCourse(course);
        if(enrollements != null){
            return enrollements.stream()
                .filter(enrollement -> enrollement.getGrade().matches("[DEF][+-]?"))
                .collect(Collectors.toList());
        }
        return null;
    }

    public List<Enrollement> getFailedEnrollmentBasedOnCourse(Course course){
        List<Enrollement> enrollements = EnrollmentRepository.findGradeBasedOnCourse(course);
        if(enrollements == null || enrollements.isEmpty()){
            return new ArrayList<>();
        }

        return enrollements.stream()
            .filter(e -> e.getGrade() != null && e.getGrade().matches("[DEF][+-]?"))
            .collect(Collectors.toList());
    }

    public List<RecoveryPlan> createRecoveryPlan(String courseID, String taskID){
        List<Enrollement> failedEnrollements = getFailedEnrollmentBasedOnCourse(CourseRepository.findCoursesByID(courseID));
        RecoveryTask selectedRecoveryTask = RecoveryRepository.findRecoveryTaskByID(taskID);

        if (failedEnrollements != null && !failedEnrollements.isEmpty()){
            List<RecoveryPlan> recoveryPlans = new ArrayList<>();
            for (var enrollement: failedEnrollements){
                recoveryPlans.add(new RecoveryPlan(selectedRecoveryTask, enrollement));
            }
            try{
                RecoveryRepository.addRecoveryPlan(recoveryPlans);
                return recoveryPlans;
            }catch(Exception e){
                System.out.println(e);
            }
        }else{
            System.out.println("Empty");
        }
        return null;
    }

    public List<Course> getCourseOfRecoveryPlans() {
        List<RecoveryPlan> allRecoveryPlans = RecoveryRepository.loadAllRecoveryPlan();

        if (allRecoveryPlans == null || allRecoveryPlans.isEmpty()) {
            System.out.println("No recovery plans found.");
            return new ArrayList<>();
        }

        Set<Course> coursesInPlans = allRecoveryPlans.stream()
                .map(plan -> {
                    if (plan.getEnrollement() == null || plan.getEnrollement().getCourse() == null) {
                        System.out.println("Invalid recovery plan: " + plan);
                        return null;
                    }
                    return plan.getEnrollement().getCourse();
                })
                .filter(course -> course != null)
                .collect(Collectors.toSet());

        if (coursesInPlans.isEmpty()) {
            System.out.println("No courses found in recovery plans.");
            return new ArrayList<>();
        }

        return allCourses.stream()
                .filter(coursesInPlans::contains)
                .collect(Collectors.toList());
    }

    public List<RecoveryPlan> getRecoveryPlansByCourse(Course course){
        return RecoveryRepository.loadAllRecoveryPlan().stream()
                .filter(plan -> plan.getEnrollement().getCourse().equals(course))
                .collect(Collectors.toList());
    }

    public boolean updateRecoveryPlan(List<RecoveryTask> recoveryTasks, Course course){
        try {
            List<RecoveryPlan> recoveryPlans = new ArrayList<>();
            List<Enrollement> enrollements = this.getFailedEnrollmentBasedOnCourse(course);
            for(var task: recoveryTasks){
                for(var enrollement: enrollements){
                    recoveryPlans.add(new RecoveryPlan(task, enrollement));
                }
            }
            RecoveryRepository.updateRecoveryPlan(recoveryPlans);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;   
    }

    public boolean deleteRecoveryPlanUsingEnrollments(List<Enrollement> enrollements){
        try {
            RecoveryRepository.deleteRecoveryPlanByEnrollment(enrollements);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;   
    }

    public RecoveryTask createRecoveryTask(String taskID, String phase, String description){
        RecoveryTask recoveryTask = new RecoveryTask(taskID, phase, description);
        try{
            RecoveryRepository.addRecoveryTask(recoveryTask);
            return recoveryTask;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public List<RecoveryTask> getAllRecoveryTask(){
        try {
            return RecoveryRepository.loadAllRecoveryTask();
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }   
    }

    public boolean updateRecoveryTask(RecoveryTask updatedRecoveryTask){
        try {
            RecoveryRepository.updateRecoveryTask(updatedRecoveryTask);
            return true;
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }   
    }
}