import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class MultiConditionSortExample {
    public static void main(String[] args) {
        // 学生のサンプルデータ
        List<Student> students = Arrays.asList(
            new Student("Alice", 2, "S003"),
            new Student("Bob", 1, "S001"),
            new Student("Charlie", 2, "S002"),
            new Student("Alice", 1, "S004")
        );
        
        // 学年で並び替え、同じ学年の場合は名前で並び替え、
        // さらに同名の場合は学生番号で並び替え
        List<Student> sortedStudents = new ArrayList<>(students);
        sortedStudents.sort(
            Comparator.comparing(Student::getGrade)
                .thenComparing(Student::getName)
                .thenComparing(Student::getStudentId)
        );
        System.out.println("学生ソート後（学年→名前→学生番号）:");
        sortedStudents.forEach(System.out::println);

        // 降順ソートの組み合わせ
        List<Student> reversedStudents = new ArrayList<>(students);
        reversedStudents.sort(
            Comparator.comparing(Student::getGrade).reversed()
                .thenComparing(Student::getName)
        );
        System.out.println("\n降順ソート（学年降順→名前昇順）:");
        reversedStudents.forEach(System.out::println);

        // 複雑な条件：優先度、作成日時、タイトルの順
        List<Task> tasks = Arrays.asList(
            new Task(1, LocalDateTime.now().minusHours(3), "Task C"),
            new Task(3, LocalDateTime.now().minusHours(1), "Task A"),
            new Task(1, LocalDateTime.now().minusHours(2), "Task B")
        );
        
        List<Task> sortedTasks = new ArrayList<>(tasks);
        sortedTasks.sort(
            Comparator.comparing(Task::getPriority).reversed()
                .thenComparing(Task::getCreatedAt)
                .thenComparing(Task::getTitle)
        );
        System.out.println("\nタスクソート後（優先度降順→作成日時→タイトル）:");
        sortedTasks.forEach(System.out::println);
    }
}
