package classes;

public class Requirement {
    private String requirementID;
    private String year;
    private String semester;
    private int requiredCourseCount;
    private int requiredCredits;

    // Constructor to initialize the rule
    public Requirement(String id, String year, String sem, int count, int credits) {
        this.requirementID = id;
        this.year = year;
        this.semester = sem;
        this.requiredCourseCount = count;
        this.requiredCredits = credits;
    }

    // Getters
    public String getRequirementID() { return requirementID; }
    public int getRequiredCourseCount() { return requiredCourseCount; }
    public int getRequiredCredits() { return requiredCredits; }
    // ... other getters
}