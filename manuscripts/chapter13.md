# 第13章 ライブラリの活用

## 始めに：ソフトウェア再利用の進化と依存関係管理の重要性

前章までJavaの基本的な技術を学習してきました。本章では、現代のソフトウェア開発において不可欠な「外部ライブラリの活用」と「依存関係管理」について詳細に学習していきます。

ライブラリの活用は、単なる便利な機能の追加ではありません。これは、ソフトウェア開発における「車輪の再発明」を避け、高品質で信頼性の高いコンポーネントを効率的に組み合わせて、より価値の高いアプリケーションを構築するための、現代的なソフトウェア工学の核心的な手法です。

### ソフトウェア再利用の歴史的発展

ソフトウェア開発における再利用の概念は、コンピュータサイエンスの黎明期から重要な課題として認識されてきました。この発展過程を理解することは、現代のライブラリ活用技術の意義を深く理解するために重要です。

**サブルーチンの概念（1940年代〜1950年代）**：プログラムの一部を切り出して再利用可能にするサブルーチン（関数）の概念が確立されました。これにより、同じ処理を複数回記述する必要がなくなりました。

**ライブラリの誕生（1950年代〜1960年代）**：よく使用される数学関数や入出力処理をまとめた「ライブラリ」が開発されました。FORTRANの数学ライブラリ、COBOLの事務処理ライブラリなどが代表例です。

**UNIX 哲学の確立（1970年代）**：「小さなツールを組み合わせて大きな問題を解決する」というUNIX哲学により、コマンドラインツールの組み合わせによる柔軟なシステム構築が可能になりました。

```bash
# UNIXツールの組み合わせ例
cat logfile.txt | grep "ERROR" | wc -l
find . -name "*.java" | xargs grep "TODO" | sort | uniq
```

**オブジェクト指向とクラスライブラリ（1980年代〜1990年代）**：オブジェクト指向プログラミングの普及により、再利用可能なクラスライブラリが発展しました。C++のSTL（Standard Template Library）、JavaのJDK標準ライブラリなどが重要な例です。

### 従来のライブラリ管理の課題

プログラミング言語の発展とともに、ライブラリの管理は複雑化し、深刻な問題を引き起こすようになりました：

**C言語における手動管理の困難さ**：

```c
// ヘッダファイルの手動include
#include <stdio.h>
#include <math.h>
#include <sqlite3.h>
#include <curl/curl.h>

// コンパイル時の手動リンク
// gcc program.c -lm -lsqlite3 -lcurl
```

このアプローチには以下の問題がありました：

**依存関係の複雑化**：ライブラリAがライブラリBに依存し、ライブラリBがライブラリCに依存するような複雑な依存関係を手動で管理することは極めて困難でした。

**バージョン管理の困難さ**：異なるプロジェクトが同じライブラリの異なるバージョンを必要とする場合、システム全体での整合性を保つことが困難でした。

**プラットフォーム依存性**：Windows、Linux、macOSなど、異なるプラットフォームでのライブラリのインストール方法や設定が大きく異なりました。

**配布とデプロイの複雑性**：アプリケーションとともに必要なライブラリを配布し、ターゲット環境に正しくインストールすることは、高度な専門知識を要求しました。

### パッケージ管理システムの革命

これらの問題を解決するため、さまざまなプログラミング言語で「パッケージ管理システム」が開発されました：

**Perl CPAN（1995年）**：Comprehensive Perl Archive Networkとして、Perlライブラリの集中管理と自動インストール機能を提供しました。依存関係の自動解決という革新的な概念を実現しました。

**Python pip（2008年）**：Python Package Indexと連携し、Pythonライブラリの簡単なインストールと管理を可能にしました。仮想環境（virtualenv）との組み合わせにより、プロジェクト固有の依存関係管理も実現しました。

**Node.js npm（2010年）**：Node Package Managerとして、JavaScriptライブラリの管理に革命をもたらしました。ローカルインストール、セマンティックバージョニング、依存関係ツリーの詳細な管理などの先進的な機能を提供しました。

**Ruby gem（2004年）**：RubyGemsとして、Rubyライブラリの管理を統一し、Bundlerと組み合わせることで、プロジェクト単位での依存関係管理を実現しました。

### Javaエコシステムにおける依存関係管理の発展

Javaにおける依存関係管理は、言語の特性を活かした独自の発展を遂げました：

**JAR（Java Archive）の標準化（1996年）**：Javaクラスファイルとリソースをまとめたアーカイブ形式により、ライブラリの配布と使用が標準化されました。

