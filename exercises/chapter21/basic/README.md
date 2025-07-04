# 第21章 基本課題

## 🎯 学習目標
- Javadocによる包括的なドキュメント作成
- 外部ライブラリの効果的な活用方法
- Maven/Gradleによる依存関係管理
- APIドキュメントの自動生成
- ライブラリ作成とパッケージング
- 技術文書ライティングとナレッジ共有のベストプラクティス

## 📝 課題一覧

### 課題1: Javadocドキュメントシステム
**ファイル名**: `MathUtils.java`, `DataProcessor.java`, `javadoc-config.xml`

包括的なJavadocドキュメントを作成し、APIドキュメントの自動生成システムを構築してください。

**要求仕様**:
- 完全なJavadocコメントの作成
- カスタムタグとアノテーションの活用
- UMLダイアグラムの埋め込み
- サンプルコードの充実
- 多言語対応ドキュメント

**実行例**:
```
=== Javadocドキュメントシステム ===

MathUtils.java - 完全なJavadocコメント:

/**
 * 数学的計算を行うユーティリティクラスです。
 * 
 * <p>このクラスは様々な数学的演算を提供します：
 * <ul>
 *   <li>基本的な算術演算</li>
 *   <li>統計計算</li>
 *   <li>幾何学的計算</li>
 *   <li>確率・統計関数</li>
 * </ul>
 * 
 * <p>使用例：
 * <pre>{@code
 * // 平均値の計算
 * double[] data = {1.0, 2.0, 3.0, 4.0, 5.0};
 * double average = MathUtils.average(data);
 * System.out.println("平均値: " + average); // 出力: 平均値: 3.0
 * 
 * // 標準偏差の計算
 * double stdDev = MathUtils.standardDeviation(data);
 * System.out.println("標準偏差: " + stdDev);
 * }</pre>
 * 
 * @author 開発チーム
 * @version 2.1.0
 * @since 1.0.0
 * @see java.lang.Math
 * @see java.util.stream.DoubleStream
 */
public class MathUtils {
    
    /**
     * 配列の平均値を計算します。
     * 
     * <p>この メソッドは配列内のすべての要素の算術平均を返します。
     * 空の配列が渡された場合は {@link IllegalArgumentException} がスローされます。
     * 
     * <p>計算式：<br>
     * {@code 平均値 = (x₁ + x₂ + ... + xₙ) / n}
     * 
     * @param values 計算対象の数値配列。{@code null} や空配列は不可
     * @return 配列の平均値
     * @throws IllegalArgumentException 配列が {@code null} または空の場合
     * @throws ArithmeticException 数値がオーバーフローした場合
     * 
     * @example
     * <pre>{@code
     * double[] scores = {85.5, 92.0, 78.5, 96.0, 88.5};
     * double avg = MathUtils.average(scores);
     * // avg = 88.1
     * }</pre>
     * 
     * @see #median(double[])
     * @see #standardDeviation(double[])
     * @since 1.0.0
     */
    public static double average(double[] values) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("配列は null または空であってはいけません");
        }
        
        double sum = 0.0;
        for (double value : values) {
            sum += value;
        }
        return sum / values.length;
    }
    
    /**
     * 2点間の距離を計算します。
     * 
     * <p>ユークリッド距離の計算式を使用：<br>
     * {@code distance = √((x₂-x₁)² + (y₂-y₁)²)}
     * 
     * @param x1 点1のx座標
     * @param y1 点1のy座標  
     * @param x2 点2のx座標
     * @param y2 点2のy座標
     * @return 2点間の距離
     * 
     * @example
     * <pre>{@code
     * // 原点(0,0)から点(3,4)までの距離
     * double distance = MathUtils.distance(0, 0, 3, 4);
     * // distance = 5.0
     * }</pre>
     * 
     * @since 2.0.0
     */
    public static double distance(double x1, double y1, double x2, double y2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        return Math.sqrt(dx * dx + dy * dy);
    }
}

DataProcessor.java - 高度なJavadocパターン:

/**
 * データ処理のためのユーティリティクラスです。
 * 
 * <h2>機能概要</h2>
 * <table border="1">
 *   <caption>提供される機能一覧</caption>
 *   <tr><th>機能</th><th>メソッド</th><th>説明</th></tr>
 *   <tr><td>フィルタリング</td><td>{@link #filter}</td><td>条件に基づくデータ抽出</td></tr>
 *   <tr><td>変換</td><td>{@link #transform}</td><td>データ形式の変換</td></tr>
 *   <tr><td>集約</td><td>{@link #aggregate}</td><td>データの集計処理</td></tr>
 * </table>
 * 
 * <h2>パフォーマンス特性</h2>
 * <ul>
 *   <li>時間計算量: O(n) - データサイズに比例</li>
 *   <li>空間計算量: O(1) - 一定のメモリ使用量</li>
 *   <li>スレッドセーフ: ✓ (状態を持たない)</li>
 * </ul>
 * 
 * @param <T> 処理対象のデータ型
 * @author データサイエンスチーム
 * @version 3.0.0
 * @since 2.5.0
 * 
 * @apiNote このクラスは関数型プログラミングスタイルで設計されています
 * @implNote 内部でStream APIを使用して効率的な処理を行います
 * @see java.util.stream.Stream
 * @see java.util.function.Predicate
 */
public class DataProcessor<T> {
    
    /**
     * 指定された条件でデータをフィルタリングします。
     * 
     * @apiNote ラムダ式を使用した例：
     * <pre>{@code
     * List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
     * List<Integer> evens = processor.filter(numbers, n -> n % 2 == 0);
     * }</pre>
     * 
     * @param data フィルタリング対象のデータ
     * @param predicate フィルタリング条件
     * @return 条件を満たすデータのリスト
     * 
     * @throws NullPointerException {@code data} または {@code predicate} が {@code null} の場合
     * 
     * @see java.util.function.Predicate
     * @since 2.5.0
     */
    public List<T> filter(List<T> data, Predicate<T> predicate) {
        // 実装...
    }
}

カスタムタグ定義:

/**
 * カスタムタグレット設定
 * 
 * @example 使用例を示すカスタムタグ
 * @performance パフォーマンス情報
 * @threading スレッドセーフティ情報
 * @complexity 計算量情報
 */

Javadoc生成設定 (Maven):
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-javadoc-plugin</artifactId>
    <version>3.4.0</version>
    <configuration>
        <source>17</source>
        <encoding>UTF-8</encoding>
        <charset>UTF-8</charset>
        <docencoding>UTF-8</docencoding>
        <locale>ja_JP</locale>
        
        <!-- カスタムタグの定義 -->
        <tags>
            <tag>
                <name>example</name>
                <placement>a</placement>
                <head>使用例:</head>
            </tag>
            <tag>
                <name>performance</name>
                <placement>a</placement>
                <head>パフォーマンス:</head>
            </tag>
        </tags>
        
        <!-- UMLダイアグラム生成 -->
        <doclet>nl.talsmasoftware.umldoclet.UMLDoclet</doclet>
        <docletArtifact>
            <groupId>nl.talsmasoftware</groupId>
            <artifactId>umldoclet</artifactId>
            <version>2.0.16</version>
        </docletArtifact>
        
        <!-- 外部リンク -->
        <links>
            <link>https://docs.oracle.com/en/java/javase/17/docs/api/</link>
            <link>https://junit.org/junit5/docs/current/api/</link>
        </links>
        
        <!-- 警告抑制 -->
        <quiet>true</quiet>
        <failOnWarnings>false</failOnWarnings>
    </configuration>
</plugin>

生成されるドキュメント構造:
target/site/apidocs/
├── index.html (トップページ)
├── overview-summary.html (概要)
├── com/
│   └── example/
│       ├── package-summary.html
│       ├── MathUtils.html
│       └── DataProcessor.html
├── stylesheet.css
└── script.js

HTMLドキュメント出力例:
<!DOCTYPE HTML>
<html lang="ja">
<head>
    <title>MathUtils (API Documentation)</title>
    <meta charset="utf-8">
</head>
<body>
    <h1>クラス MathUtils</h1>
    
    <div class="description">
        <p>数学的計算を行うユーティリティクラスです。</p>
        
        <h2>使用例</h2>
        <pre><code>
double[] data = {1.0, 2.0, 3.0, 4.0, 5.0};
double average = MathUtils.average(data);
        </code></pre>
    </div>
    
    <div class="method-summary">
        <h2>メソッドの概要</h2>
        <table>
            <tr>
                <td>static double</td>
                <td><a href="#average(double[])">average</a>(double[] values)</td>
                <td>配列の平均値を計算します。</td>
            </tr>
        </table>
    </div>
</body>
</html>

ドキュメント品質メトリクス:
総クラス数: 45
ドキュメント化率: 98.2% (44/45クラス)
メソッドドキュメント率: 95.7% (234/245メソッド)
パラメータ説明率: 92.1%
戻り値説明率: 94.8%
例外説明率: 89.3%

品質チェック結果:
✅ 全パブリッククラスにクラスレベルコメント
✅ 全パブリックメソッドにメソッドコメント
✅ 全パラメータに@paramタグ
✅ 戻り値のあるメソッドに@returnタグ
❌ 一部メソッドで@throws不足 (15件)
✅ @since タグの一貫性
✅ サンプルコードの動作確認
```

