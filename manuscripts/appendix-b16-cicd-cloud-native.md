# 付録B.16: CI/CDパイプラインとクラウドネイティブ配布

## 概要

本付録では、モダンなソフトウェア開発に欠かせないCI/CD（継続的インテグレーション/継続的デリバリー）パイプラインの構築と、クラウドネイティブなアプリケーション配布について詳細に解説します。Docker化、Kubernetes、GraalVM native-imageなど、最新の技術を活用した配布戦略を学びます。

**対象読者**: 基本的なビルドプロセスを理解し、自動化とクラウド配布に興味がある開発者  
**前提知識**: 第22章「ビルドとデプロイ」の内容、基本的なGitの知識  
**関連章**: 第22章、第20章（テスト）

## なぜCI/CDとクラウドネイティブが重要なのか

### 手動デプロイの問題と解決

**問題1: 手動デプロイによる人的エラー**
```bash
# 従来の手動デプロイ：エラーが発生しやすい
scp target/myapp.jar production-server:/opt/myapp/
ssh production-server "sudo systemctl stop myapp"
ssh production-server "sudo systemctl start myapp"

# 問題:
# - ファイル転送エラーの見落とし
# - サービス停止時間の長期化
# - ロールバック手順の複雑さ
# - 環境差異による設定ミス
```
**実際の影響**: 某企業で手動デプロイミスにより3時間のサービス停止

**問題2: 環境の不整合**
```java
// 開発環境では動作するが本番環境で失敗
public class ConfigurationDifference {
    // 開発環境: Java 11, PostgreSQL 12
    // 本番環境: Java 8, PostgreSQL 10 ← バージョン違い
    
    public void processData() {
        // Java 11の新機能を使用
        String result = String.isBlank(input) ? "empty" : input;
        // 本番環境でコンパイルエラー
    }
}
```
**影響**: 「開発環境では動くのに本番環境では動かない」問題

### ビジネスへの深刻な影響

**実際の障害事例**

手動デプロイによる障害は、多くの組織で深刻な問題となっています。某Webサービスでは、手動デプロイによりライブラリの不整合が発生し、24時間のサービス停止に至りました。ECサイトでは、繁忙期の緊急修正でCI/CDを使用せずに手動デプロイを行った結果、顧客データが破損する事態となりました。金融システムでは、本番環境での手動設定ミスにより取引処理が停止し、大きな社会的影響を与えました。

**手動運用がもたらすコスト**

手動でのデプロイ運用は、組織に重大なコストをもたらします。開発効率の面では、デプロイ作業に1日3時間を要し、実際の開発時間が50%減少します。品質リスクとしては、手動エラー率が5-10%と高く、顧客への影響が頻発します。運用負荷では、深夜や休日のデプロイ作業により担当者の負担が増大し、結果として離職率の上昇を招くこともあります。

**CI/CDとクラウドネイティブがもたらす効果**

CI/CDとクラウドネイティブアプローチの導入により、これらの問題は劇的に改善されます。デプロイ時間については、従来の3時間から5分に短縮され、36倍の高速化を実現できます。エラー率は人的ミスを根絶することで99.9%の成功率を達成できます。開発効率では、自動化により開発に集中できるようになり、生産性を3倍向上させることができます。運用コストについては、インフラの自動化により70%の削減が可能です。

**具体的な改善事例**

多くの組織がCI/CDとクラウドネイティブの導入により顕著な成果を上げています。あるスタートアップでは、GitHub Actionsの導入により1日10回のデプロイが可能となり、アジャイルな開発サイクルを実現しました。大規模Webサービスでは、Kubernetesの導入によりトラフィック増加に自動対応できるようになり、安定したサービス提供を実現しました。モバイルアプリ開発では、Native Imageの採用により起動時間を95%短縮し、ユーザー体験を大幅に向上させました。

**投資対効果**

CI/CDとクラウドネイティブへの投資は、短期間で大きなリターンをもたらします。初期投資としては、CI/CD構築に1週間、インフラ整備に2週間程度が必要です。しかし、継続的な効果として年間運用コストを50%削減し、開発速度を2倍向上させることができます。また、本番障害を90%削減することで、ビジネスの継続性を確保できます。

