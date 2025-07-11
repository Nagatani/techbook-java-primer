/**
 * リスト6-2
 * Employeeクラス
 * 
 * 元ファイル: chapter06-immutability-and-final.md (127行目)
 */

// Employee.java
public class Employee {
    private String name; // private: このクラス内からのみアクセス可能
    private int age;     // private: このクラス内からのみアクセス可能
    private double salary;

    public Employee(String name, int age, double salary) {
        // コンストラクタでもsetterを呼ぶことで、生成時にもバリデーションを適用できる
        this.setName(name);
        this.setAge(age);
        this.setSalary(salary);
    }

    // nameフィールドのgetter
    public String getName() {
        return this.name;
    }

    // nameフィールドのsetter
    public void setName(String name) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name.trim();
        } else {
            // 不正な値の場合は例外を投げて処理を中断する（例外処理は後の章で学びます）
            throw new IllegalArgumentException("名前は空にできません。");
        }
    }

    // ageフィールドのgetter
    public int getAge() {
        return this.age;
    }

    // ageフィールドのsetter（バリデーション付き）
    public void setAge(int age) {
        if (age >= 18 && age < 150) {
            this.age = age;
        } else {
            throw new IllegalArgumentException("年齢は18～149の範囲で入力してください。");
        }
    }

    // salaryフィールドのgetter
    public double getSalary() {
        return this.salary;
    }
    
    // salaryフィールドのsetter
    public void setSalary(double salary) {
        if (salary >= 0) {
            this.salary = salary;
        } else {
            throw new IllegalArgumentException("給与は0以上である必要があります。");
        }
    }

    // 昇給メソッド（ビジネスロジック）
    public void giveRaise(double percentage) {
        if (percentage > 0) {
            // salaryフィールドの操作はクラス内部なので自由に行える
            this.salary *= (1 + percentage / 100.0);
        } else {
            throw new IllegalArgumentException("昇給率は正の値である必要があります。");
        }
    }

    public void displayInfo() {
        System.out.println("名前: " + this.name + ", 年齢: " + this.age + ", 給与: " + this.salary);
    }
}