# <b>23章</b> <span>ビルドとデプロイ</span> <small>アプリケーションの配布と実行</small>

## 本章の学習目標

### 前提知識

必須
- 第1章のJava開発環境構築（javac、javaコマンド）
- 第15章のファイル入出力（クラスパス、リソースファイル）
- 第22章のドキュメントと外部ライブラリ（Maven、Gradle）
- 基本的なコマンドライン操作

推奨
- 第21章のユニットテスト（テスト自動化）
- 継続的インテグレーション（CI/CD）の基本概念
- ソフトウェアデプロイメントの基礎知識
- OS間の違いに関する基本理解（Windows、macOS、Linux）

### 学習目標

知識の獲得
- JARファイルの仕組みとその役割を理解する
- マニフェストファイルの基本的な設定方法を知る
- jpackageツールの基本的な使い方を習得する

技術の習得
- 実行可能JARファイルの作成手順を身につける
- リソースファイルを含むJARファイルの作成方法を理解する
- 各OS向けのネイティブアプリケーションパッケージを作成できる

実践力の養成
- 開発したアプリケーションを他のユーザーに配布できる
- Java環境を持たないユーザー向けの配布方法を選択できる
- 基本的なビルドとパッケージングの流れを実践できる

## はじめに

本章で学ぶビルドと配布の技術は、開発したJavaアプリケーションを、Java開発環境を持たない他のユーザーにも使ってもらうための重要なステップです。

主に以下の2つの技術を学習します。

- 実行可能JARファイルの作成
- jpackageによるネイティブアプリケーション化

## なぜビルドと配布が必要か

これまでの章では、主にIDEから直接ソースコードを実行してきました。しかし、開発したアプリケーションを他の人に使ってもらうには、ソースコードそのものを渡すわけにはいきません。

開発環境を持たないユーザーでも簡単に実行できるように、必要なファイルをまとめて実行可能な形式に変換する必要があります。この一連の作業を「ビルド」と「パッケージング」と呼び、最終的にユーザーに配布できる形にすることを「配布」といいます。

本章では、Javaアプリケーションを配布するための最も基本的な形式である実行可能JARファイルの作成方法と、さらに一歩進んでOSネイティブのアプリケーションを作成する方法を学びます。

## 実行可能JARファイルの作成

JAR（Java Archive）は、複数のJavaクラスファイルや画像・設定ファイルなどのリソースを、ZIP形式で1つにまとめたファイルです。このJARファイルに「どのクラスの`main`メソッドからプログラムを開始するか」という情報を加えることで、`java -jar`コマンドで実行できる「実行可能JARファイル」を作成できます。

### ステップ1: サンプルアプリケーションの準備

まずは、配布する簡単なSwingアプリケーションを用意します。

<span class="listing-number">**サンプルコード23-2**</span>

```java
// SimpleApp.java
import javax.swing.*;

public class SimpleApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("配布アプリ");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(300, 150);
            frame.add(new JLabel("JARファイルからの実行に成功しました！"));
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
```

### 複数のクラスファイルを含むアプリケーションの例

実際のアプリケーションは通常、複数のクラスから構成されます。以下は、シンプルなTodoアプリケーションの例です。

<span class="listing-number">**サンプルコード23-4**</span>

```java
// TodoApp.java
import javax.swing.*;
import java.awt.*;

public class TodoApp {
    private DefaultListModel<String> listModel = new DefaultListModel<>();
    
    public TodoApp() {
        createAndShowGUI();
    }
    
    private void createAndShowGUI() {
        JFrame frame = new JFrame("Todo Manager");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        
        // リストコンポーネント
        JList<String> todoList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(todoList);
        frame.add(scrollPane, BorderLayout.CENTER);
        
        // 入力パネル
        JPanel inputPanel = new JPanel(new BorderLayout());
        JTextField inputField = new JTextField();
        JButton addButton = new JButton("追加");
        
        addButton.addActionListener(e -> {
            String text = inputField.getText().trim();
            if (!text.isEmpty()) {
                listModel.addElement(text);
                inputField.setText("");
            }
        });
        
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(addButton, BorderLayout.EAST);
        frame.add(inputPanel, BorderLayout.SOUTH);
        
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(TodoApp::new);
    }
}
```