**評価ポイント**:
- 包括的なJavadocコメントの作成
- HTMLドキュメントの自動生成
- ドキュメント品質の維持

---

### 課題2: 外部ライブラリ活用システム
**ファイル名**: `LibraryIntegration.java`, `pom.xml`, `build.gradle`

人気の外部ライブラリを活用したアプリケーションを作成し、ライブラリ管理のベストプラクティスを学んでください。

**要求仕様**:
- Apache Commons、Jackson、Lombok等の活用
- 依存関係の適切な管理
- ライブラリのバージョン管理
- 競合解決とトラブルシューティング
- セキュリティ脆弱性のチェック

**実行例**:
```
=== 外部ライブラリ活用システム ===

Maven pom.xml 設定:
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.example</groupId>
    <artifactId>library-integration-demo</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>
    
    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        
        <!-- ライブラリバージョン管理 -->
        <commons-lang3.version>3.12.0</commons-lang3.version>
        <jackson.version>2.15.2</jackson.version>
        <lombok.version>1.18.28</lombok.version>
        <slf4j.version>2.0.7</slf4j.version>
    </properties>
    
    <dependencies>
        <!-- Apache Commons -->
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-lang3</artifactId>
            <version>${commons-lang3.version}</version>
        </dependency>
        
        <dependency>
            <groupId>commons-io</groupId>
            <artifactId>commons-io</artifactId>
            <version>2.11.0</version>
        </dependency>
        
        <!-- JSON処理 (Jackson) -->
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
            <version>${jackson.version}</version>
        </dependency>
        
        <dependency>
            <groupId>com.fasterxml.jackson.datatype</groupId>
            <artifactId>jackson-datatype-jsr310</artifactId>
            <version>${jackson.version}</version>
        </dependency>
        
        <!-- Lombok (開発時のみ) -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>${lombok.version}</version>
            <scope>provided</scope>
        </dependency>
        
        <!-- ログ出力 -->
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
            <version>${slf4j.version}</version>
        </dependency>
        
        <dependency>
            <groupId>ch.qos.logback</groupId>
            <artifactId>logback-classic</artifactId>
            <version>1.4.8</version>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <!-- 依存関係チェック -->
            <plugin>
                <groupId>org.owasp</groupId>
                <artifactId>dependency-check-maven</artifactId>
                <version>8.3.1</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>check</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
            
            <!-- ライセンスチェック -->
            <plugin>
                <groupId>org.codehaus.mojo</groupId>
                <artifactId>license-maven-plugin</artifactId>
                <version>2.2.0</version>
            </plugin>
        </plugins>
    </build>
</project>

ライブラリ活用例:

1. Apache Commons Lang3:
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class CommonsDemo {
    public void stringOperations() {
        // 文字列ユーティリティ
        String text = "  Hello World  ";
        
        // 空文字・null チェック
        boolean isEmpty = StringUtils.isEmpty(text);           // false
        boolean isBlank = StringUtils.isBlank(text);          // false
        boolean isNotEmpty = StringUtils.isNotEmpty(text);    // true
        
        // 文字列変換
        String trimmed = StringUtils.trim(text);              // "Hello World"
        String capitalized = StringUtils.capitalize(text);    // "  Hello world  "
        String reversed = StringUtils.reverse(text.trim());   // "dlroW olleH"
        
        // ランダム文字列生成
        String randomAlphabetic = RandomStringUtils.randomAlphabetic(10);    // "KjHgFdSaQw"
        String randomNumeric = RandomStringUtils.randomNumeric(8);           // "12345678"
        String randomAlphanumeric = RandomStringUtils.randomAlphanumeric(12); // "Kj3HgF8SaQ2w"
        
        System.out.println("元の文字列: '" + text + "'");
        System.out.println("トリム後: '" + trimmed + "'");
        System.out.println("ランダム文字列: " + randomAlphabetic);
    }
}

2. Jackson JSON処理:
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Data @NoArgsConstructor @AllArgsConstructor
public class User {
    private Long id;
    private String name;
    private String email;
    private LocalDateTime createdAt;
    private List<String> tags;
}

public class JacksonDemo {
    private final ObjectMapper objectMapper;
    
    public JacksonDemo() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }
    
    public void jsonSerialization() throws Exception {
        // オブジェクト → JSON
        User user = new User(1L, "田中太郎", "tanaka@example.com", 
            LocalDateTime.now(), Arrays.asList("VIP", "Premium"));
        
        String json = objectMapper.writeValueAsString(user);
        System.out.println("JSON: " + json);
        
        // JSON → オブジェクト
        User deserializedUser = objectMapper.readValue(json, User.class);
        System.out.println("復元されたユーザー: " + deserializedUser);
        
        // ファイルからの読み書き
        objectMapper.writeValue(new File("user.json"), user);
        User fromFile = objectMapper.readValue(new File("user.json"), User.class);
    }
}

3. Lombok活用:
import lombok.Data;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Data                    // getter, setter, equals, hashCode, toString
@Builder                 // ビルダーパターン  
@Slf4j                  // ログ出力
public class Employee {
    private Long id;
    private String name;
    private String department;
    private BigDecimal salary;
    private LocalDate hireDate;
    
    public void processEmployeeData() {
        log.info("従業員データを処理中: {}", name);
        
        if (salary.compareTo(BigDecimal.valueOf(500000)) > 0) {
            log.warn("高額給与の従業員: {} - {}", name, salary);
        }
        
        log.debug("処理完了: ID={}, 部署={}", id, department);
    }
}

// Lombokを使わない場合との比較
public class EmployeeWithoutLombok {
    private Long id;
    private String name;
    // ... フィールド
    
    // 手動でgetter/setter (約50行)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    // ... 省略
    
    // equals/hashCode (約20行)
    @Override
    public boolean equals(Object o) { /* ... */ }
    @Override
    public int hashCode() { /* ... */ }
    
    // toString (約10行)
    @Override
    public String toString() { /* ... */ }
}

使用例:
Employee emp = Employee.builder()
    .id(1L)
    .name("佐藤花子")
    .department("開発部")
    .salary(new BigDecimal("600000"))
    .hireDate(LocalDate.now())
    .build();

emp.processEmployeeData();

4. SLF4J ログ出力:
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingDemo {
    private static final Logger logger = LoggerFactory.getLogger(LoggingDemo.class);
    
    public void demonstrateLogging() {
        String username = "田中太郎";
        int attemptCount = 3;
        
        // 各ログレベルの使用例
        logger.trace("メソッド開始: demonstrateLogging()");
        logger.debug("ユーザー認証開始: username={}", username);
        logger.info("ログイン試行: user={}, attempt={}", username, attemptCount);
        logger.warn("認証失敗が{}回発生しました", attemptCount);
        logger.error("認証エラー: ユーザー '{}' のアカウントがロックされました", username);
        
        // 例外ログ
        try {
            throw new RuntimeException("テスト例外");
        } catch (Exception e) {
            logger.error("予期しないエラーが発生: user={}", username, e);
        }
    }
}

依存関係分析:
mvn dependency:tree

[INFO] com.example:library-integration-demo:jar:1.0.0
[INFO] +- org.apache.commons:commons-lang3:jar:3.12.0:compile
[INFO] +- commons-io:commons-io:jar:2.11.0:compile
[INFO] +- com.fasterxml.jackson.core:jackson-databind:jar:2.15.2:compile
[INFO] |  +- com.fasterxml.jackson.core:jackson-annotations:jar:2.15.2:compile
[INFO] |  \- com.fasterxml.jackson.core:jackson-core:jar:2.15.2:compile
[INFO] +- com.fasterxml.jackson.datatype:jackson-datatype-jsr310:jar:2.15.2:compile
[INFO] +- org.projectlombok:lombok:jar:1.18.28:provided
[INFO] +- org.slf4j:slf4j-api:jar:2.0.7:compile
[INFO] \- ch.qos.logback:logback-classic:jar:1.4.8:compile
[INFO]    \- ch.qos.logback:logback-core:jar:1.4.8:compile

脆弱性チェック:
mvn org.owasp:dependency-check-maven:check

Dependency-Check Report:
分析対象ライブラリ: 12
既知の脆弱性: 0件
疑わしい依存関係: 0件
チェック日時: 2024-07-04 10:30:00

ライセンス確認:
mvn license:aggregate-third-party-report

使用ライブラリのライセンス:
- Apache Commons Lang3: Apache License 2.0
- Jackson: Apache License 2.0  
- Lombok: MIT License
- SLF4J: MIT License
- Logback: EPL v1.0, LGPL 2.1

JAR ファイルサイズ:
元のJAR: 15.2 KB
依存関係含むJAR: 8.7 MB
  - Jackson: 3.2 MB
  - Commons: 2.1 MB
  - Logback: 1.8 MB
  - その他: 1.6 MB

パフォーマンス比較:
自作実装 vs ライブラリ使用:

文字列処理 (10,000回):
- 自作StringUtils: 245ms
- Apache Commons: 89ms (2.75倍高速)

JSON処理 (1,000オブジェクト):
- 手動JSON生成: 1,234ms
- Jackson使用: 156ms (7.9倍高速)

オブジェクト生成 (コード行数):
- 手動実装: 85行
- Lombok使用: 12行 (7.1倍簡潔)
```

