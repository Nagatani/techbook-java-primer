# 第15章 応用課題

## 🎯 学習目標
- 高度なJavaDoc活用
- 自動ドキュメント生成システム
- API仕様書自動作成
- コードから設計書の逆生成
- インタラクティブドキュメント

## 📝 課題一覧

### 課題1: 自動API仕様書生成システム
**ファイル名**: `AutoAPIDocumentationGenerator.java`

コードから包括的なAPI仕様書を自動生成するシステムを作成してください。

**要求仕様**:
- REST API仕様の自動抽出
- OpenAPI 3.0形式出力
- インタラクティブデモ生成
- 多言語ドキュメント対応

**生成機能**:
- エンドポイント仕様
- データモデル定義
- サンプルコード
- テストケース

**実行例**:
```
=== 自動API仕様書生成システム ===

📚 AutoDocGen Pro v3.0

=== プロジェクト解析 ===
🔍 包括的コード分析:

解析対象:
プロジェクト: E-Commerce API
言語: Java + Spring Boot
ファイル数: 247個
クラス数: 156個
メソッド数: 1,247個
アノテーション: @RestController, @RequestMapping等

解析結果:
REST エンドポイント: 67個
データモデル: 23個
セキュリティ設定: JWT + OAuth2
バリデーション: Bean Validation
例外処理: 15種類の例外クラス

解析時間: 8.7秒
解析精度: 98.3%

=== API仕様抽出 ===
📋 REST API分析:

```java
/**
 * ユーザー管理API
 * ユーザーの作成、取得、更新、削除を行う
 * 
 * @author API Generator
 * @version 1.0.0
 * @since 2024-07-05
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "ユーザー管理", description = "ユーザー関連のAPI操作")
public class UserController {
    
    /**
     * ユーザー一覧取得
     * 
     * @param page ページ番号 (0から開始, デフォルト: 0)
     * @param size ページサイズ (1-100, デフォルト: 20)
     * @param sort ソート条件 (例: "name,asc" or "createdAt,desc")
     * @param search 検索キーワード (部分一致)
     * @return ページング化されたユーザー一覧
     * 
     * @apiNote このAPIはページングと検索をサポートします
     * @apiExample
     * GET /api/users?page=0&size=10&sort=name,asc&search=john
     * 
     * @apiSuccess 200 正常にユーザー一覧を取得
     * @apiError 400 不正なパラメータ
     * @apiError 401 認証が必要
     * @apiError 403 権限不足
     */
    @GetMapping
    @Operation(
        summary = "ユーザー一覧取得",
        description = "条件に応じてユーザー一覧を取得します。ページング、ソート、検索に対応。",
        responses = {
            @ApiResponse(responseCode = "200", description = "成功",
                content = @Content(schema = @Schema(implementation = PagedUserResponse.class))),
            @ApiResponse(responseCode = "400", description = "パラメータエラー"),
            @ApiResponse(responseCode = "401", description = "認証エラー"),
            @ApiResponse(responseCode = "403", description = "権限エラー")
        }
    )
    public ResponseEntity<PagedResponse<UserDTO>> getUsers(
            @Parameter(description = "ページ番号", example = "0")
            @RequestParam(defaultValue = "0") @Min(0) Integer page,
            
            @Parameter(description = "ページサイズ", example = "20") 
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) Integer size,
            
            @Parameter(description = "ソート条件", example = "name,asc")
            @RequestParam(required = false) String sort,
            
            @Parameter(description = "検索キーワード", example = "john")
            @RequestParam(required = false) String search) {
        
        // 実装...
    }
    
    /**
     * ユーザー作成
     * 
     * @param request ユーザー作成リクエスト
     * @return 作成されたユーザー情報
     * 
     * @apiNote 
     * - ユーザー名は一意である必要があります
     * - メールアドレスは有効な形式である必要があります
     * - パスワードは8文字以上で英数字記号を含む必要があります
     * 
     * @apiExample
     * POST /api/users
     * Content-Type: application/json
     * 
     * {
     *   "username": "john_doe",
     *   "email": "john@example.com",
     *   "password": "SecurePass123!",
     *   "profile": {
     *     "firstName": "John",
     *     "lastName": "Doe",
     *     "phoneNumber": "+81-90-1234-5678"
     *   }
     * }
     * 
     * @apiSuccessExample 成功レスポンス
     * HTTP/1.1 201 Created
     * Content-Type: application/json
     * 
     * {
     *   "id": 123,
     *   "username": "john_doe", 
     *   "email": "john@example.com",
     *   "profile": {
     *     "firstName": "John",
     *     "lastName": "Doe",
     *     "phoneNumber": "+81-90-1234-5678"
     *   },
     *   "createdAt": "2024-07-05T14:30:00Z",
     *   "updatedAt": "2024-07-05T14:30:00Z"
     * }
     */
    @PostMapping
    @Operation(summary = "ユーザー作成", description = "新しいユーザーを作成します")
    public ResponseEntity<UserDTO> createUser(
            @Valid @RequestBody CreateUserRequest request) {
        // 実装...
    }
}

