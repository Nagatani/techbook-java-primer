# 付録G: 高度なビルドとデプロイメント技術

この付録では、第23章で学んだ基本的なビルドとデプロイメントの知識を発展させ、実務で必要となる高度な技術を解説します。

## 目次

1. [ビルドツールによる自動化](#ビルドツールによる自動化)
   - Maven設定
   - Gradle設定
   - Fat JARの作成
2. [jpackageの高度な活用](#jpackageの高度な活用)
   - プラットフォーム固有の設定
   - カスタマイズオプション
3. [トラブルシューティング](#トラブルシューティング)
   - よくあるエラーと対処法

## ビルドツールによる自動化

現代のJava開発では、MavenやGradleなどのビルドツールを使用してプロジェクトを管理するのが一般的です。

### Mavenでの実行可能JAR作成

#### 基本的なpom.xml設定

設定例は [pom.xml](./maven/pom.xml) を参照してください。
`maven/`には設定の抜粋だけを置いています（ソースコードは含みません）。
ソースコードを含み、そのままビルドして動かせるプロジェクトは[sample-projects/maven-project](./sample-projects/maven-project/)にあります。

主な設定ポイント：
- `maven-jar-plugin`：通常の実行可能JARの作成
- `maven-shade-plugin`：依存ライブラリを含むFat JARの作成

#### ビルドコマンド

`src/main/java/com/example/TodoApp.java`を置いたプロジェクトで、次のように実行します。
maven-shade-pluginは`package`のときに、通常のJARを依存ライブラリを含むFat JARで置き換えます（元のJARは`original-todo-app-1.0.0.jar`として残ります）。

```bash
# クリーンビルド
mvn clean package

# 実行可能JARの実行
java -jar target/todo-app-1.0.0.jar
```

### Gradleでの実行可能JAR作成

#### build.gradleの設定

設定例は [build.gradle](./gradle/build.gradle) と [settings.gradle](./gradle/settings.gradle) を参照してください。
`gradle/`も設定の抜粋だけで、動かせるプロジェクトは[sample-projects/gradle-project](./sample-projects/gradle-project/)にあります。

主な設定ポイント：
- `application`プラグイン：アプリケーションの実行と配布
- カスタムFat JARタスク：依存関係を含むJARの作成（重複するファイルの扱い`duplicatesStrategy`の指定が必要）

#### ビルドコマンド

`src/main/java/com/example/TodoApp.java`を置いたプロジェクトで、次のように実行します（Gradle 9）。
JARの名前は`settings.gradle`の`rootProject.name`から決まります。

```bash
# 通常のビルド
gradle build

# Fat JARの作成
gradle fatJar

# 実行
java -jar build/libs/todo-app-1.0.0-all.jar
```

### Fat JARの詳細な作成方法

外部ライブラリを使用するアプリケーションのサンプルコード：
- [JsonProcessorApp.java](./examples/JsonProcessorApp.java)

Fat JARを手動で作成する場合のスクリプト：
- [create-fat-jar.sh](./scripts/create-fat-jar.sh)

スクリプトは、`lib/gson-2.15.0.jar`（Maven Centralから取得）とコンパイル済みのクラスがあるディレクトリで実行します。

```bash
cp examples/JsonProcessorApp.java .
javac -cp lib/gson-2.15.0.jar JsonProcessorApp.java
bash scripts/create-fat-jar.sh
java -jar JsonProcessorApp-all.jar
```

## jpackageの高度な活用

### プラットフォーム固有の設定

#### Windows向けの設定

詳細な設定例：[jpackage-windows.sh](./scripts/jpackage-windows.sh)

主な設定オプション：
- アプリケーションアイコン（.ico形式）
- スタートメニューとデスクトップショートカット
- インストールディレクトリの選択
- ユーザーごとのインストール

#### macOS向けの設定

詳細な設定例：[jpackage-macos.sh](./scripts/jpackage-macos.sh)

主な設定オプション：
- アプリケーションアイコン（.icns形式）
- バンドル識別子
- コード署名（開発者証明書を使う）

インターネットで配布するアプリケーションをGatekeeperに通すには、署名に加えてAppleの公証（`xcrun notarytool`で申請する）が必要です。

#### Linux向けの設定

詳細な設定例：[jpackage-linux.sh](./scripts/jpackage-linux.sh)

主な設定オプション：
- メニューカテゴリとショートカット
- パッケージメンテナー情報
- アプリケーションカテゴリ

### カスタマイズオプション

#### JVMパラメータの設定

```bash
jpackage --type app-image \
         --name "TodoManager" \
         --input ./input \
         --main-jar TodoApp.jar \
         --java-options "-Xmx512m" \
         --java-options "-Dapp.mode=production" \
         --dest ./output
```

`--java-options`は、起動するJVMに渡すオプションを指定します（複数回書けます）。
`-D名前=値`で渡した値は、プログラムから`System.getProperty("app.mode")`のように読み出せます。
Java 18以降は`file.encoding`の既定がUTF-8なので、`-Dfile.encoding=UTF-8`を付ける必要はありません。

#### ファイル関連付け

設定例：[file-associations.properties](./config/file-associations.properties)

このファイルを`--file-associations`オプションで渡すと、拡張子`.todo`のファイルをアプリケーションで開けるようになります。
設定例のアイコンは`.ico`形式なので、Windows向けです（macOSでは`.icns`、Linuxでは`.png`を指定します）。

```bash
jpackage --type msi \
         --name "TodoManager" \
         --input ./input \
         --main-jar TodoApp.jar \
         --file-associations ./config/file-associations.properties \
         --dest ./output
```

## トラブルシューティング

### よくあるエラーと対処法

#### Mavenビルドエラー

1. **依存関係の解決エラー**
   - 原因：指定した依存関係が見つからない
   - 解決方法：[Maven依存関係の確認方法](./troubleshooting/maven-dependencies.md)

2. **Javaバージョンの不一致**
   - 原因：プロジェクトと実行環境のJavaバージョンが異なる
   - 解決方法：[Javaバージョンの設定](./troubleshooting/java-version.md)

3. **メモリ不足エラー**
   - 原因：ビルド時のメモリ不足
   - 解決方法：[Mavenメモリ設定](./troubleshooting/maven-memory.md)

#### Gradleの設定問題

詳細は [Gradleトラブルシューティング](./troubleshooting/gradle-issues.md) を参照してください。

#### JARファイルの実行エラー

1. **Main-Classが見つからない**
   - 解決方法：[マニフェスト設定の確認](./troubleshooting/manifest-issues.md)

2. **依存関係が含まれていない**
   - 解決方法：[Fat JARの作成方法](./troubleshooting/fat-jar-issues.md)

#### 環境依存の問題

- [文字エンコーディングの問題](./troubleshooting/encoding-issues.md)
- [Javaバージョンの違い](./troubleshooting/java-version.md)

#### jpackageの問題

- [必要なツールのインストール](./troubleshooting/jpackage-tools.md)

## サンプルプロジェクト

ソースコード・テスト・設定ファイルをそろえた、そのままビルドして動かせるプロジェクトです。
依存ライブラリの版はMaven版とGradle版でそろえています。

- [Mavenプロジェクト](./sample-projects/maven-project/)
- [Gradleプロジェクト](./sample-projects/gradle-project/)（Gradle Wrapper付き）

## 参考資料

- [Oracle公式 jarツールドキュメント](https://docs.oracle.com/en/java/javase/25/docs/specs/man/jar.html)
- [Oracle公式 jpackageドキュメント](https://docs.oracle.com/en/java/javase/25/docs/specs/man/jpackage.html)
- [Maven公式ドキュメント](https://maven.apache.org/guides/)
- [Gradle公式ドキュメント](https://docs.gradle.org/)