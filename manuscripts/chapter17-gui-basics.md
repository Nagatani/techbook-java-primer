# 第17章 GUIプログラミングの基礎

## 本章の学習目標

### 前提知識

本章を学習するためには、第16章までに習得した総合的なJavaプログラミング能力が必須となります。とくに、クラスとオブジェクトの設計、継承とインターフェイス、例外処理、コレクションフレームワーク、ファイル入出力など、これまで学んだ技術要素を組み合わせて実用的なプログラムを作成できる能力が求められます。また、イベント処理の基本概念について理解していることも重要です。ユーザーの操作（ボタンクリック、キー入力など）に応じてプログラムが反応するという、これまでのコンソールアプリケーションとは異なるプログラミングパラダイムを理解する準備ができていることが必要です。

さらに、オブジェクト指向設計の実践経験があることで、GUIコンポーネントの階層構造や、イベントリスナパターンなどの設計思想をより深く理解できます。

ユーザーインターフェイスの前提として、日常的にGUIアプリケーションを使用した経験があることが望ましいです。Windows、macOS、あるいはLinuxのデスクトップ環境でアプリケーションを使用し、メニュー、ボタン、テキストフィールド、スクロールバーなどの標準的なGUIコンポーネントの動作を体験的に理解していることで、開発者としての視点でこれらの実装方法を学ぶことができます。また、ユーザビリティに対する基本的な理解、つまり使いやすいインターフェイスとは何か、ユーザーの期待に応える操作性とは何かという問題意識を持っていることで、単に動くGUIではなく、使いやすいGUIを設計する能力の基礎を築くことができます。

### 学習目標

本章では、Javaを使ったデスクトップGUIアプリケーション開発の基礎を体系的に学習します。知識理解の面では、まずGUIプログラミングの基本概念と、それがもたらす新たな課題について理解します。コンソールアプリケーションが上から下へ順番に実行される手続き的なモデルであるのに対し、GUIアプリケーションはユーザーの操作を待ち、それに反応するイベント駆動型のモデルを採用しています。この根本的な違いを理解することが、効果的なGUIプログラミングの第一歩となります。

Swingフレームワークのアーキテクチャについても深く理解します。SwingはJavaの標準GUIツールキットとして、軽量コンポーネントアーキテクチャを採用し、プラットフォーム独立性を保ちながら豊富な機能を提供します。コンポーネントの階層構造、ペイントシステム、イベントディスパッチスレッド（EDT）など、Swingの内部動作を理解することで、より効率的で安定したGUIアプリケーションを開発できます。また、BorderLayout、FlowLayout、GridLayoutなど、さまざまなレイアウトマネージャーの特徴を理解し、画面サイズの変更に柔軟に対応できる設計方法を学びます。

技能習得の観点では、JFrame、JButton、JTextField、JLabelなどの基本的なGUIコンポーネントの使用方法から始め、これらを組み合わせて実用的なインターフェイスを構築する技術を習得します。イベントリスナの実装では、ActionListener、MouseListener、KeyListenerなどのインターフェイスを使って、ユーザーの操作に応答するプログラムを作成します。ラムダ式を使った簡潔なイベントハンドラの記述方法も学び、現代的なJavaプログラミングスタイルを身につけます。

アプリケーション設計能力の面では、Model-View-Controller（MVC）パターンを意識したGUI設計を学びます。ビジネスロジックとプレゼンテーション層を適切に分離することで、テスト可能で保守性の高いアプリケーションを構築する方法を習得します。また、ユーザビリティを考慮したインターフェイス設計の基本原則、たとえば一貫性のある操作性、適切なフィードバック、エラー処理などを実践的に学びます。

最終的な到達レベルとして、基本的な機能を持つGUIアプリケーションを独力で開発できるようになることを目指します。これには、ユーザーの操作に適切に応答するイベント処理の実装、画面レイアウトとコンポーネント配置の適切な設計、そしてファイルI/Oやデータベースアクセスなど、これまで学んだ技術とGUIを連携させたアプリケーションの作成能力が含まれます。これらのスキルにより、エンドユーザーに価値を提供する実用的なデスクトップアプリケーションを開発できます。



## 章の構成

本章は、GUIプログラミングの基礎を体系的に学習できるよう、以下のパートで構成されています：

### [Part A: 基本コンポーネントとレイアウト](chapter17a-basic-components.md)
- はじめてのGUIプログラム
- 基本コンポーネントの配置（JFrame、JLabel、JButton）
- レイアウトマネージャーの基礎（BorderLayout、GridLayout）
- パネルを使った複雑なレイアウト

### [Part B: イベント処理入門](chapter17b-event-handling-intro.md)
- イベント駆動プログラミングの概念
- ボタンクリックに反応するプログラム
- ActionListenerとラムダ式
- カウンタアプリケーション

### [Part C: EDT（Event Dispatch Thread）とスレッド安全性](chapter17c-edt-thread-safety.md)
- EDTとは何か、なぜ重要なのか
- SwingUtilities.invokeLater()の使用方法
- SwingWorkerによる非同期処理
- GUIアプリケーションのスレッド安全性

### [Part D: カスタムコンポーネントの作成](chapter17d-custom-components.md)
- JComponentを継承したカスタムコンポーネント
- paintComponent()メソッドのオーバーライド
- マウスイベントとキーボードイベントの処理
- 高度なカスタムコンポーネントの実装例

### [Part E: 章末演習](chapter17e-exercises.md)
- 電卓アプリケーション
- シンプルメモ帳
- レイアウトデモ
- カラーピッカー

## 学習の進め方

1. Part AでSwingの基本コンポーネントとレイアウトを理解
2. Part Bでイベント駆動プログラミングの基礎を習得
3. Part CでEDTとスレッド安全性の重要概念を学習
4. Part Dでカスタムコンポーネント作成技術を習得
5. Part Eの演習課題で実践的なスキルを身につける

各パートは独立して読むことも可能ですが、順番に学習することで、GUIプログラミングの基礎から応用まで体系的に習得できるよう設計されています。

本章で学んだGUIプログラミングの基礎を活用して、第18章ではより高度なイベント処理について学習します。



<!-- Merged from chapter17a-basic-components.md -->

## Part A: 基本コンポーネントとレイアウト

### 17.1 はじめてのGUIプログラム

#### Hello Swing - ウィンドウを表示してみよう

まずはもっとも基本的なGUIプログラムから始めましょう。以下は、単純なウィンドウを表示するプログラムです：

```java
import javax.swing.JFrame;

public class HelloSwing {
    public static void main(String[] args) {
        // JFrameのインスタンスを作成
        JFrame frame = new JFrame("はじめてのSwingアプリケーション");
        
        // ウィンドウのサイズを設定
        frame.setSize(400, 300);
        
        // ウィンドウを閉じたときの動作を設定
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // ウィンドウを画面中央に配置
        frame.setLocationRelativeTo(null);
        
        // ウィンドウを表示
        frame.setVisible(true);
    }
}
```

このプログラムの各行を詳しく見ていきましょう：

1. **JFrameクラス**: Swingでウィンドウを表すクラスです。すべてのGUIアプリケーションの基礎となります。
2. **setSize()**: ウィンドウの幅と高さをピクセル単位で設定します。
3. **setDefaultCloseOperation()**: ウィンドウの×ボタンを押したときの動作を指定します。
4. **setLocationRelativeTo(null)**: nullを指定すると、ウィンドウが画面中央に配置されます。
5. **setVisible(true)**: ウィンドウを表示します。これを呼ばないと画面に表示されません。

#### ウィンドウの基本設定

`JFrame`オブジェクトのメソッドを呼び出すことで、ウィンドウのさまざまな設定が可能です：

```java
import javax.swing.JFrame;

public class WindowSettings {
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        
        // タイトルバーに表示されるテキストを設定
        frame.setTitle("カスタマイズされたウィンドウ");
        
        // ウィンドウサイズを設定（幅500px、高さ400px）
        frame.setSize(500, 400);
        
        // ウィンドウのリサイズを禁止
        frame.setResizable(false);
        
        // ウィンドウの初期位置を指定（画面左上から100px右、50px下）
        frame.setLocation(100, 50);
        
        // ウィンドウを閉じたときにプログラムを終了
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // ウィンドウを表示
        frame.setVisible(true);
    }
}
```

### 17.2 基本的なコンポーネントの配置

#### JLabelで文字を表示する

ウィンドウに文字を表示してみましょう：

```java
import javax.swing.JFrame;
import javax.swing.JLabel;

public class HelloLabel {
    public static void main(String[] args) {
        JFrame frame = new JFrame("ラベルの表示");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // JLabelを作成して文字を設定
        JLabel label = new JLabel("Hello, Swing!");
        label.setHorizontalAlignment(JLabel.CENTER);  // 中央揃え
        
        // フレームにラベルを追加
        frame.add(label);
        
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
```

#### 複数のコンポーネントを配置する

複数のコンポーネントを配置するには、レイアウトマネージャーを使います：

```java
import javax.swing.*;
import java.awt.*;

public class MultipleComponents {
    public static void main(String[] args) {
        JFrame frame = new JFrame("複数のコンポーネント");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // レイアウトマネージャーをBorderLayoutに設定
        frame.setLayout(new BorderLayout());
        
        // 各領域にコンポーネントを配置
        frame.add(new JLabel("北", JLabel.CENTER), BorderLayout.NORTH);
        frame.add(new JLabel("南", JLabel.CENTER), BorderLayout.SOUTH);
        frame.add(new JLabel("東", JLabel.CENTER), BorderLayout.EAST);
        frame.add(new JLabel("西", JLabel.CENTER), BorderLayout.WEST);
        frame.add(new JLabel("中央", JLabel.CENTER), BorderLayout.CENTER);
        
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
```

#### 基本コンポーネントの詳細な使用例

#### JLabelとアイコンの活用

