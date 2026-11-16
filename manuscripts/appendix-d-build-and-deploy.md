# <b>付録D</b> <span>高度なビルドとデプロイメント技術</span> <small>Maven・Gradle・jpackageの応用</small>

## 付録Dについて

本付録では、第23章で学んだ実行可能JARとjpackageの基本を発展させ、ビルドツールによる自動化と、配布の形の選び方を解説します。

### 内容

1. MavenとGradleによるビルドの自動化
2. 依存ライブラリをまとめたFat JARの作成
3. jpackageの応用（アイコンなど、OSごとの設定）
4. 配布でよくある問題と対処

### 関連する章

- 第22章「ドキュメントと外部ライブラリ」
    + MavenとGradleの基本と依存関係の管理
- 第23章「ビルドとデプロイ」
    + 実行可能JARとjpackageの基本（本付録の前提）

### オンライン資料

本付録の解説とサンプルプロジェクトは、GitHubリポジトリで公開しています。<br>
`https://github.com/Nagatani/techbook-java-primer/tree/main/appendix/d-build-and-deploy/`

リポジトリには、解説（README.md）、Maven（pom.xml）とGradle（build.gradle）の設定例、jpackageのスクリプト例、そのままビルドできるサンプルプロジェクト（`sample-projects/`）があります。
