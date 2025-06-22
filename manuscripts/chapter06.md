# 第6章 例外処理

## はじめに：プログラムの信頼性とエラーハンドリングの進化

前章までで、オブジェクト指向プログラミングの基本概念とJavaの主要な機能について学習してきました。この章では、現実世界のソフトウェア開発において極めて重要な「例外処理（Exception Handling）」について詳細に学習していきます。

例外処理は、単なる技術的な仕組みではありません。これは、プログラムの実行中に発生する予期しない状況や異常事態を適切に管理し、システムの安定性と信頼性を確保するための、現代的なソフトウェア開発における必須の設計原則です。

### ソフトウェア開発におけるエラーハンドリングの歴史

コンピュータプログラムの歴史において、「エラー処理」は常に開発者を悩ませる重要な課題でした。初期のプログラミング言語では、エラーが発生した場合の対処法は限定的で、多くの場合、プログラムはクラッシュするか、予期しない動作を示すしかありませんでした。

**1950年代〜1960年代の状況**：初期のプログラミング言語（FORTRAN、COBOLなど）では、エラー処理はプログラマー個人の責任であり、統一された方法論は存在しませんでした。ハードウェア障害やデータエラーが発生すると、プログラムは停止し、場合によってはシステム全体がクラッシュしました。

**1970年代の構造化プログラミング**：C言語などの構造化プログラミング言語では、関数の戻り値を使用してエラー状態を示すという手法が一般的になりました。しかし、この方法には以下のような問題がありました：

- エラーチェックが煩雑で、正常な処理ロジックとエラー処理ロジックが混在する
- エラーチェックを忘れやすく、未処理のエラーがプログラム全体に悪影響を与える
- 複数の関数を通じてエラー情報を伝播させることが困難
- エラーの種類や詳細な情報を伝達する標準的な方法が不足

### 例外処理パラダイムの誕生

これらの問題を根本的に解決するため、1980年代から1990年代にかけて、「例外処理」という新しいパラダイムが研究・開発されました。例外処理の基本的な思想は、「正常な処理の流れ」と「異常事態への対処」を明確に分離し、より直感的で保守性の高いエラーハンドリングを実現することです。

**Ada言語（1980年代）**：軍事・航空宇宙分野での使用を想定して開発されたAda言語は、初期の構造化例外処理機能を提供しました。高い信頼性が要求されるシステムでの使用を前提として、堅牢なエラーハンドリング機能が設計されました。

**C++（1990年代）**：オブジェクト指向プログラミングの普及とともに、C++に例外処理機能が追加されました。try、catch、throwキーワードによる例外処理の基本的な構造が確立されました。

**Java（1995年）**：Javaは、例外処理を言語の中核的な機能として統合し、検査例外（checked exception）という独自の概念を導入しました。これにより、コンパイル時にエラーハンドリングの妥当性をチェックし、より安全なプログラムの作成が可能になりました。

### Javaの例外処理システムの革新性

Javaの例外処理システムは、他の言語と比較して以下の革新的な特徴を持っています：

**統一されたエラーハンドリングモデル**：すべての例外は、Throwableクラスを継承したオブジェクトとして表現されます。これにより、エラー情報の構造化と、統一された方法でのエラー処理が可能になりました。

**検査例外と非検査例外の分離**：Javaでは、例外を「検査例外（Checked Exception）」と「非検査例外（Unchecked Exception）」に分類し、それぞれ異なる処理方針を適用します。検査例外は、プログラマーが明示的に処理する必要があり、コンパイル時にチェックされます。

**例外の階層構造**：すべての例外クラスは階層的に組織化されており、適切な抽象化レベルでの例外処理が可能です。具体的なエラーから一般的なエラーまで、状況に応じて適切な粒度での処理ができます。

**リソース管理との統合**：try-with-resources構文により、ファイルやネットワーク接続などのリソースの確実な解放と、例外処理を統合した安全なリソース管理が実現されています。

### 例外処理がもたらす設計上の利点

適切に設計された例外処理システムは、ソフトウェア開発において以下の重要な利点をもたらします：

**可読性の向上**：正常な処理ロジックとエラー処理ロジックが明確に分離されることで、プログラムの主要な処理の流れが理解しやすくなります。メソッドの本来の目的に集中でき、コードの意図が明確になります。

**保守性の向上**：エラーハンドリングロジックが集約されることで、エラー処理の方針変更や改善が容易になります。また、新しい種類のエラーへの対応も、既存のコードに影響を与えることなく実装できます。

**堅牢性の向上**：想定外の状況が発生しても、プログラムが適切に対処し、可能な限り処理を継続するか、安全に停止することができます。これにより、システム全体の安定性が大幅に向上します。

**デバッグの効率化**：例外オブジェクトには、エラーの発生場所、原因、スタックトレースなどの詳細な情報が含まれるため、問題の特定と修正が効率的に行えます。

### 現代のソフトウェア開発における例外処理の重要性

現代のソフトウェア開発において、例外処理の重要性はますます高まっています：

**分散システムの普及**：マイクロサービスアーキテクチャやクラウドコンピューティングの普及により、ネットワーク障害やサービス間通信エラーなど、従来よりも多様で複雑なエラー状況への対処が必要になっています。

**セキュリティの重要性**：セキュリティ脆弱性の多くは、適切に処理されなかったエラー状況に起因します。例外処理による適切なエラーハンドリングは、セキュリティホールを防ぐ重要な手段となっています。

**ユーザーエクスペリエンスの向上**：現代のアプリケーションでは、エラーが発生してもユーザーに分かりやすいメッセージを表示し、可能な限り処理を継続することが求められます。適切な例外処理により、優れたユーザーエクスペリエンスを提供できます。

**運用監視の自動化**：例外情報をログやメトリクスとして収集・分析することで、システムの健全性を監視し、障害の予兆を早期に発見することが可能になっています。

### 関数型プログラミングとの統合

