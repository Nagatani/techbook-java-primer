# 第23章 発展課題 - ビルドとデプロイ

## 課題概要
エンタープライズレベルのビルドとデプロイメントプロセスを構築する発展課題です。

## 課題リスト

### 課題1: マルチモジュールプロジェクト
複数のモジュールから成る大規模プロジェクトを構築してください。

**プロジェクト構造:**
```
my-enterprise-app/
├── parent-pom.xml
├── common/
│   ├── pom.xml
│   └── src/
├── service/
│   ├── pom.xml
│   └── src/
├── web/
│   ├── pom.xml
│   └── src/
└── integration-tests/
    ├── pom.xml
    └── src/
```

**親POMの設定:**
```xml
<project>
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.example</groupId>
    <artifactId>my-enterprise-app</artifactId>
    <version>1.0.0</version>
    <packaging>pom</packaging>
    
    <modules>
        <module>common</module>
        <module>service</module>
        <module>web</module>
        <module>integration-tests</module>
    </modules>
    
    <properties>
        <maven.compiler.source>11</maven.compiler.source>
        <maven.compiler.target>11</maven.compiler.target>
        <spring.version>5.3.20</spring.version>
    </properties>
    
    <dependencyManagement>
        <dependencies>
            <!-- 共通の依存関係バージョン管理 -->
        </dependencies>
    </dependencyManagement>
</project>
```

### 課題2: Dockerコンテナ化
アプリケーションをDockerコンテナとしてパッケージングしてください。

**Dockerfile:**
```dockerfile
# マルチステージビルド
FROM maven:3.8-openjdk-11 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# 実行用イメージ
FROM openjdk:11-jre-slim
WORKDIR /app
COPY --from=build /app/target/my-app.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**docker-compose.yml:**
```yaml
version: '3.8'
services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - DB_HOST=database
      - DB_PORT=5432
    depends_on:
      - database
  
  database:
    image: postgres:13
    environment:
      - POSTGRES_DB=myapp
      - POSTGRES_USER=user
      - POSTGRES_PASSWORD=password
    volumes:
      - db-data:/var/lib/postgresql/data

volumes:
  db-data:
```

### 課題3: CI/CDパイプライン
GitHub Actionsを使用して自動ビルド・デプロイパイプラインを構築してください。

**.github/workflows/ci-cd.yml:**
```yaml
name: CI/CD Pipeline

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

jobs:
  test:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 11
      uses: actions/setup-java@v3
      with:
        java-version: '11'
        distribution: 'temurin'
    
    - name: Cache Maven dependencies
      uses: actions/cache@v3
      with:
        path: ~/.m2
        key: ${{ runner.os }}-m2-${{ hashFiles('**/pom.xml') }}
    
    - name: Run tests
      run: mvn clean test
    
    - name: Generate test report
      uses: dorny/test-reporter@v1
      if: success() || failure()
      with:
        name: Maven Tests
        path: target/surefire-reports/*.xml
        reporter: java-junit
  
  build:
    needs: test
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/main'
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Build application
      run: mvn clean package
    
    - name: Build Docker image
      run: docker build -t myapp:${{ github.sha }} .
    
    - name: Push to registry
      run: |
        echo ${{ secrets.DOCKER_PASSWORD }} | docker login -u ${{ secrets.DOCKER_USERNAME }} --password-stdin
        docker tag myapp:${{ github.sha }} myregistry/myapp:latest
        docker push myregistry/myapp:latest
```

## 実行方法

### マルチモジュールビルド
```bash
# 全モジュールのビルド
mvn clean install

# 特定モジュールのビルド
mvn clean install -pl service -am

# 依存関係の更新
mvn versions:display-dependency-updates
```

### Dockerの操作
```bash
# イメージのビルド
docker build -t myapp .

# コンテナの実行
docker run -p 8080:8080 myapp

# Docker Composeでの起動
docker-compose up -d

# ログの確認
docker-compose logs -f app
```

## 評価基準
- モジュール間の依存関係の適切な管理
- Dockerイメージの最適化（サイズ、レイヤー）
- CI/CDパイプラインの完全性
- セキュリティベストプラクティスの適用
- 自動化のレベル