# <b>18章</b> <span>GUIプログラミングの基礎</span> <small>Swingで作るデスクトップアプリ</small>

## 本章の学習目標

### 前提知識

必須
- 第16章までの総合的なJavaプログラミング能力
- クラスとオブジェクトの設計、継承とインターフェイス、例外処理、コレクションフレームワーク、ファイル入出力
- イベント処理の基本概念
- オブジェクト指向設計の実践経験

推奨
- 日常的なGUIアプリケーションの使用経験（Windows、macOS、Linux）
- メニュー、ボタン、テキストフィールド、スクロールバーなどの標準的なGUIコンポーネントの動作理解
- ユーザビリティに対する基本的な理解

### 学習目標

本章では、Javaを使ったデスクトップGUIアプリケーション開発の基礎を体系的に学習します。知識理解の面では、まずGUIプログラミングの基本概念と、それがもたらす新たな課題について理解します。
コンソールアプリケーションが上から下へ順番に実行される手続き的なモデルであるのに対し、GUIアプリケーションはユーザーの操作を待ち、それに反応するイベント駆動型のモデルを採用しています。
この根本的な違いを理解することが、効果的なGUIプログラミングの第一歩となります。

Swingフレームワークのアーキテクチャについても深く理解します。SwingはJavaの標準GUIツールキットとして、軽量コンポーネントアーキテクチャを採用し、プラットフォーム独立性を保ちながら豊富な機能を提供します。
コンポーネントの階層構造、ペイントシステム、イベントディスパッチスレッド（EDT）など、Swingの内部動作を理解することで、より効率的で安定したGUIアプリケーションを開発できます。
また、BorderLayout、FlowLayout、GridLayoutなど、さまざまなレイアウトマネージャーの特徴を理解し、画面サイズの変更に柔軟に対応できる設計方法を学びます。

技能習得の観点では、JFrame、JButton、JTextField、JLabelなどの基本的なGUIコンポーネントの使用方法から始め、これらを組み合わせて実用的なインターフェイスを構築する技術を習得します。
イベントリスナの実装では、ActionListener、MouseListener、KeyListenerなどのインターフェイスを使って、ユーザーの操作に応答するプログラムを作成します。
ラムダ式を使った簡潔なイベントハンドラの記述方法も学び、現代的なJavaプログラミングスタイルを身につけます。

アプリケーション設計能力の面では、Model-View-Controller（MVC）パターンを意識したGUI設計を学びます。
ビジネスロジックとプレゼンテーション層を明確に分離することで、テスト可能で保守性の高いアプリケーションを構築する方法を習得します。
また、ユーザビリティを考慮したインターフェイス設計の基本原則、たとえば一貫性のある操作性、即座のフィードバック、エラー処理などを実践的に学びます。

最終的な到達レベルとして、基本的な機能を持つGUIアプリケーションを独力で開発できるようになることを目指します。
これには、ユーザーの操作に素早く応答するイベント処理の実装、画面レイアウトとコンポーネント配置の論理的な設計、そしてファイルI/Oやデータベースアクセスなど、これまで学んだ技術とGUIを連携させたアプリケーションの作成能力が含まれます。
これらのスキルにより、エンドユーザーに価値を提供する実用的なデスクトップアプリケーションを開発できます。

## 章の構成

本章は、GUIプログラミングの基礎を体系的に学習できるよう、以下のパートで構成されています。

### [Part A: 基本コンポーネントとレイアウト](chapter18a-basic-components.md)
- はじめてのGUIプログラム
- 基本コンポーネントの配置（JFrame、JLabel、JButton）
- レイアウトマネージャーの基礎（BorderLayout、GridLayout）
- パネルを使った複雑なレイアウト

### [Part B: イベント処理入門](chapter18b-event-handling-intro.md)
- イベント駆動プログラミングの概念
- ボタンクリックに反応するプログラム
- ActionListenerとラムダ式
- カウンタアプリケーション

### [Part C: EDT（Event Dispatch Thread）とスレッド安全性](chapter18c-edt-thread-safety.md)
- EDTとは何か、なぜ重要なのか
- SwingUtilities.invokeLater()の使用方法
- SwingWorkerによる非同期処理
- GUIアプリケーションのスレッド安全性

