package chapter07.solutions;

/**
 * 従業員（課題2の解答例）
 */
public class Employee implements Payable {
    private String name;
    private String employeeId;
    private double baseSalary;
    private double bonusRate;

    public Employee(String name, String employeeId, double baseSalary, double bonusRate) {
        this.name = name;
        this.employeeId = employeeId;
        this.baseSalary = baseSalary;
        this.bonusRate = bonusRate;
    }

    // 基本給 + 基本給 × ボーナス率
    @Override
    public double calculatePayment() {
        return baseSalary + baseSalary * bonusRate;
    }

    public void displayEmployeeInfo() {
        IO.println("従業員情報:");
        IO.println("  名前: " + name + "（" + employeeId + "）");
        IO.println("  基本給: " + "%,.0f".formatted(baseSalary) + "円");
        IO.println("  ボーナス率: " + "%.0f".formatted(bonusRate * 100) + "%");
        IO.println("  総支給額: " + "%,.0f".formatted(calculatePayment()) + "円");
    }

    public String getName() {
        return name;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public double getBaseSalary() {
        return baseSalary;
    }

    public double getBonusRate() {
        return bonusRate;
    }
}
