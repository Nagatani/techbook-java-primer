/**
 * 第4章 演習問題1: EmployeeTestクラスの解答例
 * 
 * 【テストの目的】
 * - カプセル化の動作確認
 * - getter/setterメソッドの正常動作確認
 * - データ妥当性検証の動作確認
 * - エラーハンドリングの確認
 * 
 * 【デバッグのコツ】
 * 1. setterメソッドの戻り値や例外を確認
 * 2. 境界値での動作を詳しくテスト
 * 3. エラーメッセージの内容を確認
 * 4. privateフィールドに直接アクセスできないことを確認
 */
public class EmployeeTest {
    public static void main(String[] args) {
        System.out.println("=== 第4章 演習問題1: Employee クラスのテスト ===");
        
        // 【基本テスト】コンストラクタとgetter/setterのテスト
        testBasicFunctionality();
        
        // 【基本テスト】データ妥当性検証のテスト
        testDataValidation();
        
        // 【応用テスト】エラーハンドリングのテスト
        testErrorHandling();
        
        // 【発展テスト】従業員管理機能のテスト
        testAdvancedFeatures();
        
        // 【実践テスト】実際の使用例
        testPracticalUsage();
        
        // 【カプセル化テスト】データ保護の確認
        testEncapsulation();
        
        System.out.println("\n=== テスト完了 ===");
    }
    
    /**
     * 基本機能のテスト
     */
    private static void testBasicFunctionality() {
        System.out.println("\n--- 基本機能のテスト ---");
        
        // デフォルトコンストラクタ
        Employee emp1 = new Employee();
        System.out.println("デフォルト従業員:");
        emp1.displayInfo();
        
        // 基本コンストラクタ
        Employee emp2 = new Employee("田中太郎", 30, 400000.0);
        System.out.println("\n基本従業員:");
        emp2.displayDetailedInfo();
        
        // 完全コンストラクタ
        Employee emp3 = new Employee("佐藤花子", 25, 350000.0, "開発部");
        System.out.println("完全従業員:");
        emp3.displayDetailedInfo();
        
        // getter/setterのテスト
        System.out.println("\n--- getter/setterのテスト ---");
        System.out.println("変更前:");
        System.out.println("名前: " + emp2.getName());
        System.out.println("年齢: " + emp2.getAge());
        System.out.println("給与: " + emp2.getSalary());
        
        emp2.setName("田中一郎");
        emp2.setAge(35);
        emp2.setSalary(450000.0);
        
        System.out.println("\n変更後:");
        System.out.println("名前: " + emp2.getName());
        System.out.println("年齢: " + emp2.getAge());
        System.out.println("給与: " + emp2.getSalary());
    }
    
    /**
     * データ妥当性検証のテスト
     */
    private static void testDataValidation() {
        System.out.println("\n--- データ妥当性検証のテスト ---");
        
        Employee employee = new Employee("テスト従業員", 30, 400000.0);
        
        // 年齢の妥当性検証
        System.out.println("年齢の妥当性検証:");
        employee.setAge(35);   // 正常値
        System.out.println("正常値(35): 年齢 = " + employee.getAge());
        
        employee.setAge(-5);   // 負の値
        System.out.println("負の値(-5): 年齢 = " + employee.getAge());
        
        employee.setAge(150);  // 異常に大きい値
        System.out.println("大きい値(150): 年齢 = " + employee.getAge());
        
        employee.setAge(16);   // 就労年齢未満
        System.out.println("就労年齢未満(16): 年齢 = " + employee.getAge());
        
        // 給与の妥当性検証
        System.out.println("\n給与の妥当性検証:");
        employee.setSalary(500000.0);  // 正常値
        System.out.println("正常値(500000): 給与 = " + employee.getSalary());
        
        employee.setSalary(-100000.0); // 負の値
        System.out.println("負の値(-100000): 給与 = " + employee.getSalary());
        
        employee.setSalary(100000.0);  // 最低賃金以下
        System.out.println("低賃金(100000): 給与 = " + employee.getSalary());
        
        employee.setSalary(15000000.0); // 異常に高い給与
        System.out.println("高額(15000000): 給与 = " + employee.getSalary());
        
        // 名前の妥当性検証
        System.out.println("\n名前の妥当性検証:");
        employee.setName("正常な名前");
        System.out.println("正常な名前: " + employee.getName());
        
        employee.setName("");  // 空文字
        System.out.println("空文字: " + employee.getName());
        
        employee.setName(null); // null
        System.out.println("null: " + employee.getName());
        
        employee.setName("   "); // 空白のみ
        System.out.println("空白のみ: " + employee.getName());
        
        // 長い名前のテスト
        String longName = "あ".repeat(60);
        employee.setName(longName);
        System.out.println("長い名前(60文字): 長さ = " + employee.getName().length());
    }
    
