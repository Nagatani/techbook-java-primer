# 第5章 継承とポリモーフィズム：よくあるエラーと対処法

書籍『Java入門』第5章「継承とポリモーフィズム」で扱う内容に関連して、よく遭遇するエラーとその対処法をまとめています。

本章では、継承とポリモーフィズムに特有のエラーを扱います。

## 本章特有のエラー

### 1. super()呼び出しエラー
問題: 親クラスのコンストラクタを正しく呼び出さない

```java
// エラー例
class Parent {
    public Parent(String name) { }  // デフォルトコンストラクタなし
}

class Child extends Parent {
    public Child() { }  // エラー：super()が暗黙的に呼ばれるがParent()は存在しない
}
```

このエラーはParentクラスにデフォルトコンストラクタがないため、Childのコンストラクタから暗黙的にsuper()が呼ばれたときに発生します。

解決策:

```java
class Parent {
    public Parent(String name) {
        IO.println("Parent コンストラクタ: " + name);
    }
}

class Child extends Parent {
    public Child() {
        super("default");  // 明示的に親のコンストラクタを呼び出す
        IO.println("Child コンストラクタ");
    }
}
```

`new Child()`を実行した結果：
```
Parent コンストラクタ: default
Child コンストラクタ
```

重要なポイント:
- 親クラスにパラメータ付きコンストラクタのみがある場合は明示的なsuper()が必要
- super()の前には、作成中のオブジェクト（this）を使わない処理しか書けない（Java 25より前は、コンストラクタの最初に書く必要があった）
- デフォルトコンストラクタは自動生成されない

### 2. オーバーライドエラー
問題: メソッドオーバーライド時の制約違反

```java
// エラー例1：アクセス修飾子の制限強化
class Parent {
    public void method() { }
}
class Child extends Parent {
    private void method() { }  // エラー：publicからprivateへ
}

// エラー例2：戻り値型の不一致
class Parent {
    public int getValue() { return 0; }
}
class Child extends Parent {
    public String getValue() { return ""; }  // エラー：戻り値型が異なる
}
```

このエラーは、子クラスが親クラスのメソッドよりも制限的なアクセス修飾子を使用しようとした場合や、戻り値型が一致しない場合に発生します。

解決策:

```java
class Child extends Parent {
    @Override
    public void method() { }  // 同じかより緩いアクセス修飾子
    
    @Override
    public int getValue() { return 42; }  // 同じ戻り値型
}
```

これにより、親クラスでpublicメソッドとして公開されているものが、子クラスでも同じく公開されることが保証されます。

重要なポイント:
- アクセス修飾子は同じかより緩いものを使用
- 戻り値型は同じか共変戻り値型（サブタイプ）
- @Overrideアノテーションでミスを防ぐ

### 3. キャストエラー
問題: 互換性のない型へのキャストによるClassCastException

```java
// エラー例
Parent p = new Parent();
Child c = (Child) p;  // 実行時エラー：ParentはChildではない
```

このコードを実行するとClassCastExceptionが発生します。Parentクラスのインスタンスを Childクラスにキャストできないからです。

解決策:

```java
class Parent { }

class Child extends Parent {
    void childMethod() {
        IO.println("Child 固有メソッド");
    }
}

public class CastExample {
    public static void main(String[] args) {
        Parent p1 = new Parent();
        Parent p2 = new Child();

        // instanceof使用
        if (p1 instanceof Child) {
            Child c = (Child) p1;
            c.childMethod();
        } else {
            IO.println("p1はChildではないため、キャストできません");
        }

        // パターンマッチング（Java 16以降）
        if (p2 instanceof Child child) {
            IO.println("p2はChildにキャスト可能");
            child.childMethod();
        }
    }
}
```

実行結果：
```
p1はChildではないため、キャストできません
p2はChildにキャスト可能
Child 固有メソッド
```

重要なポイント:
- キャスト前にinstanceofで型チェック
- ダウンキャストは慎重に行う
- パターンマッチングで安全性と簡潔性を両立

## 関連する共通エラー

以下のエラーも本章の内容に関連します。

- **NullPointerException**（→ 付録H）
  - 継承階層でのnull参照に注意
- **抽象メソッドの実装忘れ**（→ 第7章）
  - 抽象クラス継承時の必須実装
- **メソッドシグネチャの理解不足**（→ 第4章）
  - オーバーライド条件の詳細

## デバッグのヒント

1. 継承階層の可視化
   - クラス図を描いて関係を整理
   - どのメソッドがどこで定義されているか確認

2. @Overrideの活用
   - 必ず付けることでコンパイル時チェック
   - オーバーライドの意図を明確化

3. 段階的なテスト
   - 親クラス単体→子クラス単体→ポリモーフィズムの順
