package classes;

public class Course {
    private String courseID;
    private String courseName;
    private int credits;
    private String semester;
    private String instructor;
    private int capacity;
    private String majorID;

    public Course(String courseID, String courseName, int credits, String semester, String instructor, int capacity, String majorID) {
        this.courseID = courseID;
        this.courseName = courseName;
        this.credits = credits;
        this.semester = semester;
        this.instructor = instructor;
        this.capacity = capacity;
        this.majorID = majorID;
    }

    // getter
    public String getCourseID() {
        return courseID;
    }

    public String getCourseName() {
        return courseName;
    }

    public int getCredits() {
        return credits;
    }

    public String getSemester() {
        return semester;
    }

    public String getInstructor() {
        return instructor;
    }

    public int getCapacity() {
        return capacity;
    }

    public String getMajorID(){
        return majorID;
    }

    // setter
    public void setCourseID(String courseID) {
        this.courseID = courseID;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Course course = (Course) obj;
        return courseID != null && courseID.equals(course.courseID);
    }

    @Override
    public int hashCode() {
        return courseID != null ? courseID.hashCode() : 0;
    }
}
