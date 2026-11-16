// GraduateStudent.java - 大学院生
public class GraduateStudent extends Student {
    private String researchField;
    private String advisorName;
    
    public GraduateStudent(String studentId, String name, int age, 
                          String department, String researchField) {
        super(studentId, name, age, department);
        this.researchField = researchField;
    }
    
    @Override
    public String getRole() {
        return "Graduate Student";
    }
    
    public String getResearchField() { return researchField; }
    public String getAdvisorName() { return advisorName; }
    
    public void setResearchField(String researchField) { 
        this.researchField = researchField; 
    }
    public void setAdvisor(String advisorName) { 
        this.advisorName = advisorName; 
    }
    
    @Override
    public String toString() {
        return String.format("%s[ID=%s, Name=%s, Age=%d, Dept=%s, Research=%s, Advisor=%s, GPA=%.2f]",
            getRole(), id, name, age, department, researchField, advisorName, gpa);
    }
}