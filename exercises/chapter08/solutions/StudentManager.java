package chapter08.solutions;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 学生管理システムのクラス
 * 
 * 学生の登録、検索、成績管理などを行います。
 * 適切なコレクションクラスを選択して効率的な操作を実現します。
 */
public class StudentManager {
    
    /**
     * 学生を表すクラス
     */
    public static class Student {
        private String id;
        private String name;
        private int score;
        private String department;
        
        public Student(String id, String name, int score, String department) {
            this.id = id;
            this.name = name;
            this.score = Math.max(0, Math.min(100, score));
            this.department = department;
        }
        
        // Getters and Setters
        public String getId() { return id; }
        public String getName() { return name; }
        public int getScore() { return score; }
        public String getDepartment() { return department; }
        
        public void setScore(int score) {
            this.score = Math.max(0, Math.min(100, score));
        }
        
        public String getGrade() {
            if (score >= 90) return "A";
            if (score >= 80) return "B";
            if (score >= 70) return "C";
            if (score >= 60) return "D";
            return "F";
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Student student = (Student) obj;
            return Objects.equals(id, student.id);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
        
        @Override
        public String toString() {
            return String.format("Student{id='%s', name='%s', score=%d, department='%s', grade='%s'}",
                               id, name, score, department, getGrade());
        }
    }
    
    // 学生をIDでインデックス化（高速検索用）
    private Map<String, Student> studentById;
    
    // 学科別の学生リスト
    private Map<String, List<Student>> studentsByDepartment;
    
    // 成績順の学生リスト（TreeSetで自動ソート）
    private TreeSet<Student> studentsByScore;
    
    // 全学生のリスト（登録順）
    private List<Student> allStudents;
    
    /**
     * StudentManagerのコンストラクタ
     */
    public StudentManager() {
        studentById = new HashMap<>();
        studentsByDepartment = new HashMap<>();
        studentsByScore = new TreeSet<>(
            Comparator.comparingInt(Student::getScore)
                     .reversed()
                     .thenComparing(Student::getName)
        );
        allStudents = new ArrayList<>();
    }
    
    /**
     * 学生を登録する
     * 
     * @param student 登録する学生
     * @return 登録に成功した場合true
     */
    public boolean addStudent(Student student) {
        if (student == null || studentById.containsKey(student.getId())) {
            return false;
        }
        
        // 各コレクションに追加
        studentById.put(student.getId(), student);
        allStudents.add(student);
        studentsByScore.add(student);
        
        // 学科別リストに追加
        studentsByDepartment.computeIfAbsent(student.getDepartment(), k -> new ArrayList<>())
                           .add(student);
        
        return true;
    }
    
    /**
     * 学生を削除する
     * 
     * @param studentId 削除する学生のID
     * @return 削除に成功した場合true
     */
    public boolean removeStudent(String studentId) {
        Student student = studentById.remove(studentId);
        if (student == null) {
            return false;
        }
        
        // 各コレクションから削除
        allStudents.remove(student);
        studentsByScore.remove(student);
        
        List<Student> departmentStudents = studentsByDepartment.get(student.getDepartment());
        if (departmentStudents != null) {
            departmentStudents.remove(student);
            if (departmentStudents.isEmpty()) {
                studentsByDepartment.remove(student.getDepartment());
            }
        }
        
        return true;
    }
    
    /**
     * IDで学生を検索する
     * 
     * @param studentId 学生ID
     * @return 見つかった学生（存在しない場合null）
     */
    public Student findStudentById(String studentId) {
        return studentById.get(studentId);
    }
    
    /**
     * 名前で学生を検索する
     * 
     * @param name 学生名
     * @return 見つかった学生のリスト
     */
    public List<Student> findStudentsByName(String name) {
        return allStudents.stream()
                         .filter(student -> student.getName().contains(name))
                         .collect(Collectors.toList());
    }
    
