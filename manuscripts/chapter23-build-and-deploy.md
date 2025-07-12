# 第23章 ビルドとデプロイ

## 本章の学習目標

### 前提知識

**ポイントとなる前提**：
- 第1章のJava開発環境構築（javac、java コマンド）
- 第15章のファイル入出力（クラスパス、リソースファイル）
- 第22章のドキュメントと外部ライブラリ（Maven、Gradle）
- 基本的なコマンドライン操作

**あるとよい前提**：
- 第21章のユニットテスト（テスト自動化）
- 継続的インテグレーション（CI/CD）の基本概念
- ソフトウェアデプロイメントの基礎知識
- OS間の違いに関する基本理解（Windows、macOS、Linux）

### 学習目標

**知識理解目標**：
- JARファイルの仕組みとクラスローダーの動作原理
- マニフェストファイルの役割と設定項目
- Fat JAR と通常のJARの違いと用途
- ネイティブイメージとGraalVMの基本概念

**技能習得目標**：
- コマンドラインとビルドスクリプトを使用したJAR作成
- リソースファイルを含むアプリケーションのパッケージング
- 外部ライブラリを統合したFat JARの作成
- クロスプラットフォーム対応のビルド設定

**実践的な活用目標**：
- エンドユーザー向けの実行可能アプリケーション配布
- 継続的インテグレーション環境での自動ビルド
- 本番環境でのデプロイメント自動化
- パフォーマンスと起動時間を考慮した最適化

**到達レベルの指標**：
- プロフェッショナルレベルのビルドスクリプトが作成できる
- 運用環境に適したデプロイメント戦略を選択できる
- 複雑な依存関係を持つアプリケーションを安全に配布できる
- 継続的デリバリーのパイプラインを構築できる

## はじめに

### 総合演習プロジェクトへのステップ

本章で学ぶビルドと配布の技術は、開発した**総合演習プロジェクト「ToDoリストアプリケーション」** を、Java開発環境がない友人やほかのユーザーにも使ってもらうための最終ステップです。

#### 技術的背景：モダンなソフトウェア配布の課題と解決策

**なぜ配布が複雑なのか：**

現代のソフトウェア配布には多くの課題があります：

1. **環境依存性の問題**
   - Java Runtime Environment (JRE) のバージョン差異
   - OS固有の実行形式（.exe, .app, .sh）
   - システムライブラリの依存関係
   - パス設定やレジストリの違い

2. **ユーザビリティの課題**
   - コマンドライン操作への抵抗感
   - 複雑なインストール手順
   - セキュリティ警告への対処
   - アンインストールの困難さ

3. **セキュリティ要件**
   - コード署名の必要性
   - ネットワーク経由の配布時の改ざん防止
   - 権限昇格の最小化
   - サンドボックス化の要求

**JARからネイティブアプリケーションへの進化：**

```
1990年代: クラスファイルの直接配布
2000年代: JARファイル（java -jar app.jar）
2010年代: Web Start, 自己完結型アプリ
2020年代: jpackage, GraalVM native-image
```

**実践的な配布戦略：**

1. **開発者向け配布**
   ```bash
   # Maven/Gradle経由
   mvn install
   gradle publishToMavenLocal
   ```

2. **エンドユーザー向け配布**
   ```bash
   # jpackageによるインストーラ作成
   jpackage --input lib --name MyApp \
     --main-jar myapp.jar --main-class com.example.Main \
     --type msi  # Windows
     --type dmg  # macOS
     --type deb  # Ubuntu/Debian
   ```

3. **クラウド時代の配布**
   ```dockerfile
   # Dockerコンテナ化
   FROM openjdk:17-slim
   COPY target/myapp.jar /app.jar
   ENTRYPOINT ["java", "-jar", "/app.jar"]
   ```

**実世界の配布例：**

- **IntelliJ IDEA**: カスタムJREバンドル、自動更新機能
- **Minecraft**: 独自ランチャー、マルチバージョン管理
- **Jenkins**: WAR形式、組込みーバー付き
- **ElasticSearch**: シェルスクリプトラッパ、サービス登録

**配布の自動化とCI/CD：**

