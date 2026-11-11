import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

class Student {
    String name;
    int score;
    int height;
    public Student(String name, int score, int height) { this.name = name; this.score = score; this.height = height; }
    public String getName() { return name; }
    public int getScore() { return score; }
    public int getHeight() { return height; }
    @Override public String toString() { return name + "(score:" + score + ", height:" + height + ")"; }
}

public class AdvancedSortExample {
    public static void main(String[] args) {
        List<Student> students = new ArrayList<>();
        students.add(new Student("Bob", 92, 170));
        students.add(new Student("Alice", 85, 165));
        students.add(new Student("Charlie", 92, 180));

        // 点数が高い順、同じ点数の場合は身長が高い順に並べる
        List<Student> sorted = students.stream()
            .sorted(Comparator.comparingInt(Student::getScore).reversed()
                .thenComparing(Comparator.comparingInt(Student::getHeight).reversed()))
            .collect(Collectors.toList());

        IO.println(sorted);
        IO.println("元のリスト: " + students); // 元のリストの並びは変わらない
    }
}
