// StudentService.java - 学生の登録や検索の手順をまとめる。保存の方法は知らない
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class StudentService {
    private final StudentRepository repository;

    // どのリポジトリを使うかは、外から渡す
    public StudentService(StudentRepository repository) {
        this.repository = repository;
    }

    public void register(Student student)
            throws DuplicateStudentException {
        if (repository.findById(student.getId()).isPresent()) {
            throw new DuplicateStudentException(student.getId());
        }
        repository.save(student);
    }

    public Optional<Student> find(String id) {
        return repository.findById(id);
    }

    public void recordGrade(String studentId, Grade grade) {
        Student student = repository.findById(studentId)
            .orElseThrow(() -> new IllegalArgumentException("学生が見つかりません: " + studentId));
        student.recordGrade(grade);
        repository.save(student); // 保存し直す（メモリ上の実装では何も変わらない）
    }

    public List<Student> findByDepartment(String department) {
        return repository.findAll().stream()
            .filter(s -> s.getDepartment().equals(department))
            .toList();
    }

    public List<Student> topStudents(int count) {
        return repository.findAll().stream()
            .sorted(Comparator.comparingDouble(Student::gpa).reversed())
            .limit(count)
            .toList();
    }
}
