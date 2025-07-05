/**
 * 第2章 応用課題2: 成績管理プログラム
 * 
 * 学生の成績を管理するシステムの実装。
 * 複数クラスの連携と統計計算の実践を学習します。
 * 
 * 学習ポイント:
 * - ArrayListを使った複数データの管理
 * - 成績計算ロジックの実装
 * - オブジェクト指向的な設計
 * - 統計情報の算出
 * - クラス間の連携実装
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 学生の成績を管理するクラス
 */
class Student {
    private String studentId;
    private String name;
    private HashMap<String, Integer> scores; // 科目名と成績のマップ
    private double gpa; // Grade Point Average
    
    /**
     * 学生のコンストラクタ
     */
    public Student(String studentId, String name) {
        this.studentId = studentId;
        this.name = name;
        this.scores = new HashMap<>();
        this.gpa = 0.0;
    }
    
    /**
     * 科目の成績を追加します
     */
    public void addScore(String subject, int score) {
        if (score >= 0 && score <= 100) {
            scores.put(subject, score);
            updateGPA();
        } else {
            System.out.println("エラー: 成績は0-100の範囲で入力してください");
        }
    }
    
    /**
     * 複数科目の成績を一括追加します
     */
    public void addScores(String[] subjects, int[] scoreValues) {
        if (subjects.length != scoreValues.length) {
            System.out.println("エラー: 科目数と成績数が一致しません");
            return;
        }
        
        for (int i = 0; i < subjects.length; i++) {
            addScore(subjects[i], scoreValues[i]);
        }
    }
    
    /**
     * 平均点を計算します
     */
    public double getAverage() {
        if (scores.isEmpty()) {
            return 0.0;
        }
        
        int sum = 0;
        for (int score : scores.values()) {
            sum += score;
        }
        return (double) sum / scores.size();
    }
    
    /**
     * 最高点を取得します
     */
    public int getMaxScore() {
        if (scores.isEmpty()) {
            return 0;
        }
        
        int max = 0;
        for (int score : scores.values()) {
            if (score > max) {
                max = score;
            }
        }
        return max;
    }
    
    /**
     * 最低点を取得します
     */
    public int getMinScore() {
        if (scores.isEmpty()) {
            return 0;
        }
        
        int min = 100;
        for (int score : scores.values()) {
            if (score < min) {
                min = score;
            }
        }
        return min;
    }
    
    /**
     * 平均点から成績評価を取得します
     */
    public String getGrade() {
        double avg = getAverage();
        return getGradeFromScore(avg);
    }
    
    /**
     * 点数から成績評価を取得します
     */
    public static String getGradeFromScore(double score) {
        if (score >= 90) return "A";
        else if (score >= 80) return "B";
        else if (score >= 70) return "C";
        else if (score >= 60) return "D";
        else return "F";
    }
    
    /**
     * GPAを更新します
     */
    private void updateGPA() {
        if (scores.isEmpty()) {
            gpa = 0.0;
            return;
        }
        
        double totalPoints = 0.0;
        for (int score : scores.values()) {
            totalPoints += scoreToGradePoint(score);
        }
        gpa = totalPoints / scores.size();
    }
    
    /**
     * 点数をGPAポイントに変換します
     */
    private double scoreToGradePoint(int score) {
        if (score >= 90) return 4.0;
        else if (score >= 80) return 3.0;
        else if (score >= 70) return 2.0;
        else if (score >= 60) return 1.0;
        else return 0.0;
    }
    
    /**
     * 特定科目の成績を取得します
     */
    public int getScoreBySubject(String subject) {
        return scores.getOrDefault(subject, 0);
    }
    
    /**
     * 履修科目数を取得します
     */
    public int getSubjectCount() {
        return scores.size();
    }
    