### [Part D: カスタムコンポーネントの作成](chapter18d-custom-components.md)
- JComponentを継承したカスタムコンポーネント
- paintComponent()メソッドのオーバーライド
- マウスイベントとキーボードイベントの処理
- 高度なカスタムコンポーネントの実装例

### [Part E: 章末演習](chapter18e-exercises.md)
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

本章で学んだGUIプログラミングの基礎を活用して、第19章ではより高度なイベント処理について学習します。

<!-- Merged from chapter18a-basic-components.md -->

## Part A: 基本コンポーネントとレイアウト

### はじめてのGUIプログラム

#### Hello Swing - ウィンドウを表示してみよう

まずはもっとも基本的なGUIプログラムから始めましょう。以下は、単純なウィンドウを表示するプログラムです。

Swingアプリケーションの基本構造。

このコードは、Swing GUIアプリケーションの最小構成を示しています。各ステップは、ウィンドウの作成から表示までの標準的なフローを表しています。

最小限のSwingアプリケーションの構造。

Swingを使ったGUIアプリケーションの最小限の構造を理解するために、ウィンドウを表示するだけのシンプルなプログラムを見てみましょう。

<span class="listing-number">**サンプルコード18-2**</span>

```java
import javax.swing.JFrame;

public class HelloSwing {
    public static void main(String[] args) {
        JFrame frame = new JFrame("はじめてのSwingアプリケーション"); // ①
        
        frame.setSize(400, 300);                          // ②
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // ③
        frame.setLocationRelativeTo(null);                // ④
        frame.setVisible(true);                           // ⑤
    }
}
```

Swingアプリケーションの基本的な初期化手順。

①　メインウィンドウの作成： JFrameオブジェクトを生成し、ウィンドウのタイトルバーに表示される文字列を指定します。この時点ではまだウィンドウは表示されません。

②　ウィンドウサイズの設定： setSize()メソッドで横400ピクセル、縦300ピクセルの大きさを指定します。この値はタイトルバーやウィンドウ枠を含む全体のサイズです。

③　終了動作の設定： ユーザーがウィンドウの×ボタンをクリックした際の動作を定義します。EXIT_ON_CLOSEは、ウィンドウを閉じると同時にJavaアプリケーション全体を終了する設定です。

④　ウィンドウ位置の調整： setLocationRelativeTo(null)により、ウィンドウをスクリーンの中央に自動的に配置します。nullを指定することで、画面全体を基準とした中央配置になります。

⑤　ウィンドウの表示： setVisible(true)を呼び出すことで、これまでに設定されたすべての属性を反映してウィンドウが画面に表示されます。このメソッド呼び出しは必須で、これがないとウィンドウは非表示状態のままです。

このプログラムの各行を詳しく見ていきましょう。

1. JFrameクラス： Swingでウィンドウを表すクラスである。すべてのGUIアプリケーションの基礎となる。
2. setSize(): ウィンドウの幅と高さをピクセル単位で設定する。
3. setDefaultCloseOperation(): ウィンドウの×ボタンを押したときの動作を指定する。
4. setLocationRelativeTo(null): nullを指定すると、ウィンドウが画面中央に配置される。
5. setVisible(true): ウィンドウを表示する。これを呼ばないと画面に表示されません。

#### ウィンドウの基本設定

`JFrame`オブジェクトのメソッドを呼び出すことで、ウィンドウのさまざまな設定が可能です。

ウィンドウの詳細設定例。

このコードは、ウィンドウの外観や動作をカスタマイズする方法を示しています。各設定はユーザーエクスペリエンスに直接影響します。

基本的なウィンドウ。

JFrameクラスは、Swingアプリケーションの基本となるトップレベルコンテナです。すべてのGUIアプリケーションは、少なくとも1つのJFrameを持ちます。
JFrameは、タイトルバー、境界線、メニューバー、およびコンテンツペインを含む完全なウィンドウフレームを提供します。

ウィンドウの設定では、サイズ、位置、動作などをカスタマイズできます。特に重要なのは、setDefaultCloseOperation()メソッドで、ウィンドウの閉じるボタンがクリックされたときの動作を定義します。

<span class="listing-number">**サンプルコード18-4**</span>

```java
import javax.swing.JFrame;

public class WindowSettings {
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        
        frame.setTitle("カスタマイズされたウィンドウ");     // ①
        frame.setSize(500, 400);                      // ②
        frame.setResizable(false);                    // ③
        frame.setLocation(100, 50);                   // ④
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // ⑤
        frame.setVisible(true);                       // ⑥
    }
}
```