**Ant（2000年）**：Apache Antプロジェクトにより、XMLベースのビルドシステムが確立されました。しかし、依存関係管理は依然として手動でした。

**Maven（2004年）**：Project Object Model（POM）という概念により、プロジェクトの構造、依存関係、ビルドプロセスを統一的に管理する革新的なシステムが誕生しました。中央リポジトリの概念により、世界中のJavaライブラリへのアクセスが容易になりました。

**Gradle（2008年）**：GroovyベースのDSL（Domain Specific Language）により、Mavenの柔軟性を向上させながら、より簡潔で表現力豊かなビルド設定を実現しました。

### Mavenアーキテクチャの革新性

MavenはJavaエコシステムに以下の革新をもたらしました：

**座標系による一意識別**：groupId、artifactId、versionの組み合わせにより、世界中のJavaライブラリを一意に識別できるシステムを確立しました。

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
    <version>3.12.0</version>
</dependency>
```

**推移的依存関係の自動解決**：ライブラリが依存するほかのライブラリも自動的にダウンロードし、管理するしくみにより、複雑な依存関係を透明に処理できるようになりました。

**標準的なプロジェクト構造**：src/main/java、src/test/javaなどの標準的なディレクトリ構造により、異なるプロジェクト間での一貫性が確保されました。

**ライフサイクルとフェーズ**：compile、test、package、install、deployなどの標準的なビルドライフサイクルにより、一貫したビルドプロセスが実現されました。

### 中央リポジトリとオープンソースエコシステム

Maven Central Repositoryの確立は、Javaオープンソースエコシステムの爆発的成長を促進しました：

**民主化されたライブラリ公開**：個人開発者から大企業まで、誰でも簡単にライブラリを公開し、世界中の開発者と共有できるようになりました。

**品質の向上**：多くの開発者による使用とフィードバックにより、ライブラリの品質が継続的に向上するサイクルが確立されました。

**イノベーションの加速**：既存のライブラリを組み合わせることで、新しいアイデアの実装に集中でき、イノベーションのスピードが大幅に向上しました。

**企業での採用促進**：信頼性の高いオープンソースライブラリの活用により、企業でのJava採用が促進され、エコシステムの良好なサイクルが形成されました。

### 現代の依存関係管理における課題

現代のソフトウェア開発では、ライブラリ活用の普及に伴い、新しい課題も生まれています：

**セキュリティ脆弱性の管理**：使用するライブラリに脆弱性が発見された場合の迅速な対応と、継続的なセキュリティ監視が重要になっています。

**ライセンス管理**：異なるライセンスを持つライブラリの組み合わせによる法的リスクの管理が必要です。

**依存関係の肥大化**：多数のライブラリを使用することで、アプリケーションサイズの増大や起動時間の延長などの問題が発生することがあります。

**バージョンコンフリクト**：異なるライブラリが同じ依存関係の異なるバージョンを要求する場合の競合解決が複雑化しています。

### 本章で学習する内容の意義

本章では、これらの歴史的背景と現代的な課題を踏まえて、Javaにおけるライブラリ活用と依存関係管理を体系的に学習していきます。単にツールの使い方を覚えるのではなく、以下の点を重視して学習を進めます：

**適切なライブラリ選択**：プロジェクトの要件に適したライブラリを評価し、選択する判断力を身につけます。

**効率的な依存関係管理**：Maven、Gradleなどのビルドツールを効果的に活用し、保守可能なプロジェクト構造を構築する技術を習得します。

**セキュリティとライセンスの考慮**：ライブラリ使用時の法的・技術的リスクを理解し、適切な管理手法を身につけます。

**現代的な開発フロー**：継続的インテグレーション、自動テスト、依存関係の自動更新など、現代的な開発プロセスとの統合技術を理解します。

**エコシステムの理解**：オープンソースコミュニティのしくみと、持続可能な開発エコシステムについての理解を深めます。

ライブラリの活用と依存関係管理を深く理解することは、現代的なソフトウェア開発における基本的なスキルを身につけることにつながります。本章を通じて、単なる「便利なツールの使い方」を超えて、「持続可能で効率的なソフトウェア開発エコシステムの設計思想」を習得していきましょう。

本章では、外部ライブラリの使用方法を学習します。C言語では手動でライブラリをリンクしていましたが、Javaではより簡単にライブラリを活用できます。

## 13.1 ライブラリとは

### ライブラリ管理の進化：C言語からJavaへ

**C言語での従来的なライブラリ管理：**

```c
// C言語では手動でヘッダファイルをincludeし、リンクする
#include <math.h>
#include <sqlite3.h>
#include <curl/curl.h>

