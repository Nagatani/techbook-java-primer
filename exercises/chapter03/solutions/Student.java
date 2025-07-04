/**
 * 第3章 演習問題1: Studentクラスの解答例
 * 
 * 【学習ポイント】
 * - コンストラクタの実装とオーバーロード
 * - thisキーワードの使い方
 * - コンストラクタチェーンの実装
 * - 適切な初期化処理
 * 
 * 【よくある間違いと対策】
 * 1. thisキーワードの使い忘れ
 * 2. コンストラクタ内でthis()を最初に呼び出さない
 * 3. 無限ループになるコンストラクタチェーン
 * 4. 適切な初期値の設定不足
 */
public class Student {
    // インスタンス変数（private推奨）
    private String name;
    private int age;
    private String studentId;
    
    // 【基本解答】デフォルトコンストラクタ
    public Student() {
        // thisキーワードでコンストラクタチェーンを実装
        this("未設定", 0, "未設定");
    }
    
    // 【基本解答】名前のみのコンストラクタ
    public Student(String name) {
        this(name, 0, "未設定");
    }
    
    // 【基本解答】名前と年齢のコンストラクタ
    public Student(String name, int age) {
        this(name, age, "未設定");
    }
    
    // 【基本解答】全項目のコンストラクタ（最も詳細）
    public Student(String name, int age, String studentId) {
        // thisキーワードで引数と変数を区別
        this.name = name;
        this.age = age;
        this.studentId = studentId;
    }
    
    // 【応用解答】年齢の妥当性チェック付きコンストラクタ
    public Student(String name, int age, String studentId, boolean validate) {
        this.name = name;
        this.age = validate ? Math.max(0, age) : age;  // 負の値は0に補正
        this.studentId = studentId;
    }
    
    // 【応用解答】コピーコンストラクタ
    public Student(Student other) {
        this(other.name, other.age, other.studentId);
    }
    
    // 情報表示メソッド
    public void displayInfo() {
        System.out.println("名前: " + name + ", 年齢: " + age + ", 学籍番号: " + studentId);
    }
    
    // 【発展解答】フォーマット付き情報表示
    public void displayFormattedInfo() {
        System.out.printf("学生情報 - 名前: %s, 年齢: %d歳, 学籍番号: %s%n", 
                         name, age, studentId);
    }
    
    // getter/setterメソッド（カプセル化の準備）
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public int getAge() {
        return age;
    }
    
    public void setAge(int age) {
        // 【発展解答】年齢の妥当性チェック
        if (age >= 0) {
            this.age = age;
        } else {
            System.out.println("警告: 年齢は0以上である必要があります。");
        }
    }
    
    public String getStudentId() {
        return studentId;
    }
    
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
    
    // 【発展解答】学生の成人判定メソッド
    public boolean isAdult() {
        return age >= 20;
    }
    
    // 【発展解答】学生情報の検証メソッド
    public boolean isValidStudent() {
        return name != null && !name.equals("未設定") && 
               age >= 0 && 
               studentId != null && !studentId.equals("未設定");
    }
    
    // 【発展解答】toString()メソッドのオーバーライド
    @Override
    public String toString() {
        return String.format("Student{name='%s', age=%d, studentId='%s'}", 
                           name, age, studentId);
    }
    
    // 【発展解答】equals()メソッドのオーバーライド
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Student student = (Student) obj;
        return age == student.age &&
               name.equals(student.name) &&
               studentId.equals(student.studentId);
    }
    
    // 【発展解答】hashCode()メソッドのオーバーライド
    @Override
    public int hashCode() {
        return name.hashCode() + age + studentId.hashCode();
    }
}