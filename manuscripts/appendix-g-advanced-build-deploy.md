# <b>付録G</b> <span>高度なビルドとデプロイメント技術</span> <small>プロフェッショナルのためのビルド・配布戦略</small>

## はじめに

本付録では、第23章で学んだ基本的なビルドとデプロイメントの知識を発展させ、実務で必要となる高度な技術を解説します。ビルドツールの詳細な設定、複雑な依存関係の管理、プラットフォーム固有の最適化、そして実際の開発現場で遭遇する問題の解決方法について学びます。

## ビルドツールによる自動化

現代のJava開発では、MavenやGradleなどのビルドツールを使用してプロジェクトを管理するのが一般的です。これらのツールを使用することで、依存関係の自動解決や実行可能JARの作成プロセスがコマンド1つで実行できるようになります。

### Mavenでの実行可能JAR作成

#### 基本的なpom.xml設定

以下はpom.xmlの設定例です。maven-jar-pluginで通常の実行可能JARを、maven-shade-pluginで依存ライブラリを含むFat JARを作成できます。

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.example</groupId>
    <artifactId>todo-app</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>
    
    <properties>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>com.google.code.gson</groupId>
            <artifactId>gson</artifactId>
            <version>2.10.1</version>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <!-- 実行可能JARを作成するプラグイン -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-jar-plugin</artifactId>
                <version>3.3.0</version>
                <configuration>
                    <archive>
                        <manifest>
                            <addClasspath>true</addClasspath>
                            <mainClass>com.example.TodoApp</mainClass>
                        </manifest>
                    </archive>
                </configuration>
            </plugin>
            
            <!-- Fat JARを作成するプラグイン -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-shade-plugin</artifactId>
                <version>3.5.1</version>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals>
                            <goal>shade</goal>
                        </goals>
                        <configuration>
                            <transformers>
                                <transformer
                                    implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                                    <mainClass>com.example.TodoApp</mainClass>
                                </transformer>
                            </transformers>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

#### Mavenでのビルドコマンド

```bash
# クリーンビルド
mvn clean package

# 実行可能JARの実行
java -jar target/todo-app-1.0.0.jar
```

### Gradleでの実行可能JAR作成

#### build.gradleの設定

Gradleでの設定例です。applicationプラグインを使用することで、アプリケーションの実行と配布が簡単になります。カスタムタスクでFat JARの作成も定義しています。

```gradle
plugins {
    id 'java'
    id 'application'
}

group = 'com.example'
version = '1.0.0'

repositories {
    mavenCentral()
}

dependencies {
    implementation 'com.google.code.gson:gson:2.10.1'
    implementation 'org.apache.commons:commons-lang3:3.12.0'
}

application {
    mainClass = 'com.example.TodoApp'
}

jar {
    manifest {
        attributes(
            'Main-Class': 'com.example.TodoApp',
            'Implementation-Title': 'Todo Application',
            'Implementation-Version': version
        )
    }
}

// Fat JARタスク
task fatJar(type: Jar) {
    manifest {
        attributes 'Main-Class': 'com.example.TodoApp'
    }
    archiveClassifier = 'all'
    from {
        configurations.runtimeClasspath.collect { it.isDirectory() ? it : zipTree(it) }
    }
    with jar
}
```

#### Gradleでのビルドコマンド

```bash
# 通常のビルド
gradle build

# Fat JARの作成
gradle fatJar

# 実行
java -jar build/libs/todo-app-1.0.0-all.jar
```

### Fat JARの詳細な作成方法

外部ライブラリを使用するアプリケーションの場合、すべての依存関係を1つのJARにまとめた「Fat JAR」を作成できます。

以下の例では、Gsonライブラリを使用したJSON処理アプリケーションを示します。このアプリケーションをFat JARとしてパッケージングすることで、Gsonライブラリも含めた自己完結型の実行可能JARを作成できます。