```yaml
# GitHub Actions による自動リリース
name: Release
on:
  push:
    tags:
      - 'v*'
jobs:
  build:
    runs-on: ${{ matrix.os }}
    strategy:
      matrix:
        os: [ubuntu-latest, windows-latest, macos-latest]
    steps:
      - uses: actions/checkout@v3
      - name: Build with jpackage
        run: ./build-installer.sh
      - name: Upload artifacts
        uses: actions/upload-artifact@v3
```

本章では、これらの実践的な配布技術を段階的に学習します。

- **実行可能JARファイルの作成**: アプリケーションの全クラスファイル（`Task.class`, `TaskListPanel.class`など）を1つの実行可能な`todo-app.jar`ファイルにまとめます。これにより、ユーザーは`java -jar todo-app.jar`という簡単なコマンドだけでアプリケーションを起動できます。
- **`jpackage`によるネイティブアプリケーション化**: さらに一歩進んで、`jpackage`を使い、Windowsユーザー向けには`.exe`インストーラを、macOSユーザー向けには`.dmg`インストーラを作成します。これにより、ユーザーはJavaのインストールを意識することなく、普段使いのアプリケーションと同じようにインストールして利用できるようになり、配布のハードルが劇的に下がります。

### 本章の学習目標

#### 前提知識

本章を学習するためのポイントとなる前提として、第22章までに習得した総合的なJavaアプリケーション開発能力がポイントです。これまでの章で学んだすべての技術を統合し、実用的なアプリケーションを完成させられるレベルに達していることが大切です。また、ビルドプロセスの基本概念についての理解もポイントで、ソースコードからクラスファイルへのコンパイル、依存関係の解決、パッケージングといった一連の流れを概念的に理解していることが求められます。さらに、ソフトウェア配布に関する課題の理解も大切です。開発環境と実行環境の違い、依存関係の管理、プラットフォーム間の差異など、アプリケーションを他者に届ける際に直面する実際的な問題を認識していることで、本章の内容の大切さをより深く理解できます。

システム理解の前提として、コマンドライン操作の基本的なスキルがポイントです。javac、java、jarといった基本的なコマンドを使いこなし、ファイルシステムの操作に慣れていることが求められます。また、Windows、macOS、Linuxという主要なOSの基本的な知識も大切です。それぞれのOSにおけるアプリケーションのインストール方法、実行形式の違い、パス設定などを理解していることで、クロスプラットフォーム対応の配布戦略をより効果的に学べます。

#### 学習目標

本章では、開発したJavaアプリケーションを実際にユーザーに届けるための技術を体系的に学習します。知識理解の面では、まずビルドとデプロイの本質的な意義と大切さを理解します。ソフトウェア開発は、コードを書くことだけでなく、それを価値ある成果物として適切にパッケージ化し、ユーザーが簡単に利用できる形で提供することまでを含みます。この「最後の1マイル」がなぜ大切なのか、そしてそれがビジネス価値にどのように直結するのかを理解します。

JARファイルのしくみとクラスパスについての深い理解も大切な学習目標です。JARファイルがどのようにクラスファイルやリソースをアーカイブし、Java仮想マシンがどのようにそれらを読み込むのかを理解します。マニフェストファイルの役割、クラスパスの解決メカニズム、実行可能JARの作成方法など、JARファイルに関する包括的な知識を習得します。また、Java 9で導入されたモジュールシステムとjlinkの概念についても学習し、必要最小限のランタイムを含むカスタムイメージの作成方法を理解します。

技能習得の観点では、実行可能JARファイルの作成と配布の実践的なスキルを習得します。単純なアプリケーションから、外部ライブラリを含む複雑なアプリケーションまで、さまざまなケースに対応したJARファイルの作成方法を学びます。jpackageを使ったネイティブインストーラの作成では、Windows用のMSIインストーラ、macOS用のDMGイメージ、Linux用のDEB/RPMパッケージなど、各プラットフォームのネイティブ形式でアプリケーションをパッケージ化する技術を習得します。

