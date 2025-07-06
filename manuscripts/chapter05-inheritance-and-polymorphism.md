# 第5章 継承とポリモーフィズム

## 本章の学習目標

### 前提知識
- **第4章のカプセル化とアクセス制御の理解**：`public`, `private`などの役割の理解
- **クラスの基本的な設計経験**：フィールド、メソッド、コンストラクタの作成経験

### 到達目標

#### 知識理解目標
- 継承の概念と、is-a関係の理解
- `extends`キーワードと`super`キーワードの役割
- メソッドオーバーライドのルールと目的
- ポリモーフィズム（多態性）の概念と、それがもたらす柔軟性の理解
- アップキャストとダウンキャスト、そして`instanceof`演算子の役割

#### 技能習得目標
- `extends`を使って既存のクラスを継承し、新しいクラスを定義する
- 親クラスのメソッドを`@Override`アノテーションを使って正しくオーバーライドする
- 親クラスのコンストラクタを`super()`で呼びだす
- 親クラスの型で子クラスのインスタンスを扱い、ポリモーフィズムを実践する
- `instanceof`とキャストを使い、特定のサブクラスの機能を利用する

#### 到達レベルの指標

| 到達レベル |
| :--- |
| 継承関係を適切に設計し、コードの再利用性を高めることができる |
| ポリモーフィズムを活用し、拡張しやすく保守性の高いコードを記述できる |
| is-a関係とhas-a関係を理解し、継承とコンポジションを適切に使い分けられる |
| 実行時のオブジェクトの型に応じて動的に振る舞いが変わるプログラムを作成できる |

---

## 5.1 継承：クラスの機能を引き継ぐ

オブジェクト指向プログラミングには、「カプセル化」「継承」「ポリモーフィズム」という三大原則があります。本章では、その中でも特にコードの再利用性と拡張性を高める「継承」と「ポリモーフィズム」について学びます。

**継承 (Inheritance)** とは、あるクラスが持つフィールドやメソッドなどの性質を、別のクラスが引き継ぐことができるしくみです。

- **親クラス（スーパークラス）**: 性質を継承される元のクラス。
- **子クラス（サブクラス）**: 性質を継承する先のクラス。

これにより、子クラスは親クラスの機能を再利用しつつ、新しい機能を追加したり、既存の機能を変更（オーバーライド）したりできます。

### なぜ継承が必要か？

たとえば、ゲームに登場するさまざまなキャラクタを考えてみましょう。「勇者」「魔法使い」「戦士」は、それぞれ異なる能力を持っていますが、「名前」「HP」「MP」といった共通のパラメータや、「攻撃する」「防御する」といった共通の行動も持っています。

継承を使わない場合、それぞれのクラスで同じ内容のコードを何度も書く必要があり、非効率で間違いも起きやすくなります。

```java
// 継承を使わない場合
public class Hero {
    String name;
    int hp;
    void attack() { /* ... */ }
}

public class Wizard {
    String name; // Heroと重複
    int hp;     // Heroと重複
    void attack() { /* ... */ } // Heroと重複
    void castSpell() { /* ... */ }
}
```

継承を使うと、これらの共通部分を`Character`という親クラスにまとめ、各職業クラスはそれを継承することで、重複をなくし、コードをすっきりとさせることができます。

### 段階的リファクタリング：重複コードから継承へ

実際の開発では、最初から完璧な継承構造を設計することは難しく、重複コードを発見してから継承を導入することがよくあります。その過程を段階的に見てみましょう。

**ステップ1：重複コードの発見**

```java
// 最初の実装：各クラスが独立して作成されている
public class Car {
    private String model;
    private String color;
    private int speed;
    
    public void start() {
        System.out.println(model + " のエンジンを始動");
    }
    
    public void accelerate() {
        speed += 10;
        System.out.println(model + " が加速: " + speed + "km/h");
    }
    
    public void brake() {
        speed = Math.max(0, speed - 10);
        System.out.println(model + " が減速: " + speed + "km/h");
    }
}

public class Truck {
    private String model;
    private String color;
    private int speed;
    private int loadCapacity; // トラック固有
    
    // Car と全く同じメソッド（重複！）
    public void start() {
        System.out.println(model + " のエンジンを始動");
    }
    
    public void accelerate() {
        speed += 10;
        System.out.println(model + " が加速: " + speed + "km/h");
    }
    
    public void brake() {
        speed = Math.max(0, speed - 10);
        System.out.println(model + " が減速: " + speed + "km/h");
    }
    
    // トラック固有のメソッド
    public void loadCargo(int weight) {
        System.out.println(weight + "kg の荷物を積載");
    }
}

public class Motorcycle {
    private String model;
    private String color;
    private int speed;
    
    // また同じメソッドの重複！
    public void start() {
        System.out.println(model + " のエンジンを始動");
    }
    
    public void accelerate() {
        speed += 10;
        System.out.println(model + " が加速: " + speed + "km/h");
    }
    
    public void brake() {
        speed = Math.max(0, speed - 10);
        System.out.println(model + " が減速: " + speed + "km/h");
    }
}
```

