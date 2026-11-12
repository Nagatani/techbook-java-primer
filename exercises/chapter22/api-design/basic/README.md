# 第22章 基本課題（API設計）

## 学習目標

- クラス・フィールド・コンストラクター・メソッドにJavadocコメントを書く
- `@param`・`@return`・`@throws`で、引数・戻り値・例外の約束を書く
- `{@code}`と`<pre>{@code ...}</pre>`で、コードの断片と使用例を書く
- `@since`・`@deprecated`と`@Deprecated`で、版の情報と非推奨を伝える
- `javadoc -Xdoclint:all`の警告を0にし、生成したHTMLで読みやすさを確かめる

## 課題一覧

このフォルダーのJavaファイルは、`TODO`の箇所を埋めて完成させる雛形です。
Javadocの書き方の例は[solutions/](../solutions/)にあります（雛形とは別の、統計の計算をするクラスの例です）。

### 課題1: 小さなライブラリにJavadocを書く

**ファイル**: `SimpleLibraryDoc.java`

処理はできあがっている文字列・数値のユーティリティに、Javadocコメントを書き足します。

**要求仕様**:

- `TODO`の付いたクラス・定数・コンストラクター・メソッド・内部クラス（`Result`）のすべてにJavadocを書く
- 引数のあるメソッドには`@param`、戻り値のあるメソッドには`@return`、例外を投げるメソッドには`@throws`と、投げる条件を書く
- `formatMessage`には、`<pre>{@code ...}</pre>`で使い方の例を書く
- `oldMethod`には`@deprecated`で代わりのメソッドを書き、メソッドに`@Deprecated`を付ける
- クラスには`@since 1.0`と`@author`を書く

**実行例**（`main`の出力。Javadocを書いても変わらない）:

```
=== SimpleLibrary v1.0.0 デモ ===
フォーマット結果: こんにちは、田中さん！今日は晴れです。
メール検証: true
パーセンテージ: 25.0%
検証結果: true - 入力は有効です
```

雛形のまま`javadoc -Xdoclint:all`を実行すると、コメントがないことを知らせる警告が15個出ます。
Javadocを書き終えたら、警告が0になることを確かめます。

### 課題2: 数学のユーティリティを実装して文書化する

**ファイル**: `APIDocumentation.java`

べき乗・階乗・素数の判定・平均・標準偏差・2進数と16進数への変換・最大公約数・最小公倍数・2点間の距離を求める`static`メソッドを実装し、Javadocを完成させます。

**要求仕様**:

- 各メソッドの`// TODO: 実装してください`を実装する
- 不正な引数（負の数の階乗、空の配列の平均など）には`IllegalArgumentException`を投げ、Javadocの`@throws`にその条件を書く
- `TODO: パラメータの説明`のような仮の文を、実際の説明に書き換える。`@throws TODO`のように例外のクラス名がない行は、`javadoc`がエラーにする
- インスタンスを作らせない`private`のコンストラクターは、雛形にすでにある。ほかのメソッドも同じように、「なぜそうなっているか」がわかる説明を書く
- `main`を足して、いくつかのメソッドを呼んで結果を表示する

`javadoc -Xdoclint:all`で、エラーと警告が0になることを確かめます。

## コンパイルと実行

```bash
cd exercises/chapter22/api-design/basic
javac SimpleLibraryDoc.java
java SimpleLibraryDoc

# Javadocを検査してHTMLを作る（docsフォルダーに出力）
javadoc -Xdoclint:all -d docs SimpleLibraryDoc.java
```

`docs/index.html`をブラウザで開くと、生成されたドキュメントを読めます。`docs`は試し終えたら消してかまいません。

## ヒント

- Javadocの最初の1文は、一覧に出る要約になる。「〜を返します。」のように、何をするかを1文で書く
- `@throws`には「どんなときに投げるか」を書く。「例外が起きた場合」のような書き方では、利用者は何を避ければよいかがわからない
- 本文の中のコードは`{@code}`で囲む。`<`や`>`を含むコード（`List<String>`など）も、`{@code}`の中ならそのまま書ける
- `-Xdoclint:all`は、コメントのない公開メンバー、`@param`の漏れ、HTMLの誤りを報告する

## 完了チェックリスト

- [ ] 課題1: すべての公開メンバーにJavadocがあり、警告が0になった
- [ ] 課題1: 非推奨のメソッドに、代わりのメソッドを書けた
- [ ] 課題2: 例外を投げる条件を`@throws`に書けた
- [ ] 課題2: 生成したHTMLを読んで、使い方がわかる説明になっているか確かめた

**次のステップ**: 基本課題が終わったら、[advanced/](../advanced/)の発展課題に進みましょう。
