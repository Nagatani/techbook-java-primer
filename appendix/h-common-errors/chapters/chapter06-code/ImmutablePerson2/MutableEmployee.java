// 問題: 継承によって不変性が破綻
public class MutableEmployee extends ImmutablePerson {
    private String department;
    
    public MutableEmployee(String name, int age, String department) {
        super(name, age);
        this.department = department;
    }
    
    public void setDepartment(String department) {
        this.department = department; // 状態が変更可能
    }
    
    public String getDepartment() { return department; }
}
