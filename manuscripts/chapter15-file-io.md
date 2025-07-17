# <b>15章</b> <span>ファイルI/O</span> <small>外部データとの安全な連携</small>

## 本章の学習目標

### 前提知識

**必須**：
- 第14章の例外処理の概念と技術
- 第10章までの基本的なJavaプログラミング能力
- コレクション、ラムダ式、Stream API
- try-with-resourcesの理解と実践

**推奨**：
- ファイルシステムの基本概念（ファイル、ディレクトリ、パス）
- 文字エンコーディングの基本知識（UTF-8、Shift_JIS、ASCIIなど）

### 学習目標

本章では、JavaにおけるファイルI/O操作の包括的な知識と技術を習得します。知識理解の面では、まずファイルI/Oの基本概念とその大切さを理解します。ファイルI/Oは、プログラムが外部データと連携し、永続化やデータ交換を実現するための基礎技術です。InputStream/OutputStreamとReader/Writerの違いと適切な使い分けを学び、バイトストリームと文字ストリームの特性を理解します。NIO.2による現代的なファイル操作も大切な学習ポイントで、FilesクラスやPathインターフェイスを使ったより簡潔で安全なファイル操作を学びます。文字エンコーディングとその考慮事項も欠かせないテーマで、グローバルなアプリケーション開発において不可欠な知識です。

技能習得の面では、各種I/Oクラスの適切な選択と使用方法をマスターします。BufferedReader、FileInputStream、Filesクラスなどの特性を理解し、状況に応じた最適な選択ができるようになります。try-with-resourcesによる確実なリソース管理も大切なスキルで、メモリリークやリソースの枚渇を防ぐための正しい方法を学びます。テキストファイルとバイナリファイルの処理方法の違いを理解し、ファイル検索とディレクトリ操作の実装技術も習得します。

システムプログラミング能力の観点からは、外部データとの安全で効率的な連携方法を学びます。これには、ファイルの整合性チェック、トランザクション的な操作、同期化処理などが含まれます。大容量ファイルの効率的な処理技術も大切で、メモリ使用量を抱えながら大量のデータを扱う方法を学びます。エラー処理を含む堅牢なI/Oプログラムの実装も必要で、ネットワーク障害、ディスクエラー、アクセス権の問題などに対する適切な対応方法を習得します。

最終的な到達レベルとしては、さまざまな形式のファイル処理プログラムを実装できます。CSV、JSON、XMLなどの一般的なデータ形式や、バイナリファイルの処理に対応できます。大容量データの効率的な読み書きプログラムを作成し、ネットワークリソースを含む外部リソースを安全に扱えます。最終的に、文字エンコーディングを適切に考慮した国際化対応のプログラムが実装できるようになることが、本章の最終目標です。



## 章末演習

### 演習課題へのアクセス
本章の演習課題は、GitHubリポジトリで提供されています：
`https://github.com/Nagatani/techbook-java-primer/tree/main/exercises/chapter15/`

### 課題構成
- **基礎課題**: 本章の基本概念の理解確認
- **発展課題**: 応用的な実装練習
- **チャレンジ課題**: 実践的な総合問題

詳細な課題内容と実装のヒントは、各課題フォルダ内のREADME.mdを参照してください。








## 完了確認チェックリスト

### 基礎レベル
- 基本的なファイル読み書きができている
- ディレクトリ操作とファイル検索ができている
- CSVデータの処理ができている
- ログファイルの解析ができている

### 技術要素
- NIO.2のFilesクラスを適切に使用できている
- 大容量ファイルの効率的な処理ができている
- 文字エンコーディングを適切に処理できている
- try-with-resourcesでリソース管理ができている

### 応用レベル
- パフォーマンスを考慮した実装ができている
- 複雑なファイル処理システムが構築できている
- エラーハンドリングが適切に実装できている
- 実用的なファイル処理ツールが作成できている

次のステップ: 基本課題が完了したら、advanced/の発展課題でより高度なファイル処理に挑戦しましょう！

## ファイルI/Oの基礎とストリーム

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

## テキストファイルの読み書き

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
- UTF-8: 現在最も広く使用される可変長エンコーディング
- Shift_JIS (MS932): Windows環境で使用される日本語エンコーディング
- ISO-8859-1: 西欧言語用の1バイトエンコーディング

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