---

## CI/CDパイプラインの基礎

### GitHub Actionsによる自動化

```yaml
# .github/workflows/ci.yml
name: CI/CD Pipeline

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]
  release:
    types: [ created ]

env:
  JAVA_VERSION: '17'
  DOCKER_REGISTRY: ghcr.io
  IMAGE_NAME: ${{ github.repository }}

jobs:
  # ステージ1: ビルドとテスト
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      
      - name: Set up JDK
        uses: actions/setup-java@v3
        with:
          java-version: ${{ env.JAVA_VERSION }}
          distribution: 'temurin'
          cache: maven
      
      - name: Run tests
        run: mvn clean test
      
      - name: Generate test report
        uses: dorny/test-reporter@v1
        if: success() || failure()
        with:
          name: Maven Tests
          path: target/surefire-reports/*.xml
          reporter: java-junit
      
      - name: Code coverage
        run: mvn jacoco:report
      
      - name: Upload coverage to Codecov
        uses: codecov/codecov-action@v3
        with:
          file: target/site/jacoco/jacoco.xml

  # ステージ2: 静的解析
  analysis:
    runs-on: ubuntu-latest
    needs: test
    steps:
      - uses: actions/checkout@v3
      
      - name: Run SpotBugs
        run: mvn spotbugs:check
      
      - name: Run Checkstyle
        run: mvn checkstyle:check
      
      - name: SonarCloud Scan
        uses: SonarSource/sonarcloud-github-action@master
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
          SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}

  # ステージ3: ビルドアーティファクト
  build:
    runs-on: ubuntu-latest
    needs: [test, analysis]
    steps:
      - uses: actions/checkout@v3
      
      - name: Set up JDK
        uses: actions/setup-java@v3
        with:
          java-version: ${{ env.JAVA_VERSION }}
          distribution: 'temurin'
      
      - name: Build application
        run: mvn clean package -DskipTests
      
      - name: Upload artifacts
        uses: actions/upload-artifact@v3
        with:
          name: app-artifacts
          path: target/*.jar
          retention-days: 7

  # ステージ4: Dockerイメージビルド
  docker:
    runs-on: ubuntu-latest
    needs: build
    if: github.event_name != 'pull_request'
    steps:
      - uses: actions/checkout@v3
      
      - name: Download artifacts
        uses: actions/download-artifact@v3
        with:
          name: app-artifacts
          path: target
      
      - name: Set up Docker Buildx
        uses: docker/setup-buildx-action@v2
      
      - name: Log in to Container Registry
        uses: docker/login-action@v2
        with:
          registry: ${{ env.DOCKER_REGISTRY }}
          username: ${{ github.actor }}
          password: ${{ secrets.GITHUB_TOKEN }}
      
      - name: Extract metadata
        id: meta
        uses: docker/metadata-action@v4
        with:
          images: ${{ env.DOCKER_REGISTRY }}/${{ env.IMAGE_NAME }}
          tags: |
            type=ref,event=branch
            type=ref,event=pr
            type=semver,pattern={{version}}
            type=semver,pattern={{major}}.{{minor}}
            type=sha
      
      - name: Build and push Docker image
        uses: docker/build-push-action@v4
        with:
          context: .
          push: true
          tags: ${{ steps.meta.outputs.tags }}
          labels: ${{ steps.meta.outputs.labels }}
          cache-from: type=gha
          cache-to: type=gha,mode=max
```

### GitLab CI/CDパイプライン