各設定オプションの説明。
- ① タイトルバー文字 - ウィンドウ上部に表示されるアプリケーション名
- ② ウィンドウサイズ - 幅500ピクセル、高さ400ピクセルに固定
- ③ リサイズ禁止 - ユーザーがウィンドウサイズを変更できないように制限
- ④ 絶対位置指定 - スクリーン左上から横100px、縦50pxの位置に配置
- ⑤ 終了動作 - ×ボタンクリック時にアプリケーションを完全終了
- ⑥ 表示実行 - ウィンドウをスクリーンに表示

### 基本的なコンポーネントの配置

#### JLabelで文字を表示する

ボタンとラベル。

GUIアプリケーションの基本要素であるボタンとラベルは、ユーザーインターフェイスの中核をなすコンポーネントです。
JLabelはテキストやアイコンを表示するための読み取り専用コンポーネントで、JButtonはユーザーのクリック操作を受け付けるインタラクティブなコンポーネントです。

JLabelの特徴。
- テキスト、HTML、アイコンを表示可能
- テキストの配置を水平・垂直方向で細かく制御
- 他のコンポーネントのラベルとして使用可能

ウィンドウに文字を表示してみましょう。

<span class="listing-number">**サンプルコード18-6**</span>

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

複数のコンポーネントを配置するには、レイアウトマネージャーを使います。

レイアウトマネージャー。

レイアウトマネージャーは、コンテナ内のコンポーネントの配置を自動的に管理する仕組みです。ウィンドウサイズの変更や異なる画面解像度に対応するため、絶対位置ではなく相対的な配置ルールを使用します。Javaのレイアウトマネージャーの主な特徴。

1. プラットフォーム独立性： 異なるOSで一貫したレイアウトを実現
2. 動的サイズ調整： ウィンドウサイズ変更時に自動的に再配置
3. コンポーネントの優先サイズ： 各コンポーネントの推奨サイズを考慮
4. 柔軟な配置： 画面の文字サイズやDPIに対応

BorderLayoutによる5領域レイアウトの実装。

BorderLayoutは、画面を5つの領域（北、南、東、西、中央）に分割してコンポーネントを配置するレイアウトマネージャです。多くのアプリケーションで使用される基本的なレイアウトパターンです。

<span class="listing-number">**サンプルコード18-8**</span>

```java
import javax.swing.*;
import java.awt.*;

public class MultipleComponents {
    public static void main(String[] args) {
        JFrame frame = new JFrame("複数のコンポーネント");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        frame.setLayout(new BorderLayout());  // ①
        
        frame.add(new JLabel("北", JLabel.CENTER), BorderLayout.NORTH);   // ②
        frame.add(new JLabel("南", JLabel.CENTER), BorderLayout.SOUTH);   // ②
        frame.add(new JLabel("東", JLabel.CENTER), BorderLayout.EAST);    // ②
        frame.add(new JLabel("西", JLabel.CENTER), BorderLayout.WEST);    // ②
        frame.add(new JLabel("中央", JLabel.CENTER), BorderLayout.CENTER); // ②
        
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
```

BorderLayoutの特徴と配置ルール。

①　レイアウトマネージャの設定： JFrameのデフォルトレイアウトもBorderLayoutですが、明示的に設定することで意図を明確にします。

②　5領域への配置： 各領域は以下の特性を持ちます。
- NORTH/SOUTH: 水平方向に最大まで拡張し、コンポーネントの推奨高さを維持
- EAST/WEST: 垂直方向に最大まで拡張し、コンポーネントの推奨幅を維持  
- CENTER: 残りのすべてのスペースを占有し、ウィンドウサイズ変更時に最も影響を受ける

この配置方法により、典型的なアプリケーション構造（ツールバー・メインエリア・ステータスバー）を簡単に実現できます。

#### 基本コンポーネントの詳細な使用例

#### JLabelとアイコンの活用

<span class="listing-number">**サンプルコード18-10**</span>

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

<span class="listing-number">**サンプルコード18-12**</span>

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

テキストフィールド。

テキスト入力はGUIアプリケーションの基本的な入力手段です。Swingはさまざまなテキスト入力コンポーネントを提供しており、用途に応じて選択できます。

