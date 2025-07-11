/**
 * リスト22-11
 * コードスニペット
 * 
 * 元ファイル: chapter22-documentation-and-libraries.md (1089行目)
 */

// 1. プロジェクトの初期化（Maven）
mvn archetype:generate -DgroupId=com.example -DartifactId=myapp

// 2. 依存関係の追加（pom.xmlを編集後）
mvn clean install

// 3. 依存関係ツリーの確認
mvn dependency:tree

// 4. 不要な依存関係の検出
mvn dependency:analyze

// 5. 最新バージョンの確認
mvn versions:display-dependency-updates