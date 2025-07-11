/**
 * リスト10-1
 * StudentManagerクラス
 * 
 * 元ファイル: chapter10-collections.md (29行目)
 */

// 学生の名前を管理するプログラム
public class StudentManager {
    public static void main(String[] args) {
        // 5人分の学生名を格納する配列
        String[] students = new String[5];
        students[0] = "田中";
        students[1] = "佐藤";
        students[2] = "鈴木";
        
        // 配列の内容を表示
        for (int i = 0; i < students.length; i++) {
            if (students[i] != null) {
                System.out.println("学生" + (i + 1) + ": " + students[i]);
            }
        }
    }
}