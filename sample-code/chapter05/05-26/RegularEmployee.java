public class RegularEmployee extends Employee {
    public RegularEmployee(String employeeId, String name) {
        super(employeeId, name);
    }
    
    @Override
    public void modifySystemSettings() {
        // 一般従業員にはシステム管理権限がない！
        throw new UnsupportedOperationException("権限がありません");
    }
}