**評価ポイント**:
- 適切なライブラリの選択と活用
- 依存関係管理のベストプラクティス
- セキュリティとライセンスの理解

---

### 課題3: APIドキュメント自動生成システム
**ファイル名**: `RestApiController.java`, `openapi-spec.yaml`, `swagger-config.java`

REST APIのドキュメントを自動生成し、開発者向けのAPIドキュメントサイトを構築してください。

**要求仕様**:
- OpenAPI/Swagger仕様書の自動生成
- インタラクティブなAPIドキュメント
- リクエスト/レスポンス例の充実
- 認証・エラーハンドリングの文書化
- 多バージョンAPI対応

**実行例**:
```
=== APIドキュメント自動生成システム ===

Swagger設定 (SpringBootの場合):
@Configuration
@EnableSwagger2
public class SwaggerConfig {
    
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.example.controller"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo())
                .securitySchemes(Arrays.asList(apiKey()))
                .securityContexts(Arrays.asList(securityContext()));
    }
    
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("ユーザー管理API")
                .description("ユーザー情報の管理を行うRESTful API")
                .version("2.1.0")
                .contact(new Contact("開発チーム", "https://example.com", "dev@example.com"))
                .license("MIT License")
                .licenseUrl("https://opensource.org/licenses/MIT")
                .build();
    }
}

REST APIコントローラ (完全なドキュメント):
@RestController
@RequestMapping("/api/v2/users")
@Api(tags = "ユーザー管理", description = "ユーザー情報のCRUD操作")
@Validated
public class UserController {
    
    @Autowired
    private UserService userService;
    
    /**
     * ユーザー一覧を取得します。
     * 
     * @param page ページ番号 (0から開始)
     * @param size 1ページあたりの件数
     * @param sort ソート条件
     * @return ユーザー一覧
     */
    @GetMapping
    @ApiOperation(
        value = "ユーザー一覧取得",
        notes = "指定された条件でユーザー一覧を取得します。ページネーション対応。",
        response = UserPageResponse.class
    )
    @ApiResponses({
        @ApiResponse(code = 200, message = "取得成功", response = UserPageResponse.class),
        @ApiResponse(code = 400, message = "パラメータエラー", response = ErrorResponse.class),
        @ApiResponse(code = 401, message = "認証エラー", response = ErrorResponse.class),
        @ApiResponse(code = 500, message = "サーバーエラー", response = ErrorResponse.class)
    })
    public ResponseEntity<UserPageResponse> getUsers(
            @ApiParam(value = "ページ番号", defaultValue = "0", example = "0")
            @RequestParam(defaultValue = "0") @Min(0) int page,
            
            @ApiParam(value = "ページサイズ", defaultValue = "20", example = "20")
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
            
            @ApiParam(value = "ソート条件", example = "name,asc")
            @RequestParam(required = false) String sort
    ) {
        PageRequest pageRequest = PageRequest.of(page, size);
        if (sort != null) {
            // ソート処理
        }
        
        Page<User> users = userService.findUsers(pageRequest);
        return ResponseEntity.ok(UserPageResponse.from(users));
    }
    
    @PostMapping
    @ApiOperation(
        value = "ユーザー作成",
        notes = "新しいユーザーを作成します。メールアドレスは一意である必要があります。"
    )
    @ApiResponses({
        @ApiResponse(code = 201, message = "作成成功", response = UserResponse.class),
        @ApiResponse(code = 400, message = "バリデーションエラー", response = ValidationErrorResponse.class),
        @ApiResponse(code = 409, message = "メールアドレス重複", response = ErrorResponse.class)
    })
    public ResponseEntity<UserResponse> createUser(
            @ApiParam(value = "ユーザー作成情報", required = true)
            @Valid @RequestBody CreateUserRequest request
    ) {
        User user = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UserResponse.from(user));
    }
    
    @GetMapping("/{id}")
    @ApiOperation(value = "ユーザー詳細取得")
    public ResponseEntity<UserResponse> getUser(
            @ApiParam(value = "ユーザーID", required = true, example = "123")
            @PathVariable @Positive Long id
    ) {
        User user = userService.findById(id);
        return ResponseEntity.ok(UserResponse.from(user));
    }
}

データモデル (完全なドキュメント):
@ApiModel(description = "ユーザー作成リクエスト")
@Data @NoArgsConstructor @AllArgsConstructor
public class CreateUserRequest {
    
    @ApiModelProperty(
        value = "ユーザー名",
        required = true,
        example = "田中太郎",
        notes = "2文字以上50文字以内で入力してください"
    )
    @NotBlank(message = "ユーザー名は必須です")
    @Size(min = 2, max = 50, message = "ユーザー名は2文字以上50文字以内で入力してください")
    private String name;
    
    @ApiModelProperty(
        value = "メールアドレス",
        required = true,
        example = "tanaka@example.com",
        notes = "有効なメールアドレス形式で入力してください"
    )
    @NotBlank(message = "メールアドレスは必須です")
    @Email(message = "有効なメールアドレスを入力してください")
    private String email;
    
    @ApiModelProperty(
        value = "年齢",
        required = false,
        example = "30",
        notes = "18歳以上120歳以下で入力してください"
    )
    @Min(value = 18, message = "年齢は18歳以上で入力してください")
    @Max(value = 120, message = "年齢は120歳以下で入力してください")
    private Integer age;
    
    @ApiModelProperty(
        value = "電話番号",
        required = false,
        example = "090-1234-5678",
        notes = "ハイフン区切りの形式で入力してください"
    )
    @Pattern(regexp = "^\\d{3}-\\d{4}-\\d{4}$", 
             message = "電話番号は000-0000-0000の形式で入力してください")
    private String phoneNumber;
}

@ApiModel(description = "ユーザー情報レスポンス")
@Data @Builder
public class UserResponse {
    
    @ApiModelProperty(value = "ユーザーID", example = "123")
    private Long id;
    
    @ApiModelProperty(value = "ユーザー名", example = "田中太郎")
    private String name;
    
    @ApiModelProperty(value = "メールアドレス", example = "tanaka@example.com")
    private String email;
    
    @ApiModelProperty(value = "年齢", example = "30")
    private Integer age;
    
    @ApiModelProperty(value = "作成日時", example = "2024-07-04T10:30:00Z")
    private LocalDateTime createdAt;
    
    @ApiModelProperty(value = "更新日時", example = "2024-07-04T15:45:00Z")
    private LocalDateTime updatedAt;
}

生成されるOpenAPI仕様書 (swagger.yaml):
openapi: 3.0.1
info:
  title: ユーザー管理API
  description: ユーザー情報の管理を行うRESTful API
  version: 2.1.0
  contact:
    name: 開発チーム
    url: https://example.com
    email: dev@example.com
  license:
    name: MIT License
    url: https://opensource.org/licenses/MIT

servers:
  - url: https://api.example.com/v2
    description: 本番環境
  - url: https://staging-api.example.com/v2
    description: ステージング環境
  - url: http://localhost:8080/api/v2
    description: 開発環境

paths:
  /users:
    get:
      tags:
        - ユーザー管理
      summary: ユーザー一覧取得
      description: 指定された条件でユーザー一覧を取得します。ページネーション対応。
      parameters:
        - name: page
          in: query
          description: ページ番号
          schema:
            type: integer
            minimum: 0
            default: 0
            example: 0
        - name: size
          in: query
          description: ページサイズ
          schema:
            type: integer
            minimum: 1
            maximum: 100
            default: 20
            example: 20
      responses:
        '200':
          description: 取得成功
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/UserPageResponse'
              examples:
                success:
                  summary: 成功例
                  value:
                    content:
                      - id: 1
                        name: "田中太郎"
                        email: "tanaka@example.com"
                        age: 30
                    totalElements: 100
                    totalPages: 5
                    number: 0
                    size: 20

    post:
      tags:
        - ユーザー管理
      summary: ユーザー作成
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/CreateUserRequest'
            examples:
              create_user:
                summary: ユーザー作成例
                value:
                  name: "佐藤花子"
                  email: "sato@example.com"
                  age: 25
                  phoneNumber: "080-9876-5432"

components:
  schemas:
    CreateUserRequest:
      type: object
      required:
        - name
        - email
      properties:
        name:
          type: string
          minLength: 2
          maxLength: 50
          example: "田中太郎"
          description: "ユーザー名 (2文字以上50文字以内)"
        email:
          type: string
          format: email
          example: "tanaka@example.com"
          description: "メールアドレス"
        age:
          type: integer
          minimum: 18
          maximum: 120
          example: 30
          description: "年齢 (18歳以上120歳以下)"

  securitySchemes:
    BearerAuth:
      type: http
      scheme: bearer
      bearerFormat: JWT

Swagger UI アクセス:
URL: http://localhost:8080/swagger-ui.html

表示される機能:
1. API概要とサーバー情報
2. エンドポイント一覧 (タグ別グループ化)
3. リクエスト/レスポンス例
4. インタラクティブなAPIテスト
5. スキーマ定義の詳細
6. 認証情報の入力

APIテスト実行例:
GET /api/v2/users?page=0&size=5

Response (200 OK):
{
  "content": [
    {
      "id": 1,
      "name": "田中太郎",
      "email": "tanaka@example.com",
      "age": 30,
      "createdAt": "2024-07-04T10:30:00Z"
    }
  ],
  "totalElements": 25,
  "totalPages": 5,
  "number": 0,
  "size": 5
}

ドキュメント生成統計:
API エンドポイント数: 15
データモデル数: 12
リクエスト例数: 28
レスポンス例数: 45
自動生成時間: 3.2秒
HTML ページサイズ: 1.2MB
```