    /**
     * エラーハンドリングのテスト
     */
    private static void testErrorHandling() {
        System.out.println("\n--- エラーハンドリングのテスト ---");
        
        Employee employee = new Employee();
        
        // 境界値テスト
        System.out.println("境界値テスト:");
        
        // 年齢の境界値
        employee.setAge(0);    // 最小値
        System.out.println("年齢0: " + employee.getAge());
        
        employee.setAge(18);   // 就労最低年齢
        System.out.println("年齢18: " + employee.getAge());
        
        employee.setAge(65);   // 定年年齢
        System.out.println("年齢65: " + employee.getAge());
        
        // 給与の境界値
        employee.setSalary(0.0);      // 最小値
        System.out.println("給与0: " + employee.getSalary());
        
        employee.setSalary(160000.0); // 最低賃金ライン
        System.out.println("給与160000: " + employee.getSalary());
        
        // 特殊ケースのテスト
        System.out.println("\n特殊ケースのテスト:");
        
        // Double.MAX_VALUEでの給与設定
        employee.setSalary(Double.MAX_VALUE);
        System.out.println("最大値給与: " + employee.getSalary());
        
        // 非常に大きな年齢
        employee.setAge(Integer.MAX_VALUE);
        System.out.println("最大値年齢: " + employee.getAge());
    }
    
    /**
     * 従業員管理機能のテスト
     */
    private static void testAdvancedFeatures() {
        System.out.println("\n--- 従業員管理機能のテスト ---");
        
        Employee emp1 = new Employee("管理太郎", 45, 800000.0, "管理部");
        Employee emp2 = new Employee("開発花子", 28, 450000.0, "開発部");
        
        // 勤続年数と年収の計算
        System.out.println("勤続年数と年収:");
        System.out.println(emp1.getName() + " - 勤続年数: " + emp1.getYearsOfService() + "年");
        System.out.println(emp1.getName() + " - 年収: " + String.format("%.0f円", emp1.getAnnualSalary()));
        
        // 昇給のテスト
        System.out.println("\n昇給のテスト:");
        System.out.println("昇給前の給与: " + emp2.getSalary());
        emp2.giveRaise(50000.0);  // 5万円昇給
        
        System.out.println("割合昇給前の給与: " + emp2.getSalary());
        emp2.giveRaise(10.0, true);  // 10%昇給
        
        // 従業員の比較
        System.out.println("\n従業員の比較:");
        System.out.println(emp1.getName() + " > " + emp2.getName() + " (年齢): " + emp1.isOlderThan(emp2));
        System.out.println(emp1.getName() + " > " + emp2.getName() + " (給与): " + emp1.earnMoreThan(emp2));
        System.out.println("同じ部署: " + emp1.hasSameDepartment(emp2));
        
        // 従業員の状態判定
        System.out.println("\n従業員の状態判定:");
        System.out.println(emp1.getName() + " - 新人: " + emp1.isNewEmployee());
        System.out.println(emp1.getName() + " - ベテラン: " + emp1.isVeteranEmployee());
        System.out.println(emp1.getName() + " - 退職間近: " + emp1.isNearRetirement());
        System.out.println(emp1.getName() + " - 退職まで: " + emp1.getYearsUntilRetirement() + "年");
        
        // データの妥当性
        System.out.println("\nデータの妥当性:");
        System.out.println(emp1.getName() + " - 有効な従業員: " + emp1.isValidEmployee());
        System.out.println(emp2.getName() + " - 有効な従業員: " + emp2.isValidEmployee());
    }
    
