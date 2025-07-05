package chapter10.solutions;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

/**
 * 学生の成績処理をStream APIで実装
 */
public class StudentGradeProcessor {
    
    public static class Student {
        private final String id;
        private final String name;
        private final String department;
        private final Map<String, Integer> grades;
        
        public Student(String id, String name, String department) {
            this.id = id;
            this.name = name;
            this.department = department;
            this.grades = new HashMap<>();
        }
        
        public void addGrade(String subject, int score) {
            grades.put(subject, Math.max(0, Math.min(100, score)));
        }
        
        public String getId() { return id; }
        public String getName() { return name; }
        public String getDepartment() { return department; }
        public Map<String, Integer> getGrades() { return new HashMap<>(grades); }
        
        public double getAverageGrade() {
            return grades.values().stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);
        }
        
        public OptionalInt getGrade(String subject) {
            Integer grade = grades.get(subject);
            return grade != null ? OptionalInt.of(grade) : OptionalInt.empty();
        }
        
        public long getPassingSubjectCount() {
            return grades.values().stream()
                .filter(grade -> grade >= 60)
                .count();
        }
        
        @Override
        public String toString() {
            return String.format("Student{id='%s', name='%s', department='%s', avg=%.1f}",
                               id, name, department, getAverageGrade());
        }
    }
    
    private final List<Student> students;
    
    public StudentGradeProcessor(List<Student> students) {
        this.students = new ArrayList<>(students);
    }
    
    /**
     * 学生を成績順でソート
     */
    public List<Student> sortByAverageGrade(boolean ascending) {
        Comparator<Student> comparator = Comparator.comparingDouble(Student::getAverageGrade);
        if (!ascending) {
            comparator = comparator.reversed();
        }
        
        return students.stream()
            .sorted(comparator)
            .collect(Collectors.toList());
    }
    
    /**
     * 学科別の上位学生を取得
     */
    public Map<String, List<Student>> getTopStudentsByDepartment(int n) {
        return students.stream()
            .collect(Collectors.groupingBy(
                Student::getDepartment,
                Collectors.collectingAndThen(
                    Collectors.toList(),
                    list -> list.stream()
                        .sorted(Comparator.comparingDouble(Student::getAverageGrade).reversed())
                        .limit(n)
                        .collect(Collectors.toList())
                )
            ));
    }
    
    /**
     * 科目別の平均点を計算
     */
    public Map<String, Double> calculateSubjectAverages() {
        Set<String> allSubjects = students.stream()
            .flatMap(student -> student.getGrades().keySet().stream())
            .collect(Collectors.toSet());
        
        return allSubjects.stream()
            .collect(Collectors.toMap(
                Function.identity(),
                subject -> students.stream()
                    .mapToInt(student -> student.getGrade(subject).orElse(0))
                    .filter(grade -> grade > 0)
                    .average()
                    .orElse(0.0)
            ));
    }
    
    /**
     * 合格者を取得（全科目60点以上）
     */
    public List<Student> getPassingStudents() {
        return students.stream()
            .filter(student -> student.getGrades().values().stream()
                .allMatch(grade -> grade >= 60))
            .collect(Collectors.toList());
    }
    
    /**
     * 特定科目の成績でフィルタリング
     */
    public List<Student> filterBySubjectGrade(String subject, int minGrade) {
        return students.stream()
            .filter(student -> student.getGrade(subject)
                .map(grade -> grade >= minGrade)
                .orElse(false))
            .collect(Collectors.toList());
    }
    
    /**
     * 学科別の統計情報を計算
     */
    public Map<String, DoubleSummaryStatistics> calculateDepartmentStatistics() {
        return students.stream()
            .collect(Collectors.groupingBy(
                Student::getDepartment,
                Collectors.summarizingDouble(Student::getAverageGrade)
            ));
    }
    
    /**
     * 成績分布を計算
     */
    public Map<String, Long> getGradeDistribution() {
        return students.stream()
            .collect(Collectors.groupingBy(
                student -> {
                    double avg = student.getAverageGrade();
                    if (avg >= 90) return "A";
                    if (avg >= 80) return "B";
                    if (avg >= 70) return "C";
                    if (avg >= 60) return "D";
                    return "F";
                },
                Collectors.counting()
            ));
    }
    
    /**
     * 最高得点者を取得
     */
    public Optional<Student> getTopStudent() {
        return students.stream()
            .max(Comparator.comparingDouble(Student::getAverageGrade));
    }
    
    /**
     * 平均点以上の学生を取得
     */
    public List<Student> getAboveAverageStudents() {
        double overallAverage = students.stream()
            .mapToDouble(Student::getAverageGrade)
            .average()
            .orElse(0.0);
        
        return students.stream()
            .filter(student -> student.getAverageGrade() >= overallAverage)
            .collect(Collectors.toList());
    }
    
    /**
     * 科目数でグループ化
     */
    public Map<Integer, List<Student>> groupBySubjectCount() {
        return students.stream()
            .collect(Collectors.groupingBy(
                student -> student.getGrades().size()
            ));
    }
    
    /**
     * カスタム集約で処理
     */
    public <T> T processStudents(Collector<Student, ?, T> collector) {
        return students.stream().collect(collector);
    }
    
    /**
     * 並列処理で統計計算
     */
    public DoubleSummaryStatistics calculateStatisticsParallel() {
        return students.parallelStream()
            .mapToDouble(Student::getAverageGrade)
            .summaryStatistics();
    }
    
    // ファクトリーメソッド
    public static StudentGradeProcessor of(Student... students) {
        return new StudentGradeProcessor(Arrays.asList(students));
    }
}