# 第15章 ファイルI/O

## 本章の学習目標

### 前提知識

本章を学習するためには、いくつかの重要な前提知識が必要です。まず必須の前提として、第6章で学んだ例外処理の概念と技術を十分に習得していることが不可欠です。ファイルI/O操作は、ネットワークやディスクアクセスなどの外部リソースを扱うため、さまざまな例外が発生する可能性があり、適切な例外処理が欠かせません。第10章までの基本的なJavaプログラミング能力も必要で、コレクション、ラムダ式、Stream APIなどの技術を組み合わせて効率的なファイル処理を実装します。try-with-resourcesの理解と実践も特に重要で、リソースの管理において中心的な役割を果たします。

さらに、システム理解の観点からの前提として、ファイルシステムの基本概念を理解していることが推奨されます。これには、ファイル、ディレクトリ、パスの概念、絶対パスと相対パスの違い、ファイルのメタデータ（サイズ、作成日時、アクセス権など）についての基本的な知識が含まれます。文字エンコーディングの基本知識も重要で、UTF-8、Shift_JIS、ASCIIなどの主要なエンコーディングの特徴と違い、文字化けの問題やその対処方法について理解していることが、特に国際化が重要である現代のアプリケーション開発において不可欠です。

### 学習目標

本章では、JavaにおけるファイルI/O操作の包括的な知識と技術を習得します。知識理解の面では、まずファイルI/Oの基本概念とその重要性を理解します。ファイルI/Oは、プログラムが外部データと連携し、永続化やデータ交換を実現するための基礎技術です。InputStream/OutputStreamとReader/Writerの違いと適切な使い分けを学び、バイトストリームと文字ストリームの特性を理解します。NIO.2による現代的なファイル操作も重要な学習ポイントで、FilesクラスやPathインターフェイスを使ったより簡潔で安全なファイル操作を学びます。文字エンコーディングとその考慮事項も欠かせないテーマで、グローバルなアプリケーション開発において不可欠な知識です。

技能習得の面では、各種I/Oクラスの適切な選択と使用方法をマスターします。BufferedReader、FileInputStream、Filesクラスなどの特性を理解し、状況に応じた最適な選択ができるようになります。try-with-resourcesによる確実なリソース管理も重要なスキルで、メモリリークやリソースの枚渇を防ぐための正しい方法を学びます。テキストファイルとバイナリファイルの処理方法の違いを理解し、ファイル検索とディレクトリ操作の実装技術も習得します。

システムプログラミング能力の観点からは、外部データとの安全で効率的な連携方法を学びます。これには、ファイルの整合性チェック、トランザクション的な操作、同期化処理などが含まれます。大容量ファイルの効率的な処理技術も重要で、メモリ使用量を抱えながら大量のデータを扱う方法を学びます。エラー処理を含む堅牢なI/Oプログラムの実装も不可欠で、ネットワーク障害、ディスクエラー、アクセス権の問題などに対する適切な対応方法を習得します。

最終的な到達レベルとしては、さまざまな形式のファイル処理プログラムを実装できます。CSV、JSON、XMLなどの一般的なデータ形式や、バイナリファイルの処理に対応できます。大容量データの効率的な読み書きプログラムを作成し、ネットワークリソースを含む外部リソースを安全に扱えます。最終的に、文字エンコーディングを適切に考慮した国際化対応のプログラムが実装できるようになることが、本章の最終目標です。



## 章末演習

本章で学んだファイルI/Oの概念を活用して、実践的な練習課題に取り組みましょう。

### 演習の目標
ファイルI/Oの基本操作から高度なファイルシステム管理までを習得します。




## 基礎レベル課題（必須）

### 課題1: 基本的なファイル読み書き

さまざまなI/O手法を使ったファイル読み書きを実装してください。

**技術的背景：ファイルI/Oの進化と現代的手法**

JavaのファイルI/O APIは時代とともに進化してきました：

**歴史的変遷：**
- **Java 1.0 (1996)**: InputStream/OutputStream（バイトストリーム）
- **Java 1.1 (1997)**: Reader/Writer（文字ストリーム）導入
- **Java 7 (2011)**: NIO.2（Files/Path）による革新的改善

