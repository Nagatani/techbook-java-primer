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
    //     // 検査はsetterと同じprivateメソッドで行う。setterを呼ぶと「変更しました」と表示されてしまう
    //     if (isValidAge(age)) {
    //         this.age = age;
    //     } else {
    //         IO.println("エラー: 年齢は0歳以上である必要があります。0歳にします。");
    //     }
    //     if (isValidSalary(salary)) {
    //         this.salary = salary;
    //     } else {
    //         IO.println("エラー: 給与は0円以上である必要があります。0円にします。");
    //     }
    // }
    
    // TODO: 年齢と給与の検査をprivateメソッドにまとめてください
    // private static boolean isValidAge(int age) {
    //     return age >= 0;
    // }
    
    // private static boolean isValidSalary(double salary) {
    //     return salary >= 0;
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
    //     if (!isValidAge(age)) {
    //         IO.println("エラー: 年齢は0歳以上である必要があります。");
    //         return;
    //     }
    //     this.age = age;
    //     IO.println("年齢を" + age + "歳に変更しました。");
    // }
    
    // TODO: salaryのgetterメソッドを実装してください
    // public double getSalary() {
    //     return salary;
    // }
    
    // TODO: salaryのsetterメソッドを実装してください（妥当性検証付き）
    // public void setSalary(double salary) {
    //     if (!isValidSalary(salary)) {
    //         IO.println("エラー: 給与は0円以上である必要があります。");
    //         return;
    //     }
    //     this.salary = salary;
    //     IO.println("給与を" + "%,.0f".formatted(salary) + "円に変更しました。");
    // }
    
    // TODO: 従業員情報を表示するメソッドを実装してください
    // public void showEmployeeInfo() {
    //     IO.println("従業員情報:");
    //     IO.println("名前: " + name);
    //     IO.println("年齢: " + age + "歳");
    //     IO.println("給与: " + "%,.0f".formatted(salary) + "円");
    // }
}