**評価ポイント**:
- OpenAPI仕様の完全な理解
- インタラクティブドキュメントの構築
- 実用的なAPI設計の文書化

---

### 課題4: 独自ライブラリ作成とパッケージング
**ファイル名**: `MathLibrary.java`, `pom.xml`, `README.md`

再利用可能なライブラリを作成し、Maven Central等への公開準備を行ってください。

**要求仕様**:
- 機能的で有用なライブラリの設計
- 適切なパッケージ構造
- Maven/Gradleでのビルド設定
- リリース用のメタデータ設定
- ライブラリドキュメントの充実

**実行例**:
```
=== 独自ライブラリ作成とパッケージング ===

ライブラリプロジェクト構造:
math-utils-library/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/mathutils/
│   │   │       ├── Statistics.java
│   │   │       ├── Geometry.java
│   │   │       ├── NumberUtils.java
│   │   │       └── MathConstants.java
│   │   └── resources/
│   │       └── META-INF/
│   │           └── MANIFEST.MF
│   └── test/
│       └── java/
│           └── com/example/mathutils/
│               ├── StatisticsTest.java
│               └── GeometryTest.java
├── pom.xml
├── README.md
├── LICENSE
└── CHANGELOG.md

pom.xml (Maven Central対応):
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.example</groupId>
    <artifactId>math-utils</artifactId>
    <version>1.2.0</version>
    <packaging>jar</packaging>
    
    <name>Math Utils Library</name>
    <description>高性能な数学計算ユーティリティライブラリ</description>
    <url>https://github.com/example/math-utils</url>
    
    <!-- ライセンス情報 -->
    <licenses>
        <license>
            <name>MIT License</name>
            <url>https://opensource.org/licenses/MIT</url>
            <distribution>repo</distribution>
        </license>
    </licenses>
    
    <!-- 開発者情報 -->
    <developers>
        <developer>
            <id>dev-team</id>
            <name>開発チーム</name>
            <email>dev@example.com</email>
            <organization>Example Corp</organization>
            <organizationUrl>https://example.com</organizationUrl>
        </developer>
    </developers>
    
    <!-- SCM情報 -->
    <scm>
        <connection>scm:git:git://github.com/example/math-utils.git</connection>
        <developerConnection>scm:git:ssh://github.com/example/math-utils.git</developerConnection>
        <url>https://github.com/example/math-utils/tree/main</url>
    </scm>
    
    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>
    
    <dependencies>
        <!-- テスト依存関係のみ -->
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <version>5.9.3</version>
            <scope>test</scope>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <!-- コンパイル -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
            </plugin>
            
            <!-- ソースJAR生成 -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-source-plugin</artifactId>
                <version>3.3.0</version>
                <executions>
                    <execution>
                        <id>attach-sources</id>
                        <goals>
                            <goal>jar-no-fork</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
            
            <!-- JavadocJAR生成 -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-javadoc-plugin</artifactId>
                <version>3.5.0</version>
                <executions>
                    <execution>
                        <id>attach-javadocs</id>
                        <goals>
                            <goal>jar</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
            
            <!-- GPG署名 (リリース時) -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-gpg-plugin</artifactId>
                <version>3.1.0</version>
                <executions>
                    <execution>
                        <id>sign-artifacts</id>
                        <phase>verify</phase>
                        <goals>
                            <goal>sign</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
            
            <!-- Nexusへのデプロイ -->
            <plugin>
                <groupId>org.sonatype.plugins</groupId>
                <artifactId>nexus-staging-maven-plugin</artifactId>
                <version>1.6.13</version>
                <extensions>true</extensions>
                <configuration>
                    <serverId>ossrh</serverId>
                    <nexusUrl>https://s01.oss.sonatype.org/</nexusUrl>
                    <autoReleaseAfterClose>false</autoReleaseAfterClose>
                </configuration>
            </plugin>
        </plugins>
    </build>
    
    <!-- 配布管理 -->
    <distributionManagement>
        <snapshotRepository>
            <id>ossrh</id>
            <url>https://s01.oss.sonatype.org/content/repositories/snapshots</url>
        </snapshotRepository>
        <repository>
            <id>ossrh</id>
            <url>https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/</url>
        </repository>
    </distributionManagement>
</project>

ライブラリのコア機能:

1. Statistics.java (統計計算):
/**
 * 統計計算を行うユーティリティクラスです。
 * 
 * <p>このクラスは高性能な統計計算機能を提供します：
 * <ul>
 *   <li>基本統計量（平均、分散、標準偏差）</li>
 *   <li>順序統計量（中央値、四分位数）</li>
 *   <li>相関・回帰分析</li>
 * </ul>
 * 
 * @author Math Utils Team
 * @version 1.2.0
 * @since 1.0.0
 */
public final class Statistics {
    
    private Statistics() {
        // ユーティリティクラスのため インスタンス化を防ぐ
    }
    
    /**
     * 配列の算術平均を計算します。
     * 
     * @param values 数値配列
     * @return 算術平均
     * @throws IllegalArgumentException 配列がnullまたは空の場合
     */
    public static double mean(double[] values) {
        validateArray(values);
        return Arrays.stream(values).average().orElse(0.0);
    }
    
    /**
     * 配列の分散を計算します（不偏分散）。
     * 
     * @param values 数値配列
     * @return 分散
     */
    public static double variance(double[] values) {
        validateArray(values);
        double mean = mean(values);
        return Arrays.stream(values)
                .map(x -> Math.pow(x - mean, 2))
                .average()
                .orElse(0.0);
    }
    
    /**
     * 配列の中央値を計算します。
     * 
     * @param values 数値配列
     * @return 中央値
     */
    public static double median(double[] values) {
        validateArray(values);
        double[] sorted = Arrays.copyOf(values, values.length);
        Arrays.sort(sorted);
        
        int n = sorted.length;
        if (n % 2 == 0) {
            return (sorted[n/2 - 1] + sorted[n/2]) / 2.0;
        } else {
            return sorted[n/2];
        }
    }
    
    private static void validateArray(double[] values) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("配列はnullまたは空であってはいけません");
        }
    }
}

2. Geometry.java (幾何学計算):
public final class Geometry {
    
    public static final double DEFAULT_PRECISION = 1e-10;
    
    /**
     * 2点間の距離を計算します。
     */
    public static double distance(Point2D p1, Point2D p2) {
        double dx = p2.getX() - p1.getX();
        double dy = p2.getY() - p1.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }
    
    /**
     * 円の面積を計算します。
     */
    public static double circleArea(double radius) {
        if (radius < 0) {
            throw new IllegalArgumentException("半径は0以上である必要があります");
        }
        return Math.PI * radius * radius;
    }
    
    /**
     * 三角形の面積を計算します（ヘロンの公式）。
     */
    public static double triangleArea(double a, double b, double c) {
        if (a <= 0 || b <= 0 || c <= 0) {
            throw new IllegalArgumentException("辺の長さは正の数である必要があります");
        }
        
        // 三角不等式のチェック
        if (a + b <= c || b + c <= a || c + a <= b) {
            throw new IllegalArgumentException("三角形の条件を満たしていません");
        }
        
        double s = (a + b + c) / 2.0; // 半周
        return Math.sqrt(s * (s - a) * (s - b) * (s - c));
    }
}

README.md (ライブラリドキュメント):
# Math Utils Library

高性能な数学計算ユーティリティライブラリ

## 特徴

- 🚀 高性能な統計計算
- 📐 幾何学的計算機能
- 🔧 使いやすいAPI設計
- 📚 充実したドキュメント
- ✅ 包括的なテストカバレッジ

## インストール

### Maven
```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>math-utils</artifactId>
    <version>1.2.0</version>