近年のJava開発では、従来の例外処理に加えて、関数型プログラミングの概念を取り入れた新しいエラーハンドリング手法も注目されています：

**Optionalクラス**：null参照による例外を型システムレベルで防ぐ仕組みです。値の存在・不存在を明示的に表現し、より安全なプログラムの作成を支援します。

**Result型パターン**：成功と失敗を型で表現し、例外を使用せずにエラー状態を管理する手法です。関数型プログラミングの影響を受けた、より宣言的なエラーハンドリングが可能になります。

**CompletableFuture**：非同期処理における例外処理を統合し、複雑な非同期フローでも一貫したエラーハンドリングを実現します。

### この章で学習する内容の意義

この章では、これらの歴史的背景と現代的な課題を踏まえて、Javaの例外処理メカニズムを体系的に学習していきます。単に構文を覚えるのではなく、以下の点を重視して学習を進めます：

**設計思想の理解**：なぜ例外処理が必要なのか、どのような場面で有効なのかを理解し、適切な例外設計ができる能力を身につけます。

**実践的なパターンの習得**：try-catch-finally、try-with-resources、カスタム例外など、実際の開発で頻繁に使用される例外処理パターンを習得します。

**パフォーマンスの考慮**：例外処理のパフォーマンス特性を理解し、適切な使い分けができる能力を養います。

**現代的な開発手法との統合**：Optional、CompletableFuture、ログフレームワークなど、現代的なJava機能との組み合わせ方を学習します。

**テスタビリティの向上**：例外処理を考慮したテスト設計により、より堅牢で信頼性の高いソフトウェアを作成する能力を身につけます。

例外処理を深く理解することは、Javaプログラマーとしての技術レベルを向上させるだけでなく、堅牢で保守性の高いソフトウェアを設計する能力を大幅に向上させることにつながります。この章を通じて、単なる「エラーに対処する技術」を超えて、「信頼性の高いシステムを設計する思想」を身につけていきましょう。

この章では、Javaの例外処理メカニズムについて学習します。

## 6.1 例外の基礎

### try-catch文の実践例：電卓アプリケーション

例外処理は、ユーザー入力やシステムの予期しない状況に対処するための重要な仕組みです。以下のプログラムは、電卓アプリケーションという実用的な例を通じて、基本的な例外処理パターンと、エラーハンドリングの重要性を学習するための材料です：

ファイル名： Calculator.java

```java
import java.util.Scanner;
import java.util.InputMismatchException;

public class Calculator {
    private Scanner scanner;
    
    public Calculator() {
        this.scanner = new Scanner(System.in);
    }
    
    // 基本的な例外処理：ゼロ除算エラーの処理
    public double divide(double numerator, double denominator) throws ArithmeticException {
        if (denominator == 0) {
            throw new ArithmeticException("ゼロで割ることはできません");
        }
        return numerator / denominator;
    }
    
    // 複数の例外を処理する包括的なメソッド
    public void performCalculation() {
        System.out.println("=== 安全な電卓アプリケーション ===");
        
        while (true) {
            try {
                // ユーザー入力の取得（数値以外が入力される可能性がある）
                System.out.print("最初の数値を入力してください（終了は 'q'）: ");
                String firstInput = scanner.next();
                
                // 終了条件のチェック
                if (firstInput.equalsIgnoreCase("q")) {
                    System.out.println("電卓アプリケーションを終了します。");
                    break;
                }
                
                double num1 = Double.parseDouble(firstInput); // NumberFormatException の可能性
                
                System.out.print("演算子を入力してください (+, -, *, /): ");
                String operator = scanner.next();
                
                System.out.print("2番目の数値を入力してください: ");
                double num2 = scanner.nextDouble(); // InputMismatchException の可能性
                
                double result = 0;
                boolean validOperation = true;
                
                // 演算の実行（各演算で異なる例外の可能性）
                switch (operator) {
                    case "+":
                        result = num1 + num2;
                        break;
                    case "-":
                        result = num1 - num2;
                        break;
                    case "*":
                        result = num1 * num2;
                        break;
                    case "/":
                        try {
                            result = divide(num1, num2); // カスタム例外の可能性
                        } catch (ArithmeticException e) {
                            System.out.println("計算エラー: " + e.getMessage());
                            validOperation = false;
                        }
                        break;
                    default:
                        System.out.println("無効な演算子です: " + operator);
                        validOperation = false;
                }
                
                if (validOperation) {
                    System.out.printf("結果: %.2f %s %.2f = %.2f\n", num1, operator, num2, result);
                    
                    // 結果の特殊ケースをチェック
                    if (Double.isInfinite(result)) {
                        System.out.println("警告: 結果が無限大です");
                    } else if (Double.isNaN(result)) {
                        System.out.println("警告: 結果が数値ではありません");
                    }
                }
                
            } catch (NumberFormatException e) {
                System.out.println("入力エラー: 有効な数値を入力してください");
                System.out.println("詳細: " + e.getMessage());
                
            } catch (InputMismatchException e) {
                System.out.println("入力エラー: 正しい形式で数値を入力してください");
                scanner.nextLine(); // 不正な入力をクリア
                
            } catch (Exception e) {
                // 予期しないエラーをキャッチ（最後の砦）
                System.out.println("予期しないエラーが発生しました: " + e.getClass().getSimpleName());
                System.out.println("詳細: " + e.getMessage());
                
                // デバッグ情報の表示（開発時のみ）
                System.out.println("スタックトレース:");
                e.printStackTrace();
                
            } finally {
                // 処理完了時の共通処理
                System.out.println("---");
            }
        }
    }
    
    // リソースの適切な解放
    public void cleanup() {
        if (scanner != null) {
            scanner.close();
            System.out.println("リソースを解放しました。");
        }
    }
    
    public static void main(String[] args) {
        Calculator calc = new Calculator();
        
        try {
            calc.performCalculation();
        } finally {
            // アプリケーション終了時の確実なリソース解放
            calc.cleanup();
        }
    }
}
```

