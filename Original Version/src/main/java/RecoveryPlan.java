import java.util.*;

public class RecoveryPlan {// this is to hold ONE students entier plan and their recovery taks in one place.
    private String studentID;
    private List<RecoveryTask> tasks = new ArrayList<>();
    public RecoveryPlan(String studentID) {
        this.studentID = studentID;
    }
    public void addTask(RecoveryTask task) { tasks.add(task); }
    public List<RecoveryTask> getTasks() { return tasks; }
    public String getStudentID() { return studentID; }
}
