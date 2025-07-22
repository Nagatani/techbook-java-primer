# Maven依存関係の確認方法

## 依存関係の解決エラーへの対処

### 1. 依存関係の存在を確認

```bash
# 依存関係ツリーの表示
mvn dependency:tree

# 特定の依存関係を検索
mvn dependency:tree -Dincludes=com.google.code.gson:*
```

### 2. Maven Central Repositoryでの検索

正しいgroupId、artifactId、versionを確認するには、[Maven Central Repository](https://search.maven.org/)で検索します。

### 3. ローカルリポジトリのクリア

```bash
# ローカルリポジトリの削除
rm -rf ~/.m2/repository

# 再ダウンロード
mvn clean install
```

### 4. 代替リポジトリの追加

```xml
<repositories>
    <repository>
        <id>central</id>
        <url>https://repo.maven.apache.org/maven2</url>
    </repository>
    <repository>
        <id>jcenter</id>
        <url>https://jcenter.bintray.com</url>
    </repository>
</repositories>
```

### 5. プロキシ設定の確認

企業環境では、`~/.m2/settings.xml`でプロキシ設定が必要な場合があります：

```xml
<settings>
    <proxies>
        <proxy>
            <id>example-proxy</id>
            <active>true</active>
            <protocol>http</protocol>
            <host>proxy.example.com</host>
            <port>8080</port>
        </proxy>
    </proxies>
</settings>
```