# 第23章 発展課題: プロフェッショナルなビルドとデプロイ

## 課題1: Maven/Gradleを使用した本格的なビルドシステム

### 背景
実際の開発現場では、ビルドツールを使用してプロジェクトを管理します。

### 要件
TODOリストアプリケーションをMavenまたはGradleプロジェクトとして構築してください。

#### プロジェクト構造
```
todo-app/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/todo/
│   │   │       ├── App.java
│   │   │       ├── model/
│   │   │       ├── view/
│   │   │       └── controller/
│   │   └── resources/
│   │       ├── application.properties
│   │       └── icons/
│   └── test/
│       └── java/
│           └── com/example/todo/
├── pom.xml (or build.gradle)
└── README.md
```

#### 機能要件
1. GUI付きTODOリストアプリケーション
2. データの永続化（JSONファイル）
3. 検索・フィルタリング機能
4. エクスポート機能（CSV/JSON）

#### ビルド設定要件
```xml
<!-- pom.xml の例 -->
<project>
    <properties>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>
    
    <dependencies>
        <!-- Gson for JSON -->
        <dependency>
            <groupId>com.google.code.gson</groupId>
            <artifactId>gson</artifactId>
            <version>2.10.1</version>
        </dependency>
        
        <!-- JUnit for testing -->
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <version>5.10.0</version>
            <scope>test</scope>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <!-- 実行可能JARプラグイン -->
            <!-- Fat JARプラグイン -->
        </plugins>
    </build>
</project>
```

### 実装すべきビルドタスク
1. `clean`: ビルド成果物のクリーン
2. `compile`: ソースコードのコンパイル
3. `test`: ユニットテストの実行
4. `package`: JARファイルの作成
5. `assembly`: 配布用パッケージの作成

---

## 課題2: jpackageによるネイティブインストーラの作成

### 背景
エンドユーザー向けの配布では、OSネイティブのインストーラが求められます。

### 要件
課題1で作成したTODOアプリケーションを、各OS向けのインストーラとしてパッケージ化してください。

#### 対象OS（最低1つ、できれば複数）
- Windows: MSIインストーラ
- macOS: DMGイメージ
- Linux: DEBまたはRPMパッケージ

#### インストーラの要件
1. **アプリケーション情報**
   - 名前: "Todo Manager"
   - バージョン: 1.0.0
   - 開発者: あなたの名前
   - 説明: "シンプルで使いやすいTODO管理アプリ"

2. **インストールオプション**
   - デスクトップショートカット
   - スタートメニュー登録（Windows）
   - ファイル関連付け（.todo拡張子）

3. **アイコンとブランディング**
   - アプリケーションアイコン
   - インストーラのカスタムグラフィック

#### jpackage設定スクリプト
```bash
# package-windows.sh
jpackage --type msi \
  --name "Todo Manager" \
  --app-version "1.0.0" \
  --vendor "Your Name" \
  --description "Simple and easy-to-use TODO management app" \
  --input ./target \
  --main-jar todo-app.jar \
  --icon ./resources/app-icon.ico \
  --win-menu \
  --win-shortcut \
  --win-dir-chooser \
  --file-associations ./file-associations.properties \
  --dest ./dist
```

### 追加要件
- インストール後の初回起動ガイド
- アンインストーラの適切な動作
- アップデート機能の検討

---

## 課題3: CI/CDパイプラインの構築

### 背景
現代の開発では、自動化されたビルドとデプロイが不可欠です。

### 要件
GitHub Actionsを使用して、自動ビルド・リリースパイプラインを構築してください。

#### ワークフロー設定（.github/workflows/build.yml）
```yaml
name: Build and Release

on:
  push:
    branches: [ main ]
    tags: [ 'v*' ]
  pull_request:
    branches: [ main ]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      # テストジョブの実装
  
  build:
    needs: test
    strategy:
      matrix:
        os: [ubuntu-latest, windows-latest, macos-latest]
    runs-on: ${{ matrix.os }}
    steps:
      # ビルドジョブの実装
  
  release:
    if: startsWith(github.ref, 'refs/tags/')
    needs: build
    runs-on: ubuntu-latest
    steps:
      # リリースジョブの実装
```

#### 実装すべき機能
1. **自動テスト**
   - プッシュ時に全テストを実行
   - テストレポートの生成

2. **マルチプラットフォームビルド**
   - 各OS用のバイナリを生成
   - アーティファクトとして保存

3. **自動リリース**
   - タグプッシュ時にリリースを作成
   - ビルド成果物を添付
   - リリースノートの自動生成

### 高度な機能
- コードカバレッジレポート
- 依存関係の脆弱性チェック
- ビルド成果物の署名

---

## チャレンジ課題: モダンな配布戦略の実装

### 背景
さまざまな配布チャネルに対応した、包括的な配布システムを構築します。

### 要件
以下の配布方法をすべて実装してください。

#### 1. 自動更新機能付きアプリケーション
```java
public class AutoUpdater {
    private static final String UPDATE_URL = "https://api.github.com/repos/yourname/todo-app/releases/latest";
    
    public void checkForUpdates() {
        // 最新バージョンをチェック
        // ダウンロードと適用
    }
}
```

#### 2. Dockerコンテナ化
```dockerfile
# マルチステージビルド
FROM maven:3.9-openjdk-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package

FROM openjdk:21-slim
COPY --from=build /app/target/todo-app.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

#### 3. Web Start代替（JNLP後継）
- Webサーバーからの直接起動
- 自動更新機能
- セキュリティサンドボックス

#### 4. パッケージマネージャー対応
```bash
# Homebrew (macOS)
brew tap yourname/todo-app
brew install todo-app

# Scoop (Windows)
scoop bucket add todo-app https://github.com/yourname/scoop-todo-app
scoop install todo-app
```

### 実装すべきコンポーネント
1. **更新サーバー**
   - バージョン情報API
   - 差分更新の配信
   - ロールバック機能

2. **配布ポータル**
   - ダウンロードページ
   - インストール手順
   - トラブルシューティング

3. **分析システム**
   - インストール数の追跡
   - クラッシュレポート
   - 使用統計

## 提出物
1. 完全なプロジェクトソースコード
2. ビルド成果物（各OS向け）
3. CI/CD設定ファイル
4. 配布ドキュメント
5. デモ動画（インストールから実行まで）

## 評価基準
- ビルドシステムの完成度
- 配布パッケージの品質
- 自動化のレベル
- ドキュメントの充実度
- ユーザー体験の考慮