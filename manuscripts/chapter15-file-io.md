# 第15章 ファイルI/O<small>外部データとの安全な連携</small>

## 本章の学習目標

### 前提知識

本章を学習するためには、いくつかの大切な前提知識がポイントです。まずポイントとなる前提として、第14章で学んだ例外処理の概念と技術を十分に習得していることが大切です。ファイルI/O操作は、ネットワークやディスクアクセスなどの外部リソースを扱うため、さまざまな例外が発生する可能性があり、適切な例外処理が大切です。第10章までの基本的なJavaプログラミング能力もポイントで、コレクション、ラムダ式、Stream APIなどの技術を組み合わせて効率的なファイル処理を実装します。try-with-resourcesの理解と実践も特に大切で、リソースの管理において中心的な役割を果たします。

さらに、システム理解の観点からの前提として、ファイルシステムの基本概念を理解していることがあるとよいでしょう。これには、ファイル、ディレクトリ、パスの概念、絶対パスと相対パスの違い、ファイルのメタデータ（サイズ、作成日時、アクセス権など）についての基本的な知識が含まれます。文字エンコーディングの基本知識も大切で、UTF-8、Shift_JIS、ASCIIなどの主要なエンコーディングの特徴と違い、文字化けの問題やその対処方法について理解していることが、特に国際化が重要である現代のアプリケーション開発において必要です。

### 学習目標

本章では、JavaにおけるファイルI/O操作の包括的な知識と技術を習得します。知識理解の面では、まずファイルI/Oの基本概念とその大切さを理解します。ファイルI/Oは、プログラムが外部データと連携し、永続化やデータ交換を実現するための基礎技術です。InputStream/OutputStreamとReader/Writerの違いと適切な使い分けを学び、バイトストリームと文字ストリームの特性を理解します。NIO.2による現代的なファイル操作も大切な学習ポイントで、FilesクラスやPathインターフェイスを使ったより簡潔で安全なファイル操作を学びます。文字エンコーディングとその考慮事項も欠かせないテーマで、グローバルなアプリケーション開発において不可欠な知識です。

技能習得の面では、各種I/Oクラスの適切な選択と使用方法をマスターします。BufferedReader、FileInputStream、Filesクラスなどの特性を理解し、状況に応じた最適な選択ができるようになります。try-with-resourcesによる確実なリソース管理も大切なスキルで、メモリリークやリソースの枚渇を防ぐための正しい方法を学びます。テキストファイルとバイナリファイルの処理方法の違いを理解し、ファイル検索とディレクトリ操作の実装技術も習得します。

システムプログラミング能力の観点からは、外部データとの安全で効率的な連携方法を学びます。これには、ファイルの整合性チェック、トランザクション的な操作、同期化処理などが含まれます。大容量ファイルの効率的な処理技術も大切で、メモリ使用量を抱えながら大量のデータを扱う方法を学びます。エラー処理を含む堅牢なI/Oプログラムの実装も必要で、ネットワーク障害、ディスクエラー、アクセス権の問題などに対する適切な対応方法を習得します。

最終的な到達レベルとしては、さまざまな形式のファイル処理プログラムを実装できます。CSV、JSON、XMLなどの一般的なデータ形式や、バイナリファイルの処理に対応できます。大容量データの効率的な読み書きプログラムを作成し、ネットワークリソースを含む外部リソースを安全に扱えます。最終的に、文字エンコーディングを適切に考慮した国際化対応のプログラムが実装できるようになることが、本章の最終目標です。



## 章末演習

**リポジトリ**: `https://github.com/Nagatani/techbook-java-primer/tree/main/exercises`

### 課題
- ファイル読み書き（Files API）
- ディレクトリ操作
- CSV処理
- エラーハンドリング

詳細はリポジトリ参照。








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

<span class="listing-number">**サンプルコード15-1**</span>

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

<span class="listing-number">**サンプルコード15-2**</span>

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

<span class="listing-number">**サンプルコード15-3**</span>

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
        // テキストファイルの読み込み
        // BufferedReaderを使用することで、ファイルから効率的に行単位でデータを読み込めます。
        // 大容量ファイルでもメモリを節約しながら処理できるため、実用的なアプリケーションでは最も推奨される方法です。
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

        // 大きなファイルの効率的読み込み
        // readAllLines()は小さなファイル（数MBまで）に適しています。
        // 大容量ファイルではメモリ不足の原因となるため、上記の1行ずつ読み込み方式を使用してください。
        // 方法2: 全行を一度に読み込む (小さなファイル向き)
        System.out.println("\n--- 全行を一度に読み込み ---");
        // テキストファイルの一括読み込み
        // readAllLines()は小容量ファイル専用の便利メソッドです。
        // 設定ファイルや小さなデータファイルの処理に適していますが、大容量ファイルではOutOfMemoryErrorの原因となります。
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

<span class="listing-number">**サンプルコード15-4**</span>

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

        // ファイルへの書き込み
        // BufferedWriterを使用してテキストデータを効率的にファイルに書き込みます。
        // CREATE: ファイルが存在しない場合は新規作成
        // TRUNCATE_EXISTING: 既存ファイルがある場合は内容を消去して上書き
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
// 追記モード
// StandardOpenOption.APPENDを指定すると、既存ファイルの内容を保持したまま末尾にデータを追加できます。
// ログファイルの作成や、既存データを保持しながら新しい情報を追加する場合に使用します。
`StandardOpenOption.APPEND`を指定すると、ファイルの末尾に追記できます。

### 文字エンコーディングの指定

テキストファイルを扱う際には、文字コード（エンコーディング）の指定が大切です。文字コードが異なると、文字化けが発生する可能性があります。

**主要な文字エンコーディング**：
- **UTF-8**: 現在最も広く使用される可変長エンコーディング
- **Shift_JIS (MS932)**: Windows環境で使用される日本語エンコーディング
- **ISO-8859-1**: 西欧言語用の1バイトエンコーディング

<span class="listing-number">**サンプルコード15-5**</span>

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

