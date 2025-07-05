package chapter08.solutions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

/**
 * StudentManagerクラスのテストクラス
 */
class StudentManagerTest {
    
    private StudentManager manager;
    private StudentManager.Student student1;
    private StudentManager.Student student2;
    private StudentManager.Student student3;
    private StudentManager.Student student4;
    
    @BeforeEach
    void setUp() {
        manager = new StudentManager();
        student1 = new StudentManager.Student("S001", "田中太郎", 85, "情報工学科");
        student2 = new StudentManager.Student("S002", "佐藤花子", 92, "情報工学科");
        student3 = new StudentManager.Student("S003", "鈴木次郎", 78, "機械工学科");
        student4 = new StudentManager.Student("S004", "高橋美咲", 95, "電気工学科");
    }
    
    @Test
    void testStudentBasicProperties() {
        assertEquals("S001", student1.getId());
        assertEquals("田中太郎", student1.getName());
        assertEquals(85, student1.getScore());
        assertEquals("情報工学科", student1.getDepartment());
        assertEquals("B", student1.getGrade());
    }
    
    @Test
    void testStudentGradeCalculation() {
        StudentManager.Student studentA = new StudentManager.Student("A", "A", 95, "Test");
        StudentManager.Student studentB = new StudentManager.Student("B", "B", 85, "Test");
        StudentManager.Student studentC = new StudentManager.Student("C", "C", 75, "Test");
        StudentManager.Student studentD = new StudentManager.Student("D", "D", 65, "Test");
        StudentManager.Student studentF = new StudentManager.Student("F", "F", 55, "Test");
        
        assertEquals("A", studentA.getGrade());
        assertEquals("B", studentB.getGrade());
        assertEquals("C", studentC.getGrade());
        assertEquals("D", studentD.getGrade());
        assertEquals("F", studentF.getGrade());
    }
    
    @Test
    void testStudentScoreValidation() {
        StudentManager.Student student = new StudentManager.Student("T001", "Test", 150, "Test");
        assertEquals(100, student.getScore());
        
        student.setScore(-10);
        assertEquals(0, student.getScore());
        
        student.setScore(85);
        assertEquals(85, student.getScore());
    }
    
    @Test
    void testAddStudent() {
        assertTrue(manager.addStudent(student1));
        assertEquals(1, manager.getTotalStudentCount());
        
        // 同じIDの学生は追加できない
        assertFalse(manager.addStudent(student1));
        assertEquals(1, manager.getTotalStudentCount());
        
        // nullは追加できない
        assertFalse(manager.addStudent(null));
        assertEquals(1, manager.getTotalStudentCount());
    }
    
    @Test
    void testRemoveStudent() {
        manager.addStudent(student1);
        manager.addStudent(student2);
        
        assertTrue(manager.removeStudent("S001"));
        assertEquals(1, manager.getTotalStudentCount());
        
        // 存在しない学生は削除できない
        assertFalse(manager.removeStudent("S999"));
        assertEquals(1, manager.getTotalStudentCount());
    }
    
    @Test
    void testFindStudentById() {
        manager.addStudent(student1);
        manager.addStudent(student2);
        
        StudentManager.Student found = manager.findStudentById("S001");
        assertNotNull(found);
        assertEquals("田中太郎", found.getName());
        
        assertNull(manager.findStudentById("S999"));
    }
    
    @Test
    void testFindStudentsByName() {
        manager.addStudent(student1);
        manager.addStudent(student2);
        
        List<StudentManager.Student> results = manager.findStudentsByName("田中");
        assertEquals(1, results.size());
        assertEquals("田中太郎", results.get(0).getName());
        
        results = manager.findStudentsByName("太郎");
        assertEquals(1, results.size());
        
        results = manager.findStudentsByName("存在しない");
        assertEquals(0, results.size());
    }
    
    @Test
    void testFindStudentsByDepartment() {
        manager.addStudent(student1);
        manager.addStudent(student2);
        manager.addStudent(student3);
        
        List<StudentManager.Student> results = manager.findStudentsByDepartment("情報工学科");
        assertEquals(2, results.size());
        
        results = manager.findStudentsByDepartment("機械工学科");
        assertEquals(1, results.size());
        
        results = manager.findStudentsByDepartment("存在しない学科");
        assertEquals(0, results.size());
    }
    
    @Test
    void testFindStudentsByScoreRange() {
        manager.addStudent(student1); // 85点
        manager.addStudent(student2); // 92点
        manager.addStudent(student3); // 78点
        manager.addStudent(student4); // 95点
        
        List<StudentManager.Student> results = manager.findStudentsByScoreRange(80, 90);
        assertEquals(1, results.size());
        assertEquals("田中太郎", results.get(0).getName());
        
        results = manager.findStudentsByScoreRange(90, 100);
        assertEquals(2, results.size());
        
        results = manager.findStudentsByScoreRange(100, 100);
        assertEquals(0, results.size());
    }
    
