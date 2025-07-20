/**
 * リスト5-10
 * Employeeクラス（問題のある設計例）
 * 
 * 元ファイル: chapter05-inheritance-and-polymorphism.md (684行目)
 */
// 悪い例：すべての従業員がシステム管理権限を持つという誤った前提
public class Employee {
    protected String employeeId;
    protected String name;
    
    public Employee(String employeeId, String name) {
        this.employeeId = employeeId;
        this.name = name;
    }
    
    public void modifySystemSettings() {
        System.out.println(name + " がシステム設定を変更しました");
    }
}