```java
// JsonProcessorApp.java
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;

public class JsonProcessorApp {
    
    static class Person {
        private String name;
        private int age;
        private LocalDateTime created;
        
        public Person(String name, int age) {
            this.name = name;
            this.age = age;
            this.created = LocalDateTime.now();
        }
        
        // Getters and Setters
        public String getName() { return name; }
        public int getAge() { return age; }
        public LocalDateTime getCreated() { return created; }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("JSON Processor");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());
            
            // 入力パネル
            JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 5));
            inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            inputPanel.add(new JLabel("Name:"));
            JTextField nameField = new JTextField();
            inputPanel.add(nameField);
            
            inputPanel.add(new JLabel("Age:"));
            JTextField ageField = new JTextField();
            inputPanel.add(ageField);
            
            JButton convertButton = new JButton("Convert to JSON");
            inputPanel.add(convertButton);
            inputPanel.add(new JLabel()); // 空のセル
            
            // 出力エリア
            JTextArea outputArea = new JTextArea(10, 40);
            outputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
            outputArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(outputArea);
            
            // ボタンのアクション
            Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();
                
            convertButton.addActionListener(e -> {
                try {
                    String name = nameField.getText();
                    int age = Integer.parseInt(ageField.getText());
                    
                    Person person = new Person(name, age);
                    String json = gson.toJson(person);
                    
                    outputArea.setText(json);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, 
                        "年齢は数値で入力してください", 
                        "入力エラー", 
                        JOptionPane.ERROR_MESSAGE);
                }
            });
            
            frame.add(inputPanel, BorderLayout.NORTH);
            frame.add(scrollPane, BorderLayout.CENTER);
            
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
```

#### Fat JARを手動で作成する方法

```bash
# 1. 作業ディレクトリの準備
mkdir temp
cd temp

# 2. 外部ライブラリ（gson-2.10.1.jar）を展開
jar xf ../lib/gson-2.10.1.jar

# 3. アプリケーションのクラスファイルをコピー
cp ../JsonProcessorApp*.class .

# 4. マニフェストファイルの作成
echo "Main-Class: JsonProcessorApp" > manifest.txt
echo "" >> manifest.txt

# 5. すべてを含むFat JARの作成
jar cvfm ../JsonProcessorApp-all.jar manifest.txt .

# 6. 一時ディレクトリの削除
cd ..
rm -rf temp
```

## jpackageの高度な活用

### プラットフォーム固有の詳細設定

#### Windows向けの設定（.msiインストーラ）

```bash
jpackage --type msi \
         --name "TodoManager" \
         --app-version "1.0.0" \
         --vendor "MyCompany Inc." \
         --description "Simple Todo Management Application" \
         --copyright "Copyright (c) 2024 MyCompany Inc." \
         --input ./input \
         --main-jar TodoApp.jar \
         --icon ./resources/app-icon.ico \
         --win-menu \
         --win-shortcut \
         --win-dir-chooser \
         --win-per-user-install \
         --dest ./output
```

追加オプションの説明：
- `--app-version`: アプリケーションのバージョン
- `--vendor`: ベンダー名（開発元）
- `--icon`: アプリケーションアイコン（.ico形式）
- `--win-menu`: スタートメニューにショートカットを作成
- `--win-shortcut`: デスクトップにショートカットを作成
- `--win-dir-chooser`: インストール時にディレクトリ選択を可能にする
- `--win-per-user-install`: ユーザーごとのインストール（管理者の権限不要）

#### macOS向けの設定（.dmgインストーラ）

```bash
jpackage --type dmg \
         --name "TodoManager" \
         --app-version "1.0.0" \
         --vendor "MyCompany Inc." \
         --description "Simple Todo Management Application" \
         --input ./input \
         --main-jar TodoApp.jar \
         --icon ./resources/app-icon.icns \
         --mac-package-name "com.mycompany.todomanager" \
         --mac-package-identifier "com.mycompany.todomanager" \
         --mac-sign \
         --mac-signing-key-user-name "Developer ID Application: MyCompany Inc." \
         --dest ./output
```

