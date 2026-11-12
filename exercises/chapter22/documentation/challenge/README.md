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