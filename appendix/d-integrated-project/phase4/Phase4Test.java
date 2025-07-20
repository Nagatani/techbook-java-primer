// Phase4Test.java - フェーズ4のテスト
import java.io.*;

public class Phase4Test {
    public static void main(String[] args) {
        System.out.println("=== フェーズ4: 例外処理のテスト ===\n");
        
        StudentService service = new StudentService();
        
        // 正常な学生登録
        try {
            GradeableStudent student1 = new GradeableStudent("S001", "田中太郎", 20, "工学部");
            service.registerStudent(student1);
            System.out.println("✓ 学生S001を正常に登録しました");
            
            // 成績の追加
            service.updateGrade("S001", "数学", 3.5);
            service.updateGrade("S001", "物理", 4.0);
            System.out.println("✓ 成績を正常に追加しました");
            
        } catch (Exception e) {
            System.out.println("✗ エラー: " + e.getMessage());
        }
        
        // 重複登録のテスト
        System.out.println("\n--- 重複登録のテスト ---");
        try {
            GradeableStudent duplicate = new GradeableStudent("S001", "別の人", 21, "理学部");
            service.registerStudent(duplicate);
            System.out.println("✗ 重複チェックが機能していません");
        } catch (DuplicateStudentException e) {
            System.out.println("✓ 重複を正しく検出: " + e.getMessage());
        }
        
        // 存在しない学生の成績更新
        System.out.println("\n--- 存在しない学生のテスト ---");
        try {
            service.updateGrade("S999", "化学", 3.0);
            System.out.println("✗ 存在チェックが機能していません");
        } catch (StudentNotFoundException e) {
            System.out.println("✓ 存在しない学生を正しく検出: " + e.getMessage());
        } catch (InvalidGradeException e) {
            System.out.println("✗ 想定外のエラー: " + e.getMessage());
        }
        
        // 無効な成績のテスト
        System.out.println("\n--- 無効な成績のテスト ---");
        try {
            service.updateGrade("S001", "英語", 5.0);
            System.out.println("✗ 成績範囲チェックが機能していません");
        } catch (InvalidGradeException e) {
            System.out.println("✓ 無効な成績を正しく検出: " + e.getMessage());
        } catch (StudentNotFoundException e) {
            System.out.println("✗ 想定外のエラー: " + e.getMessage());
        }
        
        // CSVインポートのテスト
        System.out.println("\n--- CSVインポートのテスト ---");
        
        // テスト用CSVファイルの作成
        try (PrintWriter writer = new PrintWriter("test_students.csv")) {
            writer.println("学生ID,氏名,年齢,学部");
            writer.println("S002,山田花子,21,理学部");
            writer.println("S003,佐藤次郎,22,工学部");
            writer.println("S001,重複太郎,20,文学部");  // 重複ID
            writer.println("S004,鈴木美咲,abc,文学部"); // 無効な年齢
            writer.println("S005,高橋健"); // データ不足
        } catch (IOException e) {
            System.out.println("テストファイル作成エラー: " + e.getMessage());
        }
        
        service.importStudentsFromCSV("test_students.csv");
        
        // 成績レポートの生成
        System.out.println("\n--- 成績レポートの生成 ---");
        System.out.println(service.generateGradeReport("S001"));
        System.out.println("\n" + service.generateGradeReport("S999")); // 存在しない学生
        
        // CSVエクスポートのテスト
        System.out.println("\n--- CSVエクスポートのテスト ---");
        try {
            service.exportToCSV("exported_students.csv");
            System.out.println("✓ CSVエクスポートが完了しました");
            
            // エクスポートしたファイルの内容を確認
            System.out.println("\nエクスポートされたデータ:");
            try (BufferedReader reader = new BufferedReader(new FileReader("exported_students.csv"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("  " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("✗ エクスポートエラー: " + e.getMessage());
        }
        
        // 後片付け
        new File("test_students.csv").delete();
        new File("exported_students.csv").delete();
    }
}