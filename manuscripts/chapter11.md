# 第11章 ファイル入出力とリソース管理

## はじめに：永続化技術の進化とI/Oシステムの重要性

前章まででJavaの中核的な機能について学習してきました。この章では、実用的なアプリケーション開発において不可欠な「ファイル入出力（I/O: Input/Output）」と「リソース管理」について詳細に学習していきます。

ファイル入出力は、プログラムが外部世界と情報をやり取りするための基本的な手段です。しかし、これは単なる技術的な機能ではありません。データの永続化、システム間の連携、ユーザーとの情報交換など、現代のソフトウェアシステムの根幹を支える重要な仕組みです。

### データ永続化の歴史的発展

コンピュータシステムにおけるデータの永続化（persistence）は、システムの電源が切れてもデータが失われることなく保存され、後で再利用できる仕組みです。この技術は、コンピュータの発展とともに段階的に進化してきました。

**初期のデータ保存（1940年代〜1950年代）**：初期のコンピュータでは、データはパンチカードや磁気テープに保存されていました。これらの媒体は順次アクセス（sequential access）しかできず、特定のデータを検索するには最初から順番に読み取る必要がありました。

**磁気ディスクの登場（1950年代〜1960年代）**：IBMによって開発された磁気ディスクドライブにより、ランダムアクセス（random access）が可能になりました。これにより、ファイルシステムという概念が生まれ、階層的なディレクトリ構造でデータを管理できるようになりました。

**ファイルシステムの標準化（1970年代〜1980年代）**：UNIX系オペレーティングシステムにより、現代的なファイルシステムの基本概念（ディレクトリ、権限、リンクなど）が確立されました。「Everything is a file」という哲学により、デバイスやプロセス間通信もファイルとして抽象化されました。

**データベースシステムの発展（1980年代〜1990年代）**：構造化されたデータの管理において、リレーショナルデータベース管理システム（RDBMS）が主流となりました。しかし、非構造化データや設定情報の保存には、依然としてファイルシステムが重要な役割を果たし続けました。

### プログラミング言語におけるI/O処理の進化

プログラミング言語におけるI/O処理は、低レベルなハードウェア操作から、高レベルな抽象化まで段階的に発展してきました：

**低レベルI/O（1950年代〜1960年代）**：初期のプログラミング言語では、プログラマーがハードウェアの詳細を直接制御する必要がありました。磁気テープの巻き戻し、ディスクヘッドの移動など、物理的な操作を意識したプログラミングが必要でした。

**標準I/Oライブラリの確立（1970年代〜）**：C言語のstdio.hライブラリに代表されるように、OSレベルでのI/O操作を抽象化した標準ライブラリが普及しました。fopen、fread、fwriteなどの関数により、ポータブルなI/O処理が可能になりました。

```c
FILE *file = fopen("data.txt", "r");
if (file != NULL) {
    char buffer[1024];
    fread(buffer, sizeof(char), 1024, file);
    fclose(file);
}
```

**オブジェクト指向I/O（1990年代〜）**：C++のiostreamライブラリやJavaのjava.ioパッケージにより、I/O操作がオブジェクト指向的に再設計されました。ストリームという概念により、様々なデータソース（ファイル、ネットワーク、メモリなど）を統一的に扱えるようになりました。

### JavaにおけるI/Oシステムの設計思想

Javaは、プラットフォーム独立性を重視する設計思想の下で、I/Oシステムを構築しました。その特徴は以下の通りです：

**統一されたストリーム抽象化**：Javaでは、すべてのI/O操作がストリームという概念で抽象化されています。ファイル、ネットワーク、メモリ、標準入出力など、データソースの種類に関わらず、同じインターフェイスでアクセスできます。

**プラットフォーム独立性**：Javaのファイルパス表現、改行文字の処理、文字エンコーディングなどは、プラットフォームに依存しない形で統一されています。これにより、同じJavaプログラムがWindows、Linux、macOSで同様に動作します。

**階層的な設計**：JavaのI/Oシステムは、低レベルなバイトストリームから高レベルなオブジェクトストリームまで、階層的に設計されています。必要に応じて適切な抽象化レベルを選択できます。

**デコレータパターンの活用**：BufferedInputStream、DataInputStream、ObjectInputStreamなど、基本的なストリームに機能を追加するデコレータパターンが広く使用されています。これにより、柔軟で拡張可能なI/Oシステムが実現されています。

### リソース管理の重要性と課題

ファイル入出力において、最も重要で困難な課題の一つが「リソース管理」です。ファイルハンドル、ネットワーク接続、データベース接続などの外部リソースは、以下の特性を持ちます：

**有限性**：オペレーティングシステムやハードウェアの制約により、同時にオープンできるリソースの数には限界があります。適切に管理されないと、リソース枯渇によりシステム全体がダウンする可能性があります。

