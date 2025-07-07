# 第5章 継承とポリモーフィズム

## 本章の学習目標

### 前提知識

本章を学習する前に、必要な前提知識を確認しておきましょう。まず、第4章で学んだカプセル化とアクセス制御の概念を完全に理解していることが必須です。特に、`public`、`private`、`protected`などのアクセス修飾子が果たす役割と、それらが継承関係においてどのように機能するかを理解している必要があります。また、クラスの基本的な設計経験も不可欠です。これには、フィールドの定義、メソッドの実装、コンストラクタの作成といった基本技術が含まれます。これらの基礎がしっかりと身についていることで、継承とポリモーフィズムというより高度な概念をスムーズに理解できるようになります。

### 到達目標

#### 知識理解目標

本章では、オブジェクト指向プログラミングの中核となる重要な概念を理解します。まず、継承の概念とis-a関係について学びます。継承は単なるコードの再利用技術ではなく、オブジェクト間の意味的な関係を表現するための強力な機能です。`extends`キーワードを使ってクラスの継承関係を定義し、`super`キーワードを使って親クラスの機能にアクセスする方法を習得します。

メソッドオーバーライドのルールと目的も重要な学習ポイントです。オーバーライドによって、子クラスは親クラスの動作を特殊化し、より具体的な振る舞いを実装できます。さらに、ポリモーフィズム（多態性）の概念を理解することで、同じインターフェイスを通じて異なる実装を透過的に扱うことができ、これがプログラムに柔軟性と拡張性をもたらします。最後に、アップキャストとダウンキャスト、そして`instanceof`演算子の役割を学び、実行時の型判定と型変換の技術を身につけます。

#### 技能習得目標

本章では、継承とポリモーフィズムを実装するための具体的な技術を習得します。まず、`extends`キーワードを使って既存のクラスを継承し、新しいクラスを定義する方法を学びます。この際、親クラスの機能を適切に継承しながら、子クラス独自の機能を追加する方法を理解します。

次に、親クラスのメソッドを`@Override`アノテーションを使って正しくオーバーライドする技術を身につけます。このアノテーションはコンパイラによるチェックを可能にし、オーバーライドの正確性を保証します。また、親クラスのコンストラクタを`super()`で呼びだす方法も重要で、これにより親クラスの初期化処理を適切に実行し、オブジェクトの整合性を保ちます。

さらに、親クラスの型で子クラスのインスタンスを扱うことで、ポリモーフィズムを実践します。この技術により、柔軟で拡張性の高いコードを書くことができるようになります。最後に、`instanceof`演算子とキャストを使って、実行時にオブジェクトの実際の型を判定し、特定のサブクラスの機能を利用する方法を学びます。

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

**継承 (Inheritance)** とは、あるクラスが持つフィールドやメソッドなどの性質を、別のクラスが引き継ぐことができるしくみです。この機能により、コードの再利用性が高まり、保守性が向上します。

継承関係には、**親クラス（スーパークラス）**と**子クラス（サブクラス）**という2つの役割が存在します。親クラスは性質を継承される元のクラスで、共通の機能を定義します。一方、子クラスは性質を継承する先のクラスで、親クラスの機能を引き継ぎながら、独自の機能を追加したり、既存の機能を上書き（オーバーライド）したりできます。

これにより、子クラスは親クラスの機能を再利用しつつ、新しい機能を追加したり、既存の機能を変更（オーバーライド）したりできます。

### なぜ継承が必要か？

たとえば、ゲームに登場するさまざまなキャラクタを考えてみましょう。「勇者」「魔法使い」「戦士」は、それぞれ異なる能力を持っていますが、「名前」「HP」「MP」といった共通のパラメータや、「攻撃する」「防御する」といった共通の行動も持っています。

継承を使わない場合、それぞれのクラスで同じ内容のコードを何度も書く必要があり、非効率で間違いも起きやすくなります。

### 継承を使わない場合の深刻な問題

以下のコードは、継承を使わずに実装した場合の典型的な例です。このアプローチがなぜ保守性の観点から深刻な問題を引き起こすのか、具体的に見ていきましょう：

