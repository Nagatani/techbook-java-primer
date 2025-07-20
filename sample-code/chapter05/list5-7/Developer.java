/**
 * リスト5-7
 * Developerクラス（Employeeクラスを継承）
 * 
 * 元ファイル: chapter05-inheritance-and-polymorphism.md (558行目)
 */
public class Developer extends Employee {  // ③
    String primaryLanguage;  // ⑤

    void writeCode() {  // ④
        System.out.println(this.name + "が" + primaryLanguage + "でコーディング中");
    }
}