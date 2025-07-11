# 第23章 チャレンジ課題 - ビルドとデプロイ

## 課題概要
フルスタックWebアプリケーションの完全な自動化デプロイメントシステムを構築する総合課題です。

## 課題

### フルスタックアプリケーションの自動化デプロイメント
Spring BootバックエンドとReactフロントエンドを持つアプリケーションの完全なCI/CDパイプラインを構築してください。

**要件:**

1. **アプリケーション構成**
   ```
   fullstack-app/
   ├── backend/
   │   ├── pom.xml
   │   ├── src/
   │   └── Dockerfile
   ├── frontend/
   │   ├── package.json
   │   ├── src/
   │   └── Dockerfile
   ├── docker-compose.yml
   ├── kubernetes/
   │   ├── backend-deployment.yaml
   │   ├── frontend-deployment.yaml
   │   ├── database-deployment.yaml
   │   └── ingress.yaml
   └── .github/
       └── workflows/
           ├── ci.yml
           ├── cd-staging.yml
           └── cd-production.yml
   ```

2. **バックエンド要件**
   - Spring Boot REST API
   - PostgreSQLデータベース
   - Flywayによるマイグレーション
   - 包括的なテスト（単体、統合、E2E）

3. **フロントエンド要件**
   - React SPA
   - TypeScript
   - Material-UI
   - Jestによるテスト

4. **インフラストラクチャ**
   - Docker/Docker Compose（開発環境）
   - Kubernetes（本番環境）
   - Helm Charts
   - ConfigMapとSecrets

5. **CI/CDパイプライン**
   - 自動テスト（単体、統合、E2E）
   - セキュリティスキャン
   - コード品質チェック
   - マルチステージデプロイ（開発→ステージング→本番）
   - Blue/Greenデプロイメント

**GitHub Actions CI/CD設定例:**
```yaml
name: Full CI/CD Pipeline

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

env:
  REGISTRY: ghcr.io
  BACKEND_IMAGE: ${{ github.repository }}/backend
  FRONTEND_IMAGE: ${{ github.repository }}/frontend

jobs:
  # バックエンドのテストとビルド
  backend:
    runs-on: ubuntu-latest
    defaults:
      run:
        working-directory: backend
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 11
      uses: actions/setup-java@v3
      with:
        java-version: '11'
    
    - name: Run tests
      run: mvn clean verify
    
    - name: SonarQube analysis
      env:
        GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
        SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
      run: mvn sonar:sonar
    
    - name: Build Docker image
      run: docker build -t ${{ env.BACKEND_IMAGE }}:${{ github.sha }} .
    
    - name: Security scan
      uses: anchore/scan-action@v3
      with:
        image: ${{ env.BACKEND_IMAGE }}:${{ github.sha }}
  
  # フロントエンドのテストとビルド
  frontend:
    runs-on: ubuntu-latest
    defaults:
      run:
        working-directory: frontend
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Use Node.js
      uses: actions/setup-node@v3
      with:
        node-version: '16'
        cache: 'npm'
        cache-dependency-path: frontend/package-lock.json
    
    - name: Install dependencies
      run: npm ci
    
    - name: Run tests
      run: npm test -- --coverage
    
    - name: Build application
      run: npm run build
    
    - name: Build Docker image
      run: docker build -t ${{ env.FRONTEND_IMAGE }}:${{ github.sha }} .
  
  # E2Eテスト
  e2e-tests:
    needs: [backend, frontend]
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Run E2E tests
      run: |
        docker-compose -f docker-compose.test.yml up -d
        npm run test:e2e
        docker-compose -f docker-compose.test.yml down
  
  # ステージング環境へのデプロイ
  deploy-staging:
    needs: e2e-tests
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/develop'
    
    steps:
    - name: Deploy to staging
      run: |
        # Kubernetesへのデプロイ
        kubectl apply -f kubernetes/staging/
  
  # 本番環境へのデプロイ
  deploy-production:
    needs: e2e-tests
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/main'
    environment:
      name: production
      url: https://myapp.example.com
    
    steps:
    - name: Blue/Green deployment
      run: |
        # Blue/Greenデプロイメントの実装
        ./scripts/deploy-production.sh
```

**Kubernetes設定例:**
```yaml
# backend-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: backend
spec:
  replicas: 3
  selector:
    matchLabels:
      app: backend
  template:
    metadata:
      labels:
        app: backend
    spec:
      containers:
      - name: backend
        image: ghcr.io/myorg/backend:latest
        ports:
        - containerPort: 8080
        env:
        - name: DB_HOST
          valueFrom:
            configMapKeyRef:
              name: app-config
              key: db.host
        - name: DB_PASSWORD
          valueFrom:
            secretKeyRef:
              name: app-secrets
              key: db.password
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 30
        readinessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 5
```

## 成果物

1. **動作するアプリケーション**
   - バックエンドAPI
   - フロントエンドUI
   - データベース

2. **完全なCI/CDパイプライン**
   - 自動テスト
   - ビルド・パッケージング
   - マルチ環境デプロイ

3. **インフラストラクチャコード**
   - Dockerfiles
   - docker-compose.yml
   - Kubernetesマニフェスト
   - Helm Charts

4. **ドキュメント**
   - アーキテクチャ図
   - デプロイメント手順
   - 運用マニュアル

## 評価基準

- **自動化の完全性**: すべてのプロセスが自動化されているか
- **信頼性**: デプロイメントの成功率とロールバック機能
- **セキュリティ**: セキュリティベストプラクティスの適用
- **可観測性**: ログ、メトリクス、トレーシングの実装
- **スケーラビリティ**: 負荷に応じた自動スケーリング
- **ドキュメント**: 運用・保守に必要な情報の完全性

## 提出方法

GitHubリポジトリとして提出し、以下を含めてください：
- 完全なソースコード
- CI/CDの設定と実行履歴
- デプロイされたアプリケーションのURL
- アーキテクチャドキュメント