    @Test
    void testGetStudentsByScore() {
        manager.addStudent(student1); // 85点
        manager.addStudent(student2); // 92点
        manager.addStudent(student3); // 78点
        manager.addStudent(student4); // 95点
        
        List<StudentManager.Student> results = manager.getStudentsByScore();
        assertEquals(4, results.size());
        
        // 成績順（降順）で並んでいることを確認
        assertEquals(95, results.get(0).getScore());
        assertEquals(92, results.get(1).getScore());
        assertEquals(85, results.get(2).getScore());
        assertEquals(78, results.get(3).getScore());
    }
    
    @Test
    void testGetTopStudents() {
        manager.addStudent(student1); // 85点
        manager.addStudent(student2); // 92点
        manager.addStudent(student3); // 78点
        manager.addStudent(student4); // 95点
        
        List<StudentManager.Student> top2 = manager.getTopStudents(2);
        assertEquals(2, top2.size());
        assertEquals(95, top2.get(0).getScore());
        assertEquals(92, top2.get(1).getScore());
        
        List<StudentManager.Student> top10 = manager.getTopStudents(10);
        assertEquals(4, top10.size()); // 全体が4人なので4人が返される
    }
    
    @Test
    void testCalculateAverageScoreByDepartment() {
        manager.addStudent(student1); // 情報工学科 85点
        manager.addStudent(student2); // 情報工学科 92点
        manager.addStudent(student3); // 機械工学科 78点
        manager.addStudent(student4); // 電気工学科 95点
        
        Map<String, Double> averages = manager.calculateAverageScoreByDepartment();
        
        assertEquals(88.5, averages.get("情報工学科"), 0.01);
        assertEquals(78.0, averages.get("機械工学科"), 0.01);
        assertEquals(95.0, averages.get("電気工学科"), 0.01);
    }
    
    @Test
    void testCountStudentsByGrade() {
        manager.addStudent(student1); // B (85点)
        manager.addStudent(student2); // A (92点)
        manager.addStudent(student3); // C (78点)
        manager.addStudent(student4); // A (95点)
        
        Map<String, Long> counts = manager.countStudentsByGrade();
        
        assertEquals(2, counts.get("A"));
        assertEquals(1, counts.get("B"));
        assertEquals(1, counts.get("C"));
        assertNull(counts.get("D"));
        assertNull(counts.get("F"));
    }
    
    @Test
    void testUpdateStudentScore() {
        manager.addStudent(student1);
        
        assertTrue(manager.updateStudentScore("S001", 90));
        assertEquals(90, manager.findStudentById("S001").getScore());
        
        // 存在しない学生は更新できない
        assertFalse(manager.updateStudentScore("S999", 90));
    }
    
    @Test
    void testScoreOrderAfterUpdate() {
        manager.addStudent(student1); // 85点
        manager.addStudent(student2); // 92点
        
        // student1の成績を更新
        manager.updateStudentScore("S001", 100);
        
        List<StudentManager.Student> results = manager.getStudentsByScore();
        assertEquals(100, results.get(0).getScore());
        assertEquals("田中太郎", results.get(0).getName());
    }
    
    @Test
    void testGetAllDepartments() {
        manager.addStudent(student1); // 情報工学科
        manager.addStudent(student2); // 情報工学科
        manager.addStudent(student3); // 機械工学科
        manager.addStudent(student4); // 電気工学科
        
        Set<String> departments = manager.getAllDepartments();
        assertEquals(3, departments.size());
        assertTrue(departments.contains("情報工学科"));
        assertTrue(departments.contains("機械工学科"));
        assertTrue(departments.contains("電気工学科"));
    }
    
    @Test
    void testGetAllStudents() {
        manager.addStudent(student1);
        manager.addStudent(student2);
        
        List<StudentManager.Student> all = manager.getAllStudents();
        assertEquals(2, all.size());
        
        // 登録順で返されることを確認
        assertEquals("田中太郎", all.get(0).getName());
        assertEquals("佐藤花子", all.get(1).getName());
    }
    
    @Test
    void testGetStatistics() {
        String stats = manager.getStatistics();
        assertTrue(stats.contains("登録された学生はいません"));
        
        manager.addStudent(student1);
        manager.addStudent(student2);
        manager.addStudent(student3);
        manager.addStudent(student4);
        
        stats = manager.getStatistics();
        assertTrue(stats.contains("総学生数: 4人"));
        assertTrue(stats.contains("学科数: 3学科"));
        assertTrue(stats.contains("平均点:"));
        assertTrue(stats.contains("最高点: 95点"));
        assertTrue(stats.contains("最低点: 78点"));
    }
    
    @Test
    void testStudentEquality() {
        StudentManager.Student student1Copy = new StudentManager.Student("S001", "田中太郎", 85, "情報工学科");
        StudentManager.Student differentStudent = new StudentManager.Student("S002", "田中太郎", 85, "情報工学科");
        
        assertEquals(student1, student1Copy);
        assertNotEquals(student1, differentStudent);
        assertEquals(student1.hashCode(), student1Copy.hashCode());
    }
    
    @Test
    void testStudentToString() {
        String str = student1.toString();
        assertTrue(str.contains("S001"));
        assertTrue(str.contains("田中太郎"));
        assertTrue(str.contains("85"));
        assertTrue(str.contains("情報工学科"));
        assertTrue(str.contains("B"));
    }
}