package chapter07.solutions;

/**
 * 学生を表すクラス
 * 
 * Sortableインターフェイスを実装し、成績や名前で
 * ソートできるようにしています。
 */
public class Student implements Sortable {
    
    private String name;
    private int score;
    private String studentId;
    private SortCriteria sortCriteria;
    
    /**
     * ソート基準の列挙型
     */
    public enum SortCriteria {
        NAME,      // 名前順
        SCORE,     // 成績順
        STUDENT_ID // 学籍番号順
    }
    
    /**
     * 学生のコンストラクタ
     * 
     * @param name 学生名
     * @param score 成績
     * @param studentId 学籍番号
     */
    public Student(String name, int score, String studentId) {
        this.name = name;
        this.score = Math.max(0, Math.min(100, score)); // 0-100の範囲
        this.studentId = studentId;
        this.sortCriteria = SortCriteria.SCORE; // デフォルトは成績順
    }
    
    /**
     * ソート基準を設定する
     * 
     * @param criteria ソート基準
     */
    public void setSortCriteria(SortCriteria criteria) {
        this.sortCriteria = criteria;
    }
    
    /**
     * 他の学生との比較を行う
     * 
     * @param other 比較対象の学生
     * @return 比較結果
     */
    @Override
    public int compareTo(Sortable other) {
        if (!(other instanceof Student)) {
            throw new IllegalArgumentException("Student同士でのみ比較可能です");
        }
        
        Student otherStudent = (Student) other;
        
        switch (sortCriteria) {
            case NAME:
                return name.compareTo(otherStudent.name);
            case SCORE:
                // 成績は降順（高い順）
                return Integer.compare(otherStudent.score, score);
            case STUDENT_ID:
                return studentId.compareTo(otherStudent.studentId);
            default:
                return 0;
        }
    }
    
    /**
     * ソート用のキーを取得する
     * 
     * @return ソート用のキー
     */
    @Override
    public Comparable<?> getSortKey() {
        switch (sortCriteria) {
            case NAME:
                return name;
            case SCORE:
                return score;
            case STUDENT_ID:
                return studentId;
            default:
                return name;
        }
    }
    
    /**
     * ソートの方向を指定する
     * 
     * @return true: 昇順, false: 降順
     */
    @Override
    public boolean isAscending() {
        // 成績の場合は降順（高い順）
        return sortCriteria != SortCriteria.SCORE;
    }
    
    /**
     * ソートの優先度を取得する
     * 
     * @return ソート優先度
     */
    @Override
    public int getSortPriority() {
        switch (sortCriteria) {
            case SCORE:
                return 1; // 成績が最優先
            case NAME:
                return 2;
            case STUDENT_ID:
                return 3;
            default:
                return 0;
        }
    }
    
    /**
     * 成績によるグレードを取得する
     * 
     * @return グレード
     */
    public String getGrade() {
        if (score >= 90) return "A";
        if (score >= 80) return "B";
        if (score >= 70) return "C";
        if (score >= 60) return "D";
        return "F";
    }
    
    /**
     * 合格判定を行う
     * 
     * @return 合格かどうか
     */
    public boolean isPassing() {
        return score >= 60;
    }
    
    /**
     * 学生情報を文字列として取得する
     * 
     * @return 学生情報
     */
    @Override
    public String toString() {
        return String.format("Student{name='%s', score=%d, studentId='%s', grade='%s'}",
                           name, score, studentId, getGrade());
    }
    
    // Getters
    public String getName() { return name; }
    public int getScore() { return score; }
    public String getStudentId() { return studentId; }
    public SortCriteria getSortCriteria() { return sortCriteria; }
    
    // Setters
    public void setScore(int score) {
        this.score = Math.max(0, Math.min(100, score));
    }
}