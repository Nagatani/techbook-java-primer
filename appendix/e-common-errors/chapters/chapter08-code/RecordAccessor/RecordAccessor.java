public class RecordAccessor {
    record Student(String name, int score) {}

    public static void main(String[] args) {
        Student s = new Student("田中", 80);
        // Recordの値は、フィールドと同じ名前のメソッドで取り出す
        IO.println(s.name() + ": " + s.score() + "点");
    }
}