**ステップ2：共通部分の抽出**

```java
// 共通部分を親クラスとして抽出
public abstract class Vehicle {
    protected String model;
    protected String color;
    protected int speed;
    
    public Vehicle(String model, String color) {
        this.model = model;
        this.color = color;
        this.speed = 0;
    }
    
    // 共通メソッドを親クラスに移動
    public void start() {
        System.out.println(model + " のエンジンを始動");
    }
    
    public void accelerate() {
        speed += 10;
        System.out.println(model + " が加速: " + speed + "km/h");
    }
    
    public void brake() {
        speed = Math.max(0, speed - 10);
        System.out.println(model + " が減速: " + speed + "km/h");
    }
    
    // ゲッターとセッター
    public String getModel() { return model; }
    public int getSpeed() { return speed; }
}
```

**ステップ3：子クラスの再実装**

```java
// リファクタリング後：重複が除去された
public class Car extends Vehicle {
    public Car(String model, String color) {
        super(model, color);
    }
    
    // Car固有の機能があれば追加
    public void openTrunk() {
        System.out.println(model + " のトランクを開く");
    }
}

public class Truck extends Vehicle {
    private int loadCapacity;
    
    public Truck(String model, String color, int loadCapacity) {
        super(model, color);
        this.loadCapacity = loadCapacity;
    }
    
    // accelerateメソッドをオーバーライド（重い車両は加速が遅い）
    @Override
    public void accelerate() {
        speed += 5; // トラックは加速が遅い
        System.out.println(model + " がゆっくり加速: " + speed + "km/h");
    }
    
    public void loadCargo(int weight) {
        if (weight <= loadCapacity) {
            System.out.println(weight + "kg の荷物を積載");
        } else {
            System.out.println("積載量オーバー！");
        }
    }
}

public class Motorcycle extends Vehicle {
    public Motorcycle(String model, String color) {
        super(model, color);
    }
    
    // accelerateメソッドをオーバーライド（バイクは加速が速い）
    @Override
    public void accelerate() {
        speed += 20; // バイクは加速が速い
        System.out.println(model + " が素早く加速: " + speed + "km/h");
    }
    
    public void wheelie() {
        System.out.println(model + " がウィリー！");
    }
}
```

**リファクタリングの効果**
- コードの重複が除去され、保守性が向上
- 共通機能の変更が1箇所で済む
- 各車両の特性をオーバーライドで表現可能
- 新しい車両タイプの追加が容易

### 継承の書き方：`extends`

Javaで継承を行うには、子クラスの宣言時に`extends`キーワードを使います。

```java
// 親クラス
public class Character {
    String name;
    int hp;

    void attack() {
        System.out.println(this.name + "の攻撃！");
    }
}

// Characterクラスを継承した子クラス
public class Hero extends Character {
    // Characterのname, hp, attack()を自動的に引き継いでいる
    void specialMove() {
        System.out.println(this.name + "の必殺技！");
    }
}

// Characterクラスを継承した子クラス
public class Wizard extends Character {
    int mp; // Wizard独自のフィールド

    void castSpell() {
        System.out.println(this.name + "は魔法を唱えた！");
    }
}
```

### is-a関係

継承は、クラス間に「**is-a関係**」（〜は〜の一種である）が成り立つ場合に使うのが適切です。

- 「勇者(Hero) **is a** キャラクタ(Character)」
- 「魔法使い(Wizard) **is a** キャラクタ(Character)」

このような関係が成り立つ場合、継承の利用を検討します。一方、「車(Car) **has a** エンジン(Engine)」のような「has-a関係」の場合は、継承ではなく、フィールドとして持つ（コンポジション）方が適切です。

### 継承の誤用例：よくある間違い

継承を誤用すると、論理的に破綻したコードになってしまいます。有名な例として「正方形と長方形」の問題を見てみましょう。

