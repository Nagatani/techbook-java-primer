# <b>23章</b> <span>ビルドとデプロイ</span> <small>アプリケーションの配布と実行</small>

## 本章の学習目標

### この章で学ぶこと

この章では、開発したJavaアプリケーションを配布するための技術について学習します。
具体的には以下のトピックを扱います。

1. JARファイルの仕組みとその役割
2. マニフェストファイルの基本的な設定方法
3. 実行可能JARファイルの作成手順
4. jpackageツールによるネイティブアプリケーション化
5. 基本的なビルドとパッケージングの流れ

### この章を始める前に

この章を学習する前に、事前に習得しておくべき知識があります。
とくに重要なのは以下の内容です。

- 第1章のJava開発環境構築（javac、javaコマンド）
- 第14章のファイル入出力（基本的なI/O操作）
- 第17章のGUIプログラミングの基礎（サンプルアプリケーションにSwingを使う）
- 第22章のドキュメントと外部ライブラリ（クラスパスとJARファイル）
- 基本的なコマンドライン操作

推奨される知識：

- 第21章のユニットテスト（テスト自動化）
- 継続的インテグレーション（CI/CD）の基本概念
- ソフトウェアデプロイメントの基礎知識
- OS間の違いに関する基本理解（Windows、macOS、Linux）

## なぜビルドと配布が必要か

これまでの章では、主にIDEから直接ソースコードを実行してきました。しかし、開発したアプリケーションを他の人に使ってもらうには、ソースコードそのものを渡すわけにはいきません。

### ビルドプロセスの技術的意味

開発環境を持たないユーザーでも簡単に実行できるように、必要なファイルをまとめて実行可能な形式に変換します。この一連の作業を「ビルド」と「パッケージング」と呼び、最終的にユーザーに配布できる形にすることを「配布」といいます。

本章では、Javaアプリケーションを配布するためのもっとも基本的な形式である実行可能JARファイルの作成方法と、さらに一歩進んでOSネイティブのアプリケーションを作成する方法を学びます。

<div class="figure">

![ソースコードから配布できる形までの流れ](images/diagrams/chapter23-build-flow.svg)

</div>

## 実行可能JARファイルの作成

JAR（Java Archive）は、複数のJavaクラスファイルや画像・設定ファイルなどのリソースを、ZIP形式で1つにまとめたファイルです。
第22章では、外部ライブラリがJARファイルの形で配布され、クラスパスに含めて使うことを学びました。
本章では、自分のプログラムをJARファイルにまとめます。
このJARファイルに「どのクラスの`main`メソッドからプログラムを開始するか」という情報を加えることで、`java -jar`コマンドで実行できる「実行可能JARファイル」を作成できます。

### ステップ1: サンプルアプリケーションの準備

まずは、配布する簡単なSwingアプリケーションを用意します。

<span class="listing-number">**サンプルコード23-1**　SimpleApp.java</span>

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

実行結果：
このプログラムを実行すると、タイトルが「配布アプリ」の300×150ピクセルのウィンドウが画面中央に表示され、「JARファイルからの実行に成功しました！」というメッセージが表示されます。
コンソールには何も出力しません。

### ステップ2: コンパイル

まず、ソースコードをコンパイルしてクラスファイル (`.class`) を作成します。

```bash
javac SimpleApp.java
```
これにより、`SimpleApp.class`ファイルが生成されます。

### ステップ3: マニフェストファイルの作成

次に、JARファイルに「メインクラスは何か」を教えるためのマニフェストファイルを作成します。`manifest.txt`という名前で、以下の内容を記述します。

<span class="listing-number">**サンプルコード23-2**</span>

```text
Main-Class: SimpleApp

```

書くときには2つの点に注意してください。
1つは、`Main-Class:`の後に半角スペースを1つ入れることです。
もう1つは、ファイルの末尾に改行を入れることです。
最後の行が改行で終わっていないと、その行が正しく読み込まれない場合があります。

### ステップ4: `jar`コマンドによるパッケージング

`jar`コマンドを使って、クラスファイルとマニフェストファイルを1つのJARファイルにまとめます。

```bash
# 書式: jar cvfm [出力JARファイル名] [マニフェストファイル名] [含めるクラスファイル]
jar cvfm SimpleApp.jar manifest.txt SimpleApp.class
```

