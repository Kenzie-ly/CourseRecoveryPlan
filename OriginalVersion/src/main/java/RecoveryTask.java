import java.util.*;

public class RecoveryTask {
    private String week;
    private String task;
    private String component;
    private String deadline;
    private String courseID;
    private boolean completed;
    private String grade;

    public RecoveryTask(String week, String task, String component, String deadline, String courseID, boolean completed, String grade) // Constructor
    {
        this.week = week != null ? week : "";
        this.task = task != null ? task : "";
        this.component = component != null ? component : "";
        this.deadline = deadline != null ? deadline : "";
        this.courseID = courseID != null ? courseID : "";
        this.completed = completed;
        this.grade = grade != null ? grade : "";
    }
    public String getWeek() { return week; }
    public String getTask() { return task; }
    public String getComponent() { return component; }
    public String getDeadline() { return deadline; }
    public String getCourseID() { return courseID; }
    public boolean isCompleted() { return completed; }
    public String getGrade() { return grade; }

    public String toString()
    {
        return week+" — "+task +" ("+ component +") ["+deadline+"]  "+(completed ? "✓" : "✗")+"  Grade: "+grade;
    }
    public String toLine() // SAVE to file (line format)
    {
        return week+"|"+task+"|"+component+"|"+deadline+"|"+courseID+"|"+(completed ? "1" : "0")+"|"+grade;
    }
    public static RecoveryTask fromLine(String line) // LOAD from file
    {
        String[] p = line.split("\\|", -1);

        String w  = p.length > 0 ? p[0] : "";
        String t  = p.length > 1 ? p[1] : "";
        String c  = p.length > 2 ? p[2] : "";
        String d  = p.length > 3 ? p[3] : "";
        String cid = p.length > 4 ? p[4] : "";
        boolean completed = p.length > 5 && p[5].equals("1");
        String grade = p.length > 6 ? p[6] : "";

        return new RecoveryTask(w, t, c, d, cid, completed, grade);
    }
}
