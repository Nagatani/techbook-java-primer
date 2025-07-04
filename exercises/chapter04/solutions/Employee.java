/**
 * 第4章 演習問題1: Employeeクラスの解答例
 * 
 * 【学習ポイント】
 * - カプセル化の基本実装
 * - privateフィールドとpublicメソッドの設計
 * - getter/setterメソッドの実装
 * - データの妥当性検証
 * 
 * 【よくある間違いと対策】
 * 1. フィールドをpublicにしてしまう
 * 2. setterメソッドで妥当性チェックを忘れる
 * 3. getterメソッドで不変オブジェクトを返さない
 * 4. エラーメッセージが不明確
 */
public class Employee {
    // 【基本解答】privateフィールド - 外部から直接アクセス不可
    private String name;
    private int age;
    private double salary;
    
    // 【応用解答】追加のフィールド
    private String employeeId;
    private String department;
    private java.time.LocalDate hireDate;
    
    // 【基本解答】デフォルトコンストラクタ
    public Employee() {
        this("未設定", 0, 0.0);
    }
    
    // 【基本解答】基本情報のコンストラクタ
    public Employee(String name, int age, double salary) {
        setName(name);
        setAge(age);
        setSalary(salary);
        this.employeeId = generateEmployeeId();
        this.department = "未配属";
        this.hireDate = java.time.LocalDate.now();
    }
    
    // 【応用解答】完全なコンストラクタ
    public Employee(String name, int age, double salary, String department) {
        this(name, age, salary);
        setDepartment(department);
    }
    
    // 【発展解答】従業員IDの自動生成
    private String generateEmployeeId() {
        return "EMP" + String.format("%06d", (int)(Math.random() * 1000000));
    }
    
