# 第22章 チャレンジ課題 - ドキュメンテーションとライブラリ

## 課題概要
小さなライブラリのプロジェクトを、コード・テスト・ドキュメントまで一通り作る総合課題です。

## 課題

### 完全なライブラリプロジェクトの作成
実用的なJSONパーサーライブラリ「SimpleJSON」を開発してください。

**要件:**

1. **コア機能**
   - JSONの解析（文字列→Javaオブジェクト）
   - JSONの生成（Javaオブジェクト→文字列）
   - 型安全なアクセサ（`getString`、`getInt`など。型が違うときは`JsonException`を投げる）
   - 外部ライブラリには頼らず、Javaの標準APIだけで作る（第22章のGsonやJacksonが何をしているかを、自分で作って確かめる課題です）

2. **プロジェクト構成**
   ```
   simple-json/
   ├── src/
   │   ├── main/java/
   │   │   └── com/example/json/
   │   │       ├── JsonParser.java
   │   │       ├── JsonBuilder.java
   │   │       ├── JsonObject.java
   │   │       ├── JsonArray.java
   │   │       └── JsonException.java
   │   └── test/java/
   │       └── com/example/json/
   │           └── (テストクラス)
   ├── docs/
   │   └── getting-started.md
   ├── README.md
   ├── LICENSE
   └── pom.xml または build.gradle
   ```

3. **ドキュメンテーション**
   - 公開するすべてのクラス・メソッドのJavadoc（`javadoc -Xdoclint:all`で警告が出ないこと）
   - Getting Startedガイド（インストール方法と最初の使用例）
   - README.md（概要、ビルド方法、基本的な使い方、ライセンス）

4. **品質保証**
   - 第21章で学んだJUnit 5による単体テスト（正しいJSON、不正なJSON、空のオブジェクト・配列、文字列のエスケープ）
   - Mavenなら`mvn test`、Gradleなら`gradle test`でテストが通る

5. **ライセンス**
   - 第22章のライセンスの説明を参考に、ライブラリのライセンス（MITやApache License 2.0など）を選び、LICENSEファイルを置く

**サンプル使用例:**
```java
// JSONの解析
String jsonString = "{\"name\":\"John\",\"age\":30}";
JsonObject obj = JsonParser.parse(jsonString);
String name = obj.getString("name");
int age = obj.getInt("age");

// JSONの生成
JsonObject json = new JsonObject()
    .put("name", "John")
    .put("age", 30)
    .put("skills", new JsonArray()
        .add("Java")
        .add("Python"));
String result = json.toString();
```

## 成果物

1. **ソースコード**
   - 完全に動作するライブラリ
   - 包括的なテストスイート

2. **ドキュメント**
   - README.md（概要、インストール、基本使用法）
   - API Documentation（Javadoc）
   - チュートリアル
   - 貢献ガイドライン

3. **生成したAPIドキュメント**
   - `mvn javadoc:javadoc`（Mavenの場合）または`gradle javadoc`（Gradleの場合）で生成したHTML

## 評価基準

- **機能性**: 要件を満たし、正しく動作するか
- **使いやすさ**: APIが直感的で使いやすいか
- **ドキュメント**: 完全で分かりやすいドキュメントか
- **コード品質**: クリーンで保守しやすいコードか
- **テスト**: 正常な入力と不正な入力の両方をテストしているか

## 発展（任意）

- ファイルから少しずつ読みながら解析するストリーミングAPI（`JsonReader`）を加える
- 第21章で学んだJaCoCoでカバレッジを測る。第21章のGitHub Actionsの設定にならって、プッシュのたびにテストを実行する
- 第23章で学ぶ`jar`コマンドで、ライブラリのJARファイルを作る

## 提出方法

GitHubリポジトリとして提出します。
ソースコード、テスト、README.md、LICENSE、ビルドの設定ファイルを含め、`target/`や`build/`などの生成物は含めません。
## 解答例

[simple-json/](simple-json/)フォルダーは解答例です。上の「プロジェクト構成」のとおりに作ってあります。

```
simple-json/
├── src/main/java/com/example/json/
│   ├── JsonParser.java     JSONの文字列を解析する
│   ├── JsonBuilder.java    Javaの値からJSONの文字列を作る
│   ├── JsonObject.java     JSONのオブジェクト（型を指定して値を取り出す）
│   ├── JsonArray.java      JSONの配列
│   ├── JsonException.java  失敗を知らせる非検査例外（解析の失敗では場所も持つ）
│   ├── JsonValues.java     JsonObjectとJsonArrayが共通で使う型の検査（公開しない）
│   └── package-info.java   パッケージのJavadoc
├── src/test/java/com/example/json/
│   ├── JsonParserTest.java   正しいJSON・空のオブジェクトと配列・エスケープ・不正なJSON
│   ├── JsonObjectTest.java   値の出し入れ、型の違い、ないキー、比較
│   └── JsonBuilderTest.java  生成・エスケープ・字下げ・解析との往復
├── docs/getting-started.md
├── README.md
├── CONTRIBUTING.md        貢献ガイドライン
├── LICENSE                MITライセンス（自分で作るときは、著作権者を自分の名前にする）
├── pom.xml
└── build.gradle
```

**実行例**:

```bash
cd exercises/chapter22/documentation/challenge/simple-json
mvn test              # Tests run: 57, Failures: 0, Errors: 0, Skipped: 0
mvn javadoc:javadoc   # target/reports/apidocs/index.html ができる
```

Gradleでは`gradle test`と`gradle javadoc`を実行します（APIドキュメントは`build/docs/javadoc/`）。
どちらも、Javadocに警告が1つでもあればビルドが失敗する設定にしてあります。
`docs/getting-started.md`の例を実行したときの表示も、ガイドに載せています。

### 解答例の作り方

- 解析は、先頭から1文字ずつ見ていき、`{`ならオブジェクト、`[`なら配列、`"`なら文字列を読むメソッドを呼ぶ。オブジェクトや配列の中の値を読むときに、同じ`readValue`を呼び直す（再帰下降構文解析と呼ばれる方法）
- 読んでいる位置をフィールドに持ち、誤りを見つけたら、その位置を`JsonException`に入れて投げる。利用者は「17文字目: 最後の要素のあとにカンマは書けません」のように、どこが違うかを知ることができる
- `put`で受け取った`int`は`Long`に、`float`は`Double`にそろえて持つ。こうすると、作ったオブジェクトと、同じJSONを解析したオブジェクトを`equals`で比べられる。テストでは「作る → 文字列にする → 解析する」で元に戻ることを確かめている
- JSONで表せない値（`NaN`、`Date`など）は、`put`の時点で`JsonException`にする。文字列にするときまで誤りを持ち越さない

### 本文で扱っていない機能

- `{@snippet}`（Javadoc）: 使用例のコードを書くタグ（Java 18から）。`<pre>{@code ...}</pre>`と違い、`@`や`<`をそのまま書ける
- `failOnWarnings`（Mavenのjavadocプラグイン）と`-Werror`（Gradleの`javadoc`）: Javadocの警告を誤りとして扱い、書き漏れがあるとビルドを止める
- テキストブロック（`"""`で囲んだ複数行の文字列）: テストと入門ガイドで、複数行のJSONを書くのに使っている
- `assertAll`（JUnit）: 複数の確認をまとめて行い、1つ目が失敗しても残りの結果を表示する