## バイナリファイルの高度な処理

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

## オブジェクトの直列化（シリアライズ）

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

## ファイルシステムの操作 (NIO.2)

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

## 実践的なファイル処理例

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

## 日付時刻処理について

ファイル操作でしばしば必要となる日付時刻処理については、Java 8で導入されたjava.time APIを使用することを強く推奨します。従来のjava.util.DateやCalendarクラスは多くの問題があるため、新しいAPIを使用してください。

java.time APIの詳細な使い方については、**付録F: java.time API完全ガイド**を参照してください。そこでは以下の内容を詳しく解説しています：

- レガシーDate/Calendarの問題点
- LocalDate、LocalTime、LocalDateTime、ZonedDateTimeの使い分け
- 日付時刻のフォーマットとパース
- 期間計算（PeriodとDuration）
- タイムゾーンの扱い
- ファイル操作での実践例
- レガシーAPIとの相互変換

本章では、ファイル操作に最低限必要な日付時刻処理のみを扱い、詳細な日付時刻処理は付録Fで学習してください。

## GUIでのファイル選択: `JFileChooser`

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

## Text Blocks - 複数行文字列の効率的な処理

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
                - ユーザー認証機能の実装
                - データベース接続の設定
                
                ### 優先度: 中
                - ログ機能の改善
                - エラーハンドリングの強化
                
                ### 優先度: 低
                - UI の改善
                - パフォーマンス最適化
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

※ 本章の高度な内容については、付録B12「高度なファイルI/O」を参照してください。

## まとめ

-   JavaのファイルI/Oは**ストリーム**ベースです。
-   リソース管理には**`try-with-resources`**を使い、安全性を高めましょう。
-   テキストファイルは**キャラクタストリーム**（`Reader`/`Writer`）、バイナリファイルは**バイトストリーム**（`InputStream`/`OutputStream`）で扱います。
-   **オブジェクト直列化**は、オブジェクトの状態を簡単に保存・復元する強力な手段です。
-   **NIO.2 (`java.nio.file`)** を使うと、モダンで高機能なファイルシステム操作が可能です。
-   GUIでは**`JFileChooser`**を使って、ユーザーにファイルを選択させることができます。

## よくあるエラーと対処法

ファイルI/O操作において開発者が遭遇しやすいエラーとその対処法を理解しておくことは、堅牢なアプリケーションを開発するために重要です。

### 1. ファイルパスの問題

**問題**: 相対パスや絶対パスの混在によるファイルが見つからないエラー

```java
// 悪い例
File file = new File("data/config.txt");
if (!file.exists()) {
    System.out.println("ファイルが見つかりません");
}
```

**エラーメッセージ**: `FileNotFoundException` または `NoSuchFileException`

**対処法**: 適切なパス管理と存在チェックを実装する

```java
// 良い例
Path configPath = Paths.get("data", "config.txt");
Path absolutePath = configPath.toAbsolutePath();

System.out.println("検索パス: " + absolutePath);

if (Files.exists(configPath)) {
    try {
        String content = Files.readString(configPath);
        System.out.println("設定を読み込みました");
    } catch (IOException e) {
        System.err.println("ファイル読み込みエラー: " + e.getMessage());
    }
} else {
    System.err.println("設定ファイルが見つかりません: " + absolutePath);
    // デフォルト設定を作成
    createDefaultConfig(configPath);
}
```

### 2. エンコーディングの問題

**問題**: 文字化けや不正な文字でのファイル読み書き

```java
// 悪い例
FileReader reader = new FileReader("japanese.txt");
BufferedReader br = new BufferedReader(reader);
String line = br.readLine(); // 文字化けする可能性
```

**エラーメッセージ**: `MalformedInputException` または文字化けした出力

**対処法**: 明示的にエンコーディングを指定する

