# 第15章 ファイルI/O

## 🎯総合演習プロジェクトへのステップ

本章で学ぶファイル入出力（I/O）は、**総合演習プロジェクト「ToDoリストアプリケーション」** の中核機能である**データの保存と読み込み**を実現するための技術です。

-   **タスクデータの永続化**: アプリケーションを終了してもタスクリストが消えないように、`Task`オブジェクトのリストをファイルに保存します。`ObjectOutputStream`を使ったシリアライズや、CSV形式での保存などが考えられます。
-   **アプリケーション起動時のデータ復元**: アプリケーション起動時に、保存しておいたファイルからタスクリストを読み込み、前回の状態を復元します。
-   **ユーザーによるファイル選択**: `JFileChooser`を使って、ユーザーがタスクリストを保存する場所や、読み込むファイルを選択できるようにします。これにより、複数のタスクリストを切り替えて使うといった機能も実現可能になります。

本章を通じて、プログラムのデータを外部ファイルとして永続化し、アプリケーションをより実用的なものにするための必須スキルを学びます。

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
        try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
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
            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
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

        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
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

## 15.3 オブジェクトの直列化（シリアライズ）

Javaオブジェクトの状態をそのままバイト列に変換して保存するしくみを**直列化（シリアライズ）**、バイト列からオブジェクトを復元することを**非直列化（デシリアライズ）**と呼びます。オブジェクトの構造を保ったまま、簡単に保存・復元できる強力な機能です。

-   直列化したいクラスは`java.io.Serializable`インターフェイスを実装する必要があります。
-   `ObjectOutputStream`で直列化し、`ObjectInputStream`で非直列化します。
-   `transient`修飾子を付けたフィールドは直列化の対象外となります（パスワードなど）。

```java
import java.io.*;
import java.util.Date;

// Serializableを実装したクラス
class UserProfile implements Serializable {
    private static final long serialVersionUID = 1L; // クラスのバージョン管理用
    String name;
    transient String password; // 直列化されない
    Date registrationDate;
    public UserProfile(String name, String pw) { this.name = name; this.password = pw; this.registrationDate = new Date(); }
    public String toString() { return "User[name=" + name + ", pw=" + password + ", date=" + registrationDate + "]"; }
}

public class SerializationExample {
    public static void main(String[] args) {
        UserProfile user = new UserProfile("testuser", "secret123");
        String filename = "user.ser";

        // 1. 直列化してファイルに保存
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(user);
            System.out.println("保存されたオブジェクト: " + user);
        } catch (IOException e) { e.printStackTrace(); }

        // 2. ファイルから非直列化して復元
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            UserProfile loadedUser = (UserProfile) ois.readObject();
            System.out.println("復元されたオブジェクト: " + loadedUser); // passwordはnullになる
        } catch (IOException | ClassNotFoundException e) { e.printStackTrace(); }
    }
}
```

## 15.4 ファイルシステムの操作 (NIO.2)

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

## 15.5 GUIでのファイル選択: `JFileChooser`

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
            System.out.println("選択されたファイル（開く）: " + selectedFile.getAbsolutePath());
        }

        // ファイルを保存するダイアログ
        int saveResult = fileChooser.showSaveDialog(null);
        if (saveResult == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            System.out.println("保存するファイル: " + fileToSave.getAbsolutePath());
        }
    }
}
```

## まとめ

-   JavaのファイルI/Oは**ストリーム**ベースです。
-   リソース管理には**`try-with-resources`**を使い、安全性を高めましょう。
-   テキストファイルは**キャラクタストリーム**（`Reader`/`Writer`）、バイナリファイルは**バイトストリーム**（`InputStream`/`OutputStream`）で扱います。
-   **オブジェクト直列化**は、オブジェクトの状態を簡単に保存・復元する強力な手段です。
-   **NIO.2 (`java.nio.file`)** を使うと、モダンで高機能なファイルシステム操作が可能です。
-   GUIでは**`JFileChooser`**を使って、ユーザーにファイルを選択させることができます。