```java
// 継承を使わない場合の問題のあるコード
public class Hero {
    String name;
    int hp;
    int maxHp;  // 最大HPの管理も必要
    
    void attack() { 
        System.out.println(name + "が攻撃！");
    }
    
    void takeDamage(int damage) {
        hp -= damage;
        if (hp < 0) hp = 0;  // HPが負にならないよう制御
    }
}

public class Wizard {
    String name;      // Heroと完全に重複
    int hp;          // Heroと完全に重複
    int maxHp;       // Heroと完全に重複
    int mp;          // 魔法使い特有
    
    void attack() {  // Heroとほぼ同じ実装
        System.out.println(name + "が攻撃！");
    }
    
    void takeDamage(int damage) {  // Heroと完全に重複
        hp -= damage;
        if (hp < 0) hp = 0;  // このロジックも重複
    }
    
    void castSpell() { /* 魔法使い特有の処理 */ }
}
```

**このコードが引き起こす実際の問題**：

1. **バグ修正の見落とし**：`takeDamage`メソッドにバグが見つかった場合、すべてのクラスで個別に修正が必要。一つでも修正を忘れると、特定のキャラクタだけバグが残る

2. **仕様変更の困難さ**：例えば「ダメージを受けた時にログを出力する」という仕様追加があった場合、すべてのクラスを探して修正する必要がある

3. **一貫性の欠如**：開発者Aが`Hero`クラスを修正し、開発者Bが`Wizard`クラスを修正した場合、微妙に異なる実装になる可能性がある

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

継承を導入したリファクタリングによって、コードの品質が大幅に向上します。まず、コードの重複が除去されることで、保守性が飛躍的に向上します。同じコードを複数の場所に書く必要がなくなるため、バグの発生率が減り、修正も簡単になります。共通機能の変更が1箇所で済むことも大きな利点です。親クラスを修正するだけで、すべての子クラスに変更が反映されます。

また、各車両の特性をオーバーライドで表現できることで、柔軟な設計が可能になります。共通のインターフェイスを保ちながら、各子クラスが独自の実装を持つことができます。さらに、新しい車両タイプの追加が容易になります。親クラスを継承し、必要な部分だけをカスタマイズすることで、短時間で新しい機能を実装できます。このように、継承はソフトウェアの拡張性と保守性を大幅に向上させる強力な技術です。

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

継承を誤用すると、論理的に破綻したコードになってしまいます。実際の開発現場でよく見られる誤用パターンをいくつか見てみましょう。

#### 誤用例1：スタックがArrayListを継承

```java
// 悪い例：実装の詳細を継承してしまう
public class MyStack<E> extends ArrayList<E> {
    public void push(E item) {
        add(item);
    }
    
    public E pop() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        return remove(size() - 1);
    }
    
    public E peek() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        return get(size() - 1);
    }
}

// 問題点が露呈する使用例
public class StackProblem {
    public static void main(String[] args) {
        MyStack<String> stack = new MyStack<>();
        stack.push("first");
        stack.push("second");
        stack.push("third");
        
        // ArrayListのメソッドが露出してしまう
        stack.add(1, "WRONG!"); // スタックの途中に要素を挿入できてしまう
        stack.remove(0); // スタックの底から要素を削除できてしまう
        stack.clear(); // スタック全体をクリアできてしまう
        
        // スタックの約束（LIFO）が破られる
    }
}
```

**解決策：コンポジションを使用**

```java
// 良い例：内部実装を隠蔽
public class MyStack<E> {
    private final ArrayList<E> elements = new ArrayList<>(); // privateで隠蔽
    
    public void push(E item) {
        elements.add(item);
    }
    
    public E pop() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        return elements.remove(elements.size() - 1);
    }
    
    public E peek() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        return elements.get(elements.size() - 1);
    }
    
    public boolean isEmpty() {
        return elements.isEmpty();
    }
    
    public int size() {
        return elements.size();
    }
    // ArrayListの他のメソッドは公開されない
}
```

#### 誤用例2：鳥の階層での問題