    /**
     * 実践的な使用例のテスト
     */
    private static void testPracticalUsage() {
        System.out.println("\n--- 実践的な使用例のテスト ---");
        
        System.out.println("=== 従業員管理システム ===");
        
        // 複数の従業員を作成
        Employee[] employees = {
            new Employee("田中太郎", 30, 400000.0, "営業部"),
            new Employee("佐藤花子", 25, 350000.0, "開発部"),
            new Employee("鈴木一郎", 45, 600000.0, "管理部"),
            new Employee("高橋美咲", 35, 500000.0, "開発部"),
            new Employee("山田次郎", 28, 380000.0, "営業部")
        };
        
        System.out.println("全従業員の情報:");
        for (int i = 0; i < employees.length; i++) {
            System.out.println((i + 1) + ". " + employees[i].toString());
        }
        
        // 統計情報の計算
        double averageSalary = Employee.getAverageSalary(employees);
        System.out.printf("%n平均給与: %.0f円%n", averageSalary);
        
        // 最も給与の高い従業員
        Employee highestPaid = employees[0];
        for (Employee emp : employees) {
            if (emp.earnMoreThan(highestPaid)) {
                highestPaid = emp;
            }
        }
        System.out.println("最高給与の従業員: " + highestPaid.getName() + 
                         " (" + String.format("%.0f円", highestPaid.getSalary()) + ")");
        
        // 部署別の従業員数
        System.out.println("\n部署別従業員数:");
        String[] departments = {"営業部", "開発部", "管理部"};
        for (String dept : departments) {
            int count = 0;
            for (Employee emp : employees) {
                if (emp.getDepartment().equals(dept)) {
                    count++;
                }
            }
            System.out.println(dept + ": " + count + "人");
        }
        
        // 全従業員に昇給
        System.out.println("\n全従業員に5%昇給実施:");
        for (Employee emp : employees) {
            double oldSalary = emp.getSalary();
            emp.giveRaise(5.0, true);
            System.out.printf("%s: %.0f円 → %.0f円%n", 
                            emp.getName(), oldSalary, emp.getSalary());
        }
        
        // 更新後の平均給与
        double newAverageSalary = Employee.getAverageSalary(employees);
        System.out.printf("%n昇給後平均給与: %.0f円 (昇給前: %.0f円)%n", 
                         newAverageSalary, averageSalary);
    }
    
    /**
     * カプセル化のテスト
     */
    private static void testEncapsulation() {
        System.out.println("\n--- カプセル化のテスト ---");
        
        Employee employee = new Employee("カプセル太郎", 30, 400000.0);
        
        System.out.println("カプセル化の確認:");
        System.out.println("✓ フィールドはprivateで外部から直接アクセス不可");
        System.out.println("✓ getter/setterメソッドを通じてのみアクセス可能");
        System.out.println("✓ setterメソッドで妥当性検証を実施");
        System.out.println("✓ 不正なデータの設定を防止");
        
        // データの整合性確認
        System.out.println("\nデータの整合性確認:");
        System.out.println("従業員ID: " + employee.getEmployeeId() + " (変更不可)");
        System.out.println("名前: " + employee.getName());
        System.out.println("年齢: " + employee.getAge());
        System.out.println("給与: " + employee.getSalary());
        System.out.println("部署: " + employee.getDepartment());
        
        // 不正なデータ設定の試行
        System.out.println("\n不正なデータ設定の試行:");
        double originalSalary = employee.getSalary();
        employee.setSalary(-100000.0);  // 負の給与
        System.out.println("負の給与設定後の給与: " + employee.getSalary() + 
                         " (元の値: " + originalSalary + ")");
        
        int originalAge = employee.getAge();
        employee.setAge(-10);  // 負の年齢
        System.out.println("負の年齢設定後の年齢: " + employee.getAge() + 
                         " (元の値: " + originalAge + ")");
        
        System.out.println("→ カプセル化により不正なデータの設定が防がれています");
    }
    
    /**
     * 静的メソッドのテスト
     */
    private static void testStaticMethods() {
        System.out.println("\n--- 静的メソッドのテスト ---");
        
        // 従業員データの妥当性チェック
        System.out.println("従業員データの妥当性チェック:");
        System.out.println("正常データ: " + Employee.isValidEmployeeData("田中太郎", 30, 400000.0));
        System.out.println("負の年齢: " + Employee.isValidEmployeeData("田中太郎", -5, 400000.0));
        System.out.println("null名前: " + Employee.isValidEmployeeData(null, 30, 400000.0));
        System.out.println("負の給与: " + Employee.isValidEmployeeData("田中太郎", 30, -100000.0));
        
        // 二人の従業員の比較
        Employee emp1 = new Employee("高給太郎", 40, 800000.0);
        Employee emp2 = new Employee("普通花子", 30, 400000.0);
        
        Employee higherPaid = Employee.getHigherPaidEmployee(emp1, emp2);
        System.out.println("\nより高給な従業員: " + higherPaid.getName() + 
                         " (" + String.format("%.0f円", higherPaid.getSalary()) + ")");
    }
}