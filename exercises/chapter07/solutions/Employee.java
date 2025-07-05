package chapter07.solutions;

/**
 * 従業員を表すクラス
 * 
 * Payableインターフェースを実装し、給与計算システムで
 * 統一的に扱えるようにしています。
 */
public class Employee implements Payable {
    
    private String name;
    private double baseSalary;
    private double overtimeHours;
    private double overtimeRate;
    
    /**
     * 従業員のコンストラクタ
     * 
     * @param name 従業員名
     * @param baseSalary 基本給
     */
    public Employee(String name, double baseSalary) {
        this.name = name;
        this.baseSalary = baseSalary;
        this.overtimeHours = 0.0;
        this.overtimeRate = 1.25; // 残業代は25%増し
    }
    
    /**
     * 残業時間を設定する
     * 
     * @param overtimeHours 残業時間
     */
    public void setOvertimeHours(double overtimeHours) {
        this.overtimeHours = Math.max(0, overtimeHours);
    }
    
    /**
     * 残業代率を設定する
     * 
     * @param overtimeRate 残業代率（1.25なら25%増し）
     */
    public void setOvertimeRate(double overtimeRate) {
        this.overtimeRate = overtimeRate;
    }
    
    /**
     * 支払い金額（給与）を計算する
     * 基本給 + 残業代を返す
     * 
     * @return 支払い金額
     */
    @Override
    public double getPaymentAmount() {
        double hourlyRate = baseSalary / 160.0; // 月160時間勤務と仮定
        double overtimePay = hourlyRate * overtimeHours * overtimeRate;
        return baseSalary + overtimePay;
    }
    
    /**
     * 支払い対象の名前を取得する
     * 
     * @return 従業員名
     */
    @Override
    public String getPaymentName() {
        return name;
    }
    
    /**
     * 従業員の詳細情報を取得する
     * 
     * @return 従業員の詳細情報
     */
    @Override
    public String getPaymentDescription() {
        double hourlyRate = baseSalary / 160.0;
        double overtimePay = hourlyRate * overtimeHours * overtimeRate;
        
        return String.format(
            "従業員: %s\n" +
            "基本給: %.0f円\n" +
            "残業時間: %.1f時間\n" +
            "残業代: %.0f円\n" +
            "総支給額: %.0f円",
            name, baseSalary, overtimeHours, overtimePay, getPaymentAmount()
        );
    }
    
    // Getters
    public String getName() { return name; }
    public double getBaseSalary() { return baseSalary; }
    public double getOvertimeHours() { return overtimeHours; }
    public double getOvertimeRate() { return overtimeRate; }
}