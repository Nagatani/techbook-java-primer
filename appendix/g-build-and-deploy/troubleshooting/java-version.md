# Javaバージョンの設定

## バージョン不一致の解決方法

本書のサンプルはJava 25（OpenJDK 25）を対象にしています。
「無効なターゲット・リリース」や`UnsupportedClassVersionError`が出たら、プロジェクトが求める版と、実際に使われているJDKの版を比べます。

### 1. 現在のJavaバージョンを確認

```bash
# Javaランタイムのバージョン
java -version

# Javaコンパイラーのバージョン
javac -version

# JAVA_HOME環境変数
echo $JAVA_HOME

# Mavenが使っているJDK（「Java version」の行）
mvn -v

# Gradleが使っているJDK（「Launcher JVM」「Daemon JVM」の行）
./gradlew --version
```

### 2. Mavenで使用するJavaバージョンの設定

#### pom.xmlでの設定

`maven.compiler.release`を指定します。
`source`と`target`だけを指定する書き方は、新しいAPIを使ってもコンパイルが通ってしまい、古いJavaで実行したときに初めて失敗するため使いません（`release`は、その版にあるAPIだけでコンパイルします）。

```xml
<properties>
    <maven.compiler.release>25</maven.compiler.release>
</properties>
```

#### 特定のJDKを使用する場合（Toolchains）

Mavenを動かしているJDKとは別のJDKでコンパイルしたい場合は、Toolchainsを使います。
まず`~/.m2/toolchains.xml`に、手元のJDKの場所を登録します（パスは例です）。

```xml
<toolchains>
    <toolchain>
        <type>jdk</type>
        <provides>
            <version>25</version>
        </provides>
        <configuration>
            <jdkHome>/path/to/jdk-25</jdkHome>
        </configuration>
    </toolchain>
</toolchains>
```

pom.xmlでは、使うJDKの版を指定します。

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-toolchains-plugin</artifactId>
            <version>3.3.0</version>
            <executions>
                <execution>
                    <goals>
                        <goal>toolchain</goal>
                    </goals>
                </execution>
            </executions>
            <configuration>
                <toolchains>
                    <jdk>
                        <version>25</version>
                    </jdk>
                </toolchains>
            </configuration>
        </plugin>
    </plugins>
</build>
```

### 3. Gradleで使用するJavaバージョンの設定

Gradleではツールチェーンで指定します。
Gradleを動かしているJDKと違う版を指定すると、手元にインストールされたJDKから合うものを探して使います。

```groovy
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}
```

### 4. 複数のJavaバージョンの管理

#### SDKMAN!を使用（macOS/Linux）

```bash
# SDKMAN!のインストール
curl -s "https://get.sdkman.io" | bash

# 利用可能なJavaバージョンの確認
sdk list java

# 特定バージョンのインストール（識別子は2026-11時点の例）
sdk install java 25.0.5.0+2-ms

# このターミナルだけで使う
sdk use java 25.0.5.0+2-ms
```

SDKMAN!の識別子は、JDKの更新のたびに変わります。
インストールするときは、`sdk list java`の表示で実際の識別子を確かめてください。

#### jenvを使用（macOS/Linux）

jenvは、インストール済みのJDKを切り替えるツールです（JDKそのものはインストールしません）。

```bash
# jenvのインストール（macOSでHomebrewを使う場合）
brew install jenv

# インストール済みのJDKを登録（パスは例）
jenv add /Library/Java/JavaVirtualMachines/jdk-25.jdk/Contents/Home

# 登録した名前の確認
jenv versions

# このディレクトリで使うバージョンの設定
jenv local 25
```