**副作用**：リソースの獲得と解放は、プログラムの外部に影響を与える副作用を伴います。ファイルロック、排他制御、トランザクションなど、慎重な管理が必要です。

**例外安全性**：I/O操作は、様々な理由で失敗する可能性があります。ネットワーク障害、ディスク容量不足、権限エラーなど、予期しない例外が発生してもリソースが適切に解放される必要があります。

**パフォーマンス**：不適切なリソース管理は、メモリリーク、ファイルディスクリプタリーク、接続プールの枯渇など、深刻なパフォーマンス問題を引き起こします。

### 従来のリソース管理手法とその問題点

Java 7以前のリソース管理では、try-finally構文が標準的な手法でした：

```java
FileInputStream input = null;
try {
    input = new FileInputStream("data.txt");
    // ファイル処理
} catch (IOException e) {
    // エラー処理
} finally {
    if (input != null) {
        try {
            input.close();
        } catch (IOException e) {
            // クローズ時のエラー処理
        }
    }
}
```

この手法には以下のような問題がありました：

**コードの冗長性**：リソース管理のためのボイラープレートコードが大量に必要で、本来の処理ロジックが埋もれてしまいました。

**エラーの隠蔽**：finally節でのclose()呼び出し時に例外が発生すると、元の例外が隠蔽される可能性がありました。

**実装ミス**：null チェックの忘れ、close()呼び出しの忘れ、ネストしたtry-catch構文でのエラーなど、実装ミスが発生しやすい状況でした。

**複数リソースの管理**：複数のリソースを同時に管理する場合、コードの複雑性が指数的に増加しました。

### Java 7 try-with-resourcesの革新

これらの問題を解決するため、Java 7では「try-with-resources」構文が導入されました。この機能により、リソース管理が大幅に簡素化されました：

```java
try (FileInputStream input = new FileInputStream("data.txt")) {
    // ファイル処理
} catch (IOException e) {
    // エラー処理
}
// inputは自動的にクローズされる
```

この革新により、以下の利点が実現されました：

**自動リソース管理**：Closeable/AutoCloseableインターフェイスを実装したリソースは、try文を抜ける際に自動的にクローズされます。

**例外の抑制**：close()時に例外が発生しても、元の例外が主例外として保持され、close()時の例外は抑制例外として記録されます。

**コードの簡素化**：ボイラープレートコードが大幅に削減され、本来の処理ロジックに集中できます。

**複数リソース対応**：セミコロンで区切ることで、複数のリソースを同時に管理できます。

### NIO.2（New I/O 2）の登場

Java 7では、try-with-resourcesと同時に、NIO.2（java.nio.file パッケージ）も導入されました。これは、従来のjava.io パッケージの制約を克服する、現代的なファイル操作APIです：

**Path指向の設計**：ファイルシステムの概念を適切に抽象化し、プラットフォーム固有の詳細を隠蔽しながら、効率的なファイル操作を実現しています。

**ファイル属性の詳細制御**：作成日時、アクセス権限、シンボリックリンクなど、現代的なファイルシステムの機能を包括的にサポートしています。

**ディレクトリの効率的な走査**：Files.walkFileTree()、DirectoryStreamなどにより、大規模なディレクトリ構造を効率的に処理できます。

**非同期I/O対応**：AsynchronousFileChannelにより、高性能な非同期ファイル操作が可能になりました。

### 現代のI/O処理における課題と動向

現代のソフトウェア開発では、従来のファイルI/Oを超えた、新しい課題と要求が生まれています：

**ビッグデータ処理**：テラバイト級のデータを効率的に処理するため、メモリマップドファイル、ストリーミング処理、分散ファイルシステムとの連携が重要になっています。

**クラウドストレージ統合**：Amazon S3、Google Cloud Storage、Azure Blobなどのクラウドストレージサービスとの統合により、スケーラブルなデータ保存が求められています。

**マイクロサービスアーキテクチャ**：サービス間でのデータ交換において、JSON、XML、Protocol Buffersなどの構造化データ形式の効率的な処理が必要になっています。

**セキュリティ強化**：データの暗号化、アクセス制御、監査ログなど、セキュリティを考慮したI/O処理が標準となっています。

### この章で学習する内容の意義

この章では、これらの歴史的背景と現代的な課題を踏まえて、Javaにおけるファイル入出力とリソース管理を体系的に学習していきます。単にAPIの使い方を覚えるのではなく、以下の点を重視して学習を進めます：

**適切なリソース管理**：try-with-resources構文を活用し、安全で効率的なリソース管理技術を習得します。

**I/O性能の最適化**：バッファリング、ストリーミング、非同期処理など、高性能なI/O処理技術を理解します。

**例外処理の統合**：I/O処理と例外処理を適切に組み合わせ、堅牢なアプリケーションを作成する能力を身につけます。

