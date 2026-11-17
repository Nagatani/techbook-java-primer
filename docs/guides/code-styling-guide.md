# コードスタイリングガイド

このガイドでは、本書の原稿でコードブロックを書く方法と、ビルドで付く見た目について説明します（2026-11-16更新）。
番号とキャプションの規則は`CLAUDE.md`の「Code Listing Numbers」「Sample Code Excerpts」が正です。

## 原稿での書き方

- コードブロックは```に言語名を付けて書く（`java`、`xml`、`bash`など）
- コードブロックの前には空行を入れる。VFMでは、空行なしで段落に続く```がコードブロックにならず、段落に吸収される
- 言語指定のあるコードブロック（bash・mermaidなどを除く）には「サンプルコードX-Y」のキャプションを付ける。番号は`node scripts/sample-code.js number`で振る
- 全文を`sample-code/`で公開したサンプルは、本文のコードブロックとキャプションを直接編集しない。全文を編集してから`npm run samples:sync`する（`sample-code/README.md`）
- 標準入出力は`IO.println`・`IO.print`・`IO.readln`で書く（`CLAUDE.md`の方針9）
- 図は```mermaidで原稿に書かない。元データを`docs/diagrams/`に置き、SVGを参照する（`CLAUDE.md`の方針11）

キャプションは次の形です（第5章の実例）。全文を公開したサンプルのキャプションは、`samples:sync`が全文とファイル名・行番号から作ります。

~~~markdown
<span class="listing-number">**サンプルコード5-3**　Character.java、Hero.java、Wizard.java、Main.java</span>

```java
// 親クラス
public class Character {
    String name;
    int hp;

    public void attack() {
        IO.println(name + "の攻撃！（HP: " + hp + "）");
    }
}

// 子クラス：勇者
public class Hero extends Character {
（以下、Hero.java・Wizard.java・Main.javaが空行で区切られて続く）
```

<span class="listing-number">**サンプルコード5-8**　UserSystemBefore.java（1〜18、26〜47行目）</span>
~~~

## ビルドで付く見た目

テーマは`theme/my-theme/theme.css`です。

- 地は明るい灰色（`#f8f8f8`）、細い枠と角丸。印刷用の設定（`@media print`）でも地は明るい色に固定される
- 色分けは、VFMがビルド時にPrism（refractor）で付ける。色分けには背景や余白を付けない（等幅のそろいが崩れる）
- `scripts/vfm-code-lines.js`がコードを1行ずつの`span.code-line`に分け、縞模様を行ごとに付ける。紙面の幅で折り返した続きの行は、その行の字下げ＋4文字の位置から始まる
- 言語ごとに左の線の色が変わる。Javaは橙、JavaScriptは黄、Pythonは青、bash・shellは緑（bashは縞模様なし）
- コードの文字色は、明るい地でコントラスト比4.5以上にする。暗い地を前提にした淡い色は使わない

## インラインコード

文章中でコードを参照する場合は、バッククォートを使います。
`IO.println()`や`public static void main(String[] args)`のように表示されます。
URLを本文に書くときもインラインコードで囲みます（ビルド時にリンクになる。`CLAUDE.md`の方針8）。

## 使わない書き方

- `<pre class="line-numbers">`による行番号付きのコードブロック（テーマに定義は残っているが、原稿では使っていない）
- コードブロックの中のHTMLタグや手作業の色付け