// 自動生成されたOpenAPI仕様
@Service
public class OpenAPISpecGenerator {
    
    public OpenAPISpecification generateSpecification(List<Class<?>> controllerClasses) {
        OpenAPISpecification spec = new OpenAPISpecification();
        
        // 基本情報設定
        spec.setInfo(Info.builder()
            .title("E-Commerce API")
            .version("1.0.0")
            .description("ECサイト用のREST API仕様書")
            .contact(Contact.builder()
                .name("開発チーム")
                .email("dev@example.com")
                .build())
            .license(License.builder()
                .name("MIT")
                .url("https://opensource.org/licenses/MIT")
                .build())
            .build());
        
        // サーバー情報
        spec.setServers(Arrays.asList(
            Server.builder()
                .url("https://api.example.com/v1")
                .description("本番環境")
                .build(),
            Server.builder()
                .url("https://api-staging.example.com/v1") 
                .description("ステージング環境")
                .build()
        ));
        
        // セキュリティ設定
        spec.setSecuritySchemes(Map.of(
            "BearerAuth", SecurityScheme.builder()
                .type("http")
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("JWT認証")
                .build(),
            "OAuth2", SecurityScheme.builder()
                .type("oauth2")
                .description("OAuth2認証")
                .flows(OAuthFlows.builder()
                    .authorizationCode(OAuthFlow.builder()
                        .authorizationUrl("https://auth.example.com/oauth2/authorize")
                        .tokenUrl("https://auth.example.com/oauth2/token")
                        .scopes(Map.of(
                            "read", "読み取り権限",
                            "write", "書き込み権限",
                            "admin", "管理者権限"
                        ))
                        .build())
                    .build())
                .build()
        ));
        
        // エンドポイント解析
        for (Class<?> controllerClass : controllerClasses) {
            PathItemMap pathItems = analyzeControllerClass(controllerClass);
            spec.addPathItems(pathItems);
        }
        
        return spec;
    }
    
    private PathItemMap analyzeControllerClass(Class<?> controllerClass) {
        PathItemMap pathItems = new PathItemMap();
        
        // クラスレベルのRequestMapping取得
        RequestMapping classMapping = controllerClass.getAnnotation(RequestMapping.class);
        String basePath = classMapping != null ? classMapping.value()[0] : "";
        
        // メソッド解析
        for (Method method : controllerClass.getDeclaredMethods()) {
            if (isApiMethod(method)) {
                PathItem pathItem = analyzeApiMethod(method, basePath);
                pathItems.put(pathItem.getPath(), pathItem);
            }
        }
        
        return pathItems;
    }
    
    private PathItem analyzeApiMethod(Method method, String basePath) {
        // HTTPメソッドとパス抽出
        RequestMapping mapping = method.getAnnotation(RequestMapping.class);
        GetMapping getMapping = method.getAnnotation(GetMapping.class);
        PostMapping postMapping = method.getAnnotation(PostMapping.class);
        
        String httpMethod = determineHttpMethod(method);
        String path = determinePath(method, basePath);
        
        //操作定義生成
        Operation operation = Operation.builder()
            .operationId(method.getName())
            .summary(extractSummary(method))
            .description(extractDescription(method))
            .tags(extractTags(method))
            .parameters(extractParameters(method))
            .requestBody(extractRequestBody(method))
            .responses(extractResponses(method))
            .security(extractSecurityRequirements(method))
            .build();
        
        return PathItem.builder()
            .path(path)
            .operation(httpMethod, operation)
            .build();
    }
}
```

自動生成結果:
```yaml
openapi: 3.0.0
info:
  title: E-Commerce API
  version: 1.0.0
  description: ECサイト用のREST API仕様書
  contact:
    name: 開発チーム
    email: dev@example.com
  license:
    name: MIT
    url: https://opensource.org/licenses/MIT