// バイナリファイルの読み込み
// DataInputStream/DataOutputStreamは、プリミティブ型のデータを効率的にバイナリ形式で保存・読み込みするためのクラスです。
// テキスト形式と比較してファイルサイズが小さく、読み書き速度も高速です。
Javaのプリミティブデータ型（`int`, `double`, `boolean`など）や文字列を、プラットフォームに依存しないバイナリ形式で読み書きするために使用します：

<span class="listing-number">**サンプルコード15-6**</span>

```java
import java.io.*;

public class DataStreamExample {
    public static void main(String[] args) throws IOException {
        String filename = "data.bin";
        
        // バイナリファイルの書き込み
        // DataOutputStreamを使用してプリミティブ型のデータをバイナリ形式で保存します。
        // この方法により、データの型情報を保持したまま効率的にファイルに保存できます。
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

**大切な注意点**：
1. **順序の一致**: 書き込んだ順序と完全に同じ順序で読み込む必要があります
2. **エンディアン**: DataStream系は常にビッグエンディアン（ネットワークバイトオーダー）を使用
3. **文字列の制限**: `writeUTF()`は最大65535バイトまでの文字列しか扱えません

### BufferedInputStream/BufferedOutputStreamの活用

// Files クラスの使用
// NIO.2のFilesクラスは、従来のFileクラスよりも高機能で安全なファイル操作を提供します。
// パフォーマンス向上のためのバッファリング機能も内蔵されており、現代的なJavaアプリケーションでは最優先で使用するとよいAPIです。

これらのクラスは、内部バッファーを持つことで、`FileInputStream` や `FileOutputStream` のパフォーマンスを向上させます。ディスクアクセスの回数を減らせるため、特に大量のデータを扱う場合に有効です。

## 15.4 オブジェクトの直列化（シリアライズ）

Javaオブジェクトの状態をそのままバイト列に変換して保存するしくみを**直列化（シリアライズ）**、バイト列からオブジェクトを復元することを**非直列化（デシリアライズ）**と呼びます。オブジェクトの構造を保ったまま、簡単に保存・復元できる強力な機能です。

-   直列化したいクラスは`java.io.Serializable`インターフェイスを実装する必要があります。
-   `ObjectOutputStream`で直列化し、`ObjectInputStream`で非直列化します。
-   `transient`修飾子を付けたフィールドは直列化の対象外となります（パスワードなど）。

<span class="listing-number">**サンプルコード15-7**</span>

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

        // FileVisitor の実装
        // ObjectOutputStreamを使用してJavaオブジェクトをバイナリ形式でファイルに保存します。
        // オブジェクトの構造や状態をそのまま保持できるため、データの永続化やキャッシュ機能に便利です。
        // 1. 直列化してファイルに保存
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(filename))) {
            oos.writeObject(user);
            System.out.println("保存されたオブジェクト: " + user);
        } catch (IOException e) { e.printStackTrace(); }

        // ストリーム API との統合
        // ObjectInputStreamでファイルからオブジェクトを復元します。
        // オブジェクトの状態が完全に復元され、保存時と同じ構造とデータを持つオブジェクトが復元されます。
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

<span class="listing-number">**サンプルコード15-8**</span>

```java
import java.io.IOException;
import java.nio.file.*;

