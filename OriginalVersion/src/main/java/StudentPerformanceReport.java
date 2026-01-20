import java.io.File;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.*;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.properties.VerticalAlignment;

public class StudentPerformanceReport extends PdfFile{
    private Document document;
    private String filterBy;
    private List<Course> courseList = Course.loadData(ResourceManager.getCourseDataPath());
    private CourseGrade courseGrade;

    public StudentPerformanceReport(String filterBy){
        this.filterBy = filterBy;
    }

    private void createIntroduction(Student student){
        //create student personal info as the introduction
        document.add(new Paragraph("Student ID: " + student.getStudentID())
            .simulateBold()
            .setFontSize(12));

        document.add(new Paragraph("Student Name: " + student.getFirstName() + " " + student.getLastName())
            .simulateBold()
            .setFontSize(12));

        document.add(new Paragraph("Program: " + student.getMajor())
            .simulateBold()
            .setFontSize(12));

        document.add(new Paragraph("\n"));
    }
    
    private void generateBody(Student student){
        this.courseGrade = new CourseGrade(student.getStudentID());

        //Based on Semester
        if (filterBy.equalsIgnoreCase("semester")) {
            String sem = student.getCurrentSemester(this.courseList);

            document.add(new Paragraph(sem+"\n")
                .simulateBold()
                .setFontSize(14));

            CourseAndGradeTable(courseGrade.getCourseAndGradeBySemester(sem, this.courseList));
            gpaParagraph(sem, student);
        //Based on Year
        }else{
            //Year, semester 1 and 2
            if(student.getCurrentSemester(this.courseList).equalsIgnoreCase("Semester 2")){
                document.add(new Paragraph(student.getYear() + " Year" + ", Semester 1"+"\n")
                    .simulateBold()
                    .setFontSize(14));

                CourseAndGradeTable(courseGrade.getCourseAndGradeBySemester("Semester 1", this.courseList));
                gpaParagraph("Semester 1", student);

                document.add(new Paragraph("\n"));

                document.add(new Paragraph(student.getYear() + " Year" + ", Semester 2"+"\n")
                    .simulateBold()
                    .setFontSize(14));

                CourseAndGradeTable(courseGrade.getCourseAndGradeBySemester("Semester 2", this.courseList));
                gpaParagraph("Semester 2", student);
            //Year, semester 1
            }else{
                document.add(new Paragraph(student.getYear() + " Year" + ", Semester 1"+"\n")
                    .simulateBold()
                    .setFontSize(14));

                CourseAndGradeTable(courseGrade.getCourseAndGradeBySemester("Semester 1",this.courseList));
                gpaParagraph("Semester 1", student);
            }
        }

        //Cumulitative GPA
        double roundedCGPA = Math.round(courseGrade.calStudentCGPA() * 100.0) / 100.0;
        document.add(new Paragraph("\nCumulative CGPA: " + roundedCGPA)
            .simulateBold()
            .setFontSize(12));
    
        document.close();
    }

    public void generateReport(Student s, boolean introduction, Document document) throws Exception{ 
        this.document = document; 
        if (introduction){
            this.createIntroduction(s);
            this.generateBody(s);
        }else{
            this.generateBody(s);
        }
    }


    private void CourseAndGradeTable(Map<Course,Map<String,Double>> CourseAndGradeMap){
        float[] columnWidths = {2f,4f, 2f, 2f, 2f};
        Table courseAndGradeTable = new Table(UnitValue.createPercentArray(columnWidths));
        
        //create header of a table
        String[] tableHeaders = {"Course Code", "Course title", "Credit Hours", "Grade", "Grade Point (4.0 Scale)"};
        for (String h:tableHeaders){
            courseAndGradeTable.addHeaderCell(new Cell()
                .add(new Paragraph(h)
                .simulateBold()
                .setTextAlignment(TextAlignment.CENTER)));
        }

        //create a function inside a funtion to add items per row
        Consumer<String[]> createRowCell = (data) -> {
            //for one row cell, create multiple column cells
            for (String d : data){
                courseAndGradeTable.addCell(new Cell().add(new Paragraph(d)).setTextAlignment(TextAlignment.CENTER));
            }
        };

        //for each course with grade, create a row cell, and add data to the row cell
        CourseAndGradeMap.forEach((course,grade) -> createRowCell.accept(new String[]{
            course.getCourseID(), 
            course.getCourseName(), 
            String.valueOf(course.getCredits()),
            grade.keySet().iterator().next(), 
            grade.values().iterator().next().toString()
            }
        ));   
           
        document.add(courseAndGradeTable);
    }

    private void gpaParagraph(String sem, Student student){
        double roundedGPA = Math.round(courseGrade.calcGPA(sem, this.courseList) * 100.0) / 100.0;
        document.add(new Paragraph("\nGPA: " + roundedGPA)
            .simulateBold()
            .setFontSize(12));
    }
}