/**
 * Employeeを試すプログラム（課題1の解答例）
 */
public class EmployeeTest {
    public static void main(String[] args) {
        IO.println("=== 従業員管理システム ===");
        
        // Employeeオブジェクトを作成する
        Employee employee = new Employee("田中太郎", 30, 400000);
        employee.showEmployeeInfo();
        IO.println();
        
        // getterメソッドを使って値を取得する
        IO.println("取得したデータ:");
        IO.println("名前: " + employee.getName());
        IO.println("年齢: " + employee.getAge());
        IO.println("給与: " + employee.getSalary());
        IO.println();
        
        // setterメソッドを使って値を変更する
        employee.setAge(35);
        employee.setSalary(450000);
        IO.println();
        
        // 更新後の情報を表示する
        IO.println("更新後の従業員情報:");
        employee.showEmployeeInfo();
        IO.println();
        
        // 不正な値を設定してエラーを確認する
        employee.setAge(-5);
        employee.setSalary(-1000);
    }
}