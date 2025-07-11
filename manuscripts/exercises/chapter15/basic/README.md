# 第15章 基礎課題：ファイル入出力

## 概要
本章で学んだファイル入出力の基本的な使い方を練習します。Files API、NIO.2、文字エンコーディング、基本的なファイル操作を身につけましょう。

## 課題一覧

### 課題1: 基本的なファイル読み書き
`BasicFileIO.java`を作成し、以下を実装してください：

1. **テキストファイルの読み込み**
   ```java
   public static String readTextFile(String filename) throws IOException {
       // Files.readString()を使用
       return Files.readString(Path.of(filename));
   }
   
   public static List<String> readLines(String filename) throws IOException {
       // Files.readAllLines()を使用
       return Files.readAllLines(Path.of(filename));
   }
   
   public static void readLargeFile(String filename) throws IOException {
       // Files.lines()を使用してStream処理
       try (Stream<String> lines = Files.lines(Path.of(filename))) {
           lines.forEach(System.out::println);
       }
   }
   ```

2. **テキストファイルの書き込み**
   ```java
   public static void writeTextFile(String filename, String content) 
           throws IOException {
       // Files.writeString()を使用
       Files.writeString(Path.of(filename), content);
   }
   
   public static void writeLines(String filename, List<String> lines) 
           throws IOException {
       // Files.write()を使用
       Files.write(Path.of(filename), lines);
   }
   
   public static void appendToFile(String filename, String content) 
           throws IOException {
       // StandardOpenOption.APPENDを使用
       Files.writeString(Path.of(filename), content, 
           StandardOpenOption.APPEND, StandardOpenOption.CREATE);
   }
   ```

### 課題2: ディレクトリ操作
`DirectoryOperations.java`を作成し、以下を実装してください：

1. **ディレクトリの作成と削除**
   ```java
   public static void createDirectory(String path) throws IOException {
       Files.createDirectory(Path.of(path));
   }
   
   public static void createDirectories(String path) throws IOException {
       // 親ディレクトリも含めて作成
       Files.createDirectories(Path.of(path));
   }
   
   public static void deleteIfExists(String path) throws IOException {
       Files.deleteIfExists(Path.of(path));
   }
   ```

2. **ファイル・ディレクトリの一覧取得**
   ```java
   public static List<String> listFiles(String directory) throws IOException {
       List<String> files = new ArrayList<>();
       try (DirectoryStream<Path> stream = 
               Files.newDirectoryStream(Path.of(directory))) {
           for (Path path : stream) {
               files.add(path.getFileName().toString());
           }
       }
       return files;
   }
   
   public static List<Path> findFiles(String directory, String pattern) 
           throws IOException {
       // 特定のパターンに一致するファイルを検索
       List<Path> result = new ArrayList<>();
       try (DirectoryStream<Path> stream = 
               Files.newDirectoryStream(Path.of(directory), pattern)) {
           stream.forEach(result::add);
       }
       return result;
   }
   ```

### 課題3: ファイルの属性とメタデータ
`FileAttributes.java`を作成し、以下を実装してください：

1. **ファイル情報の取得**
   ```java
   public static void printFileInfo(String filename) throws IOException {
       Path path = Path.of(filename);
       
       System.out.println("ファイル名: " + path.getFileName());
       System.out.println("絶対パス: " + path.toAbsolutePath());
       System.out.println("サイズ: " + Files.size(path) + " bytes");
       System.out.println("最終更新日時: " + Files.getLastModifiedTime(path));
       System.out.println("読み取り可能: " + Files.isReadable(path));
       System.out.println("書き込み可能: " + Files.isWritable(path));
       System.out.println("実行可能: " + Files.isExecutable(path));
   }
   
   public static Map<String, Object> getFileAttributes(String filename) 
           throws IOException {
       Path path = Path.of(filename);
       BasicFileAttributes attrs = Files.readAttributes(path, 
           BasicFileAttributes.class);
       
       Map<String, Object> result = new HashMap<>();
       result.put("creationTime", attrs.creationTime());
       result.put("lastModifiedTime", attrs.lastModifiedTime());
       result.put("size", attrs.size());
       result.put("isDirectory", attrs.isDirectory());
       result.put("isRegularFile", attrs.isRegularFile());
       
       return result;
   }
   ```

### 課題4: ファイルのコピーと移動
`FileCopyMove.java`を作成し、以下を実装してください：

1. **ファイル操作**
   ```java
   public static void copyFile(String source, String target) 
           throws IOException {
       Files.copy(Path.of(source), Path.of(target), 
           StandardCopyOption.REPLACE_EXISTING);
   }
   
   public static void moveFile(String source, String target) 
           throws IOException {
       Files.move(Path.of(source), Path.of(target), 
           StandardCopyOption.REPLACE_EXISTING);
   }
   
   public static void copyDirectory(String source, String target) 
           throws IOException {
       Path sourcePath = Path.of(source);
       Path targetPath = Path.of(target);
       
       Files.walk(sourcePath)
           .forEach(path -> {
               try {
                   Path targetFile = targetPath.resolve(
                       sourcePath.relativize(path));
                   if (Files.isDirectory(path)) {
                       Files.createDirectories(targetFile);
                   } else {
                       Files.copy(path, targetFile, 
                           StandardCopyOption.REPLACE_EXISTING);
                   }
               } catch (IOException e) {
                   throw new RuntimeException(e);
               }
           });
   }
   ```

## 実装のヒント

### 文字エンコーディングの指定
```java
// UTF-8でファイルを読み込む
String content = Files.readString(Path.of("file.txt"), 
    StandardCharsets.UTF_8);

// Shift_JISでファイルを書き込む
Files.writeString(Path.of("file.txt"), content, 
    Charset.forName("Shift_JIS"));
```

### Files.walk()を使ったディレクトリの走査
```java
// すべてのJavaファイルを検索
List<Path> javaFiles = Files.walk(Path.of("src"))
    .filter(path -> path.toString().endsWith(".java"))
    .collect(Collectors.toList());
```

### 一時ファイルの作成
```java
Path tempFile = Files.createTempFile("prefix", ".tmp");
Path tempDir = Files.createTempDirectory("tempdir");

// 使用後は削除
Files.deleteIfExists(tempFile);
```

## 提出前チェックリスト
- [ ] すべてのメソッドが正しく実装されている
- [ ] 適切な例外処理が実装されている
- [ ] リソースが適切にクローズされている
- [ ] 文字エンコーディングを考慮している

## 評価基準
- Files APIを正しく使用できているか
- NIO.2の機能を理解し活用できているか
- 例外処理が適切に実装されているか
- コードの可読性と保守性