**各手法の特徴と使い分け：**
```java
// 1. 旧来の方法（冗長でエラーが起きやすい）
BufferedReader br = null;
try {
    br = new BufferedReader(new FileReader("file.txt"));
    // 処理
} finally {
    if (br != null) br.close();  // 忘れやすい
}

// 2. 現代的な方法（簡潔で安全）
List<String> lines = Files.readAllLines(Path.of("file.txt"));

// 3. 大容量ファイル向け（メモリ効率的）
try (Stream<String> stream = Files.lines(Path.of("large.txt"))) {
    stream.forEach(System.out::println);
}
```

**文字エンコーディングの重要性：**
- **文字化けの原因**：エンコーディングの不一致
- **日本の特殊事情**：Shift_JIS、EUC-JP、UTF-8の混在
- **国際化対応**：UTF-8の標準化

**実際のシステムでの問題事例：**
- **Windowsの改行コード問題**：CRLF vs LF
- **BOM（Byte Order Mark）**：UTF-8 with BOMの扱い
- **大容量ログファイル**：メモリ不足によるOutOfMemoryError

この演習では、各手法の特性を理解し、適切な選択ができる能力を養います。

**要求仕様：**
- テキストファイルの読み込み（Files.readAllLines、BufferedReader等）
- テキストファイルの書き込み（Files.write、BufferedWriter等）
- バイナリファイルの読み書き
- 文字エンコーディングの指定
- ファイル存在確認と基本情報取得

**実行例：**
```
=== 基本的なファイル読み書き ===
テキストファイル読み込み:
ファイル: sample.txt
内容:
1: Hello, World!
2: Java File I/O
3: ファイル操作の基本
4: 日本語テキストの処理

ファイル情報:
サイズ: 1,234 バイト
最終更新: 2024-07-04T10:30:00
読み取り可能: true
書き込み可能: true

エンコーディング指定読み込み:
UTF-8: こんにちは、世界！
Shift_JIS: こんにちは、世界！

テキストファイル書き込み:
出力ファイル: output.txt
書き込み完了: 5行
内容確認:
Line 1: Java File I/O Example
Line 2: 現在時刻: 2024-07-04T10:30:00
Line 3: ランダム値: 42
Line 4: ユニコード: 🚀
Line 5: 処理完了

バイナリファイル処理:
画像ファイル読み込み: image.jpg
ファイルサイズ: 256,789 バイト
ヘッダー: FF D8 FF E0 (JPEG形式)
コピー作成: image_copy.jpg
コピー完了: バイト完全一致確認

大きなファイル処理:
ファイル: largefile.txt (10MB)
BufferedReader使用: 145ms
Files.readAllLines使用: 89ms
推奨: Files.readAllLines（小〜中サイズファイル）
```

- 複数のI/O手法の理解と使い分け
- エンコーディングの適切な処理
- パフォーマンスを考慮したファイル操作

**実装ヒント：**
- Files.readAllLines() で簡単読み込み
- try-with-resourcesでリソース管理
- Charset.forName() でエンコーディング指定



### 課題2: ディレクトリ操作とファイル検索

**学習目標：** NIO.2のFilesクラス活用、効率的なディレクトリ走査、条件指定による柔軟な検索

**問題説明：**
ディレクトリの操作とファイル検索機能を実装してください。

**技術的背景：ファイルシステム操作の課題と解決**

ファイルシステム操作は、OSの違いやパフォーマンスの課題を抱えています：

**従来のFile APIの問題点：**
```java
// Java 7以前の問題のあるコード
File dir = new File("/path/to/dir");
File[] files = dir.listFiles();  // nullが返る可能性
if (files != null) {
    for (File file : files) {
        // 大量ファイルでメモリ逼迫
    }
}
```

**NIO.2による改善：**
```java
// Stream APIで遅延評価
try (Stream<Path> paths = Files.walk(Paths.get("/path"))) {
    paths.filter(Files::isRegularFile)
         .filter(p -> p.toString().endsWith(".java"))
         .forEach(System.out::println);
}
```

**実際のシステムでの課題：**
- **ファイル数の爆発**：node_modules、.gitなどの巨大ディレクトリ
- **シンボリックリンク**：無限ループの危険性
- **アクセス権限**：権限不足によるAccessDeniedException
- **ネットワークドライブ**：遅延とタイムアウト

**パフォーマンス最適化：**
- **Files.walkFileTree**：大規模ディレクトリでの効率的走査
- **並列処理**：parallelStreamでの高速化
- **早期終了**：findFirstやanyMatchの活用

