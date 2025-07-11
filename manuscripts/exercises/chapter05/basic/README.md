# 第5章 基礎課題：継承とポリモーフィズムの実践

## 概要

本課題では、継承とポリモーフィズムの基本概念を実践的に学習します。親クラスと子クラスの適切な設計、メソッドのオーバーライド、ポリモーフィズムを活用した柔軟なプログラム設計を身につけます。

## 学習目標

- 継承関係の適切な設計（is-a関係の理解）
- メソッドのオーバーライドとsuper呼び出し
- ポリモーフィズムを活用した処理の実装
- instanceof演算子とキャストの適切な使用

## 課題1：従業員給与計算システム

### 要求仕様

様々な雇用形態の従業員の給与を計算するシステムを実装してください。

#### 基底クラス：Employee

```java
public abstract class Employee {
    // フィールド（protected）
    // - employeeId (String): 従業員ID
    // - name (String): 氏名
    // - department (String): 部署
    
    // コンストラクタ
    public Employee(String employeeId, String name, String department) {
        // フィールドの初期化
    }
    
    // 抽象メソッド
    public abstract double calculateMonthlySalary();
    
    // 共通メソッド
    public void displayInfo() {
        System.out.println("従業員ID: " + employeeId);
        System.out.println("氏名: " + name);
        System.out.println("部署: " + department);
        System.out.println("月給: " + calculateMonthlySalary() + "円");
    }
}
```

#### 子クラス1：FullTimeEmployee（正社員）

```java
public class FullTimeEmployee extends Employee {
    private double baseSalary;      // 基本給
    private double allowance;       // 諸手当
    private double bonus;           // ボーナス（年額）
    
    public FullTimeEmployee(String employeeId, String name, String department,
                           double baseSalary, double allowance, double bonus) {
        super(employeeId, name, department);
        this.baseSalary = baseSalary;
        this.allowance = allowance;
        this.bonus = bonus;
    }
    
    @Override
    public double calculateMonthlySalary() {
        // 月給 = 基本給 + 諸手当 + (ボーナス / 12)
        return baseSalary + allowance + (bonus / 12);
    }
    
    // 正社員固有のメソッド
    public void requestPaidLeave(int days) {
        System.out.println(name + "さんが" + days + "日間の有給休暇を申請しました。");
    }
}
```

#### 子クラス2：PartTimeEmployee（パートタイム従業員）

```java
public class PartTimeEmployee extends Employee {
    private double hourlyWage;      // 時給
    private int hoursWorked;        // 月間労働時間
    
    public PartTimeEmployee(String employeeId, String name, String department,
                           double hourlyWage) {
        super(employeeId, name, department);
        this.hourlyWage = hourlyWage;
        this.hoursWorked = 0;
    }
    
    @Override
    public double calculateMonthlySalary() {
        // 月給 = 時給 × 月間労働時間
        return hourlyWage * hoursWorked;
    }
    
    // パートタイム固有のメソッド
    public void recordHours(int hours) {
        this.hoursWorked += hours;
        System.out.println(hours + "時間の勤務を記録しました。");
    }
    
    public void resetHours() {
        this.hoursWorked = 0;
    }
}
```

#### 子クラス3：ContractEmployee（契約社員）

```java
public class ContractEmployee extends Employee {
    private double monthlyRate;     // 月額契約金
    private int contractMonths;     // 契約期間（月数）
    private int monthsWorked;       // 勤務済み月数
    
    public ContractEmployee(String employeeId, String name, String department,
                           double monthlyRate, int contractMonths) {
        super(employeeId, name, department);
        this.monthlyRate = monthlyRate;
        this.contractMonths = contractMonths;
        this.monthsWorked = 0;
    }
    
    @Override
    public double calculateMonthlySalary() {
        return monthlyRate;
    }
    
    // displayInfoメソッドのオーバーライド
    @Override
    public void displayInfo() {
        super.displayInfo();  // 親クラスのメソッドを呼び出し
        System.out.println("契約期間: " + contractMonths + "ヶ月");
        System.out.println("残り契約期間: " + (contractMonths - monthsWorked) + "ヶ月");
    }
    
    // 契約社員固有のメソッド
    public boolean isContractExpiring() {
        return (contractMonths - monthsWorked) <= 1;
    }
}
```