```java
import javax.swing.*;
import java.awt.*;

public class AdvancedLabel {
    public static void main(String[] args) {
        JFrame frame = new JFrame("高度なJLabel");
        frame.setSize(400, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout());
        
        // テキストのみのラベル
        JLabel textLabel = new JLabel("通常のテキスト");
        
        // HTMLを使用したリッチテキスト
        JLabel htmlLabel = new JLabel(
            "<html><b>太字</b>と<i>斜体</i>と<font color='red'>赤色</font></html>");
        
        // 複数行テキスト
        JLabel multiLineLabel = new JLabel("<html>複数行の<br>テキストを<br>表示</html>");
        
        frame.add(textLabel);
        frame.add(htmlLabel);
        frame.add(multiLineLabel);
        
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
```

#### JButtonの高度な使用

```java
import javax.swing.*;
import java.awt.*;

public class AdvancedButton {
    public static void main(String[] args) {
        JFrame frame = new JFrame("高度なJButton");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout());
        
        // 通常のボタン
        JButton normalButton = new JButton("通常のボタン");
        
        // 色付きボタン
        JButton coloredButton = new JButton("色付きボタン");
        coloredButton.setBackground(Color.BLUE);
        coloredButton.setForeground(Color.WHITE);
        
        // 大きなボタン
        JButton largeButton = new JButton("大きなボタン");
        largeButton.setPreferredSize(new Dimension(150, 50));
        
        // 無効化されたボタン
        JButton disabledButton = new JButton("無効なボタン");
        disabledButton.setEnabled(false);
        
        frame.add(normalButton);
        frame.add(coloredButton);
        frame.add(largeButton);
        frame.add(disabledButton);
        
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
```

#### テキスト入力コンポーネント

```java
import javax.swing.*;
import java.awt.*;

public class TextInputComponents {
    public static void main(String[] args) {
        JFrame frame = new JFrame("テキスト入力コンポーネント");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(5, 2, 5, 5));
        
        // 一行テキストフィールド
        frame.add(new JLabel("一行テキスト:"));
        JTextField textField = new JTextField("初期テキスト");
        frame.add(textField);
        
        // パスワードフィールド
        frame.add(new JLabel("パスワード:"));
        JPasswordField passwordField = new JPasswordField();
        frame.add(passwordField);
        
        // 複数行テキストエリア
        frame.add(new JLabel("複数行テキスト:"));
        JTextArea textArea = new JTextArea(3, 20);
        textArea.setLineWrap(true); // 自動改行
        textArea.setWrapStyleWord(true); // 単語境界で改行
        JScrollPane scrollPane = new JScrollPane(textArea);
        frame.add(scrollPane);
        
        // 数値スピナー
        frame.add(new JLabel("数値:"));
        JSpinner spinner = new JSpinner(
            new SpinnerNumberModel(0, 0, 100, 1));
        frame.add(spinner);
        
        // スライダー
        frame.add(new JLabel("スライダー:"));
        JSlider slider = new JSlider(0, 100, 50);
        slider.setMajorTickSpacing(25);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        frame.add(slider);
        
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
```

#### 選択コンポーネント

```java
import javax.swing.*;
import java.awt.*;

public class SelectionComponents {
    public static void main(String[] args) {
        JFrame frame = new JFrame("選択コンポーネント");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(4, 2, 5, 5));
        
        // チェックボックス
        frame.add(new JLabel("チェックボックス:"));
        JPanel checkBoxPanel = new JPanel(new FlowLayout());
        checkBoxPanel.add(new JCheckBox("オプション1"));
        checkBoxPanel.add(new JCheckBox("オプション2", true)); // 初期選択
        frame.add(checkBoxPanel);
        
        // ラジオボタン
        frame.add(new JLabel("ラジオボタン:"));
        JPanel radioPanel = new JPanel(new FlowLayout());
        ButtonGroup group = new ButtonGroup();
        JRadioButton radio1 = new JRadioButton("選択肢A");
        JRadioButton radio2 = new JRadioButton("選択肢B", true);
        group.add(radio1);
        group.add(radio2);
        radioPanel.add(radio1);
        radioPanel.add(radio2);
        frame.add(radioPanel);
        
        // コンボボックス
        frame.add(new JLabel("コンボボックス:"));
        String[] items = {"項目1", "項目2", "項目3"};
        JComboBox<String> comboBox = new JComboBox<>(items);
        frame.add(comboBox);
        
        // リスト
        frame.add(new JLabel("リスト:"));
        String[] listItems = {
            "アイテム1", "アイテム2", "アイテム3", "アイテム4", "アイテム5"
        };
        JList<String> list = new JList<>(listItems);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane listScrollPane = new JScrollPane(list);
        frame.add(listScrollPane);
        
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
```

### 17.3 レイアウトマネージャーの基礎

#### BorderLayout - 5つの領域に配置

`BorderLayout`は、ウィンドウを5つの領域（北、南、東、西、中央）に分割してコンポーネントを配置します：

```java
import javax.swing.*;
import java.awt.*;

public class BorderLayoutExample {
    public static void main(String[] args) {
        JFrame frame = new JFrame("BorderLayoutの例");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // BorderLayoutはJFrameのデフォルトレイアウトマネージャー
        frame.setLayout(new BorderLayout());
        
        // 各領域にボタンを配置
        frame.add(new JButton("北 (NORTH)"), BorderLayout.NORTH);
        frame.add(new JButton("南 (SOUTH)"), BorderLayout.SOUTH);
        frame.add(new JButton("東 (EAST)"), BorderLayout.EAST);
        frame.add(new JButton("西 (WEST)"), BorderLayout.WEST);
        frame.add(new JButton("中央 (CENTER)"), BorderLayout.CENTER);
        
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
```

**BorderLayoutの特徴：**
- **NORTH**と**SOUTH**：画面の幅いっぱいに広がり、高さは推奨サイズ
- **EAST**と**WEST**：残りの高さいっぱいに広がり、幅は推奨サイズ
- **CENTER**：残りの領域すべてを占める

#### GridLayout - 格子状に配置

`GridLayout`は、コンポーネントを格子状（行と列）に配置します：

```java
import javax.swing.*;
import java.awt.*;

public class GridLayoutExample {
    public static void main(String[] args) {
        JFrame frame = new JFrame("GridLayoutの例");
        frame.setSize(300, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // 3行2列のグリッドレイアウト
        // 3行2列のグリッドレイアウト（行、列、水平間隔、垂直間隔）
        frame.setLayout(new GridLayout(3, 2, 5, 5));
        
        // ボタンを順番に配置
        for (int i = 1; i <= 6; i++) {
            frame.add(new JButton("ボタン " + i));
        }
        
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
```

#### FlowLayout - 流れるような配置

`FlowLayout`は、コンポーネントを左から右へ、行がいっぱいになったら次の行へと配置します：

```java
import javax.swing.*;
import java.awt.*;

public class FlowLayoutExample {
    public static void main(String[] args) {
        JFrame frame = new JFrame("FlowLayoutの例");
        frame.setSize(400, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // FlowLayoutを設定（中央揃え、水平間隔10px、垂直間隔5px）
        frame.setLayout(new FlowLayout(
            FlowLayout.CENTER, 10, 5));
        
        // さまざまなサイズのボタンを追加
        frame.add(new JButton("短い"));
        frame.add(new JButton("もう少し長いボタン"));
        frame.add(new JButton("OK"));
        frame.add(new JButton("キャンセル"));
        frame.add(new JButton("ヘルプ"));
        frame.add(new JButton("設定"));
        
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
```

### 17.4 パネルを使った複雑なレイアウト

#### JPanelで階層的なレイアウトを構築

複雑なレイアウトを実現するには、`JPanel`を使って階層的にコンポーネントを組み合わせます：

```java
import javax.swing.*;
import java.awt.*;

public class ComplexLayoutExample {
    public static void main(String[] args) {
        JFrame frame = new JFrame("複雑なレイアウトの例");
        frame.setSize(500, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        
        // ツールバー（上部）
        JPanel toolbarPanel = new JPanel(
            new FlowLayout(FlowLayout.LEFT));
        toolbarPanel.add(new JButton("新規"));
        toolbarPanel.add(new JButton("開く"));
        toolbarPanel.add(new JButton("保存"));
        toolbarPanel.add(new JSeparator(SwingConstants.VERTICAL));
        toolbarPanel.add(new JButton("切り取り"));
        toolbarPanel.add(new JButton("コピー"));
        toolbarPanel.add(new JButton("貼り付け"));
        frame.add(toolbarPanel, BorderLayout.NORTH);
        
        // メインエリア（中央）
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // 左側のサイドバー
        JPanel sidebarPanel = new JPanel(new BorderLayout());
        sidebarPanel.setBorder(
            BorderFactory.createTitledBorder("ファイル一覧"));
        String[] files = {"ファイル1.txt", "ファイル2.txt", "ファイル3.txt"};
        JList<String> fileList = new JList<>(files);
        sidebarPanel.add(new JScrollPane(fileList), BorderLayout.CENTER);
        sidebarPanel.setPreferredSize(new Dimension(150, 0));
        mainPanel.add(sidebarPanel, BorderLayout.WEST);
        
        // 中央のテキストエリア
        JTextArea textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane textScrollPane = new JScrollPane(textArea);
        textScrollPane.setBorder(
            BorderFactory.createTitledBorder("エディタ"));
        mainPanel.add(textScrollPane, BorderLayout.CENTER);
        
        frame.add(mainPanel, BorderLayout.CENTER);
        
        // ステータスバー（下部）
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(
            BorderFactory.createLoweredBevelBorder());
        statusPanel.add(new JLabel("準備完了"), BorderLayout.WEST);
        statusPanel.add(new JLabel("行: 1, 列: 1"), BorderLayout.EAST);
        frame.add(statusPanel, BorderLayout.SOUTH);
        
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
```

