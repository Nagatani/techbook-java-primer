package chapter10.solutions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

class StudentGradeProcessorTest {
    
    private StudentGradeProcessor processor;
    private StudentGradeProcessor.Student student1;
    private StudentGradeProcessor.Student student2;
    
    @BeforeEach
    void setUp() {
        student1 = new StudentGradeProcessor.Student("S001", "田中", "情報学科");
        student1.addGrade("数学", 85);
        student1.addGrade("英語", 90);
        student1.addGrade("物理", 80);
        
        student2 = new StudentGradeProcessor.Student("S002", "佐藤", "情報学科");
        student2.addGrade("数学", 75);
        student2.addGrade("英語", 85);
        student2.addGrade("物理", 70);
        
        processor = StudentGradeProcessor.of(student1, student2);
    }
    
    @Test
    void testStudentBasics() {
        assertEquals("S001", student1.getId());
        assertEquals("田中", student1.getName());
        assertEquals("情報学科", student1.getDepartment());
        assertEquals(85.0, student1.getAverageGrade(), 0.1);
        assertEquals(3, student1.getPassingSubjectCount());
    }
    
    @Test
    void testSorting() {
        List<StudentGradeProcessor.Student> sorted = processor.sortByAverageGrade(false);
        assertEquals("田中", sorted.get(0).getName());
        assertEquals("佐藤", sorted.get(1).getName());
    }
    
    @Test
    void testSubjectAverages() {
        Map<String, Double> averages = processor.calculateSubjectAverages();
        assertEquals(80.0, averages.get("数学"), 0.1);
        assertEquals(87.5, averages.get("英語"), 0.1);
        assertEquals(75.0, averages.get("物理"), 0.1);
    }
    
    @Test
    void testPassingStudents() {
        List<StudentGradeProcessor.Student> passing = processor.getPassingStudents();
        assertEquals(2, passing.size());
    }
    
    @Test
    void testGradeDistribution() {
        Map<String, Long> distribution = processor.getGradeDistribution();
        assertEquals(2, distribution.get("B").longValue());
    }
    
    @Test
    void testTopStudent() {
        Optional<StudentGradeProcessor.Student> top = processor.getTopStudent();
        assertTrue(top.isPresent());
        assertEquals("田中", top.get().getName());
    }
}