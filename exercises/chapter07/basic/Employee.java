package chapter07.basic;

/**
 * 従業員を表すクラス
 * 
 * Payableインターフェイスを実装して、給与計算機能を提供します。
 */
public class Employee implements Payable {
    // 従業員の名前
    private String name;
    // 従業員ID
    private String employeeId;
    // 基本給
    private double baseSalary;
    // ボーナス率（0.0〜1.0）
    private double bonusRate;
    
    /**
     * コンストラクタ
     * @param name 従業員の名前
     * @param employeeId 従業員ID
     * @param baseSalary 基本給
     * @param bonusRate ボーナス率（0.0〜1.0）
     */
    public Employee(String name, String employeeId, double baseSalary, double bonusRate) {
        // TODO: 各フィールドを初期化してください
        
    }
    
    /**
     * 給与（支払い金額）を計算する
     * TODO: 基本給 + (基本給 × ボーナス率) で計算してください
     * @return 給与額
     */
    @Override
    public double calculatePayment() {
        // TODO: 実装してください
        return 0.0;
    }
    
    /**
     * 従業員情報を表示する
     * TODO: 従業員の名前、ID、基本給、ボーナス率、総支給額を表示してください
     */
    public void displayEmployeeInfo() {
        // TODO: 実装してください
        System.out.println("従業員情報:");
        // 名前、ID、基本給、ボーナス率、総支給額を表示
    }
    
    // ゲッターメソッド
    public String getName() {
        return name;
    }
    
    public String getEmployeeId() {
        return employeeId;
    }
    
    public double getBaseSalary() {
        return baseSalary;
    }
    
    public double getBonusRate() {
        return bonusRate;
    }
}