```yaml
# .gitlab-ci.yml
stages:
  - build
  - test
  - package
  - deploy

variables:
  MAVEN_OPTS: "-Dmaven.repo.local=$CI_PROJECT_DIR/.m2/repository"
  DOCKER_DRIVER: overlay2
  DOCKER_TLS_CERTDIR: "/certs"

cache:
  paths:
    - .m2/repository/
    - target/

# ビルドジョブ
build:
  stage: build
  image: maven:3.8-openjdk-17
  script:
    - mvn compile
  artifacts:
    paths:
      - target/
    expire_in: 1 hour

# テストジョブ
test:unit:
  stage: test
  image: maven:3.8-openjdk-17
  script:
    - mvn test
  artifacts:
    reports:
      junit:
        - target/surefire-reports/TEST-*.xml
    paths:
      - target/site/jacoco/
    expire_in: 1 week

test:integration:
  stage: test
  image: maven:3.8-openjdk-17
  services:
    - postgres:14
    - redis:7
  variables:
    POSTGRES_DB: testdb
    POSTGRES_USER: test
    POSTGRES_PASSWORD: test
    DATABASE_URL: "jdbc:postgresql://postgres:5432/testdb"
  script:
    - mvn verify -Pintegration-tests

# パッケージジョブ
package:jar:
  stage: package
  image: maven:3.8-openjdk-17
  script:
    - mvn package -DskipTests
  artifacts:
    paths:
      - target/*.jar
    expire_in: 1 week

package:docker:
  stage: package
  image: docker:latest
  services:
    - docker:dind
  before_script:
    - docker login -u $CI_REGISTRY_USER -p $CI_REGISTRY_PASSWORD $CI_REGISTRY
  script:
    - docker build -t $CI_REGISTRY_IMAGE:$CI_COMMIT_SHA .
    - docker tag $CI_REGISTRY_IMAGE:$CI_COMMIT_SHA $CI_REGISTRY_IMAGE:latest
    - docker push $CI_REGISTRY_IMAGE:$CI_COMMIT_SHA
    - docker push $CI_REGISTRY_IMAGE:latest
  only:
    - main

# デプロイジョブ
deploy:staging:
  stage: deploy
  image: bitnami/kubectl:latest
  script:
    - kubectl set image deployment/app app=$CI_REGISTRY_IMAGE:$CI_COMMIT_SHA -n staging
  environment:
    name: staging
    url: https://staging.example.com
  only:
    - main

deploy:production:
  stage: deploy
  image: bitnami/kubectl:latest
  script:
    - kubectl set image deployment/app app=$CI_REGISTRY_IMAGE:$CI_COMMIT_SHA -n production
  environment:
    name: production
    url: https://app.example.com
  when: manual
  only:
    - tags
```

---

## Dockerコンテナ化

### マルチステージビルド

```dockerfile
# Dockerfile
# ステージ1: ビルド環境
FROM maven:3.8-openjdk-17-slim AS builder

WORKDIR /app

# 依存関係のキャッシュ最適化
COPY pom.xml .
RUN mvn dependency:go-offline -B

# ソースコードのコピーとビルド
COPY src ./src
RUN mvn package -DskipTests

# ステージ2: 実行環境
FROM eclipse-temurin:17-jre-alpine

# セキュリティ: 非rootユーザーの作成
RUN addgroup -g 1000 -S appgroup && \
    adduser -u 1000 -S appuser -G appgroup

# 必要なパッケージのインストール
RUN apk add --no-cache \
    curl \
    jq \
    && rm -rf /var/cache/apk/*

WORKDIR /app

# JARファイルのコピー
COPY --from=builder /app/target/*.jar app.jar

# 設定ファイルとスクリプト
COPY docker-entrypoint.sh /
RUN chmod +x /docker-entrypoint.sh

# 所有権の設定
RUN chown -R appuser:appgroup /app

USER appuser

# ヘルスチェック
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# JVMオプションの設定
ENV JAVA_OPTS="-XX:+UseContainerSupport \
    -XX:MaxRAMPercentage=75.0 \
    -XX:+UseG1GC \
    -XX:+UseStringDeduplication \
    -Djava.security.egd=file:/dev/./urandom"

EXPOSE 8080

ENTRYPOINT ["/docker-entrypoint.sh"]
```

### docker-entrypoint.sh