**このプログラムから学ぶ重要な例外処理の概念：**

1. **多層的な例外処理**：異なる種類の例外（NumberFormatException、InputMismatchException、ArithmeticException）を適切に分類し、それぞれに適した処理を実装しています。

2. **ユーザーフレンドリーなエラーメッセージ**：技術的なエラー詳細を開発者向けに保持しながら、ユーザーには理解しやすいメッセージを提供しています。

3. **堅牢性の確保**：1つのエラーが発生してもアプリケーション全体が停止することなく、処理を継続できる設計になっています。

4. **リソース管理**：finally節を使用してリソースの確実な解放を保証しています。

5. **デバッグ支援**：開発時にはスタックトレースを表示し、問題の特定を容易にしています。

**実用的な応用場面：**

- **Webアプリケーション**：ユーザー入力の検証とエラーレスポンス
- **データベースアクセス**：接続エラーやSQL例外の処理
- **ファイル処理**：存在しないファイルやアクセス権限エラーの対応
- **ネットワーク通信**：タイムアウトや接続失敗の処理

**例外処理のベストプラクティス：**

1. **具体的な例外から汎用的な例外の順**：より具体的なエラーを先に処理し、最後に汎用的なキャッチを配置
2. **適切なロギング**：エラーの詳細情報を適切にログに記録
3. **ユーザーエクスペリエンスの配慮**：エラーが発生してもユーザーが次に何をすべきかを明確にする
4. **リソースの確実な解放**：finally節やtry-with-resources文による確実なクリーンアップ

### 複数の例外処理の実践例：ログファイル分析システム

複数の例外処理は、実際のソフトウェア開発では非常に重要です。以下のプログラムは、ログファイルを分析するシステムという実用的な例を通じて、さまざまな種類の例外を適切に処理する方法を学習するための材料です：

ファイル名： LogAnalyzer.java

```java
import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class LogAnalyzer {
    private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private SimpleDateFormat dateFormat;
    private Map<String, Integer> logLevelCounts;
    
    public LogAnalyzer() {
        this.dateFormat = new SimpleDateFormat(DATE_PATTERN);
        this.logLevelCounts = new HashMap<>();
    }
    
    // 包括的なファイル読み込みと解析メソッド
    public void analyzeLogFile(String filename) {
        System.out.println("=== ログファイル分析システム ===");
        System.out.println("分析対象ファイル: " + filename);
        
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        
        try {
            // ファイルの存在確認と読み込み (FileNotFoundException の可能性)
            File logFile = new File(filename);
            if (!logFile.exists()) {
                throw new FileNotFoundException("指定されたファイルが存在しません: " + filename);
            }
            
            if (!logFile.canRead()) {
                throw new IOException("ファイルの読み込み権限がありません: " + filename);
            }
            
            System.out.println("ファイルサイズ: " + logFile.length() + " bytes");
            
            fileReader = new FileReader(logFile);
            bufferedReader = new BufferedReader(fileReader);
            
            String line;
            int lineNumber = 0;
            int successfullyParsedLines = 0;
            int errorLines = 0;
            
            // ファイルの行ごと処理 (IOException の可能性)
            while ((line = bufferedReader.readLine()) != null) {
                lineNumber++;
                
                try {
                    // ログエントリの解析 (複数の例外の可能性)
                    LogEntry entry = parseLogLine(line, lineNumber);
                    
                    if (entry != null) {
                        processLogEntry(entry);
                        successfullyParsedLines++;
                    }
                    
                } catch (ParseException e) {
                    System.err.println("行 " + lineNumber + ": 日付解析エラー - " + e.getMessage());
                    errorLines++;
                    
                } catch (IllegalArgumentException e) {
                    System.err.println("行 " + lineNumber + ": 不正なログ形式 - " + e.getMessage());
                    errorLines++;
                    
                } catch (PatternSyntaxException e) {
                    System.err.println("行 " + lineNumber + ": 正規表現エラー - " + e.getMessage());
                    errorLines++;
                }
            }
            
            // 分析結果の表示
            displayAnalysisResults(lineNumber, successfullyParsedLines, errorLines);
            
        } catch (FileNotFoundException e) {
            System.err.println("ファイルエラー: " + e.getMessage());
            System.err.println("対処法: ファイルパスを確認してください");
            
        } catch (SecurityException e) {
            System.err.println("セキュリティエラー: " + e.getMessage());
            System.err.println("対処法: ファイルアクセス権限を確認してください");
            
        } catch (IOException e) {
            System.err.println("I/Oエラー: " + e.getMessage());
            System.err.println("対処法: ディスク容量やネットワーク接続を確認してください");
            
        } catch (OutOfMemoryError e) {
            System.err.println("メモリ不足エラー: ファイルが大きすぎます");
            System.err.println("対処法: JVMヒープサイズを増やすか、ファイルを分割してください");
            
        } catch (Exception e) {
            // 予期しない例外の処理
            System.err.println("予期しないエラーが発生しました: " + e.getClass().getSimpleName());
            System.err.println("詳細: " + e.getMessage());
            e.printStackTrace();
            
        } finally {
            // リソースの確実な解放
            closeResources(bufferedReader, fileReader);
        }
    }
    
    // ログ行の解析メソッド (複数の例外を投げる可能性)
    private LogEntry parseLogLine(String line, int lineNumber) throws ParseException, IllegalArgumentException {
        if (line == null || line.trim().isEmpty()) {
            return null; // 空行はスキップ
        }
        
        // ログ形式: "2024-01-15 10:30:45 [INFO] ログメッセージ"
        String[] parts = line.split(" ", 4);
        
        if (parts.length < 4) {
            throw new IllegalArgumentException("不完全なログ形式（必要な要素が不足）: " + line);
        }
        
        // 日付の解析 (ParseException の可能性)
        Date timestamp;
        try {
            String dateTimeString = parts[0] + " " + parts[1];
            timestamp = dateFormat.parse(dateTimeString);
        } catch (ParseException e) {
            throw new ParseException("日付形式が不正です: " + parts[0] + " " + parts[1], e.getErrorOffset());
        }
        
        // ログレベルの解析
        String logLevel = parts[2].replaceAll("[\\[\\]]", ""); // [ ] を除去
        if (!isValidLogLevel(logLevel)) {
            throw new IllegalArgumentException("無効なログレベル: " + logLevel);
        }
        
        String message = parts[3];
        
        return new LogEntry(timestamp, logLevel, message, lineNumber);
    }
    
    // ログレベルの妥当性チェック
    private boolean isValidLogLevel(String logLevel) {
        return Arrays.asList("DEBUG", "INFO", "WARN", "ERROR", "FATAL").contains(logLevel.toUpperCase());
    }
    
    // ログエントリの処理
    private void processLogEntry(LogEntry entry) {
        String logLevel = entry.getLogLevel().toUpperCase();
        logLevelCounts.put(logLevel, logLevelCounts.getOrDefault(logLevel, 0) + 1);
        
        // 重要度の高いログは即座に表示
        if ("ERROR".equals(logLevel) || "FATAL".equals(logLevel)) {
            System.out.println("重要: 行" + entry.getLineNumber() + " - " + entry.getMessage());
        }
    }
    
    // 分析結果の表示
    private void displayAnalysisResults(int totalLines, int successfulLines, int errorLines) {
        System.out.println("\n=== 分析結果 ===");
        System.out.println("総行数: " + totalLines);
        System.out.println("正常に解析された行数: " + successfulLines);
        System.out.println("エラー行数: " + errorLines);
        System.out.println("成功率: " + String.format("%.2f%%", (double) successfulLines / totalLines * 100));
        
        System.out.println("\n=== ログレベル別統計 ===");
        for (Map.Entry<String, Integer> entry : logLevelCounts.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue() + "件");
        }
    }
    
    // リソースの確実な解放
    private void closeResources(BufferedReader bufferedReader, FileReader fileReader) {
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
                System.out.println("BufferedReader を閉じました");
            }
        } catch (IOException e) {
            System.err.println("BufferedReader の閉鎖でエラー: " + e.getMessage());
        }
        
        try {
            if (fileReader != null) {
                fileReader.close();
                System.out.println("FileReader を閉じました");
            }
        } catch (IOException e) {
            System.err.println("FileReader の閉鎖でエラー: " + e.getMessage());
        }
    }
    
    // ログエントリを表現するデータクラス
    private static class LogEntry {
        private Date timestamp;
        private String logLevel;
        private String message;
        private int lineNumber;
        
        public LogEntry(Date timestamp, String logLevel, String message, int lineNumber) {
            this.timestamp = timestamp;
            this.logLevel = logLevel;
            this.message = message;
            this.lineNumber = lineNumber;
        }
        
        public Date getTimestamp() { return timestamp; }
        public String getLogLevel() { return logLevel; }
        public String getMessage() { return message; }
        public int getLineNumber() { return lineNumber; }
    }
    
    public static void main(String[] args) {
        LogAnalyzer analyzer = new LogAnalyzer();
        
        // テスト用のサンプルファイル名
        String[] testFiles = {
            "application.log",
            "error.log", 
            "non_existent.log"
        };
        
        for (String filename : testFiles) {
            try {
                analyzer.analyzeLogFile(filename);
                System.out.println("\n" + "=".repeat(50) + "\n");
            } catch (Exception e) {
                System.err.println("ファイル " + filename + " の処理中に致命的エラー: " + e.getMessage());
            }
        }
    }
}
```

