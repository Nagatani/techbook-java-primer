package chapter07.solutions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

/**
 * Sortableインターフェイスとその実装のテストクラス
 */
class SortableTest {
    
    private Student student1;
    private Student student2;
    private Student student3;
    private Student student4;
    private List<Student> students;
    
    @BeforeEach
    void setUp() {
        student1 = new Student("田中太郎", 85, "S001");
        student2 = new Student("佐藤花子", 92, "S002");
        student3 = new Student("鈴木次郎", 78, "S003");
        student4 = new Student("高橋美咲", 95, "S004");
        
        students = new ArrayList<>(Arrays.asList(student1, student2, student3, student4));
    }
    
    @Test
    void testStudentBasicProperties() {
        assertEquals("田中太郎", student1.getName());
        assertEquals(85, student1.getScore());
        assertEquals("S001", student1.getStudentId());
        assertEquals("B", student1.getGrade());
        assertTrue(student1.isPassing());
    }
    
    @Test
    void testGradeCalculation() {
        Student studentA = new Student("A", 95, "A001");
        Student studentB = new Student("B", 85, "B001");
        Student studentC = new Student("C", 75, "C001");
        Student studentD = new Student("D", 65, "D001");
        Student studentF = new Student("F", 55, "F001");
        
        assertEquals("A", studentA.getGrade());
        assertEquals("B", studentB.getGrade());
        assertEquals("C", studentC.getGrade());
        assertEquals("D", studentD.getGrade());
        assertEquals("F", studentF.getGrade());
    }
    
    @Test
    void testPassingStatus() {
        Student passing = new Student("合格", 60, "P001");
        Student failing = new Student("不合格", 59, "F001");
        
        assertTrue(passing.isPassing());
        assertFalse(failing.isPassing());
    }
    
    @Test
    void testScoreValidation() {
        Student student = new Student("テスト", 150, "T001");
        assertEquals(100, student.getScore()); // 上限値で制限
        
        student.setScore(-10);
        assertEquals(0, student.getScore()); // 下限値で制限
        
        student.setScore(85);
        assertEquals(85, student.getScore()); // 正常な値
    }
    
    @Test
    void testSortByScore() {
        // 成績順（降順）でソート
        for (Student student : students) {
            student.setSortCriteria(Student.SortCriteria.SCORE);
        }
        
        students.sort((s1, s2) -> s1.compareTo(s2));
        
        // 成績順: 95, 92, 85, 78
        assertEquals("高橋美咲", students.get(0).getName());
        assertEquals("佐藤花子", students.get(1).getName());
        assertEquals("田中太郎", students.get(2).getName());
        assertEquals("鈴木次郎", students.get(3).getName());
    }
    
    @Test
    void testSortByName() {
        // 名前順（昇順）でソート
        for (Student student : students) {
            student.setSortCriteria(Student.SortCriteria.NAME);
        }
        
        students.sort((s1, s2) -> s1.compareTo(s2));
        
        // 名前順: 佐藤花子, 鈴木次郎, 高橋美咲, 田中太郎
        assertEquals("佐藤花子", students.get(0).getName());
        assertEquals("鈴木次郎", students.get(1).getName());
        assertEquals("高橋美咲", students.get(2).getName());
        assertEquals("田中太郎", students.get(3).getName());
    }
    
    @Test
    void testSortByStudentId() {
        // 学籍番号順（昇順）でソート
        for (Student student : students) {
            student.setSortCriteria(Student.SortCriteria.STUDENT_ID);
        }
        
        students.sort((s1, s2) -> s1.compareTo(s2));
        
        // 学籍番号順: S001, S002, S003, S004
        assertEquals("S001", students.get(0).getStudentId());
        assertEquals("S002", students.get(1).getStudentId());
        assertEquals("S003", students.get(2).getStudentId());
        assertEquals("S004", students.get(3).getStudentId());
    }
    
    @Test
    void testSortKey() {
        student1.setSortCriteria(Student.SortCriteria.NAME);
        assertEquals("田中太郎", student1.getSortKey());
        
        student1.setSortCriteria(Student.SortCriteria.SCORE);
        assertEquals(85, student1.getSortKey());
        
        student1.setSortCriteria(Student.SortCriteria.STUDENT_ID);
        assertEquals("S001", student1.getSortKey());
    }
    
    @Test
    void testSortDirection() {
        student1.setSortCriteria(Student.SortCriteria.NAME);
        assertTrue(student1.isAscending());
        
        student1.setSortCriteria(Student.SortCriteria.SCORE);
        assertFalse(student1.isAscending()); // 成績は降順
        
        student1.setSortCriteria(Student.SortCriteria.STUDENT_ID);
        assertTrue(student1.isAscending());
    }
    
    @Test
    void testSortPriority() {
        student1.setSortCriteria(Student.SortCriteria.SCORE);
        assertEquals(1, student1.getSortPriority());
        
        student1.setSortCriteria(Student.SortCriteria.NAME);
        assertEquals(2, student1.getSortPriority());
        
        student1.setSortCriteria(Student.SortCriteria.STUDENT_ID);
        assertEquals(3, student1.getSortPriority());
    }
    
    @Test
    void testCompareToWithInvalidType() {
        Sortable invalidSortable = new Sortable() {
            @Override
            public int compareTo(Sortable other) { return 0; }
            @Override
            public Comparable<?> getSortKey() { return "invalid"; }
        };
        
        assertThrows(IllegalArgumentException.class, () -> {
            student1.compareTo(invalidSortable);
        });
    }
    
    @Test
    void testToString() {
        String expected = "Student{name='田中太郎', score=85, studentId='S001', grade='B'}";
        assertEquals(expected, student1.toString());
    }
    
    @Test
    void testSortablePolymorphism() {
        // Sortableインターフェイスを通じた多態性のテスト
        Sortable[] sortables = {student1, student2, student3};
        
        for (Sortable sortable : sortables) {
            assertNotNull(sortable.getSortKey());
            assertTrue(sortable.getSortPriority() >= 0);
        }
    }
}