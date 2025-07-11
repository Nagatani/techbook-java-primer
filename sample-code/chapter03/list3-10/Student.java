/**
 * リスト3-10
 * Studentクラス
 * 
 * 元ファイル: chapter03-oop-basics.md (729行目)
 */

// データと処理が一体化している
public class Student {
    private String name;
    private int age;
    private double gpa;
    
    // データを操作するメソッドも同じクラス内に定義
    public void printInfo() {
        System.out.println("名前: " + name + ", 年齢: " + age + ", GPA: " + gpa);
    }
}