**このプログラムから学ぶ重要な複数例外処理の概念：**

1. **例外の階層的処理**：具体的な例外（FileNotFoundException）から汎用的な例外（Exception）まで、適切な順序で例外をキャッチしています。

2. **リソース管理の重要性**：finally節でファイルリソースを確実に解放し、メモリリークを防いでいます。

3. **エラー情報の詳細化**：各例外に対して適切なエラーメッセージと対処法を提供し、問題解決を支援しています。

4. **部分的な障害への対応**：一部の行でエラーが発生しても処理を継続し、可能な限り多くのデータを処理します。

5. **ログの重要度による処理分岐**：エラーレベルのログは即座に表示するなど、ビジネスロジックに応じた例外処理を実装しています。

**実用的な応用場面：**

- **データ処理システム**：CSVファイルやログファイルの一括処理
- **設定ファイル読み込み**：アプリケーション設定の動的読み込み
- **バックアップシステム**：ファイルの整合性チェックと復旧処理
- **監視システム**：システムの健全性を示すメトリクスの収集と分析

## 6.2 カスタム例外の実践例：銀行口座管理システム

カスタム例外は、アプリケーション固有のビジネスルールやエラー状況を表現するための強力な仕組みです。以下のプログラムは、銀行口座管理システムという実用的な例を通じて、意味のあるカスタム例外の設計と活用方法を学習するための材料です：

ファイル名： BankingSystem.java

