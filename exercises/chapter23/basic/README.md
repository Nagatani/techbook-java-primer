# 第23章 基礎課題 - ビルドとデプロイ

## 課題概要
ビルドツールの基本的な使い方と、Javaアプリケーションのパッケージングを学ぶ課題です。

## 課題リスト

### 課題1: Mavenプロジェクトの作成とビルド
シンプルなMavenプロジェクトを作成し、ビルドしてください。

**要件:**
- pom.xmlの作成
- 依存関係の追加（JUnit、Log4j2など）
- コンパイルとテストの実行
- JARファイルの生成

**pom.xmlの例:**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.example</groupId>
    <artifactId>my-app</artifactId>
    <version>1.0-SNAPSHOT</version>
    
    <properties>
        <maven.compiler.source>11</maven.compiler.source>
        <maven.compiler.target>11</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.13.2</version>
            <scope>test</scope>
        </dependency>
    </dependencies>
</project>
```

### 課題2: Gradleプロジェクトの作成とビルド
同じアプリケーションをGradleでビルドしてください。

**要件:**
- build.gradleの作成
- タスクの定義と実行
- 依存関係の管理
- カスタムタスクの作成

**build.gradleの例:**
```gradle
plugins {
    id 'java'
    id 'application'
}

group = 'com.example'
version = '1.0-SNAPSHOT'

repositories {
    mavenCentral()
}

dependencies {
    testImplementation 'junit:junit:4.13.2'
    implementation 'org.apache.logging.log4j:log4j-core:2.17.2'
}

application {
    mainClass = 'com.example.Main'
}

test {
    useJUnit()
}
```

### 課題3: 実行可能JARの作成
依存関係を含む実行可能なJARファイル（Fat JAR）を作成してください。

**Maven（maven-shade-plugin）:**
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-shade-plugin</artifactId>
    <version>3.2.4</version>
    <configuration>
        <transformers>
            <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                <mainClass>com.example.Main</mainClass>
            </transformer>
        </transformers>
    </configuration>
    <executions>
        <execution>
            <phase>package</phase>
            <goals>
                <goal>shade</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

### 課題4: 環境設定の管理
異なる環境（開発、テスト、本番）での設定管理を実装してください。

**要件:**
- プロパティファイルの使用
- 環境変数の活用
- プロファイル別の設定

**実装例:**
```java
public class ConfigManager {
    private static final String ENV = System.getenv("APP_ENV");
    private static Properties props = new Properties();
    
    static {
        String configFile = String.format("config-%s.properties", 
            ENV != null ? ENV : "dev");
        try (InputStream is = ConfigManager.class
            .getResourceAsStream("/" + configFile)) {
            props.load(is);
        } catch (IOException e) {
            throw new RuntimeException("設定ファイルの読み込みに失敗", e);
        }
    }
    
    public static String get(String key) {
        return props.getProperty(key);
    }
}
```

## 実行方法

### Maven
```bash
# プロジェクトの作成
mvn archetype:generate -DgroupId=com.example -DartifactId=my-app

# ビルド
mvn clean compile

# テスト実行
mvn test

# パッケージング
mvn package

# 実行
java -jar target/my-app-1.0-SNAPSHOT.jar
```

### Gradle
```bash
# プロジェクトの初期化
gradle init --type java-application

# ビルド
gradle build

# テスト実行
gradle test

# 実行
gradle run

# JARの生成
gradle jar
```

## 評価基準
- ビルド設定の正確性
- 依存関係の適切な管理
- 実行可能JARの動作確認
- 環境設定の柔軟性