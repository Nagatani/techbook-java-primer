// InMemoryStudentRepository.java - メモリ上に保存する実装（終了すると消える）
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryStudentRepository implements StudentRepository {
    // IDで引けて、登録した順も保てるLinkedHashMapを1つだけ使う
    private final Map<String, Student> students = new LinkedHashMap<>();

    @Override
    public void save(Student student) {
        students.put(student.getId(), student);
    }

    @Override
    public Optional<Student> findById(String id) {
        return Optional.ofNullable(students.get(id));
    }

    @Override
    public List<Student> findAll() {
        return List.copyOf(students.values());
    }

    @Override
    public boolean deleteById(String id) {
        return students.remove(id) != null;
    }
}
