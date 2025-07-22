# Mavenメモリ設定

## ビルド時のメモリ不足への対処

### 1. Maven実行時のメモリ設定

#### 環境変数での設定

```bash
# Linux/macOS
export MAVEN_OPTS="-Xmx2g -XX:MaxPermSize=256m"

# Windows
set MAVEN_OPTS=-Xmx2g -XX:MaxPermSize=256m
```

### 2. プロジェクトごとの設定

`.mvn/jvm.config`ファイルを作成：

```
-Xmx2g
-XX:MaxPermSize=256m
-XX:+UseG1GC
```

### 3. プラグインごとのメモリ設定

#### Surefireプラグイン（テスト実行）

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>2.22.2</version>
    <configuration>
        <argLine>-Xmx1024m -XX:MaxPermSize=256m</argLine>
        <forkCount>1</forkCount>
        <reuseForks>false</reuseForks>
    </configuration>
</plugin>
```

#### Compilerプラグイン

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.11.0</version>
    <configuration>
        <fork>true</fork>
        <meminitial>512m</meminitial>
        <maxmem>2048m</maxmem>
    </configuration>
</plugin>
```

### 4. メモリ使用状況の確認

```bash
# ビルド中のメモリ使用状況を監視
jconsole

# ヒープダンプの取得
jmap -dump:format=b,file=heap.bin <pid>
```

### 5. メモリ効率の改善

```xml
<!-- 並列ビルドの無効化 -->
<properties>
    <maven.build.threads>1</maven.build.threads>
</properties>
```