macOS固有オプション：
- `--icon`: macOS用アイコン（.icns形式）
- `--mac-package-identifier`: バンドル識別子
- `--mac-sign`: アプリケーションに署名（Gatekeeperに必要）
- `--mac-signing-key-user-name`: 署名に使用する開発者証明書

#### Linux向けの設定（.debパッケージ）

```bash
jpackage --type deb \
         --name "todomanager" \
         --app-version "1.0.0" \
         --vendor "MyCompany Inc." \
         --description "Simple Todo Management Application" \
         --input ./input \
         --main-jar TodoApp.jar \
         --icon ./resources/app-icon.png \
         --linux-menu-group "Office" \
         --linux-shortcut \
         --linux-deb-maintainer "dev@mycompany.com" \
         --linux-app-category "office" \
         --dest ./output
```

Linux固有オプション：
- `--linux-menu-group`: メニューカテゴリ
- `--linux-shortcut`: デスクトップショートカット作成
- `--linux-deb-maintainer`: パッケージメンテナー情報
- `--linux-app-category`: アプリケーションカテゴリ

### ランタイムオプションとJVMパラメータ

アプリケーションの実行時にJVMパラメータを設定することもできます。

```bash
jpackage --type app-image \
         --name "TodoManager" \
         --input ./input \
         --main-jar TodoApp.jar \
         --java-options "-Xmx512m" \
         --java-options "-Dfile.encoding=UTF-8" \
         --java-options "-Duser.language=ja" \
         --java-options "-Duser.country=JP" \
         --arguments "arg1" \
         --arguments "arg2" \
         --dest ./output
```

- `--java-options`: JVMオプション（複数指定可能）
- `--arguments`: アプリケーションへの引数

### ファイル関連付けの設定

特定のファイル拡張子をアプリケーションに関連付ける場合は、次のように設定します。

```bash
jpackage --type msi \
         --name "TodoManager" \
         --input ./input \
         --main-jar TodoApp.jar \
         --file-associations ./file-associations.properties \
         --dest ./output
```

file-associations.propertiesの内容：
```properties
extension=todo
mime-type=application/x-todo
description=Todo List File
icon=./resources/todo-file-icon.ico
```

### モジュラーアプリケーションのパッケージング

Java 9以降のモジュールシステムを使用している場合：

```bash
# jlinkでカスタムランタイムを作成
jlink --add-modules java.desktop,java.logging \
      --output custom-runtime

# jpackageでパッケージング
jpackage --type app-image \
         --name "ModularApp" \
         --runtime-image ./custom-runtime \
         --module com.example.app/com.example.app.Main \
         --dest ./output
```

## トラブルシューティングガイド

### Mavenビルドエラー

#### 依存関係の解決エラー

##### 問題
指定した依存関係が見つからない。

##### エラー例
```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>non-existent-library</artifactId>
    <version>1.0.0</version>
</dependency>
```

##### エラーメッセージ
```
[ERROR] Failed to execute goal on project myapp: Could not resolve dependencies for project com.example:myapp:jar:1.0.0: Could not find artifact com.example:non-existent-library:jar:1.0.0
```

##### 解決策
```bash
# 1. 依存関係の存在を確認
mvn help:describe -Dplugin=dependency -Ddetail

# 2. 正しいgroupId, artifactId, versionを確認
# Maven Central Repository で検索: https://search.maven.org/

# 3. 正しい依存関係を追加
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
    <version>3.12.0</version>
</dependency>
```

#### Javaバージョンの不一致

##### 問題
プロジェクトのJavaバージョンと実行環境のJavaバージョンが異なる。

##### エラー例
```xml
<properties>
    <maven.compiler.source>17</maven.compiler.source>
    <maven.compiler.target>17</maven.compiler.target>
</properties>
```

##### エラーメッセージ
```
[ERROR] Failed to execute goal org.apache.maven.plugins:maven-compiler-plugin:3.8.1:compile (default-compile) on project myapp: Fatal error compiling: invalid target release: 17
```