int main() {
    // 数学ライブラリ（libm）の使用
    double result = sqrt(16.0);
    
    // SQLiteライブラリの使用
    sqlite3 *db;
    sqlite3_open("test.db", &db);
    
    // CURLライブラリの使用  
    CURL *curl = curl_easy_init();
    
    return 0;
}

// コンパイル時に複数のライブラリを手動でリンク
// gcc program.c -lm -lsqlite3 -lcurl
// 
// 問題点：
// 1. 依存関係の手動管理が必要
// 2. プラットフォーム依存のライブラリパス
// 3. バージョン管理が困難
// 4. 複雑な依存関係ツリーの解決が不可能
```

**Javaでの現代的なライブラリ管理：**

```java
// Maven/Gradleによる自動依存関係管理
// pom.xmlまたはbuild.gradleで宣言するだけ

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.httpcomponents.client5.http.classic.HttpClient;
import org.apache.httpcomponents.client5.http.impl.classic.HttpClients;
import java.sql.DriverManager;
import java.sql.Connection;

/**
 * ライブラリ活用の包括的デモンストレーション
 * 現代的な依存関係管理とライブラリエコシステムの実践例
 */
public class LibraryDemonstration {
    
    public static void main(String[] args) {
        // JSON処理（Jackson）
        ObjectMapper mapper = new ObjectMapper();
        
        // HTTP通信（Apache HttpClient）
        HttpClient httpClient = HttpClients.createDefault();
        
        // データベース接続（JDBC - 標準ライブラリ）
        try {
            Connection connection = DriverManager.getConnection("jdbc:h2:mem:test");
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        System.out.println("すべてのライブラリが正常に読み込まれました");
    }
}

// 利点：
// 1. 依存関係の自動解決
// 2. プラットフォーム独立
// 3. バージョン管理の自動化
// 4. 推移的依存関係の透明な処理
```

**Javaでのライブラリ管理手法：**

1. **Maven**: XMLベースの宣言的依存関係管理
   - 中央リポジトリからの自動ダウンロード
   - 推移的依存関係の解決
   - 標準的なプロジェクト構造

2. **Gradle**: Groovy/Kotlinベースの柔軟なビルドツール
   - プログラマブルなビルドスクリプト
   - 高速なインクリメンタルビルド
   - マルチプロジェクト対応

3. **手動管理**: 直接的なJARファイル追加（非推奨）
   - 学習用途や小規模プロジェクト
   - 依存関係管理の複雑性
   - 保守性の問題

## 13.2 Maven入門

### プロジェクト構造

```
my-java-project/
├── pom.xml
└── src/
    ├── main/
    │   └── java/
    │       └── com/
    │           └── example/
    │               └── App.java
    └── test/
        └── java/
            └── com/
                └── example/
                    └── AppTest.java
```

### pom.xml の基本構成

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.example</groupId>
    <artifactId>my-java-project</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>
    
    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>
    
    <dependencies>
        <!-- JSON処理ライブラリ -->
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
            <version>2.15.2</version>
        </dependency>
        
        <!-- HTTP クライアント -->
        <dependency>
            <groupId>org.apache.httpcomponents.client5</groupId>
            <artifactId>httpclient5</artifactId>
            <version>5.2.1</version>
        </dependency>
        
        <!-- テストフレームワーク -->
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <version>5.9.3</version>
            <scope>test</scope>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
            </plugin>
        </plugins>
    </build>
</project>
```

### 実践的なライブラリ活用例

以下は、実際のプロジェクトでよく使用される外部ライブラリを活用した実用的な例です：

```java
package com.example.libraryDemo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.httpcomponents.client5.http.classic.methods.HttpGet;
import org.apache.httpcomponents.client5.http.impl.classic.CloseableHttpClient;
import org.apache.httpcomponents.client5.http.impl.classic.HttpClients;
import org.apache.httpcomponents.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 実用的なライブラリ活用デモンストレーション
 * JSON処理、HTTP通信、文字列操作、ログ出力の統合例
 */
public class PracticalLibraryDemo {
    
    private static final Logger logger = LoggerFactory.getLogger(PracticalLibraryDemo.class);
    private final ObjectMapper objectMapper;
    private final CloseableHttpClient httpClient;
    
    public PracticalLibraryDemo() {
        this.objectMapper = new ObjectMapper();
        this.httpClient = HttpClients.createDefault();
    }
    
    // 1. JSON処理のデモンストレーション (Jackson)
    public static void demonstrateJsonProcessing() {
        System.out.println("=== JSON処理デモンストレーション (Jackson) ===");
        
        ObjectMapper mapper = new ObjectMapper();
        
        try {
            // オブジェクトからJSONへの変換
            Person person = new Person("田中太郎", 30, "engineer");
            String jsonString = mapper.writeValueAsString(person);
            System.out.println("JSON文字列: " + jsonString);
            
            // JSONからオブジェクトへの変換
            Person parsedPerson = mapper.readValue(jsonString, Person.class);
            System.out.println("復元されたオブジェクト: " + parsedPerson);
            
            // 複雑なJSON構造の解析
            String complexJson = """
                {
                    "users": [
                        {"name": "Alice", "age": 25, "skills": ["Java", "Python"]},
                        {"name": "Bob", "age": 30, "skills": ["JavaScript", "React"]}
                    ],
                    "total": 2,
                    "timestamp": "2024-01-15T10:30:00"
                }
                """;
            
            JsonNode rootNode = mapper.readTree(complexJson);
            JsonNode usersArray = rootNode.get("users");
            
            System.out.println("ユーザー一覧:");
            for (JsonNode userNode : usersArray) {
                String name = userNode.get("name").asText();
                int age = userNode.get("age").asInt();
                JsonNode skillsNode = userNode.get("skills");
                
                List<String> skills = new ArrayList<>();
                for (JsonNode skillNode : skillsNode) {
                    skills.add(skillNode.asText());
                }
                
                System.out.printf("  %s (%d歳): %s%n", name, age, skills);
            }
            
        } catch (IOException e) {
            logger.error("JSON処理エラー", e);
        }
    }
    
    // 2. HTTP通信のデモンストレーション (Apache HttpClient)
    public void demonstrateHttpClient() {
        System.out.println("\n=== HTTP通信デモンストレーション (Apache HttpClient) ===");
        
        // 公開APIからデータを取得
        String apiUrl = "https://jsonplaceholder.typicode.com/posts/1";
        
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(apiUrl);
            httpGet.setHeader("Accept", "application/json");
            httpGet.setHeader("User-Agent", "Java-Library-Demo/1.0");
            
            try (CloseableHttpResponse response = client.execute(httpGet)) {
                int statusCode = response.getCode();
                System.out.println("HTTPステータス: " + statusCode);
                
                if (statusCode == 200) {
                    String responseBody = new String(
                        response.getEntity().getContent().readAllBytes());
                    
                    // JSONレスポンスを解析
                    JsonNode postNode = objectMapper.readTree(responseBody);
                    System.out.println("投稿タイトル: " + postNode.get("title").asText());
                    System.out.println("投稿内容: " + 
                        StringUtils.abbreviate(postNode.get("body").asText(), 50));
                    
                } else {
                    System.err.println("HTTP通信エラー: " + statusCode);
                }
            }
            
        } catch (IOException e) {
            logger.error("HTTP通信エラー", e);
        }
    }
    
    // 3. 文字列操作のデモンストレーション (Apache Commons Lang)
    public static void demonstrateStringUtils() {
        System.out.println("\n=== 文字列操作デモンストレーション (Apache Commons Lang) ===");
        
        String input = "  Hello, World!  ";
        
        // 基本的な文字列操作
        System.out.println("元の文字列: '" + input + "'");
        System.out.println("空白削除: '" + StringUtils.trim(input) + "'");
        System.out.println("大文字変換: '" + StringUtils.upperCase(input) + "'");
        System.out.println("逆順: '" + StringUtils.reverse(input.trim()) + "'");
        
        // 文字列の省略
        String longText = "これは非常に長いテキストです。省略表示のテストに使用します。";
        System.out.println("省略表示: " + StringUtils.abbreviate(longText, 20));
        
        // 空文字・null チェック
        String[] testStrings = {"", null, "   ", "有効な文字列"};
        for (String str : testStrings) {
            System.out.printf("'%s' -> 空文字判定: %s, null/空白判定: %s%n",
                str, StringUtils.isEmpty(str), StringUtils.isBlank(str));
        }
        
        // 文字列の差分計算（レーベンシュタイン距離）
        String str1 = "kitten";
        String str2 = "sitting";
        int distance = StringUtils.getLevenshteinDistance(str1, str2);
        System.out.printf("'%s' と '%s' の編集距離: %d%n", str1, str2, distance);
    }
    
    // 4. ログ出力のデモンストレーション (SLF4J + Logback)
    public static void demonstrateLogging() {
        System.out.println("\n=== ログ出力デモンストレーション (SLF4J + Logback) ===");
        
        Logger logger = LoggerFactory.getLogger(PracticalLibraryDemo.class);
        
        // 各レベルでのログ出力
        logger.trace("詳細デバッグ情報: メソッド実行開始");
        logger.debug("デバッグ情報: 変数値 x={}, y={}", 10, 20);
        logger.info("一般情報: アプリケーション開始時刻 {}", 
                   LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        logger.warn("警告: メモリ使用量が閾値を超過しています");
        logger.error("エラー: ファイル読み取りに失敗しました");
        
        // 例外情報付きログ
        try {
            int result = 10 / 0;
        } catch (ArithmeticException e) {
            logger.error("計算エラーが発生しました", e);
        }
        
        // 構造化ログ（MDC使用）
        org.slf4j.MDC.put("userId", "user123");
        org.slf4j.MDC.put("sessionId", "session456");
        logger.info("ユーザーログイン処理完了");
        org.slf4j.MDC.clear();
    }
    
    // 5. 日時処理のデモンストレーション (標準ライブラリ + 外部ライブラリ)
    public static void demonstrateDateTimeProcessing() {
        System.out.println("\n=== 日時処理デモンストレーション ===");
        
        LocalDateTime now = LocalDateTime.now();
        System.out.println("現在時刻: " + now);
        
        // 様々なフォーマットでの出力
        DateTimeFormatter[] formatters = {
            DateTimeFormatter.ISO_LOCAL_DATE_TIME,
            DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH時mm分ss秒"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"),
            DateTimeFormatter.ofPattern("MM-dd HH:mm")
        };
        
        for (DateTimeFormatter formatter : formatters) {
            System.out.println("フォーマット済み: " + now.format(formatter));
        }
        
        // 日時の計算
        LocalDateTime future = now.plusDays(30).plusHours(5);
        System.out.println("30日5時間後: " + future);
        
        LocalDateTime past = now.minusMonths(3).minusDays(10);
        System.out.println("3ヶ月10日前: " + past);
    }
    
    // データクラス例
    public static class Person {
        private String name;
        private int age;
        private String occupation;
        
        // デフォルトコンストラクタ（Jacksonに必要）
        public Person() {}
        
        public Person(String name, int age, String occupation) {
            this.name = name;
            this.age = age;
            this.occupation = occupation;
        }
        
        // ゲッター・セッター（Jacksonに必要）
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public int getAge() { return age; }
        public void setAge(int age) { this.age = age; }
        
        public String getOccupation() { return occupation; }
        public void setOccupation(String occupation) { this.occupation = occupation; }
        
        @Override
        public String toString() {
            return String.format("Person{name='%s', age=%d, occupation='%s'}", 
                               name, age, occupation);
        }
    }
    
    public static void main(String[] args) {
        System.out.println("実用的ライブラリ活用デモンストレーション開始\n");
        
        PracticalLibraryDemo demo = new PracticalLibraryDemo();
        
        demonstrateJsonProcessing();
        demo.demonstrateHttpClient();
        demonstrateStringUtils();
        demonstrateLogging();
        demonstrateDateTimeProcessing();
        
        System.out.println("\nデモンストレーション完了");
    }
}
```

### 基本的なMavenコマンド

```bash
# プロジェクトのコンパイル
mvn compile

# テストの実行
mvn test

# パッケージの作成
mvn package

# 依存関係のダウンロード
mvn dependency:resolve

# クリーンビルド
mvn clean package

# プロジェクトの実行
mvn exec:java -Dexec.mainClass="com.example.App"
```

## 13.3 人気ライブラリの活用

### Jackson - JSON処理

```java
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.*;

class Person {
    private String name;
    private int age;
    private List<String> hobbies;
    
    // コンストラクタ
    public Person() {}
    
    public Person(String name, int age, List<String> hobbies) {
        this.name = name;
        this.age = age;
        this.hobbies = hobbies;
    }
    
    // getter/setter
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public List<String> getHobbies() { return hobbies; }
    public void setHobbies(List<String> hobbies) { this.hobbies = hobbies; }
    
    @Override
    public String toString() {
        return "Person{name='" + name + "', age=" + age + ", hobbies=" + hobbies + "}";
    }
}

public class JacksonExample {
    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        
        // Javaオブジェクト → JSON
        Person person = new Person("田中太郎", 25, Arrays.asList("読書", "映画鑑賞"));
        String json = mapper.writeValueAsString(person);
        System.out.println("JSON: " + json);
        
        // JSON → Javaオブジェクト
        String jsonInput = """
            {
                "name": "佐藤花子",
                "age": 30,
                "hobbies": ["料理", "旅行", "音楽"]
            }
            """;
        Person parsedPerson = mapper.readValue(jsonInput, Person.class);
        System.out.println("パースした人: " + parsedPerson);
        
        // JSON → JsonNode（動的解析）
        JsonNode node = mapper.readTree(jsonInput);
        String name = node.get("name").asText();
        int age = node.get("age").asInt();
        System.out.println("名前: " + name + ", 年齢: " + age);
        
        // 配列の処理
        String jsonArray = """
            [
                {"name": "太郎", "age": 20},
                {"name": "花子", "age": 25},
                {"name": "次郎", "age": 30}
            ]
            """;
        
        List<Person> people = mapper.readValue(jsonArray, 
            mapper.getTypeFactory().constructCollectionType(List.class, Person.class));
        
        people.forEach(System.out::println);
    }
}
```

### Apache Commons Lang - ユーティリティ

```java
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateUtils;
import java.util.Date;

public class CommonsLangExample {
    public static void main(String[] args) {
        // 文字列ユーティリティ
        String text = "  Hello World  ";
        
        System.out.println("空文字チェック: " + StringUtils.isEmpty(""));
        System.out.println("空白文字チェック: " + StringUtils.isBlank("   "));
        System.out.println("左パディング: " + StringUtils.leftPad("123", 5, "0"));
        System.out.println("キャピタライズ: " + StringUtils.capitalize("hello world"));
        System.out.println("逆順: " + StringUtils.reverse("hello"));
        
        // 配列を文字列に結合
        String[] array = {"apple", "banana", "cherry"};
        String joined = StringUtils.join(array, ", ");
        System.out.println("結合: " + joined);
        
        // ランダム文字列生成
        String randomString = RandomStringUtils.randomAlphabetic(10);
        String randomNumber = RandomStringUtils.randomNumeric(6);
        System.out.println("ランダム文字列: " + randomString);
        System.out.println("ランダム数字: " + randomNumber);
        
        // 日付ユーティリティ
        Date now = new Date();
        Date tomorrow = DateUtils.addDays(now, 1);
        Date nextWeek = DateUtils.addWeeks(now, 1);
        
        System.out.println("今日: " + now);
        System.out.println("明日: " + tomorrow);
        System.out.println("来週: " + nextWeek);
    }
}
```

### Guava - Googleのユーティリティライブラリ

```java
import com.google.common.collect.*;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import java.util.*;

public class GuavaExample {
    public static void main(String[] args) {
        // 便利なコレクション作成
        List<String> list = Lists.newArrayList("a", "b", "c");
        Set<String> set = Sets.newHashSet("x", "y", "z");
        Map<String, Integer> map = Maps.newHashMap();
        map.put("apple", 100);
        map.put("banana", 80);
        
        // ImmutableList（不変リスト）
        List<String> immutableList = ImmutableList.of("red", "green", "blue");
        // immutableList.add("yellow"); // エラー！
        
        // Multiset（要素の出現回数を管理）
        Multiset<String> multiset = HashMultiset.create();
        multiset.add("apple");
        multiset.add("banana");
        multiset.add("apple");
        multiset.add("apple");
        
        System.out.println("appleの数: " + multiset.count("apple"));  // 3
        
        // Multimap（1つのキーに複数の値）
        Multimap<String, String> multimap = ArrayListMultimap.create();
        multimap.put("fruits", "apple");
        multimap.put("fruits", "banana");
        multimap.put("vegetables", "carrot");
        multimap.put("vegetables", "spinach");
        
        System.out.println("果物: " + multimap.get("fruits"));
        
        // 文字列操作
        List<String> words = Arrays.asList("Java", "Python", "C++");
        String joined = Joiner.on(", ").join(words);
        System.out.println("結合: " + joined);
        
        String text = "apple,banana,cherry";
        Iterable<String> split = Splitter.on(',').split(text);
        System.out.println("分割: " + split);
        
        // フィルタリング
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        Iterable<Integer> evenNumbers = Iterables.filter(numbers, n -> n % 2 == 0);
        System.out.println("偶数: " + evenNumbers);
        
        // 変換
        Iterable<String> stringNumbers = Iterables.transform(numbers, n -> "Number: " + n);
        System.out.println("変換: " + stringNumbers);
    }
}
```

## 13.4 ログライブラリ

### SLF4J + Logback

```xml
<!-- pom.xmlに追加 -->
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-api</artifactId>
    <version>2.0.7</version>
</dependency>
<dependency>
    <groupId>ch.qos.logback</groupId>
    <artifactId>logback-classic</artifactId>
    <version>1.4.8</version>
</dependency>
```

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingExample {
    private static final Logger logger = LoggerFactory.getLogger(LoggingExample.class);
    
    public static void main(String[] args) {
        logger.trace("トレースメッセージ");
        logger.debug("デバッグメッセージ");
        logger.info("情報メッセージ");
        logger.warn("警告メッセージ");
        logger.error("エラーメッセージ");
        
        // パラメータ付きログ
        String user = "田中太郎";
        int attempts = 3;
        logger.info("ユーザー {} が {} 回ログインを試行しました", user, attempts);
        
        // 例外のログ
        try {
            int result = 10 / 0;
        } catch (Exception e) {
            logger.error("計算エラーが発生しました", e);
        }
    }
}
```

### logback.xml設定ファイル

```xml
<!-- src/main/resources/logback.xml -->
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <!-- コンソール出力 -->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <!-- ファイル出力 -->
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/application.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>logs/application.%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <!-- ルートロガー -->
    <root level="INFO">
        <appender-ref ref="CONSOLE" />
        <appender-ref ref="FILE" />
    </root>
    