```java
// 悪い例：すべての鳥が飛べるという誤った前提
public class Bird {
    protected String name;
    
    public Bird(String name) {
        this.name = name;
    }
    
    public void fly() {
        System.out.println(name + " が飛んでいる");
    }
}

public class Eagle extends Bird {
    public Eagle(String name) {
        super(name);
    }
    // flyメソッドを適切に継承
}

public class Penguin extends Bird {
    public Penguin(String name) {
        super(name);
    }
    
    @Override
    public void fly() {
        // ペンギンは飛べない！
        throw new UnsupportedOperationException("ペンギンは飛べません");
    }
}

// 使用時の問題
public class BirdPark {
    public static void makeBirdsFly(List<Bird> birds) {
        for (Bird bird : birds) {
            bird.fly(); // ペンギンで例外が発生！
        }
    }
}
```

**解決策：インターフェースによる能力の分離**

```java
// 良い例：能力をインターフェースで表現
public abstract class Bird {
    protected String name;
    
    public Bird(String name) {
        this.name = name;
    }
    
    public abstract void move();
}

// 飛行能力を表すインターフェース
interface Flyable {
    void fly();
}

// 泳ぐ能力を表すインターフェース
interface Swimmable {
    void swim();
}

public class Eagle extends Bird implements Flyable {
    public Eagle(String name) {
        super(name);
    }
    
    @Override
    public void move() {
        System.out.println(name + " が歩いている");
    }
    
    @Override
    public void fly() {
        System.out.println(name + " が大空を飛んでいる");
    }
}

public class Penguin extends Bird implements Swimmable {
    public Penguin(String name) {
        super(name);
    }
    
    @Override
    public void move() {
        System.out.println(name + " がよちよち歩いている");
    }
    
    @Override
    public void swim() {
        System.out.println(name + " が泳いでいる");
    }
}

// 安全な使用例
public class BirdPark {
    public static void makeFlyableBirdsFly(List<Flyable> flyables) {
        for (Flyable bird : flyables) {
            bird.fly(); // 飛べる鳥だけが対象
        }
    }
    
    public static void makeSwimmableBirdsSwim(List<Swimmable> swimmers) {
        for (Swimmable bird : swimmers) {
            bird.swim(); // 泳げる鳥だけが対象
        }
    }
}
```

#### 誤用例3：正方形と長方形の問題

有名な例として「正方形と長方形」の問題も見てみましょう。

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

### その他の継承の誤用パターン

継承の誤用は「正方形と長方形」以外にも多くのパターンがあります。以下によくある間違いを示します。

#### 誤用例1：実装の都合だけで継承を使う

```java
// 悪い例：Stackを継承したMyStack
public class MyStack<E> extends ArrayList<E> {
    public void push(E item) {
        add(item);
    }
    
    public E pop() {
        return remove(size() - 1);
    }
    
    public E peek() {
        return get(size() - 1);
    }
}

// 問題：ArrayListのすべてのメソッドが公開されてしまう
MyStack<String> stack = new MyStack<>();
stack.push("A");
stack.push("B");
stack.add(0, "C");  // スタックの途中に挿入できてしまう！
stack.remove(1);    // スタックの途中から削除できてしまう！
```

**改善策：コンポジション（委譲）を使う**

```java
// 良い例：ArrayListを内部で使用
public class MyStack<E> {
    private ArrayList<E> list = new ArrayList<>();
    
    public void push(E item) {
        list.add(item);
    }
    
    public E pop() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        return list.remove(list.size() - 1);
    }
    
    public E peek() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        return list.get(list.size() - 1);
    }
    
    public boolean isEmpty() {
        return list.isEmpty();
    }
    
    public int size() {
        return list.size();
    }
}
```

#### 誤用例2：多重継承の代替として無理な継承

```java
// 悪い例：「飛べる鳥」と「泳げる鳥」を無理に継承で表現
public class Bird {
    public void eat() { /* ... */ }
    public void sleep() { /* ... */ }
}

public class FlyingBird extends Bird {
    public void fly() { /* ... */ }
}

public class SwimmingBird extends Bird {
    public void swim() { /* ... */ }
}

// 問題：ペンギンは飛べないが泳げる、鴨は飛べて泳げる
// どちらを継承すればよい？
```