##### 解決策
```bash
# 1. 現在のJavaバージョンを確認
java -version
javac -version

# 2. 必要なJavaバージョンをインストール
# または環境変数JAVA_HOMEを設定

# 3. pom.xmlで対応するバージョンを指定
<properties>
    <maven.compiler.source>11</maven.compiler.source>
    <maven.compiler.target>11</maven.compiler.target>
</properties>
```

#### テストの失敗によるビルドエラー

##### 問題
テストが失敗してビルドが停止する。

##### エラーメッセージ
```
[ERROR] Failed to execute goal org.apache.maven.plugins:maven-surefire-plugin:2.22.2:test (default-test) on project myapp: There are test failures.
```

##### 解決策
```bash
# 1. 詳細なテスト結果を確認
mvn test

# 2. 特定のテストをスキップ
mvn clean package -DskipTests

# 3. テストを修正してから再ビルド
mvn clean test
mvn clean package
```

#### メモリ不足エラー

##### 問題
ビルド時にメモリ不足が発生する。

##### エラーメッセージ
```
[ERROR] Java heap space
[ERROR] The forked VM terminated without properly saying goodbye. VM crash or System.exit called?
```

##### 解決策
```bash
# 1. Maven実行時のメモリを増加
export MAVEN_OPTS="-Xmx2g -XX:MaxPermSize=256m"

# 2. プロジェクトごとの設定（.mvn/jvm.config）
echo "-Xmx2g -XX:MaxPermSize=256m" > .mvn/jvm.config

# 3. Surefireプラグインの設定
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>2.22.2</version>
    <configuration>
        <argLine>-Xmx1024m</argLine>
    </configuration>
</plugin>
```

### Gradleの設定問題

#### Gradle Wrapperの実行エラー

##### 問題
Gradle Wrapperが実行できない。

##### エラー例
```bash
./gradlew build
```

##### エラーメッセージ
```
Permission denied: ./gradlew
```

##### 解決策
```bash
# 1. 実行権限を付与
chmod +x gradlew

# 2. または直接Gradleを使用
gradle build

# 3. Gradle Wrapperの再生成
gradle wrapper
```

#### 依存関係の競合

##### 問題
複数のライブラリが同じクラスを提供している。

##### エラー例
```groovy
dependencies {
    implementation 'org.slf4j:slf4j-log4j12:1.7.30'
    implementation 'ch.qos.logback:logback-classic:1.2.3'
}
```

##### エラーメッセージ
```
SLF4J: Class path contains multiple SLF4J bindings.
```

##### 解決策
```groovy
dependencies {
    implementation('org.springframework:spring-core:5.3.0') {
        exclude group: 'commons-logging', module: 'commons-logging'
    }
    implementation 'ch.qos.logback:logback-classic:1.2.3'
}

// または依存関係レポートで確認
gradle dependencies --configuration runtimeClasspath
```

#### ビルドスクリプトの構文エラー

##### 問題
build.gradleの構文が正しくない。

##### エラー例
```groovy
plugins {
    id 'java'
    id 'application'
}

// mainClassNameは廃止予定
mainClassName = 'com.example.Main'
```

##### エラーメッセージ
```
The mainClassName property has been deprecated and is scheduled to be removed in Gradle 8.0.
```

##### 解決策
```groovy
plugins {
    id 'java'
    id 'application'
}

// 新しい構文を使用
application {
    mainClass = 'com.example.Main'
}
```

### JARファイルの実行エラー

#### Main-Classが見つからない

##### 問題
JARファイルにMain-Classが指定されていない。

##### エラー例
```bash
java -jar myapp.jar
```

##### エラーメッセージ
```
no main manifest attribute, in myapp.jar
```

##### 解決策
```xml
<!-- Maven -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-jar-plugin</artifactId>
    <version>3.2.2</version>
    <configuration>
        <archive>
            <manifest>
                <mainClass>com.example.Main</mainClass>
            </manifest>
        </archive>
    </configuration>
</plugin>
```

