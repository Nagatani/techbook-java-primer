// StudentTest.java - JUnitを使わずに、GradeableStudentの動作を確かめる例
// 条件が成り立たなければAssertionErrorを投げて止まる。
// （assert文は java -ea で実行しないと無効になるので、ここではif文で確かめる）
// JUnitを使ったテストの書き方は第21章で学ぶ

public class StudentTest {
    
    public static void main(String[] args) {
        IO.println("=== 単体テストのデモンストレーション ===\n");
        
        // テスト1: 学生の基本情報が正しく設定される
        testStudentCreation();
        
        // テスト2: 成績追加とGPA計算が正しく動作する
        testGradeAdditionAndGPACalculation();
        
        // テスト3: 年齢の不正な値は設定されない
        testInvalidAge();
        
        // テスト4: GPAの範囲チェックが機能する
        testGPARange();
        
        IO.println("\n✓ すべてのテストが完了しました");
    }
    
    // 条件が成り立たなければ、メッセージを付けてAssertionErrorを投げる
    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
    
    private static void testStudentCreation() {
        IO.println("テスト1: 学生の基本情報が正しく設定される");
        
        GradeableStudent student = new GradeableStudent("S001", "田中太郎", 20, "工学部");
        
        check("S001".equals(student.getId()), "学生IDが正しくない");
        check("田中太郎".equals(student.getName()), "氏名が正しくない");
        check(student.getAge() == 20, "年齢が正しくない");
        check("工学部".equals(student.getDepartment()), "学部が正しくない");
        check(student.getGpa() == 0.0, "初期GPAが0.0でない");
        
        IO.println("  ✓ 学生情報が正しく設定されました");
    }
    
    private static void testGradeAdditionAndGPACalculation() {
        IO.println("\nテスト2: 成績追加とGPA計算が正しく動作する");
        
        GradeableStudent student = new GradeableStudent("S001", "田中太郎", 20, "工学部");
        student.addGrade("数学", 3.5);
        student.addGrade("物理", 4.0);
        student.addGrade("英語", 3.0);
        
        double expectedGPA = (3.5 + 4.0 + 3.0) / 3.0;
        check(Math.abs(student.calculateGPA() - expectedGPA) < 0.01, "GPA計算が正しくない");
        check(student.getGrades().size() == 3, "成績数が正しくない");
        
        IO.println("  ✓ 成績追加とGPA計算が正しく動作しました");
        IO.println("    計算されたGPA: " + student.getGpa());
    }
    
    private static void testInvalidAge() {
        IO.println("\nテスト3: 年齢の不正な値は設定されない");
        
        GradeableStudent student = new GradeableStudent("S001", "田中太郎", 20, "工学部");
        
        student.setAge(-5);
        check(student.getAge() == 20, "負の年齢が設定されてしまった");
        
        student.setAge(25);
        check(student.getAge() == 25, "正常な年齢が設定されない");
        
        IO.println("  ✓ 年齢の検証が正しく動作しました");
    }
    
    private static void testGPARange() {
        IO.println("\nテスト4: GPAの範囲チェックが機能する");
        
        GradeableStudent student = new GradeableStudent("S001", "田中太郎", 20, "工学部");
        
        student.setGpa(5.0); // 範囲外
        check(student.getGpa() == 0.0, "範囲外のGPAが設定されてしまった");
        
        student.setGpa(3.5); // 正常範囲
        check(student.getGpa() == 3.5, "正常なGPAが設定されない");
        
        IO.println("  ✓ GPAの範囲チェックが正しく動作しました");
    }
}
