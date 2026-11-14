/**
 * 学生を表すクラス（課題4の解答例）
 */
public class Student {
    // インスタンス変数を宣言する
    String name;
    int age;
    String studentId;
    
    // デフォルトコンストラクター
    public Student() {
        this("未設定", 0, "未設定");
    }
    
    // 名前のみを設定するコンストラクター
    public Student(String name) {
        this(name, 0, "未設定");
    }
    
    // 全ての項目を設定するコンストラクター
    public Student(String name, int age, String studentId) {
        this.name = name;
        this.age = age;
        this.studentId = studentId;
    }
    
    // 情報表示メソッド
    public void showInfo() {
        IO.println("名前: " + name + ", 年齢: " + age + ", 学籍番号: " + studentId);
    }
}