```groovy
// Gradle
jar {
    manifest {
        attributes 'Main-Class': 'com.example.Main'
    }
}
```

#### 依存関係が含まれていない

##### 問題
外部ライブラリがJARファイルに含まれていない。

##### エラーメッセージ
```
java.lang.ClassNotFoundException: org.apache.commons.lang3.StringUtils
```

##### 解決策
```xml
<!-- Maven: Fat JAR作成 -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-shade-plugin</artifactId>
    <version>3.4.1</version>
    <executions>
        <execution>
            <phase>package</phase>
            <goals>
                <goal>shade</goal>
            </goals>
            <configuration>
                <transformers>
                    <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                        <mainClass>com.example.Main</mainClass>
                    </transformer>
                </transformers>
            </configuration>
        </execution>
    </executions>
</plugin>
```

```groovy
// Gradle: Fat JAR作成
jar {
    manifest {
        attributes 'Main-Class': 'com.example.Main'
    }
    from {
        configurations.runtimeClasspath.collect { it.isDirectory() ? it : zipTree(it) }
    }
}
```

#### モジュールパスの問題（Java 9+）

##### 問題
モジュールシステムでのクラスパスの問題。

##### エラーメッセージ
```
java.lang.module.FindException: Module com.example not found
```

##### 解決策
```bash
# 1. モジュールパスを明示的に指定
java --module-path ./lib --module com.example/com.example.Main

# 2. またはクラスパスを使用
java -cp "lib/*:myapp.jar" com.example.Main

# 3. jlinkでカスタムランタイムを作成
jlink --module-path $JAVA_HOME/jmods:lib \
      --add-modules com.example \
      --output custom-runtime
```

### クラスパスの問題

#### クラスパスの設定ミス

##### 問題
実行時にクラスパスが正しく設定されていない。

##### エラー例
```bash
java -cp "lib/commons-lang3-3.12.0.jar" com.example.Main
```

##### エラーメッセージ
```
java.lang.ClassNotFoundException: com.example.Main
```

##### 解決策
```bash
# 1. 現在のディレクトリも含める
java -cp ".:lib/commons-lang3-3.12.0.jar" com.example.Main

# 2. すべてのJARファイルを含める
java -cp ".:lib/*" com.example.Main

# 3. Windows環境では区切り文字に注意
java -cp ".;lib/*" com.example.Main
```

#### 相対パスの問題

##### 問題
実行ディレクトリによってクラスパスが変わる。

##### エラー例
```bash
cd /different/directory
java -cp "lib/*" com.example.Main  # libディレクトリが見つからない
```

##### 解決策
```bash
# 1. 絶対パスを使用
java -cp "/path/to/app/lib/*:/path/to/app/classes" com.example.Main

# 2. スクリプトファイルを作成
#!/bin/bash
APP_HOME=$(dirname "$0")
java -cp "$APP_HOME/lib/*:$APP_HOME/classes" com.example.Main
```

### 環境依存の問題

#### 文字エンコーディングの問題

##### 問題
異なる環境で文字化けが発生する。

##### エラー例
```java
// 日本語が含まれるファイルを読み込み
Files.readAllLines(Paths.get("data.txt"))
```

##### 解決策
```java
// 文字エンコーディングを明示的に指定
Files.readAllLines(Paths.get("data.txt"), StandardCharsets.UTF_8)
```

```bash
# JVM起動時に文字エンコーディングを指定
java -Dfile.encoding=UTF-8 -jar myapp.jar
```

#### パスセパレータの問題

##### 問題
Windows/Linux/macOSでパスの区切り文字が異なる。

##### エラー例
```java
String path = "data/config/settings.txt";  // Windowsでは問題が発生する可能性
```

##### 解決策
```java
// プラットフォーム独立なパス操作
Path path = Paths.get("data", "config", "settings.txt");
String pathString = path.toString();

// またはFile.separatorを使用
String path = "data" + File.separator + "config" + File.separator + "settings.txt";
```

