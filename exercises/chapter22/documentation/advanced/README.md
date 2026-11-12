# 第22章 発展課題 - ドキュメンテーションとライブラリ

## 課題概要
実用的なライブラリの開発とプロフェッショナルなドキュメンテーションを作成する課題です。

## 課題リスト

### 課題1: ユーティリティライブラリの開発
汎用的に使える`CommonUtils`ライブラリを開発してください。

**要件:**
- 文字列処理ユーティリティ
- 日付/時刻ユーティリティ
- コレクション操作ユーティリティ
- ファイル操作ユーティリティ
- 完全なJavadocドキュメント

**パッケージ構成:**
```
com.example.utils/
├── string/
│   ├── StringUtils.java
│   └── StringValidator.java
├── date/
│   ├── DateUtils.java
│   └── DateFormatter.java
├── collection/
│   ├── CollectionUtils.java
│   └── ListUtils.java
└── io/
    ├── FileUtils.java
    └── IOUtils.java
```

### 課題2: Maven/Gradleプロジェクトの構成
ライブラリをMavenまたはGradleプロジェクトとして構成してください。
ソースは`src/main/java`、テストは`src/test/java`に置きます（第22章のMavenのディレクトリ規約）。
Javaの版は25、Gradleを使う場合はGradle 9で動く設定にします。

**Maven (pom.xml)の例:**
```xml
<project xmlns="http://maven.apache.org/POM/4.0.0">
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.example</groupId>
    <artifactId>common-utils</artifactId>
    <version>1.0.0</version>
    
    <properties>
        <maven.compiler.release>25</maven.compiler.release>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>
    
    <dependencies>
        <!-- 必要な依存関係 -->
    </dependencies>
</project>
```

### 課題3: APIバージョニングと互換性
後方互換性を保ちながらAPIを進化させる方法を実装してください。

**要件:**
- 廃止予定（@Deprecated）の適切な使用
- 新旧APIの共存
- マイグレーションガイドの作成
- セマンティックバージョニングの実践

## 実行方法

### ライブラリのビルド
```bash
# Maven
mvn clean install

# Gradle
gradle clean build
```

### ドキュメントの生成
```bash
# Maven
mvn javadoc:javadoc

# Gradle
gradle javadoc
```

## 評価基準
- ライブラリの実用性と汎用性
- コードの品質とテストカバレッジ
- ドキュメントの充実度
- ビルド設定の適切性
- APIの使いやすさ