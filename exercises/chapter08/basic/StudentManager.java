package chapter08.basic;

import java.util.*;

/**
 * 学生管理システム
 * 
 * ArrayListとHashMapを使用して学生情報を管理します。
 */
public class StudentManager {
    // 学生のリスト（ArrayList）
    private List<Student> studentList;
    // 学生IDをキーとするマップ（HashMap）
    private Map<String, Student> studentMap;
    
    /**
     * コンストラクタ
     */
    public StudentManager() {
        // TODO: studentListとstudentMapを初期化してください
        
    }
    
    /**
     * 学生を追加する
     * TODO: リストとマップの両方に学生を追加してください
     * @param student 追加する学生
     */
    public void addStudent(Student student) {
        // TODO: 実装してください
        // 注意: 重複チェックを行ってください
    }
    
    /**
     * 学生IDで学生を検索する
     * TODO: マップを使用して効率的に検索してください
     * @param studentId 学生ID
     * @return 見つかった学生、見つからない場合はnull
     */
    public Student findStudentById(String studentId) {
        // TODO: 実装してください
        return null;
    }
    
    /**
     * 全学生の一覧を表示する
     * TODO: リストを使用して全学生を表示してください
     */
    public void displayAllStudents() {
        System.out.println("=== 学生一覧 ===");
        // TODO: 実装してください
    }
    
    /**
     * 成績順に学生をソートして表示する
     * TODO: Comparatorを使用して成績の降順でソートしてください
     */
    public void displayStudentsByScore() {
        System.out.println("=== 成績順学生一覧 ===");
        // TODO: 実装してください
        // ヒント: Collections.sort()またはList.sort()を使用
    }
    
    /**
     * 特定の学年の学生だけを取得する
     * TODO: リストをフィルタリングして特定学年の学生を返してください
     * @param grade 学年
     * @return 指定学年の学生リスト
     */
    public List<Student> getStudentsByGrade(int grade) {
        List<Student> result = new ArrayList<>();
        // TODO: 実装してください
        return result;
    }
    
    /**
     * 平均成績を計算する
     * TODO: 全学生の平均成績を計算してください
     * @return 平均成績
     */
    public double calculateAverageScore() {
        // TODO: 実装してください
        return 0.0;
    }
    
    /**
     * 学生を削除する
     * TODO: リストとマップの両方から学生を削除してください
     * @param studentId 削除する学生のID
     * @return 削除に成功した場合true
     */
    public boolean removeStudent(String studentId) {
        // TODO: 実装してください
        return false;
    }
    
    /**
     * 学生数を取得する
     * @return 学生数
     */
    public int getStudentCount() {
        return studentList.size();
    }
}