**クロスプラットフォーム対応：**
```java
// OSに依存しないパス結合
Path path = Paths.get("parent").resolve("child");
// パス区切り文字の自動処理
String separator = FileSystems.getDefault().getSeparator();
```

この演習では、実用的なファイル管理ツールの基礎を学びます。

**要求仕様：**
- ディレクトリの作成・削除・移動
- ファイル一覧の取得（フィルタリング付き）
- 再帰的なディレクトリ走査
- ファイル検索（名前、サイズ、更新日時等による条件）
- ディレクトリサイズの計算

**実行例：**
```
=== ディレクトリ操作とファイル検索 ===
ディレクトリ作成:
作成: /tmp/java-io-test/
作成: /tmp/java-io-test/subdir1/
作成: /tmp/java-io-test/subdir2/
作成完了

ファイル作成:
/tmp/java-io-test/file1.txt (100KB)
/tmp/java-io-test/file2.log (50KB)
/tmp/java-io-test/subdir1/data.csv (200KB)
/tmp/java-io-test/subdir2/config.properties (5KB)

ディレクトリ一覧:
/tmp/java-io-test/
├── file1.txt (100KB)
├── file2.log (50KB)
├── subdir1/
│   └── data.csv (200KB)
└── subdir2/
    └── config.properties (5KB)

ファイル検索:
.txt ファイル: [file1.txt]
100KB以上: [file1.txt, data.csv]
今日更新: [file1.txt, file2.log, data.csv, config.properties]
拡張子別統計:
.txt: 1ファイル (100KB)
.log: 1ファイル (50KB)
.csv: 1ファイル (200KB)
.properties: 1ファイル (5KB)

再帰的検索:
全ファイル数: 4
総サイズ: 355KB
最大ファイル: data.csv (200KB)
最小ファイル: config.properties (5KB)

ディレクトリ操作:
移動: /tmp/java-io-test/subdir1/ → /tmp/java-io-test/renamed/
コピー: /tmp/java-io-test/file1.txt → /tmp/java-io-test/backup/file1_backup.txt
削除: /tmp/java-io-test/file2.log
削除完了
```

- NIO.2のFilesクラス活用
- 効率的なディレクトリ走査
- 条件指定による柔軟な検索




### 課題3: CSVデータ処理システム

CSVファイルの読み書きと集計処理を実装してください。

**技術的背景：CSVの落とし穴と堅牢な処理**

CSVは単純に見えて、実は多くの落とし穴があるフォーマットです：

**CSVの問題点：**
- **標準規格の欠如**：RFC 4180はあるが、実装はバラバラ
- **区切り文字の混在**：カンマ、タブ、セミコロン
- **引用符の扱い**：ダブルクオート内のカンマやエスケープ
- **改行コードの処理**：フィールド内改行の扱い
- **文字エンコーディング**：ExcelのShift_JIS問題

**実装の落とし穴：**
```java
// 単純すぎる実装（問題あり）
String[] fields = line.split(",");  // "Tokyo, Japan"が分割される

// 適切な実装
// 1. 専用ライブラリ（Apache Commons CSV、OpenCSV）
// 2. 正規表現での慎重な処理
// 3. ステートマシンによる解析
```

**実際のビジネスでの課題：**
- **Excel互換性**：BOM付きUTF-8、数値の指数表記
- **大容量データ**：数GB規模のCSVファイル処理
- **データ品質**：不正なデータの検出と処理
- **国際化**：日付形式、数値形式の地域差

**パフォーマンス考慮：**
```java
// メモリ効率的な処理
try (Stream<String> lines = Files.lines(path)) {
    lines.skip(1)  // ヘッダスキップ
         .map(line -> parseCSVLine(line))
         .filter(record -> record.isValid())
         .collect(Collectors.groupingBy(
             Record::getDepartment,
             Collectors.summarizingDouble(Record::getSalary)
         ));
}
```

**エラー処理戦略：**
- **スキップして続行**：エラー行を記録して処理継続
- **早期終了**：データ品質を重視
- **修正試行**：一般的なエラーパターンの自動修正

この演習では、実務で使えるCSV処理技術を学びます。

**要求仕様：**
- CSVファイルの読み込み（ヘッダ処理含む）
- オブジェクトとCSVレコードの相互変換
- データのフィルタリングと集計
- CSVファイルの書き出し
- エラーデータの処理

