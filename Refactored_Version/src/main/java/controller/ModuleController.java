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
    private final List<Course> courses = CourseRepository.loadAllCourses();
    private List<RecoveryPlan> allRecoveryPlans = new ArrayList<>();
    private static Map<String, Double> points = new HashMap<>();

    public ModuleController(){
        initGradePoints();
    }
    
    public List<Course> getAllCourses(){
        return courses;
    }

    private static Map<String, Double> initGradePoints() {
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

    public List<Student> getAllEnrolledStudents(){
        try {
            return EnrollmentRepository.getEnrolledStudents();
        } catch (Exception e) {
            System.out.println(e + "hello");
        }
        return null;
    }

    public boolean canProgressToNextLevel(Student student) {
        int failedCourseCount = 0;
        List<Enrollement> enrollements =  EnrollmentRepository.getStudentEnrollments(student);

        for(var course:CourseRepository.findCoursesByMajor(student.getMajor())){
            for(var enrollement: enrollements){
                if(!(enrollement.getCourse().equals(course)) || enrollement.getGrade().matches("[DEF][+-]?")){
                    failedCourseCount += 1;
                }
            }
        }
        // Can progress if 3 or fewer failed courses
        return failedCourseCount <= 3;
    }

    // public List<Enrollement> getFailedEnrollements(){
    //     return EnrollmentRepository.getEnrollementsBasedOnPerformance(Status.FAILED);
    // }

    // public List<Enrollement> getPassEnrollements(){
    //     return EnrollmentRepository.getEnrollementsBasedOnPerformance(Status.PASS);
    // }


    public double calcStudentCGPA(Student student){
        double grades = 0;
        for(var enrollement :EnrollmentRepository.getStudentEnrollments(student)){
            grades = grades + points.get(enrollement.getGrade());
        }

        System.out.println(student.getMajor());
        return grades/CourseRepository.findCoursesByMajor(student.getMajor()).size();
    }

    public List<Enrollement> getPassedEnrollmentBasedOnCourse(Course course){
        List<Enrollement> enrollements = EnrollmentRepository.findGradeBasedOnCourse(course);
        if(enrollements != null){
            List<Enrollement> passedEnrollements = enrollements.stream()
                .filter(enrollement -> enrollement.getGrade().matches("[DEF][+-]?")) // Keep only passing grades
                .collect(Collectors.toList());
            return passedEnrollements;
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
            int num = 99;
            
            for (var enrollement: failedEnrollements){
                recoveryPlans.add(new RecoveryPlan(("P"+num), selectedRecoveryTask, enrollement, "Active"));
                num++;
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
        List<Course> allCourses = getAllCourses();
        this.allRecoveryPlans = RecoveryRepository.loadAllRecoveryPlan();

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
                .filter(course -> course != null) // Filter out null courses
                .collect(Collectors.toSet());

        System.out.println("Courses in Plans: " + coursesInPlans);

        if (coursesInPlans.isEmpty()) {
            System.out.println("No courses found in recovery plans.");
            return new ArrayList<>();
        }

        List<Course> filteredCourses = allCourses.stream()
                .filter(coursesInPlans::contains)
                .collect(Collectors.toList());

        System.out.println("Filtered Courses: " + filteredCourses);
        return filteredCourses;
    }

    public List<RecoveryPlan> getRecoveryPlansByCourse(Course course){
        return allRecoveryPlans.stream()
                .filter(plan -> plan.getEnrollement().getCourse().equals(course))
                .collect(Collectors.toList());
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
