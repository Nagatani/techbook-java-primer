# 第5章 パッケージとアクセス制御

## 📝 章末演習

本章で学んだ継承、ポリモーフィズム、メソッドオーバーライドを活用して、実践的な練習課題に取り組みましょう。

### 🎯 演習の目標
- 継承の概念と実装方法の理解
- superキーワードの使い方
- メソッドオーバーライドの実践
- ポリモーフィズムの基本概念
- 抽象クラスとインターフェイスの基本

### 📁 課題の場所
演習課題は `exercises/chapter05/` ディレクトリに用意されています：

```
exercises/chapter05/
├── basic/          # 基本課題（必須）
│   ├── README.md   # 課題の詳細説明
│   ├── Animal.java # 課題1: 継承階層
│   ├── Dog.java
│   ├── Cat.java
│   ├── AnimalTest.java
│   ├── Shape.java  # 課題2: 図形とポリモーフィズム
│   ├── Rectangle.java
│   ├── Circle.java
│   ├── ShapeTest.java
│   ├── Employee.java # 課題3: 従業員継承階層
│   ├── Manager.java
│   ├── Engineer.java
│   ├── EmployeeTest.java
│   ├── Vehicle.java # 課題4: 車両と多態性
│   ├── Car.java
│   ├── Motorcycle.java
│   └── VehicleTest.java
├── advanced/       # 発展課題（推奨）
└── challenge/      # 挑戦課題（上級者向け）
```

### 🚀 推奨する学習の進め方

1. **基本課題**から順番に取り組む
2. 各課題のREADME.mdで詳細を確認
3. TODOコメントを参考に実装
4. 継承関係とメソッドオーバーライドを理解する
5. ポリモーフィズムによる統一的な処理を実装する

基本課題が完了したら、`advanced/`の発展課題でより複雑な継承階層に挑戦してみましょう！

## 📋 本章の学習目標

### 前提知識
**必須前提**：
- 第4章までのオブジェクト指向概念の習得
- 複数クラスを含むプログラムの設計・実装経験
- クラスとインスタンスの基本概念の理解

**プロジェクト経験前提**：
- 中規模なクラス群を含むプログラムの開発経験
- 名前の衝突問題の実体験

### 学習目標
**知識理解目標**：
- パッケージシステムの設計思想と利点
- Java標準ライブラリの構造とパッケージ設計
- 4つのアクセスレベル（public、protected、package-private、private）の理解
- モジュラープログラミングの原則

**技能習得目標**：
- 適切なパッケージ構造の設計と実装
- import文の効率的な使用
- アクセス修飾子を使った適切な情報隠蔽
- パッケージを跨いだクラス間の連携実装

**アーキテクチャ能力目標**：
- 大規模システムを想定した構造化設計
- 再利用可能なライブラリ設計の基礎
- 依存関係の管理と最適化

**到達レベルの指標**：
- 複数パッケージで構成される中規模アプリケーションが設計・実装できる
- Java標準ライブラリを効果的に活用できる
- 適切なアクセス制御により保守性の高いコードが書ける
- プロジェクトの構造化とモジュール分割ができる

---

## 4.1 カプセル化とアクセス制限

オブジェクト指向プログラミングの重要な原則の1つである**カプセル化 (Encapsulation)** について詳しく学習しましょう。カプセル化とは、オブジェクトのデータ（フィールド）とそのデータを操作するメソッドを1つにまとめ、オブジェクトの内部構造を外部から隠蔽することです。

### なぜカプセル化が必要か？

もし、クラスのフィールドが外部から自由にアクセスできてしまうと、どうなるでしょうか？

```java
// カプセル化されていない例
public class BankAccount {
    public String ownerName;
    public double balance; // publicなので誰でも直接変更できてしまう
}

public class BankTest {
    public static void main(String[] args) {
        BankAccount account = new BankAccount();
        account.ownerName = "山田太郎";
        account.balance = 100000;

        // 外部から直接、不正な値に書き換えられてしまう
        account.balance = -50000; // 本来ありえない「負の残高」が設定できてしまう
        System.out.println(account.ownerName + "さんの残高: " + account.balance);
    }
}
```
このように、外部から直接データを操作できると、オブジェクトが不正な状態になったり、意図しない変更が加えられたりする危険性があります。

