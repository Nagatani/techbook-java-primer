/**
 * 従業員のクラスを試すプログラム（課題3の解答例）
 */
public class EmployeeTest {
    public static void main(String[] args) {
        IO.println("=== 従業員管理システム ===");

        Employee[] employees = {
            new Employee("田中太郎", 300000),
            new Manager("佐藤花子", 500000, 100000),
            new Engineer("鈴木一郎", 450000, 80000)
        };

        int total = 0;
        for (Employee employee : employees) {
            employee.showInfo();
            IO.println();
            // getSalaryは、実際のオブジェクトのgetAllowanceを使って計算される
            total += employee.getSalary();
        }
        IO.println("全従業員の総給与: " + "%,d".formatted(total) + "円");
    }
}
