// Phase6Test.java - フェーズ6のテスト（データベース統合）
import java.io.File;
import java.sql.SQLException;

public class Phase6Test {
    public static void main(String[] args) {
        System.out.println("=== フェーズ6: データベース統合のテスト ===\n");
        
        // テスト用データベースファイルの削除（クリーンスタート）
        File dbFile = new File("student_management.db");
        if (dbFile.exists()) {
            dbFile.delete();
            System.out.println("既存のデータベースを削除しました");
        }
        
        StudentService service = new StudentService();
        
        try {
            // 学生の登録とデータベース保存
            System.out.println("--- 学生の登録 ---");
            GradeableStudent student1 = new GradeableStudent("S001", "田中太郎", 20, "工学部");
            student1.addGrade("数学", 3.5);
            student1.addGrade("物理", 4.0);
            service.registerStudent(student1);
            System.out.println("✓ 学生S001を登録しました");
            
            GradeableStudent student2 = new GradeableStudent("S002", "山田花子", 21, "理学部");
            student2.addGrade("化学", 3.8);
            student2.addGrade("生物", 3.5);
            service.registerStudent(student2);
            System.out.println("✓ 学生S002を登録しました");
            
            // サービスを再起動してデータベースから読み込み
            System.out.println("\n--- サービス再起動とデータ永続性の確認 ---");
            service.shutdown();
            System.out.println("サービスをシャットダウンしました");
            
            // 新しいサービスインスタンスを作成（データベースから読み込み）
            StudentService newService = new StudentService();
            System.out.println("新しいサービスインスタンスを作成しました");
            
            // データが永続化されているか確認
            System.out.println("\n保存された学生数: " + newService.getAllStudents().size());
            
            // 成績レポートの生成
            System.out.println("\n--- 永続化されたデータの確認 ---");
            System.out.println(newService.generateGradeReport("S001"));
            
            // 成績の更新とデータベース同期
            System.out.println("\n--- 成績の更新 ---");
            newService.updateGrade("S001", "英語", 3.2);
            System.out.println("✓ S001に英語の成績を追加しました");
            
            // 学生の削除
            System.out.println("\n--- 学生の削除 ---");
            newService.deleteStudent("S002");
            System.out.println("✓ S002を削除しました");
            
            // 最終的な統計情報
            System.out.println("\n" + newService.getStatisticsReport());
            
            // トランザクションのテスト
            System.out.println("\n--- トランザクションのテスト ---");
            try {
                // 存在しない学生の削除を試みる
                newService.deleteStudent("S999");
            } catch (StudentNotFoundException e) {
                System.out.println("✓ 存在しない学生の削除を正しく検出: " + e.getMessage());
            }
            
            // 大学院生の保存テスト
            System.out.println("\n--- 大学院生の保存テスト ---");
            GraduateStudent gradStudent = new GraduateStudent("G001", "佐藤次郎", 24, 
                "工学部", "人工知能");
            gradStudent.setAdvisor("鈴木教授");
            // Note: GraduateStudentはGradeableStudentではないため、現在の実装では直接登録できません
            System.out.println("（注：現在の実装ではGraduateStudentの登録は未対応）");
            
            // クリーンアップ
            newService.shutdown();
            System.out.println("\n✓ すべてのテストが完了しました");
            
        } catch (Exception e) {
            System.out.println("✗ エラーが発生しました: " + e.getMessage());
            e.printStackTrace();
        }
    }
}