    /**
     * 学科で学生を検索する
     * 
     * @param department 学科名
     * @return 該当学科の学生リスト
     */
    public List<Student> findStudentsByDepartment(String department) {
        return studentsByDepartment.getOrDefault(department, new ArrayList<>());
    }
    
    /**
     * 成績範囲で学生を検索する
     * 
     * @param minScore 最低点
     * @param maxScore 最高点
     * @return 該当する学生のリスト
     */
    public List<Student> findStudentsByScoreRange(int minScore, int maxScore) {
        return allStudents.stream()
                         .filter(student -> student.getScore() >= minScore && 
                                          student.getScore() <= maxScore)
                         .collect(Collectors.toList());
    }
    
    /**
     * 成績順で学生を取得する
     * 
     * @return 成績順の学生リスト
     */
    public List<Student> getStudentsByScore() {
        return new ArrayList<>(studentsByScore);
    }
    
    /**
     * 上位N人の学生を取得する
     * 
     * @param n 取得する人数
     * @return 上位N人の学生リスト
     */
    public List<Student> getTopStudents(int n) {
        return studentsByScore.stream()
                             .limit(n)
                             .collect(Collectors.toList());
    }
    
    /**
     * 学科別の平均点を計算する
     * 
     * @return 学科別平均点のマップ
     */
    public Map<String, Double> calculateAverageScoreByDepartment() {
        return studentsByDepartment.entrySet().stream()
                                  .collect(Collectors.toMap(
                                      Map.Entry::getKey,
                                      entry -> entry.getValue().stream()
                                                   .mapToInt(Student::getScore)
                                                   .average()
                                                   .orElse(0.0)
                                  ));
    }
    
    /**
     * グレード別の学生数を計算する
     * 
     * @return グレード別学生数のマップ
     */
    public Map<String, Long> countStudentsByGrade() {
        return allStudents.stream()
                         .collect(Collectors.groupingBy(
                             Student::getGrade,
                             Collectors.counting()
                         ));
    }
    
    /**
     * 学生の成績を更新する
     * 
     * @param studentId 学生ID
     * @param newScore 新しい成績
     * @return 更新に成功した場合true
     */
    public boolean updateStudentScore(String studentId, int newScore) {
        Student student = studentById.get(studentId);
        if (student == null) {
            return false;
        }
        
        // TreeSetから一旦削除してから再追加（ソート順を更新するため）
        studentsByScore.remove(student);
        student.setScore(newScore);
        studentsByScore.add(student);
        
        return true;
    }
    
    /**
     * 全学生数を取得する
     * 
     * @return 全学生数
     */
    public int getTotalStudentCount() {
        return allStudents.size();
    }
    
    /**
     * 全学科名を取得する
     * 
     * @return 全学科名のセット
     */
    public Set<String> getAllDepartments() {
        return new HashSet<>(studentsByDepartment.keySet());
    }
    
    /**
     * 全学生のリストを取得する
     * 
     * @return 全学生のリスト（登録順）
     */
    public List<Student> getAllStudents() {
        return new ArrayList<>(allStudents);
    }
    
    /**
     * 統計情報を取得する
     * 
     * @return 統計情報の文字列
     */
    public String getStatistics() {
        if (allStudents.isEmpty()) {
            return "登録された学生はいません。";
        }
        
        double averageScore = allStudents.stream()
                                       .mapToInt(Student::getScore)
                                       .average()
                                       .orElse(0.0);
        
        int maxScore = allStudents.stream()
                                 .mapToInt(Student::getScore)
                                 .max()
                                 .orElse(0);
        
        int minScore = allStudents.stream()
                                 .mapToInt(Student::getScore)
                                 .min()
                                 .orElse(0);
        
        return String.format(
            "===== 学生管理システム統計 =====\n" +
            "総学生数: %d人\n" +
            "学科数: %d学科\n" +
            "平均点: %.2f点\n" +
            "最高点: %d点\n" +
            "最低点: %d点\n" +
            "================================",
            getTotalStudentCount(),
            getAllDepartments().size(),
            averageScore,
            maxScore,
            minScore
        );
    }
}