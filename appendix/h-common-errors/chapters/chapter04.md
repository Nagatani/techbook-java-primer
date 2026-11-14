# 第4章 クラスとインスタンス：よくあるエラーと対処法

書籍『Java入門』第4章「クラスとインスタンス」で扱う内容に関連して、よく遭遇するエラーとその対処法をまとめています。

本章では、クラスとインスタンスを扱う際にとくによく遭遇するエラーを扱います。

## コンストラクターに関するエラー

コンストラクターの定義や使い方を誤ると、エラーになります。

### エラー例

```java
// エラー例1：デフォルトコンストラクターが見つからない
public class User {
    public User(String name) { }  // カスタムコンストラクターのみ
}
User user = new User();  // エラー：引数なしコンストラクターがない

// エラー例2：戻り値型を指定
public class User {
    private String name;
    public void User() {  // コンストラクターではなく、Userという名前の普通のメソッドになる
        this.name = "Unknown";
    }
}
User user = new User();  // コンパイルは通るが、nameはnullのまま
```

エラー例2はコンパイルエラーになりません。
戻り値型を書くと、クラスと同じ名前の普通のメソッドとして扱われます。
コンストラクターのつもりで書いた初期化の処理が、インスタンスを作るときに実行されません。

### 対処法

```java
public class User {
    private String name;

    // 引数なしのコンストラクターを明示的に定義
    public User() {
        this("Unknown");
    }
    
    public User(String name) {  // 戻り値型なし
        this.name = name;
    }
}
```

### 重要なポイント

- 引数付きのコンストラクターを定義すると、引数なしのデフォルトコンストラクターは自動生成されない
- コンストラクターには戻り値型を指定しない（`void`を付けるとメソッドになる）
- this()でコンストラクターチェーンを活用

## NullPointerException

nullの参照に対してメソッドを呼び出したり、フィールドにアクセスしたりすると、例外が発生します。

### エラー例

コンパイルできる全文：[chapter04-code/Product/](chapter04-code/Product/)

```java
// エラー例
public class Product {
    private String name;
    
    public Product(String name) {
        this.name = name;  // nullチェックなし
    }
    
    public int getNameLength() {
        return name.length();  // nameがnullの場合エラー
    }
}
```

### 対処法

コンパイルできる全文：[chapter04-code/Product2/](chapter04-code/Product2/)

```java
public class Product {
    private String name;
    
    public Product(String name) {
        // コンストラクターでの検証
        if (name == null) {
            throw new IllegalArgumentException("商品名はnullにできません");
        }
        this.name = name;
    }
    
    public int getNameLength() {
        // コンストラクターでnullを弾いているので、nameはnullにならない
        return name.length();
    }
}
```

### 重要なポイント

- コンストラクターで引数を検証し、不正な値のインスタンスを作らせない
- 外部から受け取る値など、nullになりうる値はメソッド内でnullチェックを行う
- 有効なデフォルト値か例外処理を使用

## メソッドのオーバーロードの問題

曖昧なオーバーロードや不正な定義は、コンパイルエラーになります。

### エラー例

```java
// エラー例1：曖昧な呼び出し
public int calc(int a, double b) { }
public double calc(double a, int b) { }
calc(10, 20);  // どちらを呼ぶか不明

// エラー例2：戻り値型のみ異なる
public String process(String s) { }
public int process(String s) { }  // エラー
```

### 対処法

```java
// 明確な引数型
public int calc(int a, int b) { }
public double calc(double a, double b) { }

// 異なるメソッド名
public String processToString(String s) { }
public int processToLength(String s) { }
```

### 重要なポイント

- 引数の型は明確に区別できるようにする
- 戻り値型だけでは区別できない
- 引数の型で区別しにくい場合は、異なるメソッド名を使用する

## オブジェクト参照と防御的コピー

参照を共有すると、意図しない変更が起こります。

### エラー例

```java
// エラー例
public class Team {
    private List<String> members;
    
    public Team(List<String> members) {
        this.members = members;  // 参照を共有
    }
    
    public List<String> getMembers() {
        return members;  // 内部状態を露出
    }
}
```

### 対処法

```java
public class Team {
    private List<String> members;
    
    public Team(List<String> members) {
        // 防御的コピー
        this.members = new ArrayList<>(members);
    }
    
    public List<String> getMembers() {
        // 防御的コピーを返す
        return new ArrayList<>(members);
    }
}
```

### 重要なポイント

- コンストラクターで防御的コピーを作成
- getterでも内部状態を直接返さない
- 可変オブジェクトはとくに注意が必要

## 関連する共通エラー

以下のエラーも本章の内容に関連します。

- **ClassCastException**（→ 付録H）
  - 型キャストの誤りで発生
- **equals/hashCodeの契約違反**（→ 付録H）
  - コレクションで使用する際に問題となる
- **thisキーワードの使い忘れ**（→ 本章）
  - フィールドと引数の区別ができない

## デバッグのヒント

1. NullPointerExceptionが発生したら
   - スタックトレースで発生箇所を特定
   - 該当行の変数がnullでないか確認
   - 初期化処理を見直す

2. オーバーロードエラーの場合
   - コンパイラーのエラーメッセージを詳しく読む
   - 引数の型を明示的にキャストして確認

3. 参照関連の問題
   - デバッガーで参照先のオブジェクトを確認
   - 同一性（==）と等価性（equals）を区別

## さらに学ぶには

- 付録H: Java共通エラーガイド（詳細なエラーパターン）
- 第6章: 不変性とfinalによる安全なクラス設計
- 第13章: 例外処理の体系的な学習
