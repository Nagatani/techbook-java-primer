# 第22章 チャレンジ課題 - ドキュメンテーションとライブラリ

## 課題概要
プロダクションレベルの完全なライブラリプロジェクトを作成する総合課題です。

## 課題

### 完全なライブラリプロジェクトの作成
実用的なJSONパーサーライブラリ「SimpleJSON」を開発してください。

**要件:**

1. **コア機能**
   - JSONの解析（文字列→Javaオブジェクト）
   - JSONの生成（Javaオブジェクト→文字列）
   - ストリーミングAPI
   - 型安全なアクセサ

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
   │   ├── getting-started.md
   │   ├── api-reference.md
   │   └── examples/
   ├── .github/
   │   └── workflows/
   │       └── ci.yml
   ├── README.md
   ├── LICENSE
   ├── CONTRIBUTING.md
   └── pom.xml or build.gradle
   ```

3. **ドキュメンテーション**
   - 完全なJavadoc
   - Getting Startedガイド
   - APIリファレンス
   - 実例集
   - パフォーマンスガイド

4. **品質保証**
   - 単体テスト（90%以上のカバレッジ）
   - 統合テスト
   - パフォーマンステスト
   - コード品質チェック（SpotBugs、Checkstyle）

5. **CI/CD**
   - GitHub Actions設定
   - 自動テスト実行
   - 自動ドキュメント生成
   - リリース自動化

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

// ストリーミングAPI
try (JsonReader reader = new JsonReader(new FileReader("data.json"))) {
    reader.beginObject();
    while (reader.hasNext()) {
        String key = reader.nextName();
        // 処理...
    }
}
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

3. **ビルド成果物**
   - JARファイル
   - ソースJAR
   - JavadocJAR

4. **品質レポート**
   - テストカバレッジレポート
   - 静的解析レポート

## 評価基準

- **機能性**: 要件を満たし、正しく動作するか
- **使いやすさ**: APIが直感的で使いやすいか
- **ドキュメント**: 完全で分かりやすいドキュメントか
- **コード品質**: クリーンで保守しやすいコードか
- **テスト**: 十分なテストカバレッジがあるか
- **プロフェッショナリズム**: 実際のOSSプロジェクトとして公開できる品質か

## 提出方法

GitHubリポジトリとして提出し、以下を含めてください：
- 完全なソースコード
- ビルド済みアーティファクト
- 生成されたドキュメント
- CI/CDの設定と実行結果