### 実装のヒント

1. **ポリモーフィズムの活用**：
   ```java
   public class PayrollSystem {
       private List<Employee> employees = new ArrayList<>();
       
       public void addEmployee(Employee employee) {
           employees.add(employee);
       }
       
       public double calculateTotalPayroll() {
           double total = 0;
           for (Employee emp : employees) {
               total += emp.calculateMonthlySalary();
           }
           return total;
       }
       
       public void displayAllEmployees() {
           for (Employee emp : employees) {
               emp.displayInfo();
               System.out.println("---");
           }
       }
   }
   ```

2. **instanceof演算子の使用例**：
   ```java
   public void processSpecialRequests() {
       for (Employee emp : employees) {
           if (emp instanceof FullTimeEmployee) {
               FullTimeEmployee ft = (FullTimeEmployee) emp;
               ft.requestPaidLeave(5);
           } else if (emp instanceof ContractEmployee) {
               ContractEmployee ct = (ContractEmployee) emp;
               if (ct.isContractExpiring()) {
                   System.out.println(ct.getName() + "さんの契約更新が必要です。");
               }
           }
       }
   }
   ```

### テストシナリオ

```java
public class PayrollTest {
    public static void main(String[] args) {
        PayrollSystem payroll = new PayrollSystem();
        
        // 様々な従業員を追加
        payroll.addEmployee(new FullTimeEmployee("FT001", "山田太郎", "営業部", 
                                                 300000, 50000, 600000));
        
        PartTimeEmployee pt1 = new PartTimeEmployee("PT001", "鈴木花子", "総務部", 1200);
        pt1.recordHours(120);
        payroll.addEmployee(pt1);
        
        payroll.addEmployee(new ContractEmployee("CT001", "田中一郎", "開発部", 
                                                400000, 6));
        
        // 全従業員の情報表示
        payroll.displayAllEmployees();
        
        // 給与総額の計算
        System.out.println("給与総額: " + payroll.calculateTotalPayroll() + "円");
    }
}
```

## 課題2：図形描画システム

### 要求仕様

様々な図形を扱う描画システムを実装してください。

#### 抽象基底クラス：Shape

```java
public abstract class Shape {
    protected String color;
    protected boolean filled;
    
    public Shape(String color, boolean filled) {
        this.color = color;
        this.filled = filled;
    }
    
    // 抽象メソッド
    public abstract double calculateArea();
    public abstract double calculatePerimeter();
    public abstract void draw();
    
    // 共通メソッド
    public void displayInfo() {
        System.out.println("色: " + color);
        System.out.println("塗りつぶし: " + (filled ? "あり" : "なし"));
        System.out.println("面積: " + calculateArea());
        System.out.println("周囲長: " + calculatePerimeter());
    }
}
```

#### 具体的な図形クラス

1. **Circle（円）**：
   ```java
   public class Circle extends Shape {
       private double radius;
       
       // calculateArea(): π × 半径²
       // calculatePerimeter(): 2 × π × 半径
   }
   ```

2. **Rectangle（長方形）**：
   ```java
   public class Rectangle extends Shape {
       protected double width;
       protected double height;
       
       // calculateArea(): 幅 × 高さ
       // calculatePerimeter(): 2 × (幅 + 高さ)
   }
   ```

3. **Square（正方形）**：Rectangleを継承
   ```java
   public class Square extends Rectangle {
       public Square(String color, boolean filled, double side) {
           super(color, filled, side, side);
       }
       
       // drawメソッドをオーバーライドして正方形特有の描画
   }
   ```

4. **Triangle（三角形）**：
   ```java
   public class Triangle extends Shape {
       private double sideA, sideB, sideC;
       
       // ヘロンの公式を使用して面積を計算
   }
   ```

### DrawingCanvasクラス