#### レイアウトの組み合わせによる画面構築

レイアウトマネージャーを組み合わせることで、より柔軟な画面設計が可能です：

```java
import javax.swing.*;
import java.awt.*;

public class CombinedLayoutExample {
    public static void main(String[] args) {
        JFrame frame = new JFrame("レイアウト組み合わせの例");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        
        // 上部：メニューバーとツールバー
        JPanel topPanel = new JPanel(new BorderLayout());
        
        // メニューバー
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("ファイル");
        fileMenu.add(new JMenuItem("新規"));
        fileMenu.add(new JMenuItem("開く"));
        fileMenu.add(new JMenuItem("保存"));
        menuBar.add(fileMenu);
        
        JMenu editMenu = new JMenu("編集");
        editMenu.add(new JMenuItem("切り取り"));
        editMenu.add(new JMenuItem("コピー"));
        editMenu.add(new JMenuItem("貼り付け"));
        menuBar.add(editMenu);
        
        frame.setJMenuBar(menuBar);
        
        // ツールバー
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolbar.add(new JButton("新規"));
        toolbar.add(new JButton("開く"));
        toolbar.add(new JButton("保存"));
        topPanel.add(toolbar, BorderLayout.NORTH);
        
        frame.add(topPanel, BorderLayout.NORTH);
        
        // 中央：分割パネル
        JSplitPane splitPane = new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(200);
        
        // 左側パネル
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(
            BorderFactory.createTitledBorder("プロジェクト"));
        
        // ツリー構造
        JTree tree = new JTree();
        leftPanel.add(new JScrollPane(tree), BorderLayout.CENTER);
        
        // 左下のボタンパネル
        JPanel leftButtonPanel = new JPanel(new FlowLayout());
        leftButtonPanel.add(new JButton("追加"));
        leftButtonPanel.add(new JButton("削除"));
        leftPanel.add(leftButtonPanel, BorderLayout.SOUTH);
        
        splitPane.setLeftComponent(leftPanel);
        
        // 右側パネル（タブ付き）
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // エディタタブ
        JTextArea editor = new JTextArea();
        editor.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        tabbedPane.addTab("エディタ", new JScrollPane(editor));
        
        // コンソールタブ
        JTextArea console = new JTextArea();
        console.setBackground(Color.BLACK);
        console.setForeground(Color.WHITE);
        console.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        tabbedPane.addTab("コンソール", new JScrollPane(console));
        
        splitPane.setRightComponent(tabbedPane);
        
        frame.add(splitPane, BorderLayout.CENTER);
        
        // 下部：ステータスバー
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(
            BorderFactory.createLoweredBevelBorder());
        statusBar.add(new JLabel("準備完了"), BorderLayout.WEST);
        
        JPanel rightStatus = new JPanel(
            new FlowLayout(FlowLayout.RIGHT));
        rightStatus.add(new JLabel("行: 1"));
        rightStatus.add(new JLabel("列: 1"));
        rightStatus.add(new JLabel("エンコーディング: UTF-8"));
        statusBar.add(rightStatus, BorderLayout.EAST);
        
        frame.add(statusBar, BorderLayout.SOUTH);
        
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
```

#### まとめ

本パートでは、Swingを使った基本的なGUIプログラミングについて学習しました：

1. **JFrame**を使ったウィンドウの作成と基本設定
2. **JLabel**、**JButton**、**JTextField**などの基本コンポーネントの使用方法
3. **BorderLayout**、**GridLayout**、**FlowLayout**などのレイアウトマネージャーの特徴
4. **JPanel**を使った階層的なレイアウトの構築

これらの基礎知識により、シンプルなGUIアプリケーションの画面を構築できるようになりました。次のパートでは、これらのコンポーネントにユーザーの操作に応答する機能を追加する方法を学習します。




<!-- Merged from chapter17b-event-handling-intro.md -->

## Part B: イベント処理入門

### 17.5 イベント駆動プログラミングの概念

#### イベント駆動とは何か？

これまでのコンソールプログラムは、上から下へ順番に実行される「手続き型」でした。しかし、GUIアプリケーションはユーザーがいつ、どのボタンを押すかわからないため、「ユーザーの操作（**イベント**）をきっかけに、特定の処理（**リスナー**）が動く」という**イベント駆動（Event-Driven）**というモデルを採用しています。

イベント駆動は、「**何かが起きたら、これを実行する**」というルールの集合でプログラムを構築する考え方です。

#### イベント駆動の3要素

Swingのイベント処理は、以下の3つの要素で構成されます：

1. **イベントソース**: イベントを発生させるコンポーネント（例: `JButton`, `JTextField`）
2. **イベントオブジェクト**: 発生したイベントの詳細情報を持つオブジェクト（例: `ActionEvent`はボタンがクリックされたことを示す）
3. **イベントリスナー**: イベントを監視し、イベントが発生したときに実行される処理を記述したオブジェクト（例: `ActionListener`）

処理の流れは以下の通りです：

```
ユーザー操作 → イベントソース → イベントオブジェクト生成 → リスナーに通知 → 処理実行
```

### 17.6 ボタンクリックに反応するプログラム

#### 基本的なボタンクリックイベント

もっとも基本的なイベント処理である、ボタンクリックの実装を見てみましょう：

```java
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;

public class ButtonEventExample {
    public static void main(String[] args) {
        JFrame frame = new JFrame("ボタンイベントの例");
        frame.setSize(300, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout());

        JButton button = new JButton("クリックしてね");

        // イベントリスナーを作成し、ボタンに登録
        // 匿名クラスを使用
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // ボタンがクリックされたときの処理
                System.out.println("ボタンがクリックされました！");
            }
        });

        frame.add(button);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
```

#### リスナーの登録

イベントソースに「このイベントが起きたら、このリスナーを呼び出してください」とお願いすることを「リスナーの登録」と呼びます。`addXXXListener`という形式のメソッド（例: `addActionListener`）を使用します。

#### テキストフィールドとの連携

ボタンが押されたタイミングで、`JTextField`に入力されているテキストを取得してみましょう：

```java
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class TextFieldEventExample {
    public static void main(String[] args) {
        JFrame frame = new JFrame("テキストフィールドイベント");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout());

        JTextField textField = new JTextField(15);
        JButton button = new JButton("表示");
        JLabel label = new JLabel("ここに結果が表示されます");

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // テキストフィールドから入力された文字列を取得
                String inputText = textField.getText();
                // ラベルにその文字列を設定
                label.setText("こんにちは、" + inputText + "さん！");
            }
        });

        frame.add(new JLabel("名前を入力:"));
        frame.add(textField);
        frame.add(button);
        frame.add(label);
        
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
```

### 17.7 ラムダ式による簡潔な記述

#### ラムダ式の基本

ラムダ式を使うと、匿名クラスよりも簡潔にイベントリスナーを記述できます。`ActionListener`のように、実装すべきメソッドが1つだけのインターフェイス（関数型インターフェイス）に対して使用できます。

先ほどのボタンクリックの例をラムダ式で書き換えてみましょう：

```java
// 匿名クラスのバージョン
button.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("ボタンがクリックされました！");
    }
});

// ラムダ式のバージョン
button.addActionListener(e -> {
    System.out.println("ボタンがクリックされました！");
});

// 処理が1行の場合はさらに簡潔に
button.addActionListener(e -> System.out.println("ボタンがクリックされました！"));
```

#### ラムダ式を使った実用例

```java
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import javax.swing.*;

public class LambdaEventExample {
    public static void main(String[] args) {
        JFrame frame = new JFrame("ラムダ式イベント処理");
        frame.setSize(300, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(3, 1));

        JTextField inputField = new JTextField();
        JLabel resultLabel = new JLabel("結果が表示されます", JLabel.CENTER);
        JPanel buttonPanel = new JPanel();

        // 各ボタンにラムダ式でイベント処理を設定
        JButton upperButton = new JButton("大文字に変換");
        upperButton.addActionListener(e -> {
            String text = inputField.getText();
            resultLabel.setText(text.toUpperCase());
        });

        JButton lowerButton = new JButton("小文字に変換");
        lowerButton.addActionListener(e -> {
            String text = inputField.getText();
            resultLabel.setText(text.toLowerCase());
        });

        JButton clearButton = new JButton("クリア");
        clearButton.addActionListener(e -> {
            inputField.setText("");
            resultLabel.setText("結果が表示されます");
        });

        buttonPanel.add(upperButton);
        buttonPanel.add(lowerButton);
        buttonPanel.add(clearButton);

        frame.add(inputField);
        frame.add(resultLabel);
        frame.add(buttonPanel);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
```

### 17.8 カウンタアプリケーション

#### シンプルなカウンタの実装

実際に動作するアプリケーションとして、カウンタを作成してみましょう：

```java
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.*;

public class CounterApplication {
    private int count = 0;
    private JLabel countLabel;
    
    public CounterApplication() {
        // フレームの初期設定
        JFrame frame = new JFrame("カウンタアプリケーション");
        frame.setSize(300, 150);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        
        // カウント表示ラベル
        countLabel = new JLabel(String.valueOf(count), JLabel.CENTER);
        countLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        frame.add(countLabel, BorderLayout.CENTER);
        
        // ボタンパネル
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        // 増加ボタン
        JButton incrementButton = new JButton("+1");
        incrementButton.addActionListener(e -> {
            count++;
            updateDisplay();
        });
        
        // 減少ボタン
        JButton decrementButton = new JButton("-1");
        decrementButton.addActionListener(e -> {
            count--;
            updateDisplay();
        });
        
        // リセットボタン
        JButton resetButton = new JButton("リセット");
        resetButton.addActionListener(e -> {
            count = 0;
            updateDisplay();
        });
        
        buttonPanel.add(decrementButton);
        buttonPanel.add(resetButton);
        buttonPanel.add(incrementButton);
        
        frame.add(buttonPanel, BorderLayout.SOUTH);
        
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    private void updateDisplay() {
        countLabel.setText(String.valueOf(count));
    }
    
    public static void main(String[] args) {
        // Event Dispatch Thread上でGUIを作成
        SwingUtilities.invokeLater(() -> new CounterApplication());
    }
}
```