プロダクション能力の面では、エンドユーザー向けの使いやすいアプリケーション配布戦略を学びます。インストールの簡便性、アップデートの容易さ、アンインストールのクリーンさなど、ユーザー体験を重視した配布方法を設計する能力を養います。継続的デプロイ（CD）の基本実装では、ビルドの自動化、テストの統合、自動リリースなど、現代的なソフトウェア配布のベストプラクティスを学びます。また、コード署名、セキュリティスキャン、ライセンス管理など、企業環境で求められるセキュリティと法的要件への対応方法も習得します。

最終的な到達レベルとして、プロフェッショナルな品質の配布パッケージが作成できるようになることを目指します。これには、適切なバージョニング、分かりやすいインストール手順、包括的なドキュメントが含まれます。各種プラットフォーム向けの最適化された配布により、Windows、macOS、Linuxのユーザーそれぞれに最適な体験を提供できます。継続的なアップデート配布システムの構築により、バグ修正や新機能を迅速かつ安全にユーザーに届けることができます。最終的には、スケーラビリティ、セキュリティ、ユーザビリティを考慮した企業レベルのソフトウェア配布戦略を設計できる能力を身につけます。



## 23.1 なぜビルドと配布が必要か？

これまでの章では、主にIDE（統合開発環境）から直接ソースコードを実行してきました。しかし、開発したアプリケーションをほかの人に使ってもらうには、ソースコードそのものを渡すわけにはいきません。

ユーザーがJava開発環境を持っていない場合でも簡単に実行できるように、必要なファイルをすべて1つにまとめ、実行可能な形式に変換する必要があります。この一連の作業を「**ビルド**」と「**パッケージング**」と呼び、最終的にユーザーに渡せる形にすることを「**配布**」と言います。

本章では、Javaアプリケーションを配布するための最も基本的な形式である**実行可能JARファイル**の作成方法と、さらに一歩進んでOSネイティブのアプリケーションを作成する方法を学びます。

## 23.2 実行可能JARファイルの作成

**JAR (Java Archive)** は、複数のJavaクラスファイルや、画像・設定ファイルなどのリソースを、ZIP形式で1つにまとめたファイルです。このJARファイルに「どのクラスの`main`メソッドからプログラムを開始するか」という情報を加えることで、ダブルクリックや簡単なコマンドで実行できる「実行可能JARファイル」を作成できます。

#### ステップ1: サンプルアプリケーションの準備

まずは、配布する簡単なSwingアプリケーションを用意します。

<span class="listing-number">**サンプルコード23-1**</span>

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

#### 複数のクラスファイルを含むアプリケーションの例

実際のアプリケーションは通常、複数のクラスから構成されます。以下は、より実践的な例です：

<span class="listing-number">**サンプルコード23-2**</span>

```java
// AppConfig.java
public class AppConfig {
    private static final String VERSION = "1.0.0";
    private static final String APP_NAME = "Todo Manager";
    
    public static String getVersion() {
        return VERSION;
    }
    
    public static String getAppName() {
        return APP_NAME;
    }
}
```

次に、データモデルとしてのTodoItemクラスを作成します。このクラスはタスクの情報を保持し、完了状態を管理します。toStringメソッドをオーバーライドして、リスト表示用のフォーマットを提供します。

<span class="listing-number">**サンプルコード23-3**</span>

```java
// TodoItem.java
public class TodoItem {
    private String title;
    private boolean completed;
    
    public TodoItem(String title) {
        this.title = title;
        this.completed = false;
    }
    
    public String getTitle() {
        return title;
    }
    
    public boolean isCompleted() {
        return completed;
    }
    
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
    
    @Override
    public String toString() {
        return (completed ? "[✓] " : "[ ] ") + title;
    }
}
```

<span class="listing-number">**サンプルコード23-4**</span>