**現代的なAPI の活用**：NIO.2のPath APIやFiles クラスを活用し、保守性の高いファイル操作コードを作成します。

**実践的なパターン**：設定ファイルの読み込み、ログファイルの出力、データの永続化など、実際の開発で頻繁に使用されるパターンを習得します。

ファイル入出力とリソース管理を深く理解することは、実用的なJavaアプリケーション開発において不可欠なスキルを身につけることにつながります。この章を通じて、単なる「ファイルの読み書き技術」を超えて、「データ永続化とリソース管理の総合的な設計能力」を習得していきましょう。

この章では、Javaでのファイル操作とリソース管理について学習します。

## 11.1 ファイル読み書きの基礎

### 基本的なファイル読み取り

```java
import java.io.*;
import java.nio.file.*;
import java.util.List;

public class FileReadExample {
    public static void main(String[] args) {
        // Files.readAllLines を使用（推奨）
        try {
            List<String> lines = Files.readAllLines(Paths.get("sample.txt"));
            for (String line : lines) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.out.println("ファイル読み取りエラー: " + e.getMessage());
        }
        
        // BufferedReader を使用
        try (BufferedReader reader = Files.newBufferedReader(Paths.get("sample.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.out.println("ファイル読み取りエラー: " + e.getMessage());
        }
    }
}
```

### ファイル書き込み

```java
import java.io.*;
import java.nio.file.*;
import java.util.Arrays;

public class FileWriteExample {
    public static void main(String[] args) {
        // Files.write を使用（推奨）
        try {
            List<String> lines = Arrays.asList(
                "1行目のテキスト",
                "2行目のテキスト",
                "3行目のテキスト"
            );
            Files.write(Paths.get("output.txt"), lines);
            System.out.println("ファイルに書き込み完了");
        } catch (IOException e) {
            System.out.println("ファイル書き込みエラー: " + e.getMessage());
        }
        
        // BufferedWriter を使用
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get("output2.txt"))) {
            writer.write("Hello, World!");
            writer.newLine();
            writer.write("Java File I/O");
        } catch (IOException e) {
            System.out.println("ファイル書き込みエラー: " + e.getMessage());
        }
    }
}
```

## 11.2 ファイル操作

### ファイル・ディレクトリ操作

```java
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class FileOperations {
    public static void main(String[] args) throws IOException {
        Path filePath = Paths.get("example.txt");
        Path dirPath = Paths.get("testdir");
        
        // ファイル存在確認
        if (Files.exists(filePath)) {
            System.out.println("ファイルが存在します");
        }
        
        // ディレクトリ作成
        if (!Files.exists(dirPath)) {
            Files.createDirectory(dirPath);
            System.out.println("ディレクトリを作成しました");
        }
        
        // ファイル情報取得
        if (Files.exists(filePath)) {
            BasicFileAttributes attrs = Files.readAttributes(filePath, BasicFileAttributes.class);
            System.out.println("ファイルサイズ: " + attrs.size() + " bytes");
            System.out.println("作成日時: " + attrs.creationTime());
            System.out.println("最終更新日時: " + attrs.lastModifiedTime());
        }
        
        // ファイル一覧
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get("."))) {
            for (Path entry : stream) {
                System.out.println(entry.getFileName());
            }
        }
    }
}
```

## 11.3 CSVファイル処理

### CSVファイルの読み取りと処理

```java
import java.io.*;
import java.nio.file.*;
import java.util.*;

class Student {
    private String name;
    private int age;
    private String major;
    
    public Student(String name, int age, String major) {
        this.name = name;
        this.age = age;
        this.major = major;
    }
    
    // getters
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getMajor() { return major; }
    
    @Override
    public String toString() {
        return name + "(" + age + "歳, " + major + ")";
    }
}

public class CsvExample {
    public static void main(String[] args) {
        // サンプルCSVファイルの作成
        createSampleCsv();
        
        // CSVファイルの読み取り
        List<Student> students = readStudentsFromCsv("students.csv");
        students.forEach(System.out::println);
        
        // データの集計
        Map<String, Long> majorCount = students.stream()
            .collect(Collectors.groupingBy(Student::getMajor, Collectors.counting()));
        
        System.out.println("\n専攻別学生数:");
        majorCount.forEach((major, count) -> 
            System.out.println(major + ": " + count + "人"));
    }
    
    private static void createSampleCsv() {
        List<String> lines = Arrays.asList(
            "name,age,major",
            "田中太郎,20,コンピュータ科学",
            "佐藤花子,21,数学",
            "鈴木次郎,19,物理学",
            "高橋三郎,22,コンピュータ科学",
            "伊藤四郎,20,数学"
        );
        
        try {
            Files.write(Paths.get("students.csv"), lines);
        } catch (IOException e) {
            System.out.println("CSVファイル作成エラー: " + e.getMessage());
        }
    }
    
    private static List<Student> readStudentsFromCsv(String filename) {
        List<Student> students = new ArrayList<>();
        
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filename))) {
            reader.readLine(); // ヘッダー行をスキップ
            
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String name = parts[0];
                    int age = Integer.parseInt(parts[1]);
                    String major = parts[2];
                    students.add(new Student(name, age, major));
                }
            }
        } catch (IOException e) {
            System.out.println("CSVファイル読み取りエラー: " + e.getMessage());
        }
        
        return students;
    }
}
```

