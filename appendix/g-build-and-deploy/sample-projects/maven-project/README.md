# Mavenサンプルプロジェクト

外部ライブラリ（Gson、Commons Lang、Logback）を使う小さなTodoアプリケーションを、Mavenでビルドして配布するサンプルです。
JDK 25とMaven 3.9以降が必要です。
依存ライブラリの版は、Gradle版（`../gradle-project/build.gradle`）とそろえています。

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
# クリーンビルド（テストも実行する）
mvn clean package

# テストをスキップしてビルド
mvn clean package -DskipTests
```

`package`で次のものが`target/`にできます。

- `sample-todo-app-1.0.0.jar`（通常のJAR）
- `lib/`（通常のJARが使う依存ライブラリ。maven-dependency-pluginがコピーする）
- `sample-todo-app-1.0.0-all.jar`（すべての依存関係を含むFat JAR。maven-shade-pluginが作る）

### 実行方法

```bash
# 通常のJAR実行（マニフェストのClass-Pathで target/lib/ の依存ライブラリを読み込む）
java -jar target/sample-todo-app-1.0.0.jar

# クラスパスを指定して実行（Windowsでは区切りの「:」を「;」にする）
java -cp "target/sample-todo-app-1.0.0.jar:target/lib/*" com.example.TodoApp

# Fat JAR実行（すべての依存関係を含む）
java -jar target/sample-todo-app-1.0.0-all.jar
```

実行すると、標準出力に次の内容が出ます。
ログ（「追加しました」の行）は`logback.xml`の設定で標準エラー出力に出ます。

```
Todoアプリ 1.0.0
[
  {
    "title": "牛乳を買う",
    "done": false
  },
  {
    "title": "レポートを 書く",
    "done": false
  }
]
```

### jpackageでのネイティブアプリ作成

```bash
# jpackageプロファイルを使用してビルド
mvn clean package -Pjpackage
```

作成されたアプリケーションは`target/jpackage/TodoApp`に生成されます。
jpackageの入力にはFat JARだけをコピーしたディレクトリ（`target/jpackage-input`）を使い、出力先と分けています。
Linuxでは、jpackageが使う`objcopy`（binutilsパッケージ）が必要です。

## 依存関係の管理

### 依存関係ツリーの表示

```bash
mvn dependency:tree
```

### 依存関係のダウンロード

`mvn package`でも`target/lib`にコピーされますが、コピーだけを行うこともできます。

```bash
# 実行に使う依存関係（テスト用を除く）を target/lib にコピー
mvn dependency:copy-dependencies -DoutputDirectory=target/lib -DincludeScope=runtime
```

## トラブルシューティング

### メモリ不足の場合

```bash
export MAVEN_OPTS="-Xmx2g -XX:MaxMetaspaceSize=512m"
mvn clean package
```

### エンコーディングの問題

pom.xmlで以下が設定されていることを確認します。

```xml
<properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
</properties>
```
