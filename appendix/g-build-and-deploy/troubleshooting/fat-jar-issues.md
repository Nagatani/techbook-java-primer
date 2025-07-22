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
    <version>3.5.1</version>
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
    <version>3.6.0</version>
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

```groovy
task fatJar(type: Jar) {
    manifest {
        attributes 'Main-Class': 'com.example.Main'
    }
    archiveClassifier = 'all'
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from {
        configurations.runtimeClasspath.collect { it.isDirectory() ? it : zipTree(it) }
    }
    with jar
}

// または shadowプラグインを使用
plugins {
    id 'com.github.johnrengelman.shadow' version '8.1.1'
}

shadowJar {
    manifest {
        attributes 'Main-Class': 'com.example.Main'
    }
}
```

### 4. 手動での作成

```bash
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
task fatJar(type: Jar) {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    // または
    duplicatesStrategy = DuplicatesStrategy.WARN
}
```