## 11.4 バイナリファイル処理

### 画像ファイルのコピー

```java
import java.io.*;
import java.nio.file.*;

public class BinaryFileExample {
    public static void copyFile(String source, String destination) {
        try {
            Files.copy(Paths.get(source), Paths.get(destination), 
                      StandardCopyOption.REPLACE_EXISTING);
            System.out.println("ファイルコピー完了: " + source + " -> " + destination);
        } catch (IOException e) {
            System.out.println("ファイルコピーエラー: " + e.getMessage());
        }
    }
    
    public static void copyFileWithStreams(String source, String destination) {
        try (InputStream in = new FileInputStream(source);
             OutputStream out = new FileOutputStream(destination)) {
            
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            System.out.println("ストリームでコピー完了");
            
        } catch (IOException e) {
            System.out.println("ストリームコピーエラー: " + e.getMessage());
        }
    }
}
```

## 11.5 設定ファイル処理

### Propertiesファイル

```java
import java.io.*;
import java.util.Properties;

public class PropertiesExample {
    public static void main(String[] args) {
        // Propertiesファイルの作成
        createPropertiesFile();
        
        // Propertiesファイルの読み取り
        Properties props = loadProperties();
        
        String dbUrl = props.getProperty("database.url");
        String dbUser = props.getProperty("database.user");
        String dbPassword = props.getProperty("database.password", "defaultPassword");
        
        System.out.println("DB URL: " + dbUrl);
        System.out.println("DB User: " + dbUser);
        System.out.println("DB Password: " + dbPassword);
    }
    
    private static void createPropertiesFile() {
        Properties props = new Properties();
        props.setProperty("database.url", "jdbc:mysql://localhost:3306/mydb");
        props.setProperty("database.user", "admin");
        props.setProperty("database.password", "secret123");
        props.setProperty("app.name", "MyApplication");
        props.setProperty("app.version", "1.0.0");
        
        try (OutputStream out = new FileOutputStream("config.properties")) {
            props.store(out, "Application Configuration");
            System.out.println("設定ファイルを作成しました");
        } catch (IOException e) {
            System.out.println("設定ファイル作成エラー: " + e.getMessage());
        }
    }
    
    private static Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream in = new FileInputStream("config.properties")) {
            props.load(in);
        } catch (IOException e) {
            System.out.println("設定ファイル読み取りエラー: " + e.getMessage());
        }
        return props;
    }
}
```

## 11.6 ストリームと高度なファイル処理

### ストリームの種類と使い分け

ストリームは、データの流れを抽象化したもので、JavaのI/Oシステムの基盤です。

