# <b>付録B</b> <span>技術的な詳細解説（Deep Dive）</span> <small>本文の技術を内部から理解する</small>

本付録では、本文で扱った技術の内部のしくみや設計の背景を、15の節に分けて解説します。
各節はGitHubリポジトリの`appendix/`の下に、解説（README.md）と実行できるサンプルコードとともに置いています<span class="footnote">`https://github.com/Nagatani/techbook-java-primer/tree/main/appendix/`です。</span>。
本文の各章からは「付録B.06」のように節の番号で案内しています。関連する章を読み終えてから開いてください。

## 付録リソース一覧

各節の番号は、GitHubリポジトリの`appendix/`のディレクトリ名（b01〜b15）と一致しています。

### 言語基盤と設計の考え方

#### B.01 JVMアーキテクチャとバイトコード
- 場所
    + `https://github.com/Nagatani/techbook-java-primer/tree/main/appendix/b01-jvm-architecture/`
- 内容
    + JVMの内部構造、クラスローディング、バイトコード命令、JITコンパイレーション
- 関連章
    + 第1章、第2章

#### B.02 プログラミングパラダイム
- 場所
    + `https://github.com/Nagatani/techbook-java-primer/tree/main/appendix/b02-programming-paradigms/`
- 内容
    + パラダイムの歴史的な発展と、関数型プログラミングの数学的基礎（ラムダ計算、圏論、モナド、ファンクター）
- 関連章
    + 第3章、第11章

#### B.03 ソフトウェア設計原則
- 場所
    + `https://github.com/Nagatani/techbook-java-primer/tree/main/appendix/b03-software-design-principles/`
- 内容
    + SOLID原則、デザインパターン、アーキテクチャパターン
- 関連章
    + 第3-7章、第25章

### オブジェクト指向

#### B.04 仮想メソッドテーブル
- 場所
    + `https://github.com/Nagatani/techbook-java-primer/tree/main/appendix/b04-virtual-method-table/`
- 内容
    + ポリモーフィズムの内部実装（vtable）、動的ディスパッチ、JITによる最適化（インラインキャッシュ、クラス階層解析）
- 関連章
    + 第5章

#### B.05 不変性の設計パターン
- 場所
    + `https://github.com/Nagatani/techbook-java-primer/tree/main/appendix/b05-immutability-patterns/`
- 内容
    + 不変オブジェクトの実装パターン、Copy-on-Write、永続的データ構造（構造共有）
- 関連章
    + 第6章

### コレクション・ジェネリクス・Stream

#### B.06 コレクションの内部実装
- 場所
    + `https://github.com/Nagatani/techbook-java-primer/tree/main/appendix/b06-collection-internals/`
- 内容
    + HashMap、TreeMapの内部構造、パフォーマンス特性
- 関連章
    + 第9章

#### B.07 型消去とジェネリクス
- 場所
    + `https://github.com/Nagatani/techbook-java-primer/tree/main/appendix/b07-type-erasure-patterns/`
- 内容
    + 型消去のメカニズム、ブリッジメソッド、ジェネリクスの制限
- 関連章
    + 第10章

#### B.08 Stream APIの内部実装
- 場所
    + `https://github.com/Nagatani/techbook-java-primer/tree/main/appendix/b08-stream-api-internals/`
- 内容
    + Spliterator、並列処理、パフォーマンス最適化
- 関連章
    + 第12章

### 言語機能

#### B.09 インターフェイスの進化
- 場所
    + `https://github.com/Nagatani/techbook-java-primer/tree/main/appendix/b09-interface-evolution/`
- 内容
    + Java 8以降のインターフェイス機能、defaultメソッド、staticメソッド、privateメソッド
- 関連章
    + 第7章

#### B.10 レコードとデータ指向プログラミング
- 場所
    + `https://github.com/Nagatani/techbook-java-primer/tree/main/appendix/b10-records-dop/`
- 内容
    + レコードとsealedによるデータ指向設計、データ変換のパイプライン、レコードの最適化
- 関連章
    + 第8章

#### B.11 Enumの高度な活用
- 場所
    + `https://github.com/Nagatani/techbook-java-primer/tree/main/appendix/b11-enum-patterns/`
- 内容
    + Enumを使った高度な設計パターン、状態機械
- 関連章
    + 第8章

#### B.12 例外処理のパフォーマンス
- 場所
    + `https://github.com/Nagatani/techbook-java-primer/tree/main/appendix/b12-exception-performance/`
- 内容
    + 例外処理のコスト、スタックトレースを作らない例外、例外に代わるエラー処理（Result型など）
- 関連章
    + 第13章

### I/O・並行処理

#### B.13 NIO.2の高度な機能
- 場所
    + `https://github.com/Nagatani/techbook-java-primer/tree/main/appendix/b13-nio2-advanced/`
- 内容
    + WatchService、非同期I/O、メモリマップドファイル
- 関連章
    + 第14章

#### B.14 Javaメモリモデル
- 場所
    + `https://github.com/Nagatani/techbook-java-primer/tree/main/appendix/b14-java-memory-model/`
- 内容
    + スレッド間の可視性、happens-before関係、並行処理の正確性
- 関連章
    + 第15章

### テスト

#### B.15 テスト戦略
- 場所
    + `https://github.com/Nagatani/techbook-java-primer/tree/main/appendix/b15-testing-strategies/`
- 内容
    + テストピラミッド、Property-based Testing、ミューテーションテスト、データベースを使う統合テスト
- 関連章
    + 第21章、第26章

## 付録の活用方法

1. 解説の確認
    + 各節のディレクトリにある`README.md`で解説と実行方法を確認する
2. サンプルコード
    + `src/`ディレクトリ内の実装例を実行する（B.02はREADME.mdにコード例を載せている）
3. ベンチマーク
    + 性能を比べるプログラムは自分の環境で実行して確かめる

## 注意事項

- サンプルコードの実行に追加のライブラリやツールが必要な節がある（各節のREADME.mdに手順がある）
- 性能を比べるプログラムの結果は、実行する環境によって変わる