</dependency>
```

### Gradle
```gradle
implementation 'com.example:math-utils:1.2.0'
```

## 使用例

### 統計計算
```java
double[] data = {1.0, 2.0, 3.0, 4.0, 5.0};

double mean = Statistics.mean(data);        // 3.0
double variance = Statistics.variance(data); // 2.5
double median = Statistics.median(data);    // 3.0
```

### 幾何学計算
```java
// 円の面積
double area = Geometry.circleArea(5.0);     // 78.54

// 三角形の面積
double triangleArea = Geometry.triangleArea(3, 4, 5); // 6.0
```

## API ドキュメント

詳細なAPIドキュメントは [Javadoc](https://example.github.io/math-utils/) を参照してください。

## ライセンス

MIT License - 詳細は [LICENSE](LICENSE) ファイルを参照

ビルドとリリース:

# 開発用ビルド
mvn clean compile test

# パッケージング (JAR作成)
mvn clean package

# ソースとJavadoc JAR も生成
mvn clean package source:jar javadoc:jar

# ローカルリポジトリにインストール
mvn clean install

# リリース準備 (バージョン更新)
mvn versions:set -DnewVersion=1.3.0
mvn versions:commit

# Maven Central へのデプロイ
mvn clean deploy -P release

生成されるアーティファクト:
target/
├── math-utils-1.2.0.jar          # メインJAR
├── math-utils-1.2.0-sources.jar  # ソースJAR
├── math-utils-1.2.0-javadoc.jar  # JavadocJAR
├── math-utils-1.2.0.jar.asc      # GPG署名
├── math-utils-1.2.0-sources.jar.asc
└── math-utils-1.2.0-javadoc.jar.asc

品質メトリクス:
コードカバレッジ: 95.2%
Javadoc カバレッジ: 100%
依存関係: 0 (ランタイム)
JARサイズ: 45.2 KB
性能: Statistics.mean() - 1M要素/50ms

ライブラリ使用統計:
ダウンロード数: 15,247回
GitHub Stars: 234
依存プロジェクト: 67個
最新バージョン: 1.2.0
```

