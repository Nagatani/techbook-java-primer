# マニフェスト設定の確認

## Main-Classが見つからない問題の解決

### 1. JARファイル内のマニフェストを確認

```bash
# マニフェストの内容を表示
jar xf myapp.jar META-INF/MANIFEST.MF
cat META-INF/MANIFEST.MF

# JARファイルの内容を確認
jar tf myapp.jar | grep Main
```

### 2. 正しいマニフェストファイルの作成

#### manifest.txtの例

```
Manifest-Version: 1.0
Main-Class: com.example.Main
Class-Path: lib/gson-2.15.0.jar lib/commons-lang3-3.22.0.jar

```

**重要**: 
- 各行の末尾にスペースがないことを確認する
- ファイルの最後の行も改行で終える（改行のない最後の行は読み飛ばされる）
- `Main-Class:`のコロンの後にはスペースが1つ必要

### 3. jarコマンドでの作成

```bash
# マニフェストを指定してJAR作成
jar cvfm myapp.jar manifest.txt com/example/*.class

# 既存JARのマニフェストを更新
jar uvfm myapp.jar manifest.txt
```

### 4. ビルドツールでの設定

#### Maven

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-jar-plugin</artifactId>
    <version>3.5.1</version>
    <configuration>
        <archive>
            <manifest>
                <addClasspath>true</addClasspath>
                <classpathPrefix>lib/</classpathPrefix>
                <mainClass>com.example.Main</mainClass>
            </manifest>
            <manifestEntries>
                <Built-By>${user.name}</Built-By>
                <Implementation-Version>${project.version}</Implementation-Version>
            </manifestEntries>
        </archive>
    </configuration>
</plugin>
```

#### Gradle

`Class-Path`は`provider`で包み、JARを作るときに初めて依存ライブラリの一覧を求めるようにします（設定の段階で毎回依存を解決しないため）。
`Class-Path`に書いた`lib/`の下には、依存ライブラリを別にコピーしておく必要があります。

```groovy
jar {
    def runtimeClasspath = configurations.runtimeClasspath
    manifest {
        attributes(
            'Main-Class': 'com.example.Main',
            'Class-Path': provider { runtimeClasspath.files.collect { "lib/$it.name" }.join(' ') },
            'Implementation-Title': 'My Application',
            'Implementation-Version': version
        )
    }
}
```

依存ライブラリを`build/libs/lib`にコピーする例は[sample-projects/gradle-project/build.gradle](../sample-projects/gradle-project/build.gradle)の`copyLibs`タスクにあります。