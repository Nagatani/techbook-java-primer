/**
 * 一般社員（課題3の解答例）
 *
 * ManagerとEngineerは、手当と役職名だけをオーバーライドする。
 * 総給与の計算と表示の流れは、このクラスにまとめる。
 */
public class Employee {
    protected String name;
    protected int baseSalary;

    public Employee(String name, int baseSalary) {
        this.name = name;
        this.baseSalary = baseSalary;
    }

    public String getPosition() {
        return "一般社員";
    }

    // 手当。一般社員にはない
    public int getAllowance() {
        return 0;
    }

    public int getSalary() {
        return baseSalary + getAllowance();
    }

    public void showInfo() {
        IO.println("従業員: " + name + "（" + getPosition() + "）");
        IO.println("基本給: " + "%,d".formatted(baseSalary) + "円");
        showAllowance();
        IO.println("総給与: " + "%,d".formatted(getSalary()) + "円");
    }

    // 手当の行を表示する。一般社員は表示しない
    protected void showAllowance() {
    }
}