複数のクラスファイルを含む場合のJAR作成

<span class="listing-number">**サンプルコード23-5**</span>

```bash
# すべてのJavaファイルをコンパイル
javac *.java

# 複数のクラスファイルを含むJARを作成
jar cvfm TodoApp.jar manifest.txt *.class
```

### ステップ2: コンパイル

まず、ソースコードをコンパイルしてクラスファイル (`.class`) を作成します。

<span class="listing-number">**サンプルコード23-6**</span>

```bash
javac SimpleApp.java
```
これにより、`SimpleApp.class`ファイルが生成されます。

### ステップ3: マニフェストファイルの作成

次に、JARファイルに「メインクラスは何か」を教えるためのマニフェストファイルを作成します。`manifest.txt`という名前で、以下の内容を記述します。

<span class="listing-number">**サンプルコード23-7**</span>

```text
Main-Class: SimpleApp

```
重要な注意点
- `Main-Class:`の後には半角スペースが1つ必要
- ファイルの末尾には改行を入れることが必須
- 改行がないと正しく認識されない場合がある

### リソースファイルを含むJARの作成

アプリケーションには画像やプロパティファイルなどのリソースが含まれることがあります。

<span class="listing-number">**サンプルコード23-9**</span>

```java
// ResourceApp.java
import javax.swing.*;
import java.awt.*;

public class ResourceApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("リソース付きアプリ");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            
            // アイコンの読み込み
            ImageIcon icon = new ImageIcon(
                ResourceApp.class.getResource("/icon.png")
            );
            JLabel label = new JLabel("画像リソースの例", icon, JLabel.CENTER);
            
            frame.add(label);
            frame.setSize(300, 200);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
```

リソースを含むJARの作成方法

<span class="listing-number">**サンプルコード23-10**</span>

```bash
# コンパイル
javac ResourceApp.java

# JARファイルの作成（リソースファイルも含める）
jar cvfm ResourceApp.jar manifest.txt ResourceApp.class icon.png
```

### ステップ4: `jar`コマンドによるパッケージング

`jar`コマンドを使って、クラスファイルとマニフェストファイルを1つのJARファイルにまとめます。

<span class="listing-number">**サンプルコード23-11**</span>

```bash
# 書式: jar cvfm [出力JARファイル名] [マニフェストファイル名] [含めるクラスファイル]
jar cvfm SimpleApp.jar manifest.txt SimpleApp.class
```
コマンドの各オプションの意味
- `c`：新しいアーカイブを作成する（create）
- `v`：詳細な情報を表示する（verbose）
- `f`：作成するファイル名を指定する（file）
- `m`：指定されたマニフェストファイルを取り込む（manifest）

このコマンドを実行すると、`SimpleApp.jar`という実行可能なJARファイルが作成されます。

### ステップ5: 実行

作成したJARファイルは、`java -jar`コマンドで実行できます。

<span class="listing-number">**サンプルコード23-12**</span>

```bash
java -jar SimpleApp.jar
```
多くのデスクトップ環境では、このJARファイルをダブルクリックするだけでもアプリケーションが起動します。これで、Java開発環境がない人にもアプリケーションを配布できるようになりました。

### IntelliJ IDEAでのJARファイル作成

IntelliJ IDEAでは、GUI操作で実行可能JARファイルを生成できます。

1. `File` → `Project Structure...` を選択する
2. `Artifacts` → `+` → `JAR` → `From modules with dependencies...` を選択する
3. `Main Class`として実行したいクラス（例：`SimpleApp`）を選択し、OKを押す
4. メニューの `Build` → `Build Artifacts...` → `(作成したArtifact名)` → `Build` を選択すると、`out/artifacts`ディレクトリ以下にJARファイルが生成される

## jpackageによるネイティブアプリケーション化

JARファイルは便利ですが、実行するにはユーザーのPCにJavaランタイム（JRE）がインストールされている必要があります。

Java 14から導入された`jpackage`ツールを使えば、アプリケーションと必要なJavaランタイムを丸ごとパッケージングし、OS標準のインストーラや実行ファイル（Windowsなら`.exe`、Macなら`.app`）を作成できます。 これにより、ユーザーはJavaの存在を意識することなく、普段使いのアプリケーションと同じようにインストール・実行できます。