#### 選択状態に応じた処理

`JCheckBox`や`JRadioButton`の選択状態を取得するイベント処理も実装してみましょう：

```java
import java.awt.GridLayout;
import javax.swing.*;

public class SelectionEventExample {
    public static void main(String[] args) {
        JFrame frame = new JFrame("選択イベントの例");
        frame.setSize(400, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(4, 1));

        // チェックボックス
        JCheckBox agreeCheckBox = new JCheckBox("利用規約に同意する");
        JLabel agreeLabel = new JLabel("同意状態: 未同意");
        
        agreeCheckBox.addActionListener(e -> {
            if (agreeCheckBox.isSelected()) {
                agreeLabel.setText("同意状態: 同意済み");
            } else {
                agreeLabel.setText("同意状態: 未同意");
            }
        });

        // ラジオボタン
        JPanel radioPanel = new JPanel(new FlowLayout());
        ButtonGroup group = new ButtonGroup();
        JRadioButton beginnerRadio = new JRadioButton("初心者", true);
        JRadioButton intermediateRadio = new JRadioButton("中級者");
        JRadioButton advancedRadio = new JRadioButton("上級者");
        
        group.add(beginnerRadio);
        group.add(intermediateRadio);
        group.add(advancedRadio);
        
        JLabel levelLabel = new JLabel("レベル: 初心者");
        
        // 各ラジオボタンにリスナーを設定
        beginnerRadio.addActionListener(e -> levelLabel.setText("レベル: 初心者"));
        intermediateRadio.addActionListener(e -> levelLabel.setText("レベル: 中級者"));
        advancedRadio.addActionListener(e -> levelLabel.setText("レベル: 上級者"));
        
        radioPanel.add(beginnerRadio);
        radioPanel.add(intermediateRadio);
        radioPanel.add(advancedRadio);

        frame.add(agreeCheckBox);
        frame.add(agreeLabel);
        frame.add(radioPanel);
        frame.add(levelLabel);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
```

#### フォーム入力アプリケーション

複数のコンポーネントを組み合わせた、より実用的な例を作成してみましょう：

```java
import java.awt.*;
import javax.swing.*;

public class FormApplication {
    public static void main(String[] args) {
        JFrame frame = new JFrame("フォーム入力アプリケーション");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // 入力パネル
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("個人情報入力"));

        // 名前入力
        inputPanel.add(new JLabel("名前:"));
        JTextField nameField = new JTextField();
        inputPanel.add(nameField);

        // 年齢入力
        inputPanel.add(new JLabel("年齢:"));
        JSpinner ageSpinner = new JSpinner(new SpinnerNumberModel(20, 0, 120, 1));
        inputPanel.add(ageSpinner);

        // 性別選択
        inputPanel.add(new JLabel("性別:"));
        JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ButtonGroup genderGroup = new ButtonGroup();
        JRadioButton maleRadio = new JRadioButton("男性", true);
        JRadioButton femaleRadio = new JRadioButton("女性");
        genderGroup.add(maleRadio);
        genderGroup.add(femaleRadio);
        genderPanel.add(maleRadio);
        genderPanel.add(femaleRadio);
        inputPanel.add(genderPanel);

        // 趣味選択
        inputPanel.add(new JLabel("趣味:"));
        String[] hobbies = {"読書", "映画鑑賞", "スポーツ", "音楽", "料理"};
        JComboBox<String> hobbyCombo = new JComboBox<>(hobbies);
        inputPanel.add(hobbyCombo);

        // メルマガ購読
        JCheckBox newsletterCheck = new JCheckBox("メールマガジンを購読する");
        inputPanel.add(new JLabel()); // 空のラベル
        inputPanel.add(newsletterCheck);

        frame.add(inputPanel, BorderLayout.CENTER);

        // 結果表示エリア
        JTextArea resultArea = new JTextArea(8, 30);
        resultArea.setEditable(false);
        resultArea.setBorder(BorderFactory.createTitledBorder("入力結果"));
        JScrollPane scrollPane = new JScrollPane(resultArea);
        frame.add(scrollPane, BorderLayout.SOUTH);

        // ボタンパネル
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton submitButton = new JButton("送信");
        JButton clearButton = new JButton("クリア");

        submitButton.addActionListener(e -> {
            StringBuilder result = new StringBuilder();
            result.append("=== 入力内容 ===\n");
            result.append("名前: ").append(nameField.getText()).append("\n");
            result.append("年齢: ").append(ageSpinner.getValue()).append("歳\n");
            result.append("性別: ").append(maleRadio.isSelected() ? "男性" : "女性").append("\n");
            result.append("趣味: ").append(hobbyCombo.getSelectedItem()).append("\n");
            result.append("メルマガ: ").append(newsletterCheck.isSelected() ? "購読する" : "購読しない").append("\n");
            result.append("==================\n");
            
            resultArea.append(result.toString());
        });

        clearButton.addActionListener(e -> {
            nameField.setText("");
            ageSpinner.setValue(20);
            maleRadio.setSelected(true);
            hobbyCombo.setSelectedIndex(0);
            newsletterCheck.setSelected(false);
            resultArea.setText("");
        });

        buttonPanel.add(submitButton);
        buttonPanel.add(clearButton);

        frame.add(buttonPanel, BorderLayout.NORTH);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
```

#### まとめ

本パートでは、GUIアプリケーションの核心であるイベント処理について学習しました：

1. **イベント駆動プログラミング**の基本概念
2. **イベントソース**、**イベントオブジェクト**、**イベントリスナー**の3要素
3. **ActionListener**を使ったボタンクリックイベントの実装
4. **ラムダ式**による簡潔なイベント処理の記述
5. **複数のコンポーネント**を組み合わせた実用的なアプリケーション

これらの知識により、ユーザーの操作に応答するインタラクティブなGUIアプリケーションを作成できるようになりました。次のパートでは、より高度なイベント処理とスレッド安全性について学習します。



<!-- Merged from chapter17c-edt-thread-safety.md -->

## Part C: EDT（Event Dispatch Thread）とスレッド安全性

### 17.9 Event Dispatch Thread（EDT）の理解と実践

#### 17.9.1 EDTとは何か

**Event Dispatch Thread（EDT）**は、Swingアプリケーションにおいてすべてのイベント処理とUI更新を担当する専用のスレッドです。Swingのコンポーネントは**スレッドセーフではない**ため、複数のスレッドから同時にアクセスされると、予期しない動作やクラッシュを引き起こす可能性があります。

##### EDTの重要性

SwingのGUIコンポーネントへのアクセス（表示の更新、プロパティの変更、イベント処理など）は、原則として**EDT上でのみ**行う必要があります。これにより以下の利点があります：

1. **スレッド安全性の確保**: 複数のスレッドからの同時アクセスを防ぐ
2. **一貫性のある描画**: UIの更新が同期的に行われる
3. **デッドロックの回避**: スレッド間の競合状態を防ぐ

##### 正しいEDTの使用方法

```java
import javax.swing.*;

public class EDTBasicExample {
    public static void main(String[] args) {
        // 間違った方法：メインスレッドから直接GUI作成
        // JFrame frame = new JFrame("Bad Example");
        
        // 正しい方法：EDT上でGUI作成
        SwingUtilities.invokeLater(() -> {
            createAndShowGUI();
        });
    }
    
    private static void createAndShowGUI() {
        JFrame frame = new JFrame("正しいEDTの使用例");
        frame.setSize(300, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JLabel label = new JLabel("このGUIはEDT上で作成されました", JLabel.CENTER);
        frame.add(label);
        
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
```

#### 17.9.2 EDTの役割と仕組み

##### 1. イベントキューの管理

EDTは内部的に**イベントキュー**を管理しており、すべてのユーザー操作（マウスクリック、キー入力など）とプログラム的なイベントを順次処理します：

```java
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EventQueueExample {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("イベントキューの例");
            frame.setSize(400, 200);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new FlowLayout());
            
            JButton button = new JButton("イベント確認");
            JLabel statusLabel = new JLabel("準備完了");
            
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // EDTの確認
                    if (SwingUtilities.isEventDispatchThread()) {
                        statusLabel.setText("正しくEDT上で実行されています");
                    } else {
                        statusLabel.setText("警告: EDT以外のスレッドで実行されています");
                    }
                    
                    // イベントキューの状況を表示
                    System.out.println("現在のスレッド: " + Thread.currentThread().getName());
                    System.out.println("EDT上での実行: " + SwingUtilities.isEventDispatchThread());
                }
            });
            
            frame.add(button);
            frame.add(statusLabel);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
```

##### 2. SwingWorkerによる非同期処理

長時間かかる処理をEDT上で実行すると、UIがフリーズしてしまいます。このような場合には**SwingWorker**を使用して背景スレッドで処理を実行します：