カプセル化は、このような問題を防ぎ、以下の利点をもたらします。

*   **データの保護（整合性の維持）**: 外部から直接フィールドにアクセスできなくすることで、意図しない値の書き換えや、不正な状態になることを防ぎます。
*   **保守性の向上**: クラスの内部実装（フィールドの持ち方やメソッド内のロジック）を変更しても、外部への影響を最小限に抑えることができます。公開しているメソッドの仕様が変わらなければ、内部は自由に変更できます。
*   **独立性と再利用性の向上**: 適切にカプセル化されたクラスは、部品としての独立性が高まり、ほかのプログラムでも安心して利用しやすくなります。

### アクセス修飾子によるアクセス制御

Javaでは、クラス、フィールド、メソッド、コンストラクタに対して**アクセス修飾子**を指定することで、外部からのアクセスレベルを制御します。

| 修飾子 | 同じクラス | 同じパッケージ | サブクラス (別パッケージ) | それ以外 (別パッケージ) | 説明 |
| :--- | :---: | :---: | :---: | :---: | :--- |
| `public` | ○ | ○ | ○ | ○ | **公開**。どこからでもアクセス可能。 |
| `protected` | ○ | ○ | ○ | × | **保護**。同じクラス、同じパッケージ、または別パッケージのサブクラスからアクセス可能。 |
| (修飾子なし) | ○ | ○ | × | × | **パッケージプライベート**。修飾子を記述しない場合。同じパッケージ内からのみアクセス可能。 |
| `private` | ○ | × | × | × | **非公開**。同じクラス内からのみアクセス可能。 |

**選択の基本方針:**
*   **フィールドは原則 `private`**: クラスの心臓部であるデータは、外部から直接触られないように隠します。
*   **メソッドは外部に公開するものだけ `public`**: 外部から使われることを意図した機能だけを公開し、内部だけで使う補助的なメソッドは `private` にします。
*   **迷ったら、できるだけ厳しい（狭い）範囲の修飾子を選ぶ**: 最初は `private` にしておき、必要に応じてアクセス範囲を広げていくのが安全なアプローチです。

### getter/setter パターンによる実践

カプセル化の最も一般的な実践方法が、`private`なフィールドと、それに対応する`public`な**getter**（ゲッタ／取得メソッド）と**setter**（セッタ／設定メソッド）を用意するパターンです。

-   **getter**: `private`なフィールドの値を読み取って返すメソッド。メソッド名は `getフィールド名()` とするのが慣例です（例： `getName()`）。
-   **setter**: `private`なフィールドに値を設定するメソッド。メソッド名は `setフィールド名()` とするのが慣例です（例： `setAge(int age)`）。

setterメソッドの重要な役割は、フィールドに値を設定する前に、その値が**妥当かどうかを検証（バリデーション）**できる点にあります。

#### 実践例：`Employee`クラス

```java
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
```

```java
// EmployeeManagement.java
public class EmployeeManagement {
    public static void main(String[] args) {
        Employee emp = new Employee("山田 太郎", 30, 300000);
        emp.displayInfo();

        // フィールドへの直接アクセスはコンパイルエラーになる
        // emp.salary = -10000; 

        // setterを使って安全に値を変更
        emp.setSalary(320000);
        System.out.println("新しい給与: " + emp.getSalary());

        // ビジネスロジックの実行
        emp.giveRaise(5); // 5%昇給
        System.out.println("昇給後の給与: " + emp.getSalary());

        // 不正な値を設定しようとすると、例外が発生してプログラムが停止する
        try {
            emp.setAge(200);
        } catch (IllegalArgumentException e) {
            System.err.println("エラー: " + e.getMessage());
        }
        
        // 現在の状態を再表示
        emp.displayInfo();
    }
}
```