#### JVMバージョンの違い

##### 問題
開発環境と本番環境でJavaバージョンが異なる。

##### エラー例
```java
// Java 11で導入されたメソッド
String result = str.isBlank();  // Java 8では利用不可
```

##### 解決策
```java
// バージョン固有の機能を使用する前にチェック
if (System.getProperty("java.version").startsWith("11")) {
    // Java 11以降の処理
} else {
    // Java 8対応の処理
}

// またはMavenでターゲットバージョンを明示
<properties>
    <maven.compiler.source>8</maven.compiler.source>
    <maven.compiler.target>8</maven.compiler.target>
</properties>
```

### jpackageの問題

#### 必要なツールが不足

##### 問題
jpackageに必要なネイティブツールがインストールされていない。

##### エラーメッセージ
```
jpackage: error: Bundler "MSI" (msi) failed to produce a bundle.
```

##### 解決策
```bash
# Windows: WiX Toolsetをインストール
# https://wixtoolset.org/releases/

# macOS: Xcodeコマンドラインツールをインストール
xcode-select --install

# Linux: 必要なパッケージをインストール
sudo apt-get install fakeroot
```

#### モジュールパスの設定問題

##### 問題
モジュールパスが正しく設定されていない。

##### エラー例
```bash
jpackage --type app-image \
         --name "MyApp" \
         --module com.example.app/com.example.app.Main \
         --dest ./output
```

##### エラーメッセージ
```
jpackage: error: Module com.example.app not found
```

##### 解決策
```bash
# 1. モジュールパスを明示的に指定
jpackage --type app-image \
         --name "MyApp" \
         --module-path ./lib:./modules \
         --module com.example.app/com.example.app.Main \
         --dest ./output

# 2. またはJARファイルを使用
jpackage --type app-image \
         --name "MyApp" \
         --input ./lib \
         --main-jar myapp.jar \
         --main-class com.example.app.Main \
         --dest ./output
```

#### アプリケーションアイコンの問題

##### 問題
アプリケーションアイコンが正しく設定されない。

##### エラー例
```bash
jpackage --type app-image \
         --name "MyApp" \
         --icon ./icon.png \
         --main-jar myapp.jar \
         --dest ./output
```

##### 解決策
```bash
# 1. プラットフォーム固有の形式を使用
# Windows: .ico
# macOS: .icns
# Linux: .png

# 2. 各OSに対応したサイズのアイコンを使用
# Windows: 256x256 pixels
# macOS: 512x512 pixels
# Linux: 512x512 pixels

jpackage --type app-image \
         --name "MyApp" \
         --icon ./icon.ico \
         --main-jar myapp.jar \
         --dest ./output
```

### 一般的なデバッグ手法

#### 詳細なエラーログの取得

```bash
# Mavenの詳細ログ
mvn clean package -X

# Gradleの詳細ログ
./gradlew build --debug --stacktrace

# Java実行時の詳細ログ
java -verbose:class -jar myapp.jar
```

#### 依存関係の確認

```bash
# Maven依存関係ツリー
mvn dependency:tree

# Gradle依存関係
./gradlew dependencies

# JARファイルの内容確認
jar tf myapp.jar | head -20
```

#### 環境変数の確認

```bash
# Java環境の確認
echo $JAVA_HOME
java -version
javac -version

# クラスパスの確認
echo $CLASSPATH

# Maven設定の確認
mvn help:effective-pom
```

## まとめ

本付録では、実務で必要となる高度なビルドとデプロイメント技術について解説しました。ビルドツールの詳細な設定、プラットフォーム固有の最適化、そして実際の開発現場で遭遇する問題の解決方法を理解することで、より効率的で信頼性の高いアプリケーション配布が可能になります。

これらの知識は、プロフェッショナルな開発者として活動する上で重要な基盤となります。状況に応じて適切な技術を選択し、チームの生産性を向上させるために活用してください。