**改善策：インターフェースで能力を表現**

```java
// 良い例：インターフェースで能力を分離
public interface Flyable {
    void fly();
}

public interface Swimmable {
    void swim();
}

public class Bird {
    public void eat() { /* ... */ }
    public void sleep() { /* ... */ }
}

public class Duck extends Bird implements Flyable, Swimmable {
    @Override
    public void fly() {
        System.out.println("鴨が飛んでいる");
    }
    
    @Override
    public void swim() {
        System.out.println("鴨が泳いでいる");
    }
}

public class Penguin extends Bird implements Swimmable {
    @Override
    public void swim() {
        System.out.println("ペンギンが高速で泳いでいる");
    }
}

public class Eagle extends Bird implements Flyable {
    @Override
    public void fly() {
        System.out.println("鷲が高く飛んでいる");
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

メソッドオーバーライドを正しく行うためには、いくつかの重要なルールを守る必要があります。まず最も基本的なルールとして、メソッド名、引数の型・数・順序が親クラスのメソッドと**完全に一致**していなければなりません。このシグネチャ（メソッド名とパラメータリスト）が一致しない場合、それはオーバーライドではなく、単なる新しいメソッドの定義とみなされます。

次に、戻り値の型についても制約があります。基本的には、親クラスのメソッドと**同じ型**を返す必要があります。ただし、Java 5以降では共変戻り値型（covariant return type）がサポートされ、より具体的な（サブクラスの）型を返すことも許可されています。これにより、型安全性を保ちながらより精密な設計が可能になります。

さらに、アクセス修飾子に関する重要なルールがあります。オーバーライドするメソッドのアクセス修飾子は、親クラスのメソッドよりも**制限を緩くすることしかできません**。例えば、親クラスのメソッドが`protected`である場合、子クラスでは`protected`のままにするか、`public`に変更することはできますが、`private`に変更することはできません。この制約は、ポリモーフィズムの原則を守り、リスコフの置換原則を保証するために必要です。

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

### ポリモーフィズムの実践例：図形描画システム

より実践的な例として、図形描画システムを考えてみましょう。

**Before：ポリモーフィズムを使わない場合**

```java
// 図形の種類を列挙型で管理
enum ShapeType {
    CIRCLE, RECTANGLE, TRIANGLE
}

// すべての図形データを1つのクラスで管理（悪い設計）
class ShapeData {
    ShapeType type;
    // 円の属性
    double radius;
    // 長方形の属性
    double width, height;
    // 三角形の属性
    double base, triangleHeight;
    
    double calculateArea() {
        switch (type) {
            case CIRCLE:
                return Math.PI * radius * radius;
            case RECTANGLE:
                return width * height;
            case TRIANGLE:
                return 0.5 * base * triangleHeight;
            default:
                return 0;
        }
    }
    
    void draw() {
        switch (type) {
            case CIRCLE:
                System.out.println("円を描画：半径 = " + radius);
                break;
            case RECTANGLE:
                System.out.println("長方形を描画：幅 = " + width + ", 高さ = " + height);
                break;
            case TRIANGLE:
                System.out.println("三角形を描画：底辺 = " + base + ", 高さ = " + triangleHeight);
                break;
        }
    }
}

// 使用例
public class DrawingAppBefore {
    public static void main(String[] args) {
        ShapeData[] shapes = new ShapeData[3];
        
        // 円を作成
        shapes[0] = new ShapeData();
        shapes[0].type = ShapeType.CIRCLE;
        shapes[0].radius = 5.0;
        
        // 長方形を作成
        shapes[1] = new ShapeData();
        shapes[1].type = ShapeType.RECTANGLE;
        shapes[1].width = 10.0;
        shapes[1].height = 20.0;
        
        // 三角形を作成
        shapes[2] = new ShapeData();
        shapes[2].type = ShapeType.TRIANGLE;
        shapes[2].base = 15.0;
        shapes[2].triangleHeight = 8.0;
        
        // すべての図形を描画
        for (ShapeData shape : shapes) {
            shape.draw();
            System.out.println("面積: " + shape.calculateArea());
        }
    }
}
```

**After：ポリモーフィズムを使った場合**

```java
// 抽象基底クラス
abstract class Shape {
    abstract double calculateArea();
    abstract void draw();
}