```java
// 問題のあるコード：数学的には正方形は長方形の一種だが...
public class Rectangle {
    protected int width;
    protected int height;
    
    public Rectangle(int width, int height) {
        this.width = width;
        this.height = height;
    }
    
    public void setWidth(int width) {
        this.width = width;
    }
    
    public void setHeight(int height) {
        this.height = height;
    }
    
    public int getArea() {
        return width * height;
    }
}

// 正方形を長方形として継承すると問題が発生
public class Square extends Rectangle {
    public Square(int size) {
        super(size, size);
    }
    
    // 正方形では幅と高さは常に同じでなければならない
    @Override
    public void setWidth(int width) {
        this.width = width;
        this.height = width; // 高さも同じ値に！
    }
    
    @Override
    public void setHeight(int height) {
        this.width = height; // 幅も同じ値に！
        this.height = height;
    }
}

// 使用例で問題が明らかに
public class GeometryTest {
    public static void main(String[] args) {
        Rectangle rect = new Square(5);
        
        // 長方形として扱うコード
        rect.setWidth(10);  // 幅を10に
        rect.setHeight(5);  // 高さを5に
        
        // 期待値：10 × 5 = 50
        // 実際の結果：5 × 5 = 25（最後のsetHeightで幅も5になってしまう）
        System.out.println("面積: " + rect.getArea()); // 25が出力される
    }
}
```

この例は**リスコフの置換原則**に違反しています。子クラスは親クラスと置き換え可能でなければなりませんが、`Square`は`Rectangle`の期待される振る舞いを破壊してしまっています。

**解決策：コンポジションの使用**

```java
// 改善されたコード：継承ではなくインターフェースを使用
public interface Shape {
    int getArea();
}

public class Rectangle implements Shape {
    private final int width;
    private final int height;
    
    public Rectangle(int width, int height) {
        this.width = width;
        this.height = height;
    }
    
    @Override
    public int getArea() {
        return width * height;
    }
}

public class Square implements Shape {
    private final int size;
    
    public Square(int size) {
        this.size = size;
    }
    
    @Override
    public int getArea() {
        return size * size;
    }
}
```

### 継承の実践例

```java
// Main.java
public class Main {
    public static void main(String[] args) {
        Hero hero = new Hero();
        hero.name = "勇者"; // 親クラスのフィールドを利用
        hero.hp = 100;

        Wizard wizard = new Wizard();
        wizard.name = "魔法使い";
        wizard.hp = 70;
        wizard.mp = 50; // 子クラス独自のフィールド

        hero.attack();       // 親クラスのメソッドを利用
        hero.specialMove();  // 子クラス独自のメソッド

        wizard.attack();     // 親クラスのメソッドを利用
        wizard.castSpell();  // 子クラス独自のメソッド
    }
}
```

## 5.2 メソッドのオーバーライド

継承の強力な機能の1つが、**メソッドのオーバーライド (Method Overriding)** です。

オーバーライドとは、親クラスで定義されたメソッドを、子クラスで**再定義（上書き）**することです。これにより、子クラスは親クラスの基本的な振る舞いを引き継ぎつつ、自身の特性に合わせた具体的な振る舞いを実装できます。

たとえば、`Character`クラスの`attack()`メソッドは「〜の攻撃！」と表示するだけですが、「勇者」は剣で攻撃し、「魔法使い」は杖で攻撃するなど、職業によって攻撃方法は異なります。これをオーバーライドで表現します。

### オーバーライドのルール

1.  メソッド名、引数の型・数・順序が親クラスのメソッドと**完全に一致**していること。
2.  戻り値の型が親クラスのメソッドと**同じ**であること（または、より具体的な型（共変戻り値型）であること）。
3.  アクセス修飾子は、親クラスのメソッドよりも**制限を緩くすることしかできない**（例： `protected` → `public` はOK、`public` → `private` はNG）。

### `@Override`アノテーション

オーバーライドを行う際は、メソッドの直前に`@Override`という**アノテーション**を付けることが強く推奨されます。

これは、コンパイラに対して「このメソッドは親クラスのメソッドをオーバーライドする意図で書いています」と伝える目印です。もし、タイプミスなどでオーバーライドのルールを満たせていない場合、コンパイラがエラーを検知してくれます。

### `super`キーワード

子クラスのオーバーライドしたメソッド内から、親クラスの同名メソッドを呼び出したい場合があります。その際に使うのが`super`キーワードです。

`super.メソッド名()`とすることで、親クラスのメソッドを呼びだすことができます。これにより、親クラスの共通処理を活かしつつ、子クラス独自の処理を追加できます。

