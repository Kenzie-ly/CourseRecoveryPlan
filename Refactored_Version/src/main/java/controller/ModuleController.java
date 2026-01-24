package controller;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import classes.*;
import classes.Enrollement;
import repository.*;

public class ModuleController {
    private final List<Course> courses = CourseRepository.loadAllCourses();
    
    public List<Course> getAllCourses(){
        return courses;
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

        System.out.println("is this empty too?");
        return enrollements.stream()
            .filter(e -> e.getGrade() != null && e.getGrade().matches("[DEF][+-]?"))
            .collect(Collectors.toList());
    }

    public List<RecoveryPlan> createRecoveryPlan(List<Enrollement> failedEnrollements, RecoveryTask selectedRecoveryTask){
        if (failedEnrollements != null && !failedEnrollements.isEmpty()){
            List<RecoveryPlan> recoveryPlans = new ArrayList<>();
            int num = 99;
            
            for (var enrollement: failedEnrollements){
                recoveryPlans.add(new RecoveryPlan(("P"+num), enrollement, selectedRecoveryTask, "Active"));
                num++;
            }
            try{
                EnrollmentRepository.addRecoveryPlan(recoveryPlans);
                return recoveryPlans;
            }catch(Exception e){
                System.out.println(e);
            }
        }else{
            System.out.println("Empty");
        }
        return null;
    }

    public RecoveryTask createRecoveryTask(String taskID, String phase, String description){
        RecoveryTask recoveryTask = new RecoveryTask(taskID, phase, description);
        try{
            EnrollmentRepository.addRecoveryTask(recoveryTask);
            return recoveryTask;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public List<RecoveryTask> getAllRecoveryTask(){
        try {
            return EnrollmentRepository.loadAllRecoveryTask();
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }   
    }

    public boolean updateRecoveryTask(RecoveryTask updatedRecoveryTask){
        try {
            EnrollmentRepository.updateRecoveryTask(updatedRecoveryTask);
            return true;
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }   
    }
}
