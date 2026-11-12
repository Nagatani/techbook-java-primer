/**
 * 従業員を表すクラス（課題1の解答例）
 */
public class Employee {
    // privateフィールドを宣言する
    private String name;
    private int age;
    private double salary;
    
    // コンストラクタ
    public Employee(String name, int age, double salary) {
        this.name = name;
        setAge(age);       // 妥当性検証のためsetterを使用
        setSalary(salary); // 妥当性検証のためsetterを使用
    }
    
    // nameのgetterメソッド
    public String getName() {
        return name;
    }
    
    // nameのsetterメソッド
    public void setName(String name) {
        this.name = name;
    }
    
    // ageのgetterメソッド
    public int getAge() {
        return age;
    }
    
    // ageのsetterメソッド（妥当性検証付き）
    public void setAge(int age) {
        if (age < 0) {
            IO.println("エラー: 年齢は0歳以上である必要があります。");
            return;
        }
        this.age = age;
        IO.println("年齢を" + age + "歳に変更しました。");
    }
    
    // salaryのgetterメソッド
    public double getSalary() {
        return salary;
    }
    
    // salaryのsetterメソッド（妥当性検証付き）
    public void setSalary(double salary) {
        if (salary < 0) {
            IO.println("エラー: 給与は0円以上である必要があります。");
            return;
        }
        this.salary = salary;
        IO.println("給与を" + "%,.0f".formatted(salary) + "円に変更しました。");
    }
    
    // 従業員情報を表示するメソッド
    public void showEmployeeInfo() {
        IO.println("従業員情報:");
        IO.println("名前: " + name);
        IO.println("年齢: " + age + "歳");
        IO.println("給与: " + "%,.0f".formatted(salary) + "円");
    }
}