```java
import java.util.*;

// カスタム例外1: 残高不足例外
class InsufficientFundsException extends Exception {
    private double requestedAmount;
    private double availableBalance;
    
    public InsufficientFundsException(double requestedAmount, double availableBalance) {
        super(String.format("残高不足: 要求額 %.2f円に対して利用可能残高は %.2f円です", 
                          requestedAmount, availableBalance));
        this.requestedAmount = requestedAmount;
        this.availableBalance = availableBalance;
    }
    
    public double getRequestedAmount() { return requestedAmount; }
    public double getAvailableBalance() { return availableBalance; }
    public double getShortfall() { return requestedAmount - availableBalance; }
}

// カスタム例外2: 無効な口座操作例外
class InvalidAccountOperationException extends Exception {
    private String accountNumber;
    private String operationType;
    private String reason;
    
    public InvalidAccountOperationException(String accountNumber, String operationType, String reason) {
        super(String.format("口座 %s での %s 操作が無効です: %s", accountNumber, operationType, reason));
        this.accountNumber = accountNumber;
        this.operationType = operationType;
        this.reason = reason;
    }
    
    public String getAccountNumber() { return accountNumber; }
    public String getOperationType() { return operationType; }
    public String getReason() { return reason; }
}

// カスタム例外3: 口座が見つからない例外
class AccountNotFoundException extends Exception {
    private String accountNumber;
    
    public AccountNotFoundException(String accountNumber) {
        super("口座番号 " + accountNumber + " は存在しません");
        this.accountNumber = accountNumber;
    }
    
    public String getAccountNumber() { return accountNumber; }
}

// カスタム例外4: 日次限度額超過例外
class DailyLimitExceededException extends Exception {
    private double attemptedAmount;
    private double dailyLimit;
    private double alreadyUsed;
    
    public DailyLimitExceededException(double attemptedAmount, double dailyLimit, double alreadyUsed) {
        super(String.format("日次限度額超過: 試行額 %.2f円, 限度額 %.2f円, 本日利用済み %.2f円", 
                          attemptedAmount, dailyLimit, alreadyUsed));
        this.attemptedAmount = attemptedAmount;
        this.dailyLimit = dailyLimit;
        this.alreadyUsed = alreadyUsed;
    }
    
    public double getAttemptedAmount() { return attemptedAmount; }
    public double getDailyLimit() { return dailyLimit; }
    public double getAlreadyUsed() { return alreadyUsed; }
    public double getRemainingLimit() { return dailyLimit - alreadyUsed; }
}

// 銀行口座クラス
class BankAccount {
    private String accountNumber;
    private String accountHolder;
    private double balance;
    private boolean isActive;
    private double dailyWithdrawalLimit;
    private double todayWithdrawn;
    private Date lastTransactionDate;
    
    public BankAccount(String accountNumber, String accountHolder, double initialBalance) {
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
        this.balance = initialBalance;
        this.isActive = true;
        this.dailyWithdrawalLimit = 100000; // デフォルト日次限度額: 10万円
        this.todayWithdrawn = 0;
        this.lastTransactionDate = new Date();
    }
    
    // 預金操作
    public void deposit(double amount) throws InvalidAccountOperationException {
        validateAccount();
        
        if (amount <= 0) {
            throw new InvalidAccountOperationException(accountNumber, "預金", "金額は正の値である必要があります");
        }
        
        if (amount > 1000000) { // 100万円を超える預金は制限
            throw new InvalidAccountOperationException(accountNumber, "預金", "一度に預金できる上限は100万円です");
        }
        
        balance += amount;
        lastTransactionDate = new Date();
        System.out.printf("預金完了: %.2f円が口座 %s に預金されました。新残高: %.2f円\n", 
                         amount, accountNumber, balance);
    }
    
    // 出金操作
    public void withdraw(double amount) throws InsufficientFundsException, 
                                             InvalidAccountOperationException, 
                                             DailyLimitExceededException {
        validateAccount();
        validateWithdrawalAmount(amount);
        checkDailyLimit(amount);
        checkSufficientFunds(amount);
        
        balance -= amount;
        todayWithdrawn += amount;
        lastTransactionDate = new Date();
        
        System.out.printf("出金完了: %.2f円が口座 %s から出金されました。新残高: %.2f円\n", 
                         amount, accountNumber, balance);
    }
    
    // 口座の妥当性確認
    private void validateAccount() throws InvalidAccountOperationException {
        if (!isActive) {
            throw new InvalidAccountOperationException(accountNumber, "取引", "口座が無効化されています");
        }
    }
    
    // 出金額の妥当性確認
    private void validateWithdrawalAmount(double amount) throws InvalidAccountOperationException {
        if (amount <= 0) {
            throw new InvalidAccountOperationException(accountNumber, "出金", "出金額は正の値である必要があります");
        }
        
        if (amount > 500000) { // 50万円を超える出金は制限
            throw new InvalidAccountOperationException(accountNumber, "出金", "一度に出金できる上限は50万円です");
        }
    }
    
    // 日次限度額チェック
    private void checkDailyLimit(double amount) throws DailyLimitExceededException {
        if (todayWithdrawn + amount > dailyWithdrawalLimit) {
            throw new DailyLimitExceededException(amount, dailyWithdrawalLimit, todayWithdrawn);
        }
    }
    
    // 残高確認
    private void checkSufficientFunds(double amount) throws InsufficientFundsException {
        if (amount > balance) {
            throw new InsufficientFundsException(amount, balance);
        }
    }
    
    // 口座情報の表示
    public void displayAccountInfo() {
        System.out.println("=== 口座情報 ===");
        System.out.println("口座番号: " + accountNumber);
        System.out.println("口座名義: " + accountHolder);
        System.out.printf("残高: %.2f円\n", balance);
        System.out.println("口座状態: " + (isActive ? "有効" : "無効"));
        System.out.printf("日次出金限度額: %.2f円\n", dailyWithdrawalLimit);
        System.out.printf("本日出金済み: %.2f円\n", todayWithdrawn);
        System.out.printf("本日の残り限度額: %.2f円\n", dailyWithdrawalLimit - todayWithdrawn);
    }
    
    // アクセサメソッド
    public String getAccountNumber() { return accountNumber; }
    public double getBalance() { return balance; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { this.isActive = active; }
}

// 銀行システム管理クラス
class BankingSystem {
    private Map<String, BankAccount> accounts;
    
    public BankingSystem() {
        this.accounts = new HashMap<>();
    }
    
    public void addAccount(BankAccount account) {
        accounts.put(account.getAccountNumber(), account);
        System.out.println("口座 " + account.getAccountNumber() + " がシステムに追加されました");
    }
    
    public BankAccount getAccount(String accountNumber) throws AccountNotFoundException {
        BankAccount account = accounts.get(accountNumber);
        if (account == null) {
            throw new AccountNotFoundException(accountNumber);
        }
        return account;
    }
    
    // 振込操作（複数の例外が発生する可能性）
    public void transfer(String fromAccount, String toAccount, double amount) 
            throws AccountNotFoundException, InsufficientFundsException, 
                   InvalidAccountOperationException, DailyLimitExceededException {
        
        System.out.println("\n=== 振込処理開始 ===");
        System.out.printf("送金元: %s → 送金先: %s, 金額: %.2f円\n", fromAccount, toAccount, amount);
        
        BankAccount from = getAccount(fromAccount);
        BankAccount to = getAccount(toAccount);
        
        // 送金元から出金
        from.withdraw(amount);
        
        try {
            // 送金先に預金
            to.deposit(amount);
            System.out.println("振込が正常に完了しました");
            
        } catch (InvalidAccountOperationException e) {
            // 送金先への預金に失敗した場合、送金元に返金
            try {
                from.deposit(amount);
                System.out.println("送金先預金失敗のため、送金元に返金しました");
            } catch (InvalidAccountOperationException returnError) {
                System.err.println("重大エラー: 返金処理も失敗しました - " + returnError.getMessage());
            }
            throw e; // 元の例外を再スロー
        }
    }
}

public class BankingSystemDemo {
    public static void main(String[] args) {
        BankingSystem bank = new BankingSystem();
        
        // テスト用口座の作成
        BankAccount account1 = new BankAccount("001-12345", "田中太郎", 500000);
        BankAccount account2 = new BankAccount("001-67890", "佐藤花子", 300000);
        
        bank.addAccount(account1);
        bank.addAccount(account2);
        
        System.out.println("\n=== 銀行取引システム デモンストレーション ===");
        
        // 正常な取引のテスト
        try {
            account1.displayAccountInfo();
            account1.deposit(50000);
            account1.withdraw(30000);
            System.out.println("取引後の情報:");
            account1.displayAccountInfo();
            
        } catch (InsufficientFundsException e) {
            System.err.println("残高不足エラー: " + e.getMessage());
            System.err.printf("不足額: %.2f円\n", e.getShortfall());
            
        } catch (InvalidAccountOperationException e) {
            System.err.println("無効操作エラー: " + e.getMessage());
            System.err.println("操作タイプ: " + e.getOperationType());
            
        } catch (DailyLimitExceededException e) {
            System.err.println("日次限度額エラー: " + e.getMessage());
            System.err.printf("本日の残り限度額: %.2f円\n", e.getRemainingLimit());
        }
        
        // エラーケースのテスト
        System.out.println("\n=== エラーケースのテスト ===");
        
        try {
            // 残高不足テスト
            account2.withdraw(400000);
            
        } catch (Exception e) {
            System.err.println("予期されたエラー: " + e.getMessage());
        }
        
        try {
            // 存在しない口座への振込テスト
            bank.transfer("001-12345", "999-99999", 10000);
            
        } catch (AccountNotFoundException e) {
            System.err.println("口座不存在エラー: " + e.getMessage());
            System.err.println("探索対象口座: " + e.getAccountNumber());
            
        } catch (Exception e) {
            System.err.println("その他のエラー: " + e.getMessage());
        }
        
        try {
            // 無効な金額での取引テスト
            account1.withdraw(-5000);
            
        } catch (Exception e) {
            System.err.println("無効金額エラー: " + e.getMessage());
        }
    }
}
```

