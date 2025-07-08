# コードスタイリングガイド

このガイドでは、本書でコードブロックをスタイリングする方法について説明します。

## デフォルトの動作

すべてのコードブロックには以下のスタイルが自動的に適用されます：
- 薄い灰色の背景
- 角丸のボーダー
- **ゼブラストライプ（行ごとに薄い背景色）**
- 等幅フォント（Consolas, Monaco等）

## 基本的なコードブロック

通常のMarkdownコードブロック：

```java
public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
    }
}
```

上記のコードブロックには自動的にゼブラストライプが適用され、行を識別しやすくなります。

## 行番号付きコードブロック

行番号を表示したい場合は、HTMLを使用して以下のように記述します：

```html
<pre class="line-numbers language-java"><code><span>public class HelloWorld {</span>
<span>    public static void main(String[] args) {</span>
<span>        System.out.println("Hello, World!");</span>
<span>    }</span>
<span>}</span></code></pre>
```

## シンタックスハイライト

以下の言語がサポートされています：

- `language-java` - Javaコード（オレンジの左ボーダー）
- `language-javascript` または `language-js` - JavaScript（黄色の左ボーダー）
- `language-python` - Python（青の左ボーダー）
- `language-bash` または `language-shell` - シェルスクリプト（緑の左ボーダー、暗い背景）

## コードブロックの例

### Java
```java
// Javaの例
import java.util.ArrayList;
import java.util.List;

public class Example {
    private String name;
    
    public Example(String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return "Example: " + name;
    }
}
```

### Bash
```bash
# ディレクトリの作成
mkdir -p src/main/java

# Javaファイルのコンパイル
javac HelloWorld.java

# プログラムの実行
java HelloWorld
```

## インラインコード

文章中でコードを参照する場合は、バッククォートを使用します：
`System.out.println()` や `public static void main()` のように表示されます。

## 注意事項

1. VivliostyleはPrism.jsのような外部ライブラリを標準では含まないため、完全なシンタックスハイライトには追加の設定が必要です
2. 行番号付きコードブロックを使用する場合は、各行を`<span>`タグで囲む必要があります
3. コードブロック内でHTMLエンティティ（`<`, `>`, `&`）を使用する場合は適切にエスケープしてください