```java
// TodoApp.java
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TodoApp {
    private List<TodoItem> items = new ArrayList<>();
    private DefaultListModel<TodoItem> listModel = new DefaultListModel<>();
    
    public TodoApp() {
        createAndShowGUI();
    }
    
    private void createAndShowGUI() {
        JFrame frame = new JFrame(AppConfig.getAppName() + " v" + AppConfig.getVersion());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        
        // リストコンポーネント
        JList<TodoItem> todoList = new JList<>(listModel);
        todoList.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(todoList);
        frame.add(scrollPane, BorderLayout.CENTER);
        
        // 入力パネル
        JPanel inputPanel = new JPanel(new BorderLayout());
        JTextField inputField = new JTextField();
        JButton addButton = new JButton("追加");
        
        addButton.addActionListener(e -> {
            String text = inputField.getText().trim();
            if (!text.isEmpty()) {
                TodoItem item = new TodoItem(text);
                items.add(item);
                listModel.addElement(item);
                inputField.setText("");
            }
        });
        
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(addButton, BorderLayout.EAST);
        frame.add(inputPanel, BorderLayout.SOUTH);
        
        // ボタンパネル
        JPanel buttonPanel = new JPanel();
        JButton completeButton = new JButton("完了/未完了");
        JButton deleteButton = new JButton("削除");
        
        completeButton.addActionListener(e -> {
            int index = todoList.getSelectedIndex();
            if (index >= 0) {
                TodoItem item = items.get(index);
                item.setCompleted(!item.isCompleted());
                listModel.setElementAt(item, index);
            }
        });
        
        deleteButton.addActionListener(e -> {
            int index = todoList.getSelectedIndex();
            if (index >= 0) {
                items.remove(index);
                listModel.remove(index);
            }
        });
        
        buttonPanel.add(completeButton);
        buttonPanel.add(deleteButton);
        frame.add(buttonPanel, BorderLayout.NORTH);
        
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(TodoApp::new);
    }
}
```

複数のクラスファイルを含むJARを作成するには：

```bash
# すべてのJavaファイルをコンパイル
javac *.java

# 複数のクラスファイルを含むJARを作成
jar cvfm TodoApp.jar manifest.txt *.class
```

#### ステップ2: コンパイル

まず、ソースコードをコンパイルしてクラスファイル (`.class`) を作成します。

```bash
javac SimpleApp.java
```
これにより、`SimpleApp.class`ファイルが生成されます。

#### ステップ3: マニフェストファイルの作成

次に、JARファイルに「メインクラスは何か」を教えるための**マニフェストファイル**を作成します。`manifest.txt`という名前で、以下の内容を記述します。

```text
Main-Class: SimpleApp

```
**【大切】**: `Main-Class:`の後には半角スペースが1つ必要です。また、**ファイルの末尾には改行を入れることが大切です。** 改行がないと正しく認識されない場合があります。

#### マニフェストファイルの詳細なオプション

マニフェストファイルには、`Main-Class`以外にもさまざまな情報を記述できます：

```text
Manifest-Version: 1.0
Main-Class: TodoApp
Class-Path: lib/commons-lang3-3.12.0.jar lib/gson-2.10.1.jar
Created-By: 21.0.6 (Oracle Corporation)
Implementation-Title: Todo Manager Application
Implementation-Version: 1.0.0
Implementation-Vendor: MyCompany Inc.
Specification-Title: Todo Management API
Specification-Version: 1.0
Specification-Vendor: MyCompany Inc.
Sealed: true

```

各属性の説明：
- **Class-パス**: 外部ライブラリへのパスを指定（相対パス）
- **Created-By**: JARを作成したJDKのバージョン
- **Implementation-***: 実装に関する情報（バージョン、ベンダーなど）
- **Specification-***: 仕様に関する情報
- **Sealed**: trueにすると、このJAR内のパッケージはほかのJARから拡張できない

#### リソースファイルを含むJARの作成

アプリケーションには画像やプロパティファイルなどのリソースが含まれることがあります：

<span class="listing-number">**サンプルコード23-5**</span>

```java
// ResourceApp.java
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ResourceApp {
    private Properties config = new Properties();
    
    public ResourceApp() {
        loadConfiguration();
        createGUI();
    }
    
    private void loadConfiguration() {
        try (InputStream is = getClass().getResourceAsStream("/config.properties")) {
            if (is != null) {
                config.load(is);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void createGUI() {
        JFrame frame = new JFrame(config.getProperty("app.title", "Default App"));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // アイコンの読み込み
        ImageIcon icon = new ImageIcon(getClass().getResource("/icon.png"));
        JLabel label = new JLabel("リソースを含むアプリケーション", icon, JLabel.CENTER);
        label.setFont(new Font("Sans-serif", Font.BOLD, 16));
        
        frame.add(label);
        frame.setSize(400, 200);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(ResourceApp::new);
    }
}
```