1. JTextField: 一行テキスト入力（名前、メールアドレスなど）
2. JPasswordField: パスワード入力（文字がマスクされる）
3. JTextArea: 複数行テキスト入力（コメント、説明文など）
4. JFormattedTextField: 書式制限付き入力（日付、数値など）

<span class="listing-number">**サンプルコード18-14**</span>

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

チェックボックス。

チェックボックスは、複数の選択肢から任意の組み合わせを選択できるコンポーネントです。各項目は独立しており、オン/オフを自由に切り替えられます。設定画面のオプション選択や、アンケートの複数回答などに適しています。

ラジオボタン。

ラジオボタンは、複数の選択肢から1つだけを選択する場合に使用します。ButtonGroupにグループ化することで、同一グループ内では1つのラジオボタンしか選択できなくなります。

コンボボックス。

コンボボックス（ドロップダウンリスト）は、限られたスペースで多くの選択肢を提供する場合に有効です。通常は1つの項目のみ表示され、クリックするとリストが展開されます。

リスト。

JListは、複数の項目を一度に表示し、単一または複数選択を可能にするコンポーネントです。スクロール機能を追加することで、大量のデータも扱えます。

<span class="listing-number">**サンプルコード18-16**</span>

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

### レイアウトマネージャーの基礎

#### BorderLayout - 5つの領域に配置

BorderLayoutの例。

BorderLayoutは、ウィンドウを方位に基づいた5つの領域に分割するレイアウトマネージャーです。JFrameのデフォルトレイアウトであり、多くのアプリケーションで基本構造として使用されます。

BorderLayoutの領域の特性。
- NORTH/SOUTH: ウィンドウの幅全体を使用し、高さはコンポーネントの優先サイズに応じて決定
- EAST/WEST: 残った高さを使用し、幅はコンポーネントの優先サイズに応じて決定
- CENTER: 残ったすべての領域を使用（伸縮可能）

`BorderLayout`は、ウィンドウを5つの領域（北、南、東、西、中央）に分割してコンポーネントを配置します。

<span class="listing-number">**サンプルコード18-18**</span>

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

BorderLayoutの特徴。
- NORTHとSOUTH画面の幅いっぱいに広がり、高さは推奨サイズ
- EASTとWEST残りの高さいっぱいに広がり、幅は推奨サイズ
- CENTER残りの領域すべてを占める

#### GridLayout - 格子状に配置

GridLayoutの例。

GridLayoutは、コンポーネントを同じサイズのセルに均等に配置するレイアウトマネージャーです。電卓のボタン配置や、カレンダーの日付表示など、規則的な配置が必要な場合に最適です。

GridLayoutの特徴。
- すべてのセルが同じサイズに統一される
- コンポーネントは左から右、上から下へ順番に配置
- ウィンドウサイズに応じてセルサイズが自動調整
- セル間の間隔をピクセル単位で指定可能

`GridLayout`は、コンポーネントを格子状（行と列）に配置します。

<span class="listing-number">**サンプルコード18-20**</span>

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

FlowLayoutの例。

FlowLayoutは、コンポーネントを流れるように配置する最もシンプルなレイアウトマネージャーです。コンポーネントはコンテナの幅に応じて自動的に折り返されるため、ウィンドウサイズの変更に柔軟に対応します。

FlowLayoutの特徴。
- デフォルトでは中央揃えで配置
- 左揃え、右揃えも指定可能
- コンポーネント間の間隔をピクセル単位で設定可能
- ボタンやラベルなど、小さなコンポーネントの配置に適している

`FlowLayout`は、コンポーネントを左から右へ、行がいっぱいになったら次の行へと配置します。

<span class="listing-number">**サンプルコード18-22**</span>

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

### パネルを使った複雑なレイアウト

#### JPanelで階層的なレイアウトを構築

JPanelの使用。

JPanelは、Swingで最も頻繁に使用されるコンテナコンポーネントの1つです。軽量なコンテナとして、他のコンポーネントを整理し、グループ化するために使用されます。JPanelの主な用途には以下があります。

1. レイアウトの分割： 複雑なUIを論理的なセクションに分割
2. 背景色や境界線の設定： 視覚的なグループ化
3. カスタム描画： paintComponent()メソッドをオーバーライドして独自の描画を実装
4. イベント処理の単位： パネル単位でイベントリスナーを設定

