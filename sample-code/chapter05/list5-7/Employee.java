/**
 * リスト5-7
 * Employeeクラス（親クラス）
 * 
 * 元ファイル: chapter05-inheritance-and-polymorphism.md (543行目)
 */
public class Employee {  // 親クラス（スーパークラス）
    String employeeId;   // ①
    String name;         // ①

    void work() {        // ②
        System.out.println(this.name + "が業務を実行中");
    }
}