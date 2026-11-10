public class Employee {  // 親クラス（スーパークラス）
    String employeeId;   // ①
    String name;         // ①

    void work() {        // ②
        IO.println(this.name + "が業務を実行中");
    }
}
