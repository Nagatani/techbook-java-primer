/**
 * リスト3-37
 * GradeCalculatorクラス
 * 
 * 元ファイル: chapter03-oop-basics.md (1804行目)
 */

public class GradeCalculator {
    public static void main(String[] args) {
        // テストケースとして複数の成績を評価
        char[] grades = {'A', 'B', 'C', 'D', 'F', 'B'};
        int totalGradePoints = 0;
        int courseCount = grades.length;
        
        System.out.println("=== 成績評価システム ===");
        System.out.println("履修科目数: " + courseCount + "科目");
        System.out.println();
        
        for (int i = 0; i < grades.length; i++) {
            char grade = grades[i];
            int gradePoint;
            String evaluation;
            
            // switch文による成績の詳細評価
            switch (grade) {
                case 'A':
                    gradePoint = 4;
                    evaluation = "優秀（Excellent）";
                    break;
                case 'B':
                    gradePoint = 3;
                    evaluation = "良好（Good）";
                    break;
                case 'C':
                    gradePoint = 2;
                    evaluation = "普通（Average）";
                    break;
                case 'D':
                    gradePoint = 1;
                    evaluation = "要努力（Below Average）";
                    break;
                case 'F':
                    gradePoint = 0;
                    evaluation = "不合格（Fail）";
                    break;
                default:
                    gradePoint = 0;
                    evaluation = "無効な成績";
                    System.out.println("警告: 無効な成績 '" + grade + "' が検出されました");
                    break;
            }
            
            totalGradePoints += gradePoint;
            System.out.println("科目" + (i + 1) + ": " + grade + "(" + gradePoint + "点) - " + evaluation);
        }
        
        System.out.println();
        
        // GPA計算と総合評価
        double gpa = (double) totalGradePoints / courseCount;
        System.out.println("総ポイント: " + totalGradePoints + "点");
        System.out.printf("GPA: %.2f\n", gpa);
        
        // GPAにもとづく総合評価（switch文の応用）
        int gpaCategory = (int) gpa; // 小数点以下切り捨て
        
        System.out.print("総合評価: ");
        switch (gpaCategory) {
            case 4:
                System.out.println("最優秀（Summa Cum Laude）- 学長表彰対象");
                break;
            case 3:
                System.out.println("優秀（Magna Cum Laude）- 学部長表彰対象");
                break;
            case 2:
                System.out.println("良好（Cum Laude）- 追加指導不要");
                break;
            case 1:
                System.out.println("要注意（Academic Warning）- 学習指導が必要");
                break;
            case 0:
                System.out.println("要改善（Academic Probation）- 緊急学習支援が必要");
                break;
            default:
                System.out.println("計算エラー");
                break;
        }
        
        // 複数caseラベルの活用例（奨学金適用条件）
        System.out.print("奨学金適用: ");
        switch (gpaCategory) {
            case 4:
            case 3:
                System.out.println("特待生奨学金適用対象");
                break;
            case 2:
                System.out.println("一般奨学金適用対象");
                break;
            default:
                System.out.println("奨学金適用対象外");
                break;
        }
    }
}