/**
 * 第3章 発展課題2: 成績管理プログラム（解答例）
 *
 * 学生を表すStudentクラスと、クラス全体の成績をまとめるGradeBookクラスを組み合わせて、
 * 個人の成績レポートとクラスの統計を表示します。
 *
 * 学習ポイント:
 * - 複数のクラスの連携（GradeBookがStudentのオブジェクトを持つ）
 * - 配列をフィールドに持つクラスの設計
 * - staticメソッドによる共通の処理（点数から評価を求める）
 *
 * 科目名と点数の組は、第9章で学ぶMapを使うと1つにまとめられます。
 * この課題では、科目名の配列と点数の配列を同じ添字で対応させます。
 */
public class GradeManagement {
    public static void main(String[] args) {
        String[] subjects = {"数学", "英語", "理科"};

        IO.println("=== 成績管理システム ===");
        IO.println();

        GradeBook gradeBook = new GradeBook("プログラミング基礎クラス", 10);
        IO.println(gradeBook.getClassName());
        IO.println();

        IO.println("学生を登録しています...");
        Student s1 = new Student("S001", "山田太郎", subjects);
        Student s2 = new Student("S002", "佐藤花子", subjects);
        Student s3 = new Student("S003", "田中次郎", subjects);
        gradeBook.addStudent(s1);
        gradeBook.addStudent(s2);
        gradeBook.addStudent(s3);
        IO.println();

        IO.println("成績を登録しています...");
        s1.setScores(new int[]{85, 78, 92});
        s2.setScores(new int[]{94, 89, 87});
        s3.setScores(new int[]{76, 82, 79});
        gradeBook.showAllScores();
        IO.println();

        IO.println("=== 個人成績レポート ===");
        gradeBook.showReports();

        IO.println("=== クラス統計 ===");
        gradeBook.showStatistics();
    }
}

/**
 * 学生と、その科目別の成績を表すクラス
 */
class Student {
    private String studentId;
    private String name;
    private String[] subjects;
    private int[] scores;

    public Student(String studentId, String name, String[] subjects) {
        this.studentId = studentId;
        this.name = name;
        this.subjects = subjects;
        this.scores = new int[subjects.length];
    }

    public String getStudentId() {
        return studentId;
    }

    public String getName() {
        return name;
    }

    public String[] getSubjects() {
        return subjects;
    }

    public int getScore(int index) {
        return scores[index];
    }

    /**
     * 科目の順に点数を設定する
     */
    public void setScores(int[] newScores) {
        for (int i = 0; i < scores.length && i < newScores.length; i++) {
            scores[i] = newScores[i];
        }
    }

    public double getAverage() {
        int sum = 0;
        for (int score : scores) {
            sum += score;
        }
        return (double) sum / scores.length;
    }

    /**
     * 点数から評価（A〜F）を求める。どの学生にも共通の処理なのでstaticにしている
     */
    public static String toGrade(double score) {
        if (score >= 90) {
            return "A";
        } else if (score >= 80) {
            return "B";
        } else if (score >= 70) {
            return "C";
        } else if (score >= 60) {
            return "D";
        }
        return "F";
    }

    public String scoresText() {
        StringBuilder sb = new StringBuilder(name + ": ");
        for (int i = 0; i < subjects.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(subjects[i]).append("=").append(scores[i]);
        }
        return sb.toString();
    }

    public void showReport() {
        IO.println("学生: " + name + " (" + studentId + ")");
        IO.println("科目別成績:");
        for (int i = 0; i < subjects.length; i++) {
            IO.println("- " + subjects[i] + ": " + scores[i] + "点 (" + toGrade(scores[i]) + ")");
        }
        double average = getAverage();
        IO.println("平均点: " + "%.1f".formatted(average) + "点 (" + toGrade(average) + ")");
    }
}

/**
 * クラス全体の成績をまとめるクラス
 */
class GradeBook {
    private String className;
    private Student[] students;
    private int count; // 登録した学生の数

    public GradeBook(String className, int capacity) {
        this.className = className;
        this.students = new Student[capacity];
        this.count = 0;
    }

    public String getClassName() {
        return className;
    }

    public void addStudent(Student student) {
        if (count >= students.length) {
            IO.println("これ以上登録できません: " + student.getName());
            return;
        }
        students[count] = student;
        count++;
        IO.println("✓ 学生ID: " + student.getStudentId() + ", 名前: " + student.getName());
    }

    public void showAllScores() {
        for (int i = 0; i < count; i++) {
            IO.println(students[i].scoresText());
        }
    }

    public void showReports() {
        for (int i = 0; i < count; i++) {
            students[i].showReport();
            IO.println();
        }
    }

    public void showStatistics() {
        if (count == 0) {
            IO.println("学生が登録されていません");
            return;
        }

        double totalAverage = 0;
        int highest = -1;
        int lowest = 101;
        String highestText = "";
        String lowestText = "";
        // 評価ごとの人数（A, B, C, D, Fの順）
        String[] grades = {"A", "B", "C", "D", "F"};
        int[] gradeCounts = new int[grades.length];

        for (int i = 0; i < count; i++) {
            Student s = students[i];
            totalAverage += s.getAverage();

            String[] subjects = s.getSubjects();
            for (int j = 0; j < subjects.length; j++) {
                int score = s.getScore(j);
                if (score > highest) {
                    highest = score;
                    highestText = s.getName() + " - " + subjects[j];
                }
                if (score < lowest) {
                    lowest = score;
                    lowestText = s.getName() + " - " + subjects[j];
                }
            }

            String grade = Student.toGrade(s.getAverage());
            for (int g = 0; g < grades.length; g++) {
                if (grades[g].equals(grade)) {
                    gradeCounts[g]++;
                }
            }
        }

        IO.println("登録学生数: " + count + "名");
        IO.println("クラス平均: " + "%.1f".formatted(totalAverage / count) + "点");
        IO.println("最高得点: " + highest + "点 (" + highestText + ")");
        IO.println("最低得点: " + lowest + "点 (" + lowestText + ")");
        IO.println();
        IO.println("成績分布（平均点による評価）:");
        for (int g = 0; g < grades.length; g++) {
            if (gradeCounts[g] > 0) {
                double percent = gradeCounts[g] * 100.0 / count;
                IO.println(grades[g] + ": " + gradeCounts[g] + "名 (" + "%.1f".formatted(percent) + "%)");
            }
        }
    }
}
