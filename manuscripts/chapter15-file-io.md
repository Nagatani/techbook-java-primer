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

**リポジトリ**: `https://github.com/[your-repo]/java-primer-exercises`

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

Javaのプリミティブデータ型（`int`, `double`, `boolean`など）や文字列を、プラットフォームに依存しないバイナリ形式で読み書きするために使用します：

<span class="listing-number">**サンプルコード15-6**</span>
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

<span class="listing-number">**サンプルコード15-8**</span>
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

<span class="listing-number">**サンプルコード15-9**</span>
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

本章で学んだ基本的なファイルI/Oをさらに発展させたい方は、GitHubリポジトリの付録資料を参照してください：

**付録リソース**: `/appendix/b15-nio2-advanced/`

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