servers:
  - url: https://api.example.com/v1
    description: 本番環境
  - url: https://api-staging.example.com/v1
    description: ステージング環境

paths:
  /api/users:
    get:
      summary: ユーザー一覧取得
      description: 条件に応じてユーザー一覧を取得します。ページング、ソート、検索に対応。
      operationId: getUsers
      tags:
        - ユーザー管理
      parameters:
        - name: page
          in: query
          description: ページ番号
          required: false
          schema:
            type: integer
            minimum: 0
            default: 0
          example: 0
        - name: size
          in: query
          description: ページサイズ
          required: false
          schema:
            type: integer
            minimum: 1
            maximum: 100
            default: 20
          example: 20
      responses:
        '200':
          description: 成功
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/PagedUserResponse'
        '400':
          description: パラメータエラー
        '401':
          description: 認証エラー
        '403':
          description: 権限エラー
      security:
        - BearerAuth: []

    post:
      summary: ユーザー作成
      description: 新しいユーザーを作成します
      operationId: createUser
      tags:
        - ユーザー管理
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/CreateUserRequest'
            example:
              username: john_doe
              email: john@example.com
              password: SecurePass123!
              profile:
                firstName: John
                lastName: Doe
                phoneNumber: "+81-90-1234-5678"
      responses:
        '201':
          description: 作成成功
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/UserDTO'

components:
  schemas:
    UserDTO:
      type: object
      required:
        - id
        - username
        - email
      properties:
        id:
          type: integer
          format: int64
          description: ユーザーID
          example: 123
        username:
          type: string
          description: ユーザー名
          example: john_doe
        email:
          type: string
          format: email
          description: メールアドレス
          example: john@example.com

  securitySchemes:
    BearerAuth:
      type: http
      scheme: bearer
      bearerFormat: JWT
      description: JWT認証

生成統計:
解析エンドポイント: 67個
生成スキーマ: 23個
セキュリティ定義: 2個
サンプルコード: 134個
生成時間: 12.3秒
```

=== インタラクティブドキュメント生成 ===
```java
// Swagger UI + カスタムデモ生成
@Service
public class InteractiveDocumentationGenerator {
    
    public InteractiveDocumentation generateInteractiveDoc(OpenAPISpecification spec) {
        InteractiveDocumentation doc = new InteractiveDocumentation();
        
        // Swagger UI設定
        SwaggerUIConfig swaggerConfig = SwaggerUIConfig.builder()
            .url("/api-docs/openapi.json")
            .deepLinking(true)
            .displayOperationId(false)
            .defaultModelsExpandDepth(1)
            .defaultModelExpandDepth(1)
            .defaultModelRendering("example")
            .displayRequestDuration(true)
            .docExpansion("none")
            .filter(false)
            .maxDisplayedTags(50)
            .showExtensions(false)
            .showCommonExtensions(false)
            .tryItOutEnabled(true)
            .requestInterceptor("(request) => { " +
                "request.headers['X-API-Key'] = 'demo-key'; return request; }")
            .build();
        
        doc.setSwaggerUI(swaggerConfig);
        
        // カスタムデモページ生成
        DemoPageGenerator demoGenerator = new DemoPageGenerator();
        
        for (PathItem path : spec.getPaths()) {
            for (Operation operation : path.getOperations()) {
                // インタラクティブサンプル生成
                InteractiveSample sample = generateInteractiveSample(operation);
                doc.addSample(sample);
                
                // curl コマンド生成
                CurlCommand curlCommand = generateCurlCommand(operation);
                doc.addCurlCommand(curlCommand);
                
                // 各言語のサンプルコード生成
                CodeSamples samples = generateCodeSamples(operation);
                doc.addCodeSamples(samples);
            }
        }
        
        return doc;
    }
    
