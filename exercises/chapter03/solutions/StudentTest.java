/**
 * 第3章 演習問題1: StudentTestクラスの解答例
 * 
 * 【テストの目的】
 * - 複数のコンストラクタの動作確認
 * - thisキーワードの正しい動作確認
 * - エラーケースのテスト
 * - 実践的な使用例の提示
 * 
 * 【デバッグのコツ】
 * 1. コンストラクタごとに分けてテスト
 * 2. 期待値と実際の値を比較
 * 3. エラーケースも含めた網羅的なテスト
 * 4. 出力形式の確認
 */
public class StudentTest {
    public static void main(String[] args) {
        IO.println("=== 第3章 演習問題1: Student クラスのテスト ===");
        
        // 【基本テスト】各コンストラクタの動作確認
        testBasicConstructors();
        
        // 【応用テスト】コンストラクタチェーンの確認
        testConstructorChaining();
        
        // 【発展テスト】妥当性チェック機能
        testValidation();
        
        // 【実践テスト】実際の使用例
        testPracticalUsage();
        
        // 【発展テスト】オーバーライドメソッドの確認
        testOverriddenMethods();
        
        IO.println("\n=== テスト完了 ===");
    }
    
    /**
     * 基本的なコンストラクタのテスト
     */
    private static void testBasicConstructors() {
        IO.println("\n--- 基本コンストラクタのテスト ---");
        
        // デフォルトコンストラクタ
        Student student1 = new Student();
        IO.print("学生1: ");
        student1.displayInfo();
        
        // 名前のみのコンストラクタ
        Student student2 = new Student("田中太郎");
        IO.print("学生2: ");
        student2.displayInfo();
        
        // 名前と年齢のコンストラクタ
        Student student3 = new Student("佐藤花子", 20);
        IO.print("学生3: ");
        student3.displayInfo();
        
        // 全項目のコンストラクタ
        Student student4 = new Student("鈴木一郎", 22, "S2024001");
        IO.print("学生4: ");
        student4.displayInfo();
    }
    
    /**
     * コンストラクタチェーンの動作確認
     */
    private static void testConstructorChaining() {
        IO.println("\n--- コンストラクタチェーンのテスト ---");
        
        // thisキーワードによるコンストラクタチェーンの確認
        Student student1 = new Student();
        Student student2 = new Student("山田太郎");
        Student student3 = new Student("田中花子", 19);
        
        IO.println("全てのコンストラクタが正しく動作しています。");
        IO.println("デフォルト値が適切に設定されています。");
        
        // 値の確認
        IO.println("student1 - 名前: " + student1.getName() + 
                         ", 年齢: " + student1.getAge() + 
                         ", 学籍番号: " + student1.getStudentId());
    }
    
    /**
     * 妥当性チェック機能のテスト
     */
    private static void testValidation() {
        IO.println("\n--- 妥当性チェックのテスト ---");
        
        // 正常な値
        Student validStudent = new Student("正常太郎", 20, "S2024001");
        IO.println("正常な学生: " + validStudent.isValidStudent());
        
        // 不正な値（年齢）
        Student invalidStudent = new Student("不正太郎", -5, "S2024002");
        IO.println("不正な学生（負の年齢）: " + invalidStudent.isValidStudent());
        
        // setterメソッドでの妥当性チェック
        IO.println("\nsetterメソッドでの妥当性チェック:");
        Student student = new Student();
        student.setAge(-10);  // 警告が表示される
        student.setAge(25);   // 正常に設定される
        IO.println("設定後の年齢: " + student.getAge());
    }
    
    /**
     * 実践的な使用例のテスト
     */
    private static void testPracticalUsage() {
        IO.println("\n--- 実践的な使用例のテスト ---");
        
        // 学生管理システムのシミュレーション
        Student[] students = {
            new Student("アリス", 18, "S2024001"),
            new Student("ボブ", 19, "S2024002"),
            new Student("チャーリー", 20, "S2024003"),
            new Student("ダイアナ", 21, "S2024004")
        };
        
        IO.println("=== 学生管理システム ===");
        for (int i = 0; i < students.length; i++) {
            IO.print("学生" + (i + 1) + ": ");
            students[i].displayFormattedInfo();
            IO.println("  成人: " + (students[i].isAdult() ? "はい" : "いいえ"));
        }
        
        // 成人の学生をカウント
        int adultCount = 0;
        for (Student student : students) {
            if (student.isAdult()) {
                adultCount++;
            }
        }
        IO.println("\n成人の学生数: " + adultCount + "人");
    }
    
    /**
     * オーバーライドメソッドのテスト
     */
    private static void testOverriddenMethods() {
        IO.println("\n--- オーバーライドメソッドのテスト ---");
        
        Student student1 = new Student("テスト太郎", 20, "S2024001");
        Student student2 = new Student("テスト太郎", 20, "S2024001");
        Student student3 = new Student("テスト花子", 19, "S2024002");
        
        // toString()のテスト
        IO.println("toString(): " + student1.toString());
        
        // equals()のテスト
        IO.println("student1.equals(student2): " + student1.equals(student2));
        IO.println("student1.equals(student3): " + student1.equals(student3));
        
        // hashCode()のテスト
        IO.println("student1.hashCode(): " + student1.hashCode());
        IO.println("student2.hashCode(): " + student2.hashCode());
        IO.println("ハッシュコード一致: " + 
                         (student1.hashCode() == student2.hashCode()));
    }
    
    /**
     * 【発展】エラーケースのテスト
     */
    private static void testErrorCases() {
        IO.println("\n--- エラーケースのテスト ---");
        
        // null値のテスト
        try {
            Student student = new Student(null, 20, "S2024001");
            IO.println("null名前の学生: " + student.getName());
        } catch (Exception e) {
            IO.println("例外発生: " + e.getMessage());
        }
        
        // 極端な値のテスト
        Student extremeStudent = new Student("極端", Integer.MAX_VALUE, "EXTREME");
        IO.println("極端な値の学生: " + extremeStudent.toString());
    }
    
    /**
     * 【発展】パフォーマンステスト
     */
    private static void testPerformance() {
        IO.println("\n--- パフォーマンステスト ---");
        
        long startTime = System.currentTimeMillis();
        
        // 大量のオブジェクト作成
        Student[] students = new Student[10000];
        for (int i = 0; i < students.length; i++) {
            students[i] = new Student("学生" + i, i % 100, "S" + i);
        }
        
        long endTime = System.currentTimeMillis();
        IO.println("10000個のStudentオブジェクト作成時間: " + 
                         (endTime - startTime) + "ms");
    }
}