**評価ポイント**:
- 実用的なライブラリの設計と実装
- Maven Central対応の完全な設定
- 充実したドキュメントとメタデータ

## 💡 ヒント

### 課題1のヒント
- @param、@return、@throws タグは必須
- <pre>{@code ... }</pre> でサンプルコード埋め込み
- @since でバージョン情報管理

### 課題2のヒント
- 依存関係のスコープ(compile, test, provided)を適切に設定
- バージョンはプロパティで一元管理
- OWASP Dependency Check で脆弱性チェック

### 課題3のヒント
- @ApiOperation、@ApiModel でSwagger注釈
-例外ケースも含めた完全なレスポンス定義
- セキュリティ設定の文書化

### 課題4のヒント
- groupId は逆ドメイン形式で命名
- Maven Central の要件を満たす完全なメタデータ
- Semantic Versioning でバージョン管理

## 🔍 ドキュメントとライブラリのポイント

1. **Javadoc**: 完全で有用なAPIドキュメント作成
2. **依存関係管理**: 適切なライブラリ選択と設定
3. **API設計**: RESTful原則に基づく一貫したAPI
4. **パッケージング**: 再利用可能なライブラリ作成
5. **ドキュメント**: 開発者向けの充実した情報提供
6. **品質保証**: テスト・セキュリティ・パフォーマンス

## ✅ 完了チェックリスト

- [ ] 課題1: 包括的なJavadocドキュメントが作成できている
- [ ] 課題2: 外部ライブラリを適切に活用できている
- [ ] 課題3: APIドキュメントの自動生成システムが構築できている
- [ ] 課題4: 独自ライブラリの作成とパッケージングができている
- [ ] Maven/Gradleによる依存関係管理ができている
- [ ] プロフェッショナルレベルのドキュメント作成ができている

**次のステップ**: 基本課題が完了したら、advanced/の発展課題でより高度なライブラリ設計に挑戦しましょう！