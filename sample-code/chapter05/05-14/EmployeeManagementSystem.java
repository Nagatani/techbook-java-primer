import java.util.List;

// 使用時の問題
public class EmployeeManagementSystem {
    public static void performSystemMaintenance(List<Employee> employees) {
        for (Employee employee : employees) {
            employee.modifySystemSettings(); // 一般従業員で例外が発生！
        }
    }
}
