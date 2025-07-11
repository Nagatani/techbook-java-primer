import java.util.*;
import java.util.stream.*;

/**
 * 第10章 Stream API - 基本演習
 * 学生データ分析クラス
 */
public class StudentAnalyzer {
    private List<Student> students;
    
    public StudentAnalyzer(List<Student> students) {
        this.students = students;
    }
    
    /**
     * 1. すべての学生の名前をリストで返す
     */
    public List<String> getAllNames() {
        // TODO: Streamを使用して実装
        // ヒント: map()メソッドを使用
        return new ArrayList<>();
    }
    
    /**
     * 2. 指定されたGPA以上の学生をフィルタリング
     */
    public List<Student> filterByGPA(double minGPA) {
        // TODO: Streamを使用して実装
        // ヒント: filter()メソッドを使用
        return new ArrayList<>();
    }
    
    /**
     * 3. 学部ごとに学生をグループ化
     */
    public Map<String, List<Student>> groupByDepartment() {
        // TODO: Streamを使用して実装
        // ヒント: Collectors.groupingBy()を使用
        return new HashMap<>();
    }
    
    /**
     * 4. 全学生の平均GPAを計算
     */
    public double calculateAverageGPA() {
        // TODO: Streamを使用して実装
        // ヒント: mapToDouble()とaverage()を使用
        return 0.0;
    }
    
    /**
     * 5. GPAの降順でソートして上位N人を取得
     */
    public List<Student> getTopStudents(int n) {
        // TODO: Streamを使用して実装
        // ヒント: sorted()とlimit()を使用
        return new ArrayList<>();
    }
}