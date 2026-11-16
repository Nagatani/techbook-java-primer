# Gradleサンプルプロジェクト

外部ライブラリ（Gson、Commons Lang、Logback）を使う小さなTodoアプリケーションを、Gradle 9でビルドして配布するサンプルです。
Gradle Wrapper（`gradlew`）を同梱しているため、Gradleをインストールしなくてもビルドできます（JDK 25が必要です）。
依存ライブラリの版は、Maven版（`../maven-project/pom.xml`）とそろえています。

## プロジェクト構成

```
gradle-project/
├── build.gradle            # Gradle設定ファイル
├── settings.gradle         # プロジェクト名設定
├── gradle.properties       # Gradleプロパティ
├── gradlew                 # Gradle Wrapper（macOS/Linux）
├── gradlew.bat             # Gradle Wrapper（Windows）
├── gradle/
│   └── wrapper/            # Wrapperの本体と、使うGradleの版（9.9.1）
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

Windowsでは、以下のコマンドの`./gradlew`を`gradlew.bat`に読み替えてください。

## ビルドと実行

### 基本的なビルド

```bash
# クリーンビルド（テストも実行する）
./gradlew clean build

# テストをスキップしてビルド
./gradlew clean build -x test
```

`build`で、通常のJAR・Fat JAR（`build/libs/`）と、通常のJARが使う依存ライブラリ（`build/libs/lib/`）ができます。

### 実行方法

```bash
# Gradleから直接実行
./gradlew run

# 通常のJAR実行（build/libs/lib/ の依存ライブラリを読み込む）
java -jar build/libs/sample-todo-app-1.0.0.jar

# Fat JAR実行（shadowプラグインで作った、すべての依存関係を含むJAR）
java -jar build/libs/sample-todo-app-1.0.0-all.jar
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
# jpackageタスクを実行
./gradlew jpackage
```

作成されたアプリケーションは`build/jpackage-output/TodoApp`に生成されます。
jpackageの入力にはFat JARだけを置いたディレクトリ（`build/jpackage-input`）を使い、出力先と分けています。
Linuxでは、jpackageが使う`objcopy`（binutilsパッケージ）が必要です。

## 依存関係の管理

### 依存関係の表示

```bash
# 依存関係ツリー
./gradlew dependencies

# 特定の設定の依存関係
./gradlew dependencies --configuration runtimeClasspath

# build.gradleに直接書いた依存の一覧（build.gradleで定義したタスク）
./gradlew dependencyReport
```

## 便利なタスク

### 配布用パッケージの作成

Fat JARとREADMEを`build/distribution`にまとめます。

```bash
./gradlew createDistribution
```

### カスタムFat JARの作成

shadowプラグインを使わずに、`Jar`タスクだけでFat JAR（`build/libs/sample-todo-app-1.0.0-custom-all.jar`）を作ります。

```bash
./gradlew customFatJar
```

## トラブルシューティング

### Gradle Wrapperの権限エラー

```bash
chmod +x gradlew
```

### メモリ不足の場合

`gradle.properties`で以下を調整します。

```properties
org.gradle.jvmargs=-Xmx4g -XX:MaxMetaspaceSize=1g
```

### ビルドキャッシュのクリア

```bash
./gradlew clean --no-build-cache
rm -rf ~/.gradle/caches/
```
