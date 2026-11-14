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
# Gradle Wrapperの再生成（版は2026-11時点の例）
./gradlew wrapper --gradle-version=9.9.1

# 特定のディストリビューションタイプを指定
./gradlew wrapper --gradle-version=9.9.1 --distribution-type=all
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
        force 'com.google.code.gson:gson:2.15.0'
        
        // 版の競合があったらビルドを失敗させる
        // （指定しない場合、Gradleは競合した版のうち最も新しいものを選ぶ）
        failOnVersionConflict()
    }
    // 除外設定（推移的な依存から取り除く）
    exclude group: 'commons-logging', module: 'commons-logging'
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

ビルドキャッシュの設定は、`build.gradle`ではなく`settings.gradle`に書きます。
`gradle.properties`の`org.gradle.caching=true`でも有効にできます。

```groovy
// settings.gradle
buildCache {
    local {
        enabled = true
    }
}
```

古いキャッシュを消すまでの日数は、Gradle 9では`buildCache`では指定できません（以前の`removeUnusedEntriesAfterDays`はありません）。
Gradleのユーザーホーム（`~/.gradle`）の初期化スクリプト`init.d/cache-settings.gradle`で指定します。

```groovy
// ~/.gradle/init.d/cache-settings.gradle
beforeSettings { settings ->
    settings.caches {
        buildCache.setRemoveUnusedEntriesAfterDays(30)
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
org.gradle.jvmargs=-Xmx4g -XX:MaxMetaspaceSize=512m -XX:+UseG1GC
```