```bash
#!/bin/sh
set -e

# 環境変数の展開
export JAVA_OPTS="${JAVA_OPTS} ${JAVA_OPTS_APPEND}"

# アプリケーション設定の動的生成
if [ -n "$CONFIG_SERVER_URL" ]; then
    echo "Fetching configuration from $CONFIG_SERVER_URL"
    curl -sf "$CONFIG_SERVER_URL" > application.properties
fi

# シークレットのマウント確認
if [ -d "/run/secrets" ]; then
    for secret in /run/secrets/*; do
        if [ -f "$secret" ]; then
            secret_name=$(basename "$secret" | tr '[:lower:]' '[:upper:]')
            export "$secret_name"=$(cat "$secret")
        fi
    done
fi

# JMXの設定（デバッグ環境のみ）
if [ "$ENABLE_JMX" = "true" ]; then
    export JAVA_OPTS="${JAVA_OPTS} \
        -Dcom.sun.management.jmxremote \
        -Dcom.sun.management.jmxremote.port=9999 \
        -Dcom.sun.management.jmxremote.authenticate=false \
        -Dcom.sun.management.jmxremote.ssl=false"
fi

# アプリケーション起動
exec java ${JAVA_OPTS} -jar app.jar "$@"
```

### Docker Compose開発環境

```yaml
# docker-compose.yml
version: '3.8'

services:
  app:
    build:
      context: .
      dockerfile: Dockerfile
      target: builder  # 開発時はビルダーステージを使用
    volumes:
      - ./src:/app/src
      - ./target:/app/target
      - maven-cache:/root/.m2
    environment:
      - SPRING_PROFILES_ACTIVE=dev
      - DATABASE_URL=jdbc:postgresql://db:5432/devdb
      - REDIS_URL=redis://cache:6379
    ports:
      - "8080:8080"
      - "5005:5005"  # デバッグポート
    command: mvn spring-boot:run -Dspring-boot.run.jvmArguments="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"
    depends_on:
      db:
        condition: service_healthy
      cache:
        condition: service_started

  db:
    image: postgres:14-alpine
    environment:
      - POSTGRES_DB=devdb
      - POSTGRES_USER=dev
      - POSTGRES_PASSWORD=devpass
    volumes:
      - postgres-data:/var/lib/postgresql/data
      - ./init-scripts:/docker-entrypoint-initdb.d
    ports:
      - "5432:5432"
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U dev"]
      interval: 10s
      timeout: 5s
      retries: 5

  cache:
    image: redis:7-alpine
    ports:
      - "6379:6379"
    command: redis-server --appendonly yes
    volumes:
      - redis-data:/data

  # 開発ツール
  adminer:
    image: adminer
    ports:
      - "8081:8080"
    depends_on:
      - db

  redis-commander:
    image: rediscommander/redis-commander:latest
    environment:
      - REDIS_HOSTS=local:cache:6379
    ports:
      - "8082:8081"
    depends_on:
      - cache

volumes:
  maven-cache:
  postgres-data:
  redis-data:
```

---

## Kubernetes展開

### アプリケーションマニフェスト

