// 第5章コラム「equalsとhashCodeのバグはなぜ見つけにくいか」：equalsだけをオーバーライドしたクラスとHashSet
import java.util.HashSet;
import java.util.Set;

public class EqualsOnly {
    static class Student {
        private final String id;
        Student(String id) { this.id = id; }
        @Override
        public boolean equals(Object obj) {
            return obj instanceof Student other && id.equals(other.id);
        }
    }
    public static void main(String[] args) {
        Set<Student> set = new HashSet<>();
        set.add(new Student("S001"));
        IO.println("equals: " + new Student("S001").equals(new Student("S001")));
        IO.println("contains: " + set.contains(new Student("S001")));
        set.add(new Student("S001"));
        IO.println("size: " + set.size());
    }
}
