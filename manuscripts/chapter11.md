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

## まとめ

この章では、Javaでのファイル入出力とリソース管理について学習しました。適切なリソース管理と例外処理により、堅牢なファイル処理アプリケーションを作成できます。