```properties
# config.properties
app.title=Resource Demo Application
app.version=1.0.0
app.author=Developer
```

リソースを含むJARの作成方法：

```bash
# ディレクトリ構造
# project/
# ├── ResourceApp.java
# ├── config.properties
# ├── icon.png
# └── manifest.txt

# コンパイル
javac ResourceApp.java

# JARファイルの作成（リソースファイルも含める）
jar cvfm ResourceApp.jar manifest.txt ResourceApp.class config.properties icon.png
```

#### ステップ4: `jar`コマンドによるパッケージング

`jar`コマンドを使って、クラスファイルとマニフェストファイルを1つのJARファイルにまとめます。

```bash
# 書式: jar cvfm [出力JARファイル名] [マニフェストファイル名] [含めるクラスファイル]
jar cvfm SimpleApp.jar manifest.txt SimpleApp.class
```
コマンドの各オプションの意味は以下のとおりです。
- `c`: 新しいアーカイブを作成する (create)
- `v`: 詳細な情報を表示する (verbose)
- `f`: 作成するファイル名を指定する (file)
- `m`: 指定されたマニフェストファイルを取り込む (manifest)

このコマンドを実行すると、`SimpleApp.jar`という実行可能なJARファイルが作成されます。

#### ステップ5: 実行

作成したJARファイルは、`java -jar`コマンドで実行できます。

```bash
java -jar SimpleApp.jar
```
多くのデスクトップ環境では、このJARファイルをダブルクリックするだけでもアプリケーションが起動します。これで、Java開発環境がない人にもアプリケーションを配布できるようになりました。

#### IntelliJ IDEAでのJARファイル作成
IntelliJ IDEAでは、GUI操作で実行可能JARファイルを生成できます。
1.  `File` -> `Project Structure...` を選択します。
2.  `Artifacts` -> `+` -> `JAR` -> `From modules with dependencies...` を選択します。
3.  `Main Class`として実行したいクラス（例： `SimpleApp`）を選択し、OKを押します。
4.  メニューの `Build` -> `Build Artifacts...` -> `(作成したArtifact名)` -> `Build` を選択すると、`out/artifacts`ディレクトリ以下にJARファイルが生成されます。

#### 外部ライブラリを含むFat JARの作成

外部ライブラリを使用するアプリケーションの場合、すべての依存関係を1つのJARにまとめた「Fat JAR」を作成できます：

以下の例では、Gsonライブラリを使用したJSON処理アプリケーションを示します。このアプリケーションをFat JARとしてパッケージングすることで、Gsonライブラリも含めた自己完結型の実行可能JARを作成できます。

<span class="listing-number">**サンプルコード23-6**</span>

```java
// JsonProcessorApp.java
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;

public class JsonProcessorApp {
    
    static class Person {
        private String name;
        private int age;
        private LocalDateTime created;
        
        public Person(String name, int age) {
            this.name = name;
            this.age = age;
            this.created = LocalDateTime.now();
        }
        
        // Getters and Setters
        public String getName() { return name; }
        public int getAge() { return age; }
        public Date getCreated() { return created; }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("JSON Processor");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());
            
            // 入力パネル
            JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 5));
            inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            inputPanel.add(new JLabel("Name:"));
            JTextField nameField = new JTextField();
            inputPanel.add(nameField);
            
            inputPanel.add(new JLabel("Age:"));
            JTextField ageField = new JTextField();
            inputPanel.add(ageField);
            
            JButton convertButton = new JButton("Convert to JSON");
            inputPanel.add(convertButton);
            inputPanel.add(new JLabel()); // 空のセル
            
            // 出力エリア
            JTextArea outputArea = new JTextArea(10, 40);
            outputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
            outputArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(outputArea);
            
            // ボタンのアクション
            Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();
                
            convertButton.addActionListener(e -> {
                try {
                    String name = nameField.getText();
                    int age = Integer.parseInt(ageField.getText());
                    
                    Person person = new Person(name, age);
                    String json = gson.toJson(person);
                    
                    outputArea.setText(json);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, 
                        "年齢は数値で入力してください", 
                        "入力エラー", 
                        JOptionPane.ERROR_MESSAGE);
                }
            });
            
            frame.add(inputPanel, BorderLayout.NORTH);
            frame.add(scrollPane, BorderLayout.CENTER);
            
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
```

