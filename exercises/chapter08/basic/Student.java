package chapter08.basic;

/**
 * 学生を表すクラス
 * 
 * 学生管理システムで使用する学生情報を保持します。
 */
public class Student {
    // 学生ID
    private String studentId;
    // 名前
    private String name;
    // 学年
    private int grade;
    // 成績（0.0〜100.0）
    private double score;
    
    /**
     * コンストラクタ
     * @param studentId 学生ID
     * @param name 名前
     * @param grade 学年
     * @param score 成績
     */
    public Student(String studentId, String name, int grade, double score) {
        // TODO: 各フィールドを初期化してください
        
    }
    
    /**
     * 学生情報を文字列で返す
     * TODO: 学生情報を見やすい形式で返してください
     * @return 学生情報の文字列
     */
    @Override
    public String toString() {
        // TODO: 実装してください
        // 例: "学生ID: S001, 名前: 山田太郎, 学年: 2, 成績: 85.5"
        return "";
    }
    
    /**
     * 学生IDが同じかどうかで等価性を判定
     * TODO: equalsメソッドを適切にオーバーライドしてください
     */
    @Override
    public boolean equals(Object obj) {
        // TODO: 実装してください
        // ヒント: 
        // 1. 同じオブジェクトか確認
        // 2. nullチェック
        // 3. 型チェック
        // 4. studentIdで比較
        return false;
    }
    
    /**
     * ハッシュコードを返す
     * TODO: equalsと整合性のあるhashCodeを実装してください
     */
    @Override
    public int hashCode() {
        // TODO: 実装してください
        // ヒント: studentIdのhashCodeを使用
        return 0;
    }
    
    // ゲッターメソッド
    public String getStudentId() {
        return studentId;
    }
    
    public String getName() {
        return name;
    }
    
    public int getGrade() {
        return grade;
    }
    
    public double getScore() {
        return score;
    }
    
    // セッターメソッド
    public void setScore(double score) {
        this.score = score;
    }
}