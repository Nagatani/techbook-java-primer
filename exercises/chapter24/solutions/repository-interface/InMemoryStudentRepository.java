// InMemoryStudentRepository.java - メモリ上に保存する実装
// フェーズ3のHashMapとArrayListの二重管理を、LinkedHashMap 1つにまとめた
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class InMemoryStudentRepository<T extends Student> implements StudentRepository<T> {
    private final Map<String, T> students = new LinkedHashMap<>();

    @Override
    public void addStudent(T student) {
        if (students.containsKey(student.getId())) {
            throw new IllegalArgumentException("学生ID " + student.getId() + " は既に存在します");
        }
        students.put(student.getId(), student);
    }

    @Override
    public T findById(String studentId) {
        return students.get(studentId);
    }

    @Override
    public List<T> findAll() {
        return List.copyOf(students.values());
    }

    @Override
    public boolean removeStudent(String studentId) {
        return students.remove(studentId) != null;
    }
}