```java
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SwingWorkerExample {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("SwingWorkerの例");
            frame.setSize(400, 200);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());
            
            JProgressBar progressBar = new JProgressBar(0, 100);
            JButton startButton = new JButton("長時間処理を開始");
            JLabel statusLabel = new JLabel("準備完了", JLabel.CENTER);
            
            startButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    startButton.setEnabled(false);
                    progressBar.setValue(0);
                    statusLabel.setText("処理中...");
                    
                    // SwingWorkerで背景処理を実行
                    SwingWorker<Void, Integer> worker = new SwingWorker<Void, Integer>() {
                        @Override
                        protected Void doInBackground() throws Exception {
                            // 重い処理のシミュレーション
                            for (int i = 0; i <= 100; i++) {
                                Thread.sleep(50); // 50msの遅延
                                publish(i); // 進捗を報告
                            }
                            return null;
                        }
                        
                        @Override
                        protected void process(java.util.List<Integer> chunks) {
                            // EDT上で進捗バーを更新
                            for (Integer progress : chunks) {
                                progressBar.setValue(progress);
                            }
                        }
                        
                        @Override
                        protected void done() {
                            // EDT上で完了処理
                            startButton.setEnabled(true);
                            statusLabel.setText("処理完了");
                        }
                    };
                    
                    worker.execute();
                }
            });
            
            frame.add(progressBar, BorderLayout.NORTH);
            frame.add(statusLabel, BorderLayout.CENTER);
            frame.add(startButton, BorderLayout.SOUTH);
            
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
```

#### 17.9.3 EDT関連のユーティリティメソッド

SwingUtilitiesクラスには、EDTを操作するための便利なメソッドが用意されています：

```java
import javax.swing.*;
import java.awt.*;

public class EDTUtilityExample {
    public static void main(String[] args) {
        // 1. SwingUtilities.invokeLater() - EDT上で後で実行
        SwingUtilities.invokeLater(() -> {
            System.out.println("invokeLater: " + Thread.currentThread().getName());
            createGUI();
        });
        
        // 2. SwingUtilities.invokeAndWait() - EDT上で実行し、完了まで待機
        try {
            SwingUtilities.invokeAndWait(() -> {
                System.out.println("invokeAndWait: " + Thread.currentThread().getName());
                JOptionPane.showMessageDialog(null, "EDT上で実行され、完了まで待機しました");
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // 3. SwingUtilities.isEventDispatchThread() - 現在のスレッドがEDTかチェック
        System.out.println("メインスレッドはEDT？: " + SwingUtilities.isEventDispatchThread());
    }
    
    private static void createGUI() {
        JFrame frame = new JFrame("EDT ユーティリティの例");
        frame.setSize(300, 150);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JButton checkThreadButton = new JButton("スレッドを確認");
        checkThreadButton.addActionListener(e -> {
            String threadInfo = String.format(
                "現在のスレッド: %s%nEDT上での実行: %s",
                Thread.currentThread().getName(),
                SwingUtilities.isEventDispatchThread()
            );
            JOptionPane.showMessageDialog(frame, threadInfo);
        });
        
        frame.add(checkThreadButton);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
```

#### 17.9.4 EDTに関するベストプラクティス

##### 1. GUIの初期化

アプリケーションの起動時は、必ずEDT上でGUIを初期化します：

```java
public class ProperGUIInitialization {
    public static void main(String[] args) {
        // システムのLook&Feelを設定（EDT外でも可）
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // GUIの作成と表示はEDT上で
        SwingUtilities.invokeLater(() -> {
            new MyApplication().createAndShowGUI();
        });
    }
}

class MyApplication {
    public void createAndShowGUI() {
        JFrame frame = new JFrame("適切な初期化の例");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // コンポーネントの作成と設定
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel("適切に初期化されたGUI", JLabel.CENTER));
        
        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
```

##### 2. 重い処理の扱い

EDT上では重い処理を避け、必要な場合はSwingWorkerを使用します：

```java
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class HeavyProcessingExample {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("重い処理の例");
            frame.setSize(400, 200);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());
            
            JTextArea textArea = new JTextArea();
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            
            JButton processButton = new JButton("データ処理開始");
            JProgressBar progressBar = new JProgressBar();
            progressBar.setStringPainted(true);
            
            processButton.addActionListener(e -> {
                processButton.setEnabled(false);
                textArea.setText("");
                
                SwingWorker<String, String> worker = new SwingWorker<String, String>() {
                    @Override
                    protected String doInBackground() throws Exception {
                        StringBuilder result = new StringBuilder();
                        
                        // 重い処理のシミュレーション
                        for (int i = 1; i <= 10; i++) {
                            Thread.sleep(500); // 0.5秒の処理
                            
                            String message = "処理ステップ " + i + " 完了\n";
                            publish(message);
                            setProgress(i * 10);
                            result.append(message);
                        }
                        
                        return result.toString();
                    }
                    
                    @Override
                    protected void process(List<String> chunks) {
                        for (String chunk : chunks) {
                            textArea.append(chunk);
                        }
                    }
                    
                    @Override
                    protected void done() {
                        processButton.setEnabled(true);
                        progressBar.setValue(0);
                        try {
                            textArea.append("\n=== 処理完了 ===\n" + get());
                        } catch (Exception ex) {
                            textArea.append("エラーが発生しました: " + ex.getMessage());
                        }
                    }
                };
                
                // 進捗バーとWorkerを連携
                worker.addPropertyChangeListener(evt -> {
                    if ("progress".equals(evt.getPropertyName())) {
                        int progress = (Integer) evt.getNewValue();
                        progressBar.setValue(progress);
                        progressBar.setString(progress + "%");
                    }
                });
                
                worker.execute();
            });
            
            frame.add(scrollPane, BorderLayout.CENTER);
            frame.add(processButton, BorderLayout.NORTH);
            frame.add(progressBar, BorderLayout.SOUTH);
            
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
```

##### 3. 定期的な処理

定期的なUI更新が必要な場合は、`javax.swing.Timer`を使用します：

```java
import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TimerExample {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("タイマーの例");
            frame.setSize(300, 150);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());
            
            JLabel timeLabel = new JLabel("", JLabel.CENTER);
            timeLabel.setFont(new Font("Monospaced", Font.BOLD, 18));
            
            JButton toggleButton = new JButton("開始");
            
            // javax.swing.Timerを使用（EDT上で実行される）
            Timer timer = new Timer(1000, e -> {
                // 定期的にEDT上で実行される処理
                LocalTime now = LocalTime.now();
                String timeText = now.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                timeLabel.setText(timeText);
            });
            
            toggleButton.addActionListener(e -> {
                if (timer.isRunning()) {
                    timer.stop();
                    toggleButton.setText("開始");
                } else {
                    timer.start();
                    toggleButton.setText("停止");
                }
            });
            
            frame.add(timeLabel, BorderLayout.CENTER);
            frame.add(toggleButton, BorderLayout.SOUTH);
            
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
```

#### 17.9.5 デッドロックと競合状態の回避

```java
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ThreadSafetyExample extends JFrame {
    private volatile boolean running = false;
    private JLabel statusLabel;
    private JButton startButton;
    private JButton stopButton;
    
    public ThreadSafetyExample() {
        setTitle("スレッドセーフティの例");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        statusLabel = new JLabel("停止中", JLabel.CENTER);
        startButton = new JButton("開始");
        stopButton = new JButton("停止");
        stopButton.setEnabled(false);
        
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startProcessing();
            }
        });
        
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopProcessing();
            }
        });
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);
        
        add(statusLabel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(null);
    }
    
    private void startProcessing() {
        running = true;
        startButton.setEnabled(false);
        stopButton.setEnabled(true);
        
        // バックグラウンド処理開始
        new Thread(() -> {
            int counter = 0;
            while (running) {
                try {
                    Thread.sleep(100);
                    final int currentCount = ++counter;
                    
                    // EDT上でUI更新
                    SwingUtilities.invokeLater(() -> {
                        statusLabel.setText("処理中... " + currentCount);
                    });
                } catch (InterruptedException e) {
                    break;
                }
            }
            
            // 処理終了時のUI更新
            SwingUtilities.invokeLater(() -> {
                statusLabel.setText("停止中");
                startButton.setEnabled(true);
                stopButton.setEnabled(false);
            });
        }).start();
    }
    
    private void stopProcessing() {
        running = false;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ThreadSafetyExample().setVisible(true);
        });
    }
}
```

#### まとめ

EDTの理解は、安定したSwingアプリケーションを構築するために不可欠です：

1. **すべてのGUI操作はEDT上で実行する**
2. **重い処理はSwingWorkerを使用して非同期実行する**
3. **SwingUtilities.invokeLater()でEDT上での実行を保証する**
4. **SwingUtilities.isEventDispatchThread()で現在のスレッドを確認する**

これらの原則を守ることで、レスポンシブで安定したGUIアプリケーションを開発できます。次のパートでは、カスタムコンポーネントの作成方法について学習します。



<!-- Merged from chapter17d-custom-components.md -->

## Part D: カスタムコンポーネントの作成

### 17.10 カスタムコンポーネントの基本概念

標準のSwingコンポーネントでは実現できない独自の機能や見た目を持つコンポーネントが必要になることがあります。このセクションでは、カスタムコンポーネントの作成方法を学びます。

カスタムコンポーネントは、既存のSwingコンポーネントを継承して作成します。もっとも一般的なアプローチは、**JComponent**クラスを継承する方法です。JComponentはすべてのSwingコンポーネントの基底クラスで、描画、イベント処理、レイアウトなどの基本機能を提供します。

### 17.11 基本的なカスタムコンポーネント

まず、シンプルなカスタムコンポーネントから始めましょう：

