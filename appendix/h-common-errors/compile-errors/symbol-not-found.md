# シンボルが見つからないエラー

## 概要

「シンボルが見つからない」エラーは、変数名、メソッド名、クラス名などが正しく宣言されていない、またはスコープ外で使用された場合に発生する最も一般的なコンパイルエラーの一つです。

## エラーメッセージの例

```
error: cannot find symbol
    symbol: variable message
    location: class Example

error: cannot find symbol
    symbol: method prinln(String)
    location: variable out of type PrintStream

error: cannot find symbol
    symbol: class Scanner
    location: class Main
```

## 典型的な発生パターン

### パターン1：変数の宣言忘れ

```java
public class Example {
    public void method() {
        // エラー：messageが宣言されていない
        System.out.println(message);
        
        // エラー：宣言前に使用
        count = 10;
        int count;
    }
}
```

### パターン2：スペルミス

```java
public class Calculator {
    private int result;
    
    public void calculate() {
        // エラー：resutlは存在しない（resultのスペルミス）
        System.out.println(resutl);
        
        // エラー：prinlnは存在しない（printlnのスペルミス）
        System.out.prinln("計算結果");
    }
}
```

### パターン3：スコープ外での使用

```java
public class ScopeExample {
    public void method1() {
        int localVar = 10;
    }
    
    public void method2() {
        // エラー：localVarはmethod1のスコープ内
        System.out.println(localVar);
    }
    
    public void blockScope() {
        if (true) {
            int blockVar = 20;
        }
        // エラー：blockVarはifブロック内のスコープ
        System.out.println(blockVar);
    }
}
```

### パターン4：インポート忘れ

```java
// import java.util.Scanner; が必要

public class InputExample {
    public void readInput() {
        // エラー：Scannerクラスが見つからない
        Scanner scanner = new Scanner(System.in);
    }
}
```

### パターン5：大文字小文字の間違い

```java
public class CaseSensitive {
    private String Name;  // 大文字で始まる
    
    public void printName() {
        // エラー：nameは存在しない（Nameが正しい）
        System.out.println(name);
    }
}
```

## 解決策

### 1. 変数の適切な宣言

```java
public class Example {
    // インスタンス変数として宣言
    private String message = "Hello";
    
    public void method() {
        // ローカル変数として宣言
        int count = 10;
        
        System.out.println(message);
        System.out.println(count);
    }
}
```

### 2. インポート文の追加

```java
// 必要なクラスをインポート
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

// またはワイルドカードを使用（推奨されない場合もある）
import java.util.*;

public class ImportExample {
    private Scanner scanner = new Scanner(System.in);
    private List<String> list = new ArrayList<>();
}
```

### 3. スコープの理解と修正

```java
public class ScopeFixed {
    // クラススコープの変数
    private int classVar = 100;
    
    public void demonstrateScope() {
        // メソッドスコープの変数
        int methodVar = 200;
        
        for (int i = 0; i < 5; i++) {
            // ループスコープの変数
            int loopVar = i * 10;
            
            // すべてのスコープの変数にアクセス可能
            System.out.println(classVar + methodVar + loopVar);
        }
        
        // loopVarはここではアクセス不可
    }
}
```

### 4. IDEの活用

```java
public class IDEHelper {
    // IDEの自動補完を活用
    private StringBuilder stringBuilder = new StringBuilder();
    
    public void useAutoComplete() {
        // "str" + Ctrl+Space で自動補完
        stringBuilder.append("Hello");
        
        // 赤い波線でエラーを即座に検出
        // stringBilder.append("World");  // スペルミス
    }
}
```

## 高度な解決策

### 1. 静的インポート

```java
// 静的メソッドを直接インポート
import static java.lang.Math.*;
import static java.lang.System.out;

public class StaticImportExample {
    public void calculate() {
        // Mathクラスの接頭辞不要
        double result = sqrt(pow(3, 2) + pow(4, 2));
        
        // System.out の代わりに out を直接使用
        out.println("結果: " + result);
    }
}
```

### 2. 完全修飾名の使用

```java
public class FullyQualified {
    public void useWithoutImport() {
        // インポートせずに完全修飾名を使用
        java.util.List<String> list = new java.util.ArrayList<>();
        java.util.Scanner scanner = new java.util.Scanner(System.in);
        
        // 名前の衝突を避ける場合に有用
        java.sql.Date sqlDate = new java.sql.Date(System.currentTimeMillis());
        java.util.Date utilDate = new java.util.Date();
    }
}
```

### 3. 内部クラスとスコープ

```java
public class OuterClass {
    private String outerField = "外部";
    
    public class InnerClass {
        private String innerField = "内部";
        
        public void accessFields() {
            // 外部クラスのフィールドにアクセス
            System.out.println(outerField);
            
            // 明示的な参照
            System.out.println(OuterClass.this.outerField);
        }
    }
    
    public static class StaticInnerClass {
        public void method() {
            // エラー：静的内部クラスから非静的フィールドにアクセス不可
            // System.out.println(outerField);
        }
    }
}
```

## デバッグのコツ

### 1. エラーメッセージの詳細な読み方

```
error: cannot find symbol
    symbol:   variable count
    location: class com.example.MyClass
             ^
MyClass.java:25: error: cannot find symbol
        System.out.println(count);
                          ^
```

- `symbol`: 見つからないシンボルの種類と名前
- `location`: エラーが発生したクラスや場所
- 行番号とカーソル位置で正確な場所を特定

### 2. コンパイルオプションの活用

```bash
# 詳細なエラー情報
javac -verbose MyClass.java

# すべての警告を表示
javac -Xlint:all MyClass.java

# デバッグ情報を含める
javac -g MyClass.java
```

### 3. 段階的なコンパイル

```java
// 1. まず基本的な構造をコンパイル
public class Step1 {
    public static void main(String[] args) {
        System.out.println("基本構造OK");
    }
}

// 2. 徐々に機能を追加
public class Step2 {
    private String message = "次のステップ";
    
    public static void main(String[] args) {
        Step2 step = new Step2();
        System.out.println(step.message);
    }
}
```

## よくある原因と対策

### 1. パッケージ宣言の不一致

```java
// ファイル: com/example/MyClass.java
package com.example;  // パッケージ宣言が必要

public class MyClass {
    // ...
}
```

### 2. クラスパスの問題

```bash
# クラスパスを指定してコンパイル
javac -cp ./lib/*:. MyClass.java

# 実行時もクラスパスが必要
java -cp ./lib/*:. com.example.MyClass
```

### 3. 循環参照

```java
// ClassA.java
public class ClassA {
    private ClassB b;  // ClassBを参照
}

// ClassB.java
public class ClassB {
    private ClassA a;  // ClassAを参照
}

// 両方を同時にコンパイル
// javac ClassA.java ClassB.java
```

## ベストプラクティス

1. **一貫した命名規則**: キャメルケースを正しく使用
2. **IDEの活用**: 自動補完とエラー検出を最大限活用
3. **インポートの整理**: 使用しないインポートは削除
4. **スコープの最小化**: 変数は必要最小限のスコープで宣言
5. **早期のコンパイル**: こまめにコンパイルしてエラーを早期発見

## 関連項目

- [type-mismatch.md](type-mismatch.md)
- [access-control.md](access-control.md)
- スコープとライフサイクル
- パッケージとインポート