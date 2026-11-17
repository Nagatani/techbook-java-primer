// Student.java - 学生。在籍区分と成績は、別のオブジェクトに任せる（合成）
import java.util.List;
import java.util.Objects;

public final class Student {
    private final String id;
    private final String name;
    private final String department;
    private final Enrollment enrollment;
    private final Transcript transcript = new Transcript();

    public Student(String id, String name, String department, Enrollment enrollment) {
        this.id = requireText(id, "学生ID");
        this.name = requireText(name, "氏名");
        this.department = requireText(department, "学部");
        this.enrollment = Objects.requireNonNull(enrollment, "在籍区分がnullです");
    }

    private static String requireText(String value, String label) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(label + "が空です");
        }
        return value.strip();
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDepartment() { return department; }
    public Enrollment getEnrollment() { return enrollment; }

    // 成績の操作はTranscriptに任せる（呼び出し側はTranscriptを知らなくてよい）
    public void recordGrade(Grade grade) { transcript.record(grade); }
    public boolean removeGrade(String course) {
        return transcript.remove(course);
    }
    public List<Grade> getGrades() { return transcript.grades(); }
    public double gpa() { return transcript.gpa(); }

    // 在籍区分ごとの説明。区分を足すと、ここがコンパイルエラーになって気づける
    public String describeEnrollment() {
        return switch (enrollment) {
            case Undergraduate u -> "学部生（" + u.admissionYear()
                    + "年度入学）";
            case Graduate g -> "大学院生（研究分野: " + g.researchField()
                    + "）";
        };
    }
}
