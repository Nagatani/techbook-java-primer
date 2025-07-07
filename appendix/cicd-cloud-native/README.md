# CI/CD Cloud Native Microservice

A comprehensive Java microservice demonstrating CI/CD pipelines and cloud-native deployment patterns.

## 目次

1. [概要](#概要)
2. [なぜCI/CDとクラウドネイティブが必要なのか](#なぜcicdとクラウドネイティブが必要なのか)
3. [アーキテクチャ](#アーキテクチャ)
4. [技術スタック](#技術スタック)
5. [プロジェクト構成](#プロジェクト構成)
6. [セットアップ](#セットアップ)
7. [CI/CDパイプライン](#cicdパイプライン)
8. [コンテナ化](#コンテナ化)
9. [Kubernetesデプロイメント](#kubernetesデプロイメント)
10. [モニタリング](#モニタリング)
11. [12ファクターアプリの実装](#12ファクターアプリの実装)
12. [ベストプラクティス](#ベストプラクティス)

## 概要

このプロジェクトは、モダンなJavaアプリケーション開発におけるCI/CDパイプラインとクラウドネイティブな配布方法を実践的に示すものです。Spring Bootを使用したRESTful APIサービスを例に、開発から本番環境へのデプロイまでの完全な自動化を実現しています。

## なぜCI/CDとクラウドネイティブが必要なのか

### 手動デプロイの問題点

#### 1. 人的エラーのリスク
```bash
# 従来の手動デプロイの例
scp target/app.jar production:/opt/app/
ssh production "sudo systemctl restart app"
# → ファイルの転送ミス、サービス停止の長時間化などのリスク
```

#### 2. 環境差異による障害
- 開発環境: Java 17, PostgreSQL 14
- 本番環境: Java 11, PostgreSQL 10
- → 「開発では動くのに本番で動かない」問題

#### 3. ビジネスへの影響
- **デプロイ時間**: 手動3時間 → CI/CD 5分（36倍高速化）
- **エラー率**: 手動5-10% → 自動化0.1%（99%削減）
- **開発効率**: 自動化により3倍の生産性向上

### CI/CDがもたらす価値

1. **継続的品質保証**
   - すべてのコミットで自動テスト実行
   - コード品質の即時フィードバック
   - 早期のバグ発見と修正

2. **高速なフィードバックループ**
   - 開発からデプロイまで数分で完了
   - 1日に複数回の本番リリースが可能
   - 顧客要望への迅速な対応

3. **一貫性のある環境**
   - Dockerによる環境の標準化
   - 「どこでも同じように動く」保証
   - インフラのコード化（IaC）

## アーキテクチャ

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│   開発者    │────▶│   GitHub    │────▶│  GitHub     │
│             │     │             │     │  Actions    │
└─────────────┘     └─────────────┘     └─────┬───────┘
                                               │
                    ┌──────────────────────────┴───────────────────┐
                    │                                              │
                    ▼                                              ▼
            ┌─────────────┐                              ┌─────────────┐
            │   Docker    │                              │ Kubernetes  │
            │  Registry   │                              │  Cluster    │
            └─────────────┘                              └─────────────┘
                    │                                              │
                    └──────────────────────────────────────────────┘
```

## 技術スタック

- **言語**: Java 17
- **フレームワーク**: Spring Boot 3.2.0
- **データベース**: PostgreSQL 14
- **キャッシュ**: Redis 7
- **コンテナ**: Docker
- **オーケストレーション**: Kubernetes
- **CI/CD**: GitHub Actions, GitLab CI, Jenkins
- **モニタリング**: Prometheus, Grafana
- **トレーシング**: Zipkin

## プロジェクト構成

```
cicd-cloud-native/
├── src/
│   ├── main/
│   │   ├── java/com/example/
│   │   │   ├── controller/     # REST API エンドポイント
│   │   │   ├── service/        # ビジネスロジック
│   │   │   ├── repository/     # データアクセス層
│   │   │   ├── model/          # エンティティ
│   │   │   ├── dto/            # データ転送オブジェクト
│   │   │   ├── config/         # 設定クラス
│   │   │   └── exception/      # 例外処理
│   │   └── resources/
│   │       ├── application.yml # アプリケーション設定
│   │       └── db/migration/   # Flywayマイグレーション
│   └── test/                   # テストコード
├── .github/workflows/          # GitHub Actions設定
├── k8s/                        # Kubernetesマニフェスト
│   ├── base/                   # 基本設定
│   └── overlays/               # 環境別設定
├── helm/                       # Helm Chart
├── docker/                     # Docker関連ファイル
├── Dockerfile                  # マルチステージビルド
├── docker-compose.yml          # 開発環境
└── pom.xml                     # Maven設定
```

## セットアップ

### 前提条件

- Java 17以上
- Docker Desktop
- kubectl
- Maven 3.8以上

### ローカル開発環境の起動

```bash
# リポジトリのクローン
git clone https://github.com/myorg/cicd-cloud-native-app.git
cd cicd-cloud-native-app

# 開発環境の起動（PostgreSQL, Redis含む）
docker-compose up -d

# アプリケーションの起動
./mvnw spring-boot:run

# ブラウザでアクセス
open http://localhost:8080/swagger-ui.html
```

### テストの実行

```bash
# 単体テスト
./mvnw test

# 統合テスト（Testcontainersを使用）
./mvnw verify

# カバレッジレポート生成
./mvnw jacoco:report
open target/site/jacoco/index.html
```

## CI/CDパイプライン

### GitHub Actions

本プロジェクトでは、以下のステージを持つCI/CDパイプラインを実装しています：

1. **テスト**: 単体テストとコードカバレッジ
2. **静的解析**: SpotBugs, Checkstyle, SonarCloud
3. **ビルド**: JARファイルの生成
4. **Dockerイメージ**: マルチステージビルドとレジストリへのプッシュ
5. **デプロイ**: Kubernetesへの自動デプロイ

```yaml
# .github/workflows/ci.yml の主要部分
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Run tests
        run: mvn clean test
      - name: Upload coverage
        uses: codecov/codecov-action@v3
```

### GitLab CI

```yaml
# .gitlab-ci.yml の主要部分
stages:
  - build
  - test
  - package
  - deploy

test:integration:
  stage: test
  services:
    - postgres:14
    - redis:7
  script:
    - mvn verify -Pintegration-tests
```

### Jenkins Pipeline

Jenkinsfileを使用した宣言的パイプライン：

```groovy
pipeline {
    agent any
    stages {
        stage('Test') {
            parallel {
                stage('Unit Tests') {
                    steps {
                        sh 'mvn test'
                    }
                }
                stage('Static Analysis') {
                    steps {
                        sh 'mvn spotbugs:check'
                    }
                }
            }
        }
    }
}
```

## コンテナ化

### マルチステージDockerビルド

```dockerfile
# ビルドステージ
FROM maven:3.8-openjdk-17-slim AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn package -DskipTests

# 実行ステージ
FROM eclipse-temurin:17-jre-alpine
RUN addgroup -g 1000 -S appgroup && \
    adduser -u 1000 -S appuser -G appgroup
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
USER appuser
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### ヘルスチェックの実装

```yaml
# docker-compose.yml
healthcheck:
  test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
  interval: 30s
  timeout: 3s
  retries: 3
```

## Kubernetesデプロイメント

### 基本的なデプロイメント

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: app
spec:
  replicas: 3
  selector:
    matchLabels:
      app: cicd-app
  template:
    metadata:
      labels:
        app: cicd-app
    spec:
      containers:
      - name: app
        image: ghcr.io/myorg/cicd-cloud-native-app:latest
        ports:
        - containerPort: 8080
        livenessProbe:
          httpGet:
            path: /actuator/health/liveness
            port: 8080
          initialDelaySeconds: 60
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8080
          initialDelaySeconds: 30
```

### 自動スケーリング（HPA）

```yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: app-hpa
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: app
  minReplicas: 3
  maxReplicas: 10
  metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 70
```

### ConfigMapとSecretの管理

```yaml
# ConfigMap（設定情報）
apiVersion: v1
kind: ConfigMap
metadata:
  name: app-config
data:
  application.yaml: |
    server:
      port: 8080
    logging:
      level:
        root: INFO

# Secret（機密情報）
apiVersion: v1
kind: Secret
metadata:
  name: app-secrets
type: Opaque
stringData:
  database-password: changeme
```

### ローリングアップデート

```bash
# 新しいイメージでデプロイ
kubectl set image deployment/app app=ghcr.io/myorg/app:v2.0.0

# デプロイメントの状態確認
kubectl rollout status deployment/app

# 問題がある場合はロールバック
kubectl rollout undo deployment/app
```

### カナリーリリース

```yaml
# 10%のトラフィックを新バージョンへ
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  annotations:
    nginx.ingress.kubernetes.io/canary: "true"
    nginx.ingress.kubernetes.io/canary-weight: "10"
spec:
  rules:
  - host: app.example.com
    http:
      paths:
      - path: /
        backend:
          service:
            name: app-v2
            port:
              number: 80
```

## モニタリング

### Prometheusメトリクス

```java
// カスタムメトリクスの定義
@RestController
@Timed("user.controller")
public class UserController {
    
    private final MeterRegistry meterRegistry;
    
    @PostMapping("/users")
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO user) {
        // カスタムメトリクスの記録
        meterRegistry.counter("user.created").increment();
        return ResponseEntity.ok(userService.create(user));
    }
}
```

### アプリケーションメトリクス

- **リクエストレート**: `rate(http_server_requests_seconds_count[5m])`
- **レスポンスタイム**: `histogram_quantile(0.95, http_server_requests_seconds_bucket)`
- **エラー率**: `rate(http_server_requests_seconds_count{status=~"5.."}[5m])`

### ログ集約

```yaml
# Fluentd設定例
<source>
  @type tail
  path /var/log/containers/*.log
  pos_file /var/log/fluentd-containers.log.pos
  tag kubernetes.*
  <parse>
    @type json
    time_format %Y-%m-%dT%H:%M:%S.%NZ
  </parse>
</source>
```

## 12ファクターアプリの実装

### 1. コードベース
- Gitによるバージョン管理
- 単一のコードベースから複数環境へデプロイ

### 2. 依存関係
- Maven/Gradleによる明示的な依存関係宣言
- ビルド時に全依存関係を解決

### 3. 設定
```java
// 環境変数による設定
@Value("${DATABASE_URL}")
private String databaseUrl;

// Spring Cloud Configによる外部設定
@RefreshScope
@Configuration
public class AppConfig {
    // 設定の動的更新が可能
}
```

### 4. バックエンドサービス
```yaml
# 環境変数でサービスを指定
DATABASE_URL: jdbc:postgresql://db:5432/app
REDIS_URL: redis://cache:6379
```

### 5. ビルド、リリース、実行
- CI/CDパイプラインによる完全分離
- Dockerイメージによる成果物管理

### 6. プロセス
- ステートレスな設計
- セッション情報はRedisに保存

### 7. ポートバインディング
```java
// 自己完結型サービス
server.port=${PORT:8080}
```

### 8. 並行性
- Kubernetesによる水平スケーリング
- プロセスモデルによる並行性

### 9. 廃棄容易性
```java
// グレースフルシャットダウン
@PreDestroy
public void onShutdown() {
    // クリーンアップ処理
}
```

### 10. 開発/本番環境の一致
- Dockerによる環境の統一
- docker-composeで本番環境を再現

### 11. ログ
```java
// 標準出力へのログ出力
log.info("User created: {}", userId);
// ログは外部システムで集約
```

### 12. 管理プロセス
```bash
# ワンオフタスクの実行
kubectl run --rm -it admin-task --image=app:latest -- java -jar app.jar --task=migrate
```

## ベストプラクティス

### 1. セキュリティ
- 非rootユーザーでコンテナ実行
- シークレットの適切な管理
- 最小権限の原則

### 2. 可観測性
- 構造化ログ
- 分散トレーシング
- メトリクスの収集

### 3. レジリエンス
- サーキットブレーカーパターン
- リトライとタイムアウト
- ヘルスチェック

### 4. パフォーマンス
- JVMチューニング
- コネクションプーリング
- キャッシュ戦略

## トラブルシューティング

### よくある問題

1. **Podが起動しない**
```bash
kubectl describe pod <pod-name>
kubectl logs <pod-name>
```

2. **データベース接続エラー**
```bash
# 環境変数の確認
kubectl exec <pod-name> -- env | grep DATABASE
```

3. **メモリ不足**
```yaml
# リソース制限の調整
resources:
  requests:
    memory: "1Gi"
  limits:
    memory: "2Gi"
```

## 参考資料

- [12-Factor App](https://12factor.net/)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Kubernetes Documentation](https://kubernetes.io/docs/)
- [Docker Best Practices](https://docs.docker.com/develop/dev-best-practices/)

## ライセンス

このプロジェクトはMITライセンスの下で公開されています。