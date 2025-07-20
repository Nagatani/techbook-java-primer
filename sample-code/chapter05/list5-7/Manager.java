/**
 * リスト5-7
 * Managerクラス（Employeeクラスを継承）
 * 
 * 元ファイル: chapter05-inheritance-and-polymorphism.md (552行目)
 */
public class Manager extends Employee {  // ③
    void conductMeeting() {  // ④
        System.out.println(this.name + "が会議を主宰");
    }
}