コマンドの各オプションの意味は次のとおりです。

- `c`
    + 新しいアーカイブを作成する（create）
- `v`
    + 詳細な情報を表示する（verbose）
- `f`
    + 作成するファイル名を指定する（file）
- `m`
    + 指定されたマニフェストファイルを取り込む（manifest）

このコマンドを実行すると、`SimpleApp.jar`という実行可能なJARファイルが作成されます。

### ステップ5: 実行

作成したJARファイルは、`java -jar`コマンドで実行できます。

```bash
java -jar SimpleApp.jar
```
多くのデスクトップ環境では、このJARファイルをダブルクリックするだけでもアプリケーションが起動します<span class="footnote">ダブルクリックで起動するのは、`.jar`の拡張子がJavaの実行環境に関連付けられている場合です。関連付けがないときやOSのセキュリティ機能に止められたときは、`java -jar`で実行します。</span>。
これで、JDKでコンパイルしなくても、Javaの実行環境がある人ならアプリケーションを実行できるようになりました。

### 複数のクラスファイルを含むアプリケーションの例

ここからは、ステップ1〜5の手順を応用して、もう少し実際のアプリケーションに近いJARを作ります。
実際のアプリケーションは通常、複数のクラスから構成されます。
次のTodoアプリケーションは、ウィンドウを組み立てる`TodoApp`クラスと、入力欄と「追加」ボタンをまとめた`TodoInputPanel`クラスの2つのファイルからなります。
`TodoInputPanel`は`JPanel`を継承したクラスで、「追加」ボタンが押されると、入力された文字を`TodoApp`から受け取ったリストのモデルに加えます。

<span class="listing-number">**サンプルコード23-3**　TodoApp.java（1、5〜6、12〜13、21〜22、27〜32行目）、TodoInputPanel.java（1、5〜21行目）</span>

```java
// TodoApp.java
// ...
public class TodoApp {
    private final DefaultListModel<String> listModel = new DefaultListModel<>();
    // ...
    private void createAndShowGUI() {
        JFrame frame = new JFrame("Todo Manager");
        // ...
        // 入力パネル（TodoInputPanel.javaで定義したクラス）
        frame.add(new TodoInputPanel(listModel), BorderLayout.SOUTH);
        // ...
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TodoApp::new);
    }
}

// TodoInputPanel.java
// ...
public class TodoInputPanel extends JPanel {
    private final JTextField inputField = new JTextField();
    private final JButton addButton = new JButton("追加");

    public TodoInputPanel(DefaultListModel<String> listModel) {
        super(new BorderLayout());
        addButton.addActionListener(e -> {
            String text = inputField.getText().trim();
            if (!text.isEmpty()) {
                listModel.addElement(text);
                inputField.setText("");
            }
        });
        add(inputField, BorderLayout.CENTER);
        add(addButton, BorderLayout.EAST);
    }
}
```

実行結果：
このプログラムを実行すると、タイトルが「Todo Manager」の400×300ピクセルのウィンドウが表示されます。
中央にリストの表示エリア、下部にテキストの入力欄と「追加」ボタンが並び、入力欄に文字を入れて「追加」を押すと、その文字がリストに加わります。
コンソールには何も出力しません。

複数のクラスファイルを含む場合は、次のようにJARを作成します。
マニフェストファイル`manifest.txt`には、ステップ3と同じ書き方で`Main-Class: TodoApp`と書いておきます。
`main`メソッドを持つのは`TodoApp`だけなので、`Main-Class`には`TodoApp`だけを書きます。
`TodoInputPanel.java`と`manifest.txt`は、GitHubリポジトリの全文と同じディレクトリに用意しています<span class="footnote">`https://github.com/Nagatani/techbook-java-primer/tree/main/sample-code/chapter23/23-03/`です。</span>。

```bash
# すべてのJavaファイルをコンパイル（TodoApp.classとTodoInputPanel.classができる）
javac *.java

# 複数のクラスファイルを含むJARを作成
jar cvfm TodoApp.jar manifest.txt *.class

# JARに含まれるファイルの一覧を表示
jar tf TodoApp.jar
```