```yaml
# k8s/namespace.yaml
apiVersion: v1
kind: Namespace
metadata:
  name: myapp

---
# k8s/configmap.yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: app-config
  namespace: myapp
data:
  application.yaml: |
    server:
      port: 8080
    logging:
      level:
        root: INFO
        com.example: DEBUG
    management:
      endpoints:
        web:
          exposure:
            include: health,info,metrics,prometheus

---
# k8s/secret.yaml
apiVersion: v1
kind: Secret
metadata:
  name: app-secrets
  namespace: myapp
type: Opaque
stringData:
  database-url: jdbc:postgresql://postgres:5432/proddb
  database-username: produser
  database-password: changeme

---
# k8s/deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: app
  namespace: myapp
  labels:
    app: myapp
spec:
  replicas: 3
  selector:
    matchLabels:
      app: myapp
  template:
    metadata:
      labels:
        app: myapp
      annotations:
        prometheus.io/scrape: "true"
        prometheus.io/port: "8080"
        prometheus.io/path: "/actuator/prometheus"
    spec:
      serviceAccountName: app-sa
      securityContext:
        runAsNonRoot: true
        runAsUser: 1000
        fsGroup: 1000
      containers:
      - name: app
        image: ghcr.io/myorg/myapp:latest
        imagePullPolicy: IfNotPresent
        ports:
        - containerPort: 8080
          name: http
        env:
        - name: JAVA_OPTS
          value: "-XX:MaxRAMPercentage=75.0 -XX:+UseG1GC"
        - name: SPRING_PROFILES_ACTIVE
          value: "production"
        envFrom:
        - configMapRef:
            name: app-config
        - secretRef:
            name: app-secrets
        resources:
          requests:
            memory: "512Mi"
            cpu: "500m"
          limits:
            memory: "1Gi"
            cpu: "1000m"
        livenessProbe:
          httpGet:
            path: /actuator/health/liveness
            port: http
          initialDelaySeconds: 60
          periodSeconds: 30
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: http
          initialDelaySeconds: 30
          periodSeconds: 10
        volumeMounts:
        - name: app-storage
          mountPath: /data
      volumes:
      - name: app-storage
        persistentVolumeClaim:
          claimName: app-pvc

---
# k8s/service.yaml
apiVersion: v1
kind: Service
metadata:
  name: app-service
  namespace: myapp
spec:
  type: ClusterIP
  selector:
    app: myapp
  ports:
  - port: 80
    targetPort: http
    protocol: TCP

---
# k8s/ingress.yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: app-ingress
  namespace: myapp
  annotations:
    cert-manager.io/cluster-issuer: letsencrypt-prod
    nginx.ingress.kubernetes.io/rate-limit: "100"
spec:
  ingressClassName: nginx
  tls:
  - hosts:
    - app.example.com
    secretName: app-tls
  rules:
  - host: app.example.com
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: app-service
            port:
              number: 80

---
# k8s/hpa.yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: app-hpa
  namespace: myapp
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
  - type: Resource
    resource:
      name: memory
      target:
        type: Utilization
        averageUtilization: 80
```

### Helm Chart

```yaml
# helm/Chart.yaml
apiVersion: v2
name: myapp
description: A Helm chart for MyApp
type: application
version: 1.0.0
appVersion: "1.0"

---
# helm/values.yaml
replicaCount: 3

image:
  repository: ghcr.io/myorg/myapp
  pullPolicy: IfNotPresent
  tag: ""

imagePullSecrets: []
nameOverride: ""
fullnameOverride: ""

serviceAccount:
  create: true
  annotations: {}
  name: ""

podAnnotations: {}

podSecurityContext:
  runAsNonRoot: true
  runAsUser: 1000
  fsGroup: 1000

securityContext:
  capabilities:
    drop:
    - ALL
  readOnlyRootFilesystem: true
  allowPrivilegeEscalation: false

service:
  type: ClusterIP
  port: 80

ingress:
  enabled: true
  className: "nginx"
  annotations:
    cert-manager.io/cluster-issuer: letsencrypt-prod
  hosts:
    - host: app.example.com
      paths:
        - path: /
          pathType: Prefix
  tls:
    - secretName: app-tls
      hosts:
        - app.example.com

resources:
  limits:
    cpu: 1000m
    memory: 1Gi
  requests:
    cpu: 500m
    memory: 512Mi

autoscaling:
  enabled: true
  minReplicas: 3
  maxReplicas: 10
  targetCPUUtilizationPercentage: 70
  targetMemoryUtilizationPercentage: 80

persistence:
  enabled: true
  storageClass: "standard"
  accessMode: ReadWriteOnce
  size: 8Gi
  
config:
  spring:
    profiles:
      active: production
  logging:
    level:
      root: INFO

secrets:
  database:
    url: ""
    username: ""
    password: ""
```

---

## GraalVM Native Image

### Native Image設定

```xml
<!-- pom.xml -->
<plugin>
    <groupId>org.graalvm.buildtools</groupId>
    <artifactId>native-maven-plugin</artifactId>
    <version>0.9.28</version>
    <configuration>
        <imageName>${project.artifactId}</imageName>
        <mainClass>${main.class}</mainClass>
        <buildArgs>
            --no-fallback
            --enable-http
            --enable-https
            -H:+ReportExceptionStackTraces
            -H:EnableURLProtocols=http,https
            -H:ConfigurationFileDirectories=src/main/resources/META-INF/native-image
            --initialize-at-build-time=org.slf4j,ch.qos.logback
            -H:+AddAllCharsets
        </buildArgs>
    </configuration>
</plugin>
```