**実行例：**
```
=== CSVデータ処理システム ===
CSVファイル読み込み:
ファイル: employees.csv
ヘッダー: [ID, 名前, 部署, 年齢, 給与]

従業員データ:
1001, 田中太郎, 営業部, 30, 5000000
1002, 佐藤花子, 開発部, 25, 4500000
1003, 鈴木一郎, 開発部, 35, 6000000
1004, 山田次郎, 総務部, 28, 4200000
1005, 中村美咲, 営業部, 32, 5200000

読み込み完了: 5件

データ集計:
部署別統計:
営業部: 2名（平均給与: 5,100,000円）
開発部: 2名（平均給与: 5,250,000円）  
総務部: 1名（平均給与: 4,200,000円）

年齢層別分析:
20代: 2名（平均給与: 4,350,000円）
30代: 3名（平均給与: 5,400,000円）

データフィルタリング:
開発部のみ:
1002, 佐藤花子, 開発部, 25, 4500000
1003, 鈴木一郎, 開発部, 35, 6000000

給与500万円以上:
1001, 田中太郎, 営業部, 30, 5000000
1003, 鈴木一郎, 開発部, 35, 6000000
1005, 中村美咲, 営業部, 32, 5200000

CSVファイル出力:
出力ファイル: filtered_employees.csv
フィルタ条件: 開発部且つ給与450万円以上
出力件数: 2件

エラーデータ処理:
不正なレコード: "1006,不完全データ,部署なし"
エラー: 給与データが不正です
スキップして処理続行

処理結果:
正常データ: 5件
エラーデータ: 1件
処理成功率: 83.3%
```

- CSVパーサの実装
- データ変換とエラーハンドリング
- 集計処理の効率的な実装




### 課題4: ログファイル解析システム

ログファイルを解析し、統計情報を生成するシステムを実装してください。

**技術的背景：ログ解析の実務的重要性**

ログ解析は、システム運用において不可欠な技術です：

**ログ解析の重要性：**
- **障害分析**：エラーの原因特定と影響範囲の把握
- **セキュリティ監視**：不正アクセスの検知
- **パフォーマンス分析**：ボトルネックの特定
- **ビジネス分析**：ユーザー行動の理解

**一般的なログフォーマット：**
```
# Apache Common Log Format
127.0.0.1 - - [10/Jul/2024:13:55:36 +0900] "GET /index.html HTTP/1.1" 200 2326

# JSON形式（構造化ログ）
{"timestamp":"2024-07-10T13:55:36+09:00","level":"INFO","message":"User login","userId":1234}

# アプリケーションログ
2024-07-10 13:55:36.123 [INFO] com.example.Service - Processing request: id=12345
```

**大容量ログの課題：**
- **ファイルサイズ**：数GB〜数TBのログファイル
- **リアルタイム処理**：tail -fのような追記監視
- **圧縮ファイル**：.gz形式での保存と処理
- **ローテーション**：日付別ファイルの処理

**効率的な処理手法：**
```java
// 正規表現のコンパイル最適化
private static final Pattern LOG_PATTERN = Pattern.compile(
    "^(\\S+) \\S+ \\S+ \\[(.*?)\\] \"(.*?)\" (\\d+) (\\d+)$"
);

// 並列処理での高速化
Files.lines(path)
     .parallel()
     .map(LOG_PATTERN::matcher)
     .filter(Matcher::matches)
     .map(LogEntry::parse)
     .collect(Collectors.groupingByConcurrent(
         LogEntry::getStatusCode,
         Collectors.counting()
     ));
```

**実際のツールとの比較：**
- **Elasticsearch/Kibana**：大規模ログの全文検索
- **Splunk**：エンタープライズログ管理
- **AWK/sed**：UNIXツールでの処理

この演習では、ログ解析の基本技術を実装を通して学びます。

**要求仕様：**
- ログファイルの解析（アクセスログ、エラーログ等）
- 正規表現を使ったログパース
- 時間範囲でのフィルタリング
- 統計レポートの生成
- 大容量ファイルの効率的処理