### `jpackage`を使うための準備：入力と出力の分離

`jpackage`でエラーを起こさないための重要事項は、
「パッケージ化の材料（入力）が入ったディレクトリ」と
「作成されたアプリケーション（出力）を保存するディレクトリ」を
別々のディレクトリとして管理することです。

```
/MyProject/
|
|-- input/
|   └── SimpleApp.jar  <-- 先ほど作成したJARファイルをここに置く
|
|-- output/
|   └── (完成したアプリはここに作られる)
|
`-- SimpleApp.java (ソースコードなど)
```

### `jpackage`の実行例

プロジェクトのルートディレクトリ (`/MyProject/`) で、以下のコマンドを実行します。

<span class="listing-number">**サンプルコード23-13**</span>

```bash
jpackage --type app-image \
          --name "SimpleApp" \
          --input ./input \
          --main-jar SimpleApp.jar \
          --dest ./output
```
主なオプションの説明
- `--type app-image`：アプリケーションイメージを作成する。インストーラ形式にしたい場合は、OSに応じて`dmg`（macOS）、`msi`（Windows）、`rpm`/`deb`（Linux）などを指定する
- `--name "SimpleApp"`：アプリケーションの名前を指定する
- `--input ./input`：パッケージ化の材料（JARファイル）が入っているディレクトリを指定する
- `--main-jar SimpleApp.jar`：入力ディレクトリ内の、メインとなるJARファイル名を指定する
- `--dest ./output`：完成したアプリケーションを保存するディレクトリを指定する

### 各OSに向けた基本的なパッケージング例

#### Windows向け（.exeまたは.msi）

<span class="listing-number">**サンプルコード23-14**</span>

```bash
jpackage --type exe \
         --name "SimpleApp" \
         --input ./input \
         --main-jar SimpleApp.jar \
         --win-shortcut \
         --dest ./output
```

#### macOS向け（.appまたは.dmg）

<span class="listing-number">**サンプルコード23-15**</span>

```bash
jpackage --type dmg \
         --name "SimpleApp" \
         --input ./input \
         --main-jar SimpleApp.jar \
         --dest ./output
```

#### Linux向け（.debまたは.rpm）

<span class="listing-number">**サンプルコード23-16**</span>

```bash
jpackage --type deb \
         --name "simpleapp" \
         --input ./input \
         --main-jar SimpleApp.jar \
         --dest ./output
```

コマンドが成功すると、`output`ディレクトリ内にOSに応じたインストーラが作成されます。これは、Javaランタイムを含んだ自己完結型のアプリケーションであり、ユーザーはJavaをインストールせずにアプリケーションを使用できます。

## まとめ

本章では、開発したJavaアプリケーションをほかのユーザーに届けるための基本的な手法を学びました。

- JARファイルは、Javaのクラスファイルやリソースをまとめる標準的な方法
- マニフェストファイルに`Main-Class`を記述することで、実行可能なJARファイルを作成できる
- `java -jar`コマンドで、実行可能JARファイルを起動できる
- `jpackage`ツールを使えば、Javaランタイムを同梱したネイティブアプリケーションを作成できる

ソフトウェア開発は、コードを書くだけでなく、それを価値ある成果物としてユーザーに届けるところまでが含まれます。本章で学んだビルドと配布の知識は、あなたの作品を世界に公開するための第一歩です。

より高度なビルド技術については、以下の付録を参照してください。

※ 本章の高度な内容については、付録G「ビルドとデプロイ」を参照してください。
（`https://github.com/Nagatani/techbook-java-primer/tree/main/appendix/g-build-and-deploy/`）

## 章末演習

### 演習課題へのアクセス
本章の演習課題は、GitHubリポジトリで提供されています。
`https://github.com/Nagatani/techbook-java-primer/tree/main/exercises/chapter23/`

### 課題構成

1. 基礎課題：シンプルなアプリケーションの実行可能JAR作成
2. 発展課題：リソースファイルを含むJARファイルの作成
3. チャレンジ課題：各OS向けのネイティブインストーラ作成

詳細な課題内容と実装のヒントは、GitHubリポジトリの各課題フォルダ内のREADME.mdを参照してください。

