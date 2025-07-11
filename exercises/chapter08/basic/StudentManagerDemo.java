package chapter08.basic;

/**
 * 学生管理システムのデモプログラム
 * 
 * ArrayListとHashMapの使い方を学習します。
 */
public class StudentManagerDemo {
    public static void main(String[] args) {
        System.out.println("=== 学生管理システムデモ ===\n");
        
        // TODO: StudentManagerのインスタンスを作成してください
        // StudentManager manager = new StudentManager();
        
        // TODO: 複数の学生を追加してください
        // 例:
        // manager.addStudent(new Student("S001", "山田太郎", 2, 85.5));
        // manager.addStudent(new Student("S002", "鈴木花子", 1, 92.0));
        // 他にも3〜4人追加
        
        // TODO: 全学生を表示してください
        System.out.println("\n--- 全学生一覧 ---");
        // manager.displayAllStudents();
        
        // TODO: 特定の学生を検索してください
        System.out.println("\n--- 学生検索 ---");
        // Student found = manager.findStudentById("S001");
        // 結果を表示
        
        // TODO: 成績順に表示してください
        System.out.println("\n--- 成績順表示 ---");
        // manager.displayStudentsByScore();
        
        // TODO: 特定学年の学生を取得して表示してください
        System.out.println("\n--- 2年生の学生 ---");
        // List<Student> grade2Students = manager.getStudentsByGrade(2);
        // 結果を表示
        
        // TODO: 平均成績を計算して表示してください
        System.out.println("\n--- 平均成績 ---");
        // double average = manager.calculateAverageScore();
        // 結果を表示
        
        // TODO: 学生を削除してみてください
        System.out.println("\n--- 学生削除 ---");
        // boolean removed = manager.removeStudent("S003");
        // 結果を表示し、削除後の一覧も表示
    }
}