複雑なレイアウトを実現するには、`JPanel`を使って階層的にコンポーネントを組み合わせます。

<span class="listing-number">**サンプルコード18-24**</span>

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

レイアウトマネージャーを組み合わせることで、より柔軟な画面設計が可能です。

<span class="listing-number">**サンプルコード18-26**</span>

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

本パートでは、Swingを使った基本的なGUIプログラミングについて学習しました。

1. JFrameを使ったウィンドウの作成と基本設定
2. JLabel、JButton、JTextFieldなどの基本コンポーネントの使用方法
3. BorderLayout、GridLayout、FlowLayoutなどのレイアウトマネージャーの特徴
4. JPanelを使った階層的なレイアウトの構築

これらの基礎知識により、シンプルなGUIアプリケーションの画面を構築できるようになりました。次のパートでは、これらのコンポーネントにユーザーの操作に応答する機能を追加する方法を学習します。

<!-- Merged from chapter18b-event-handling-intro.md -->

## Part B: イベント処理入門

### イベント駆動プログラミングの概念

#### イベント駆動とは何か？

これまでのコンソールプログラムは、上から下へ順番に実行される「手続き型」でした。
しかし、GUIアプリケーションはユーザーがいつ、どのボタンを押すかわからないため、「ユーザーの操作（イベント）をきっかけに、特定の処理（リスナー）が動く」というイベント駆動（Event-Driven）というモデルを採用しています。

イベント駆動は、「何かが起きたら、これを実行する」というルールの集合でプログラムを構築する考え方です。

#### イベント駆動の3要素

Swingのイベント処理は、以下の3つの要素で構成されます。

1. イベントソース： イベントを発生させるコンポーネント（例： `JButton`, `JTextField`）
2. イベントオブジェクト： 発生したイベントの詳細情報を持つオブジェクト（例： `ActionEvent`はボタンがクリックされたことを示す）
3. イベントリスナー: イベントを監視し、イベントが発生したときに実行される処理を記述したオブジェクト（例： `ActionListener`）

処理の流れは以下の通りです。

```
ユーザー操作 → イベントソース → イベントオブジェクト生成 → リスナーに通知 → 処理実行
```

### ボタンクリックに反応するプログラム

#### 基本的なボタンクリックイベント

もっとも基本的なイベント処理である、ボタンクリックの実装を見てみましょう。

<span class="listing-number">**サンプルコード18-28**</span>

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

イベントソースに「このイベントが起きたら、このリスナーを呼び出してください」とお願いすることを「リスナーの登録」と呼びます。`addXXXListener`という形式のメソッド（例： `addActionListener`）を使用します。

#### テキストフィールドとの連携

ボタンが押されたタイミングで、`JTextField`に入力されているテキストを取得してみましょう。

<span class="listing-number">**サンプルコード18-30**</span>

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

### ラムダ式による簡潔な記述

#### ラムダ式の基本

ラムダ式を使うと、匿名クラスよりも簡潔にイベントリスナーを記述できます。`ActionListener`のように、実装すべきメソッドが1つだけのインターフェイス（関数型インターフェイス）に対して使用できます。

先ほどのボタンクリックの例をラムダ式で書き換えてみましょう。

<span class="listing-number">**サンプルコード18-32**</span>

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

<span class="listing-number">**サンプルコード18-34**</span>

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

### カウンタアプリケーション

#### シンプルなカウンタの実装

実際に動作するアプリケーションとして、カウンタを作成してみましょう。

<span class="listing-number">**サンプルコード18-36**</span>

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

`JCheckBox`や`JRadioButton`の選択状態を取得するイベント処理も実装してみましょう。

<span class="listing-number">**サンプルコード18-38**</span>

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

複数のコンポーネントを組み合わせた、より実用的な例を作成してみましょう。

<span class="listing-number">**サンプルコード18-40**</span>

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

本パートでは、GUIアプリケーションの核心であるイベント処理について学習しました。

1. イベント駆動プログラミングの基本概念
2. イベントソース、イベントオブジェクト、イベントリスナーの3要素
3. ActionListenerを使ったボタンクリックイベントの実装
4. ラムダ式による簡潔なイベント処理の記述
5. 複数のコンポーネントを組み合わせた実用的なアプリケーション

