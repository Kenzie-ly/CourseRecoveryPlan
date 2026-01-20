import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import java.util.List;

public class CourseRecoveryReport extends PdfFile {

    private Student student;
    private List<RecoveryTask> tasks;
    public CourseRecoveryReport(List<RecoveryTask> tasks)
    {
        this.tasks = tasks;
    }
    public void generateReport(Student student, boolean introduction, Document document)
    {
        this.student = student;
        if (introduction != false){
            addStudentInfo(document);
        }

        addTaskTable(document);
        document.close();
    }
    private void addStudentInfo(Document document) { //Student Information
        document.add(new Paragraph("Student Information").simulateBold().setFontSize(14));
        document.add(new Paragraph("Student ID: "+student.getStudentID()));
        document.add(new Paragraph("Name     : "+student.getFirstName()+" "+student.getLastName()));
        document.add(new Paragraph("Major    : "+student.getMajor()));
        document.add(new Paragraph("Year     : "+student.getYear()));
        document.add(new Paragraph("Email    : "+student.getEmail()));
        document.add(new Paragraph("\n"));
    }
    private void addTaskTable(Document document) //Tasj TABLE selection
    {
        float[] widths = {2f, 4f, 3f, 3f, 2f, 2f};
        Table table = new Table(UnitValue.createPercentArray(widths));

        String[] headers = {"Week", "Task", "Component", "Deadline", "Completed", "Grade"};

        for (String h : headers)
        {
            table.addHeaderCell(new Cell()
                    .add(new Paragraph(h).simulateBold())
                    .setTextAlignment(TextAlignment.CENTER));
        }
        for (RecoveryTask t : tasks) { // rows
            table.addCell(new Cell().add(new Paragraph(t.getWeek()))
                    .setTextAlignment(TextAlignment.CENTER));

            table.addCell(new Cell().add(new Paragraph(t.getTask())));

            table.addCell(new Cell().add(new Paragraph(t.getComponent()))
                    .setTextAlignment(TextAlignment.CENTER));

            table.addCell(new Cell().add(new Paragraph(t.getDeadline()))
                    .setTextAlignment(TextAlignment.CENTER));

            String completedText = t.isCompleted() ? "Yes" : "No"; // Completed YES / NO, status
            table.addCell(new Cell().add(new Paragraph(completedText))
                    .setTextAlignment(TextAlignment.CENTER));

            String gradeText = (t.getGrade() == null ? "" : t.getGrade()); // assigned Grade by user
            table.addCell(new Cell().add(new Paragraph(gradeText))
                    .setTextAlignment(TextAlignment.CENTER));
        }
        document.add(table);
    }
}