```java
import java.io.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;

public class StreamTypesExample {
    public static void main(String[] args) {
        String filePath = "stream_example.txt";
        
        // バイトストリームでのファイル操作
        demonstrateByteStreams(filePath);
        
        // キャラクタストリームでのファイル操作
        demonstrateCharacterStreams(filePath);
        
        // バッファー付きストリームのパフォーマンス比較
        compareBufferedVsUnbuffered();
    }
    
    private static void demonstrateByteStreams(String filePath) {
        System.out.println("=== バイトストリームの例 ===");
        
        // バイナリデータの書き込み
        try (FileOutputStream fos = new FileOutputStream(filePath);
             BufferedOutputStream bos = new BufferedOutputStream(fos)) {
            
            byte[] data = "バイトストリームテスト\nデータ\n".getBytes(StandardCharsets.UTF_8);
            bos.write(data);
            bos.flush(); // バッファーを強制的にフラッシュ
            System.out.println("バイトストリームで書き込み完了");
            
        } catch (IOException e) {
            System.err.println("バイトストリーム書き込みエラー: " + e.getMessage());
        }
        
        // バイナリデータの読み込み
        try (FileInputStream fis = new FileInputStream(filePath);
             BufferedInputStream bis = new BufferedInputStream(fis)) {
            
            byte[] buffer = new byte[1024];
            int bytesRead = bis.read(buffer);
            String content = new String(buffer, 0, bytesRead, StandardCharsets.UTF_8);
            System.out.println("読み込んだ内容: " + content);
            
        } catch (IOException e) {
            System.err.println("バイトストリーム読み込みエラー: " + e.getMessage());
        }
    }
    
    private static void demonstrateCharacterStreams(String filePath) {
        System.out.println("\n=== キャラクタストリームの例 ===");
        
        // 文字データの書き込み（UTF-8文字エンコーディング指定）
        try (FileWriter fw = new FileWriter(filePath, StandardCharsets.UTF_8);
             BufferedWriter bw = new BufferedWriter(fw)) {
            
            bw.write("日本語のテキスト\n");
            bw.write("キャラクタストリームで処理\n");
            bw.write("文字エンコーディングを考慮\n");
            System.out.println("キャラクタストリームで書き込み完了");
            
        } catch (IOException e) {
            System.err.println("キャラクタストリーム書き込みエラー: " + e.getMessage());
        }
        
        // 文字データの読み込み
        try (FileReader fr = new FileReader(filePath, StandardCharsets.UTF_8);
             BufferedReader br = new BufferedReader(fr)) {
            
            String line;
            System.out.println("読み込んだ内容:");
            while ((line = br.readLine()) != null) {
                System.out.println("  " + line);
            }
            
        } catch (IOException e) {
            System.err.println("キャラクタストリーム読み込みエラー: " + e.getMessage());
        }
    }
    
    private static void compareBufferedVsUnbuffered() {
        System.out.println("\n=== バッファーのパフォーマンス比較 ===");
        
        String testFile = "performance_test.txt";
        int iterations = 10000;
        
        // バッファーなしでの書き込み
        long startTime = System.currentTimeMillis();
        try (FileWriter fw = new FileWriter(testFile)) {
            for (int i = 0; i < iterations; i++) {
                fw.write("テストライン " + i + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        long unbufferedTime = System.currentTimeMillis() - startTime;
        
        // バッファーありでの書き込み
        startTime = System.currentTimeMillis();
        try (FileWriter fw = new FileWriter(testFile);
             BufferedWriter bw = new BufferedWriter(fw)) {
            for (int i = 0; i < iterations; i++) {
                bw.write("テストライン " + i + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        long bufferedTime = System.currentTimeMillis() - startTime;
        
        System.out.println("バッファーなし: " + unbufferedTime + "ms");
        System.out.println("バッファーあり: " + bufferedTime + "ms");
        System.out.println("性能改善倍率: " + (double) unbufferedTime / bufferedTime + "倍");
        
        // テストファイルを削除
        try {
            Files.deleteIfExists(Paths.get(testFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

### オブジェクトの直列化（シリアライズ）

```java
import java.io.*;
import java.util.Date;

// シリアライズ可能なクラス
class UserProfile implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String username;
    private int age;
    private transient String password; // シリアライズ対象外
    private Date registrationDate;
    
    public UserProfile(String username, int age, String password) {
        this.username = username;
        this.age = age;
        this.password = password;
        this.registrationDate = new Date();
    }
    
    // カスタムシリアライゼーションメソッド
    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject(); // デフォルトのシリアライゼーション
        // passwordを暗号化して保存（簡単な例）
        if (password != null) {
            oos.writeObject("ENCRYPTED:" + password);
        } else {
            oos.writeObject(null);
        }
    }
    
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject(); // デフォルトのデシリアライゼーション
        // 暗号化されたpasswordを復号化
        String encryptedPassword = (String) ois.readObject();
        if (encryptedPassword != null && encryptedPassword.startsWith("ENCRYPTED:")) {
            this.password = encryptedPassword.substring(10); // "ENCRYPTED:"を削除
        }
    }
    
    @Override
    public String toString() {
        return "UserProfile{username='" + username + "', age=" + age + 
               ", password='" + (password != null ? "[SET]" : "[NOT SET]") + 
               "', registrationDate=" + registrationDate + "}";
    }
}

public class SerializationExample {
    public static void main(String[] args) {
        String fileName = "user_profile.ser";
        
        // オブジェクトの作成とシリアライゼーション
        UserProfile user = new UserProfile("山田太郎", 30, "secret123");
        System.out.println("シリアライズ前: " + user);
        
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(user);
            System.out.println("オブジェクトをシリアライズしました");
        } catch (IOException e) {
            System.err.println("シリアライゼーションエラー: " + e.getMessage());
        }
        
        // オブジェクトのデシリアライゼーション
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            UserProfile loadedUser = (UserProfile) ois.readObject();
            System.out.println("デシリアライズ後: " + loadedUser);
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("デシリアライゼーションエラー: " + e.getMessage());
        }
    }
}
```

## 11.7 ランダムアクセスファイル

ファイルの任意の位置に直接アクセスできるランダムアクセス機能を紹介します。

```java
import java.io.*;

public class RandomAccessFileExample {
    public static void main(String[] args) {
        String fileName = "random_access_demo.dat";
        
        // 固定長レコードの作成と操作
        createAndManipulateRecords(fileName);
        
        // ファイルの部分更新
        updateSpecificRecord(fileName);
        
        // 全レコードの読み取り
        readAllRecords(fileName);
    }
    