#### 実践例：`attack`メソッドのオーバーライド

```java
// 親クラス
public class Character {
    String name;
    int hp;

    public Character(String name, int hp) {
        this.name = name;
        this.hp = hp;
    }

    public void attack() {
        System.out.println(this.name + "の攻撃！");
    }
}

// 子クラス
public class Wizard extends Character {
    int mp;

    public Wizard(String name, int hp, int mp) {
        // super()で親クラスのコンストラクタを呼び出す
        super(name, hp); 
        this.mp = mp;
    }

    // attackメソッドをオーバーライド
    @Override
    public void attack() {
        System.out.println(this.name + "は杖で殴った！");
    }

    public void castSpell() {
        System.out.println(this.name + "は魔法を唱えた！");
    }
}

// 子クラス
public class Knight extends Character {
    public Knight(String name, int hp) {
        super(name, hp);
    }

    // attackメソッドをオーバーライドし、親の処理も利用する
    @Override
    public void attack() {
        super.attack(); // 親クラスのattack()を呼び出す
        System.out.println("追加で剣を振るった！"); // 子クラス独自の処理を追加
    }
}
```

### コンストラクタと`super()`

子クラスのコンストラクタでは、その処理の**一番初め**に、親クラスのコンストラクタを呼びだす必要があります。これは`super()`を使って行います。

-   もし、子クラスのコンストラクタで明示的に`super()`を呼び出さない場合、コンパイラは自動的に親クラスの**引数なしのコンストラクタ (`super()`)** を呼びだすコードを挿入します。
-   親クラスに引数なしのコンストラクタが存在しない場合は、子クラスのコンストラクタで必ず明示的に`super(...)`を呼び出さなければならず、さもないとコンパイルエラーになります。

## 5.3 ポリモーフィズム（多態性）

**ポリモーフィズム (Polymorphism)** は、ギリシャ語で「多くの形を持つ」という意味で、オブジェクト指向における最も強力で柔軟な概念の1つです。

ポリモーフィズムとは、**同じ型の変数や同じメソッド呼び出しが、実行時のオブジェクトの種類によって異なる振る舞いをする**性質を指します。

継承とオーバーライドは、このポリモーフィズムを実現するための土台となります。

### 親クラスの型で子クラスのインスタンスを扱う

Javaでは、親クラス型の変数に、その子クラスのインスタンスを代入できます。これを**アップキャスト**と呼びます。

```java
// 親クラス型の変数に、子クラスのインスタンスを代入
Character chara1 = new Hero("勇者", 100);
Character chara2 = new Wizard("魔法使い", 70, 50);
Character chara3 = new Knight("騎士", 120);
```

このとき、変数`chara1`, `chara2`, `chara3`はすべて`Character`型として扱われます。しかし、それぞれの変数が実際に指し示しているオブジェクトの実体は`Hero`, `Wizard`, `Knight`と異なります。

### 同じ呼び出しで、異なる振る舞いを実現する

ここからがポリモーフィズムの真骨頂です。これらの`Character`型の変数に対して`attack()`メソッドを呼びだすと、何が起こるでしょうか。

```java
chara1.attack(); // 実行結果: 勇者の攻撃！
chara2.attack(); // 実行結果: 魔法使いは杖で殴った！
chara3.attack(); // 実行結果: 騎士の攻撃！
                 //         追加で剣を振るった！
```

`chara2.attack()`という同じ呼び出し方にもかかわらず、Javaの実行環境は`chara2`が実際に`Wizard`オブジェクトを指していることを認識し、`Wizard`クラスでオーバーライドされた`attack()`メソッドを自動的に呼び出します。これがポリモーフィズムです。

### ポリモーフィズムの利点

この性質を利用すると、非常に柔軟で拡張性の高いプログラムを書くことができます。たとえば、さまざまなキャラクタをまとめて管理する配列を考えてみましょう。

```java
public class GameParty {
    public static void main(String[] args) {
        // 親クラスの配列に、様々な子クラスのインスタンスを格納できる
        Character[] party = new Character[3];
        party[0] = new Hero("勇者", 100);
        party[1] = new Wizard("魔法使い", 70, 50);
        party[2] = new Knight("騎士", 120);

        // パーティ全員で一斉攻撃！
        for (Character member : party) {
            // member変数の型はCharacterだが、実行時には
            // 実際のインスタンスのattack()が呼び出される
            member.attack(); 
        }
    }
}
```

