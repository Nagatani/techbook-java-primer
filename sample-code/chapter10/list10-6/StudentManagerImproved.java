/**
 * リスト10-6
 * StudentManagerImprovedクラス
 * 
 * 元ファイル: chapter10-collections.md (126行目)
 */

import java.util.ArrayList;
import java.util.List;

public class StudentManagerImproved {
    public static void main(String[] args) {
        // 動的に拡張可能なリスト
        List<String> students = new ArrayList<>();
        
        // 要素の追加（サイズを気にしない）
        students.add("田中");
        students.add("佐藤");
        students.add("鈴木");
        students.add("高橋");
        students.add("伊藤");
        students.add("山田");  // 6人目も問題なく追加
        
        // 要素の削除（自動的に詰められる）
        students.remove("鈴木");  // 簡単に削除
        
        // 便利なメソッド
        System.out.println("学生数: " + students.size());
        System.out.println("田中さんは在籍？ " + students.contains("田中"));
        
        // 拡張for文もそのまま使える
        for (String student : students) {
            System.out.println(student);
        }
    }
}