Fat JARを手動で作成する方法：

```bash
# 1. 作業ディレクトリの準備
mkdir temp
cd temp

# 2. 外部ライブラリ（gson-2.10.1.jar）を展開
jar xf ../lib/gson-2.10.1.jar

# 3. アプリケーションのクラスファイルをコピー
cp ../JsonProcessorApp*.class .

# 4. マニフェストファイルの作成
echo "Main-Class: JsonProcessorApp" > manifest.txt
echo "" >> manifest.txt

# 5. すべてを含むFat JARの作成
jar cvfm ../JsonProcessorApp-all.jar manifest.txt .

# 6. 一時ディレクトリの削除
cd ..
rm -rf temp
```

#### Maven/Gradleでのビルド設定例

現代のJava開発では、MavenやGradleなどのビルドツールを使用してプロジェクトを管理するのが一般的です。これらのツールを使用することで、依存関係の管理や実行可能JARの作成が大幅に簡素化されます。

##### Mavenでの実行可能JAR作成（pom.xml）

以下はpom.xmlの設定例です。maven-jar-pluginで通常の実行可能JARを、maven-shade-pluginで依存ライブラリを含むFat JARを作成できます。

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.example</groupId>
    <artifactId>todo-app</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>
    
    <properties>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>com.google.code.gson</groupId>
            <artifactId>gson</artifactId>
            <version>2.10.1</version>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <!-- 実行可能JARを作成するプラグイン -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-jar-plugin</artifactId>
                <version>3.3.0</version>
                <configuration>
                    <archive>
                        <manifest>
                            <addClasspath>true</addClasspath>
                            <mainClass>com.example.TodoApp</mainClass>
                        </manifest>
                    </archive>
                </configuration>
            </plugin>
            
            <!-- Fat JARを作成するプラグイン -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-shade-plugin</artifactId>
                <version>3.5.1</version>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals>
                            <goal>shade</goal>
                        </goals>
                        <configuration>
                            <transformers>
                                <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                                    <mainClass>com.example.TodoApp</mainClass>
                                </transformer>
                            </transformers>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

Mavenでのビルドコマンド：

```bash
# クリーンビルド
mvn clean package

# 実行可能JARの実行
java -jar target/todo-app-1.0.0.jar
```

##### Gradleでの実行可能JAR作成（build.gradle）

Gradleでの設定例です。applicationプラグインを使用することで、アプリケーションの実行と配布が簡単になります。カスタムタスクでFat JARの作成も定義しています。

```gradle
plugins {
    id 'java'
    id 'application'
}

group = 'com.example'
version = '1.0.0'

repositories {
    mavenCentral()
}

dependencies {
    implementation 'com.google.code.gson:gson:2.10.1'
    implementation 'org.apache.commons:commons-lang3:3.12.0'
}

application {
    mainClass = 'com.example.TodoApp'
}

jar {
    manifest {
        attributes(
            'Main-Class': 'com.example.TodoApp',
            'Implementation-Title': 'Todo Application',
            'Implementation-Version': version
        )
    }
}

// Fat JARタスク
task fatJar(type: Jar) {
    manifest {
        attributes 'Main-Class': 'com.example.TodoApp'
    }
    archiveClassifier = 'all'
    from {
        configurations.runtimeClasspath.collect { it.isDirectory() ? it : zipTree(it) }
    }
    with jar
}
```

Gradleでのビルドコマンド：

```bash
# 通常のビルド
gradle build

# Fat JARの作成
gradle fatJar

# 実行
java -jar build/libs/todo-app-1.0.0-all.jar
```

## 23.3 （応用）`jpackage`によるネイティブアプリケーション化

JARファイルは便利ですが、実行するにはユーザーのPCにJavaランタイム（JRE）がインストールされている必要があります。