この例のように、カプセル化はクラスを「データの番人」として機能させ、オブジェクトが常に正しく、一貫性のある状態を保つことを保証します。

## 4.2 パッケージによるクラスの整理

プログラムの規模が大きくなると、作成するクラスの数も増えていきます。すべてのクラスを同じ場所に置いていると、名前の衝突が起きたり、目的のクラスを探すのがたいへんになったりします。

そこでJavaでは、関連するクラスをグループ化するためのしくみとして**パッケージ (package)** が用意されています。

パッケージは、コンピュータのフォルダ（ディレクトリ）でファイルを整理するのと似ています。

### パッケージの役割

*   **名前空間の提供**: パッケージが異なれば、同じ名前のクラスを定義できます。これにより、Java標準ライブラリのクラス名（例： `List`）や、外部ライブラリのクラス名と偶然同じ名前を付けてしまっても、衝突を避けられます。クラスの完全な名前は `パッケージ名.クラス名` となります（例： `java.util.List`）。
*   **クラスの分類**: 機能や役割に応じてクラスを分類することで、プロジェクトの構造が分かりやすくなります。たとえば、データモデル関連のクラスを`model`パッケージに、UI関連のクラスを`ui`パッケージにまとめる、といった使い方ができます。
*   **アクセス制御**: パッケージはアクセス制御の単位にもなります。アクセス修飾子を何も付けない（`default`）場合、そのメンバーは同じパッケージ内のクラスからのみアクセス可能になります。

### パッケージの宣言とディレクトリ構造

クラスがどのパッケージに属するかを指定するには、ソースファイルの先頭で`package`文を使います。

```java
package com.example.geometry; // このファイル内のクラスは com.example.geometry パッケージに属する

public class Circle {
    // ...
}
```

この`package`宣言は、ソースファイルの**物理的なディレクトリ構造と一致している必要があります**。`com.example.geometry`パッケージの場合、ソースファイルは以下のようなディレクトリに配置されている必要があります。

```
(プロジェクトのソースルート)
└── com
    └── example
        └── geometry
            ├── Circle.java
            └── Rectangle.java
```

### パッケージの命名規則

パッケージ名が世界中で一意（ユニーク）になるように、Javaでは組織が所有する**インターネットのドメイン名を逆順にする**ことが推奨されています。

*   例： `example.com` というドメインを持つ組織なら、`com.example.プロジェクト名.機能名` のように命名します。

この規則は、特にライブラリを公開する場合に重要です。個人学習や組織内での利用の場合は、必ずしもこの規則に従う必要はありませんが、`「逆引きドメイン名」.プロジェクト名` のような構造的な命名を心がけると良いでしょう。また、パッケージ名は**すべて小文字**で記述するのが慣例です。

## 4.3 import文によるクラスの利用

他のパッケージに属するクラスを利用するには、本来`パッケージ名.クラス名`という**完全限定名 (Fully Qualified Name)** で記述する必要があります。

```java
// importを使わない場合
java.util.Scanner scanner = new java.util.Scanner(System.in);
java.util.ArrayList<String> list = new java.util.ArrayList<>();
```

しかし、これではコードが長くなり、可読性が低下します。そこで、ソースファイルの先頭（`package`文の後）に`import`文を記述することで、クラス名を短く書けます。

```java
import java.util.Scanner; // java.util.Scannerクラスをインポート
import java.util.ArrayList; // java.util.ArrayListクラスをインポート

public class MyProgram {
    public static void main(String[] args) {
        // importしているので、クラス名だけで書ける
        Scanner scanner = new Scanner(System.in);
        ArrayList<String> list = new ArrayList<>();
    }
}
```

### オンデマンドインポート

同じパッケージの多くのクラスを使いたい場合、アスタリスク `*` を使って、そのパッケージに属するすべての`public`なクラスをまとめてインポートできます。これを**オンデマンドインポート**と呼びます。

