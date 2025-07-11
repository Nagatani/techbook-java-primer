/**
 * リスト6-3
 * EmployeeManagementクラス
 * 
 * 元ファイル: chapter06-immutability-and-final.md (201行目)
 */

// EmployeeManagement.java
public class EmployeeManagement {
    public static void main(String[] args) {
        Employee emp = new Employee("山田 太郎", 30, 300000);
        emp.displayInfo();

        // フィールドへの直接アクセスはコンパイルエラーになる
        // emp.salary = -10000; 

        // setterを使って安全に値を変更
        emp.setSalary(320000);
        System.out.println("新しい給与: " + emp.getSalary());

        // ビジネスロジックの実行
        emp.giveRaise(5); // 5%昇給
        System.out.println("昇給後の給与: " + emp.getSalary());

        // 不正な値を設定しようとすると、例外が発生してプログラムが停止する
        try {
            emp.setAge(200);
        } catch (IllegalArgumentException e) {
            System.err.println("エラー: " + e.getMessage());
        }
        
        // 現在の状態を再表示
        emp.displayInfo();
    }
}