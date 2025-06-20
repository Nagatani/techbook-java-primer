# 第5章 パッケージとアクセス制御

この章では、Javaのパッケージシステムとアクセス修飾子について学習します。

## 5.1 パッケージの基礎

### パッケージ宣言

```java
// com/example/utils/StringUtil.java
package com.example.utils;

public class StringUtil {
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }
}
```

### パッケージのインポート

```java
// com/example/app/Main.java
package com.example.app;

import com.example.utils.StringUtil;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        if (StringUtil.isEmpty("")) {
            System.out.println("文字列は空です");
        }
    }
}
```

## 5.2 アクセス修飾子

```java
public class AccessExample {
    public String publicField;        // どこからでもアクセス可能
    protected String protectedField;  // 同じパッケージ + サブクラス
    String packageField;              // 同じパッケージのみ
    private String privateField;      // 同じクラスのみ
    
    public void accessTest() {
        // 全てのフィールドにアクセス可能
        publicField = "public";
        protectedField = "protected";
        packageField = "package";
        privateField = "private";
    }
}
```

## 5.3 実践例

### ライブラリパッケージの設計

```java
// com/mylib/math/Calculator.java
package com.mylib.math;

public class Calculator {
    public static double add(double a, double b) {
        return a + b;
    }
    
    public static double multiply(double a, double b) {
        return a * b;
    }
}

// com/mylib/string/TextProcessor.java
package com.mylib.string;

public class TextProcessor {
    public static String capitalize(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }
}
```

## 5.4 JARファイルの作成と配布

Javaプログラムを配布する際は、コンパイル済みクラスファイルをJAR（Java ARchive）ファイルとしてパッケージングします。

### JARファイルとは

JARファイルは、複数のクラスファイルやリソースファイルを一つのアーカイブファイルにまとめた形式です。ZIPファイルの形式をベースとしており、以下のような利点があります：

- **配布の簡略化**: 複数のクラスファイルを一つのファイルにまとめて配布
- **実行可能形式**: メインクラスを指定することで、ダブルクリックまたは`java -jar`コマンドで実行可能
- **ライブラリ管理**: 外部ライブラリとして他のプロジェクトから参照可能

### jarコマンドによる作成

#### 基本的なJARファイルの作成

```bash
# コンパイル
javac MyApp.java

# JARファイル作成
jar -cf myapp.jar *.class
```

#### 実行可能JARファイルの作成

実行可能なJARファイルを作成するには、MANIFESTファイルが必要です。

**manifest.mf**
```
Manifest-Version: 1.0
Main-Class: MyApp
```

```bash
# 実行可能JARファイルの作成
jar -cfm myapp.jar manifest.mf *.class

# 実行
java -jar myapp.jar
```

#### jarコマンドの主なオプション

| オプション | 説明 |
|------------|------|
| `-c` | 新規JARファイルを作成 |
| `-u` | 既存JARファイルを更新 |
| `-x` | JARファイルの内容を抽出 |
| `-t` | JARファイルの内容をリスト表示 |
| `-f` | 対象JARファイルを指定 |
| `-m` | MANIFESTファイルを使用 |
| `-v` | 詳細情報を表示 |

### IntelliJ IDEAでのJAR作成

**手順1: Artifactsの設定**
1. `File` → `Project Structure` を選択
2. `Artifacts` を選択し、`+` ボタンをクリック
3. `JAR` → `From modules with dependencies...` を選択
4. `Main Class` を指定
5. `OK` をクリック

**手順2: JARファイルの生成**
1. `Build` → `Build Artifacts...` を選択
2. 対象Artifactの `Build` を選択
3. `out/artifacts/` ディレクトリにJARファイルが生成される

**手順3: 動作確認**
```bash
# プロジェクトディレクトリに移動
cd ~/IdeaProjects/MyProject

# JARファイルの場所に移動
cd out/artifacts/MyProject_jar

# 実行
java -jar MyProject.jar
```

## 5.5 JavaDocによるドキュメンテーション

JavaDocは、ソースコード内の特別なコメントから API ドキュメントを自動生成するツールです。

### JavaDocコメントの基本構文

JavaDocコメントは `/**` で始まり `*/` で終わります：

