import java.io.*;
import java.util.*;

public class RecoveryPlanIO {

    private static final String FOLDER = ResourceManager.getRecoveryPlanPath();

    public static void savePlan(RecoveryPlan plan) throws IOException { //saving the plan

        File dir = new File(FOLDER);
        if (!dir.exists()) dir.mkdirs();

        File file = new File(FOLDER + plan.getStudentID() + "_plan.txt");

        try (PrintWriter pw = new PrintWriter(new FileWriter(file, false))) {

            pw.println("Week|Task|Component|Deadline|CourseID|Completed|Grade"); // readable header for conv

            for (RecoveryTask t : plan.getTasks()) {
                pw.println(t.toLine());   // Always 7 fields
            }
        }
    }
    public static RecoveryPlan loadPlan(String studentID) throws IOException {// Loading pl[an

        RecoveryPlan plan = new RecoveryPlan(studentID);

        File file = new File(FOLDER + studentID + "_plan.txt");
        if (!file.exists()) return plan;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            String line;
            boolean first = true;

            while ((line = br.readLine()) != null) {

                if (line.trim().isEmpty()) continue;

                if (first && line.toLowerCase().startsWith("week|task|")) { // Skiping header line
                    first = false;
                    continue;
                }
                first = false;

                RecoveryTask task = RecoveryTask.fromLine(line); // Convert line → RecoveryTask
                plan.addTask(task);
            }
        }
        return plan;
    }
}
