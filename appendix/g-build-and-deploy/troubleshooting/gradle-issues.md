# Gradleトラブルシューティング

## よくある問題と解決方法

### 1. Gradle Wrapperの実行エラー

#### 実行権限の問題

```bash
# 実行権限を付与
chmod +x gradlew

# Windows環境での実行
gradlew.bat build
```

#### Wrapperの再生成

```bash
# Gradle Wrapperの再生成
gradle wrapper --gradle-version=8.0

# 特定のディストリビューションタイプを指定
gradle wrapper --gradle-version=8.0 --distribution-type=all
```

### 2. 依存関係の競合解決

#### 依存関係の確認

```bash
# 依存関係ツリーの表示
./gradlew dependencies

# 特定の設定の依存関係
./gradlew dependencies --configuration runtimeClasspath

# 依存関係の洞察
./gradlew dependencyInsight --dependency gson
```

#### 競合の解決

```groovy
configurations.all {
    resolutionStrategy {
        // 特定バージョンを強制
        force 'com.google.code.gson:gson:2.10.1'
        
        // 競合時は最新バージョンを選択
        failOnVersionConflict()
        
        // 除外設定
        exclude group: 'commons-logging', module: 'commons-logging'
    }
}
```

### 3. ビルドパフォーマンスの改善

#### Gradleデーモンの設定

```properties
# gradle.properties
org.gradle.daemon=true
org.gradle.parallel=true
org.gradle.configureondemand=true
org.gradle.caching=true
```

#### ビルドキャッシュの活用

```groovy
buildCache {
    local {
        enabled = true
        removeUnusedEntriesAfterDays = 30
    }
}
```

### 4. プロキシ設定

```properties
# gradle.properties
systemProp.http.proxyHost=proxy.example.com
systemProp.http.proxyPort=8080
systemProp.https.proxyHost=proxy.example.com
systemProp.https.proxyPort=8080
```

### 5. メモリ設定

```properties
# gradle.properties
org.gradle.jvmargs=-Xmx4g -XX:MaxPermSize=512m -XX:+UseG1GC
```