    private CodeSamples generateCodeSamples(Operation operation) {
        CodeSamples samples = new CodeSamples();
        
        // JavaScript (fetch API)
        String jsCode = generateJavaScriptSample(operation);
        samples.addSample("JavaScript", jsCode);
        
        // Python (requests)
        String pythonCode = generatePythonSample(operation);
        samples.addSample("Python", pythonCode);
        
        // Java (OkHttp)
        String javaCode = generateJavaSample(operation);
        samples.addSample("Java", javaCode);
        
        // curl
        String curlCode = generateCurlSample(operation);
        samples.addSample("curl", curlCode);
        
        return samples;
    }
    
    private String generateJavaScriptSample(Operation operation) {
        StringBuilder code = new StringBuilder();
        
        code.append("// JavaScript (fetch API)\n");
        code.append("const response = await fetch('").append(operation.getUrl()).append("', {\n");
        code.append("  method: '").append(operation.getHttpMethod().toUpperCase()).append("',\n");
        code.append("  headers: {\n");
        code.append("    'Content-Type': 'application/json',\n");
        code.append("    'Authorization': 'Bearer YOUR_JWT_TOKEN'\n");
        code.append("  }");
        
        if (operation.hasRequestBody()) {
            code.append(",\n  body: JSON.stringify({\n");
            code.append(generateRequestBodyExample(operation.getRequestBody()));
            code.append("\n  })");
        }
        
        code.append("\n});\n\n");
        code.append("if (response.ok) {\n");
        code.append("  const data = await response.json();\n");
        code.append("  console.log(data);\n");
        code.append("} else {\n");
        code.append("  console.error('Error:', response.status);\n");
        code.append("}");
        
        return code.toString();
    }
}
```

インタラクティブドキュメント例:
```html
<!DOCTYPE html>
<html>
<head>
    <title>E-Commerce API Documentation</title>
    <style>
        .api-demo { border: 1px solid #ccc; margin: 20px 0; padding: 15px; }
        .code-sample { background: #f4f4f4; padding: 10px; margin: 10px 0; }
        .try-it-button { background: #007bff; color: white; padding: 10px 20px; }
    </style>
</head>
<body>
    <h1>E-Commerce API Documentation</h1>
    
    <div class="api-demo">
        <h2>GET /api/users - ユーザー一覧取得</h2>
        <p>条件に応じてユーザー一覧を取得します。ページング、ソート、検索に対応。</p>
        
        <h3>パラメータ</h3>
        <form id="getUsersForm">
            <label>Page: <input type="number" name="page" value="0" min="0"></label><br>
            <label>Size: <input type="number" name="size" value="20" min="1" max="100"></label><br>
            <label>Search: <input type="text" name="search" placeholder="検索キーワード"></label><br>
            <button type="submit" class="try-it-button">Try it out!</button>
        </form>
        
        <h3>レスポンス例</h3>
        <div class="code-sample">
            <pre id="getUsersResponse">ここにレスポンスが表示されます</pre>
        </div>
        
        <h3>サンプルコード</h3>
        <div class="code-tabs">
            <button onclick="showCode('js')">JavaScript</button>
            <button onclick="showCode('python')">Python</button>
            <button onclick="showCode('java')">Java</button>
            <button onclick="showCode('curl')">curl</button>
        </div>
        
        <div id="js-code" class="code-sample">
            <pre>
const response = await fetch('/api/users?page=0&size=20', {
  method: 'GET',
  headers: {
    'Authorization': 'Bearer YOUR_JWT_TOKEN'
  }
});

if (response.ok) {
  const users = await response.json();
  console.log(users);
}
            </pre>
        </div>
    </div>
    
    <script>
        document.getElementById('getUsersForm').addEventListener('submit', async (e) => {
            e.preventDefault();
            const formData = new FormData(e.target);
            const params = new URLSearchParams(formData);
            
            try {
                const response = await fetch(`/api/users?${params}`);
                const data = await response.json();
                document.getElementById('getUsersResponse').textContent = 
                    JSON.stringify(data, null, 2);
            } catch (error) {
                document.getElementById('getUsersResponse').textContent = 
                    'Error: ' + error.message;
            }
        });
    </script>
</body>
</html>
```
```

### 課題2: コードメトリクス・品質レポートシステム
**ファイル名**: `CodeQualityReportSystem.java`

コードの品質指標を自動計算してレポートを生成するシステムを作成してください。

**要求仕様**:
- 複雑度計算 (循環的複雑度等)
- テストカバレッジ分析
- 技術的負債の可視化
- トレンド分析

**レポート機能**:
- HTMLレポート生成
- ダッシュボード表示
- アラート機能
- 改善提案

**実行例**:
```
=== コードメトリクス・品質レポートシステム ===

📊 CodeQuality Analyzer v2.0

=== プロジェクト品質分析 ===
📈 包括的品質評価:

分析対象:
プロジェクト名: E-Commerce Backend
言語: Java 17
フレームワーク: Spring Boot 3.0
総行数: 125,847行
ファイル数: 1,247個
クラス数: 892個
メソッド数: 4,567個

分析期間: 2024年1月〜7月 (6ヶ月間)
分析時刻: 2024-07-05 14:30:00

=== 複雑度分析 ===
🔍 コード複雑度評価:

循環的複雑度 (Cyclomatic Complexity):
平均: 3.2 (良好)
最大: 15 (UserService.validateUser)
警告レベル: 10以上 (12メソッド)
危険レベル: 15以上 (3メソッド)

認知的複雑度 (Cognitive Complexity):
平均: 4.1 (普通)
最大: 23 (OrderProcessor.processComplexOrder)
警告レベル: 15以上 (8メソッド)

ネスト深度:
平均: 2.1層
最大: 6層 (PaymentValidator.validateCreditCard)
推奨: 3層以下

メソッド長:
平均: 15.7行
最大: 89行 (ReportGenerator.generateMonthlyReport)
推奨: 20行以下

クラスサイズ:
平均: 142行
最大: 567行 (OrderManagementService)
推奨: 200行以下
```

### 課題3: 設計書逆生成システム
**ファイル名**: `DesignDocumentReverseGenerator.java`

既存のコードから設計書やアーキテクチャ図を自動生成するシステムを作成してください。

**要求仕様**:
- UMLクラス図生成
- シーケンス図自動作成
- アーキテクチャ図生成
- データフロー図作成

**実行例**:
```
=== 設計書逆生成システム ===

🏗️ ReverseDesignGen v4.0

=== アーキテクチャ分析 ===
🔧 システム構造解析:

レイヤー構成:
1. Presentation Layer (Controller)
   - 23個のRESTコントローラー
   - 145個のエンドポイント
   
2. Application Layer (Service)
   - 67個のサービスクラス
   - 234個のビジネスメソッド
   
3. Domain Layer (Entity/Domain)
   - 45個のエンティティ
   - 12個のドメインサービス
   
4. Infrastructure Layer (Repository/External)
   - 45個のリポジトリ
   - 23個の外部API連携

依存関係:
- 単方向依存: 98.7% (推奨)
- 循環依存: 3件 (要修正)
- 結合度: 低 (Good)
- 凝集度: 高 (Excellent)

生成される設計書:
- システム構成図
- クラス関係図  
- データフロー図
- シーケンス図
- ER図
```

## 🎯 習得すべき技術要素

### ドキュメント生成技術
- JavaDoc API
- アノテーション処理器
- AST (Abstract Syntax Tree) 解析
- コード静的解析

### API仕様書標準
- OpenAPI 3.0 仕様
- JSON Schema
- Swagger/OpenAPI ツールチェーン
- REST API設計原則

### コード品質分析
- 循環的複雑度
- 認知的複雑度
- コードカバレッジ
- 技術的負債測定

## 📚 参考リソース

- OpenAPI Specification 3.0
- JavaDoc Tool Reference
- Clean Code (Robert C. Martin)
- Code Quality Metrics (SonarQube Documentation)

## ⚠️ 重要な注意事項

自動生成されたドキュメントは開発者による確認と調整が必要です。生成されたドキュメントをそのまま使用せず、適切な品質管理を行ってください。