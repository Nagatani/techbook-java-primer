/**
 * リスト5-10
 * SystemAdministratorクラス
 * 
 * 元ファイル: chapter05-inheritance-and-polymorphism.md (698行目)
 */
public class SystemAdministrator extends Employee {
    public SystemAdministrator(String employeeId, String name) {
        super(employeeId, name);
    }
    // modifySystemSettingsメソッドを適切に継承
}