    <!-- 特定パッケージのログレベル -->
    <logger name="com.example" level="DEBUG" />
</configuration>
```

## 13.5 データベースアクセス

### H2データベース + JDBC

```xml
<!-- pom.xmlに追加 -->
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <version>2.2.220</version>
</dependency>
```

```java
import java.sql.*;
import java.util.*;

class User {
    private int id;
    private String name;
    private String email;
    
    public User(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
    
    // getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    
    @Override
    public String toString() {
        return "User{id=" + id + ", name='" + name + "', email='" + email + "'}";
    }
}

public class DatabaseExample {
    private static final String DB_URL = "jdbc:h2:mem:testdb";
    private static final String USER = "sa";
    private static final String PASS = "";
    
    public static void main(String[] args) {
        try {
            // データベース接続
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            
            // テーブル作成
            createTable(conn);
            
            // データ挿入
            insertUsers(conn);
            
            // データ取得
            List<User> users = selectAllUsers(conn);
            users.forEach(System.out::println);
            
            // データ更新
            updateUser(conn, 1, "田中太郎（更新）");
            
            // 特定ユーザー検索
            User user = selectUserById(conn, 1);
            System.out.println("更新後: " + user);
            
            conn.close();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private static void createTable(Connection conn) throws SQLException {
        String sql = """
            CREATE TABLE users (
                id INT PRIMARY KEY,
                name VARCHAR(100),
                email VARCHAR(100)
            )
            """;
        
        Statement stmt = conn.createStatement();
        stmt.execute(sql);
        stmt.close();
    }
    
    private static void insertUsers(Connection conn) throws SQLException {
        String sql = "INSERT INTO users (id, name, email) VALUES (?, ?, ?)";
        
        PreparedStatement pstmt = conn.prepareStatement(sql);
        
        // 複数ユーザーの挿入
        String[][] userData = {
            {"1", "田中太郎", "tanaka@example.com"},
            {"2", "佐藤花子", "sato@example.com"},
            {"3", "鈴木次郎", "suzuki@example.com"}
        };
        
        for (String[] user : userData) {
            pstmt.setInt(1, Integer.parseInt(user[0]));
            pstmt.setString(2, user[1]);
            pstmt.setString(3, user[2]);
            pstmt.executeUpdate();
        }
        
        pstmt.close();
    }
    
    private static List<User> selectAllUsers(Connection conn) throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, name, email FROM users";
        
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        
        while (rs.next()) {
            users.add(new User(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("email")
            ));
        }
        
        rs.close();
        stmt.close();
        return users;
    }
    
    private static User selectUserById(Connection conn, int id) throws SQLException {
        String sql = "SELECT id, name, email FROM users WHERE id = ?";
        
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, id);
        ResultSet rs = pstmt.executeQuery();
        
        User user = null;
        if (rs.next()) {
            user = new User(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("email")
            );
        }
        
        rs.close();
        pstmt.close();
        return user;
    }
    
