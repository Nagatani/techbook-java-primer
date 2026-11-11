import java.time.LocalDateTime;

// 本文では省略している補助クラス
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
        return "Student{name='" + name + "', grade=" + grade + ", id='" + studentId + "'}";
    }
}

class Task {
    private final int priority;
    private final LocalDateTime createdAt;
    private final String title;

    Task(int priority, LocalDateTime createdAt, String title) {
        this.priority = priority;
        this.createdAt = createdAt;
        this.title = title;
    }

    int getPriority() { return priority; }
    LocalDateTime getCreatedAt() { return createdAt; }
    String getTitle() { return title; }

    @Override
    public String toString() {
        return "Task{priority=" + priority + ", title='" + title + "'}";
    }
}
