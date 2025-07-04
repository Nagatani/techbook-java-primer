/**
 * 第4章 基本課題1: Employeeクラスのカプセル化
 * 
 * 課題: 従業員を表すEmployeeクラスを作成し、適切なカプセル化を実装してください。
 * 
 * 要求仕様:
 * - privateフィールド: name（String）, age（int）, salary（double）
 * - publicなgetter/setterメソッド
 * - 年齢と給与の妥当性検証
 * - 情報表示メソッド
 * 
 * 評価ポイント:
 * - privateフィールドの適切な使用
 * - getter/setterメソッドの実装
 * - データの妥当性検証
 */
public class Employee {
    // TODO: privateフィールドを宣言してください
    // private String name;
    // private int age;
    // private double salary;
    
    // TODO: コンストラクタを実装してください
    // public Employee(String name, int age, double salary) {
    //     this.name = name;
    //     setAge(age);       // 妥当性検証のためsetterを使用
    //     setSalary(salary); // 妥当性検証のためsetterを使用
    // }
    
    // TODO: nameのgetterメソッドを実装してください
    // public String getName() {
    //     return name;
    // }
    
    // TODO: nameのsetterメソッドを実装してください
    // public void setName(String name) {
    //     this.name = name;
    // }
    
    // TODO: ageのgetterメソッドを実装してください
    // public int getAge() {
    //     return age;
    // }
    
    // TODO: ageのsetterメソッドを実装してください（妥当性検証付き）
    // public void setAge(int age) {
    //     if (age < 0) {
    //         System.out.println("エラー: 年齢は0歳以上である必要があります。");
    //         return;
    //     }
    //     this.age = age;
    //     System.out.println("年齢を" + age + "歳に変更しました。");
    // }
    
    // TODO: salaryのgetterメソッドを実装してください
    // public double getSalary() {
    //     return salary;
    // }
    
    // TODO: salaryのsetterメソッドを実装してください（妥当性検証付き）
    // public void setSalary(double salary) {
    //     if (salary < 0) {
    //         System.out.println("エラー: 給与は0円以上である必要があります。");
    //         return;
    //     }
    //     this.salary = salary;
    //     System.out.println("給与を" + String.format("%.0f", salary) + "円に変更しました。");
    // }
    
    // TODO: 従業員情報を表示するメソッドを実装してください
    // public void showEmployeeInfo() {
    //     System.out.println("従業員情報:");
    //     System.out.println("名前: " + name);
    //     System.out.println("年齢: " + age + "歳");
    //     System.out.println("給与: " + String.format("%.0f", salary) + "円");
    // }
}