**このプログラムから学ぶ重要なカスタム例外の概念：**

1. **ビジネスロジックの表現**：各カスタム例外がそれぞれ特定のビジネスルール違反（残高不足、日次限度額超過等）を表現しています。

2. **詳細情報の保持**：例外オブジェクト内に関連するビジネスデータ（要求額、利用可能残高等）を保持し、適切な対処法の提示を可能にしています。

3. **例外の階層化**：すべてのカスタム例外がExceptionを継承し、統一的な例外処理を可能にしています。

4. **ロールバック処理**：振込処理での部分的失敗時に、既に実行された処理を取り消すロールバック機能を実装しています。

5. **ユーザビリティの向上**：技術的な詳細ではなく、ユーザーが理解しやすいビジネス的な観点でのエラーメッセージを提供しています。

**実用的な応用場面：**

- **Eコマースシステム**：在庫不足、支払い失敗、配送エラー等の業務例外
- **予約システム**：重複予約、キャンセル期限超過、定員オーバー等の例外
- **認証システム**：無効なパスワード、アカウントロック、権限不足等の例外
- **在庫管理システム**：発注数量エラー、期限切れ商品、倉庫容量超過等の例外

## 6.3 try-with-resourcesの実践例：設定ファイル管理システム

try-with-resources文は、リソースの確実な解放を自動化する重要な仕組みです。以下のプログラムは、設定ファイル管理システムという実用的な例を通じて、安全で効率的なリソース管理の方法を学習するための材料です：

ファイル名： ConfigurationManager.java