    private static void updateUser(Connection conn, int id, String newName) throws SQLException {
        String sql = "UPDATE users SET name = ? WHERE id = ?";
        
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, newName);
        pstmt.setInt(2, id);
        pstmt.executeUpdate();
        
        pstmt.close();
    }
}
```

## 13.6 HTTP通信

### OkHttp ライブラリ

```xml
<!-- pom.xmlに追加 -->
<dependency>
    <groupId>com.squareup.okhttp3</groupId>
    <artifactId>okhttp</artifactId>
    <version>4.11.0</version>
</dependency>
```

```java
import okhttp3.*;
import java.io.IOException;

public class HttpClientExample {
    private static final OkHttpClient client = new OkHttpClient();
    
    public static void main(String[] args) throws IOException {
        // GET リクエスト
        String getResult = performGetRequest("https://httpbin.org/get");
        System.out.println("GET結果: " + getResult);
        
        // POST リクエスト
        String postResult = performPostRequest("https://httpbin.org/post", 
            "{\"name\":\"田中太郎\",\"age\":25}");
        System.out.println("POST結果: " + postResult);
        
        // ヘッダー付きリクエスト
        String headerResult = performRequestWithHeaders("https://httpbin.org/headers");
        System.out.println("ヘッダー付き結果: " + headerResult);
    }
    