// 円クラス
class Circle extends Shape {
    private double radius;
    
    public Circle(double radius) {
        this.radius = radius;
    }
    
    @Override
    double calculateArea() {
        return Math.PI * radius * radius;
    }
    
    @Override
    void draw() {
        System.out.println("円を描画：半径 = " + radius);
    }
}

// 長方形クラス
class Rectangle extends Shape {
    private double width;
    private double height;
    
    public Rectangle(double width, double height) {
        this.width = width;
        this.height = height;
    }
    
    @Override
    double calculateArea() {
        return width * height;
    }
    
    @Override
    void draw() {
        System.out.println("長方形を描画：幅 = " + width + ", 高さ = " + height);
    }
}

// 三角形クラス
class Triangle extends Shape {
    private double base;
    private double height;
    
    public Triangle(double base, double height) {
        this.base = base;
        this.height = height;
    }
    
    @Override
    double calculateArea() {
        return 0.5 * base * height;
    }
    
    @Override
    void draw() {
        System.out.println("三角形を描画：底辺 = " + base + ", 高さ = " + height);
    }
}

// 使用例
public class DrawingAppAfter {
    public static void main(String[] args) {
        Shape[] shapes = {
            new Circle(5.0),
            new Rectangle(10.0, 20.0),
            new Triangle(15.0, 8.0)
        };
        
        // すべての図形を描画（型を意識しない）
        for (Shape shape : shapes) {
            shape.draw();
            System.out.println("面積: " + shape.calculateArea());
        }
        
        // 新しい図形（五角形）を追加しても、このコードは変更不要
    }
}

// 新しい図形の追加が容易
class Pentagon extends Shape {
    private double side;
    
    public Pentagon(double side) {
        this.side = side;
    }
    
    @Override
    double calculateArea() {
        return 0.25 * Math.sqrt(5 * (5 + 2 * Math.sqrt(5))) * side * side;
    }
    