これらの知識により、ユーザーの操作に応答するインタラクティブなGUIアプリケーションを作成できるようになりました。次のパートでは、より高度なイベント処理とスレッド安全性について学習します。

<!-- Merged from chapter18c-edt-thread-safety.md -->

## Part C: EDT（Event Dispatch Thread）とスレッド安全性

### Event Dispatch Thread（EDT）の理解と実践

#### EDTとは何か

Event Dispatch Thread（EDT）は、Swingアプリケーションにおいてすべてのイベント処理とUI更新を担当する専用のスレッドです。
Swingのコンポーネントはスレッドセーフではないため、複数のスレッドから同時にアクセスされると、予期しない動作やクラッシュを引き起こす可能性があります。

##### EDTの重要性

SwingのGUIコンポーネントへのアクセス（表示の更新、プロパティの変更、イベント処理など）は、原則としてEDT上でのみ行う必要があります。これにより以下の利点があります。

1. スレッド安全性の確保： 複数のスレッドからの同時アクセスを防ぐ
2. 一貫性のある描画： UIの更新が同期的に行われる
3. デッドロックの回避： スレッド間の競合状態を防ぐ

##### 正しいEDTの使用方法

<span class="listing-number">**サンプルコード18-42**</span>

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

#### EDTの役割と仕組み

##### イベントキューの管理

EDTは内部的にイベントキューを管理しており、すべてのユーザー操作（マウスクリック、キー入力など）とプログラム的なイベントを順次処理します。

<span class="listing-number">**サンプルコード18-44**</span>

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

##### SwingWorkerによる非同期処理

長時間かかる処理をEDT上で実行すると、UIがフリーズしてしまいます。このような場合にはSwingWorkerを使用して背景スレッドで処理を実行します。

<span class="listing-number">**サンプルコード18-46**</span>

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

#### EDT関連のユーティリティメソッド

SwingUtilitiesクラスには、EDTを操作するための便利なメソッドが用意されています。

<span class="listing-number">**サンプルコード18-48**</span>

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

#### EDTに関するベストプラクティス

##### GUIの初期化

アプリケーションの起動時は、必ずEDT上でGUIを初期化します。

<span class="listing-number">**サンプルコード18-50**</span>

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

##### 重い処理の扱い

EDT上では重い処理を避け、必要な場合はSwingWorkerを使用します。

<span class="listing-number">**サンプルコード18-52**</span>

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

##### 定期的な処理

定期的なUI更新が必要な場合は、`javax.swing.Timer`を使用します。

<span class="listing-number">**サンプルコード18-54**</span>

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

#### デッドロックと競合状態の回避

<span class="listing-number">**サンプルコード18-56**</span>

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

EDTの理解は、安定したSwingアプリケーションを構築するために不可欠です。

1. すべてのGUI操作はEDT上で実行する
2. 重い処理はSwingWorkerを使用して非同期実行する
3. SwingUtilities.invokeLater()でEDT上での実行を保証する
4. SwingUtilities.isEventDispatchThread()で現在のスレッドを確認する

これらの原則を守ることで、レスポンシブで安定したGUIアプリケーションを開発できます。次のパートでは、カスタムコンポーネントの作成方法について学習します。

<!-- Merged from chapter18d-custom-components.md -->

## Part D: カスタムコンポーネントの作成

### 適切な基底クラスの選択

<span class="listing-number">**サンプルコード18-58**</span>

```java
// 単純な描画コンポーネント
public class MyComponent extends JComponent

// 既存コンポーネントを拡張
public class EnhancedButton extends JButton

// パネルとして使用
public class MyPanel extends JPanel
```

#### パフォーマンスの最適化

<span class="listing-number">**サンプルコード18-60**</span>

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

#### アクセシビリティの考慮

<span class="listing-number">**サンプルコード18-62**</span>

```java
public MyComponent() {
    // キーボードナビゲーション対応
    setFocusable(true);
    
    // アクセシビリティ情報を設定
    getAccessibleContext().setAccessibleName("カスタムコンポーネント");
    getAccessibleContext().setAccessibleDescription("説明");
}
```

#### イベント処理の標準化

<span class="listing-number">**サンプルコード18-64**</span>

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

カスタムコンポーネントの作成により、標準のSwingコンポーネントでは実現できない独自のUI要素を実装できます。

