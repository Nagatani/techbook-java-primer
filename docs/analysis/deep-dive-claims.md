# 付録Bの主張の検証状況

`npm run check:deep-dive -- --report`が生成する（2026-11-18）。手で書き換えない。しくみは`docs/guides/deep-dive-verification.md`。

| 節 | 主張 | 機械で確認 | 手で確認 | 失敗 | 印のない候補 |
|---|---|---|---|---|---|
| b01-jvm-architecture | 50 | 43 | 7 | 0 | 2 |
| b02-programming-paradigms | 54 | 43 | 11 | 0 | 0 |
| b03-software-design-principles | 40 | 33 | 7 | 0 | 0 |
| b04-virtual-method-table | 23 | 22 | 1 | 0 | 0 |
| b05-immutability-patterns | 35 | 29 | 6 | 0 | 4 |
| b06-collection-internals | 44 | 36 | 8 | 0 | 1 |
| b07-type-erasure-patterns | 51 | 45 | 6 | 0 | 0 |
| b08-stream-api-internals | 40 | 34 | 6 | 0 | 1 |
| b09-interface-evolution | 34 | 30 | 4 | 0 | 1 |
| b10-records-dop | 47 | 40 | 7 | 0 | 0 |
| b11-enum-patterns | 39 | 34 | 5 | 0 | 0 |
| b12-exception-performance | 33 | 30 | 3 | 0 | 1 |
| b13-nio2-advanced | 47 | 37 | 10 | 0 | 2 |
| b14-java-memory-model | 42 | 27 | 15 | 0 | 0 |
| b15-testing-strategies | 31 | 23 | 8 | 0 | 5 |

## 印のない候補

数値・Javaの版・性能の言い回しを含み、主張の印がないまとまり。主張なら台帳に足し、主張でなければ（例・手順の説明など）そのままでよい。

### b01-jvm-architecture

- `appendix/b01-jvm-architecture/README.md:3` [性能] 第1章と第2章で、Javaのプログラムはバイトコードにコンパイルされ、JVM（Java仮想マシン）のうえで動くことを学びました。
- `appendix/b01-jvm-architecture/README.md:95` [数値] `verify/ClassFileHeader.java`は、自分自身のクラスファイルを読んで先頭の8バイトを表示します。

### b05-immutability-patterns

- `appendix/b05-immutability-patterns/README.md:594` [性能] 不変なデータ構造で「変更」するには、新しい版を作ります。
- `appendix/b05-immutability-patterns/README.md:606` [数値] src/の`PersistentHashMap`は、キーのハッシュ値を5ビットずつ区切り、32方向に枝分かれする木です。
- `appendix/b05-immutability-patterns/README.md:637` [数値] \| データ構造 \| 読み取り \| 更新 \| 古い版 \|
- `appendix/b05-immutability-patterns/README.md:645` [性能] `PersistentDataStructuresDemo`は、同じ1万回の追加を可変のデータ構造と比べます。

### b06-collection-internals

- `appendix/b06-collection-internals/README.md:179` [数値] `HashMapTable`は、上位16ビットだけが違う8つのキー（`0x10000`〜`0x80000`）を入れ、それぞれのノードが入った

### b08-stream-api-internals

- `appendix/b08-stream-api-internals/README.md:691` [性能] 並列ストリームが速くなるかどうかに影響する主な条件は、次のとおりです。

### b09-interface-evolution

- `appendix/b09-interface-evolution/README.md:46` [Javaの版] 第7章の「Java 8以降のインターフェイス進化」と「高度な設計パターン」の内容を、少し大きなプログラムで試すためのデモです。

### b12-exception-performance

- `appendix/b12-exception-performance/README.md:441` [性能] オブジェクトを1つ割り当てる時間はもともと短く、プールからの出し入れ（`ConcurrentLinkedQueue`の操作）にも時間がかかり

### b13-nio2-advanced

- `appendix/b13-nio2-advanced/README.md:702` [数値] `IOBenchmark`は、`FileInputStream`、`BufferedInputStream`、`FileChannel`（ヒ
- `appendix/b13-nio2-advanced/examples/EXAMPLES.md:62` [数値] 毎日数千のCSVファイル（各100MB以上）を処理し、データを抽出、変換してデータベースにロードする。

### b15-testing-strategies

- `appendix/b15-testing-strategies/README.md:290` [数値] `verify/TimeoutProbe.java`は、10秒待つテストと、割り込みを確かめずに1秒間回り続ける計算のループに、200ミリ秒
- `appendix/b15-testing-strategies/README.md:455` [性能] - 別の実装と比べる
- `appendix/b15-testing-strategies/README.md:537` [性能] \| 方法 \| 速さ \| 本番との近さ \| 必要なもの \|
- `appendix/b15-testing-strategies/README.md:563` [性能] - 単体テストは速く、失敗したときに原因の場所がすぐわかる
- `appendix/b15-testing-strategies/README.md:568` [性能] - 画面やシナリオ全体のテストは、利用者から見た動きを確かめる
