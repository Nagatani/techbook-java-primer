/**
 * Studentを試すプログラム（課題4の解答例）
 */
public class StudentTest {
    public static void main(String[] args) {
        IO.println("=== 学生管理システム ===");
        
        // デフォルトコンストラクターを使って学生オブジェクトを作成する
        Student student1 = new Student();
        IO.print("学生1: ");
        student1.showInfo();
        
        // 名前のみを設定するコンストラクターを使って学生オブジェクトを作成する
        Student student2 = new Student("田中太郎");
        IO.print("学生2: ");
        student2.showInfo();
        
        // 全ての項目を設定するコンストラクターを使って学生オブジェクトを作成する
        Student student3 = new Student("佐藤花子", 20, "S2024001");
        IO.print("学生3: ");
        student3.showInfo();
    }
}