```java
import java.util.*; // java.utilパッケージの全クラスを対象にする

public class MyProgram {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ArrayList<String> list = new ArrayList<>();
        Random random = new Random();
    }
}
```
**注意:** オンデマンドインポートは、そのパッケージのさらに下のサブパッケージのクラスまではインポートしません（例： `import java.util.*` は `java.util.regex.Pattern` をインポートしない）。

### `import`文の注意点：名前の衝突

異なるパッケージに同じ名前のクラスが存在する場合、両方を同時にオンデマンドインポートしたり、個別にインポートしたりすると、コンパイラがどちらのクラスを使えばよいか判断できず、コンパイルエラーになります。

```java
import java.util.List;
import java.awt.List; // エラー: Listクラスが両方のパッケージに存在する

public class AmbiguousClass {
    // List list; // どちらのListか不明
}
```

このような場合は、片方を`import`し、もう片方は完全限定名で記述して、どちらのクラスを使うかを明示的に指定する必要があります。

```java
import java.util.List; // java.util.Listを主に使うと決める

public class SolvedAmbiguity {
    public static void main(String[] args) {
        List<String> utilList = new java.util.ArrayList<>(); // importした方
        java.awt.List awtList = new java.awt.List();      // 完全限定名で指定
    }
}
```

## 4.4 章末演習

本章で学んだパッケージと`import`の概念を実践的に理解するため、以下の演習に取り組みましょう。

### 演習：図形計算プログラムのパッケージ分割

**目的:** 役割の異なるクラスを別々のパッケージに整理し、`import`文を使って連携させる。

**手順:**

1.  **プロジェクトの準備**: IntelliJ IDEAで新しいプロジェクトを作成します。

2.  **`geometry`パッケージの作成**:
    *   `src`ディレクトリの下に、`com.example.geometry`というパッケージを作成します。
    *   このパッケージ内に、以下の2つのクラスを作成します。

    **`Circle.java`**
    ```java
    package com.example.geometry;

    public class Circle {
        private double radius;

        public Circle(double radius) {
            this.radius = (radius > 0) ? radius : 0;
        }

        public double getArea() {
            return Math.PI * radius * radius;
        }

        public double getCircumference() {
            return 2 * Math.PI * radius;
        }
    }
    ```

    **`Rectangle.java`**
    ```java
    package com.example.geometry;

    public class Rectangle {
        private double width;
        private double height;

        public Rectangle(double width, double height) {
            this.width = (width > 0) ? width : 0;
            this.height = (height > 0) ? height : 0;
        }

        public double getArea() {
            return width * height;
        }

        public double getPerimeter() {
            return 2 * (width + height);
        }
    }
    ```

3.  **`main`パッケージの作成**:
    *   `src`ディレクトリの下に、`com.example.main`というパッケージを作成します。
    *   このパッケージ内に、`Main.java`というクラスを作成します。

4.  **`Main.java`の実装**:
    *   `Main.java`で、`com.example.geometry`パッケージの`Circle`クラスと`Rectangle`クラスを`import`して利用するコードを記述します。

    **`Main.java`**
    ```java
    package com.example.main;

    // 別のパッケージにあるクラスを利用するためにimportする
    import com.example.geometry.Circle;
    import com.example.geometry.Rectangle;

    public class Main {
        public static void main(String[] args) {
            System.out.println("--- 図形計算プログラム ---");

            Circle circle = new Circle(5.0);
            System.out.println("円の面積: " + circle.getArea());
            System.out.println("円周の長さ: " + circle.getCircumference());

            System.out.println();

            Rectangle rectangle = new Rectangle(4.0, 6.0);
            System.out.println("長方形の面積: " + rectangle.getArea());
            System.out.println("長方形の周の長さ: " + rectangle.getPerimeter());
        }
    }
    ```

5.  **実行と確認**:
    *   `Main.java`を実行し、コンソールに円と長方形の計算結果が正しく表示されることを確認してください。

この演習を通じて、プログラムの構成要素をパッケージに分割して整理する方法と、`import`文を使ってそれらを連携させる基本的な流れを体験できます。
