package chapter12.solutions;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * 学生のデータの分析（課題1の解答例）
 *
 * どのメソッドも、元のリストを変えずに新しい結果を作って返します。
 */
public class StudentAnalyzer {
    private List<Student> students;

    public StudentAnalyzer(List<Student> students) {
        this.students = students;
    }

    /**
     * 1. 全員の名前（mapで学生を名前に変える）
     */
    public List<String> getAllNames() {
        return students.stream()
                .map(Student::getName)
                .collect(Collectors.toList());
        // Java 16以降はtoList()でも書ける: students.stream().map(Student::getName).toList();
        // ただしtoList()が返すリストは変更できない
    }

    /**
     * 2. GPAがminGPA以上の学生（filterで条件に合う要素だけを残す）
     */
    public List<Student> filterByGPA(double minGPA) {
        return students.stream()
                .filter(s -> s.getGpa() >= minGPA)
                .collect(Collectors.toList());
    }

    /**
     * 3. 学部ごとの学生。groupingByの2つ目の引数にTreeMap::newを渡し、学部名の順に並ぶマップにする
     * （groupingBy(Student::getDepartment)だけだとHashMapになり、表示の順序が決まらない）
     */
    public Map<String, List<Student>> groupByDepartment() {
        return students.stream()
                .collect(Collectors.groupingBy(Student::getDepartment, TreeMap::new, Collectors.toList()));
    }

    /**
     * 4. 平均GPA。mapToDoubleでdoubleのストリーム（DoubleStream）にするとaverageが使える。
     * averageはOptionalDoubleを返す（学生がいないと平均がない）ので、orElseで0.0にする
     */
    public double calculateAverageGPA() {
        return students.stream()
                .mapToDouble(Student::getGpa)
                .average()
                .orElse(0.0);
    }

    /**
     * 5. GPAの高い順に上位n人（sortedで並べ、limitで先頭のn件に絞る）
     */
    public List<Student> getTopStudents(int n) {
        return students.stream()
                .sorted(Comparator.comparingDouble(Student::getGpa).reversed())
                .limit(n)
                .collect(Collectors.toList());
    }
}