Java 14から導入された`jpackage`ツールを使えば、**アプリケーションと必要なJavaランタイムを丸ごとパッケージングし、OS標準のインストーラや実行ファイル（Windowsなら`.exe`、Macなら`.app`）を作成できます。** これにより、ユーザーはJavaの存在を意識することなく、普段使いのアプリケーションと同じようにインストール・実行できます。

#### `jpackage`を使うための準備：入力と出力の分離

`jpackage`でエラーを起こさないための重要なポイントは、**「パッケージ化の材料（入力）が入ったディレクトリ」と「作成されたアプリケーション（出力）を保存するディレクトリ」**を完全に分けることです。

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

#### `jpackage`の実行例

プロジェクトのルートディレクトリ (`/MyProject/`) で、以下のコマンドを実行します。

```bash
jpackage --type app-image \
          --name "SimpleApp" \
          --input ./input \
          --main-jar SimpleApp.jar \
          --dest ./output
```
- `--type app-image`: アプリケーションイメージを作成します。インストーラ形式にしたい場合は、OSに応じて`dmg`(macOS), `msi`(Windows), `rpm`/`deb`(Linux)などを指定します。
- `--name "SimpleApp"`: アプリケーションの名前を指定します。
- `--input ./input`: パッケージ化の材料（JARファイル）が入っているディレクトリを指定します。
- `--main-jar SimpleApp.jar`: 入力ディレクトリ内の、メインとなるJARファイル名を指定します。
- `--dest ./output`: 完成したアプリケーションを保存するディレクトリを指定します。

#### jpackageの詳細なオプション

より本格的なアプリケーション配布のための高度なオプション例：

##### Windows向けの設定（.msiインストーラ）

```bash
jpackage --type msi \
         --name "TodoManager" \
         --app-version "1.0.0" \
         --vendor "MyCompany Inc." \
         --description "Simple Todo Management Application" \
         --copyright "Copyright (c) 2024 MyCompany Inc." \
         --input ./input \
         --main-jar TodoApp.jar \
         --icon ./resources/app-icon.ico \
         --win-menu \
         --win-shortcut \
         --win-dir-chooser \
         --win-per-user-install \
         --dest ./output
```

追加オプションの説明：
- `--app-version`: アプリケーションのバージョン
- `--vendor`: ベンダー名（開発元）
- `--icon`: アプリケーションアイコン（.ico形式）
- `--win-menu`: スタートメニューにショートカットを作成
- `--win-shortcut`: デスクトップにショートカットを作成
- `--win-dir-chooser`: インストール時にディレクトリ選択を可能にする
- `--win-per-user-install`: ユーザーごとのインストール（管理者権限不要）

##### macOS向けの設定（.dmgインストーラ）

```bash
jpackage --type dmg \
         --name "TodoManager" \
         --app-version "1.0.0" \
         --vendor "MyCompany Inc." \
         --description "Simple Todo Management Application" \
         --input ./input \
         --main-jar TodoApp.jar \
         --icon ./resources/app-icon.icns \
         --mac-package-name "com.mycompany.todomanager" \
         --mac-package-identifier "com.mycompany.todomanager" \
         --mac-sign \
         --mac-signing-key-user-name "Developer ID Application: MyCompany Inc." \
         --dest ./output
```

macOS固有オプション：
- `--icon`: macOS用アイコン（.icns形式）
- `--mac-package-identifier`: バンドル識別子
- `--mac-sign`: アプリケーションに署名（Gatekeeperに必要）
- `--mac-signing-key-user-name`: 署名に使用する開発者証明書

##### Linux向けの設定（.debパッケージ）

```bash
jpackage --type deb \
         --name "todomanager" \
         --app-version "1.0.0" \
         --vendor "MyCompany Inc." \
         --description "Simple Todo Management Application" \
         --input ./input \
         --main-jar TodoApp.jar \
         --icon ./resources/app-icon.png \
         --linux-menu-group "Office" \
         --linux-shortcut \
         --linux-deb-maintainer "dev@mycompany.com" \
         --linux-app-category "office" \
         --dest ./output
```