`jar tf`は、JARファイルに含まれるファイルの一覧を表示します（`t`は一覧の表示、`f`はファイル名の指定）。
次のように、マニフェストと2つのクラスファイルが含まれていることを確かめられます。

```
META-INF/
META-INF/MANIFEST.MF
TodoApp.class
TodoInputPanel.class
```

`java -jar TodoApp.jar`で実行すると、JVMはマニフェストの`Main-Class`に書いた`TodoApp`から実行を始めます。
`TodoApp`が`TodoInputPanel`を使う時点で、同じJARのなかから`TodoInputPanel.class`を読み込みます。

### リソースファイルを含むJARの作成

アプリケーションには画像やプロパティファイルなどのリソースが含まれることがあります。

<span class="listing-number">**サンプルコード23-4**　ResourceApp.java</span>

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

実行すると、`icon.png`のアイコンと「画像リソースの例」という文字を中央に表示する、300×200ピクセルのウィンドウが開きます。コンソールには何も出力しません。

`getResource("/icon.png")`は、クラスパスの直下から`icon.png`を探し、見つからないと`null`を返します。
`null`をそのまま`ImageIcon`に渡すと`NullPointerException`になるため、`icon.png`はクラスファイルと同じ場所（JARにする場合はJARのなか）に置いてください。
`icon.png`と、次のJARの作成で使う`manifest.txt`（中身は`Main-Class: ResourceApp`）は、全文と同じディレクトリに用意しています<span class="footnote">`https://github.com/Nagatani/techbook-java-primer/tree/main/sample-code/chapter23/23-04/`です。</span>。

リソースを含むJARは、次のように作成します。

```bash
# コンパイル
javac ResourceApp.java

# JARファイルの作成（リソースファイルも含める）
jar cvfm ResourceApp.jar manifest.txt ResourceApp.class icon.png
```

### IntelliJ IDEAでのJARファイル作成

IntelliJ IDEAでは、GUI操作で実行可能JARファイルを生成できます。

1. `File` → `Project Structure...` を選択する
2. `Artifacts` → `+` → `JAR` → `From modules with dependencies...` を選択する
3. `Main Class`として実行したいクラス（例：`SimpleApp`）を選択し、OKを押す
4. メニューの `Build` → `Build Artifacts...` → `(作成したArtifact名)` → `Build` を選択すると、`out/artifacts`ディレクトリ以下にJARファイルが生成される

## jpackageによるネイティブアプリケーション化

JARファイルは便利ですが、実行するにはユーザーのPCにJavaランタイム（JRE）<span class="footnote">JREはJava Runtime Environmentの略です。OracleはJava 11からJREを単独では配布しておらず、アプリケーションに必要なモジュールだけを含むランタイムを`jlink`で作る方法を勧めています。</span>がインストールされている必要があります。

Java 16で正式に導入された`jpackage`ツール<span class="footnote">`jpackage`はJava 14で試験的な機能（インキュベーター）として加わり、Java 16（JEP 392）で正式な機能になりました。内部では`jlink`を使って、アプリケーションに同梱するランタイムを作ります。</span>を使えば、アプリケーションと必要なJavaランタイムを丸ごとパッケージングし、OS標準のインストーラー（Windowsの`.exe`・`.msi`、macOSの`.dmg`など）や、インストールせずに実行できるアプリケーション（macOSの`.app`など）を作成できます。これにより、ユーザーはJavaの存在を意識することなく、普段使いのアプリケーションと同じようにインストール・実行できます。

### `jpackage`を使うための準備：入力と出力の分離

`jpackage`でエラーを起こさないために、パッケージ化の材料（入力）を入れるディレクトリと、作成されたアプリケーション（出力）を保存するディレクトリを分けて管理します。

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

```bash
jpackage --type app-image \
          --name "SimpleApp" \
          --input ./input \
          --main-jar SimpleApp.jar \
          --dest ./output
```

主なオプションの意味は次のとおりです。

- `--type app-image`
    + アプリケーションイメージを作成する。インストーラー形式にしたい場合は、OSに応じて`dmg`（macOS）、`msi`（Windows）、`rpm`/`deb`（Linux）などを指定する
- `--name "SimpleApp"`
    + アプリケーションの名前を指定する