```java
import java.io.*;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;

public class ConfigurationManager {
    private static final String CONFIG_DIR = "./config/";
    private Map<String, String> configCache;
    private ReentrantReadWriteLock lock;
    
    public ConfigurationManager() {
        this.configCache = new HashMap<>();
        this.lock = new ReentrantReadWriteLock();
    }
    
    // try-with-resources を使用したファイル読み込み
    public Properties loadConfiguration(String configFileName) throws IOException {
        Properties props = new Properties();
        String fullPath = CONFIG_DIR + configFileName;
        
        System.out.println("設定ファイルを読み込み中: " + fullPath);
        
        // try-with-resources: 複数のリソースを同時に管理
        try (InputStream fileInput = Files.newInputStream(Paths.get(fullPath));
             BufferedInputStream bufferedInput = new BufferedInputStream(fileInput);
             InputStreamReader reader = new InputStreamReader(bufferedInput, StandardCharsets.UTF_8)) {
            
            props.load(reader);
            
            System.out.println("設定ファイル読み込み完了: " + props.size() + " 項目");
            
            // 読み込んだ設定をキャッシュに保存
            cacheConfiguration(configFileName, props);
            
        } catch (NoSuchFileException e) {
            System.err.println("設定ファイルが見つかりません: " + fullPath);
            throw new IOException("設定ファイルが見つかりません: " + configFileName, e);
            
        } catch (AccessDeniedException e) {
            System.err.println("設定ファイルへのアクセス権限がありません: " + fullPath);
            throw new IOException("ファイルアクセス権限エラー: " + configFileName, e);
        }
        // try-with-resources により、全てのリソースが自動的に閉じられる
        
        return props;
    }
    
    // try-with-resources を使用したファイル書き込み
    public void saveConfiguration(String configFileName, Properties props) throws IOException {
        String fullPath = CONFIG_DIR + configFileName;
        String backupPath = fullPath + ".backup";
        
        System.out.println("設定ファイルを保存中: " + fullPath);
        
        // バックアップファイルの作成
        createBackup(fullPath, backupPath);
        
        // try-with-resources: 書き込み専用リソースの管理
        try (OutputStream fileOutput = Files.newOutputStream(Paths.get(fullPath), 
                                                             StandardOpenOption.CREATE, 
                                                             StandardOpenOption.TRUNCATE_EXISTING);
             BufferedOutputStream bufferedOutput = new BufferedOutputStream(fileOutput);
             OutputStreamWriter writer = new OutputStreamWriter(bufferedOutput, StandardCharsets.UTF_8)) {
            
            // プロパティファイルの保存（コメント付き）
            props.store(writer, "Configuration saved at " + new Date());
            
            System.out.println("設定ファイル保存完了: " + props.size() + " 項目");
            
            // キャッシュの更新
            cacheConfiguration(configFileName, props);
            
        } catch (IOException e) {
            System.err.println("設定ファイルの保存に失敗しました: " + e.getMessage());
            
            // 保存に失敗した場合、バックアップから復元を試行
            restoreFromBackup(backupPath, fullPath);
            throw e;
        }
    }
    
    // バックアップファイルの作成
    private void createBackup(String originalPath, String backupPath) {
        try {
            Path original = Paths.get(originalPath);
            Path backup = Paths.get(backupPath);
            
            if (Files.exists(original)) {
                Files.copy(original, backup, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("バックアップファイルを作成しました: " + backupPath);
            }
        } catch (IOException e) {
            System.err.println("バックアップファイルの作成に失敗: " + e.getMessage());
        }
    }
    
    // バックアップからの復元
    private void restoreFromBackup(String backupPath, String originalPath) {
        try (InputStream backup = Files.newInputStream(Paths.get(backupPath));
             OutputStream original = Files.newOutputStream(Paths.get(originalPath))) {
            
            backup.transferTo(original);
            System.out.println("バックアップから設定ファイルを復元しました");
            
        } catch (IOException e) {
            System.err.println("バックアップからの復元に失敗: " + e.getMessage());
        }
    }
    
    // スレッドセーフなキャッシュ操作
    private void cacheConfiguration(String fileName, Properties props) {
        Lock writeLock = lock.writeLock();
        
        // try-with-resources の代替：AutoCloseable インターフェイスの活用
        try (AutoCloseableLock autoLock = new AutoCloseableLock(writeLock)) {
            
            for (String key : props.stringPropertyNames()) {
                String cacheKey = fileName + "." + key;
                configCache.put(cacheKey, props.getProperty(key));
            }
            
            System.out.println("設定をキャッシュに保存しました（" + props.size() + " 項目）");
        }
    }
    
    // 高度なファイル処理：バッチ処理での複数ファイル操作
    public void processMultipleConfigFiles(List<String> configFiles) {
        List<String> successfulFiles = new ArrayList<>();
        List<String> failedFiles = new ArrayList<>();
        
        for (String configFile : configFiles) {
            
            // try-with-resources での個別ファイル処理
            try (FileInputStream fis = new FileInputStream(CONFIG_DIR + configFile);
                 Scanner scanner = new Scanner(fis, StandardCharsets.UTF_8)) {
                
                int lineCount = 0;
                Map<String, String> tempConfig = new HashMap<>();
                
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine().trim();
                    lineCount++;
                    
                    if (!line.isEmpty() && !line.startsWith("#")) {
                        String[] parts = line.split("=", 2);
                        if (parts.length == 2) {
                            tempConfig.put(parts[0].trim(), parts[1].trim());
                        }
                    }
                }
                
                System.out.printf("ファイル %s 処理完了: %d行, %d設定項目\n", 
                                configFile, lineCount, tempConfig.size());
                successfulFiles.add(configFile);
                
            } catch (FileNotFoundException e) {
                System.err.println("ファイルが見つかりません: " + configFile);
                failedFiles.add(configFile);
                
            } catch (IOException e) {
                System.err.println("ファイル処理エラー " + configFile + ": " + e.getMessage());
                failedFiles.add(configFile);
            }
            // scanner と fileInputStream は自動的に閉じられる
        }
        
        // 処理結果のサマリー表示
        displayProcessingSummary(successfulFiles, failedFiles);
    }
    
    // ネットワークリソースとの組み合わせ例
    public void downloadAndSaveConfig(String url, String fileName) throws IOException {
        System.out.println("設定ファイルをダウンロード中: " + url);
        
        // 注意: 実際のHTTP接続の代わりに、モックオブジェクトを使用
        try (MockHttpConnection connection = new MockHttpConnection(url);
             InputStream networkStream = connection.getInputStream();
             OutputStream fileStream = Files.newOutputStream(Paths.get(CONFIG_DIR + fileName));
             BufferedOutputStream bufferedOut = new BufferedOutputStream(fileStream)) {
            
            byte[] buffer = new byte[8192];
            int bytesRead;
            long totalBytes = 0;
            
            while ((bytesRead = networkStream.read(buffer)) != -1) {
                bufferedOut.write(buffer, 0, bytesRead);
                totalBytes += bytesRead;
            }
            
            System.out.printf("ダウンロード完了: %s (%d bytes)\n", fileName, totalBytes);
            
        } catch (IOException e) {
            System.err.println("ダウンロードエラー: " + e.getMessage());
            throw e;
        }
        // 全てのネットワークリソースとファイルリソースが自動的に閉じられる
    }
    
    private void displayProcessingSummary(List<String> successful, List<String> failed) {
        System.out.println("\n=== バッチ処理結果 ===");
        System.out.println("成功: " + successful.size() + " ファイル");
        System.out.println("失敗: " + failed.size() + " ファイル");
        
        if (!failed.isEmpty()) {
            System.out.println("失敗したファイル: " + String.join(", ", failed));
        }
    }
    
    // AutoCloseable を実装したロッククラス（try-with-resources での使用例）
    private static class AutoCloseableLock implements AutoCloseable {
        private final Lock lock;
        
        public AutoCloseableLock(Lock lock) {
            this.lock = lock;
            this.lock.lock();
        }
        
        @Override
        public void close() {
            lock.unlock();
        }
    }
    
    // モックHTTP接続クラス（実際のネットワーク処理のデモンストレーション用）
    private static class MockHttpConnection implements AutoCloseable {
        private String url;
        private boolean closed = false;
        
        public MockHttpConnection(String url) throws IOException {
            this.url = url;
            System.out.println("HTTP接続を開始: " + url);
            
            // 接続エラーのシミュレーション
            if (url.contains("invalid")) {
                throw new IOException("無効なURL: " + url);
            }
        }
        
        public InputStream getInputStream() {
            // サンプル設定データを返すモックストリーム
            String sampleConfig = "# Sample Configuration\n" +
                                  "app.name=Sample Application\n" +
                                  "app.version=1.0.0\n" +
                                  "app.debug=true\n";
            return new ByteArrayInputStream(sampleConfig.getBytes(StandardCharsets.UTF_8));
        }
        
        @Override
        public void close() throws IOException {
            if (!closed) {
                System.out.println("HTTP接続を閉じました: " + url);
                closed = true;
            }
        }
    }
    
    public static void main(String[] args) {
        ConfigurationManager manager = new ConfigurationManager();
        
        // ディレクトリの作成
        try {
            Files.createDirectories(Paths.get(CONFIG_DIR));
        } catch (IOException e) {
            System.err.println("設定ディレクトリの作成に失敗: " + e.getMessage());
            return;
        }
        
        System.out.println("=== 設定ファイル管理システム デモンストレーション ===");
        
        // サンプル設定の作成と保存
        Properties sampleConfig = new Properties();
        sampleConfig.setProperty("database.url", "jdbc:mysql://localhost:3306/app");
        sampleConfig.setProperty("database.username", "appuser");
        sampleConfig.setProperty("server.port", "8080");
        sampleConfig.setProperty("logging.level", "INFO");
        
        try {
            // 設定ファイルの保存
            manager.saveConfiguration("application.properties", sampleConfig);
            
            // 設定ファイルの読み込み
            Properties loadedConfig = manager.loadConfiguration("application.properties");
            
            System.out.println("\n読み込まれた設定:");
            for (String key : loadedConfig.stringPropertyNames()) {
                System.out.println("  " + key + " = " + loadedConfig.getProperty(key));
            }
            
            // 複数ファイルのバッチ処理
            List<String> configFiles = Arrays.asList(
                "application.properties",
                "database.properties",  // 存在しないファイル
                "logging.properties"    // 存在しないファイル
            );
            
            manager.processMultipleConfigFiles(configFiles);
            
            // ネットワークダウンロードのデモ
            manager.downloadAndSaveConfig("http://example.com/config", "downloaded.properties");
            
        } catch (IOException e) {
            System.err.println("設定管理エラー: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
```

