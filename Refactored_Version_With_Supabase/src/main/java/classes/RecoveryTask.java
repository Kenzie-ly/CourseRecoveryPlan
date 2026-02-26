package classes;

public class RecoveryTask {
    private String task;
    private String phase;
    private String taskID;

    public RecoveryTask(String taskID, String phase, String task){
        this.task = task;
        this.taskID = taskID;
        this.phase = phase;
    }

    public String getTaskID(){
        return taskID;
    }

    public String getPhase(){
        return phase;
    }

    public String getTask(){
        return task;
    }

    public void setTask(String task){ this.task = task;}

}
