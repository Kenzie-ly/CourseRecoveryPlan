package classes;

public class RecoveryPlan {
    private Enrollement enrollement;
    private RecoveryTask task;
    
    public RecoveryPlan(RecoveryTask task, Enrollement enrollement){
        this.enrollement = enrollement;
        this.task = task;
    }

    public Course getRetakeCourse(){
        return enrollement.getCourse();
    }

    public RecoveryTask getTask(){
        return task;
    }

    public String getTaskID(){
        return task.getTaskID();
    }

    public Enrollement getEnrollement(){
        return enrollement;
    }

    public String getEnrollmentID(){
        return enrollement.getEnrollmentID();
    }

    public String getRecoveryTaskID(){
        return this.task.getTaskID();
    }

}
