<!-- 
校正チャンク情報
================
元ファイル: chapter03-oop-basics.md
チャンク: 5/12
行範囲: 908 - 1142
作成日時: 2025-08-02 14:34:01

校正時の注意事項:
- 文章の流れは前後のチャンクを考慮してください
- このヘッダーとフッターは校正対象外です
- 校正が完了したらステータスを「completed」に変更してください
================
-->

### コンストラクタ（初期化メソッド）

コンストラクタは、オブジェクトを作成するときに自動的に呼ばれる特別なメソッドです。

<span class="listing-number">**サンプルコード3-11**</span>

```java
public class Book {
    private String title;
    private String author;
    private int pages;
    
    // コンストラクタ（クラス名と同じ名前）
    public Book(String title, String author, int pages) {
        this.title = title;
        this.author = author;
        this.pages = pages;
    }
    
    // デフォルトコンストラクタ（引数なし）
    public Book() {
        this.title = "未設定";
        this.author = "不明";
        this.pages = 0;
    }
    
    public void displayInfo() {
        System.out.println("タイトル: " + title);
        System.out.println("著者: " + author);
        System.out.println("ページ数: " + pages);
    }
}
```

使用例と実行結果：
```java
// BookTest.java
public class BookTest {
    public static void main(String[] args) {
        // 引数ありコンストラクタで作成
        Book book1 = new Book("Java入門", "田中太郎", 300);
        book1.displayInfo();
        
        System.out.println("---");
        
        // デフォルトコンストラクタで作成
        Book book2 = new Book();
        book2.displayInfo();
    }
}
```

実行結果：
```
タイトル: Java入門
著者: 田中太郎
ページ数: 300
---
タイトル: 未設定
著者: 不明
ページ数: 0
```

### メソッドオーバーロード（同じ名前のメソッド）

Javaでは、同じ名前のメソッドを複数定義できます。これを「メソッドオーバーロード」と呼びます。

<span class="listing-number">**サンプルコード3-12**</span>

```java
public class PrintHelper {
    // 文字列を出力
    public void print(String message) {
        System.out.println(message);
    }
    
    // 整数を出力
    public void print(int number) {
        System.out.println("数値: " + number);
    }
    
    // 2つの値を出力
    public void print(String label, int value) {
        System.out.println(label + ": " + value);
    }
}

// 使用例
public class OverloadExample {
    public static void main(String[] args) {
        PrintHelper helper = new PrintHelper();
        
        // 同じprintという名前だが、引数によって異なるメソッドが呼ばれる
        helper.print("こんにちは");          // String版
        helper.print(42);                   // int版
        helper.print("年齢", 20);           // String, int版
    }
}
```

実行結果：
```
こんにちは
数値: 42
年齢: 20
```

#### メソッドオーバーロードの詳細

メソッドオーバーロードは、同じ名前で異なるパラメータを持つメソッドを複数定義できる強力な機能です。
これにより、似た機能を持つメソッドに統一的な名前を付けることができ、コードの可読性と使いやすさが向上します。

##### C言語との決定的な違い

C言語では関数名は一意でなければならず、パラメータが違っても別の名前を付ける必要がありました。
以下の例でこの制約を確認できます。
```c
// C言語では別々の名前が必要
int add_int(int a, int b);
double add_double(double a, double b);
int add_three_ints(int a, int b, int c);
```

一方、Javaではすべて同じ名前で定義できます：
<span class="listing-number">**サンプルコード3-33**</span>

```java
public int add(int a, int b) { return a + b; }
public double add(double a, double b) { return a + b; }
public int add(int a, int b, int c) { return a + b + c; }
```

これはメソッドオーバーロードの例です。

##### オーバーロードのメリット

1. 直感的なAPI設計
   - 利用者は1つのメソッド名を覚えるだけで、さまざまな状況で使える
   - `Math.max(int, int)`、`Math.max(double, double)`など、統一的な名前で提供
2. 型安全性の向上
   - コンパイル時に引数の型に基づいてもっとも適合するメソッドが選択される
   - 型エラーが実行前に検出される
3. 後方互換性の維持
   - 既存のメソッドを変更せずに、新しいパラメータパターンを追加できる
   - 機能拡張が容易

##### オーバーロードの解決規則

Javaコンパイラはメソッド選択の際に特定のルールに従います。
以下の優先順位でメソッドを選択します。

1. 完全一致（型変換なし）
2. プリミティブ型の拡大変換（`int` → `long`、`float` → `double`など）
3. オートボクシング（`int` → `Integer`など）
4. 可変長引数

<span class="listing-number">**サンプルコード3-34**</span>

```java
public class OverloadResolution {
    public static void test(int x) {
        System.out.println("int: " + x);
    }
    
    public static void test(long x) {
        System.out.println("long: " + x);
    }
    
    public static void test(Integer x) {
        System.out.println("Integer: " + x);
    }
    
    public static void test(int... x) {
        System.out.println("可変長引数: " + Arrays.toString(x));
    }
    
    public static void main(String[] args) {
        byte b = 10;
        test(b);      // int版が呼ばれる（自動拡大変換）
        test(10);     // int版が呼ばれる（完全一致）
        test(10L);    // long版が呼ばれる（完全一致）
        test(Integer.valueOf(10)); // Integer版が呼ばれる
        test(1, 2, 3); // 可変長引数版が呼ばれる
    }
}
```

実行結果：
```
int: 10
int: 10
long: 10
Integer: 10
可変長引数: [1, 2, 3]
```

> **重要**: 戻り値の型だけが異なるメソッドはオーバーロードできません。次のコードはコンパイルエラーになります。
> ```java
> public int calculate() { return 1; }
> public double calculate() { return 1.0; }  // エラー：戻り値の型だけでは区別できない
> ```

##### コンストラクタのオーバーロード

コンストラクタもメソッドと同様にオーバーロードできます。これにより、オブジェクトの作成時にさまざまな初期化方法を提供できます：

<span class="listing-number">**サンプルコード3-35**</span>

```java
public class Person {
    private String name;
    private int age;
    
    // デフォルトコンストラクタ
    public Person() {
        this("名無し", 0);
    }
    
    // 名前のみを指定するコンストラクタ
    public Person(String name) {
        this(name, 0);
    }
    
    // すべての属性を指定するコンストラクタ
    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }
}
```

これはコンストラクタオーバーロードの例です。

## 実用的なクラス設計例



<!-- 
================
チャンク 5/12 の終了
校正ステータス: [ ] 未完了 / [ ] 完了
================
-->