```java
// 良い例
try (BufferedReader reader = Files.newBufferedReader(
        Paths.get("japanese.txt"), StandardCharsets.UTF_8)) {
    
    String line;
    while ((line = reader.readLine()) != null) {
        System.out.println(line);
    }
} catch (IOException e) {
    System.err.println("ファイル読み込みエラー: " + e.getMessage());
}

// 書き込み時も同様
try (BufferedWriter writer = Files.newBufferedWriter(
        Paths.get("output.txt"), StandardCharsets.UTF_8)) {
    writer.write("日本語のテキスト");
} catch (IOException e) {
    System.err.println("ファイル書き込みエラー: " + e.getMessage());
}
```

### 3. ファイルロックとアクセス権限の問題

**問題**: ファイルが他のプロセスによって使用されている、または権限不足

```java
// 問題のあるコード
try {
    Files.write(Paths.get("C:/Windows/system.log"), "data".getBytes());
} catch (IOException e) {
    e.printStackTrace(); // 権限エラーの詳細が不明
}
```

**エラーメッセージ**: `AccessDeniedException` または `FileSystemException`

**対処法**: 適切な権限チェックとエラーハンドリングを実装する

```java
// 良い例
Path logFile = Paths.get("logs", "application.log");

try {
    // ディレクトリの存在チェックと作成
    Files.createDirectories(logFile.getParent());
    
    // 書き込み権限の確認
    if (!Files.isWritable(logFile.getParent())) {
        throw new IOException("ログディレクトリに書き込み権限がありません");
    }
    
    // ファイルロックを考慮した書き込み
    try (FileChannel channel = FileChannel.open(logFile, 
            StandardOpenOption.CREATE, 
            StandardOpenOption.APPEND)) {
        
        try (FileLock lock = channel.tryLock()) {
            if (lock != null) {
                channel.write(ByteBuffer.wrap("ログデータ\n".getBytes()));
            } else {
                System.err.println("ファイルがロックされています");
            }
        }
    }
    
} catch (AccessDeniedException e) {
    System.err.println("アクセス権限エラー: " + e.getMessage());
} catch (IOException e) {
    System.err.println("ファイル操作エラー: " + e.getMessage());
}
```

### 4. メモリ効率の問題

**問題**: 大きなファイルを一度にメモリに読み込む

```java
// 悪い例（大きなファイルでOutOfMemoryError）
String content = Files.readString(Paths.get("large_file.txt"));
String[] lines = content.split("\n");
```

**エラーメッセージ**: `OutOfMemoryError`

**対処法**: ストリーミング処理やBufferedReaderを使用する

```java
// 良い例1: ストリーミング処理
try (Stream<String> lines = Files.lines(Paths.get("large_file.txt"))) {
    lines.filter(line -> line.contains("ERROR"))
         .forEach(System.out::println);
} catch (IOException e) {
    System.err.println("ファイル読み込みエラー: " + e.getMessage());
}

// 良い例2: バッファリングによる効率的な処理
try (BufferedReader reader = Files.newBufferedReader(
        Paths.get("large_file.txt"))) {
    
    String line;
    int lineCount = 0;
    while ((line = reader.readLine()) != null) {
        if (line.contains("ERROR")) {
            System.out.println("Line " + lineCount + ": " + line);
        }
        lineCount++;
        
        // 進捗表示（10000行ごと）
        if (lineCount % 10000 == 0) {
            System.out.println("処理済み: " + lineCount + "行");
        }
    }
} catch (IOException e) {
    System.err.println("ファイル読み込みエラー: " + e.getMessage());
}
```

### 5. クロスプラットフォーム対応の問題

**問題**: 特定のOS固有のパス区切り文字やファイル名制限

```java
// 悪い例
File file = new File("data\\config\\settings.txt"); // Windows固有
String path = "/home/user/data.txt"; // Unix固有
```

**対処法**: `java.nio.file.Path`とシステムプロパティを活用する

```java
// 良い例
Path dataDir = Paths.get(System.getProperty("user.home"), "myapp", "data");
Path configFile = dataDir.resolve("config.properties");

// ディレクトリの作成
try {
    Files.createDirectories(dataDir);
    
    // プラットフォーム固有の区切り文字を自動処理
    System.out.println("設定ファイル: " + configFile.toAbsolutePath());
    
    // 無効な文字のチェック
    if (isValidFileName(configFile.getFileName().toString())) {
        // ファイル操作
        if (Files.notExists(configFile)) {
            Files.createFile(configFile);
        }
    }
    
} catch (IOException e) {
    System.err.println("ファイル操作エラー: " + e.getMessage());
}

private static boolean isValidFileName(String fileName) {
    // 各OS共通の無効文字をチェック
    String invalidChars = "<>:\"/\\|?*";
    for (char c : invalidChars.toCharArray()) {
        if (fileName.indexOf(c) >= 0) {
            return false;
        }
    }
    return true;
}
```

