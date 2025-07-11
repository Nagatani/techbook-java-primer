# 第22章 発展課題: 実践的なライブラリ活用

## 課題1: RESTful APIクライアントの実装

### 背景
OkHttpライブラリを使用して、実践的なAPIクライアントを作成します。

### 要件
GitHub APIを利用するクライアントライブラリを実装してください。

#### 実装すべきクラス
```java
public class GitHubClient {
    private final OkHttpClient httpClient;
    private final Gson gson;
    private final String baseUrl = "https://api.github.com";
    
    // ユーザー情報を取得
    public GitHubUser getUser(String username) {
        // 実装してください
    }
    
    // ユーザーのリポジトリ一覧を取得
    public List<Repository> getUserRepositories(String username) {
        // 実装してください
    }
    
    // リポジトリの詳細情報を取得
    public Repository getRepository(String owner, String repo) {
        // 実装してください
    }
    
    // レート制限情報を取得
    public RateLimit getRateLimit() {
        // 実装してください
    }
}
```

#### データモデル
```java
public class GitHubUser {
    private String login;
    private String name;
    private String email;
    private String bio;
    private int publicRepos;
    private int followers;
    private int following;
    private String createdAt;
    // getter/setter
}

public class Repository {
    private String name;
    private String fullName;
    private String description;
    private boolean isPrivate;
    private String language;
    private int stargazersCount;
    private int forksCount;
    private String createdAt;
    private String updatedAt;
    // getter/setter
}
```

### 高度な要件
1. **エラーハンドリング**: API制限、ネットワークエラー、404エラーなど
2. **非同期処理**: `CompletableFuture`を使用した非同期メソッドの提供
3. **キャッシング**: 同じリクエストの結果を一定時間キャッシュ
4. **ログ出力**: SLF4Jを使用した適切なログ記録

---

## 課題2: カスタムJavadocタグレットの作成

### 背景
プロジェクト固有のドキュメント要件に対応するため、カスタムタグを作成します。

### 要件
以下のカスタムJavadocタグを実装してください。

#### カスタムタグ
1. `@requirement`: 要件IDへのリンク
2. `@performance`: パフォーマンス特性の記述
3. `@thread-safe`: スレッドセーフティの保証レベル

#### 使用例
```java
/**
 * 高性能なデータ処理エンジン
 * 
 * @requirement REQ-001, REQ-023
 * @performance O(n log n)の時間計算量、メモリ使用量はO(n)
 * @thread-safe 完全にスレッドセーフ（内部で同期化）
 */
public class DataProcessor {
    // 実装
}
```

### 実装内容
1. カスタムタグレットクラスの作成
2. タグの情報を含むHTMLレポートの生成
3. Mavenプラグインとしての統合

---

## 課題3: マルチフォーマット対応データコンバータ

### 背景
複数のライブラリを組み合わせて、柔軟なデータ変換ツールを作成します。

### 要件
以下の形式間でデータを相互変換できるツールを実装してください。

#### サポートする形式
- JSON (Gson/Jackson)
- XML (JAXB)
- CSV (Apache Commons CSV)
- YAML (SnakeYAML)

#### 実装すべきインターフェース
```java
public interface DataConverter {
    <T> String convert(T object, Format from, Format to);
    <T> T parse(String data, Format format, Class<T> clazz);
}

public enum Format {
    JSON, XML, CSV, YAML
}
```

#### サンプル実装
```java
@Component
public class UniversalDataConverter implements DataConverter {
    private final Map<Format, Serializer> serializers;
    private final Map<Format, Deserializer> deserializers;
    
    // 依存性注入でシリアライザーを登録
    public UniversalDataConverter(List<Serializer> serializers, 
                                  List<Deserializer> deserializers) {
        // 実装
    }
    
    @Override
    public <T> String convert(T object, Format from, Format to) {
        // 実装：from形式でデシリアライズ → to形式でシリアライズ
    }
}
```

### 追加要件
1. **プラグイン機構**: 新しい形式を簡単に追加できる設計
2. **バリデーション**: 各形式に応じたデータ検証
3. **ストリーミング**: 大きなファイルの効率的な処理
4. **CLIツール**: コマンドラインから使用可能

---

## チャレンジ課題: 独自ライブラリの作成と公開

### 背景
学んだ知識を活かして、他の開発者が使える有用なライブラリを作成します。

### 要件
以下の要件を満たす独自のユーティリティライブラリを作成してください。

#### ライブラリの機能（例）
1. **高度な文字列処理**: 自然言語処理、テンプレートエンジンなど
2. **日付時間ユーティリティ**: 営業日計算、タイムゾーン変換など
3. **検証フレームワーク**: Bean Validationの拡張
4. **その他**: 独自のアイデア

#### 必須要件
1. **完全なJavadoc**: すべてのpublicAPIにドキュメント
2. **包括的なテスト**: 90%以上のカバレッジ
3. **使用例**: READMEとサンプルコード
4. **ビルド設定**: Maven CentralまたはGitHub Packagesへの公開準備
5. **ライセンス**: 適切なオープンソースライセンスの選択

#### プロジェクト構造
```
my-awesome-library/
├── src/
│   ├── main/java/
│   └── test/java/
├── docs/
│   ├── api/        # Javadoc
│   └── guides/     # 使用ガイド
├── examples/       # サンプルプロジェクト
├── pom.xml
├── README.md
├── LICENSE
└── CONTRIBUTING.md
```

### 提出物
1. GitHubリポジトリのURL
2. ライブラリの設計ドキュメント
3. パフォーマンスベンチマーク結果
4. 将来の拡張計画

## 評価基準
- コードの品質と設計
- ドキュメントの充実度
- テストの網羅性
- 実用性と独創性
- ビルド/配布の準備状況