```java
/**
 * このクラスは計算機能を提供します。
 * 
 * @author Yamada Taro
 * @version 1.0
 * @since 1.0
 */
public class Calculator {
    
    /**
     * 2つの整数の和を計算します。
     * 
     * @param a 最初の整数
     * @param b 2番目の整数
     * @return 2つの整数の和
     * @throws IllegalArgumentException 引数が不正な場合
     */
    public int add(int a, int b) {
        if (a < 0 || b < 0) {
            throw new IllegalArgumentException("負の数は許可されていません");
        }
        return a + b;
    }
    
    /**
     * 配列の平均値を計算します。<br>
     * 空の配列の場合は0を返します。
     * 
     * @param values 計算対象の配列
     * @return 平均値
     * @see #add(int, int)
     */
    public double average(int[] values) {
        if (values.length == 0) {
            return 0.0;
        }
        
        int sum = 0;
        for (int value : values) {
            sum = add(sum, value);
        }
        return (double) sum / values.length;
    }
}
```

### 主要なJavaDocタグ

| タグ | 説明 | 使用例 |
|------|------|--------|
| `@author` | 作者名 | `@author 山田太郎` |
| `@version` | バージョン情報 | `@version 1.2.0` |
| `@since` | 追加されたバージョン | `@since 1.0` |
| `@param` | パラメータの説明 | `@param name ユーザー名` |
| `@return` | 戻り値の説明 | `@return 計算結果` |
| `@throws` | 例外の説明 | `@throws IOException ファイル読み込みエラー` |
| `@see` | 関連項目への参照 | `@see Calculator#add(int, int)` |
| `@deprecated` | 非推奨マーク | `@deprecated 代わりに newMethod() を使用` |

### コマンドラインでのJavaDoc生成

```bash
# 基本的な生成
javadoc Calculator.java

# プライベートメンバーも含めて生成
javadoc -private Calculator.java

# 出力ディレクトリを指定
javadoc -d docs Calculator.java

# 日本語ドキュメントの生成
javadoc -locale ja_JP -encoding utf8 -docencoding utf8 -charset utf8 Calculator.java
```

### IntelliJ IDEAでのJavaDoc生成

**手順:**
1. `Tools` → `Generate JavaDoc...` を選択
2. 以下の設定を行う：
   - **Output Directory**: ドキュメント出力先ディレクトリ
   - **Locale**: `ja_JP`（日本語ドキュメント用）
   - **Other command line arguments**: `-encoding utf8 -docencoding utf8 -charset utf8`
3. `OK` をクリックして生成

### JavaDocのベストプラクティス

**1. 明確で簡潔な説明**
```java
/**
 * 指定された文字列が空文字列かnullかを判定します。
 * 
 * @param str 判定対象の文字列
 * @return 空文字列またはnullの場合true、それ以外はfalse
 */
public static boolean isEmpty(String str) {
    return str == null || str.length() == 0;
}
```

**2. パラメータと戻り値の詳細説明**
```java
/**
 * ファイルからテキストを読み込みます。
 * 
 * @param filename 読み込み対象のファイルパス（相対パスまたは絶対パス）
 * @param encoding 文字エンコーディング（例: "UTF-8", "Shift_JIS"）
 * @return ファイルの内容を文字列として返す。空ファイルの場合は空文字列
 * @throws FileNotFoundException ファイルが存在しない場合
 * @throws IOException ファイル読み込み中にエラーが発生した場合
 */
public String readFile(String filename, String encoding) 
        throws FileNotFoundException, IOException {
    // 実装
}
```

**3. HTMLタグの活用**
```java
/**
 * 複数の計算モードをサポートする計算機クラス。
 * <p>
 * 以下の演算をサポートします：
 * <ul>
 *   <li>四則演算（+, -, *, /）</li>
 *   <li>三角関数（sin, cos, tan）</li>
 *   <li>対数関数（log, ln）</li>
 * </ul>
 * 
 * <h3>使用例</h3>
 * <pre>
 * Calculator calc = new Calculator();
 * double result = calc.add(10.5, 20.3);
 * System.out.println("結果: " + result);
 * </pre>
 * 
 * @author 開発チーム
 * @version 2.1.0
 */
public class Calculator {
    // ...
}
```

## まとめ

この章では、パッケージによるコードの構造化、アクセス修飾子によるカプセル化の実現、そしてJARファイルでの配布とJavaDocでのドキュメンテーションについて学習しました。これらの仕組みにより、保守性が高く、再利用可能で、よく文書化されたJavaアプリケーションを開発できます。