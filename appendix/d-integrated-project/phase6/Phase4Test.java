// Phase4Test.java - フェーズ4のテスト
// フェーズ6では、student_management.dbを書き換えないようにメモリ上のデータベースを使う
import java.io.*;
import java.sql.SQLException;

public class Phase4Test {
    public static void main(String[] args) throws SQLException {
        IO.println("=== フェーズ4: 例外処理のテスト ===\n");
        
        StudentService service = new StudentService("jdbc:sqlite::memory:");
        
        // 正常な学生登録
        try {
            GradeableStudent student1 = new GradeableStudent("S001", "田中太郎", 20, "工学部");
            service.registerStudent(student1);
            IO.println("✓ 学生S001を正常に登録しました");
            
            // 成績の追加
            service.updateGrade("S001", "数学", 3.5);
            service.updateGrade("S001", "物理", 4.0);
            IO.println("✓ 成績を正常に追加しました");
            
        } catch (Exception e) {
            IO.println("✗ エラー: " + e.getMessage());
        }
        
        // 重複登録のテスト
        IO.println("\n--- 重複登録のテスト ---");
        try {
            GradeableStudent duplicate = new GradeableStudent("S001", "別の人", 21, "理学部");
            service.registerStudent(duplicate);
            IO.println("✗ 重複チェックが機能していません");
        } catch (DuplicateStudentException e) {
            IO.println("✓ 重複を正しく検出: " + e.getMessage());
        }
        
        // 存在しない学生の成績更新
        IO.println("\n--- 存在しない学生のテスト ---");
        try {
            service.updateGrade("S999", "化学", 3.0);
            IO.println("✗ 存在チェックが機能していません");
        } catch (StudentNotFoundException e) {
            IO.println("✓ 存在しない学生を正しく検出: " + e.getMessage());
        } catch (InvalidGradeException e) {
            IO.println("✗ 想定外のエラー: " + e.getMessage());
        }
        
        // 無効な成績のテスト
        IO.println("\n--- 無効な成績のテスト ---");
        try {
            service.updateGrade("S001", "英語", 5.0);
            IO.println("✗ 成績範囲チェックが機能していません");
        } catch (InvalidGradeException e) {
            IO.println("✓ 無効な成績を正しく検出: " + e.getMessage());
        } catch (StudentNotFoundException e) {
            IO.println("✗ 想定外のエラー: " + e.getMessage());
        }
        
        // CSVインポートのテスト
        IO.println("\n--- CSVインポートのテスト ---");
        
        // テスト用CSVファイルの作成
        try (PrintWriter writer = new PrintWriter("test_students.csv")) {
            writer.println("学生ID,氏名,年齢,学部");
            writer.println("S002,山田花子,21,理学部");
            writer.println("S003,佐藤次郎,22,工学部");
            writer.println("S001,重複太郎,20,文学部");  // 重複ID
            writer.println("S004,鈴木美咲,abc,文学部"); // 無効な年齢
            writer.println("S005,高橋健"); // データ不足
        } catch (IOException e) {
            IO.println("テストファイル作成エラー: " + e.getMessage());
        }
        
        try {
            StudentService.ImportResult result = service.importStudentsFromCSV("test_students.csv");
            IO.println("インポート結果: 成功=" + result.successCount()
                + "件, エラー=" + result.errorCount() + "件");
        } catch (IOException e) {
            IO.println("✗ インポートエラー: " + e.getMessage());
        }
        
        // 成績レポートの生成
        IO.println("\n--- 成績レポートの生成 ---");
        IO.println(service.generateGradeReport("S001"));
        IO.println("\n" + service.generateGradeReport("S999")); // 存在しない学生
        
        // CSVエクスポートのテスト
        IO.println("\n--- CSVエクスポートのテスト ---");
        try {
            service.exportToCSV("exported_students.csv");
            IO.println("✓ CSVエクスポートが完了しました");
            
            // エクスポートしたファイルの内容を確認
            IO.println("\nエクスポートされたデータ:");
            try (BufferedReader reader = new BufferedReader(new FileReader("exported_students.csv"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    IO.println("  " + line);
                }
            }
        } catch (IOException e) {
            IO.println("✗ エクスポートエラー: " + e.getMessage());
        }
        
        // 後片付け
        new File("test_students.csv").delete();
        new File("exported_students.csv").delete();
    }
}