1. JComponentを継承してカスタムコンポーネントを作成
2. paintComponent()メソッドをオーバーライドして独自の描画を実装
3. マウスイベントやキーボードイベントを処理してインタラクション機能を追加
4. ActionListenerパターンを実装して標準的なイベント処理を提供
5. パフォーマンスとアクセシビリティを考慮した設計

これらの技術により、プロフェッショナルなGUIアプリケーションに必要な独自のUI要素を作成できます。次のパートでは、これまで学んだ内容を活用した実践的な演習課題に取り組みます。

<!-- Merged from chapter18e-exercises.md -->

## 章末演習

### 演習課題へのアクセス
本章の演習課題は、GitHubリポジトリで提供されています。
`https://github.com/Nagatani/techbook-java-primer/tree/main/exercises/chapter18/`

### 課題構成
- 本章の基本概念の理解確認
- 応用的な実装練習
- 実践的な総合問題

詳細な課題内容と実装のヒントは、各課題フォルダ内のREADME.mdを参照してください。

1. 基礎課題： 電卓アプリケーションの作成
2. 発展課題： メモ帳アプリケーションの開発
3. チャレンジ課題： 画像ビューアーアプリケーションの実装

詳細な課題内容と実装のヒントは、GitHubリポジトリの各課題フォルダ内のREADME.mdを参照してください。

次のステップ： 基礎課題が完了したら、第19章「GUIイベント処理」に進みましょう。

## よくあるエラーと対処法

GUI基礎の学習において遭遇しやすい典型的なエラーとその対処法を以下にまとめます。

### レイアウトマネージャーの問題

#### 問題：コンポーネントが表示されない

エラー症状。
```
JFrame frame = new JFrame("タイトル");
frame.add(new JButton("ボタン"));
frame.setVisible(true);
// ボタンが表示されない
```

原因
- `frame.setSize()`または`frame.pack()`が呼び出されていない
- レイアウトマネージャーの制約が正しく設定されていない

対処法。

<span class="listing-number">**サンプルコード18-65**</span>

```java
JFrame frame = new JFrame("タイトル");
frame.setSize(300, 200);  // サイズを明示的に設定
frame.add(new JButton("ボタン"));
frame.setVisible(true);

// または
frame.pack();  // 推奨サイズに自動調整
```

#### 問題：BorderLayoutで複数のコンポーネントが重なる

エラー症状。

<span class="listing-number">**サンプルコード18-66**</span>

```java
frame.setLayout(new BorderLayout());
frame.add(new JButton("ボタン1"));
frame.add(new JButton("ボタン2"));
// ボタン1が見えない
```

原因
- BorderLayoutの位置指定が省略されている
- 同じ位置に複数のコンポーネントが配置されている

対処法。

<span class="listing-number">**サンプルコード18-67**</span>

```java
frame.setLayout(new BorderLayout());
frame.add(new JButton("ボタン1"), BorderLayout.NORTH);
frame.add(new JButton("ボタン2"), BorderLayout.SOUTH);
```

### コンポーネントの表示問題

#### 問題：JPanelの背景色が反映されない

エラー症状。

<span class="listing-number">**サンプルコード18-68**</span>

```java
JPanel panel = new JPanel();
panel.setBackground(Color.RED);
// 背景色が表示されない
```

原因
- `setOpaque(true)`が設定されていない
- 親コンポーネントの背景色が優先されている

対処法。

<span class="listing-number">**サンプルコード18-69**</span>

```java
JPanel panel = new JPanel();
panel.setBackground(Color.RED);
panel.setOpaque(true);  // 不透明に設定
```

#### 問題：JTextFieldが編集できない

エラー症状。

<span class="listing-number">**サンプルコード18-70**</span>

```java
JTextField textField = new JTextField("初期値");
// テキストが編集できない
```

原因
- `setEditable(false)`が設定されている
- フォーカスが取得できない状態

対処法。

<span class="listing-number">**サンプルコード18-71**</span>

```java
JTextField textField = new JTextField("初期値");
textField.setEditable(true);   // 編集可能に設定
textField.setFocusable(true);  // フォーカス取得可能に設定
```

### イベントディスパッチスレッドの問題

#### 問題：GUI更新でException in thread "AWT-EventQueue-0"が発生

エラーメッセージ。
```
Exception in thread "AWT-EventQueue-0" java.lang.NullPointerException
	at SwingApplication.updateUI(SwingApplication.java:45)
```

