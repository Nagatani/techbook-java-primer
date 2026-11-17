// StudentServiceTest.java - サービスをメモリ上のリポジトリと組み合わせて検査する
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StudentServiceTest {
    private StudentService service;

    @BeforeEach
    void setUp() throws DuplicateStudentException {
        // データベースを用意しなくても、サービスの規則を検査できる
        service = new StudentService(new InMemoryStudentRepository());
        service.register(new Student("S001", "山田太郎", "工学部",
                new Undergraduate(2025)));
    }

    // 同じ学生IDでは登録できない
    @Test
    void rejectsDuplicateId() {
        Student same = new Student("S001", "田中次郎", "文学部",
                new Undergraduate(2024));
        assertThrows(DuplicateStudentException.class,
                () -> service.register(same));
    }

    // GPAは単位数で重み付けした平均になる
    @Test
    void gpaIsWeightedByCredits() {
        service.recordGrade("S001",
                new Grade("プログラミング演習", 2, 4.0));
        service.recordGrade("S001", new Grade("線形代数", 4, 3.0));
        assertEquals(20.0 / 6, service.find("S001").orElseThrow().gpa(),
                1e-9);
    }

    // 学部で絞り込める
    @Test
    void filtersByDepartment() throws DuplicateStudentException {
        service.register(new Student("S002", "佐藤花子", "理学部",
                new Undergraduate(2024)));
        List<Student> result = service.findByDepartment("工学部");
        assertEquals(List.of("S001"), result.stream()
                .map(Student::getId).toList());
    }
}