    @Override
    void draw() {
        System.out.println("五角形を描画：一辺 = " + side);
    }
}
```

**ポリモーフィズムがもたらす設計上の利点**

1. **Open/Closed原則の実現**: 拡張に対して開いており、修正に対して閉じている
2. **単一責任原則**: 各図形クラスは自身の描画と面積計算のみに責任を持つ
3. **依存関係逆転の原則**: 高レベルのコードが低レベルの詳細に依存しない
4. **テストの容易性**: 各図形クラスを独立してテスト可能
5. **並列開発**: 複数の開発者が異なる図形クラスを同時に開発可能

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

**技術的背景：給与計算システムにおける継承の実践的価値**

企業の人事・給与システムは、継承とポリモーフィズムが最も効果的に活用される領域の一つです。実際の企業では、以下のような複雑な雇用形態が存在します：

- **正社員**：基本給＋役職手当＋住宅手当＋家族手当
- **契約社員**：時給ベース＋深夜手当＋休日手当
- **パートタイマー**：時給ベース（最低賃金保証あり）
- **管理職**：年俸制＋業績連動ボーナス
- **営業職**：基本給＋インセンティブ（売上の〇％）

**継承を使わない場合の問題点：**
```java
// 悪い例：型ごとの条件分岐が膨大に
public double calculatePayroll(String employeeType, Map<String, Object> data) {
    if (employeeType.equals("MANAGER")) {
        double base = (Double) data.get("baseSalary");
        double bonus = (Double) data.get("bonus");
        return base + bonus;
    } else if (employeeType.equals("ENGINEER")) {
        double base = (Double) data.get("baseSalary");
        int overtime = (Integer) data.get("overtimeHours");
        return base + (overtime * 5000);
    } else if (employeeType.equals("SALES")) {
        // さらに複雑な計算...
    }
    // 新しい雇用形態が増えるたびにこのメソッドが肥大化
}
```

**継承とポリモーフィズムによる解決：**
- **拡張性**：新しい雇用形態の追加が容易（既存コードを変更せずに新クラスを追加）
- **保守性**：各雇用形態の給与計算ロジックが独立して管理される
- **法令対応**：労働基準法の改正時も、該当クラスのみ修正すればよい
- **テスト容易性**：各クラスの計算ロジックを個別にユニットテスト可能

この演習では、実際の給与システム開発で必要となる設計原則を、シンプルな例を通じて学習します。

**手順:**

この演習では、段階的にクラスを構築していきます。まず最初に、すべての従業員の基本となる`Employee`親クラスを作成します。`Employee.java`ファイルを作成し、従業員の名前を表す`name`（String型）と基本給を表す`baseSalary`（double型）の2つのフィールドを定義します。コンストラクタではこれらの値を初期化し、給与を計算する`calculateSalary()`メソッドでは基本給をそのまま返します。また、従業員情報を表示する`displayInfo()`メソッドでは、名前と計算後の給与を出力します。

次に、管理職を表す`Manager`クラスを作成します。`Manager.java`ファイルで`Employee`クラスを継承し、管理職独自の特性としてボーナスを表す`bonus`（double型）フィールドを追加します。コンストラクタでは、名前、基本給、ボーナスを受け取り、`super`キーワードを使って親クラスのコンストラクタを呼び出して名前と基本給を初期化します。そして、`calculateSalary()`メソッドをオーバーライドして、基本給にボーナスを加えた値を返すように実装します。

同様に、エンジニアを表す`Engineer`クラスも作成します。`Engineer.java`ファイルで`Employee`クラスを継承し、エンジニア独自の特性として残業時間を表す`overtimeHours`（int型）フィールドを追加します。コンストラクタでは名前、基本給、残業時間を初期化し、`calculateSalary()`メソッドをオーバーライドして、基本給に残業手当（残業時間 × 5000円）を加えた値を返すように実装します。

最後に、これらのクラスを統合的に扱う`PayrollSystem`クラスを作成します。`PayrollSystem.java`ファイルの`main`メソッド内で、`Employee`型の配列を作成し、その中に`Manager`と`Engineer`のインスタンスを複数格納します。これがポリモーフィズムの実践です。`for`ループを使って配列を処理し、各従業員の`displayInfo()`メソッドを呼び出すことで、それぞれの役職に応じた給与が正しく計算・表示されることを確認します。さらにチャレンジ課題として、ループの中で`instanceof`演算子を使って従業員の実際の型を判定し、`Manager`の場合は「役職： 管理職」と追加情報を表示する機能を実装してみましょう。

### スケルトンコード

演習を始めるための基本的な構造を以下に示します：

```java
// Employee.java (親クラス)
public class Employee {
    // TODO: フィールドの定義
    // - name (String型)
    // - baseSalary (double型)
    
    // TODO: コンストラクタ
    
    // TODO: calculateSalary()メソッド
    // 基本給をそのまま返す
    
    // TODO: displayInfo()メソッド
    // 名前と計算後の給与を表示
}

// Manager.java (子クラス)
public class Manager extends Employee {
    // TODO: 追加フィールド
    // - bonus (double型)
    
    // TODO: コンストラクタ
    // super()を使って親クラスのコンストラクタを呼び出す
    
    // TODO: calculateSalary()メソッドのオーバーライド
    // 基本給 + ボーナスを返す
}

// Engineer.java (子クラス)
public class Engineer extends Employee {
    // TODO: 追加フィールド
    // - overtimeHours (int型)
    
    // TODO: コンストラクタ
    
    // TODO: calculateSalary()メソッドのオーバーライド
    // 基本給 + (残業時間 × 5000円)を返す
}

// PayrollSystem.java (メインクラス)
public class PayrollSystem {
    public static void main(String[] args) {
        // TODO: Employee型の配列を作成
        Employee[] employees = new Employee[4];
        
        // TODO: 配列に各種従業員を格納
        // employees[0] = new Manager(...);
        // employees[1] = new Engineer(...);
        // ...
        
        // TODO: ループで各従業員の情報を表示
        // ポリモーフィズムが機能することを確認
    }
}
```