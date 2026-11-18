# <b>付録B</b> <span>技術的な詳細解説（Deep Dive）</span> <small>本文の技術を内部から理解する</small>

本付録では、本文で扱った技術の内部のしくみや設計の背景を、15の節に分けて解説します。
各節はGitHubリポジトリの`appendix/`の下に、解説（README.md）と実行できるサンプルコードとともに置いています<span class="footnote">`https://github.com/Nagatani/techbook-java-primer/tree/main/appendix/`です。</span>。
本文の各章からは「付録B.06」のように節の番号で案内しています。関連する章を読み終えてから開いてください。

各節の解説は、JDKの内部を実際に観察しながら読む形にしています。
解説の主張には、その確かめ方（`javap`で見るバイトコード、JVMのログ、小さな観察用のプログラムの出力、仕様の節）を対応づけてあります。
解説に載せた出力は、観察用のプログラムをJDK 25で実際に動かして得たものです。

## 付録リソース一覧

各節の番号は、GitHubリポジトリの`appendix/`のディレクトリ名（b01〜b15）と一致しています。

### 言語基盤と設計の考え方

#### B.01 JVMアーキテクチャとバイトコード
- 場所
    + `https://github.com/Nagatani/techbook-java-primer/tree/main/appendix/b01-jvm-architecture/`
- 内容
    + クラスファイルの構造、スタックマシンとしてのバイトコード、クラスローダーと初期化の時期、実行時データ領域とGC、エスケープ解析、CDSとAOTキャッシュ
- 関連章
    + 第1章、第2章

#### B.02 プログラミングパラダイム
- 場所
    + `https://github.com/Nagatani/techbook-java-primer/tree/main/appendix/b02-programming-paradigms/`
- 内容
    + パラダイムの歴史（ソフトウェア危機・構造化・オブジェクト指向・関数型）と関数型の数学的基礎（ラムダ計算、圏論、ファンクター、モナド）、ラムダ式のコンパイル結果、OptionalとStreamで法則が成り立つ場合と崩れる場合
- 関連章
    + 第3章、第11章

#### B.03 ソフトウェア設計原則
- 場所
    + `https://github.com/Nagatani/techbook-java-primer/tree/main/appendix/b03-software-design-principles/`
- 内容
    + SOLID原則を標準ライブラリの実例で確かめる、標準ライブラリにあるデザインパターン、シングルトンの書き方の比較、アーキテクチャパターン
- 関連章
    + 第3-7章、第25章

### オブジェクト指向

#### B.04 仮想メソッドテーブル
- 場所
    + `https://github.com/Nagatani/techbook-java-primer/tree/main/appendix/b04-virtual-method-table/`
- 内容
    + メソッド呼び出しのバイトコード、仮想メソッドテーブル（vtable）と動的ディスパッチ、呼び出し箇所の型とインライン化、クラス階層解析と脱最適化
- 関連章
    + 第5章

#### B.05 不変性の設計パターン
- 場所
    + `https://github.com/Nagatani/techbook-java-primer/tree/main/appendix/b05-immutability-patterns/`
- 内容
    + Stringの内部とfinalフィールドの意味、変更できないコレクション（ビューとコピー）、Integerのキャッシュ、Copy-on-Write、永続的データ構造（構造共有）
- 関連章
    + 第6章

### コレクション・ジェネリクス・Stream

#### B.06 コレクションの内部実装
- 場所
    + `https://github.com/Nagatani/techbook-java-primer/tree/main/appendix/b06-collection-internals/`
- 内容
    + HashMapの表とリサイズ、衝突したバケットの赤黒木化、ArrayListの伸び方、LinkedHashMapとTreeMap（赤黒木）、変更できないコレクションとfail-fast、JOLで測るメモリ量
- 関連章
    + 第9章

#### B.07 型消去とジェネリクス
- 場所
    + `https://github.com/Nagatani/techbook-java-primer/tree/main/appendix/b07-type-erasure-patterns/`
- 内容
    + 型消去の規則とキャストの挿入、Signature属性とリフレクション、ブリッジメソッド、消去による制限、ヒープ汚染と@SafeVarargs、型トークンとワイルドカード
- 関連章
    + 第10章

