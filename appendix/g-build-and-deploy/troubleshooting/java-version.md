# Javaバージョンの設定

## バージョン不一致の解決方法

### 1. 現在のJavaバージョンを確認

```bash
# Javaランタイムのバージョン
java -version

# Javaコンパイラのバージョン
javac -version

# JAVA_HOME環境変数
echo $JAVA_HOME
```

### 2. Mavenで使用するJavaバージョンの設定

#### pom.xmlでの設定

```xml
<properties>
    <maven.compiler.source>11</maven.compiler.source>
    <maven.compiler.target>11</maven.compiler.target>
</properties>
```

#### 特定のJDKを使用する場合

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.11.0</version>
            <configuration>
                <source>11</source>
                <target>11</target>
                <fork>true</fork>
                <executable>${JAVA_11_HOME}/bin/javac</executable>
            </configuration>
        </plugin>
    </plugins>
</build>
```

### 3. 複数のJavaバージョンの管理

#### jenvを使用（macOS/Linux）

```bash
# jenvのインストール
brew install jenv

# JDKの追加
jenv add /Library/Java/JavaVirtualMachines/jdk-11.jdk/Contents/Home
jenv add /Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home

# プロジェクトで使用するバージョンの設定
jenv local 11
```

#### SDKMAN!を使用

```bash
# SDKMAN!のインストール
curl -s "https://get.sdkman.io" | bash

# 利用可能なJavaバージョンの確認
sdk list java

# 特定バージョンのインストール
sdk install java 11.0.12-open

# プロジェクトで使用
sdk use java 11.0.12-open
```