### リフレクション設定

```json
// src/main/resources/META-INF/native-image/reflect-config.json
[
  {
    "name": "com.example.model.User",
    "allDeclaredFields": true,
    "allDeclaredMethods": true,
    "allDeclaredConstructors": true
  },
  {
    "name": "com.example.dto.UserDTO",
    "allDeclaredFields": true,
    "allDeclaredMethods": true,
    "allDeclaredConstructors": true
  }
]
```

### Native Dockerイメージ

```dockerfile
# Dockerfile.native
# ステージ1: Native Image ビルド
FROM ghcr.io/graalvm/graalvm-ce:ol9-java17-22.3.3 AS builder

RUN gu install native-image

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN ./mvnw -Pnative native:compile

# ステージ2: 最小実行環境
FROM gcr.io/distroless/base-debian11

WORKDIR /app

COPY --from=builder /app/target/myapp myapp

EXPOSE 8080

ENTRYPOINT ["/app/myapp"]
```

---

## モニタリングとオブザーバビリティ

### Prometheus + Grafana

```yaml
# k8s/monitoring/prometheus.yaml
apiVersion: v1
kind: ServiceMonitor
metadata:
  name: app-metrics
  namespace: myapp
spec:
  selector:
    matchLabels:
      app: myapp
  endpoints:
  - port: http
    path: /actuator/prometheus
    interval: 30s

---
# Grafana Dashboard JSON (一部)
{
  "dashboard": {
    "title": "MyApp Monitoring",
    "panels": [
      {
        "title": "Request Rate",
        "targets": [
          {
            "expr": "rate(http_server_requests_seconds_count{job=\"myapp\"}[5m])"
          }
        ]
      },
      {
        "title": "Response Time",
        "targets": [
          {
            "expr": "histogram_quantile(0.95, rate(http_server_requests_seconds_bucket{job=\"myapp\"}[5m]))"
          }
        ]
      },
      {
        "title": "JVM Memory",
        "targets": [
          {
            "expr": "jvm_memory_used_bytes{job=\"myapp\"}"
          }
        ]
      }
    ]
  }
}
```

### 分散トレーシング

```java
// Micrometer Tracing設定
@Configuration
public class TracingConfiguration {
    
    @Bean
    public Tracer tracer() {
        return Tracer.builder()
            .currentTraceContext(ThreadLocalCurrentTraceContext.newBuilder()
                .addScopeDecorator(MDCScopeDecorator.get())
                .build())
            .sampler(Sampler.create(0.1f)) // 10%サンプリング
            .traceReporter(zipkinReporter())
            .build();
    }
    
    @Bean
    public ZipkinSpanReporter zipkinReporter() {
        return AsyncZipkinSpanReporter.builder()
            .endpoint("http://zipkin:9411/api/v2/spans")
            .build();
    }
}
```

---

## まとめ

CI/CDパイプラインとクラウドネイティブ配布により：

1. **自動化**: ビルド、テスト、デプロイの完全自動化
2. **品質保証**: 継続的なテストと静的解析
3. **スケーラビリティ**: Kubernetesによる自動スケーリング
4. **可観測性**: メトリクス、ログ、トレースの統合
5. **高速起動**: GraalVM Native Imageによる起動時間短縮

これらの技術を組み合わせることで、モダンなクラウド環境で高品質なJavaアプリケーションを効率的に開発・運用できます。ただし、すべての技術を一度に導入する必要はなく、プロジェクトの規模と要件に応じて段階的に採用することが重要です。

## 実践的なサンプルコード

本付録で解説したCI/CDパイプラインとクラウドネイティブ配布の実践的なサンプルコードは、`/appendix/cicd-cloud-native/`ディレクトリに収録されています。GitHub Actions、Docker、Kubernetes、Helmチャートなどの設定ファイルやサンプルプロジェクトを参照することで、実際の開発環境での適用方法を学べます。