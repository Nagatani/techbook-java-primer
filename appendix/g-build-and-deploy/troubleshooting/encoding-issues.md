# 文字エンコーディングの問題

## プラットフォーム間での文字化け対策

Java 18以降（JEP 400）は、ファイルの読み書きなどで使う既定の文字コード（`file.encoding`）が、OSにかかわらずUTF-8です。
Java 25では、ファイルの中身の文字化けは「UTF-8以外で保存されたファイルを読む」場合に起きます。
一方、コンソールへの出力や入力の文字コード（`stdout.encoding`、`stdin.encoding`）は、OSやターミナルの設定（Windowsのコードページなど）に従います。
そのため、コンソールでの文字化けはJava 25でも起こり得ます。

### 1. 問題の診断

```bash
# 文字コードにかかわる設定を確認
# file.encoding: ファイルなどの既定（Java 18以降はUTF-8）
# native.encoding: OSの設定から決まる文字コード
# stdout.encoding: コンソール出力の文字コード
java -XshowSettings:properties -version 2>&1 | grep encoding

# ファイルのエンコーディングを確認（Linux/macOS）
file -i myfile.txt
```

### 2. Javaコードでの対策

#### ファイル読み書き時の明示的な指定

```java
// 推奨: エンコーディングを明示的に指定
Files.readAllLines(Path.of("data.txt"), StandardCharsets.UTF_8);
Files.write(Path.of("output.txt"), lines, StandardCharsets.UTF_8);

// Shift_JISで保存されたファイルは、その文字コードを指定して読む
Files.readAllLines(Path.of("sjis.txt"), Charset.forName("Shift_JIS"));

// 注意: 文字コードを指定しないFileReaderやnew String(bytes)は既定の文字コードを使う。
// Java 17以前ではOSによって結果が変わった（Windowsの日本語環境ではMS932）
new FileReader("data.txt");
```

`Files.readAllLines(path)`のように文字コードを指定しない`Files`のメソッドは、Java 7で登場したときから常にUTF-8を使います。

#### Properties ファイルの扱い

```java
// UTF-8でPropertiesファイルを読み込む
try (InputStreamReader reader = new InputStreamReader(
        new FileInputStream("config.properties"), StandardCharsets.UTF_8)) {
    Properties props = new Properties();
    props.load(reader);
}
```

### 3. ビルドツールでの設定

#### Maven

```xml
<properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
</properties>

<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
        <encoding>UTF-8</encoding>
    </configuration>
</plugin>
```

#### Gradle

```groovy
tasks.withType(JavaCompile).configureEach {
    options.encoding = 'UTF-8'
}

tasks.withType(Javadoc).configureEach {
    options.encoding = 'UTF-8'
}
```

javacも、Java 18以降はソースファイルを既定でUTF-8として読みます。それでも、古いJDKでビルドしたときにも同じ結果になるよう、ソースファイルの文字コード（`encoding`）は明示しておきます。
テストを実行するJVMの`file.encoding`は、Java 18以降は既定でUTF-8なので指定する必要はありません。

### 4. 実行時の設定

Java 18以降は`-Dfile.encoding=UTF-8`を付けなくてもUTF-8です。
コンソールでの文字化けは、ターミナル側の文字コードをUTF-8にして直します。

```bash
# macOS/Linux: ロケールをUTF-8にする
export LANG=ja_JP.UTF-8
java -jar myapp.jar
```

```powershell
# Windows: コンソールのコードページをUTF-8（65001）にする
chcp 65001
java -jar myapp.jar
```

Java 17以前のJVMで動かす必要がある場合だけ、`java -Dfile.encoding=UTF-8 -jar myapp.jar`のように指定します。

### 5. IDEでの設定

#### IntelliJ IDEA

1. Settings → Editor → File Encodings
2. Global Encoding: UTF-8
3. Project Encoding: UTF-8
4. Default encoding for properties files: UTF-8

#### Eclipse

1. Window → Preferences → General → Workspace
2. Text file encoding: UTF-8