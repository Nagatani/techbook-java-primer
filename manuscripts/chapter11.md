# 第11章 ファイル入出力とリソース管理

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