Linux固有オプション：
- `--linux-menu-group`: メニューカテゴリ
- `--linux-shortcut`: デスクトップショートカット作成
- `--linux-deb-maintainer`: パッケージメンテナーー情報
- `--linux-app-category`: アプリケーションカテゴリ

#### ランタイムオプションとJVMパラメータ

アプリケーションの実行時にJVMパラメータを設定することもできます：

```bash
jpackage --type app-image \
         --name "TodoManager" \
         --input ./input \
         --main-jar TodoApp.jar \
         --java-options "-Xmx512m" \
         --java-options "-Dfile.encoding=UTF-8" \
         --java-options "-Duser.language=ja" \
         --java-options "-Duser.country=JP" \
         --arguments "arg1" \
         --arguments "arg2" \
         --dest ./output
```

- `--java-options`: JVMオプション（複数指定可能）
- `--arguments`: アプリケーションへの引数

#### ファイル関連付けの設定

特定のファイル拡張子をアプリケーションに関連付ける：

```bash
jpackage --type msi \
         --name "TodoManager" \
         --input ./input \
         --main-jar TodoApp.jar \
         --file-associations ./file-associations.properties \
         --dest ./output
```

file-associations.propertiesの内容：
```properties
extension=todo
mime-type=application/x-todo
description=Todo List File
icon=./resources/todo-file-icon.ico
```

#### モジュラーアプリケーションのパッケージング

Java 9以降のモジュールシステムを使用している場合：

```bash
# jlinkでカスタムランタイムを作成
jlink --add-modules java.desktop,java.logging \
      --output custom-runtime

# jpackageでパッケージング
jpackage --type app-image \
         --name "ModularApp" \
         --runtime-image ./custom-runtime \
         --module com.example.app/com.example.app.Main \
         --dest ./output
```

コマンドが成功すると、`output`ディレクトリ内に`SimpleApp`という名前のアプリケーションが作成されます。これは、Javaランタイムを含んだ自己完結型のアプリケーションであり、ほかのユーザーにそのまま配布できます。

`jpackage`は、Javaアプリケーションをより多くのユーザーに届けるための非常に強力なツールです。


## まとめ

本章では、開発したJavaアプリケーションをほかのユーザーに届けるための基本的な手法を学びました。

- **JARファイル**は、Javaのクラスファイルやリソースをまとめる標準的な方法です。
- **マニフェストファイル**に`Main-Class`を記述することで、実行可能なJARファイルを作成できます。
- **`java -jar`コマンド**で、実行可能JARファイルを起動できます。
- **`jpackage`ツール**を使えば、Javaランタイムを同梱した、より配布しやすいネイティブアプリケーションを作成できます。

ソフトウェア開発は、コードを書くだけでなく、それを価値ある成果物としてユーザーに届けるところまでが含まれます。本章で学んだビルドと配布の知識は、あなたの作品を世界に公開するための第一歩です。

## 章末演習

本章で学んだビルドとデプロイを実践的な課題で確認しましょう。

### 演習課題へのアクセス

本書の演習課題は、以下のGitHubリポジトリで提供されています：

**リポジトリ**: `https://github.com/[your-repo]/java-primer-exercises`

### 第23章の課題構成

```
exercises/chapter23/
├── basic/              # 基礎課題（ポイント）
│   ├── README.md       # 詳細な課題説明
│   └── [関連ファイル名]
├── advanced/           # 発展課題（推奨）
├── challenge/          # チャレンジ課題（任意）
└── solutions/          # 解答例（実装後に参照）
```

### 学習の目標

本章の演習を通じて以下のスキルを習得します：
- 実行可能JARファイルの作成と配布
- Maven/Gradleを使った自動ビルド
- jpackageによるネイティブアプリケーションの作成

### 課題の概要

1. **基礎課題**: シンプルなアプリケーションの実行可能JAR作成
2. **発展課題**: 外部ライブラリを含むFat JARの作成とビルド自動化
3. **チャレンジ課題**: 各OS向けのネイティブインストーラ作成

詳細な課題内容と実装のヒントは、GitHubリポジトリの各課題フォルダ内のREADME.mdを参照してください。

**次のステップ**: 基礎課題が完了したら、総合演習プロジェクトに進みましょう。