/**
 * リスト22-12
 * コードスニペット
 * 
 * 元ファイル: chapter22-documentation-and-libraries.md (1111行目)
 */

// 1. 依存関係の競合
// 解決策: 明示的にバージョンを指定
<dependency>
    <groupId>commons-logging</groupId>
    <artifactId>commons-logging</artifactId>
    <version>1.2</version>
</dependency>

// 2. 推移的依存関係の除外
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-core</artifactId>
    <version>5.3.28</version>
    <exclusions>
        <exclusion>
            <groupId>commons-logging</groupId>
            <artifactId>commons-logging</artifactId>
        </exclusion>
    </exclusions>
</dependency>

// 3. ローカルリポジトリのクリア（キャッシュ問題）
mvn dependency:purge-local-repository