    private static void createAndManipulateRecords(String fileName) {
        System.out.println("=== 固定長レコードの作成 ===");
        
        try (RandomAccessFile raf = new RandomAccessFile(fileName, "rw")) {
            // 5件のレコードを作成（ID: 4バイト, 名前: 20バイト, 年齢: 4バイト）
            String[] names = {"田中太郎", "佐藤花子", "鈴木一郎", "高橋美穂", "伊藤秀一"};
            int[] ages = {25, 30, 35, 28, 42};
            
            for (int i = 0; i < names.length; i++) {
                raf.writeInt(i + 1); // ID
                
                // 名前を固定長（20バイト）で書き込み
                byte[] nameBytes = names[i].getBytes("UTF-8");
                byte[] paddedName = new byte[20];
                System.arraycopy(nameBytes, 0, paddedName, 0, 
                               Math.min(nameBytes.length, 20));
                raf.write(paddedName);
                
                raf.writeInt(ages[i]); // 年齢
            }
            
            System.out.println(names.length + "件のレコードを作成しました");
            
        } catch (IOException e) {
            System.err.println("レコード作成エラー: " + e.getMessage());
        }
    }
    
    private static void updateSpecificRecord(String fileName) {
        System.out.println("\n=== 特定レコードの更新 ===");
        
        try (RandomAccessFile raf = new RandomAccessFile(fileName, "rw")) {
            // 3番目のレコード（インデックス2）の年齢を更新
            int recordIndex = 2;
            int recordSize = 4 + 20 + 4; // ID + 名前 + 年齢
            long position = recordIndex * recordSize + 4 + 20; // 年齢の位置
            
            raf.seek(position);
            int oldAge = raf.readInt();
            
            raf.seek(position);
            raf.writeInt(40); // 新しい年齢
            
            System.out.println("3番目のレコードの年齢を " + oldAge + " から 40 に更新しました");
            
        } catch (IOException e) {
            System.err.println("レコード更新エラー: " + e.getMessage());
        }
    }
    
    private static void readAllRecords(String fileName) {
        System.out.println("\n=== 全レコードの読み取り ===");
        
        try (RandomAccessFile raf = new RandomAccessFile(fileName, "r")) {
            long fileLength = raf.length();
            int recordSize = 4 + 20 + 4;
            int numRecords = (int) (fileLength / recordSize);
            
            System.out.println("ファイルサイズ: " + fileLength + "バイト, レコード数: " + numRecords);
            
            for (int i = 0; i < numRecords; i++) {
                raf.seek(i * recordSize);
                
                int id = raf.readInt();
                
                byte[] nameBytes = new byte[20];
                raf.read(nameBytes);
                String name = new String(nameBytes, "UTF-8").trim();
                
                int age = raf.readInt();
                
                System.out.println("ID: " + id + ", 名前: " + name + ", 年齢: " + age);
            }
            
        } catch (IOException e) {
            System.err.println("レコード読み取りエラー: " + e.getMessage());
        }
    }
}
```

## 11.8 ファイルシステム操作とメタデータ

### ファイル属性とメタデータの取得

```java
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Set;

public class FileAttributesExample {
    public static void main(String[] args) {
        String fileName = "metadata_test.txt";
        Path filePath = Paths.get(fileName);
        
        // テストファイルの作成
        createTestFile(filePath);
        
        // 基本ファイル属性の取得
        getBasicFileAttributes(filePath);
        
        // ファイルシステム情報の取得
        getFileSystemInfo(filePath);
        
        // ファイルの権限確認
        checkFilePermissions(filePath);
    }
    
    private static void createTestFile(Path filePath) {
        try {
            Files.write(filePath, "メタデータテスト用ファイル\n作成日時のテスト".getBytes());
            System.out.println("テストファイルを作成しました: " + filePath);
        } catch (IOException e) {
            System.err.println("ファイル作成エラー: " + e.getMessage());
        }
    }
    
    private static void getBasicFileAttributes(Path filePath) {
        System.out.println("\n=== 基本ファイル属性 ===");
        
        try {
            BasicFileAttributes attrs = Files.readAttributes(filePath, BasicFileAttributes.class);
            
            System.out.println("ファイルサイズ: " + attrs.size() + " バイト");
            
            LocalDateTime creationTime = LocalDateTime.ofInstant(
                attrs.creationTime().toInstant(), ZoneId.systemDefault());
            LocalDateTime lastModified = LocalDateTime.ofInstant(
                attrs.lastModifiedTime().toInstant(), ZoneId.systemDefault());
            LocalDateTime lastAccess = LocalDateTime.ofInstant(
                attrs.lastAccessTime().toInstant(), ZoneId.systemDefault());
            
            System.out.println("作成日時: " + creationTime);
            System.out.println("最終更新日時: " + lastModified);
            System.out.println("最終アクセス日時: " + lastAccess);
            
            System.out.println("ファイルか: " + attrs.isRegularFile());
            System.out.println("ディレクトリか: " + attrs.isDirectory());
            System.out.println("シンボリックリンクか: " + attrs.isSymbolicLink());
            
        } catch (IOException e) {
            System.err.println("ファイル属性取得エラー: " + e.getMessage());
        }
    }
    