```java
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RoundButton extends JComponent {
    private String text;
    private boolean pressed = false;
    private Color backgroundColor = Color.LIGHT_GRAY;
    private Color textColor = Color.BLACK;
    private Color pressedColor = Color.GRAY;
    
    public RoundButton(String text) {
        this.text = text;
        setOpaque(false); // 透明にして独自描画を有効にする
        setFocusable(true); // フォーカス可能にする
        
        // マウスイベントリスナーを追加
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                pressed = true;
                repaint(); // 再描画を要求
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                pressed = false;
                repaint();
                
                // ボタンの境界内でリリースされた場合のみアクションを実行
                if (contains(e.getPoint())) {
                    fireActionPerformed();
                }
            }
        });
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // アンチエイリアシングを有効にして滑らかな描画
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                           RenderingHints.VALUE_ANTIALIAS_ON);
        
        // ボタンの背景色を決定
        Color currentBgColor = pressed ? pressedColor : backgroundColor;
        
        // 円形のボタンを描画
        int diameter = Math.min(getWidth(), getHeight()) - 4;
        int x = (getWidth() - diameter) / 2;
        int y = (getHeight() - diameter) / 2;
        
        g2d.setColor(currentBgColor);
        g2d.fillOval(x, y, diameter, diameter);
        
        // 境界線を描画
        g2d.setColor(Color.DARK_GRAY);
        g2d.drawOval(x, y, diameter, diameter);
        
        // テキストを描画
        g2d.setColor(textColor);
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getHeight();
        int textX = (getWidth() - textWidth) / 2;
        int textY = (getHeight() - textHeight) / 2 + fm.getAscent();
        
        g2d.drawString(text, textX, textY);
        g2d.dispose();
    }
    
    @Override
    public Dimension getPreferredSize() {
        // コンポーネントの推奨サイズを返す
        FontMetrics fm = getFontMetrics(getFont());
        int textWidth = fm.stringWidth(text);
        int size = Math.max(textWidth + 20, 60); // 最小サイズ60px
        return new Dimension(size, size);
    }
    
    // ActionListenerサポートを追加
    private java.util.List<java.awt.event.ActionListener> actionListeners = 
        new java.util.ArrayList<>();
    
    public void addActionListener(java.awt.event.ActionListener listener) {
        actionListeners.add(listener);
    }
    
    public void removeActionListener(java.awt.event.ActionListener listener) {
        actionListeners.remove(listener);
    }
    
    private void fireActionPerformed() {
        java.awt.event.ActionEvent event = new java.awt.event.ActionEvent(
            this, java.awt.event.ActionEvent.ACTION_PERFORMED, text);
        
        for (java.awt.event.ActionListener listener : actionListeners) {
            listener.actionPerformed(event);
        }
    }
    
    // プロパティのアクセサメソッド
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
        repaint();
    }
    
    public Color getBackgroundColor() {
        return backgroundColor;
    }
    
    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        repaint();
    }
}
```

#### カスタムコンポーネントの使用例

```java
import javax.swing.*;
import java.awt.*;

public class CustomComponentExample extends JFrame {
    public CustomComponentExample() {
        setTitle("カスタムコンポーネントの例");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());
        
        // カスタムの丸いボタンを作成
        RoundButton roundButton1 = new RoundButton("OK");
        RoundButton roundButton2 = new RoundButton("Cancel");
        RoundButton roundButton3 = new RoundButton("Help");
        
        // ボタンの色をカスタマイズ
        roundButton1.setBackgroundColor(Color.GREEN);
        roundButton2.setBackgroundColor(Color.RED);
        roundButton3.setBackgroundColor(Color.YELLOW);
        
        // イベントリスナーを追加
        roundButton1.addActionListener(e -> 
            JOptionPane.showMessageDialog(this, "OK がクリックされました"));
        
        roundButton2.addActionListener(e -> 
            JOptionPane.showMessageDialog(this, "Cancel がクリックされました"));
        
        roundButton3.addActionListener(e -> 
            JOptionPane.showMessageDialog(this, "Help がクリックされました"));
        
        // コンポーネントを追加
        add(roundButton1);
        add(roundButton2);
        add(roundButton3);
        
        pack();
        setLocationRelativeTo(null);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CustomComponentExample().setVisible(true);
        });
    }
}
```

### 17.12 高度なカスタムコンポーネント：チャートコンポーネント

より複雑な例として、データを視覚化するチャートコンポーネントを作成してみましょう：

```java
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class SimpleBarChart extends JComponent {
    private List<ChartData> data = new ArrayList<>();
    private String title = "チャート";
    private Color[] colors = {
        Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE, 
        Color.MAGENTA, Color.CYAN, Color.PINK, Color.YELLOW
    };
    private int hoveredIndex = -1;
    
    public SimpleBarChart() {
        setOpaque(true);
        setBackground(Color.WHITE);
        
        // マウスイベントでホバー効果を実装
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int oldIndex = hoveredIndex;
                hoveredIndex = getBarIndex(e.getX(), e.getY());
                if (oldIndex != hoveredIndex) {
                    repaint();
                }
            }
        });
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                hoveredIndex = -1;
                repaint();
            }
        });
    }
    
    public void addData(String label, double value) {
        data.add(new ChartData(label, value));
        repaint();
    }
    
    public void clearData() {
        data.clear();
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (data.isEmpty()) return;
        
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                           RenderingHints.VALUE_ANTIALIAS_ON);
        
        int width = getWidth();
        int height = getHeight();
        int margin = 50;
        
        // タイトルを描画
        g2d.setFont(new Font("SansSerif", Font.BOLD, 16));
        FontMetrics titleFm = g2d.getFontMetrics();
        int titleWidth = titleFm.stringWidth(title);
        g2d.setColor(Color.BLACK);
        g2d.drawString(title, (width - titleWidth) / 2, 30);
        
        // チャート領域を計算
        int chartWidth = width - 2 * margin;
        int chartHeight = height - margin - 80;
        int chartX = margin;
        int chartY = 50;
        
        // 最大値を取得
        double maxValue = data.stream().mapToDouble(ChartData::getValue).max().orElse(1.0);
        
        // バーを描画
        int barWidth = chartWidth / data.size();
        for (int i = 0; i < data.size(); i++) {
            ChartData item = data.get(i);
            
            // バーの位置とサイズを計算
            int barX = chartX + i * barWidth + barWidth / 4;
            int barHeight = (int) ((item.getValue() / maxValue) * chartHeight);
            int barY = chartY + chartHeight - barHeight;
            int actualBarWidth = barWidth / 2;
            
            // バーの色を決定（ホバー時は明るく）
            Color barColor = colors[i % colors.length];
            if (i == hoveredIndex) {
                barColor = barColor.brighter();
            }
            
            // バーを描画
            g2d.setColor(barColor);
            g2d.fillRect(barX, barY, actualBarWidth, barHeight);
            
            // 境界線を描画
            g2d.setColor(Color.BLACK);
            g2d.drawRect(barX, barY, actualBarWidth, barHeight);
            
            // ラベルを描画
            g2d.setFont(new Font("SansSerif", Font.PLAIN, 12));
            FontMetrics labelFm = g2d.getFontMetrics();
            int labelWidth = labelFm.stringWidth(item.getLabel());
            int labelX = barX + (actualBarWidth - labelWidth) / 2;
            int labelY = chartY + chartHeight + 20;
            
            g2d.drawString(item.getLabel(), labelX, labelY);
            
            // 値を描画
            String valueStr = String.format("%.1f", item.getValue());
            int valueWidth = labelFm.stringWidth(valueStr);
            int valueX = barX + (actualBarWidth - valueWidth) / 2;
            int valueY = barY - 5;
            
            g2d.drawString(valueStr, valueX, valueY);
        }
        
        // 軸を描画
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        
        // Y軸
        g2d.drawLine(chartX, chartY, chartX, chartY + chartHeight);
        
        // X軸
        g2d.drawLine(chartX, chartY + chartHeight, 
                    chartX + chartWidth, chartY + chartHeight);
        
        g2d.dispose();
    }
    
    private int getBarIndex(int x, int y) {
        if (data.isEmpty()) return -1;
        
        int margin = 50;
        int chartWidth = getWidth() - 2 * margin;
        int barWidth = chartWidth / data.size();
        
        int index = (x - margin) / barWidth;
        return (index >= 0 && index < data.size()) ? index : -1;
    }
    
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(400, 300);
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
        repaint();
    }
    
    // 内部クラス：チャートデータ
    private static class ChartData {
        private String label;
        private double value;
        
        public ChartData(String label, double value) {
            this.label = label;
            this.value = value;
        }
        
        public String getLabel() { return label; }
        public double getValue() { return value; }
    }
}
```

#### カスタムチャートの使用例

```java
import javax.swing.*;
import java.awt.*;

public class ChartExample extends JFrame {
    public ChartExample() {
        setTitle("カスタムチャートの例");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // カスタムチャートコンポーネントを作成
        SimpleBarChart chart = new SimpleBarChart();
        chart.setTitle("売上実績");
        
        // データを追加
        chart.addData("1月", 120);
        chart.addData("2月", 150);
        chart.addData("3月", 180);
        chart.addData("4月", 200);
        chart.addData("5月", 170);
        chart.addData("6月", 190);
        
        // パネルに追加
        JPanel chartPanel = new JPanel(new BorderLayout());
        chartPanel.setBorder(BorderFactory.createTitledBorder("月別売上"));
        chartPanel.add(chart, BorderLayout.CENTER);
        
        // コントロールパネルを作成
        JPanel controlPanel = new JPanel(new FlowLayout());
        JButton refreshButton = new JButton("更新");
        JButton clearButton = new JButton("クリア");
        
        refreshButton.addActionListener(e -> {
            chart.clearData();
            // ランダムデータを生成
            String[] months = {"1月", "2月", "3月", "4月", "5月", "6月"};
            for (String month : months) {
                chart.addData(month, Math.random() * 300 + 50);
            }
        });
        
        clearButton.addActionListener(e -> chart.clearData());
        
        controlPanel.add(refreshButton);
        controlPanel.add(clearButton);
        
        add(chartPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(null);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ChartExample().setVisible(true);
        });
    }
}
```

### 17.13 インタラクティブな描画コンポーネント

マウス操作で線を描画できるコンポーネントを作成してみましょう：

