# Contributing to Java Open Source Ecosystem

このプロジェクトへの貢献を検討いただき、ありがとうございます！このガイドでは、プロジェクトへの貢献方法について説明します。

## 行動規範

このプロジェクトは[行動規範](CODE_OF_CONDUCT.md)を採用しています。プロジェクトに参加することで、この規範を遵守することに同意したものとみなされます。

## 貢献の方法

### 1. Issueの作成

バグ報告、機能要望、質問などは、GitHubのIssueを通じて行ってください。

#### バグ報告

バグを報告する際は、以下の情報を含めてください：

- **環境情報**
  - OS（Windows/macOS/Linux）とバージョン
  - Javaのバージョン（`java -version`の出力）
  - プロジェクトのバージョン
  
- **再現手順**
  1. 実行したコマンドまたはコード
  2. 期待される動作
  3. 実際の動作
  
- **エラーメッセージ**
  - スタックトレース全体
  - 関連するログ

#### 機能要望

新機能の提案時は以下を含めてください：

- **ユースケース**: なぜこの機能が必要か
- **提案する解決策**: どのように実装するか
- **代替案**: 検討した他の方法
- **追加情報**: 参考リンクや類似実装

### 2. Pull Requestの作成

#### 準備

1. **フォーク**: プロジェクトをフォークします
2. **クローン**: フォークしたリポジトリをローカルにクローン
   ```bash
   git clone https://github.com/yourusername/opensource-ecosystem.git
   cd opensource-ecosystem
   ```
3. **ブランチ作成**: 機能ごとにブランチを作成
   ```bash
   git checkout -b feature/your-feature-name
   ```

#### 開発

1. **コーディング規約**に従ってコードを記述
2. **テスト**を追加または更新
3. **ドキュメント**を更新

#### コミット

セマンティックコミットメッセージを使用してください：

```
feat: 新機能の追加
fix: バグ修正
docs: ドキュメントのみの変更
style: コードの意味に影響しない変更（空白、フォーマット等）
refactor: バグ修正や機能追加を伴わないコード変更
test: テストの追加や修正
chore: ビルドプロセスやツールの変更
```

例：
```bash
git commit -m "feat: Add support for YAML data transformation"
git commit -m "fix: Resolve memory leak in plugin loader"
git commit -m "docs: Update API documentation for Version class"
```

#### プルリクエストの提出

1. **変更をプッシュ**
   ```bash
   git push origin feature/your-feature-name
   ```

2. **PRを作成**: GitHubでPull Requestを作成

3. **PRテンプレートに記入**:
   - 変更の概要
   - 関連するIssue番号
   - テスト方法
   - チェックリスト

## コーディング規約

### Javaコーディング標準

1. **命名規則**
   - クラス名: PascalCase（例: `HttpRequestBuilder`）
   - メソッド名: camelCase（例: `getUserById`）
   - 定数: UPPER_SNAKE_CASE（例: `MAX_RETRY_COUNT`）
   - パッケージ名: 小文字（例: `com.example.oss`）

2. **フォーマット**
   - インデント: スペース4つ
   - 行の最大長: 120文字
   - 中括弧: K&Rスタイル

3. **Javadoc**
   - すべての公開API（public/protectedなクラス、メソッド、フィールド）
   - `@param`, `@return`, `@throws`を必ず記述
   - 使用例を含める

### 例

```java
/**
 * HTTPリクエストを実行します
 * 
 * <p>指定されたURLに対してHTTPリクエストを送信し、
 * レスポンスを返します。
 * 
 * <h3>使用例</h3>
 * <pre>{@code
 * HttpResponse response = client.execute(
 *     HttpMethod.GET, 
 *     "/api/users", 
 *     User.class
 * );
 * }</pre>
 * 
 * @param method HTTPメソッド
 * @param path APIパス
 * @param responseType レスポンスの型
 * @return HTTPレスポンス
 * @throws ApiClientException API呼び出しに失敗した場合
 * @since 1.0.0
 */
public <T> HttpResponse<T> execute(HttpMethod method, String path, Class<T> responseType) 
        throws ApiClientException {
    // 実装
}
```

## テスト

### テストの作成

1. **単体テスト**: すべての公開メソッドに対してテストを作成
2. **統合テスト**: 複数のコンポーネントの連携をテスト
3. **カバレッジ**: 80%以上のコードカバレッジを維持

### テストの実行

```bash
# すべてのテストを実行
mvn test

# 特定のテストクラスを実行
mvn test -Dtest=VersionTest

# 統合テストを含めて実行
mvn verify
```

### テストの書き方

```java
@Test
@DisplayName("バージョン文字列の解析が正しく動作すること")
void testVersionParsing() {
    // Given
    String versionString = "1.2.3-alpha.1+build.123";
    
    // When
    Version version = Version.parse(versionString);
    
    // Then
    assertThat(version.getMajor()).isEqualTo(1);
    assertThat(version.getMinor()).isEqualTo(2);
    assertThat(version.getPatch()).isEqualTo(3);
    assertThat(version.getPreRelease()).isEqualTo("alpha.1");
    assertThat(version.getBuild()).isEqualTo("build.123");
}
```

## ドキュメント

### APIドキュメント

- Javadocコメントを使用
- 公開APIはすべて文書化
- 使用例を含める

### README更新

新機能を追加した場合：
- 「主要コンポーネント」セクションに追加
- 使用例を追加
- 必要に応じて「クイックスタート」を更新

## リリースプロセス

### バージョニング

セマンティックバージョニングに従います：
- **MAJOR**: 後方互換性のない変更
- **MINOR**: 後方互換性のある機能追加
- **PATCH**: 後方互換性のあるバグ修正

### リリース手順

1. `CHANGELOG.md`を更新
2. バージョン番号を更新（`pom.xml`）
3. タグを作成
   ```bash
   git tag -a v1.2.3 -m "Release version 1.2.3"
   git push origin v1.2.3
   ```

## 質問とサポート

- **質問**: GitHub Discussionsを使用
- **バグ**: GitHub Issuesで報告
- **セキュリティ**: [SECURITY.md](SECURITY.md)を参照

## ライセンス

貢献されたコードは、プロジェクトと同じ[Apache License 2.0](LICENSE)の下でライセンスされます。

## 謝辞

貢献者の皆様に感謝します！あなたの貢献がこのプロジェクトを素晴らしいものにしています。