原因
- EDT（Event Dispatch Thread）以外からGUIコンポーネントにアクセスしている
- コンポーネントがnullのまま使用されている

対処法。

<span class="listing-number">**サンプルコード18-72**</span>

```java
// 別スレッドからGUIを更新する場合
SwingUtilities.invokeLater(() -> {
    label.setText("更新されたテキスト");
});

// コンポーネントのnullチェック
if (label != null) {
    label.setText("安全な更新");
}
```

### コンポーネントのサイズ設定

#### 問題：JTextAreaが1行しか表示されない

エラー症状。

<span class="listing-number">**サンプルコード18-73**</span>

```java
JTextArea textArea = new JTextArea();
textArea.setText("長いテキスト...");
// 1行しか見えない
```

原因
- JScrollPaneに配置されていない
- 必要なサイズが設定されていない

対処法。

<span class="listing-number">**サンプルコード18-74**</span>

```java
JTextArea textArea = new JTextArea(10, 30);  // 行数と列数を指定
textArea.setLineWrap(true);  // 行の折り返しを有効
textArea.setWrapStyleWord(true);  // 単語単位で折り返し

JScrollPane scrollPane = new JScrollPane(textArea);
frame.add(scrollPane);
```

#### 問題：JButtonのサイズが意図通りにならない

エラー症状。

<span class="listing-number">**サンプルコード18-75**</span>

```java
JButton button = new JButton("ボタン");
button.setSize(200, 100);
// サイズが反映されない
```

原因
- レイアウトマネージャーがサイズを制御している
- `setPreferredSize()`を使用していない

対処法。

<span class="listing-number">**サンプルコード18-76**</span>

```java
JButton button = new JButton("ボタン");
button.setPreferredSize(new Dimension(200, 100));

// または、レイアウトマネージャーをnullに設定
frame.setLayout(null);
button.setBounds(10, 10, 200, 100);
```

### 画面のちらつき問題

#### 問題：コンポーネントの更新時に画面がちらつく

エラー症状。

<span class="listing-number">**サンプルコード18-77**</span>

```java
// 大量のコンポーネントを動的に追加・削除する際のちらつき
panel.removeAll();
for (int i = 0; i < 100; i++) {
    panel.add(new JLabel("Item " + i));
}
panel.revalidate();
panel.repaint();
```

原因
- 個別の更新処理が画面に反映されている
- ダブルバッファリングが無効になっている

対処法。

<span class="listing-number">**サンプルコード18-78**</span>

```java
// 更新処理をバッチ化
panel.setVisible(false);  // 一時的に非表示
panel.removeAll();
for (int i = 0; i < 100; i++) {
    panel.add(new JLabel("Item " + i));
}
panel.revalidate();
panel.repaint();
panel.setVisible(true);  // 再表示

// または、SwingUtilities.invokeLaterを使用
SwingUtilities.invokeLater(() -> {
    // 更新処理
    panel.removeAll();
    // コンポーネント追加
    panel.revalidate();
    panel.repaint();
});
```

### デバッグのヒント

#### 1. コンポーネントの境界を可視化する

<span class="listing-number">**サンプルコード18-79**</span>

```java
// 境界線を追加してレイアウトを確認
component.setBorder(BorderFactory.createLineBorder(Color.RED));

// または、背景色を設定
component.setBackground(Color.YELLOW);
component.setOpaque(true);
```

#### 2. レイアウトマネージャーの確認

<span class="listing-number">**サンプルコード18-80**</span>

```java
// 現在のレイアウトマネージャーを確認
System.out.println("Layout: " + container.getLayout());

// コンポーネントのサイズ情報を確認
System.out.println("Size: " + component.getSize());
System.out.println("Preferred Size: " + component.getPreferredSize());
```

#### 3. イベントディスパッチスレッドの確認

<span class="listing-number">**サンプルコード18-81**</span>

```java
// 現在のスレッドがEDTかどうかを確認
if (SwingUtilities.isEventDispatchThread()) {
    System.out.println("EDTで実行中");
} else {
    System.out.println("EDTではないスレッドで実行中");
}
```

これらの対処法を参考に、GUI基礎の学習を進めてください。問題が解決しない場合は、エラーメッセージを詳しく確認し、最小限の再現可能なコードで問題を特定することが重要です。

