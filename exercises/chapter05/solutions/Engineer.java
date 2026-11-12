/**
 * 技術者（課題3の解答例）
 */
public class Engineer extends Employee {
    private int technicalAllowance;

    public Engineer(String name, int baseSalary, int technicalAllowance) {
        super(name, baseSalary);
        this.technicalAllowance = technicalAllowance;
    }

    @Override
    public String getPosition() {
        return "技術者";
    }

    @Override
    public int getAllowance() {
        return technicalAllowance;
    }

    @Override
    protected void showAllowance() {
        IO.println("技術手当: " + "%,d".formatted(technicalAllowance) + "円");
    }
}
