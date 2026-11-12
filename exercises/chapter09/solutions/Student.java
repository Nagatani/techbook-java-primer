package chapter09.solutions;

/**
 * 学生を表すクラス（課題1の解答例）
 *
 * equalsとhashCodeを学生IDだけで比べるように書いておくと、
 * ListのremoveやcontainsでもMapのキーと同じ「同じ学生」の考え方で探せます。
 */
public class Student {
    private String studentId;
    private String name;
    private int grade;
    private double score;

    public Student(String studentId, String name, int grade, double score) {
        this.studentId = studentId;
        this.name = name;
        this.grade = grade;
        this.score = score;
    }

    @Override
    public String toString() {
        return "学生ID: " + studentId + ", 名前: " + name + ", 学年: " + grade + ", 成績: " + score;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Student other = (Student) obj;
        return studentId.equals(other.studentId);
    }

    @Override
    public int hashCode() {
        // equalsで比べるフィールド（学生ID）だけから計算する
        return studentId.hashCode();
    }

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

    public void setScore(double score) {
        this.score = score;
    }
}