    // 【基本解答】名前のgetter/setter
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            System.out.println("エラー: 名前は空にできません。");
            this.name = "未設定";
            return;
        }
        
        // 【応用解答】名前の長さチェック
        if (name.length() > 50) {
            System.out.println("警告: 名前が長すぎます。50文字以内にしてください。");
            this.name = name.substring(0, 50);
            return;
        }
        
        this.name = name.trim();
    }
    
    // 【基本解答】年齢のgetter/setter
    public int getAge() {
        return age;
    }
    
    public void setAge(int age) {
        if (age < 0) {
            System.out.println("エラー: 年齢は0歳以上である必要があります。");
            return;
        }
        
        // 【応用解答】現実的な年齢制限
        if (age > 120) {
            System.out.println("警告: 年齢が現実的ではありません。確認してください。");
        }
        
        // 【発展解答】就労年齢の確認
        if (age < 18) {
            System.out.println("注意: 18歳未満の従業員です。労働基準法を確認してください。");
        }
        
        this.age = age;
    }
    
    // 【基本解答】給与のgetter/setter
    public double getSalary() {
        return salary;
    }
    
    public void setSalary(double salary) {
        if (salary < 0) {
            System.out.println("エラー: 給与は0円以上である必要があります。");
            return;
        }
        
        // 【応用解答】最低賃金のチェック（例：時給1000円×8時間×20日）
        double minimumMonthlySalary = 160000.0;
        if (salary > 0 && salary < minimumMonthlySalary) {
            System.out.println("警告: 給与が最低賃金を下回る可能性があります。");
        }
        
        // 【発展解答】異常に高い給与のチェック
        if (salary > 10000000.0) {
            System.out.println("注意: 給与が非常に高額です。確認してください。");
        }
        
        this.salary = salary;
    }
    
    // 【応用解答】従業員IDのgetter（setterは提供しない - 不変）
    public String getEmployeeId() {
        return employeeId;
    }
    
    // 【応用解答】部署のgetter/setter
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        if (department == null || department.trim().isEmpty()) {
            this.department = "未配属";
        } else {
            this.department = department.trim();
        }
    }
    
    // 【応用解答】入社日のgetter/setter
    public java.time.LocalDate getHireDate() {
        return hireDate;
    }
    
    public void setHireDate(java.time.LocalDate hireDate) {
        if (hireDate == null) {
            System.out.println("エラー: 入社日を設定してください。");
            return;
        }
        
        // 【発展解答】未来の日付チェック
        if (hireDate.isAfter(java.time.LocalDate.now())) {
            System.out.println("警告: 入社日が未来の日付です。");
        }
        
        this.hireDate = hireDate;
    }
    
    // 【基本解答】情報表示メソッド
    public void displayInfo() {
        System.out.println("名前: " + name);
        System.out.println("年齢: " + age + "歳");
        System.out.printf("給与: %.0f円%n", salary);
    }
    
    // 【応用解答】詳細情報表示メソッド
    public void displayDetailedInfo() {
        System.out.println("=== 従業員情報 ===");
        System.out.println("従業員ID: " + employeeId);
        System.out.println("名前: " + name);
        System.out.println("年齢: " + age + "歳");
        System.out.printf("給与: %,.0f円%n", salary);
        System.out.println("部署: " + department);
        System.out.println("入社日: " + hireDate);
        System.out.println("勤続年数: " + getYearsOfService() + "年");
        System.out.println("================");
    }
    
    // 【発展解答】勤続年数の計算
    public int getYearsOfService() {
        if (hireDate == null) return 0;
        return java.time.Period.between(hireDate, java.time.LocalDate.now()).getYears();
    }
    
    // 【発展解答】年収の計算
    public double getAnnualSalary() {
        return salary * 12;
    }
    
    // 【発展解答】昇給メソッド
    public void giveRaise(double amount) {
        if (amount <= 0) {
            System.out.println("エラー: 昇給額は正の値である必要があります。");
            return;
        }
        
        double oldSalary = this.salary;
        setSalary(this.salary + amount);
        
        if (this.salary > oldSalary) {
            System.out.printf("昇給しました: %.0f円 → %.0f円 (+%.0f円)%n", 
                            oldSalary, this.salary, amount);
        }
    }
    
    // 【発展解答】昇給（割合指定）
    public void giveRaise(double percentage, boolean isPercentage) {
        if (!isPercentage) {
            giveRaise(percentage);
            return;
        }
        
        if (percentage <= 0) {
            System.out.println("エラー: 昇給率は正の値である必要があります。");
            return;
        }
        
        double raiseAmount = salary * (percentage / 100.0);
        giveRaise(raiseAmount);
    }
    
    // 【発展解答】従業員の比較メソッド
    public boolean isOlderThan(Employee other) {
        return this.age > other.age;
    }
    
    public boolean hasSameDepartment(Employee other) {
        return this.department.equals(other.department);
    }
    
    public boolean earnMoreThan(Employee other) {
        return this.salary > other.salary;
    }
    
    // 【発展解答】退職年数の計算
    public int getYearsUntilRetirement() {
        int retirementAge = 65;  // 標準的な退職年齢
        return Math.max(0, retirementAge - age);
    }
    
    // 【発展解答】従業員の状態判定
    public boolean isNewEmployee() {
        return getYearsOfService() < 1;
    }
    
    public boolean isVeteranEmployee() {
        return getYearsOfService() >= 10;
    }
    
    public boolean isNearRetirement() {
        return getYearsUntilRetirement() <= 5;
    }
    
    // 【発展解答】データの妥当性チェック
    public boolean isValidEmployee() {
        return name != null && !name.equals("未設定") &&
               age >= 18 && age <= 120 &&
               salary >= 0 &&
               department != null &&
               employeeId != null;
    }
    
    // toString()メソッドのオーバーライド
    @Override
    public String toString() {
        return String.format("Employee{id='%s', name='%s', age=%d, salary=%.0f, dept='%s'}", 
                           employeeId, name, age, salary, department);
    }
    
    // 【発展解答】equals()メソッドのオーバーライド
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Employee employee = (Employee) obj;
        return employeeId.equals(employee.employeeId);  // IDで判定
    }
    
    // 【発展解答】hashCode()メソッドのオーバーライド
    @Override
    public int hashCode() {
        return employeeId.hashCode();
    }
    
    // 【発展解答】静的メソッド - 従業員の比較
    public static Employee getHigherPaidEmployee(Employee emp1, Employee emp2) {
        return emp1.getSalary() >= emp2.getSalary() ? emp1 : emp2;
    }
    
    // 【発展解答】静的メソッド - 従業員情報の妥当性チェック
    public static boolean isValidEmployeeData(String name, int age, double salary) {
        return name != null && !name.trim().isEmpty() &&
               age >= 18 && age <= 120 &&
               salary >= 0;
    }
    
    // 【発展解答】静的メソッド - 複数従業員の平均給与
    public static double getAverageSalary(Employee[] employees) {
        if (employees.length == 0) return 0.0;
        
        double totalSalary = 0.0;
        for (Employee emp : employees) {
            totalSalary += emp.getSalary();
        }
        return totalSalary / employees.length;
    }
}