    private static void getFileSystemInfo(Path filePath) {
        System.out.println("\n=== ファイルシステム情報 ===");
        
        try {
            FileSystem fs = filePath.getFileSystem();
            FileStore store = Files.getFileStore(filePath);
            
            System.out.println("ファイルシステム: " + fs.getClass().getSimpleName());
            System.out.println("ファイルストア: " + store.name());
            System.out.println("ファイルストアタイプ: " + store.type());
            
            long totalSpace = store.getTotalSpace();
            long usableSpace = store.getUsableSpace();
            long unallocatedSpace = store.getUnallocatedSpace();
            
            System.out.println("全容量: " + formatBytes(totalSpace));
            System.out.println("使用可能容量: " + formatBytes(usableSpace));
            System.out.println("未割り当て容量: " + formatBytes(unallocatedSpace));
            
            Set<String> supportedViews = fs.supportedFileAttributeViews();
            System.out.println("サポートされる属性ビュー: " + supportedViews);
            
        } catch (IOException e) {
            System.err.println("ファイルシステム情報取得エラー: " + e.getMessage());
        }
    }
    
    private static void checkFilePermissions(Path filePath) {
        System.out.println("\n=== ファイル権限 ===");
        
        System.out.println("読み取り可能: " + Files.isReadable(filePath));
        System.out.println("書き込み可能: " + Files.isWritable(filePath));
        System.out.println("実行可能: " + Files.isExecutable(filePath));
        System.out.println("隠しファイル: " + isHidden(filePath));
        
        // POSIXシステムでの権限情報（Linux/macOS）
        try {
            if (filePath.getFileSystem().supportedFileAttributeViews().contains("posix")) {
                PosixFileAttributes posixAttrs = Files.readAttributes(filePath, PosixFileAttributes.class);
                System.out.println("所有者: " + posixAttrs.owner().getName());
                System.out.println("グループ: " + posixAttrs.group().getName());
                System.out.println("POSIX権限: " + PosixFilePermissions.toString(posixAttrs.permissions()));
            }
        } catch (IOException e) {
            System.err.println("POSIX権限取得エラー: " + e.getMessage());
        }
    }
    
    private static boolean isHidden(Path filePath) {
        try {
            return Files.isHidden(filePath);
        } catch (IOException e) {
            return false;
        }
    }
    
    private static String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024));
        return String.format("%.1f GB", bytes / (1024.0 * 1024 * 1024));
    }
}
```

### ディレクトリの再帰的処理

```java
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicLong;

public class DirectoryTraversalExample {
    public static void main(String[] args) {
        String targetDir = ".";
        Path startPath = Paths.get(targetDir);
        
        // ファイルツリーの表示
        displayFileTree(startPath);
        
        // ディレクトリサイズの計算
        calculateDirectorySize(startPath);
        
        // 特定のファイルを検索
        searchFiles(startPath, "*.java");
    }
    