    private static String performGetRequest(String url) throws IOException {
        Request request = new Request.Builder()
            .url(url)
            .build();
        
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }
    
    private static String performPostRequest(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(json, MediaType.get("application/json"));
        
        Request request = new Request.Builder()
            .url(url)
            .post(body)
            .build();
        
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }
    
    private static String performRequestWithHeaders(String url) throws IOException {
        Request request = new Request.Builder()
            .url(url)
            .addHeader("User-Agent", "Java-App/1.0")
            .addHeader("Accept", "application/json")
            .addHeader("Authorization", "Bearer token123")
            .build();
        
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }
}
```

## 13.7 実践的なプロジェクト例

### 天気情報取得アプリケーション

```java
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;

class WeatherInfo {
    private String city;
    private double temperature;
    private String description;
    private int humidity;
    
    public WeatherInfo(String city, double temperature, String description, int humidity) {
        this.city = city;
        this.temperature = temperature;
        this.description = description;
        this.humidity = humidity;
    }
    
    @Override
    public String toString() {
        return String.format("%s: %.1f℃, %s, 湿度%d%%", 
                           city, temperature, description, humidity);
    }
}

public class WeatherApp {
    private static final Logger logger = LoggerFactory.getLogger(WeatherApp.class);
    private static final OkHttpClient client = new OkHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();
    
