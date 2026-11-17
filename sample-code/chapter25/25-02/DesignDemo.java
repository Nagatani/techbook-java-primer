// DesignDemo.java - 学部生と大学院生を同じStudentクラスで扱う
import java.util.List;

public class DesignDemo {
    public static void main(String[] args) {
        Student taro = new Student("S001", "山田太郎", "工学部", new Undergraduate(2025));
        Student hanako = new Student("G001", "佐藤花子", "工学研究科",
            new Graduate("機械学習", "鈴木教授"));

        taro.recordGrade(new Grade("プログラミング演習", 2, 4.0));
        taro.recordGrade(new Grade("線形代数", 4, 3.0));
        // 大学院生も成績を持てる
        hanako.recordGrade(new Grade("情報理論特論", 2, 3.5));

        for (Student s : List.of(taro, hanako)) {
            IO.println("%s %s %s GPA=%.2f".formatted(
                s.getId(), s.getName(), s.describeEnrollment(),
                s.gpa()));
        }
    }
}
