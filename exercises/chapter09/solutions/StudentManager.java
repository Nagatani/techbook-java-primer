package chapter09.solutions;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 学生管理システム（課題1の解答例）
 *
 * ArrayListで登録順を保ち、HashMapで学生IDから素早く引きます。
 * 追加と削除では、2つのコレクションを必ず一緒に更新します。
 */
public class StudentManager {
    private List<Student> studentList;
    private Map<String, Student> studentMap;

    public StudentManager() {
        studentList = new ArrayList<>();
        studentMap = new HashMap<>();
    }

    /**
     * 学生を追加する。登録済みのIDなら追加せずにメッセージを出す
     */
    public void addStudent(Student student) {
        if (studentMap.containsKey(student.getStudentId())) {
            IO.println("登録済みのIDです: " + student.getStudentId());
            return;
        }
        studentList.add(student);
        studentMap.put(student.getStudentId(), student);
    }

    /**
     * 学生IDで検索する。リストを先頭から探さず、マップから直接取り出す
     */
    public Student findStudentById(String studentId) {
        return studentMap.get(studentId);
    }

    public void displayAllStudents() {
        IO.println("=== 学生一覧 ===");
        for (Student student : studentList) {
            IO.println(student);
        }
    }

    /**
     * 成績の高い順に表示する。元のリストの順序を変えないよう、コピーを並べ替える
     */
    public void displayStudentsByScore() {
        IO.println("=== 成績順学生一覧 ===");
        List<Student> sorted = new ArrayList<>(studentList);
        // 第8章の匿名クラスでComparatorを書く（引数を逆にして比べると降順になる）
        sorted.sort(new Comparator<Student>() {
            @Override
            public int compare(Student a, Student b) {
                return Double.compare(b.getScore(), a.getScore());
            }
        });
        // 第11章のラムダ式・メソッド参照を使うと、次のどちらかの1行で書ける
        // sorted.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));
        // sorted.sort(Comparator.comparingDouble(Student::getScore).reversed());
        for (Student student : sorted) {
            IO.println(student);
        }
    }

    /**
     * 指定した学年の学生だけを、登録順のまま新しいリストに集めて返す
     */
    public List<Student> getStudentsByGrade(int grade) {
        List<Student> result = new ArrayList<>();
        for (Student student : studentList) {
            if (student.getGrade() == grade) {
                result.add(student);
            }
        }
        // 第12章のStream APIを使うと、次のように書ける
        // return studentList.stream().filter(s -> s.getGrade() == grade).toList();
        return result;
    }

    /**
     * 平均成績を返す。学生がいないときは0.0を返す
     */
    public double calculateAverageScore() {
        if (studentList.isEmpty()) {
            return 0.0;
        }
        double sum = 0.0;
        for (Student student : studentList) {
            sum += student.getScore();
        }
        // 第12章のStream APIを使うと、次のように書ける
        // return studentList.stream().mapToDouble(Student::getScore).average().orElse(0.0);
        return sum / studentList.size();
    }

    /**
     * 学生を削除する。マップから取り出した学生を、リストからも除く
     */
    public boolean removeStudent(String studentId) {
        Student removed = studentMap.remove(studentId);
        if (removed == null) {
            return false;
        }
        // equalsを学生IDで比べるように書いてあるので、同じ学生がリストから除かれる
        studentList.remove(removed);
        return true;
    }

    public int getStudentCount() {
        return studentList.size();
    }
}
