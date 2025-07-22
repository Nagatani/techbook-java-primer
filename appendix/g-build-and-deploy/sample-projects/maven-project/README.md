# Mavenサンプルプロジェクト

## プロジェクト構成

```
maven-project/
├── pom.xml                 # Maven設定ファイル
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
mvn clean package

# テストをスキップしてビルド
mvn clean package -DskipTests
```

### 実行方法

```bash
# 通常のJAR実行（依存関係は別途必要）
java -cp "target/sample-todo-app.jar:target/lib/*" com.example.TodoApp

# Fat JAR実行（すべての依存関係を含む）
java -jar target/sample-todo-app-all.jar
```

### jpackageでのネイティブアプリ作成

```bash
# jpackageプロファイルを使用してビルド
mvn clean package -Pjpackage

# 作成されたアプリケーションは target/jpackage/ に生成される
```

## 依存関係の管理

### 依存関係ツリーの表示

```bash
mvn dependency:tree
```

### 依存関係のダウンロード

```bash
# 依存関係を target/lib にコピー
mvn dependency:copy-dependencies -DoutputDirectory=target/lib
```

## トラブルシューティング

### メモリ不足の場合

```bash
export MAVEN_OPTS="-Xmx2g -XX:MaxPermSize=512m"
mvn clean package
```

### エンコーディングの問題

pom.xmlで以下が設定されていることを確認：

```xml
<properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
</properties>
```