# 付録一覧<small>実践と理論を深める資料集</small>

本書の付録は、実践的な開発に必要な環境構築手順から、JVMの内部動作やパフォーマンス最適化手法まで、幅広い内容を網羅しています。各付録は独立して読むことができますので、現在の学習段階やプロジェクトの要求に応じて参照してください。

## 付録の構成

### 付録A: 開発環境の構築

Java開発を始めるための環境構築手順を詳細に解説します。初学者のほうは、まずこの付録を参照して開発環境を整えてください。

含まれる内容。
- JDKのインストールと設定
- 統合開発環境（IDE）のセットアップ
- ビルドツールの基本設定
- バージョン管理システムの導入



### 付録B: 技術的な詳細解説（Deep Dive）

本書の各章で扱った技術的概念について、より深い理解を提供する上級者向けの内容です。各章の学習フローに沿って再編成され、18のセクションで構成されています。

主要なカテゴリ。
- 基礎概念（第1-2章関連）: B.01-B.03
- オブジェクト指向概念（第3-7章関連）: B.04-B.06
- コレクションとジェネリクス（第8-10章関連）: B.07-B.09
- モダンJava機能（第11-13章関連）: B.10
- 例外処理とI/O（第14-15章関連）: B.11-B.12
- 並行処理（第16章関連）: B.13-B.14
- 高度なトピック（第20-22章関連）: B.15

詳細は[付録B インデックス](appendix-b-index.md)を参照してください。



### 付録C: ソフトウェア工学の理論的基盤

ソフトウェア開発の背景にある理論的な概念や、工学的アプローチについて解説します。

含まれる内容。
- ソフトウェアクライシスと工学的アプローチ
- ソフトウェア品質とメトリクス
- アーキテクチャ理論
- 形式手法の基礎
- プロジェクト管理の理論



### 付録D: 統合プロジェクト

本書で学んだ知識を総合的に活用する統合プロジェクトです。実践的なアプリケーション開発を通じて、学習内容を深めます。

含まれる内容。
- プロジェクトの要件定義
- アーキテクチャ設計
- 段階的な実装ガイド
- テストとデバッグの実践
- デプロイメントまでの完全な流れ



### 付録E: データベースプログラミング

Javaアプリケーションとデータベースの連携について詳しく解説します。

含まれる内容。
- JDBCの基礎と実践
- SQLの基本操作
- トランザクション管理
- 接続プーリング
- ORMの概要



### 付録F: java.time API完全ガイド

Java 8で導入された新しい日時APIの完全なリファレンスです。

含まれる内容。
- 日時クラスの体系
- タイムゾーンの扱い
- 日時の計算と操作
- フォーマッティング
- レガシーAPIとの相互変換



### 付録G: ビルドとデプロイ

開発したアプリケーションを配布可能な形式にパッケージングする方法を解説します。

含まれる内容。
- JARファイルの作成
- 実行可能JARの構築
- jpackageによるネイティブアプリケーション化
- ビルドツールの活用
- 各OS向けのパッケージング



### 付録H: Java共通エラーガイド

Javaプログラミングで頻繁に遭遇するエラーパターンを体系的にまとめ、効果的な対処法を解説します。

含まれる内容。
- 実行時例外の詳細と対処法
- コンパイルエラーの理解と解決
- デバッグ手法とトラブルシューティング
- エラー予防のためのベストプラクティス
- 効果的なエラーハンドリング手法

※注意： 現在、付録Bの各章の詳細なサンプルコードと実装例は、GitHubリポジトリに格納されています。`https://github.com/Nagatani/techbook-java-primer/tree/main/appendix/`ディレクトリから参照できます。各付録の末尾にあるリンクもご活用ください。

## 付録の活用方法

1. 初学者のほう：まず付録Aで環境を整え、本文の学習を進めてください
2. 実践的な開発を始める方：第19章のJavaBeansイベントモデルを参照して、再利用可能なコンポーネント設計を学んでください
3. より深い理解を求める方：付録Bシリーズで技術的な詳細を学習してください
4. 理論的背景に興味がある方：付録Cでソフトウェア工学の基礎を学んでください

各付録は、本文の理解を深めるための補助教材として設計されています。例えば、第8章のコレクションを学習中なら付録B.06で内部実装を理解し、第16章のマルチスレッドを学んだら付録B.13-B.14で並行処理の詳細を確認するなど、学習の進捗に合わせて参照することをお勧めします。