**実行例：**
```
=== ログファイル解析システム ===
ログファイル読み込み:
ファイル: access.log (5.2MB, 50,000行)
形式: Apache Common Log Format

サンプルログエントリ:
192.168.1.100 - - [04/Jul/2024:10:30:15 +0900] "GET /index.html HTTP/1.1" 200 1234
192.168.1.101 - - [04/Jul/2024:10:30:16 +0900] "POST /api/login HTTP/1.1" 200 56
192.168.1.102 - - [04/Jul/2024:10:30:17 +0900] "GET /css/style.css HTTP/1.1" 404 0

解析結果:
総リクエスト数: 50,000
解析成功: 49,856（99.7%）
解析失敗: 144（0.3%）

HTTPステータス別統計:
200 OK: 42,345件（84.7%）
404 Not Found: 4,123件（8.2%）
500 Internal Server Error: 1,234件（2.5%）
403 Forbidden: 987件（2.0%）
その他: 1,167件（2.3%）

アクセス頻度分析:
時間別アクセス数:
10:00-11:00: 8,234件
11:00-12:00: 9,567件
12:00-13:00: 6,789件
13:00-14:00: 7,456件

人気ページランキング:
1位: /index.html (5,678アクセス)
2位: /api/users (3,456アクセス)
3位: /login (2,234アクセス)
4位: /dashboard (1,987アクセス)
5位: /profile (1,567アクセス)

IPアドレス分析:
ユニークIP数: 2,456
トップアクセスIP:
192.168.1.100: 234アクセス
192.168.1.101: 198アクセス
192.168.1.102: 156アクセス

エラー分析:
エラーログ: error.log
重要度別:
ERROR: 123件
WARN: 456件
INFO: 12,345件

パフォーマンス:
処理時間: 1.23秒
処理速度: 40,650行/秒
メモリ使用量: 45MB（ストリーム処理）
```

- 正規表現を使った効率的な解析
- 大容量ファイルの処理最適化
- 統計処理とレポート生成




## 実装のヒント

### ファイルI/Oのポイント

1. **NIO.2 活用**: Filesクラスで簡潔なファイル操作
2. **リソース管理**: try-with-resourcesで確実なクローズ
3. **エンコーディング**: 文字化けを防ぐ適切な指定
4. **パフォーマンス**: ストリーム処理で大容量ファイル対応
5. **例外処理**: IOExceptionの適切なハンドリング
6. **メモリ効率**: BufferedReader/Writerでバッファリング

### よくある落とし穴
- ファイルのクローズ忘れ（try-with-resourcesで回避）
- 文字エンコーディングの考慮不足
- 大容量ファイルでのメモリ不足
- パスの区切り文字のOS依存性

### 設計のベストプラクティス
- NIO.2のパスとFilesクラスを積極活用
- ストリーム処理で効率的なファイル操作
- 適切な例外処理とエラーメッセージ
- テスト用の一時ファイル管理



## 実装環境

演習課題の詳細な実装テンプレート、テストコード、解答例は以下のディレクトリを参照してください：

```
exercises/chapter15/
├── basic/          # 基礎レベル課題
│   ├── README.md   # 詳細な課題説明
│   ├── BasicFileIO.java
│   ├── DirectoryOperations.java
│   ├── CsvProcessor.java
│   └── LogAnalyzer.java
├── advanced/       # 応用レベル課題
├── challenge/      # 発展レベル課題
└── solutions/      # 解答例（実装完了後に参照）
```



## 完了確認チェックリスト

### 基礎レベル
- [ ] 基本的なファイル読み書きができている
- [ ] ディレクトリ操作とファイル検索ができている
- [ ] CSVデータの処理ができている
- [ ] ログファイルの解析ができている

### 技術要素
- [ ] NIO.2のFilesクラスを適切に使用できている
- [ ] 大容量ファイルの効率的な処理ができている
- [ ] 文字エンコーディングを適切に処理できている
- [ ] try-with-resourcesでリソース管理ができている

### 応用レベル
- [ ] パフォーマンスを考慮した実装ができている
- [ ] 複雑なファイル処理システムが構築できている
- [ ] エラーハンドリングが適切に実装できている
- [ ] 実用的なファイル処理ツールが作成できている

**次のステップ**: 基本課題が完了したら、advanced/の発展課題でより高度なファイル処理に挑戦しましょう！

## 15.1 ファイルI/Oの基礎とストリーム

プログラムが外部のファイルとデータをやりとりすることを**ファイル入出力 (I/O)** と呼びます。Javaでは、このデータの流れを**ストリーム (Stream)** という統一された概念で扱います。

