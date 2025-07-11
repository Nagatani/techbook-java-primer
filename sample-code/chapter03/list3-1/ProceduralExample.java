/**
 * リスト3-1
 * ProceduralExampleクラス
 * 
 * 元ファイル: chapter03-oop-basics.md (260行目)
 */

public class ProceduralExample {
    public static void main(String[] args) {
        String studentName = "田中太郎";  // ①
        int studentAge = 20;             // ①
        double studentGpa = 3.5;         // ①
        
        printStudent(studentName, studentAge, studentGpa);  // ②
    }
    
    public static void printStudent(String name, int age, double gpa) {  // ③
        System.out.println("名前: " + name + ", 年齢: " + age + ", GPA: " + gpa);
    }
}