public class FileSystemExample {
    public static void main(String[] args) throws IOException {
        Path dir = Paths.get("my_temp_dir");
        Path file = dir.resolve("my_file.txt");

        // ディレクトリ操作
        // Files.createDirectory()は指定されたディレクトリを作成します。
        // createDirectories()を使用すると、必要に応じて親ディレクトリも同時に作成されます。
        // ディレクトリ作成
        if (Files.notExists(dir)) {
            Files.createDirectory(dir);
            System.out.println("ディレクトリを作成しました: " + dir);
        }

        // テキストファイルの一括書き込み
        // writeString()は小容量のテキストデータを簡単にファイルに保存できる便利メソッドです。
        // 内部的にはBufferedWriterが使用されており、効率的な書き込みが行われます。
        // ファイル作成と書き込み
        Files.writeString(file, "Hello, NIO.2!");
        System.out.println("ファイルを作成しました: " + file);

        // ファイル属性の取得
        // Files.copy()はファイルのコピーを行います。StandardCopyOptionで動作を制御できます。
        // REPLACE_EXISTING: 同名ファイルがある場合は上書き
        // COPY_ATTRIBUTES: ファイルの属性（作成日時、アクセス権など）もコピー
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

## 15.6 実践的なファイル処理例

### CSV ファイルの処理

// CSV ファイルの処理
// CSV（Comma-Separated Values）形式は、表計算データやデータベースのエクスポート・インポートでよく使われるテキスト形式です。
// Javaでは標準ライブラリだけでも基本的なCSV処理が可能ですが、複雑な処理では専用ライブラリ（Apache Commons CSV等）の使用を推奨します。
// 以下は基本的なCSV読み込み・書き込みの実装例です。

<span class="listing-number">**サンプルコード15-9**</span>

```java
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class CSVProcessingExample {
    public static void main(String[] args) {
        Path csvFile = Paths.get("sample.csv");
        
        // CSVファイルの作成（サンプルデータ）
        createSampleCSV(csvFile);
        
        // CSVファイルの読み込み
        List<List<String>> csvData = readCSV(csvFile);
        System.out.println("読み込んだCSVデータ:");
        csvData.forEach(row -> System.out.println(String.join(" | ", row)));
        
        // CSVファイルの書き込み
        writeCSV(Paths.get("output.csv"), csvData);
    }
    
    // CSVファイルの読み込み
    public static List<List<String>> readCSV(Path filePath) {
        List<List<String>> result = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                List<String> row = parseCSVLine(line);
                result.add(row);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    
    // 簡単なCSV行解析（引用符などの複雑なケースは考慮していません）
    private static List<String> parseCSVLine(String line) {
        return Arrays.asList(line.split(","));
    }
    
    // CSVファイルの書き込み
    public static void writeCSV(Path filePath, List<List<String>> data) {
        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8)) {
            for (List<String> row : data) {
                writer.write(String.join(",", row));
                writer.newLine();
            }
            System.out.println("CSVファイルを書き込みました: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // サンプルCSVファイルの作成
    private static void createSampleCSV(Path filePath) {
        List<List<String>> sampleData = Arrays.asList(
            Arrays.asList("名前", "年齢", "職業"),
            Arrays.asList("田中太郎", "30", "エンジニア"),
            Arrays.asList("佐藤花子", "25", "デザイナー"),
            Arrays.asList("鈴木一郎", "35", "マネージャー")
        );
        writeCSV(filePath, sampleData);
    }
}
```

## 15.7 java.time APIによる日付時刻処理

### レガシーDate/Calendarの問題点

Java 8以前は、日付時刻の処理に`java.util.Date`と`java.util.Calendar`を使用していましたが、これらのAPIには多くの問題がありました：

1. **可変性（ミュータブル）**: Dateオブジェクトは変更可能であるため、バグの温床となりやすい
2. **設計の不整合**: 月が0から始まる（1月が0）など、直感的でない仕様
3. **スレッドセーフではない**: SimpleDateFormatなどがスレッドセーフでないため、並行処理で問題が発生
4. **タイムゾーン処理の複雑さ**: タイムゾーンの扱いが難しく、エラーが起きやすい
5. **APIの使いにくさ**: 日付の計算や比較が直感的でない

### java.time APIの主要クラス

Java 8で導入された`java.time`パッケージは、これらの問題を解決する新しい日付時刻APIです：

<span class="listing-number">**サンプルコード15-11**</span>

```java
import java.time.*;
import java.time.format.DateTimeFormatter;

public class JavaTimeBasicExample {
    public static void main(String[] args) {
        // 1. LocalDate - 日付のみ（タイムゾーンなし）
        LocalDate today = LocalDate.now();
        LocalDate birthday = LocalDate.of(2000, 1, 15);
        System.out.println("今日: " + today);
        System.out.println("誕生日: " + birthday);
        
        // 2. LocalTime - 時刻のみ（タイムゾーンなし）
        LocalTime now = LocalTime.now();
        LocalTime meetingTime = LocalTime.of(14, 30);
        System.out.println("現在時刻: " + now);
        System.out.println("会議時刻: " + meetingTime);
        
        // 3. LocalDateTime - 日付と時刻（タイムゾーンなし）
        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime appointment = LocalDateTime.of(2024, 12, 25, 18, 0);
        System.out.println("現在の日時: " + currentDateTime);
        System.out.println("予定: " + appointment);
        
        // 4. ZonedDateTime - タイムゾーン付き日時
        ZonedDateTime tokyoTime = ZonedDateTime.now(ZoneId.of("Asia/Tokyo"));
        ZonedDateTime newYorkTime = ZonedDateTime.now(ZoneId.of("America/New_York"));
        System.out.println("東京: " + tokyoTime);
        System.out.println("ニューヨーク: " + newYorkTime);
        
        // 5. Instant - エポック秒（UTC基準のタイムスタンプ）
        Instant timestamp = Instant.now();
        System.out.println("タイムスタンプ: " + timestamp);
    }
}
```

### 日付時刻の作成と操作

java.time APIは不変（イミュータブル）であり、操作は常に新しいインスタンスを返します：

<span class="listing-number">**サンプルコード15-12**</span>

```java
import java.time.*;
import java.time.temporal.ChronoUnit;

public class DateTimeManipulationExample {
    public static void main(String[] args) {
        // 日付の操作
        LocalDate date = LocalDate.of(2024, 1, 1);
        LocalDate nextWeek = date.plusWeeks(1);
        LocalDate lastMonth = date.minusMonths(1);
        LocalDate nextYear = date.plusYears(1);
        
        System.out.println("元の日付: " + date);
        System.out.println("1週間後: " + nextWeek);
        System.out.println("1ヶ月前: " + lastMonth);
        System.out.println("1年後: " + nextYear);
        
        // 時刻の操作
        LocalTime time = LocalTime.of(10, 30);
        LocalTime after2Hours = time.plusHours(2);
        LocalTime before30Min = time.minusMinutes(30);
        
        System.out.println("\n元の時刻: " + time);
        System.out.println("2時間後: " + after2Hours);
        System.out.println("30分前: " + before30Min);
        
        // より柔軟な操作
        LocalDateTime dateTime = LocalDateTime.now();
        LocalDateTime modified = dateTime
            .withYear(2025)
            .withMonth(6)
            .withDayOfMonth(15)
            .withHour(14)
            .withMinute(0)
            .withSecond(0);
        
        System.out.println("\n現在: " + dateTime);
        System.out.println("変更後: " + modified);
        
        // 日付の比較
        LocalDate date1 = LocalDate.of(2024, 1, 1);
        LocalDate date2 = LocalDate.of(2024, 6, 1);
        
        System.out.println("\n日付の比較:");
        System.out.println("date1 < date2: " + date1.isBefore(date2));
        System.out.println("date1 > date2: " + date1.isAfter(date2));
        System.out.println("date1 == date2: " + date1.isEqual(date2));
    }
}
```

### フォーマットとパース（DateTimeFormatter）

日付時刻の文字列表現との相互変換には`DateTimeFormatter`を使用します：

<span class="listing-number">**サンプルコード15-13**</span>

```java
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

public class DateTimeFormatterExample {
    public static void main(String[] args) {
        LocalDateTime dateTime = LocalDateTime.of(2024, 12, 25, 14, 30, 45);
        
        // 1. 定義済みフォーマッタ
        System.out.println("ISO_LOCAL_DATE_TIME: " + 
            dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        // 2. ロケール対応フォーマッタ
        DateTimeFormatter japaneseFormatter = DateTimeFormatter
            .ofLocalizedDateTime(FormatStyle.FULL)
            .withLocale(Locale.JAPANESE);
        System.out.println("日本語形式: " + dateTime.format(japaneseFormatter));
        
        // 3. カスタムパターン
        DateTimeFormatter customFormatter = 
            DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH時mm分ss秒");
        System.out.println("カスタム形式: " + dateTime.format(customFormatter));
        
        // 4. よく使われるパターン
        DateTimeFormatter[] formatters = {
            DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("yyyyMMddHHmmss"),
            DateTimeFormatter.ofPattern("E, d MMM yyyy HH:mm:ss", Locale.ENGLISH)
        };
        
        System.out.println("\nさまざまな形式:");
        for (DateTimeFormatter formatter : formatters) {
            System.out.println(dateTime.format(formatter));
        }
        
        // 5. 文字列からのパース
        String dateStr = "2024-12-25 14:30:45";
        DateTimeFormatter parseFormatter = 
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime parsed = LocalDateTime.parse(dateStr, parseFormatter);
        System.out.println("\nパース結果: " + parsed);
    }
}
```

### 期間と期限の計算（Period、Duration）

日付や時刻の差を扱うために、`Period`（日付ベース）と`Duration`（時間ベース）を使用します：

<span class="listing-number">**サンプルコード15-14**</span>

```java
import java.time.*;
import java.time.temporal.ChronoUnit;

public class PeriodDurationExample {
    public static void main(String[] args) {
        // Period - 日付ベースの期間
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 3, 15);
        
        Period period = Period.between(startDate, endDate);
        System.out.println("期間: " + period); // P1Y2M14D
        System.out.println("詳細: " + period.getYears() + "年 " + 
                          period.getMonths() + "ヶ月 " + 
                          period.getDays() + "日");
        
        // Periodを使った日付の計算
        LocalDate futureDate = LocalDate.now().plus(Period.ofMonths(6));
        System.out.println("6ヶ月後: " + futureDate);
        
        // Duration - 時間ベースの期間
        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(17, 30);
        
        Duration duration = Duration.between(startTime, endTime);
        System.out.println("\n勤務時間: " + duration); // PT8H30M
        System.out.println("詳細: " + duration.toHours() + "時間 " + 
                          duration.toMinutesPart() + "分");
        
        // 日時間の日数計算
        LocalDate birth = LocalDate.of(2000, 1, 1);
        long daysAlive = ChronoUnit.DAYS.between(birth, LocalDate.now());
        System.out.println("\n生まれてから " + daysAlive + " 日経過");
        
        // インスタント間の経過時間
        Instant start = Instant.now();
        // 何か処理
        try { Thread.sleep(1000); } catch (InterruptedException e) {}
        Instant end = Instant.now();
        
        Duration elapsed = Duration.between(start, end);
        System.out.println("処理時間: " + elapsed.toMillis() + "ミリ秒");
    }
}
```

### タイムゾーンの扱い

グローバルなアプリケーションでは、タイムゾーンの適切な処理が重要です：

<span class="listing-number">**サンプルコード15-15**</span>

```java
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Set;

public class TimeZoneExample {
    public static void main(String[] args) {
        // 利用可能なタイムゾーン一覧（一部）
        Set<String> zones = ZoneId.getAvailableZoneIds();
        System.out.println("利用可能なタイムゾーン数: " + zones.size());
        zones.stream()
             .filter(z -> z.startsWith("Asia/"))
             .sorted()
             .limit(5)
             .forEach(System.out::println);
        
        // タイムゾーン付き日時の作成
        ZonedDateTime tokyoTime = ZonedDateTime.now(ZoneId.of("Asia/Tokyo"));
        ZonedDateTime londonTime = tokyoTime.withZoneSameInstant(
            ZoneId.of("Europe/London"));
        ZonedDateTime newYorkTime = tokyoTime.withZoneSameInstant(
            ZoneId.of("America/New_York"));
        
        DateTimeFormatter formatter = 
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
        
        System.out.println("\n同一時刻の各地表示:");
        System.out.println("東京: " + tokyoTime.format(formatter));
        System.out.println("ロンドン: " + londonTime.format(formatter));
        System.out.println("ニューヨーク: " + newYorkTime.format(formatter));
        
        // オフセット付き日時
        OffsetDateTime offsetDateTime = OffsetDateTime.now();
        System.out.println("\nオフセット付き日時: " + offsetDateTime);
        
        // 夏時間（DST）の考慮
        ZoneId nyZone = ZoneId.of("America/New_York");
        ZonedDateTime winterTime = ZonedDateTime.of(
            2024, 1, 15, 12, 0, 0, 0, nyZone);
        ZonedDateTime summerTime = ZonedDateTime.of(
            2024, 7, 15, 12, 0, 0, 0, nyZone);
        
        System.out.println("\n夏時間の影響:");
        System.out.println("冬時間: " + winterTime);
        System.out.println("夏時間: " + summerTime);
        System.out.println("オフセット差: " + 
            (winterTime.getOffset().getTotalSeconds() - 
             summerTime.getOffset().getTotalSeconds()) / 3600 + "時間");
    }
}
```

### ファイル操作での実践例

java.time APIをファイル操作と組み合わせた実践的な例：

<span class="listing-number">**サンプルコード15-16**</span>

```java
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class FileTimeExample {
    public static void main(String[] args) throws IOException, InterruptedException {
        Path logDir = Paths.get("logs");
        Files.createDirectories(logDir);
        
        // 1. タイムスタンプ付きログファイルの作成
        DateTimeFormatter fileNameFormatter = 
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String timestamp = LocalDateTime.now().format(fileNameFormatter);
        Path logFile = logDir.resolve("app_" + timestamp + ".log");
        
        // ログエントリーの書き込み
        DateTimeFormatter logFormatter = 
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        List<String> logEntries = new ArrayList<>();
        
        for (int i = 0; i < 5; i++) {
            String entry = String.format("[%s] INFO: Processing item %d",
                LocalDateTime.now().format(logFormatter), i);
            logEntries.add(entry);
            Thread.sleep(100); // 処理のシミュレーション
        }
        
        Files.write(logFile, logEntries);
        System.out.println("ログファイル作成: " + logFile);
        
        // 2. ファイルの更新日時を取得
        BasicFileAttributes attrs = Files.readAttributes(
            logFile, BasicFileAttributes.class);
        
        FileTime creationTime = attrs.creationTime();
        FileTime lastModified = attrs.lastModifiedTime();
        FileTime lastAccess = attrs.lastAccessTime();
        
        System.out.println("\nファイル時刻情報:");
        System.out.println("作成日時: " + 
            LocalDateTime.ofInstant(creationTime.toInstant(), 
                                  ZoneId.systemDefault()));
        System.out.println("最終更新: " + 
            LocalDateTime.ofInstant(lastModified.toInstant(), 
                                  ZoneId.systemDefault()));
        System.out.println("最終アクセス: " + 
            LocalDateTime.ofInstant(lastAccess.toInstant(), 
                                  ZoneId.systemDefault()));
        
        // 3. 古いログファイルの検索と削除
        LocalDateTime cutoffTime = LocalDateTime.now().minusDays(7);
        
        System.out.println("\n7日以上前のログファイルを検索...");
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(
                logDir, "*.log")) {
            for (Path file : stream) {
                FileTime fileTime = Files.getLastModifiedTime(file);
                LocalDateTime fileDateTime = LocalDateTime.ofInstant(
                    fileTime.toInstant(), ZoneId.systemDefault());
                
                if (fileDateTime.isBefore(cutoffTime)) {
                    System.out.println("古いファイル発見: " + file);
                    // Files.delete(file); // 実際の削除はコメントアウト
                }
            }
        }
        
        // 4. ファイルの更新日時を変更
        LocalDateTime newDateTime = LocalDateTime.now().minusHours(3);
        FileTime newFileTime = FileTime.from(
            newDateTime.atZone(ZoneId.systemDefault()).toInstant());
        Files.setLastModifiedTime(logFile, newFileTime);
        System.out.println("\n更新日時を3時間前に変更しました");
    }
}
```

### レガシーAPIとの相互変換

既存のコードとの互換性のため、java.time APIとレガシーAPIの間で変換が必要な場合があります：

<span class="listing-number">**サンプルコード15-17**</span>

```java
import java.time.*;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class LegacyConversionExample {
    public static void main(String[] args) {
        // 1. Date ←→ Instant
        Date legacyDate = new Date();
        Instant instant = legacyDate.toInstant();
        Date convertedDate = Date.from(instant);
        
        System.out.println("Legacy Date: " + legacyDate);
        System.out.println("Instant: " + instant);
        System.out.println("Converted Date: " + convertedDate);
        
        // 2. Date → LocalDateTime
        LocalDateTime localDateTime = LocalDateTime.ofInstant(
            legacyDate.toInstant(), ZoneId.systemDefault());
        System.out.println("\nLocalDateTime: " + localDateTime);
        
        // 3. LocalDateTime → Date
        ZonedDateTime zonedDateTime = localDateTime.atZone(
            ZoneId.systemDefault());
        Date fromLocalDateTime = Date.from(zonedDateTime.toInstant());
        System.out.println("Date from LocalDateTime: " + fromLocalDateTime);
        
        // 4. Calendar ←→ ZonedDateTime
        Calendar calendar = Calendar.getInstance();
        calendar.set(2024, Calendar.DECEMBER, 25, 14, 30, 0);
        
        ZonedDateTime fromCalendar = ((GregorianCalendar) calendar)
            .toZonedDateTime();
        System.out.println("\nCalendar → ZonedDateTime: " + fromCalendar);
        
        GregorianCalendar newCalendar = GregorianCalendar.from(
            ZonedDateTime.now());
        System.out.println("ZonedDateTime → Calendar: " + 
            newCalendar.getTime());
        
        // 5. java.sql.Date との変換
        java.sql.Date sqlDate = new java.sql.Date(
            System.currentTimeMillis());
        LocalDate localDate = sqlDate.toLocalDate();
        java.sql.Date convertedSqlDate = java.sql.Date.valueOf(localDate);
        
        System.out.println("\nSQL Date: " + sqlDate);
        System.out.println("LocalDate: " + localDate);
        System.out.println("Converted SQL Date: " + convertedSqlDate);
        
        // 6. タイムゾーンの変換
        TimeZone legacyTimeZone = TimeZone.getTimeZone("Asia/Tokyo");
        ZoneId zoneId = legacyTimeZone.toZoneId();
        TimeZone convertedTimeZone = TimeZone.getTimeZone(zoneId);
        
        System.out.println("\nLegacy TimeZone: " + legacyTimeZone.getID());
        System.out.println("ZoneId: " + zoneId);
        System.out.println("Converted TimeZone: " + 
            convertedTimeZone.getID());
    }
}
```

### java.time APIのベストプラクティス

1. **適切なクラスの選択**:
   - 日付のみ: `LocalDate`
   - 時刻のみ: `LocalTime`
   - 日付と時刻: `LocalDateTime`
   - タイムゾーン付き: `ZonedDateTime`
   - タイムスタンプ: `Instant`

2. **不変性の活用**: java.timeのクラスはすべて不変なので、スレッドセーフで安全に使用できます

3. **明示的なタイムゾーン指定**: グローバルアプリケーションでは常にタイムゾーンを明示的に扱う

4. **適切なフォーマッタの再利用**: `DateTimeFormatter`はスレッドセーフなので、静的フィールドとして再利用可能

これらの新しいAPIを使用することで、日付時刻処理のコードがより明確で、バグの少ないものになります。

## 15.8 GUIでのファイル選択: `JFileChooser`

Swingアプリケーションでユーザーにファイルを選択させるには、`JFileChooser`を使います。

<span class="listing-number">**サンプルコード15-18**</span>

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

## 15.9 Text Blocks - 複数行文字列の効率的な処理

Java 15で正式に導入された**Text Blocks**は、複数行にわたる文字列リテラルを簡潔で読みやすく記述するための機能です。特にファイルI/Oにおいて、JSON、XML、SQL文、HTMLテンプレートなどの構造化されたテキストデータを扱う際に非常に有用です。

### 従来の文字列連結の問題点

Text Blocksが導入される前は、複数行の文字列を作成するために文字列連結やエスケープシーケンスを多用する必要がありました：

<span class="listing-number">**サンプルコード15-19**</span>

```java
public class TraditionalStringProblems {
    public static void main(String[] args) {
        // 従来の方法：読みにくく、エラーが起きやすい
        String jsonData = "{\n" +
                "  \"name\": \"田中太郎\",\n" +
                "  \"age\": 30,\n" +
                "  \"address\": {\n" +
                "    \"city\": \"東京\",\n" +
                "    \"zipCode\": \"100-0001\"\n" +
                "  }\n" +
                "}";
        
        String sqlQuery = "SELECT u.name, u.email, p.title\n" +
                "FROM users u\n" +
                "INNER JOIN posts p ON u.id = p.user_id\n" +
                "WHERE u.created_at > '2024-01-01'\n" +
                "ORDER BY u.name";
        
        String htmlTemplate = "<html>\n" +
                "<head>\n" +
                "  <title>ユーザー情報</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "  <h1>プロフィール</h1>\n" +
                "  <p>名前: %s</p>\n" +
                "  <p>年齢: %d</p>\n" +
                "</body>\n" +
                "</html>";
        
        System.out.println("=== 従来の方法の問題点 ===");
        System.out.println("1. エスケープシーケンス（\\n, \\\"）が多くて読みにくい");
        System.out.println("2. 文字列連結（+）によるエラーが起きやすい");
        System.out.println("3. インデントの管理が困難");
        System.out.println("4. 引用符のエスケープが煩雑");
    }
}
```

### Text Blocksの基本構文

Text Blocksは三重引用符（`"""`）を使用して複数行文字列を定義します：

<span class="listing-number">**サンプルコード15-20**</span>

```java
public class TextBlockBasics {
    public static void main(String[] args) {
        // Text Blocksの基本構文
        String textBlock = """
                これは Text Block の例です。
                複数行にわたる文字列を
                簡潔に記述できます。
                """;
        
        System.out.println("=== Text Block の出力 ===");
        System.out.println(textBlock);
        
        // 従来の文字列との比較
        String traditional = "これは Text Block の例です。\n" +
                "複数行にわたる文字列を\n" +
                "簡潔に記述できます。\n";
        
        System.out.println("\n=== 従来の文字列との同等性 ===");
        System.out.println("同じ内容？: " + textBlock.equals(traditional));
        
        // 空のText Block
        String empty = """
                """;
        System.out.println("空のText Block: '" + empty + "'");
        
        // 1行のText Block
        String singleLine = """
                単一行のText Block
                """;
        System.out.println("1行のText Block: " + singleLine);
    }
}
```

### インデントの自動処理

Text Blocksの最も重要な特徴の一つは、**共通インデントの自動除去**です：

<span class="listing-number">**サンプルコード15-21**</span>

```java
public class TextBlockIndentation {
    public static void main(String[] args) {
        // インデントの自動処理
        String codeExample = """
                public class Example {
                    public void method() {
                        System.out.println("Hello");
                        if (true) {
                            System.out.println("Nested");
                        }
                    }
                }
                """;
        
        System.out.println("=== インデント自動処理の例 ===");
        System.out.println(codeExample);
        
        // 手動でインデントを制御
        String customIndent = """
            レベル1
                レベル2
                    レベル3
            レベル1に戻る
            """;
        
        System.out.println("\n=== カスタムインデント ===");
        System.out.println(customIndent);
        
        // 最も左にある行でインデントが決まる
        String mixedIndent = """
                    深いインデント
                中程度のインデント
            浅いインデント
                    再び深いインデント
            """;
        
        System.out.println("\n=== 混合インデント（最も浅い行が基準） ===");
        System.out.println(mixedIndent);
        
        // stripIndent()メソッドでインデント除去を明示的に実行
        String manualStrip = """
                        これは深いインデントです
                            さらに深いインデント
                        元のレベル
                        """.stripIndent();
        
        System.out.println("\n=== 手動stripIndent() ===");
        System.out.println(manualStrip);
    }
}
```

### エスケープシーケンスの簡略化

Text Blocksでは多くのエスケープシーケンスが不要になります：

<span class="listing-number">**サンプルコード15-22**</span>

```java
public class TextBlockEscaping {
    public static void main(String[] args) {
        // 引用符のエスケープが不要
        String dialogue = """
                "こんにちは"と彼は言った。
                '時は金なり'という諺がある。
                "引用符をエスケープする必要がない"
                """;
        
        System.out.println("=== 引用符のエスケープ不要 ===");
        System.out.println(dialogue);
        
        // バックスラッシュの処理
        String windowsPath = """
                Windowsのパス: C:\\Users\\Name\\Documents
                正規表現: \\d+\\.\\d+
                """;
        
        System.out.println("\n=== バックスラッシュの処理 ===");
        System.out.println(windowsPath);
        
        // 三重引用符を含む場合（エスケープが必要）
        String codeWithTripleQuotes = """
                Text Blockの構文:
                String text = \"""
                    複数行文字列
                    \""";
                """;
        
        System.out.println("\n=== 三重引用符のエスケープ ===");
        System.out.println(codeWithTripleQuotes);
        
        // 行末の制御
        String trailingSpaces = """
                この行の末尾にスペースがある   \s
                この行は普通の改行
                この行は改行なし\
                """;
        
        System.out.println("\n=== 行末の制御 ===");
        System.out.println("'" + trailingSpaces + "'");
    }
}
```

### 実践例：構造化データの処理

Text BlocksはJSON、XML、SQL、HTMLなどの構造化データを扱う際に特に威力を発揮します：

<span class="listing-number">**サンプルコード15-23**</span>

```java
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TextBlockStructuredData {
    public static void main(String[] args) throws IOException {
        // 1. JSON データの生成
        String userName = "田中太郎";
        int userAge = 30;
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        
        String jsonData = """
                {
                  "user": {
                    "name": "%s",
                    "age": %d,
                    "timestamp": "%s",
                    "preferences": {
                      "language": "Japanese",
                      "theme": "dark",
                      "notifications": true
                    }
                  },
                  "metadata": {
                    "version": "1.0",
                    "created": "%s"
                  }
                }
                """.formatted(userName, userAge, timestamp, timestamp);
        
        // JSONファイルとして保存
        Path jsonFile = Paths.get("user_data.json");
        Files.writeString(jsonFile, jsonData);
        System.out.println("=== JSON データを保存しました ===");
        System.out.println(jsonData);
        
        // 2. SQL クエリの生成
        String tableName = "users";
        String condition = "2024-01-01";
        
        String sqlQuery = """
                SELECT
                    u.id,
                    u.name,
                    u.email,
                    u.created_at,
                    p.title AS latest_post,
                    COUNT(c.id) AS comment_count
                FROM %s u
                LEFT JOIN posts p ON u.id = p.user_id 
                    AND p.created_at = (
                        SELECT MAX(created_at) 
                        FROM posts 
                        WHERE user_id = u.id
                    )
                LEFT JOIN comments c ON p.id = c.post_id
                WHERE u.created_at > '%s'
                GROUP BY u.id, u.name, u.email, u.created_at, p.title
                ORDER BY u.created_at DESC
                LIMIT 100;
                """.formatted(tableName, condition);
        
        Path sqlFile = Paths.get("user_query.sql");
        Files.writeString(sqlFile, sqlQuery);
        System.out.println("\n=== SQL クエリを保存しました ===");
        System.out.println(sqlQuery);
        
        // 3. HTML テンプレートの生成
        String pageTitle = "ユーザープロフィール";
        
        String htmlTemplate = """
                <!DOCTYPE html>
                <html lang="ja">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>%s</title>
                    <style>
                        body {
                            font-family: 'Helvetica Neue', Arial, sans-serif;
                            max-width: 800px;
                            margin: 0 auto;
                            padding: 20px;
                            background-color: #f5f5f5;
                        }
                        .profile-card {
                            background: white;
                            border-radius: 8px;
                            padding: 24px;
                            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
                        }
                        .user-name {
                            color: #333;
                            font-size: 24px;
                            margin-bottom: 8px;
                        }
                        .user-details {
                            color: #666;
                            line-height: 1.6;
                        }
                    </style>
                </head>
                <body>
                    <div class="profile-card">
                        <h1 class="user-name">%s</h1>
                        <div class="user-details">
                            <p><strong>年齢:</strong> %d歳</p>
                            <p><strong>最終更新:</strong> %s</p>
                            <p><strong>ステータス:</strong> アクティブ</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(pageTitle, userName, userAge, timestamp);
        
        Path htmlFile = Paths.get("user_profile.html");
        Files.writeString(htmlFile, htmlTemplate);
        System.out.println("\n=== HTML テンプレートを保存しました ===");
        System.out.println("ファイル: " + htmlFile.toAbsolutePath());
        
        // 4. XML 設定ファイルの生成
        String appName = "UserManager";
        String version = "2.1.0";
        
        String xmlConfig = """
                <?xml version="1.0" encoding="UTF-8"?>
                <configuration>
                    <application>
                        <name>%s</name>
                        <version>%s</version>
                        <environment>production</environment>
                    </application>
                    <database>
                        <url>jdbc:postgresql://localhost:5432/userdb</url>
                        <username>app_user</username>
                        <pool>
                            <min-connections>5</min-connections>
                            <max-connections>20</max-connections>
                            <timeout>30000</timeout>
                        </pool>
                    </database>
                    <logging>
                        <level>INFO</level>
                        <file>logs/application.log</file>
                        <pattern>%%d{yyyy-MM-dd HH:mm:ss} [%%thread] %%-5level %%logger{36} - %%msg%%n</pattern>
                    </logging>
                </configuration>
                """.formatted(appName, version);
        
        Path xmlFile = Paths.get("app_config.xml");
        Files.writeString(xmlFile, xmlConfig);
        System.out.println("\n=== XML 設定ファイルを保存しました ===");
        
        // 生成されたファイルの一覧表示
        System.out.println("\n=== 生成されたファイル一覧 ===");
        Path currentDir = Paths.get(".");
        Files.list(currentDir)
                .filter(p -> p.toString().matches(".*\\.(json|sql|html|xml)$"))
                .forEach(p -> {
                    try {
                        long size = Files.size(p);
                        System.out.printf("%-20s (%d bytes)%n", 
                                p.getFileName(), size);
                    } catch (IOException e) {
                        System.out.println(p.getFileName() + " (サイズ取得エラー)");
                    }
                });
    }
}
```

### ファイルI/Oでの活用例

Text Blocksをファイル読み書きと組み合わせた実践的な使用例：

<span class="listing-number">**サンプルコード15-24**</span>

```java
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.IntStream;

public class TextBlockFileIO {
    public static void main(String[] args) throws IOException {
        // 1. ログファイルテンプレートの生成
        generateLogTemplate();
        
        // 2. 設定ファイルの読み書き
        handleConfigurationFile();
        
        // 3. レポート生成
        generateReport();
        
        // 4. 複数ファイルの一括処理
        batchProcessFiles();
    }
    
    private static void generateLogTemplate() throws IOException {
        String logTemplate = """
                # アプリケーションログ設定
                # 生成日時: %s
                
                ## ログレベル設定
                root.level=INFO
                com.example.app=DEBUG
                com.example.db=WARN
                
                ## 出力先設定
                console.enabled=true
                file.enabled=true
                file.path=logs/app-%s.log
                file.maxSize=10MB
                file.maxHistory=30
                
                ## フォーマット設定
                pattern.console=%%d{HH:mm:ss} [%%thread] %%-5level %%logger{0} - %%msg%%n
                pattern.file=%%d{yyyy-MM-dd HH:mm:ss.SSS} [%%thread] %%-5level %%logger{36} - %%msg%%n
                """.formatted(
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        
        Path logConfigPath = Paths.get("log_config.properties");
        Files.writeString(logConfigPath, logTemplate);
        System.out.println("ログ設定ファイルを生成しました: " + logConfigPath);
    }
    
    private static void handleConfigurationFile() throws IOException {
        // デフォルト設定の作成
        String defaultConfig = """
                # アプリケーション設定ファイル
                app.name=Java Primer Sample
                app.version=1.0.0
                
                # データベース設定
                db.url=jdbc:h2:mem:testdb
                db.username=sa
                db.password=
                db.pool.size=10
                
                # キャッシュ設定
                cache.enabled=true
                cache.size=1000
                cache.ttl=300
                
                # ファイル設定
                files.upload.dir=uploads
                files.max.size=10485760
                files.allowed.types=jpg,png,gif,pdf,doc,docx
                """;
        
        Path configPath = Paths.get("application.properties");
        
        // 設定ファイルが存在しない場合のみ作成
        if (Files.notExists(configPath)) {
            Files.writeString(configPath, defaultConfig);
            System.out.println("デフォルト設定ファイルを作成しました");
        } else {
            // 既存設定ファイルの読み込みと表示
            String existingConfig = Files.readString(configPath);
            System.out.println("既存の設定ファイルを読み込みました:");
            System.out.println(existingConfig.substring(0, 
                    Math.min(200, existingConfig.length())) + "...");
        }
    }
    
    private static void generateReport() throws IOException {
        // サンプルデータ
        String[][] userData = {
                {"田中太郎", "30", "東京", "エンジニア"},
                {"佐藤花子", "25", "大阪", "デザイナー"},
                {"鈴木一郎", "35", "名古屋", "マネージャー"},
                {"高橋美咲", "28", "福岡", "アナリスト"}
        };
        
        // HTML レポートの生成
        StringBuilder tableRows = new StringBuilder();
        for (String[] user : userData) {
            tableRows.append("""
                    <tr>
                        <td>%s</td>
                        <td>%s歳</td>
                        <td>%s</td>
                        <td>%s</td>
                    </tr>
                    """.formatted(user[0], user[1], user[2], user[3]));
        }
        
        String reportHtml = """
                <!DOCTYPE html>
                <html lang="ja">
                <head>
                    <meta charset="UTF-8">
                    <title>ユーザーレポート</title>
                    <style>
                        body { font-family: 'Yu Gothic', sans-serif; margin: 40px; }
                        h1 { color: #2c3e50; border-bottom: 3px solid #3498db; padding-bottom: 10px; }
                        table { border-collapse: collapse; width: 100%%; margin-top: 20px; }
                        th, td { border: 1px solid #ddd; padding: 12px; text-align: left; }
                        th { background-color: #f2f2f2; font-weight: bold; }
                        tr:nth-child(even) { background-color: #f9f9f9; }
                        .meta { color: #666; font-size: 14px; margin-top: 20px; }
                    </style>
                </head>
                <body>
                    <h1>📊 ユーザー管理システム - 月次レポート</h1>
                    <p>レポート生成日時: %s</p>
                    
                    <table>
                        <thead>
                            <tr>
                                <th>名前</th>
                                <th>年齢</th>
                                <th>所在地</th>
                                <th>職業</th>
                            </tr>
                        </thead>
                        <tbody>
                %s
                        </tbody>
                    </table>
                    
                    <div class="meta">
                        <p>総ユーザー数: %d名</p>
                        <p>平均年齢: %.1f歳</p>
                    </div>
                </body>
                </html>
                """.formatted(
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm")),
                tableRows.toString(),
                userData.length,
                java.util.Arrays.stream(userData)
                        .mapToInt(user -> Integer.parseInt(user[1]))
                        .average().orElse(0.0));
        
        Path reportPath = Paths.get("user_report.html");
        Files.writeString(reportPath, reportHtml);
        System.out.println("HTMLレポートを生成しました: " + reportPath);
    }
    
    private static void batchProcessFiles() throws IOException {
        // 複数のサンプルファイルを一括生成
        String[] fileContents = {
                """
                # README.md
                
                ## Java Primer Sample Project
                
                このプロジェクトはJava学習用のサンプルプロジェクトです。
                
                ### 機能
                - ファイルI/O操作
                - Text Blocks の活用
                - 設定ファイルの管理
                """,
                
                """
                # TODO.md
                
                ## 開発タスク
                
                ### 優先度: 高
                - [ ] ユーザー認証機能の実装
                - [ ] データベース接続の設定
                
                ### 優先度: 中
                - [ ] ログ機能の改善
                - [ ] エラーハンドリングの強化
                
                ### 優先度: 低
                - [ ] UI の改善
                - [ ] パフォーマンス最適化
                """,
                
                """
                {
                  "project": {
                    "name": "Java Primer Sample",
                    "version": "1.0.0",
                    "description": "Text Blocks機能のデモンストレーション",
                    "author": "Java学習者",
                    "dependencies": [
                      "java.base",
                      "java.desktop"
                    ],
                    "features": [
                      "ファイルI/O",
                      "Text Blocks",
                      "JSON処理",
                      "HTML生成"
                    ]
                  }
                }
                """
        };
        
        String[] fileNames = {"README.md", "TODO.md", "project.json"};
        
        for (int i = 0; i < fileNames.length; i++) {
            Path filePath = Paths.get(fileNames[i]);
            Files.writeString(filePath, fileContents[i]);
            System.out.println("生成しました: " + filePath);
        }
        
        System.out.println("\n=== 一括ファイル処理完了 ===");
    }
}
```

### Text Blocksのベストプラクティス

1. **構造化データでの活用**: JSON、XML、SQL、HTMLなどの複雑な構造を持つテキストでText Blocksの真価が発揮されます。

2. **適切なインデント管理**: 最も左の行がインデントの基準となることを理解して使用しましょう。

3. **文字列フォーマッティングとの組み合わせ**: `formatted()`メソッドや`String.format()`と組み合わせることで、動的なコンテンツ生成が可能です。

4. **ファイルテンプレートとしての利用**: 設定ファイル、レポート、コード生成のテンプレートとして活用できます。

5. **可読性の向上**: 従来のエスケープシーケンスを多用した文字列と比較して、大幅に可読性が向上します。

Text Blocksは、特にファイルI/O処理において構造化されたテキストデータを扱う際の強力なツールです。JSON APIの応答処理、SQLクエリの構築、HTMLテンプレートの生成など、現代的なJavaアプリケーション開発において欠かせない機能となっています。

## より深い理解のために

本章で学んだ基本的なファイルI/Oをさらに発展させたい方は、GitHubリポジトリの付録資料を参照してください：

**付録リソース**: `https://github.com/Nagatani/techbook-java-primer/tree/main/appendix/b15-nio2-advanced/`

この付録では以下の高度なトピックを扱います：

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