**このプログラムから学ぶ重要なtry-with-resources の概念：**

1. **自動リソース管理**：try-with-resources文により、ファイルストリーム、ネットワーク接続、ロックなどのリソースが確実に解放されます。

2. **複数リソースの同時管理**：1つのtry文で複数のリソースを同時に管理し、逆順で自動的に閉じられます。

3. **例外安全性**：リソースのクローズ処理中に例外が発生しても、すべてのリソースが適切に解放されます。

4. **AutoCloseableインターフェイス**：カスタムクラスでAutoCloseableを実装することで、try-with-resourcesの恩恵を受けられます。

5. **可読性の向上**：finally節での明示的なクローズ処理が不要になり、コードがより簡潔で理解しやすくなります。

**従来のfinally節との比較：**

```java
// 従来の方法（非推奨）
FileInputStream fis = null;
try {
    fis = new FileInputStream("file.txt");
    // ファイル処理
} catch (IOException e) {
    // エラー処理
} finally {
    if (fis != null) {
        try {
            fis.close();
        } catch (IOException e) {
            // クローズエラーの処理
        }
    }
}

// try-with-resources（推奨）
try (FileInputStream fis = new FileInputStream("file.txt")) {
    // ファイル処理
} catch (IOException e) {
    // エラー処理
}
```

**実用的な応用場面：**

- **データベース接続**：Connection、PreparedStatement、ResultSetの管理
- **ファイル処理**：大量ファイルの一括処理システム
- **ネットワーク通信**：HTTP接続、ソケット通信の安全な管理
- **並行処理**：ロックやセマフォの確実な解放

## まとめ

適切な例外処理により、堅牢なアプリケーションを作成できます。この章で学習した内容：

1. **基本的な例外処理**：try-catch-finally文による安全なエラーハンドリング
2. **複数例外の処理**：例外の階層と適切な順序での処理
3. **カスタム例外**：ビジネスロジックに特化した意味のある例外設計
4. **try-with-resources**：リソースの自動管理による安全なプログラミング

これらの技術を適切に組み合わせることで、エラーに強く、保守性の高いJavaアプリケーションを開発することができます。例外処理は単なる「エラー対応」ではなく、システムの信頼性と品質を根本的に向上させる重要な設計手法です。