# 第4章 クラスとインスタンス：よくあるエラーと対処法

書籍『Java入門』第4章「クラスとインスタンス」で扱う内容に関連して、よく遭遇するエラーとその対処法をまとめています。

本章では、クラスとインスタンスを扱う際にとくによく遭遇するエラーを扱います。

## 本章特有のエラー

### 1. コンストラクタ関連のエラー（統合版）
問題: コンストラクタの定義や使用方法を誤る

```java
// エラー例1：デフォルトコンストラクタが見つからない
public class User {
    public User(String name) { }  // カスタムコンストラクタのみ
}
User user = new User();  // エラー：引数なしコンストラクタがない

// エラー例2：戻り値型を指定
public void User() { }  // エラー：コンストラクタに戻り値型
```

解決策:
```java
public class User {
    // デフォルトコンストラクタを明示的に定義
    public User() {
        this("Unknown");
    }
    
    public User(String name) {  // 戻り値型なし
        this.name = name;
    }
}
```

重要なポイント:
- カスタムコンストラクタを定義すると、デフォルトコンストラクタは自動生成されない
- コンストラクタには戻り値型を指定しない
- this()でコンストラクタチェーンを活用

### 2. NullPointerException完全ガイド
問題: nullの参照にメソッド呼び出しやフィールドアクセスを行う

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

解決策:
コンパイルできる全文：[chapter04-code/Product2/](chapter04-code/Product2/)

```java
public class Product {
    private String name;
    
    public Product(String name) {
        // コンストラクタでの検証
        if (name == null) {
            throw new IllegalArgumentException("商品名はnullにできません");
        }
        this.name = name;
    }
    
    public int getNameLength() {
        // 防御的プログラミング
        return (name != null) ? name.length() : 0;
    }
}
```

重要なポイント:
- コンストラクタで引数を検証する
- メソッド内でnullチェックを行う
- 有効なデフォルト値か例外処理を使用

### 3. メソッドオーバーロードの問題
問題: 曖昧なオーバーロードや不正な定義

```java
// エラー例1：曖昧な呼び出し
public int calc(int a, double b) { }
public double calc(double a, int b) { }
calc(10, 20);  // どちらを呼ぶか不明

// エラー例2：戻り値型のみ異なる
public String process(String s) { }
public int process(String s) { }  // エラー
```

解決策:
```java
// 明確な引数型
public int calc(int a, int b) { }
public double calc(double a, double b) { }

// 異なるメソッド名
public String processToString(String s) { }
public int processToLength(String s) { }
```

重要なポイント:
- 引数の型は明確に区別できるようにする
- 戻り値型だけでは区別できない
- 引数の型で区別しにくい場合は、異なるメソッド名を使用する

### 4. オブジェクト参照と防御的コピー
問題: 参照の共有により意図しない変更が発生

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

解決策:
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

重要なポイント:
- コンストラクタで防御的コピーを作成
- getterでも内部状態を直接返さない
- 可変オブジェクトはとくに注意が必要

## 関連する共通エラー

以下のエラーも本章の内容に関連します。

- **ClassCastException**（→ 付録H）
  - 型キャストの誤りで発生
- **equals/hashCodeの契約違反**（→ 付録H）
  - コレクションで使用する際に問題となる
- **thisキーワードの使い忘れ**（→ 第3章）
  - フィールドと引数の区別ができない

## デバッグのヒント

1. NullPointerExceptionが発生したら
   - スタックトレースで発生箇所を特定
   - 該当行の変数がnullでないか確認
   - 初期化処理を見直す

2. オーバーロードエラーの場合
   - コンパイラのエラーメッセージを詳しく読む
   - 引数の型を明示的にキャストして確認

3. 参照関連の問題
   - デバッガで参照先のオブジェクトを確認
   - 同一性（==）と等価性（equals）を区別

## さらに学ぶには

- 付録H: Java共通エラーガイド（詳細なエラーパターン）
- 第6章: 不変性とfinalによる安全なクラス設計
- 第13章: 例外処理の体系的な学習
