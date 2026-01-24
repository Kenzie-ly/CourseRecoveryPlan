package classes;

public class RecoveryPlan {
    private Enrollement enrollement;
    private String planID;
    private RecoveryTask task;
    private String status;
    
    public RecoveryPlan(String planID, Enrollement enrollement, RecoveryTask task,String status){
        this.planID = planID;
        this.enrollement = enrollement;
        this.task = task;
        this.status = status;
    }

    public Course getRetakeCourse(){
        return enrollement.getCourse();
    }

    public String getPlanID(){
        return planID;
    }

    public String getTaskID(){
        return task.getTaskID();
    }

    public String getEnrollmentID(){
        return enrollement.getEnrollmentID();
    }

    public String getRecoveryTaskID(){
        return this.task.getTaskID();
    }

    public String getStatus(){
        return status;
    }
}
