// RepositorySwapCheck.java - StudentServiceを変えずに、リポジトリの実装を差し替えられることを確かめる
import java.util.List;

public class RepositorySwapCheck {
    // 呼ばれた回数を数える実装（中身はメモリ上の実装に任せる）
    static class CountingRepository implements StudentRepository<GradeableStudent> {
        private final StudentRepository<GradeableStudent> inner = new InMemoryStudentRepository<>();
        int findAllCount = 0;

        @Override public void addStudent(GradeableStudent s) { inner.addStudent(s); }
        @Override public GradeableStudent findById(String id) { return inner.findById(id); }
        @Override public List<GradeableStudent> findAll() { findAllCount++; return inner.findAll(); }
        @Override public boolean removeStudent(String id) { return inner.removeStudent(id); }
    }

    public static void main(String[] args) {
        CountingRepository repository = new CountingRepository();
        StudentService service = new StudentService(repository);

        GradeableStudent a = new GradeableStudent("S001", "田中太郎", 20, "工学部");
        a.addGrade("数学", 3.0);
        GradeableStudent b = new GradeableStudent("S002", "山田花子", 21, "理学部");
        b.addGrade("数学", 4.0);
        service.registerStudent(a);
        service.registerStudent(b);

        IO.println("平均GPA: %.2f".formatted(service.getAverageGPA()));
        IO.println("学部別: " + service.getStudentCountByDepartment());
        IO.println("findAllを呼んだ回数: " + repository.findAllCount);

        try {
            service.registerStudent(new GradeableStudent("S001", "佐藤次郎", 22, "工学部"));
            throw new AssertionError("重複したIDを登録できてしまった");
        } catch (IllegalArgumentException e) {
            IO.println("重複を検出: " + e.getMessage());
        }
        IO.println("すべての確認に成功しました");
    }
}