- `--input ./input`
    + パッケージ化の材料（JARファイル）が入っているディレクトリを指定する
- `--main-jar SimpleApp.jar`
    + 入力ディレクトリ内の、メインとなるJARファイル名を指定する
- `--dest ./output`
    + 完成したアプリケーションを保存するディレクトリを指定する

### 各OSに向けた基本的なパッケージング例

#### Windows向け（.exeまたは.msi）

```bash
jpackage --type exe \
         --name "SimpleApp" \
         --input ./input \
         --main-jar SimpleApp.jar \
         --win-shortcut \
         --dest ./output
```

#### macOS向け（.appまたは.dmg）

```bash
jpackage --type dmg \
         --name "SimpleApp" \
         --input ./input \
         --main-jar SimpleApp.jar \
         --dest ./output
```

#### Linux向け（.debまたは.rpm）

```bash
jpackage --type deb \
         --name "simpleapp" \
         --input ./input \
         --main-jar SimpleApp.jar \
         --dest ./output
```

コマンドが成功すると、`output`ディレクトリ内にOSに応じたインストーラーが作成されます。これは、Javaランタイムを含んだ自己完結型のアプリケーションであり、ユーザーはJavaをインストールせずにアプリケーションを使用できます<span class="footnote">インストーラーを作るには、OSごとに追加のツールが要ります。Windowsの`exe`・`msi`にはWiX Toolset、Linuxの`deb`には`dpkg-deb`と`fakeroot`、`rpm`には`rpmbuild`を使います。</span>。
先ほどの`--type app-image`はインストーラーではなく、インストールせずにそのまま実行できるアプリケーションのフォルダーを作ります。

## まとめ

本章では、開発したJavaアプリケーションをほかのユーザーに届けるための基本的な手法を学びました。

### ビルド技術の進化

本章で学んだ技術は、Javaのビルド・配布技術の基礎です。
実際の開発では、次のような高度な技術も使われます。

1. ビルド自動化ツール
    + 第22章で学んだMavenなどのビルドツールで、依存関係の管理に加えて、コンパイルからJARの作成までを1つのコマンドで行う
2. Fat JAR（Uber JAR）
    + 依存ライブラリを含む単一JARファイルの生成（Spring Boot、Shade Plugin）
3. ネイティブイメージ
    + GraalVMによるAOT（Ahead-of-Time）コンパイルで起動時間とメモリ使用量を削減する
4. コンテナー化
    + DockerやKubernetesによるアプリケーションの標準化された配布
5. CI/CD統合
    + 第21章でテストの自動実行に使ったGitHub Actionsなどで、ビルドと配布まで自動化する

### 今後の学習に向けて

ソフトウェア開発は、コードを書くだけでなく、それを価値ある成果物としてユーザーに届けるところまでが含まれます。

実務では、プロジェクトの規模や要件に合わせて、ビルドと配布の方法を選びます。小規模なツールであれば実行可能JARで十分ですが、エンタープライズアプリケーションではより高度な配布戦略が必要になります。本章で学んだビルドと配布の知識は、あなたの作品を世界に公開するための第一歩です。

Maven/Gradleによる自動化、Fat JARの作成、jpackageの高度な活用などは、付録G「高度なビルドとデプロイメント技術」で扱います。<br>
`https://github.com/Nagatani/techbook-java-primer/tree/main/appendix/g-build-and-deploy/`

## 章末演習

### 演習課題へのアクセス

本章の演習課題は、GitHubリポジトリで提供されています。<br>
`https://github.com/Nagatani/techbook-java-primer/tree/main/exercises/chapter23/`

### 課題構成

1. 基礎課題
    + シンプルなアプリケーションの実行可能JAR作成
2. 発展課題
    + リソースファイルを含むJARファイルの作成
3. チャレンジ課題
    + 各OS向けのネイティブインストーラー作成

詳細な課題内容と実装のヒントは、GitHubリポジトリの各課題フォルダー内のREADME.mdを参照してください<span class="footnote">各課題フォルダーは`https://github.com/Nagatani/techbook-java-primer/tree/main/exercises/chapter23/`の下の`basic/`・`advanced/`・`challenge/`です。</span>。
