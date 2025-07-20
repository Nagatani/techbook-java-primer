// StudentTest.java - JUnitを使用した単体テストの例
// 注：実行にはJUnit 5が必要です

public class StudentTest {
    
    public static void main(String[] args) {
        System.out.println("=== 単体テストのデモンストレーション ===\n");
        
        // テスト1: 学生の基本情報が正しく設定される
        testStudentCreation();
        
        // テスト2: 成績追加とGPA計算が正しく動作する
        testGradeAdditionAndGPACalculation();
        
        // テスト3: 年齢の不正な値は設定されない
        testInvalidAge();
        
        // テスト4: GPAの範囲チェックが機能する
        testGPARange();
        
        System.out.println("\n✓ すべてのテストが完了しました");
    }
    
    private static void testStudentCreation() {
        System.out.println("テスト1: 学生の基本情報が正しく設定される");
        
        GradeableStudent student = new GradeableStudent("S001", "田中太郎", 20, "工学部");
        
        assert "S001".equals(student.getId()) : "学生IDが正しくない";
        assert "田中太郎".equals(student.getName()) : "氏名が正しくない";
        assert student.getAge() == 20 : "年齢が正しくない";
        assert "工学部".equals(student.getDepartment()) : "学部が正しくない";
        assert student.getGpa() == 0.0 : "初期GPAが0.0でない";
        
        System.out.println("  ✓ 学生情報が正しく設定されました");
    }
    
    private static void testGradeAdditionAndGPACalculation() {
        System.out.println("\nテスト2: 成績追加とGPA計算が正しく動作する");
        
        GradeableStudent student = new GradeableStudent("S001", "田中太郎", 20, "工学部");
        student.addGrade("数学", 3.5);
        student.addGrade("物理", 4.0);
        student.addGrade("英語", 3.0);
        
        double expectedGPA = (3.5 + 4.0 + 3.0) / 3.0;
        assert Math.abs(student.calculateGPA() - expectedGPA) < 0.01 : "GPA計算が正しくない";
        assert student.getGrades().size() == 3 : "成績数が正しくない";
        
        System.out.println("  ✓ 成績追加とGPA計算が正しく動作しました");
        System.out.println("    計算されたGPA: " + student.getGpa());
    }
    
    private static void testInvalidAge() {
        System.out.println("\nテスト3: 年齢の不正な値は設定されない");
        
        GradeableStudent student = new GradeableStudent("S001", "田中太郎", 20, "工学部");
        
        student.setAge(-5);
        assert student.getAge() == 20 : "負の年齢が設定されてしまった";
        
        student.setAge(25);
        assert student.getAge() == 25 : "正常な年齢が設定されない";
        
        System.out.println("  ✓ 年齢の検証が正しく動作しました");
    }
    
    private static void testGPARange() {
        System.out.println("\nテスト4: GPAの範囲チェックが機能する");
        
        GradeableStudent student = new GradeableStudent("S001", "田中太郎", 20, "工学部");
        
        student.setGpa(5.0); // 範囲外
        assert student.getGpa() == 0.0 : "範囲外のGPAが設定されてしまった";
        
        student.setGpa(3.5); // 正常範囲
        assert student.getGpa() == 3.5 : "正常なGPAが設定されない";
        
        System.out.println("  ✓ GPAの範囲チェックが正しく動作しました");
    }
}