### 6. 並行ファイルアクセスの問題

**問題**: 複数のスレッドからの同時ファイルアクセス

```java
// 悪い例
public void appendLog(String message) {
    try (FileWriter writer = new FileWriter("app.log", true)) {
        writer.write(message + "\n");
    } catch (IOException e) {
        e.printStackTrace();
    }
}
```

**対処法**: 適切な同期化とファイルロックを実装する

```java
// 良い例
public class ThreadSafeFileLogger {
    private final Path logFile;
    private final Object lock = new Object();
    
    public ThreadSafeFileLogger(String filename) {
        this.logFile = Paths.get(filename);
    }
    
    public void appendLog(String message) {
        synchronized (lock) {
            try (FileChannel channel = FileChannel.open(logFile,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND)) {
                
                String timestampedMessage = LocalDateTime.now() + ": " + message + "\n";
                channel.write(ByteBuffer.wrap(timestampedMessage.getBytes()));
                
            } catch (IOException e) {
                System.err.println("ログ書き込みエラー: " + e.getMessage());
            }
        }
    }
}
```

### 7. 一時ファイルの管理問題

**問題**: 一時ファイルの削除忘れによるディスク容量の浪費

```java
// 悪い例
File tempFile = new File("temp_" + System.currentTimeMillis() + ".tmp");
// 処理後にファイルが残る
```

**対処法**: 適切な一時ファイル管理を実装する

```java
// 良い例
Path tempFile = null;
try {
    tempFile = Files.createTempFile("myapp_", ".tmp");
    System.out.println("一時ファイル: " + tempFile);
    
    // 一時ファイルでの処理
    Files.writeString(tempFile, "一時的なデータ");
    
    // 処理の実行
    processTemporaryFile(tempFile);
    
} catch (IOException e) {
    System.err.println("一時ファイル処理エラー: " + e.getMessage());
} finally {
    // 確実にクリーンアップ
    if (tempFile != null) {
        try {
            Files.deleteIfExists(tempFile);
            System.out.println("一時ファイルを削除しました");
        } catch (IOException e) {
            System.err.println("一時ファイル削除エラー: " + e.getMessage());
        }
    }
}

// JVMシャットダウン時の自動削除
Runtime.getRuntime().addShutdownHook(new Thread(() -> {
    try {
        Files.deleteIfExists(tempFile);
    } catch (IOException e) {
        // シャットダウン時なので、エラーログのみ
        System.err.println("シャットダウン時のファイル削除エラー: " + e.getMessage());
    }
}));
```

### デバッグのヒント

1. **ファイルパスの確認**:
   ```java
   Path path = Paths.get("config.txt");
   System.out.println("現在のディレクトリ: " + System.getProperty("user.dir"));
   System.out.println("絶対パス: " + path.toAbsolutePath());
   System.out.println("ファイル存在: " + Files.exists(path));
   ```

2. **ファイル属性の確認**:
   ```java
   if (Files.exists(path)) {
       System.out.println("読み取り可能: " + Files.isReadable(path));
       System.out.println("書き込み可能: " + Files.isWritable(path));
       System.out.println("ファイルサイズ: " + Files.size(path));
       System.out.println("最終更新: " + Files.getLastModifiedTime(path));
   }
   ```

3. **例外の詳細情報の活用**:
   ```java
   try {
       Files.readString(path);
   } catch (NoSuchFileException e) {
       System.err.println("ファイルが見つかりません: " + e.getFile());
   } catch (AccessDeniedException e) {
       System.err.println("アクセス権限エラー: " + e.getFile());
   } catch (IOException e) {
       System.err.println("I/Oエラー: " + e.getClass().getSimpleName() + " - " + e.getMessage());
   }
   ```

これらの対処法を理解し実践することで、より信頼性の高いファイルI/O処理を実装できるようになります。
