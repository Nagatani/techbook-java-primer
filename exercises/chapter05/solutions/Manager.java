/**
 * 管理職（課題3の解答例）
 */
public class Manager extends Employee {
    private int managementAllowance;

    public Manager(String name, int baseSalary, int managementAllowance) {
        super(name, baseSalary);
        this.managementAllowance = managementAllowance;
    }

    @Override
    public String getPosition() {
        return "管理職";
    }

    @Override
    public int getAllowance() {
        return managementAllowance;
    }

    @Override
    protected void showAllowance() {
        IO.println("管理手当: " + "%,d".formatted(managementAllowance) + "円");
    }
}