    /**
     * 合格科目数を取得します（60点以上）
     */
    public int getPassedSubjectCount() {
        int count = 0;
        for (int score : scores.values()) {
            if (score >= 60) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * 学生の成績レポートを表示します
     */
    public void displayReport() {
        System.out.println("学生: " + name + " (" + studentId + ")");
        System.out.println("科目別成績:");
        
        for (Map.Entry<String, Integer> entry : scores.entrySet()) {
            String subject = entry.getKey();
            int score = entry.getValue();
            String grade = getGradeFromScore(score);
            System.out.printf("- %s: %d点 (%s)%n", subject, score, grade);
        }
        
        System.out.printf("平均点: %.1f点 (%s)%n", getAverage(), getGrade());
        System.out.printf("GPA: %.2f%n", gpa);
        System.out.printf("履修科目: %d科目 (合格: %d科目)%n", 
                         getSubjectCount(), getPassedSubjectCount());
        System.out.println();
    }
    
    /**
     * 詳細な学習分析を表示します
     */
    public void displayDetailedAnalysis() {
        System.out.println("=== 詳細学習分析: " + name + " ===");
        
        // 成績分布
        int[] gradeDistribution = new int[5]; // A, B, C, D, F
        for (int score : scores.values()) {
            String grade = getGradeFromScore(score);
            switch (grade) {
                case "A": gradeDistribution[0]++; break;
                case "B": gradeDistribution[1]++; break;
                case "C": gradeDistribution[2]++; break;
                case "D": gradeDistribution[3]++; break;
                case "F": gradeDistribution[4]++; break;
            }
        }
        
        System.out.println("成績分布:");
        String[] gradeLabels = {"A", "B", "C", "D", "F"};
        for (int i = 0; i < gradeLabels.length; i++) {
            if (gradeDistribution[i] > 0) {
                System.out.printf("  %s: %d科目%n", gradeLabels[i], gradeDistribution[i]);
            }
        }
        
        // 強み・弱み分析
        System.out.println("\n学習分析:");
        double avg = getAverage();
        if (avg >= 85) {
            System.out.println("  優秀な成績です！この調子で頑張ってください。");
        } else if (avg >= 75) {
            System.out.println("  良好な成績です。更なる向上を目指しましょう。");
        } else if (avg >= 65) {
            System.out.println("  平均的な成績です。苦手分野の克服に取り組みましょう。");
        } else {
            System.out.println("  成績向上が必要です。計画的な学習をお勧めします。");
        }
        
        // 最高・最低科目
        String bestSubject = "";
        String worstSubject = "";
        int maxScore = 0;
        int minScore = 100;
        
        for (Map.Entry<String, Integer> entry : scores.entrySet()) {
            if (entry.getValue() > maxScore) {
                maxScore = entry.getValue();
                bestSubject = entry.getKey();
            }
            if (entry.getValue() < minScore) {
                minScore = entry.getValue();
                worstSubject = entry.getKey();
            }
        }
        
        if (!bestSubject.isEmpty()) {
            System.out.printf("  得意科目: %s (%d点)%n", bestSubject, maxScore);
            System.out.printf("  要改善科目: %s (%d点)%n", worstSubject, minScore);
        }
        
        System.out.println();
    }
    
    // ゲッターメソッド
    public String getStudentId() { return studentId; }
    public String getName() { return name; }
    public HashMap<String, Integer> getScores() { return new HashMap<>(scores); }
    public double getGPA() { return gpa; }
}

/**
 * クラス全体の成績を管理するクラス
 */
class GradeBook {
    private String className;
    private ArrayList<Student> students;
    private String[] subjects;
    
    /**
     * 成績簿のコンストラクタ
     */
    public GradeBook(String className) {
        this.className = className;
        this.students = new ArrayList<>();
        this.subjects = new String[]{"数学", "英語", "理科", "国語", "社会"};
    }
    
    /**
     * カスタム科目を設定します
     */
    public void setSubjects(String[] subjects) {
        this.subjects = subjects.clone();
    }
    
    /**
     * 学生を追加します
     */
    public void addStudent(Student student) {
        students.add(student);
        System.out.println("✓ 学生ID: " + student.getStudentId() + ", 名前: " + student.getName());
    }
    
    /**
     * 複数の学生を一括追加します
     */
    public void addStudents(Student[] newStudents) {
        System.out.println("学生を登録しています...");
        for (Student student : newStudents) {
            addStudent(student);
        }
        System.out.println();
    }
    
    /**
     * 学生IDで学生を検索します
     */
    public Student findStudentById(String studentId) {
        for (Student student : students) {
            if (student.getStudentId().equals(studentId)) {
                return student;
            }
        }
        return null;
    }
    
    /**
     * 名前で学生を検索します
     */
    public Student findStudentByName(String name) {
        for (Student student : students) {
            if (student.getName().equals(name)) {
                return student;
            }
        }
        return null;
    }
    
    /**
     * クラス全体の平均点を計算します
     */
    public double getClassAverage() {
        if (students.isEmpty()) {
            return 0.0;
        }
        
        double sum = 0.0;
        for (Student student : students) {
            sum += student.getAverage();
        }
        return sum / students.size();
    }
    
    /**
     * 科目別のクラス平均を計算します
     */
    public double getSubjectAverage(String subject) {
        if (students.isEmpty()) {
            return 0.0;
        }
        
        int sum = 0;
        int count = 0;
        
        for (Student student : students) {
            int score = student.getScoreBySubject(subject);
            if (score > 0) { // 0は未履修として扱う
                sum += score;
                count++;
            }
        }
        
        return count > 0 ? (double) sum / count : 0.0;
    }
    
    /**
     * 最高得点と学生を取得します
     */
    public String getTopPerformer() {
        if (students.isEmpty()) {
            return "該当者なし";
        }
        
        Student topStudent = students.get(0);
        double topScore = topStudent.getAverage();
        
        for (Student student : students) {
            if (student.getAverage() > topScore) {
                topScore = student.getAverage();
                topStudent = student;
            }
        }
        
        return topStudent.getName() + " (平均: " + String.format("%.1f", topScore) + "点)";
    }
    
    /**
     * 成績分布を計算します
     */
    public Map<String, Integer> getGradeDistribution() {
        Map<String, Integer> distribution = new HashMap<>();
        distribution.put("A", 0);
        distribution.put("B", 0);
        distribution.put("C", 0);
        distribution.put("D", 0);
        distribution.put("F", 0);
        
        for (Student student : students) {
            String grade = student.getGrade();
            distribution.put(grade, distribution.get(grade) + 1);
        }
        
        return distribution;
    }
    
    /**
     * 個人成績レポートを表示します
     */
    public void displayIndividualReports() {
        System.out.println("=== 個人成績レポート ===");
        System.out.println();
        
        for (Student student : students) {
            student.displayReport();
        }
    }
    
    /**
     * クラス統計を表示します
     */
    public void displayClassStatistics() {
        System.out.println("=== クラス統計 ===");
        System.out.println("クラス名: " + className);
        System.out.println("登録学生数: " + students.size() + "名");
        System.out.printf("クラス平均: %.1f点%n", getClassAverage());
        
        // 科目別平均
        System.out.println("\n科目別平均:");
        for (String subject : subjects) {
            double avg = getSubjectAverage(subject);
            System.out.printf("- %s: %.1f点%n", subject, avg);
        }
        
        System.out.println("\n最優秀者: " + getTopPerformer());
        
        // 成績分布
        Map<String, Integer> distribution = getGradeDistribution();
        System.out.println("\n成績分布:");
        for (Map.Entry<String, Integer> entry : distribution.entrySet()) {
            String grade = entry.getKey();
            int count = entry.getValue();
            double percentage = students.size() > 0 ? (double) count / students.size() * 100 : 0;
            System.out.printf("%s: %d名 (%.1f%%)%n", grade, count, percentage);
        }
        
        System.out.println();
    }
    
    /**
     * 詳細なクラス分析レポートを表示します
     */
    public void displayDetailedAnalysis() {
        System.out.println("=== 詳細クラス分析 ===");
        
        // 科目別統計
        System.out.println("科目別詳細統計:");
        for (String subject : subjects) {
            int max = 0, min = 100, sum = 0, count = 0;
            
            for (Student student : students) {
                int score = student.getScoreBySubject(subject);
                if (score > 0) {
                    max = Math.max(max, score);
                    min = Math.min(min, score);
                    sum += score;
                    count++;
                }
            }
            
            if (count > 0) {
                double avg = (double) sum / count;
                System.out.printf("  %s: 平均%.1f点 (最高%d点, 最低%d点)%n", 
                                 subject, avg, max, min);
            }
        }
        
        // GPA分析
        double totalGPA = 0.0;
        for (Student student : students) {
            totalGPA += student.getGPA();
        }
        double avgGPA = students.size() > 0 ? totalGPA / students.size() : 0.0;
        System.out.printf("\nクラス平均GPA: %.2f%n", avgGPA);
        
        // 履修状況
        int totalSubjects = 0;
        int passedSubjects = 0;
        
        for (Student student : students) {
            totalSubjects += student.getSubjectCount();
            passedSubjects += student.getPassedSubjectCount();
        }
        
        double passRate = totalSubjects > 0 ? (double) passedSubjects / totalSubjects * 100 : 0;
        System.out.printf("総合合格率: %.1f%% (%d/%d科目)%n", passRate, passedSubjects, totalSubjects);
        
        System.out.println();
    }
    
    // ゲッターメソッド
    public String getClassName() { return className; }
    public int getStudentCount() { return students.size(); }
    public String[] getSubjects() { return subjects.clone(); }
}

/**
 * メインクラス - 成績管理システムのデモンストレーション
 */
public class GradeManagement {
    public static void main(String[] args) {
        // 成績簿を作成
        GradeBook gradeBook = new GradeBook("プログラミング基礎クラス");
        
        System.out.println("=== 成績管理システム ===");
        System.out.println();
        System.out.println(gradeBook.getClassName());
        System.out.println();
        
        // 学生を作成して追加
        Student[] students = {
            new Student("S001", "山田太郎"),
            new Student("S002", "佐藤花子"),
            new Student("S003", "田中次郎"),
            new Student("S004", "鈴木美咲"),
            new Student("S005", "高橋研二")
        };
        
        gradeBook.addStudents(students);
        
        // 成績を登録
        System.out.println("成績を登録しています...");
        students[0].addScores(new String[]{"数学", "英語", "理科"}, new int[]{85, 78, 92});
        students[1].addScores(new String[]{"数学", "英語", "理科"}, new int[]{94, 89, 87});
        students[2].addScores(new String[]{"数学", "英語", "理科"}, new int[]{76, 82, 79});
        students[3].addScores(new String[]{"数学", "英語", "理科"}, new int[]{88, 91, 85});
        students[4].addScores(new String[]{"数学", "英語", "理科"}, new int[]{72, 68, 74});
        
        System.out.println("山田太郎: 数学=85, 英語=78, 理科=92");
        System.out.println("佐藤花子: 数学=94, 英語=89, 理科=87");
        System.out.println("田中次郎: 数学=76, 英語=82, 理科=79");
        System.out.println("鈴木美咲: 数学=88, 英語=91, 理科=85");
        System.out.println("高橋研二: 数学=72, 英語=68, 理科=74");
        System.out.println();
        
        // 個人成績レポート表示
        gradeBook.displayIndividualReports();
        
        // クラス統計表示
        gradeBook.displayClassStatistics();
        
        // 詳細分析表示
        gradeBook.displayDetailedAnalysis();
        
        // 個別の詳細分析例
        System.out.println("=== 個別詳細分析例 ===");
        Student topStudent = gradeBook.findStudentByName("佐藤花子");
        if (topStudent != null) {
            topStudent.displayDetailedAnalysis();
        }
        
        System.out.println("成績管理システムのデモンストレーションを終了します。");
    }
}

/*
 * 学習のポイント:
 * 
 * 1. HashMap の活用:
 *    - 科目名と成績のペアを効率的に管理
 *    - キーと値の対応関係の理解
 * 
 * 2. ArrayList の使用:
 *    - 可変長の学生リスト管理
 *    - 動的なデータ追加・検索
 * 
 * 3. 統計計算:
 *    - 平均値、最大値、最小値の算出
 *    - 成績分布の集計
 * 
 * 4. オブジェクト指向設計:
 *    - Student と GradeBook の責任分離
 *    - 適切なカプセル化
 * 
 * 5. 実用的な機能:
 *    - GPA計算システム
 *    - 詳細な成績分析機能
 *    - 教育現場で使える機能設計
 */