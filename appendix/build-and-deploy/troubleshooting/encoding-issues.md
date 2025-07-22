# 文字エンコーディングの問題

## プラットフォーム間での文字化け対策

### 1. 問題の診断

```bash
# システムのデフォルトエンコーディングを確認
java -XshowSettings:properties -version 2>&1 | grep file.encoding

# ファイルのエンコーディングを確認（Linux/macOS）
file -i myfile.txt
```

### 2. Javaコードでの対策

#### ファイル読み書き時の明示的な指定

```java
// 推奨: エンコーディングを明示的に指定
Files.readAllLines(Paths.get("data.txt"), StandardCharsets.UTF_8);
Files.write(Paths.get("output.txt"), lines, StandardCharsets.UTF_8);

// 避けるべき: プラットフォームのデフォルトに依存
Files.readAllLines(Paths.get("data.txt")); // デフォルトエンコーディング
```

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
tasks.withType(JavaCompile) {
    options.encoding = 'UTF-8'
}

tasks.withType(Javadoc) {
    options.encoding = 'UTF-8'
}

test {
    systemProperty "file.encoding", "UTF-8"
}
```

### 4. 実行時の設定

```bash
# JVMオプションでエンコーディングを指定
java -Dfile.encoding=UTF-8 -jar myapp.jar

# 環境変数での設定
export JAVA_TOOL_OPTIONS="-Dfile.encoding=UTF-8"
```

### 5. IDEでの設定

#### IntelliJ IDEA

1. Settings → Editor → File Encodings
2. Global Encoding: UTF-8
3. Project Encoding: UTF-8
4. Default encoding for properties files: UTF-8

#### Eclipse

1. Window → Preferences → General → Workspace
2. Text file encoding: UTF-8