```java
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class DrawingPanel extends JComponent {
    private List<Point> currentStroke = new ArrayList<>();
    private List<List<Point>> allStrokes = new ArrayList<>();
    private Color drawingColor = Color.BLACK;
    private int strokeWidth = 2;
    
    public DrawingPanel() {
        setOpaque(true);
        setBackground(Color.WHITE);
        
        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                currentStroke = new ArrayList<>();
                currentStroke.add(e.getPoint());
            }
            
            @Override
            public void mouseDragged(MouseEvent e) {
                currentStroke.add(e.getPoint());
                repaint();
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                if (!currentStroke.isEmpty()) {
                    allStrokes.add(new ArrayList<>(currentStroke));
                    currentStroke.clear();
                }
            }
        };
        
        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                           RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, 
                                     BasicStroke.JOIN_ROUND));
        g2d.setColor(drawingColor);
        
        // 完成した線を描画
        for (List<Point> stroke : allStrokes) {
            drawStroke(g2d, stroke);
        }
        
        // 現在描画中の線を描画
        if (!currentStroke.isEmpty()) {
            drawStroke(g2d, currentStroke);
        }
        
        g2d.dispose();
    }
    
    private void drawStroke(Graphics2D g2d, List<Point> stroke) {
        if (stroke.size() < 2) {
            if (stroke.size() == 1) {
                Point p = stroke.get(0);
                g2d.fillOval(p.x - strokeWidth/2, p.y - strokeWidth/2, 
                           strokeWidth, strokeWidth);
            }
            return;
        }
        
        for (int i = 1; i < stroke.size(); i++) {
            Point p1 = stroke.get(i - 1);
            Point p2 = stroke.get(i);
            g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
        }
    }
    
    public void clearDrawing() {
        allStrokes.clear();
        currentStroke.clear();
        repaint();
    }
    
    public void setDrawingColor(Color color) {
        this.drawingColor = color;
    }
    
    public void setStrokeWidth(int width) {
        this.strokeWidth = width;
    }
    
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(600, 400);
    }
}
```

#### 描画アプリケーションの例

```java
import javax.swing.*;
import java.awt.*;

public class DrawingApplication extends JFrame {
    private DrawingPanel drawingPanel;
    
    public DrawingApplication() {
        setTitle("描画アプリケーション");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // 描画パネル
        drawingPanel = new DrawingPanel();
        JScrollPane scrollPane = new JScrollPane(drawingPanel);
        add(scrollPane, BorderLayout.CENTER);
        
        // ツールバー
        JPanel toolbar = new JPanel(new FlowLayout());
        
        // 色選択ボタン
        JButton[] colorButtons = {
            createColorButton("黒", Color.BLACK),
            createColorButton("赤", Color.RED),
            createColorButton("青", Color.BLUE),
            createColorButton("緑", Color.GREEN)
        };
        
        for (JButton button : colorButtons) {
            toolbar.add(button);
        }
        
        // 線の太さスライダー
        toolbar.add(new JLabel("線の太さ:"));
        JSlider strokeSlider = new JSlider(1, 10, 2);
        strokeSlider.addChangeListener(e -> {
            drawingPanel.setStrokeWidth(strokeSlider.getValue());
        });
        toolbar.add(strokeSlider);
        
        // クリアボタン
        JButton clearButton = new JButton("クリア");
        clearButton.addActionListener(e -> drawingPanel.clearDrawing());
        toolbar.add(clearButton);
        
        add(toolbar, BorderLayout.NORTH);
        
        pack();
        setLocationRelativeTo(null);
    }
    
    private JButton createColorButton(String name, Color color) {
        JButton button = new JButton(name);
        button.setBackground(color);
        button.setForeground(color.equals(Color.BLACK) ? Color.WHITE : Color.BLACK);
        button.addActionListener(e -> drawingPanel.setDrawingColor(color));
        return button;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new DrawingApplication().setVisible(true);
        });
    }
}
```

### 17.14 カスタムコンポーネント設計のベストプラクティス

カスタムコンポーネントを設計する際は、以下の点に注意してください：

#### 1. 適切な基底クラスの選択

```java
// 単純な描画コンポーネント
public class MyComponent extends JComponent

// 既存コンポーネントを拡張
public class EnhancedButton extends JButton

// パネルとして使用
public class MyPanel extends JPanel
```

#### 2. パフォーマンスの最適化

```java
@Override
protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    
    // 必要な場合のみ再描画
    if (needsRepaint) {
        // 重い描画処理
        needsRepaint = false;
    }
}

// 効率的な境界計算
@Override
public boolean contains(int x, int y) {
    // カスタムの境界判定
    return customBounds.contains(x, y);
}
```

#### 3. アクセシビリティの考慮

```java
public MyComponent() {
    // キーボードナビゲーション対応
    setFocusable(true);
    
    // アクセシビリティ情報を設定
    getAccessibleContext().setAccessibleName("カスタムコンポーネント");
    getAccessibleContext().setAccessibleDescription("説明");
}
```

#### 4. イベント処理の標準化

```java
// 標準的なイベントリスナーパターンを実装
public void addMyComponentListener(MyComponentListener listener) {
    listeners.add(listener);
}

public void removeMyComponentListener(MyComponentListener listener) {
    listeners.remove(listener);
}

protected void fireMyComponentEvent(MyComponentEvent event) {
    for (MyComponentListener listener : listeners) {
        listener.componentChanged(event);
    }
}
```

#### まとめ

カスタムコンポーネントの作成により、標準のSwingコンポーネントでは実現できない独自のUI要素を実装できます：

1. **JComponentを継承**してカスタムコンポーネントを作成
2. **paintComponent()メソッドをオーバーライド**して独自の描画を実装
3. **マウスイベントやキーボードイベントを処理**してインタラクション機能を追加
4. **ActionListenerパターンを実装**して標準的なイベント処理を提供
5. **パフォーマンスとアクセシビリティを考慮**した設計

これらの技術により、プロフェッショナルなGUIアプリケーションに必要な独自のUI要素を作成できます。次のパートでは、これまで学んだ内容を活用した実践的な演習課題に取り組みます。



<!-- Merged from chapter17e-exercises.md -->

## Part E: 章末演習

本章で学んだGUIプログラミングの基礎概念を活用して、実践的な練習課題に取り組みましょう。

### 演習の目標

Swingを使ったデスクトップGUIアプリケーションの基礎を習得します。



### 基礎レベル課題（必須）

#### 課題1: 電卓アプリケーション

四則演算ができる電卓GUIアプリケーションを作成してください。

**技術的背景：GUIアプリケーション開発の基礎とSwing**

GUIプログラミングは、コンソールプログラミングとは根本的に異なるパラダイムです：

**イベント駆動プログラミングの特徴：**
- **イベントループ**：ユーザー入力を待機し続ける
- **非同期処理**：いつ何が起きるか予測不能
- **コールバック**：イベント発生時の処理登録
- **状態管理**：画面の現在状態を常に把握

**Swingの特徴と選択理由：**
```java
// JavaFX vs Swing
// Swing：成熟、安定、豊富なサードパーティライブラリ
// JavaFX：モダン、CSS対応、JavaFX 11からOpenJFX化

// 電卓の基本構造
JFrame frame = new JFrame("Calculator");
JTextField display = new JTextField();
JPanel buttonPanel = new JPanel(new GridLayout(4, 4));
```

**MVC（Model-View-Controller）パターンの適用：**
- **Model**：計算ロジック（四則演算、状態管理）
- **View**：GUI部品（ボタン、ディスプレイ）
- **Controller**：イベントハンドラ（ボタンクリック処理）

**実際の電卓アプリケーションの設計考慮点：**
- **数値精度**：BigDecimalによる正確な計算
- **エラー処理**：ゼロ除算、オーバーフロー
- **ユーザビリティ**：キーボード入力対応
- **状態遷移**：数値入力→演算子→数値入力→計算

**レイアウト管理の重要性：**
```java
// GridLayoutの利点：均等配置、自動リサイズ
new GridLayout(rows, cols, hgap, vgap)
// BorderLayoutの利点：領域分割、柔軟性
new BorderLayout()
```

**実世界の電卓アプリケーション例：**
- **Windows計算機**：標準/関数電卓モード切替
- **macOS計算機**：ウィジェット対応
- **Google計算機**：単位変換、グラフ表示

この演習では、実用的なGUIアプリケーションの基礎を学びます。

**要求仕様：**
- 数字ボタン（0-9）と演算子ボタン（+、-、×、÷）
- 結果表示用のJTextField
- GridLayoutによるボタン配置
- 計算ロジックの実装
- エラーハンドリング（ゼロ除算等）

**実行例：**
```
=== 電卓アプリケーション ===
ウィンドウ表示: "Calculator"
サイズ: 300x400ピクセル

電卓レイアウト:
┌─────────────────────────────────┐
│ [123.45________________]    │ (結果表示)
├─────────────────────────────────┤
│ [C] [±] [%] [÷]             │
├─────────────────────────────────┤
│ [7] [8] [9] [×]             │
├─────────────────────────────────┤
│ [4] [5] [6] [-]             │
├─────────────────────────────────┤
│ [1] [2] [3] [+]             │
├─────────────────────────────────┤
│ [0___] [.] [=]              │
└─────────────────────────────────┘

操作例:
1. 「123」入力 → ディスプレイに「123」表示
2. 「+」押下 → 演算子待機状態
3. 「456」入力 → ディスプレイに「456」表示
4. 「=」押下 → ディスプレイに「579」表示
```

#### 課題2: シンプルメモ帳

基本的なテキスト編集機能を持つメモ帳アプリケーションを作成してください。

**技術的背景：テキスト処理とファイルI/O**

テキストエディタは、多くのGUIアプリケーションの基本的な機能です：

**Swingテキストコンポーネントの階層：**
```java
JTextComponent
├── JTextField (1行テキスト)
├── JPasswordField (パスワード入力)
├── JTextArea (複数行テキスト)
└── JEditorPane (リッチテキスト、HTML対応)
```