    // 実際のAPIキーが必要
    private static final String API_KEY = "your-api-key";
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather";
    
    public static void main(String[] args) {
        WeatherApp app = new WeatherApp();
        
        try {
            WeatherInfo tokyo = app.getWeatherInfo("Tokyo");
            WeatherInfo osaka = app.getWeatherInfo("Osaka");
            
            System.out.println(tokyo);
            System.out.println(osaka);
            
        } catch (Exception e) {
            logger.error("天気情報の取得に失敗しました", e);
        }
    }
    
    public WeatherInfo getWeatherInfo(String city) throws IOException {
        String url = String.format("%s?q=%s&appid=%s&units=metric&lang=ja", 
                                 BASE_URL, city, API_KEY);
        
        Request request = new Request.Builder()
            .url(url)
            .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("APIエラー: " + response.code());
            }
            
            String jsonResponse = response.body().string();
            return parseWeatherInfo(city, jsonResponse);
        }
    }
    
    private WeatherInfo parseWeatherInfo(String city, String json) throws IOException {
        JsonNode root = mapper.readTree(json);
        
        double temperature = root.path("main").path("temp").asDouble();
        String description = root.path("weather").get(0).path("description").asText();
        int humidity = root.path("main").path("humidity").asInt();
        
        return new WeatherInfo(city, temperature, description, humidity);
    }
}
```

## 13.8 練習問題

1. JSONファイルを読み込んで、Javaオブジェクトに変換し、フィルタリング・ソート機能を実装してください。

2. 簡単なREST APIクライアントを作成し、外部サービスからデータを取得してください。

3. データベースを使用した簡単なCRUDアプリケーションを作成してください。

## まとめ

本章では、外部ライブラリの活用方法を学習しました。Mavenによる依存関係管理、人気ライブラリの使用方法、データベースアクセス、HTTP通信など、実用的なJavaアプリケーション開発に必要な知識を習得できました。ライブラリを適切に活用することで、開発効率を大幅に向上させることができます。