この`for`ループの中では、`member`が`Hero`なのか`Wizard`なのかを一切気にする必要がありません。ただ`attack()`を呼びだすだけで、各キャラクタは自身の職業に合った攻撃を自動的に行ってくれます。

もし将来、「忍者 `Ninja`」という新しい職業クラスを追加したくなっても、`GameParty`クラスのコードは**一切変更する必要がありません**。`Ninja`クラスを作成し、`Character`を継承して`attack()`をオーバーライドし、`party`配列に追加するだけで、新しいキャラクタも問題なく動作します。これがポリモーフィズムがもたらす**拡張性**です。

### ポリモーフィズムのBefore/After比較

ポリモーフィズムを使わない場合と使った場合の違いを、実際のコードで比較してみましょう。

**Before：ポリモーフィズムを使わない場合**

```java
// 型ごとに別々の処理を書く必要がある
public class GamePartyBefore {
    public static void main(String[] args) {
        Hero hero = new Hero("勇者", 100);
        Wizard wizard = new Wizard("魔法使い", 70, 50);
        Knight knight = new Knight("騎士", 120);
        
        // それぞれの型に応じた攻撃処理
        System.out.println("=== パーティ全員の攻撃（ポリモーフィズムなし）===");
        
        // Heroの攻撃
        hero.attack();
        
        // Wizardの攻撃
        wizard.attack();
        
        // Knightの攻撃
        knight.attack();
        
        // 新しいメンバーを追加するたびに、ここにコードを追加する必要がある
        // Archer archer = new Archer("弓使い", 80);
        // archer.attack();
    }
    
    // 全員の合計ダメージを計算する場合も型別処理が必要
    public static int calculateTotalDamage(Hero hero, Wizard wizard, Knight knight) {
        int totalDamage = 0;
        
        // 各型ごとに個別に処理
        if (hero != null) {
            totalDamage += 50; // Heroの攻撃力
        }
        if (wizard != null) {
            totalDamage += 30; // Wizardの攻撃力
        }
        if (knight != null) {
            totalDamage += 70; // Knightの攻撃力
        }
        
        // 新しい型が増えるたびに、このメソッドも修正が必要
        return totalDamage;
    }
}
```

**After：ポリモーフィズムを使った場合**

```java
// 統一的な処理で全ての型を扱える
public class GamePartyAfter {
    public static void main(String[] args) {
        // ポリモーフィズム：親クラスの型で管理
        Character[] party = {
            new Hero("勇者", 100),
            new Wizard("魔法使い", 70, 50),
            new Knight("騎士", 120)
            // 新しいメンバーを追加しても、以下のコードは変更不要
            // new Archer("弓使い", 80)
        };
        
        System.out.println("=== パーティ全員の攻撃（ポリモーフィズムあり）===");
        
        // 統一的な処理で全員を扱える
        for (Character member : party) {
            member.attack(); // 実際の型に応じた攻撃が自動的に呼ばれる
        }
    }
    
    // 合計ダメージ計算も拡張性が高い
    public static int calculateTotalDamage(Character[] party) {
        int totalDamage = 0;
        
        // 型を意識せずに処理できる
        for (Character member : party) {
            totalDamage += member.getAttackPower(); // 各キャラクタの攻撃力を取得
        }
        
        return totalDamage;
    }
    
    // 特定の条件での処理も簡潔に書ける
    public static void healLowHpMembers(Character[] party) {
        for (Character member : party) {
            if (member.getHp() < 30) {
                member.heal(20); // HPが低いメンバーを回復
                System.out.println(member.getName() + " を回復しました");
            }
        }
    }
}

// 親クラスに共通インターフェースを定義
abstract class Character {
    protected String name;
    protected int hp;
    
    public abstract int getAttackPower();
    
    public void heal(int amount) {
        this.hp += amount;
    }
    
    public String getName() { return name; }
    public int getHp() { return hp; }
}
```

**ポリモーフィズムの利点まとめ**

1. **コードの簡潔性**: 型別の条件分岐が不要になり、コードがシンプルに
2. **拡張性**: 新しい型を追加してもクライアントコードの変更が不要
3. **保守性**: 処理の追加・変更が容易
4. **再利用性**: 汎用的なメソッドが書きやすい
5. **型安全性**: コンパイル時に型チェックが行われる

## 5.4 `instanceof`演算子とキャスト

親クラスの型でオブジェクトを扱っていると、そのオブジェクトがもともとどの具体的な子クラスのインスタンスだったかを知り、その子クラス独自の機能を使いたくなることがあります。