**テキストエディタの主要機能：**
- **ファイル操作**：新規作成、開く、保存、名前を付けて保存
- **編集機能**：切り取り、コピー、貼り付け、元に戻す
- **検索置換**：文字列検索、正規表現検索
- **表示設定**：フォント、文字エンコーディング

**メニューシステムの実装：**
```java
JMenuBar menuBar = new JMenuBar();
JMenu fileMenu = new JMenu("ファイル");
fileMenu.add(new JMenuItem("新規"));
fileMenu.add(new JMenuItem("開く"));
fileMenu.addSeparator(); // 区切り線
fileMenu.add(new JMenuItem("保存"));
```

**ファイルダイアログの使用：**
```java
JFileChooser fileChooser = new JFileChooser();
int result = fileChooser.showOpenDialog(parent);
if (result == JFileChooser.APPROVE_OPTION) {
    File file = fileChooser.getSelectedFile();
    // ファイル処理
}
```

**実世界のテキストエディタ例：**
- **VS Code**：拡張機能、統合ターミナル
- **Sublime Text**：高速、マルチカーソル
- **Notepad++**：プラグイン、正規表現対応

**要求仕様：**
- JTextAreaによる複数行テキスト編集
- メニューバー（ファイル、編集）
- ファイルの新規作成、開く、保存機能
- JFileChooserによるファイル選択
- 基本的な編集機能（切り取り、コピー、貼り付け）

**実行例：**
```
=== シンプルメモ帳 ===
ウィンドウ表示: "メモ帳 - untitled"
サイズ: 600x400ピクセル

メニュー構成:
ファイル(F)
├── 新規(N)     Ctrl+N
├── 開く(O)     Ctrl+O
├── 保存(S)     Ctrl+S
├── 名前を付けて保存(A)
└── 終了(X)

編集(E)
├── 元に戻す(U)   Ctrl+Z
├── ──────────
├── 切り取り(T)   Ctrl+X
├── コピー(C)     Ctrl+C
└── 貼り付け(P)   Ctrl+V

中央: スクロール可能なテキストエリア
下部: ステータスバー（行数、文字数表示）
```

#### 課題3: レイアウトデモ

複数のレイアウトマネージャーを組み合わせた画面を作成してください。

**技術的背景：レイアウト管理の設計原則**

効果的なGUIレイアウトは、ユーザビリティに直結する重要な要素です：

**レイアウトマネージャーの特性比較：**

| レイアウト | 用途 | 利点 | 制限 |
|------------|------|------|------|
| BorderLayout | メイン画面構成 | 直感的な5領域分割 | 領域数が固定 |
| GridLayout | 均等配置 | すべて同サイズ | サイズ調整不可 |
| FlowLayout | ツールバー | 自然な流れ | 行折り返し |
| GridBagLayout | 複雑な配置 | 最大の柔軟性 | 設定が複雑 |
| BoxLayout | 一方向配置 | シンプル | 直線配置のみ |

**階層的レイアウト設計：**
```java
// トップレベル：BorderLayout
JFrame frame = new JFrame();
frame.setLayout(new BorderLayout());

// 中央：左右分割（JSplitPane）
JSplitPane splitPane = new JSplitPane();
frame.add(splitPane, BorderLayout.CENTER);

// 各パネル：適切なレイアウト選択
JPanel leftPanel = new JPanel(new GridLayout(0, 1));
JPanel rightPanel = new JPanel(new BorderLayout());
```

**レスポンシブデザインの考慮：**
- **最小/最大サイズ**：setMinimumSize(), setMaximumSize()
- **推奨サイズ**：getPreferredSize()のオーバーライド
- **比率維持**：JSplitPaneのdividerLocation設定

**要求仕様：**
- 最低3種類のレイアウトマネージャーを使用
- JPanelによる階層的構成
- 各レイアウトの特徴を示すコンポーネント配置
- ウィンドウリサイズに対する適切な応答

**実行例：**
```
=== レイアウトデモ ===
ウィンドウ表示: "Layout Demo"
サイズ: 800x600ピクセル

上部（BorderLayout.NORTH）：
┌─────────────────────────────────────────┐
│ FlowLayoutツールバー: [新規][開く][保存] │
└─────────────────────────────────────────┘

中央（JSplitPane - 水平分割）：
┌──────────────────┬──────────────────────┐
│左パネル(GridLayout) │右パネル(BorderLayout)  │
│┌────┬────┬────┐│┌──────────────────┐ │
││ B1 │ B2 │ B3 ││││      Center      │ │
│├────┼────┼────┤││└──────────────────┘ │
││ B4 │ B5 │ B6 ││                      │
│└────┴────┴────┘│                      │
└──────────────────┴──────────────────────┘

下部（BorderLayout.SOUTH）：
┌─────────────────────────────────────────┐
│ ステータス: GridLayout | BorderLayout    │
└─────────────────────────────────────────┘
```

#### 課題4: カラーピッカー

RGB値を調整して色を選択できるカラーピッカーアプリケーションを作成してください。

**技術的背景：色彩理論とGUIコントロール**

色の操作は、グラフィックアプリケーションの基本機能です：

**RGB色空間の特性：**
```java
// RGB: Red(0-255), Green(0-255), Blue(0-255)
Color color = new Color(red, green, blue);
int rgb = color.getRGB(); // 24bit整数値として取得
```

**HSB色空間の利点：**
```java
// HSB: Hue(色相), Saturation(彩度), Brightness(明度)
Color color = Color.getHSBColor(hue, saturation, brightness);
// より直感的な色調整が可能
```

**Swingコントロールの活用：**
```java
// スライダー：連続値調整
JSlider redSlider = new JSlider(0, 255, 128);
redSlider.setPaintTicks(true);
redSlider.setPaintLabels(true);

// スピナー：数値入力
JSpinner redSpinner = new JSpinner(new SpinnerNumberModel(128, 0, 255, 1));

// 色見本パネル
JPanel colorPreview = new JPanel();
colorPreview.setBackground(currentColor);
```

**リアルタイム更新の実装：**
```java
// ChangeListenerによるリアルタイム反映
redSlider.addChangeListener(e -> updateColor());
greenSlider.addChangeListener(e -> updateColor());
blueSlider.addChangeListener(e -> updateColor());
```

**実世界のカラーピッカー例：**
- **Adobe Photoshop**：HSB、RGB、CMYK対応
- **macOS ColorSync**：色プロファイル管理
- **Windows ペイント**：パレット、カスタム色

**要求仕様：**
- RGB各成分のJSlider（0-255範囲）
- 数値入力用のJSpinner
- 選択色のプレビューパネル
- 16進カラーコード表示（#RRGGBB形式）
- リアルタイム色更新

**実行例：**
```
=== カラーピッカー ===
ウィンドウ表示: "Color Picker"
サイズ: 400x300ピクセル

上部：色プレビュー
┌─────────────────────────────────────┐
│                                     │
│            #FF6600                 │ (選択色表示)
│                                     │
└─────────────────────────────────────┘

中央：RGB調整
Red   (255): [=========|    ] [255]
Green (102): [====|         ] [102]  
Blue  (  0): [|            ] [  0]

下部：色情報
┌─────────────────────────────────────┐
│ RGB(255,102,0) HSB(24°,100%,100%) │
│ 16進: #FF6600  Webセーフ: #FF6633  │
└─────────────────────────────────────┘

操作:
- スライダードラッグで連続調整
- スピナーで数値直接入力
- リアルタイムプレビュー更新
```



### 実装のヒント

#### GUI基礎のポイント

1. **EDTの正しい使用**
   ```java
   SwingUtilities.invokeLater(() -> {
       createAndShowGUI();
   });
   ```

2. **適切なレイアウト選択**
   - BorderLayout: 全体構成
   - GridLayout: 等間隔配置
   - FlowLayout: 自然な流れ

3. **イベント処理の分離**
   ```java
   // ラムダ式で簡潔に
   button.addActionListener(e -> handleButtonClick());
   
   // 複雑な処理は別メソッドに分離
   private void handleButtonClick() {
       // ビジネスロジック
   }
   ```

#### よくある落とし穴

1. **EDT上での重い処理**
   - 長時間処理でUIフリーズ
   - SwingWorkerで解決

2. **レイアウトの競合**
   - setSize()とpack()の混在
   - setLayout(null)の使用

3. **メモリリーク**
   - リスナーの適切な除去
   - 循環参照の回避

#### 設計のベストプラクティス

1. **MVCパターンの適用**
   - Model: データとビジネスロジック
   - View: UI表示
   - Controller: イベント処理

2. **コンポーネントの再利用**
   - カスタムコンポーネント作成
   - 設定の外部化

3. **ユーザビリティの考慮**
   - 適切なフィードバック
   - エラー処理とメッセージ表示
   - キーボードショートカット対応



### 実装環境

- **Java SE 11以降**推奨
- **IDE**: IntelliJ IDEA、Eclipse、VS Code
- **ビルドツール**: Maven、Gradleが使用可能



### 完了確認チェックリスト

#### 基礎レベル

- [ ] JFrameでウィンドウを作成できる
- [ ] 基本コンポーネントを配置できる
- [ ] レイアウトマネージャーを使い分けできる
- [ ] ボタンクリックイベントを処理できる
- [ ] テキスト入力を取得・表示できる

#### 技術要素

- [ ] EDTを理解し、適切に使用している
- [ ] ラムダ式でイベント処理を記述している
- [ ] ファイルI/Oと連携している（メモ帳課題）
- [ ] リアルタイム更新を実装している（カラーピッカー課題）
- [ ] エラーハンドリングを適切に行っている

#### 応用レベル

- [ ] MVCパターンを意識した設計ができる
- [ ] カスタムコンポーネントを作成できる
- [ ] SwingWorkerで非同期処理を実装できる
- [ ] メニューシステムを構築できる
- [ ] キーボードショートカットを実装できる

これらの演習を通じて、実用的なGUIアプリケーション開発の基礎スキルを身につけることができます。



