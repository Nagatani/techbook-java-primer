package chapter09.solutions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StudentManagerTest {
    private StudentManager manager;

    @BeforeEach
    void setUp() {
        manager = new StudentManager();
        manager.addStudent(new Student("S001", "山田太郎", 2, 85.5));
        manager.addStudent(new Student("S002", "鈴木花子", 1, 92.0));
        manager.addStudent(new Student("S003", "佐藤次郎", 2, 78.0));
        manager.addStudent(new Student("S004", "高橋美咲", 3, 88.5));
    }

    @Test
    void 学生IDが同じならequalsとhashCodeが一致する() {
        Student a = new Student("S001", "山田太郎", 2, 85.5);
        Student b = new Student("S001", "別の名前", 1, 0.0);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertFalse(a.equals(new Student("S002", "山田太郎", 2, 85.5)));
        assertFalse(a.equals(null));
    }

    @Test
    void toStringの形() {
        assertEquals("学生ID: S001, 名前: 山田太郎, 学年: 2, 成績: 85.5",
                manager.findStudentById("S001").toString());
    }

    @Test
    void 登録済みのIDは追加しない() {
        manager.addStudent(new Student("S001", "田中一郎", 1, 70.0));
        assertEquals(4, manager.getStudentCount());
        assertEquals("山田太郎", manager.findStudentById("S001").getName());
    }

    @Test
    void IDで検索できない学生はnull() {
        assertNull(manager.findStudentById("S009"));
    }

    @Test
    void 学年で絞り込むと登録順に返る() {
        List<Student> grade2 = manager.getStudentsByGrade(2);
        assertEquals(2, grade2.size());
        assertEquals("S001", grade2.get(0).getStudentId());
        assertEquals("S003", grade2.get(1).getStudentId());
        assertTrue(manager.getStudentsByGrade(4).isEmpty());
    }

    @Test
    void 平均成績() {
        assertEquals(86.0, manager.calculateAverageScore(), 0.001);
        assertEquals(0.0, new StudentManager().calculateAverageScore(), 0.001);
    }

    @Test
    void 削除するとリストとマップの両方から消える() {
        assertTrue(manager.removeStudent("S003"));
        assertFalse(manager.removeStudent("S003"));
        assertNull(manager.findStudentById("S003"));
        assertEquals(3, manager.getStudentCount());
        assertTrue(manager.getStudentsByGrade(2).size() == 1);
    }
}