#### B.08 Stream APIの内部実装
- 場所
    + `https://github.com/Nagatani/techbook-java-primer/tree/main/appendix/b08-stream-api-internals/`
- 内容
    + パイプラインの構造と遅延評価、Spliteratorの特性と分割、並列ストリームと共通プール、並列で結果を変えないための約束、Stream Gatherers
- 関連章
    + 第12章

### 言語機能

#### B.09 インターフェイスの進化
- 場所
    + `https://github.com/Nagatani/techbook-java-primer/tree/main/appendix/b09-interface-evolution/`
- 内容
    + default・static・privateメソッドのバイトコードと呼び出し命令、メソッドの優先の規則とダイヤモンド問題、関数型インターフェイスの条件、メソッドを足すときの互換性
- 関連章
    + 第7章

#### B.10 レコードとデータ指向プログラミング
- 場所
    + `https://github.com/Nagatani/techbook-java-primer/tree/main/appendix/b10-records-dop/`
- 内容
    + レコードのコンパイル結果（javapで見る構造とinvokedynamic）、コンパクトコンストラクター、equalsの規則と浅い不変性、sealedとパターンマッチングの網羅性、シリアライズ、データ指向設計
- 関連章
    + 第8章

#### B.11 Enumの高度な活用
- 場所
    + `https://github.com/Nagatani/techbook-java-primer/tree/main/appendix/b11-enum-patterns/`
- 内容
    + enumのコンパイル結果と定数ごとの本体、インスタンスが増えないしくみ、初期化の順序、switchのコンパイル結果、EnumSet・EnumMapの内部、ordinalに頼る危険、enumを使った設計の例
- 関連章
    + 第8章

#### B.12 例外処理のパフォーマンス
- 場所
    + `https://github.com/Nagatani/techbook-java-primer/tree/main/appendix/b12-exception-performance/`
- 内容
    + 例外表とfinally・try-with-resourcesのバイトコード、スタックトレースの記録と抜けるフレームのコスト、スタックトレースを作らない例外、OmitStackTraceInFastThrow、詳しいNullPointerExceptionのメッセージ、StackWalker、例外の代わりに値で返す設計
- 関連章
    + 第13章

### I/O・並行処理

#### B.13 NIO.2の高度な機能
- 場所
    + `https://github.com/Nagatani/techbook-java-primer/tree/main/appendix/b13-nio2-advanced/`
- 内容
    + PathとFileSystem（ZIP・jrt）、Filesの例外と属性のビュー、ATOMIC_MOVE、WatchServiceのOSによる違い、メモリマップドファイルとMemorySegment、AsynchronousFileChannel、FileLock
- 関連章
    + 第14章

#### B.14 Javaメモリモデル
- 場所
    + `https://github.com/Nagatani/techbook-java-primer/tree/main/appendix/b14-java-memory-model/`
- 内容
    + happens-before関係と可視性・原子性、安全な公開とfinalフィールド、VarHandleのアクセスモード、false sharingと@Contended、仮想スレッドとsynchronized
- 関連章
    + 第15章

### テスト

#### B.15 テスト戦略
- 場所
    + `https://github.com/Nagatani/techbook-java-primer/tree/main/appendix/b15-testing-strategies/`
- 内容
    + JUnitがテストを実行するしくみ（結果の決まり方・実行順・インスタンス）、ミューテーションテスト、Property-based Testing、データベースを使う統合テスト、テストピラミッド
- 関連章
    + 第21章、第26章

## 付録の活用方法

1. 解説の確認
    + 各節のディレクトリにある`README.md`で解説と実行方法を確認する
2. サンプルコード
    + `src/`ディレクトリ内の実装例を実行する（B.02には`src/`がなく、コード例は`verify/`に置いている）
3. 観察用のプログラム
    + `verify/`ディレクトリのプログラムを動かし、解説に載せた出力と同じ結果になるかを自分の環境で確かめる
4. ベンチマーク
    + 性能を比べるプログラムは自分の環境で実行して確かめる

## 注意事項

- サンプルコードの実行に追加のライブラリやツールが必要な節がある（各節のREADME.mdに手順がある）
- 性能を比べるプログラムの結果は、実行する環境によって変わる