-   **入力ストリーム (`InputStream`, `Reader`)**: ファイルなどからプログラムへデータを読み込む流れ。
-   **出力ストリーム (`OutputStream`, `Writer`)**: プログラムからファイルなどへデータを書き出す流れ。

ストリームには、データをバイト単位で扱う**バイトストリーム**と、文字単位で扱う**キャラクタストリーム**があります。テキストファイルを扱う際は、文字コードを正しく解釈できるキャラクタストリームを使うのが基本です。

### `try-with-resources`による安全なリソース管理

ファイルなどの外部リソースは、使い終わったら必ず「閉じる（closeする）」必要があります。これを怠ると、リソースリークなどの問題を引き起こします。Java 7以降で導入された`try-with-resources`文を使うと、リソースのクローズ処理が自動的に行われ、安全かつ簡潔にコードを記述できます。

```java
// try()の括弧内でリソースを初期化する
try (BufferedReader br = new BufferedReader(new FileReader("file.txt"))) {
    // ... ファイル読み込み処理 ...
} catch (IOException e) {
    // ... エラー処理 ...
}
// tryブロックを抜ける際にbrが自動的にクローズされる
```

## 15.2 テキストファイルの読み書き

### テキストファイルの読み込み

`java.nio.file.Files`クラスと`BufferedReader`を使うのが現代的な方法です。

### Scannerクラスによる柔軟な入力処理

`java.util.Scanner`クラスは、テキスト入力を解析するための便利なクラスです。ファイルだけでなく、標準入力や文字列からもデータを読み取れます：

```java
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class ScannerExample {
    public static void main(String[] args) {
        Path path = Paths.get("data.txt");
        
        // 1. 行単位の読み込み
        try (Scanner scanner = new Scanner(
                Files.newBufferedReader(path, StandardCharsets.UTF_8))) {
            System.out.println("--- Scannerによる行単位の読み込み ---");
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // 2. デリミターを使った読み込み
        String csvData = "リンゴ,150,赤\nバナナ,100,黄";
        try (Scanner scanner = new Scanner(csvData)) {
            scanner.useDelimiter(",|\\n");  // コンマまたは改行で区切る
            while (scanner.hasNext()) {
                System.out.println(scanner.next());
            }
        }
        
        // 3. 型を指定した読み込み
        String numbers = "100 3.14 true";
        try (Scanner scanner = new Scanner(numbers)) {
            int intValue = scanner.nextInt();
            double doubleValue = scanner.nextDouble();
            boolean boolValue = scanner.nextBoolean();
            System.out.printf("整数: %d, 小数: %.2f, 真偽値: %b%n", 
                            intValue, doubleValue, boolValue);
        }
    }
}
```

