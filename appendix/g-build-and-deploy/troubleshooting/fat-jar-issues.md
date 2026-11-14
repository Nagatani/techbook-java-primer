# Fat JARの作成方法

## 依存関係を含むJARファイルの作成

### 1. Fat JARが必要な理由

通常のJARファイルには、アプリケーションのクラスファイルのみが含まれ、外部ライブラリは含まれません。Fat JAR（またはUber JAR）は、すべての依存関係を1つのJARファイルにまとめたものです。

### 2. Mavenでの作成方法

#### maven-shade-plugin

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-shade-plugin</artifactId>
    <version>3.6.2</version>
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
                    <!-- サービスファイルのマージ -->
                    <transformer implementation="org.apache.maven.plugins.shade.resource.ServicesResourceTransformer"/>
                </transformers>
                <filters>
                    <filter>
                        <artifact>*:*</artifact>
                        <excludes>
                            <exclude>META-INF/*.SF</exclude>
                            <exclude>META-INF/*.DSA</exclude>
                            <exclude>META-INF/*.RSA</exclude>
                        </excludes>
                    </filter>
                </filters>
            </configuration>
        </execution>
    </executions>
</plugin>
```

#### maven-assembly-plugin

```xml
<plugin>
    <artifactId>maven-assembly-plugin</artifactId>
    <version>3.8.0</version>
    <configuration>
        <archive>
            <manifest>
                <mainClass>com.example.Main</mainClass>
            </manifest>
        </archive>
        <descriptorRefs>
            <descriptorRef>jar-with-dependencies</descriptorRef>
        </descriptorRefs>
    </configuration>
    <executions>
        <execution>
            <id>make-assembly</id>
            <phase>package</phase>
            <goals>
                <goal>single</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

### 3. Gradleでの作成方法

#### Jarタスクで作る

```groovy
tasks.register('fatJar', Jar) {
    manifest {
        attributes 'Main-Class': 'com.example.Main'
    }
    archiveClassifier = 'all'
    // ライブラリどうしで重なるファイル（module-info.classなど）は最初の1つだけを入れる
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from {
        configurations.runtimeClasspath.collect { it.isDirectory() ? it : zipTree(it) }
    }
    with jar
}
```

#### Shadowプラグインで作る

Shadowプラグインを使う場合は、`build.gradle`の先頭の`plugins`ブロックに書きます（`plugins`ブロックはほかの記述より前に置く必要があります）。
Gradle 9では、旧来の`com.github.johnrengelman.shadow`は動きません。後継の`com.gradleup.shadow`を使います。
`./gradlew shadowJar`で`build/libs/プロジェクト名-版-all.jar`ができます。

```groovy
plugins {
    id 'java'
    id 'application'
    id 'com.gradleup.shadow' version '9.6.1'
}

application {
    mainClass = 'com.example.Main'
}
```

`application`プラグインの`mainClass`が、Fat JARのマニフェストの`Main-Class`になります。
動かせる例は[sample-projects/gradle-project](../sample-projects/gradle-project/)にあります。

### 4. 手動での作成

`lib/`に依存JAR、`target/classes/`にアプリケーションのクラス、`manifest.txt`にマニフェストがあるディレクトリで実行します。
複数のJARに同じ名前のファイルがあると、後から展開したもので上書きされます。
`META-INF/services/`の下のファイル（ServiceLoaderの登録）が重なる場合は、上書きされると動かなくなるため、ビルドツール（maven-shade-pluginの`ServicesResourceTransformer`など）で結合してください。

```bash
# 途中で失敗したら止める
set -eu

# 作業ディレクトリを作成
mkdir temp
cd temp

# 依存JARを展開
for jar in ../lib/*.jar; do
    jar xf "$jar"
done

# アプリケーションのクラスをコピー
cp -r ../target/classes/* .

# META-INFの競合を解決
rm -rf META-INF/*.SF META-INF/*.DSA META-INF/*.RSA

# Fat JARを作成
jar cvfm ../app-all.jar ../manifest.txt .

# クリーンアップ
cd ..
rm -rf temp
```

### 5. 一般的な問題と解決策

#### 署名ファイルの競合

```xml
<configuration>
    <filters>
        <filter>
            <artifact>*:*</artifact>
            <excludes>
                <exclude>META-INF/*.SF</exclude>
                <exclude>META-INF/*.DSA</exclude>
                <exclude>META-INF/*.RSA</exclude>
            </excludes>
        </filter>
    </filters>
</configuration>
```

#### リソースファイルの重複

```groovy
tasks.named('fatJar', Jar) {
    // 重複したファイルは最初の1つだけを入れる
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    // または、警告を出したうえで最初の1つだけを入れる
    // duplicatesStrategy = DuplicatesStrategy.WARN
}
```