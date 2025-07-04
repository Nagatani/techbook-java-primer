/**
 * 第4章 基本課題1: EmployeeTest クラス
 * 
 * Employeeクラスをテストするためのクラスです。
 * カプセル化と妥当性検証を確認してください。
 * 
 * 実行例:
 * === 従業員管理システム ===
 * 従業員情報:
 * 名前: 田中太郎
 * 年齢: 30歳
 * 給与: 400,000円
 * 
 * 年齢を35歳に変更しました。
 * 給与を450,000円に変更しました。
 * 
 * 更新後の従業員情報:
 * 名前: 田中太郎
 * 年齢: 35歳
 * 給与: 450,000円
 * 
 * エラー: 年齢は0歳以上である必要があります。
 * エラー: 給与は0円以上である必要があります。
 */
public class EmployeeTest {
    public static void main(String[] args) {
        System.out.println("=== 従業員管理システム ===");
        
        // TODO: Employeeオブジェクトを作成してください
        // Employee employee = new Employee("田中太郎", 30, 400000);
        // employee.showEmployeeInfo();
        // System.out.println();
        
        // TODO: getterメソッドを使って値を取得してください
        // System.out.println("取得したデータ:");
        // System.out.println("名前: " + employee.getName());
        // System.out.println("年齢: " + employee.getAge());
        // System.out.println("給与: " + employee.getSalary());
        // System.out.println();
        
        // TODO: setterメソッドを使って値を変更してください
        // employee.setAge(35);
        // employee.setSalary(450000);
        // System.out.println();
        
        // TODO: 更新後の情報を表示してください
        // System.out.println("更新後の従業員情報:");
        // employee.showEmployeeInfo();
        // System.out.println();
        
        // TODO: 不正な値を設定してエラーを確認してください
        // employee.setAge(-5);
        // employee.setSalary(-1000);
    }
}