```java
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class TextFileReaderExample {
    public static void main(String[] args) {
        Path filePath = Paths.get("sample.txt");
        // 事前にファイルを作成
        try { Files.writeString(filePath, "Line 1\nLine 2"); } catch (IOException e) {}

        // 方法1: 1行ずつ読み込む (大きなファイルに最適)
        System.out.println("--- 1行ずつ読み込み ---");
        try (BufferedReader reader = Files.newBufferedReader(
                filePath, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 方法2: 全行を一度に読み込む (小さなファイル向き)
        System.out.println("\n--- 全行を一度に読み込み ---");
        try {
            List<String> lines = Files.readAllLines(
                    filePath, StandardCharsets.UTF_8);
            lines.forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

### テキストファイルの書き込み

同様に`Files`クラスと`BufferedWriter`を使います。

```java
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class TextFileWriterExample {
    public static void main(String[] args) {
        Path filePath = Paths.get("output.txt");

        try (BufferedWriter writer = Files.newBufferedWriter(
                filePath, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, 
                StandardOpenOption.TRUNCATE_EXISTING)) {
            writer.write("最初の行。");
            writer.newLine(); // 改行
            writer.write("次の行。");
            System.out.println("ファイルに書き込みました。");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```
`StandardOpenOption.APPEND`を指定すると、ファイルの末尾に追記できます。

### 文字エンコーディングの指定

テキストファイルを扱う際には、文字コード（エンコーディング）の指定が重要です。文字コードが異なると、文字化けが発生する可能性があります。

**主要な文字エンコーディング**：
- **UTF-8**: 現在最も広く使用される可変長エンコーディング
- **Shift_JIS (MS932)**: Windows環境で使用される日本語エンコーディング
- **ISO-8859-1**: 西欧言語用の1バイトエンコーディング

```java
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CharacterEncodingExample {
    public static void main(String[] args) {
        Path path = Paths.get("japanese.txt");
        
        // UTF-8で書き込み
        try {
            Files.writeString(path, "こんにちは、世界！", StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // Shift_JISで読み込み（文字化けの例）
        System.out.println("--- Shift_JISで読み込み（文字化け） ---");
        try (BufferedReader reader = Files.newBufferedReader(
                path, Charset.forName("Shift_JIS"))) {
            System.out.println(reader.readLine());  // 文字化けが発生
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // UTF-8で正しく読み込み
        System.out.println("\n--- UTF-8で正しく読み込み ---");
        try (BufferedReader reader = Files.newBufferedReader(
                path, StandardCharsets.UTF_8)) {
            System.out.println(reader.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // システムデフォルトのエンコーディング確認
        System.out.println("\nデフォルトエンコーディング: " + Charset.defaultCharset());
    }
}
```

## 15.3 バイナリファイルの高度な処理

### DataInputStream/DataOutputStreamによるプリミティブ型の読み書き

Javaのプリミティブデータ型（`int`, `double`, `boolean`など）や文字列を、プラットフォームに依存しないバイナリ形式で読み書きするために使用します：

```java
import java.io.*;

public class DataStreamExample {
    public static void main(String[] args) throws IOException {
        String filename = "data.bin";
        
        // データの書き込み
        try (DataOutputStream dos = new DataOutputStream(
                new BufferedOutputStream(new FileOutputStream(filename)))) {
            // プリミティブ型の書き込み
            dos.writeInt(12345);
            dos.writeLong(9876543210L);
            dos.writeDouble(3.14159);
            dos.writeBoolean(true);
            dos.writeUTF("こんにちは、世界！");  // 修正UTF-8形式
            
            System.out.println("データを書き込みました。");
        }
        
        // データの読み込み（書き込んだ順序と同じ順序で読む）
        try (DataInputStream dis = new DataInputStream(
                new BufferedInputStream(new FileInputStream(filename)))) {
            int intValue = dis.readInt();
            long longValue = dis.readLong();
            double doubleValue = dis.readDouble();
            boolean boolValue = dis.readBoolean();
            String strValue = dis.readUTF();
            
            System.out.printf("読み込んだデータ: %d, %d, %.5f, %b, %s%n",
                    intValue, longValue, doubleValue, 
                    boolValue, strValue);
        }
    }
}
```

**重要な注意点**：
1. **順序の一致**: 書き込んだ順序と完全に同じ順序で読み込む必要があります
2. **エンディアン**: DataStream系は常にビッグエンディアン（ネットワークバイトオーダー）を使用
3. **文字列の制限**: `writeUTF()`は最大65535バイトまでの文字列しか扱えません

### BufferedInputStream/BufferedOutputStreamの活用

これらのクラスは、内部バッファーを持つことで、`FileInputStream` や `FileOutputStream` のパフォーマンスを向上させます。ディスクアクセスの回数を減らせるため、特に大量のデータを扱う場合に有効です。

## 15.4 オブジェクトの直列化（シリアライズ）

Javaオブジェクトの状態をそのままバイト列に変換して保存するしくみを**直列化（シリアライズ）**、バイト列からオブジェクトを復元することを**非直列化（デシリアライズ）**と呼びます。オブジェクトの構造を保ったまま、簡単に保存・復元できる強力な機能です。

-   直列化したいクラスは`java.io.Serializable`インターフェイスを実装する必要があります。
-   `ObjectOutputStream`で直列化し、`ObjectInputStream`で非直列化します。
-   `transient`修飾子を付けたフィールドは直列化の対象外となります（パスワードなど）。

```java
import java.io.*;
import java.time.LocalDateTime;

// Serializableを実装したクラス
class UserProfile implements Serializable {
    private static final long serialVersionUID = 1L; // クラスのバージョン管理用
    String name;
    transient String password; // 直列化されない
    LocalDateTime registrationDate;
    public UserProfile(String name, String pw) { 
        this.name = name; 
        this.password = pw; 
        this.registrationDate = LocalDateTime.now(); 
    }
    public String toString() { 
        return "User[name=" + name + ", pw=" + password + 
               ", date=" + registrationDate + "]"; 
    }
}

public class SerializationExample {
    public static void main(String[] args) {
        UserProfile user = new UserProfile("testuser", "secret123");
        String filename = "user.ser";

        // 1. 直列化してファイルに保存
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(filename))) {
            oos.writeObject(user);
            System.out.println("保存されたオブジェクト: " + user);
        } catch (IOException e) { e.printStackTrace(); }

        // 2. ファイルから非直列化して復元
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(filename))) {
            UserProfile loadedUser = (UserProfile) ois.readObject();
            System.out.println("復元されたオブジェクト: " + loadedUser); 
            // passwordはnullになる
        } catch (IOException | ClassNotFoundException e) { e.printStackTrace(); }
    }
}
```

## 15.5 ファイルシステムの操作 (NIO.2)

Java 7で導入された`java.nio.file`パッケージ（NIO.2）を使うと、よりモダンで高機能なファイル・ディレクトリ操作が可能です。

-   **`Path`**: ファイルやディレクトリのパスを表現します。
-   **`Paths`**: `Path`オブジェクトを生成するためのユーティリティクラス。
-   **`Files`**: ファイル・ディレクトリの操作（作成、削除、コピー、移動など）を行うためのユーティリティクラス。

```java
import java.io.IOException;
import java.nio.file.*;

public class FileSystemExample {
    public static void main(String[] args) throws IOException {
        Path dir = Paths.get("my_temp_dir");
        Path file = dir.resolve("my_file.txt");

        // ディレクトリ作成
        if (Files.notExists(dir)) {
            Files.createDirectory(dir);
            System.out.println("ディレクトリを作成しました: " + dir);
        }

        // ファイル作成と書き込み
        Files.writeString(file, "Hello, NIO.2!");
        System.out.println("ファイルを作成しました: " + file);

        // ファイルのコピー
        Path copiedFile = dir.resolve("my_file_copy.txt");
        Files.copy(file, copiedFile, StandardCopyOption.REPLACE_EXISTING);
        System.out.println("ファイルをコピーしました: " + copiedFile);

        // ファイルの削除
        Files.delete(copiedFile);
        Files.delete(file);
        Files.delete(dir);
        System.out.println("ファイルとディレクトリを削除しました。");
    }
}
```

## 15.6 GUIでのファイル選択: `JFileChooser`

Swingアプリケーションでユーザーにファイルを選択させるには、`JFileChooser`を使います。

```java
import javax.swing.*;
import java.io.File;

public class FileChooserExample {
    public static void main(String[] args) {
        JFileChooser fileChooser = new JFileChooser();
        
        // ファイルを開くダイアログ
        int openResult = fileChooser.showOpenDialog(null);
        if (openResult == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            System.out.println("選択されたファイル（開く）: " + 
                    selectedFile.getAbsolutePath());
        }

        // ファイルを保存するダイアログ
        int saveResult = fileChooser.showSaveDialog(null);
        if (saveResult == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            System.out.println("保存するファイル: " + 
                    fileToSave.getAbsolutePath());
        }
    }
}
```

## より深い理解のために

本章で学んだ基本的なファイルI/Oをさらに発展させたい方は、付録B.15「NIO.2の高度な機能とリアクティブI/O」を参照してください。この付録では以下の高度なトピックを扱います：

- **WatchService**: ファイルシステムの変更をリアルタイムで監視
- **非同期ファイルI/O**: AsynchronousFileChannelによるノンブロッキング処理
- **メモリマップドファイル**: 大容量ファイルの高速処理
- **リアクティブI/O**: Flow APIを使用したバックプレッシャー対応
- **パフォーマンス最適化**: 各種I/O手法のベンチマークと選択指針

これらの技術は、高性能なファイル処理システムやリアルタイムデータ処理アプリケーションの開発に役立ちます。

## まとめ

-   JavaのファイルI/Oは**ストリーム**ベースです。
-   リソース管理には**`try-with-resources`**を使い、安全性を高めましょう。
-   テキストファイルは**キャラクタストリーム**（`Reader`/`Writer`）、バイナリファイルは**バイトストリーム**（`InputStream`/`OutputStream`）で扱います。
-   **オブジェクト直列化**は、オブジェクトの状態を簡単に保存・復元する強力な手段です。
-   **NIO.2 (`java.nio.file`)** を使うと、モダンで高機能なファイルシステム操作が可能です。
-   GUIでは**`JFileChooser`**を使って、ユーザーにファイルを選択させることができます。
