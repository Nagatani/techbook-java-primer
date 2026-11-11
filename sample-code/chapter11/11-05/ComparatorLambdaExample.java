import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ComparatorLambdaExample {
    public static void main(String[] args) {
        List<Student> students = Arrays.asList(
            new Student("Charlie", 2, "S002"),
            new Student("Alice", 2, "S003"),
            new Student("Bob", 1, "S001")
        );

        IO.println("ソート前: " + students);

        // 従来の方法：冗長で理解しにくい
        List<Student> students1 = new ArrayList<>(students);
        Collections.sort(students1, new Comparator<Student>() {
            @Override
            public int compare(Student s1, Student s2) {
                return s1.getName().compareTo(s2.getName());
            }
        });
        IO.println("従来の方法: " + students1);

        // ラムダ式：簡潔で意図が明確
        List<Student> students2 = new ArrayList<>(students);
        students2.sort((s1, s2) -> s1.getName().compareTo(s2.getName()));
        IO.println("ラムダ式: " + students2);

        // メソッド参照：さらに簡潔
        List<Student> students3 = new ArrayList<>(students);
        students3.sort(Comparator.comparing(Student::getName));
        IO.println("メソッド参照: " + students3);
    }
}
