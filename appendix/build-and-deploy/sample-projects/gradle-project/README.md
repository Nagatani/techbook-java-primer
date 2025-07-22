# Gradleサンプルプロジェクト

## プロジェクト構成

```
gradle-project/
├── build.gradle            # Gradle設定ファイル
├── settings.gradle         # プロジェクト名設定
├── gradle.properties       # Gradleプロパティ
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── example/
│   │   │           └── TodoApp.java
│   │   └── resources/
│   │       ├── logback.xml
│   │       └── application.properties
│   └── test/
│       └── java/
│           └── com/
│               └── example/
│                   └── TodoAppTest.java
└── README.md
```

## ビルドと実行

### 基本的なビルド

```bash
# クリーンビルド
./gradlew clean build

# テストをスキップしてビルド
./gradlew clean build -x test
```

### 実行方法

```bash
# Gradleから直接実行
./gradlew run

# 通常のJAR実行
java -jar build/libs/sample-todo-app-1.0.0.jar

# Fat JAR実行（shadowプラグイン使用）
java -jar build/libs/sample-todo-app-1.0.0-all.jar
```

### jpackageでのネイティブアプリ作成

```bash
# jpackageタスクを実行
./gradlew jpackage

# 作成されたアプリケーションは build/jpackage-output/ に生成される
```

## 依存関係の管理

### 依存関係の表示

```bash
# 依存関係ツリー
./gradlew dependencies

# 特定の設定の依存関係
./gradlew dependencies --configuration runtimeClasspath

# カスタムレポート
./gradlew dependencyReport
```

### 依存関係の更新確認

```bash
# 古い依存関係の確認
./gradlew dependencyUpdates
```

## 便利なタスク

### 配布用パッケージの作成

```bash
./gradlew createDistribution
```

### カスタムFat JARの作成

```bash
./gradlew customFatJar
```

## トラブルシューティング

### Gradle Wrapperの権限エラー

```bash
chmod +x gradlew
```

### メモリ不足の場合

`gradle.properties`で以下を調整：

```properties
org.gradle.jvmargs=-Xmx4g -XX:MaxMetaspaceSize=1g
```

### ビルドキャッシュのクリア

```bash
./gradlew clean --no-build-cache
rm -rf ~/.gradle/caches/
```