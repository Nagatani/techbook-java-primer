// 本文では省略している補助クラス（サンプルコード11-6と同じ）
class Student {
    private final String name;
    private final int grade;
    private final String studentId;

    Student(String name, int grade, String studentId) {
        this.name = name;
        this.grade = grade;
        this.studentId = studentId;
    }

    String getName() { return name; }
    int getGrade() { return grade; }
    String getStudentId() { return studentId; }

    @Override
    public String toString() {
        return name + "(" + grade + "年, " + studentId + ")";
    }
}
