// Phase6Test.java - フェーズ6のテスト（データベース統合）
// GUIが使うstudent_management.dbを消さないように、テスト用のファイルを使う
import java.io.File;

public class Phase6Test {
    private static final String TEST_DB_FILE = "test_student_management.db";
    private static final String TEST_DB_URL = "jdbc:sqlite:" + TEST_DB_FILE;
    
    public static void main(String[] args) {
        IO.println("=== フェーズ6: データベース統合のテスト ===\n");
        
        // テスト用データベースファイルの削除（クリーンスタート）
        File dbFile = new File(TEST_DB_FILE);
        if (dbFile.exists()) {
            dbFile.delete();
            IO.println("前回のテスト用データベースを削除しました");
        }
        
        try {
            StudentService service = new StudentService(TEST_DB_URL);
            
            // 学生の登録とデータベース保存
            IO.println("--- 学生の登録 ---");
            GradeableStudent student1 = new GradeableStudent("S001", "田中太郎", 20, "工学部");
            student1.addGrade("数学", 3.5);
            student1.addGrade("物理", 4.0);
            service.registerStudent(student1);
            IO.println("✓ 学生S001を登録しました");
            
            GradeableStudent student2 = new GradeableStudent("S002", "山田花子", 21, "理学部");
            student2.addGrade("化学", 3.8);
            student2.addGrade("生物", 3.5);
            service.registerStudent(student2);
            IO.println("✓ 学生S002を登録しました");
            
            // サービスを再起動してデータベースから読み込み
            IO.println("\n--- サービス再起動とデータ永続性の確認 ---");
            service.shutdown();
            IO.println("サービスをシャットダウンしました");
            
            // 新しいサービスインスタンスを作成（データベースから読み込み）
            StudentService newService = new StudentService(TEST_DB_URL);
            IO.println("新しいサービスインスタンスを作成しました");
            
            // データが永続化されているか確認
            IO.println("\n保存された学生数: " + newService.getAllStudents().size());
            
            // 成績レポートの生成
            IO.println("\n--- 永続化されたデータの確認 ---");
            IO.println(newService.generateGradeReport("S001"));
            
            // 成績の更新とデータベース同期
            IO.println("\n--- 成績の更新と削除 ---");
            newService.updateGrade("S001", "英語", 3.2);
            IO.println("✓ S001に英語の成績を追加しました");
            newService.removeGrade("S001", "物理");
            IO.println("✓ S001の物理の成績を削除しました");
            
            // 学生の削除（トランザクションで成績と学生をまとめて削除する）
            IO.println("\n--- 学生の削除 ---");
            newService.deleteStudent("S002");
            IO.println("✓ S002を削除しました");
            
            // 存在しない学生の削除
            IO.println("\n--- 存在しない学生の削除 ---");
            try {
                newService.deleteStudent("S999");
            } catch (StudentNotFoundException e) {
                IO.println("✓ 存在しない学生の削除を正しく検出: " + e.getMessage());
            }
            
            // もう一度読み込み直して、更新と削除が保存されたことを確かめる
            newService.shutdown();
            StudentService reloaded = new StudentService(TEST_DB_URL);
            IO.println("\n--- 読み込み直した結果 ---");
            IO.println(reloaded.generateGradeReport("S001"));
            IO.println("\n" + reloaded.getStatisticsReport());
            
            // クリーンアップ
            reloaded.shutdown();
            dbFile.delete();
            IO.println("✓ すべてのテストが完了しました");
            
        } catch (Exception e) {
            IO.println("✗ エラーが発生しました: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
