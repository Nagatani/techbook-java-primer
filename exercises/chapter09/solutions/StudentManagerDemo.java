package chapter09.solutions;

import java.util.List;

/**
 * 学生管理システムのデモ（課題1の解答例）
 */
public class StudentManagerDemo {
    public static void main(String[] args) {
        IO.println("=== 学生管理システムデモ ===\n");

        StudentManager manager = new StudentManager();
        manager.addStudent(new Student("S001", "山田太郎", 2, 85.5));
        manager.addStudent(new Student("S002", "鈴木花子", 1, 92.0));
        manager.addStudent(new Student("S003", "佐藤次郎", 2, 78.0));
        manager.addStudent(new Student("S004", "高橋美咲", 3, 88.5));
        // IDが重複する学生は追加されない
        manager.addStudent(new Student("S001", "田中一郎", 1, 70.0));

        IO.println("\n--- 全学生一覧 ---");
        manager.displayAllStudents();

        IO.println("\n--- 学生検索 ---");
        IO.println("S001: " + manager.findStudentById("S001"));
        IO.println("S009: " + manager.findStudentById("S009"));

        IO.println("\n--- 成績順表示 ---");
        manager.displayStudentsByScore();

        IO.println("\n--- 2年生の学生 ---");
        List<Student> grade2Students = manager.getStudentsByGrade(2);
        for (Student student : grade2Students) {
            IO.println(student.getName());
        }

        IO.println("\n--- 平均成績 ---");
        IO.println("平均: " + "%.2f".formatted(manager.calculateAverageScore()));

        IO.println("\n--- 学生削除 ---");
        IO.println("S003を削除: " + manager.removeStudent("S003"));
        IO.println("S003をもう一度削除: " + manager.removeStudent("S003"));
        IO.println("残りの人数: " + manager.getStudentCount());
    }
}
