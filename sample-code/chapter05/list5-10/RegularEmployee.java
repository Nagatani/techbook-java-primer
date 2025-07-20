/**
 * リスト5-10
 * RegularEmployeeクラス（一般従業員）
 * 
 * 元ファイル: chapter05-inheritance-and-polymorphism.md (705行目)
 */
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