    private static void displayFileTree(Path startPath) {
        System.out.println("=== ファイルツリー ===");
        
        try {
            Files.walkFileTree(startPath, new SimpleFileVisitor<Path>() {
                private int depth = 0;
                
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    System.out.println(getIndent(depth) + "[DIR] " + dir.getFileName());
                    depth++;
                    return FileVisitResult.CONTINUE;
                }
                
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    System.out.println(getIndent(depth) + "[FILE] " + file.getFileName() + 
                                     " (" + attrs.size() + " bytes)");
                    return FileVisitResult.CONTINUE;
                }
                
                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
                    depth--;
                    return FileVisitResult.CONTINUE;
                }
                
                private String getIndent(int depth) {
                    return "  ".repeat(depth);
                }
            });
        } catch (IOException e) {
            System.err.println("ファイルツリー表示エラー: " + e.getMessage());
        }
    }
    
    private static void calculateDirectorySize(Path startPath) {
        System.out.println("\n=== ディレクトリサイズ計算 ===");
        
        AtomicLong totalSize = new AtomicLong(0);
        AtomicLong fileCount = new AtomicLong(0);
        AtomicLong dirCount = new AtomicLong(0);
        
        try {
            Files.walkFileTree(startPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    totalSize.addAndGet(attrs.size());
                    fileCount.incrementAndGet();
                    return FileVisitResult.CONTINUE;
                }
                
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    dirCount.incrementAndGet();
                    return FileVisitResult.CONTINUE;
                }
            });
            
            System.out.println("総ファイル数: " + fileCount.get());
            System.out.println("総ディレクトリ数: " + dirCount.get());
            System.out.println("総サイズ: " + formatBytes(totalSize.get()));
            
        } catch (IOException e) {
            System.err.println("ディレクトリサイズ計算エラー: " + e.getMessage());
        }
    }
    
    private static void searchFiles(Path startPath, String pattern) {
        System.out.println("\n=== ファイル検索 (" + pattern + ") ===");
        
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + pattern);
        
        try {
            Files.walkFileTree(startPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    if (matcher.matches(file.getFileName())) {
                        System.out.println("登録: " + file + " (" + formatBytes(attrs.size()) + ")");
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            System.err.println("ファイル検索エラー: " + e.getMessage());
        }
    }
    
    private static String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024));
        return String.format("%.1f GB", bytes / (1024.0 * 1024 * 1024));
    }
}
```

## 11.9 ファイル監視とイベント処理

```java
import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class FileWatcherExample {
    private final WatchService watchService;
    private final ExecutorService executor;
    private boolean running = true;
    
    public FileWatcherExample() throws IOException {
        this.watchService = FileSystems.getDefault().newWatchService();
        this.executor = Executors.newSingleThreadExecutor();
    }
    
    public void watchDirectory(Path directory) {
        try {
            directory.register(watchService,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_DELETE,
                StandardWatchEventKinds.ENTRY_MODIFY);
            
            System.out.println("ディレクトリを監視開始: " + directory);
            
            executor.submit(() -> {
                while (running) {
                    try {
                        WatchKey key = watchService.take();
                        
                        for (WatchEvent<?> event : key.pollEvents()) {
                            WatchEvent.Kind<?> kind = event.kind();
                            Path fileName = (Path) event.context();
                            
                            handleFileEvent(kind, fileName, directory);
                        }
                        
                        if (!key.reset()) {
                            System.out.println("ディレクトリの監視が無効になりました");
                            break;
                        }
                        
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            });
            
        } catch (IOException e) {
            System.err.println("ディレクトリ監視エラー: " + e.getMessage());
        }
    }
    
    private void handleFileEvent(WatchEvent.Kind<?> kind, Path fileName, Path directory) {
        String eventType = "";
        if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
            eventType = "作成";
        } else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
            eventType = "削除";
        } else if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
            eventType = "変更";
        }
        
        Path fullPath = directory.resolve(fileName);
        System.out.println("["+eventType+"] " + fullPath);
        
        // ファイルタイプ別の処理
        if (fileName.toString().endsWith(".txt")) {
            handleTextFileEvent(eventType, fullPath);
        } else if (fileName.toString().endsWith(".log")) {
            handleLogFileEvent(eventType, fullPath);
        }
    }
    
    private void handleTextFileEvent(String eventType, Path filePath) {
        if ("作成".equals(eventType)) {
            System.out.println("  → テキストファイルが作成されました: " + filePath.getFileName());
        } else if ("変更".equals(eventType)) {
            System.out.println("  → テキストファイルが更新されました: " + filePath.getFileName());
            // 必要に応じてファイルの内容を読み取り、処理を実行
        }
    }
    
    private void handleLogFileEvent(String eventType, Path filePath) {
        if ("変更".equals(eventType)) {
            System.out.println("  → ログファイルが更新されました: " + filePath.getFileName());
            // ログファイルの新しい行を読み取り、解析処理を実行
        }
    }
    
    public void stopWatching() {
        running = false;
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
            watchService.close();
        } catch (InterruptedException | IOException e) {
            executor.shutdownNow();
        }
        System.out.println("ファイル監視を停止しました");
    }
    
    public static void main(String[] args) {
        try {
            FileWatcherExample watcher = new FileWatcherExample();
            Path watchDir = Paths.get(".");
            
            watcher.watchDirectory(watchDir);
            
            System.out.println("ファイル監視を開始しました。Enterキーで終了します。");
            System.in.read(); // ユーザー入力を待つ
            
            watcher.stopWatching();
            
        } catch (IOException e) {
            System.err.println("ファイル監視エラー: " + e.getMessage());
        }
    }
}
```

## まとめ

この章では、Javaでのファイル入出力とリソース管理について、基本から高度なテクニックまで幅広く学習しました。

**主要な学習内容:**
- **ストリームベースI/O**: バイトストリームとキャラクタストリームの違いと適切な使い分け
- **NIO.2 API**: `java.nio.file`パッケージを使ったモダンなファイル操作
- **リソース管理**: try-with-resources構文による安全なリソース管理
- **オブジェクトシリアライゼーション**: Javaオブジェクトの永続化と復元
- **ランダムアクセス**: 固定長レコードの効率的な操作
- **ファイルシステム操作**: メタデータ取得、ディレクトリ処理、ファイル監視

適切なリソース管理、例外処理、そして文字エンコーディングへの配慮により、堅牢で効率的なファイル処理アプリケーションを作成できるようになりました。