```java
public class DrawingCanvas {
    private List<Shape> shapes = new ArrayList<>();
    
    public void addShape(Shape shape) {
        shapes.add(shape);
    }
    
    public void drawAll() {
        for (Shape shape : shapes) {
            shape.draw();
            System.out.println();
        }
    }
    
    public double getTotalArea() {
        double total = 0;
        for (Shape shape : shapes) {
            total += shape.calculateArea();
        }
        return total;
    }
    
    // 特定の色の図形だけを描画
    public void drawByColor(String color) {
        for (Shape shape : shapes) {
            if (shape.getColor().equals(color)) {
                shape.draw();
            }
        }
    }
    
    // 円だけを大きい順に表示
    public void displayCirclesBySize() {
        List<Circle> circles = new ArrayList<>();
        for (Shape shape : shapes) {
            if (shape instanceof Circle) {
                circles.add((Circle) shape);
            }
        }
        
        // ソートして表示
        circles.sort((c1, c2) -> 
            Double.compare(c2.calculateArea(), c1.calculateArea()));
        
        for (Circle circle : circles) {
            circle.displayInfo();
        }
    }
}
```

## 課題3：ゲームキャラクターシステム

### 要求仕様

RPGゲームのキャラクターシステムを実装してください。

#### 基底クラス：GameCharacter

```java
public abstract class GameCharacter {
    protected String name;
    protected int level;
    protected int hp;
    protected int maxHp;
    protected int attackPower;
    protected int defense;
    
    // 基本的な行動
    public abstract void attack(GameCharacter target);
    public abstract void useSpecialAbility();
    
    // ダメージ処理
    public void takeDamage(int damage) {
        int actualDamage = Math.max(damage - defense, 0);
        hp = Math.max(hp - actualDamage, 0);
        System.out.println(name + "は" + actualDamage + "のダメージを受けた！");
        if (hp == 0) {
            System.out.println(name + "は倒れた！");
        }
    }
    
    // レベルアップ処理
    public void levelUp() {
        level++;
        onLevelUp();  // 子クラスでカスタマイズ
    }
    
    protected abstract void onLevelUp();
}
```

#### 具体的なキャラクタークラス

1. **Warrior（戦士）**：
   - 高いHPと防御力
   - 特殊能力：「狂戦士モード」（攻撃力2倍、防御力半減）

2. **Mage（魔法使い）**：
   - MPシステムを追加
   - 特殊能力：「メテオ」（全体攻撃）

3. **Healer（回復役）**：
   - 回復魔法を使用可能
   - 特殊能力：「全体回復」

4. **Rogue（盗賊）**：
   - 高い回避率
   - 特殊能力：「暗殺」（クリティカル攻撃）

### バトルシステムの実装

```java
public class BattleSystem {
    public void executeBattle(List<GameCharacter> party, List<GameCharacter> enemies) {
        System.out.println("バトル開始！");
        
        while (!isPartyDefeated(party) && !isPartyDefeated(enemies)) {
            // プレイヤーターン
            for (GameCharacter member : party) {
                if (member.getHp() > 0) {
                    GameCharacter target = selectTarget(enemies);
                    member.attack(target);
                    
                    // 特定条件で特殊能力を使用
                    if (shouldUseSpecialAbility(member)) {
                        member.useSpecialAbility();
                    }
                }
            }
            
            // エネミーターン
            // ...
        }
    }
}
```

## 評価ポイント

1. **継承関係の適切性**（30点）
   - is-a関係が正しく成立している
   - 共通機能が親クラスに適切に配置されている
   - 不適切な継承を避けている

2. **オーバーライドの実装**（25点）
   - @Overrideアノテーションの使用
   - superキーワードの適切な使用
   - オーバーライドルールの遵守

3. **ポリモーフィズムの活用**（25点）
   - 親クラス型での統一的な処理
   - 型に依存しない柔軟な設計
   - 拡張性の確保

4. **instanceof演算子の適切な使用**（20点）
   - 必要な場面でのみ使用
   - 安全なキャスト処理
   - 代替設計の検討

## 発展学習の提案

1. **テンプレートメソッドパターン**：
   - 処理の骨格を親クラスで定義
   - 詳細を子クラスで実装

2. **ファクトリパターン**：
   - オブジェクト生成の抽象化
   - 型に応じた適切なインスタンス生成

3. **Visitorパターン**：
   - instanceof地獄の回避
   - 処理の外部化

4. **共変戻り値型の活用**：
   - より型安全な設計
   - メソッドチェーンの実現

これらの基礎課題を通じて、継承とポリモーフィズムの本質を理解し、実践的に活用できるようになってください。