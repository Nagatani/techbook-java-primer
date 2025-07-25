# <b>付録B</b> <span>技術的な詳細解説（Deep Dive）インデックス</span> <small>上級者向け技術解説</small>

この付録では、本書の各章で扱った技術的概念について、より深い理解を提供します。各付録は、GitHubリポジトリのhttps://github.com/Nagatani/techbook-java-primer/tree/main/appendix/ディレクトリに実装例とともに提供されています。

## 付録リソース一覧

### JVM・言語基盤

#### B.01 JVMアーキテクチャとバイトコード
- 場所：https://github.com/Nagatani/techbook-java-primer/tree/main/appendix/b01-jvm-architecture/
- 内容：JVMの内部構造、クラスローディング、バイトコード命令、JITコンパイレーション
- 関連章：第1章、第2章

#### B.02 Javaメモリモデル
- 場所：https://github.com/Nagatani/techbook-java-primer/tree/main/appendix/b14-java-memory-model/
- 内容：スレッド間の可視性、happens-before関係、並行処理の正確性
- 関連章：第16章

### オブジェクト指向・設計

#### B.03 ソフトウェア設計原則
- 場所：https://github.com/Nagatani/techbook-java-primer/tree/main/appendix/b03-software-design-principles/`
- 内容：SOLID原則、デザインパターン、アーキテクチャパターン
- 関連章：第3-7章

#### B.04 仮想メソッドテーブル
- 場所：https://github.com/Nagatani/techbook-java-primer/tree/main/appendix/b04-virtual-method-table/`
- 内容：ポリモーフィズムの内部実装、動的ディスパッチ
- 関連章：第5章

#### B.05 不変性の設計パターン
- 場所：https://github.com/Nagatani/techbook-java-primer/tree/main/appendix/b05-immutability-patterns/`
- 内容：不変オブジェクトの高度な実装テクニック
- 関連章：第6章

#### B.06 インターフェイスの進化
- 場所：https://github.com/Nagatani/techbook-java-primer/tree/main/appendix/b09-interface-evolution/`
- 内容：Java 8以降のインターフェイス機能、defaultメソッド、privateメソッド
- 関連章：第7章

### コレクション・ジェネリクス

#### B.07 コレクションの内部実装
- 場所：https://github.com/Nagatani/techbook-java-primer/tree/main/appendix/b06-collection-internals/`
- 内容：HashMap、TreeMapの内部構造、パフォーマンス特性
- 関連章：第8章

#### B.08 型消去とジェネリクス
- 場所：https://github.com/Nagatani/techbook-java-primer/tree/main/appendix/b07-type-erasure-patterns/`
- 内容：型消去のメカニズム、ブリッジメソッド、ジェネリクスの制限
- 関連章：第9章

### 関数型プログラミング

#### B.09 Stream APIの内部実装
- 場所：https://github.com/Nagatani/techbook-java-primer/tree/main/appendix/b08-stream-api-internals/`
- 内容：Spliterator、並列処理、パフォーマンス最適化
- 関連章：第10章

#### B.10 プログラミングパラダイム
- 場所：https://github.com/Nagatani/techbook-java-primer/tree/main/appendix/b02-programming-paradigms/
- 内容：手続き型、オブジェクト指向、関数型プログラミングのパラダイム比較
- 関連章：第3章、第12章

### 高度な言語機能

#### B.11 レコードとデータ指向プログラミング
- 場所：https://github.com/Nagatani/techbook-java-primer/tree/main/appendix/b10-records-dop/`
- 内容：レコードの最適化、データ指向設計パターン
- 関連章：第12章

#### B.12 Enumの高度な活用
- 場所：https://github.com/Nagatani/techbook-java-primer/tree/main/appendix/b11-enum-patterns/`
- 内容：Enumを使った高度な設計パターン、状態機械
- 関連章：第13章

#### B.13 例外処理のパフォーマンス
- 場所：https://github.com/Nagatani/techbook-java-primer/tree/main/appendix/b12-exception-performance/`
- 内容：例外処理のコスト、最適化テクニック
- 関連章：第14章

### I/O・並行処理

#### B.14 NIO.2の高度な機能
- 場所：https://github.com/Nagatani/techbook-java-primer/tree/main/appendix/b13-nio2-advanced/`
- 内容：WatchService、非同期I/O、メモリマップドファイル
- 関連章：第15章


### テスト・開発プロセス

#### B.15 テスト戦略
- 場所：https://github.com/Nagatani/techbook-java-primer/tree/main/appendix/b15-testing-strategies/`
- 内容：テストピラミッド、統合テスト、モックの活用
- 関連章：第22章

## 付録の活用方法：

1. 実装例の確認： 各付録ディレクトリ内の`README.md`で概要を確認
2. サンプルコード： `src/`ディレクトリ内の実装例を実行
3. ベンチマーク： パフォーマンス測定結果を参考に最適化を学習

## 注意事項

- 付録の内容は高度な技術的詳細を含むため、該当する章の内容を十分に理解してから参照することを推奨する
- サンプルコードの実行には、追加のライブラリやツールが必要な場合があります（各README参照）
- 最新の情報とアップデートは、GitHubリポジトリで確認してください