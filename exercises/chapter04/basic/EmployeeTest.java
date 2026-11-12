/**
 * 第4章 基本課題1: EmployeeTest クラス
 * 
 * Employeeクラスをテストするためのクラスです。
 * カプセル化と妥当性検証を確認してください。
 * 
 * 実行例:
 * === 従業員管理システム ===
 * 年齢を30歳に変更しました。
 * 給与を400,000円に変更しました。
 * 従業員情報:
 * 名前: 田中太郎
 * 年齢: 30歳
 * 給与: 400,000円
 *
 * 取得したデータ:
 * 名前: 田中太郎
 * 年齢: 30
 * 給与: 400000.0
 *
 * 年齢を35歳に変更しました。
 * 給与を450,000円に変更しました。
 *
 * 更新後の従業員情報:
 * 従業員情報:
 * 名前: 田中太郎
 * 年齢: 35歳
 * 給与: 450,000円
 *
 * エラー: 年齢は0歳以上である必要があります。
 * エラー: 給与は0円以上である必要があります。
 */
public class EmployeeTest {
    public static void main(String[] args) {
        IO.println("=== 従業員管理システム ===");
        
        // TODO: Employeeオブジェクトを作成してください
        // Employee employee = new Employee("田中太郎", 30, 400000);
        // employee.showEmployeeInfo();
        // IO.println();
        
        // TODO: getterメソッドを使って値を取得してください
        // IO.println("取得したデータ:");
        // IO.println("名前: " + employee.getName());
        // IO.println("年齢: " + employee.getAge());
        // IO.println("給与: " + employee.getSalary());
        // IO.println();
        
        // TODO: setterメソッドを使って値を変更してください
        // employee.setAge(35);
        // employee.setSalary(450000);
        // IO.println();
        
        // TODO: 更新後の情報を表示してください
        // IO.println("更新後の従業員情報:");
        // employee.showEmployeeInfo();
        // IO.println();
        
        // TODO: 不正な値を設定してエラーを確認してください
        // employee.setAge(-5);
        // employee.setSalary(-1000);
    }
}