たとえば、`Character`型の変数`member`が、もし`Wizard`だったら`castSpell()`メソッドを呼びたい、という場合です。

```java
Character member = new Wizard("魔法使い", 70, 50);
// member.castSpell(); // コンパイルエラー！
```
これはコンパイルエラーになります。なぜなら、コンパイラは変数`member`を`Character`型としてしか認識しておらず、`Character`クラスには`castSpell()`メソッドが定義されていないからです。

### `instanceof`演算子：型の調査

`instanceof`演算子を使うと、あるオブジェクトが特定のクラス（またはそのサブクラス）のインスタンスであるかどうかを調べることができます。

`変数 instanceof クラス名` の形で使用し、結果は`boolean`値（`true`または`false`）で返されます。

### キャスト：型変換

オブジェクトが特定の子クラスのインスタンスであることが分かったら、その変数の型を一時的に子クラスの型に変換できます。これを**ダウンキャスト**と呼びます。

`(変換したい型)変数` のように記述します。

```java
public class GameParty {
    public static void main(String[] args) {
        Character[] party = {
            new Hero("勇者", 100),
            new Wizard("魔法使い", 70, 50),
            new Knight("騎士", 120)
        };

        for (Character member : party) {
            member.attack(); // これはポリモーフィズムでOK

            // もし、メンバーがWizardだったら、特別に魔法も使わせる
            if (member instanceof Wizard) {
                // memberをWizard型にダウンキャストする
                Wizard wizard = (Wizard) member; 
                wizard.castSpell();
            }
        }
    }
}
```

**注意:** `instanceof`でチェックせずにいきなりキャストしようとすると、もしオブジェクトがその型でなかった場合に`ClassCastException`という実行時エラーが発生します。必ず`instanceof`で確認してからキャストするのが安全です。

#### モダンJavaのパターンマッチング

Java 16から、`instanceof`とキャストをより簡潔に書ける「`instanceof`のパターンマッチング」が導入されました。

```java
// 従来の書き方
if (member instanceof Wizard) {
    Wizard wizard = (Wizard) member;
    wizard.castSpell();
}

// パターンマッチングを使った書き方 (Java 16以降)
if (member instanceof Wizard wizard) {
    // instanceofがtrueの場合、キャスト済みのwizard変数が使える
    wizard.castSpell();
}
```
この新しい書き方を使うと、より安全で読みやすいコードになります。

## 5.5 章末演習

### 演習：さまざまな従業員の給与計算

**目的:** 継承、オーバーライド、ポリモーフィズムを使って、異なる種類の従業員の給与計算を統一的に扱うプログラムを作成する。

**手順:**

1.  **`Employee`親クラスの作成**:
    *   `Employee.java`を作成します。
    *   フィールド： `name` (String), `baseSalary` (double)
    *   コンストラクタ： 名前と基本給を初期化する。
    *   メソッド： `calculateSalary()`: 基本給をそのまま返す。`displayInfo()`: 名前と計算後の給与を表示する。

2.  **`Manager`子クラスの作成**:
    *   `Employee`を継承した`Manager.java`を作成します。
    *   フィールド： `bonus` (double)
    *   コンストラクタ： 名前、基本給、ボーナスを初期化する（親クラスのコンストラクタを`super`で呼びだす）。
    *   メソッド： `calculateSalary()`を**オーバーライド**し、「基本給 + ボーナス」を返すように変更する。

3.  **`Engineer`子クラスの作成**:
    *   `Employee`を継承した`Engineer.java`を作成します。
    *   フィールド： `overtimeHours` (int)
    *   コンストラクタ： 名前、基本給、残業時間を初期化する。
    *   メソッド： `calculateSalary()`を**オーバーライド**し、「基本給 +（残業時間 * 5000）」を返すように変更する。

4.  **`PayrollSystem`実行クラスの作成**:
    *   `main`メソッドを持つ`PayrollSystem.java`を作成します。
    *   `Employee`型の配列を作成し、その中に`Manager`と`Engineer`のインスタンスを複数格納します。
    *   `for`ループを使って配列を処理し、各従業員の`displayInfo()`メソッドを呼び出します。ポリモーフィズムにより、それぞれの役職に応じた給与が正しく計算・表示されることを確認してください。
    *   （チャレンジ）ループの中で、`instanceof`を使って、もし従業員が`Manager`だったら「役職： 管理職」と追加で表示するようにしてみましょう。