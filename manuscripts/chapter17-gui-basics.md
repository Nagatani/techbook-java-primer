# <b>17章</b> <span>GUIプログラミングの基礎</span> <small>Swingで作るデスクトップアプリ</small>

## 本章の学習目標

### この章で学ぶこと

1. GUIプログラミングの基本
    - Swingフレームワークの構成とコンポーネントの階層構造
    - `SwingUtilities.invokeLater()`でGUIを組み立てる定型
2. 基本的なGUIコンポーネント
    - JFrame、JLabel、JButton、JTextFieldなどの使い方
    - テキスト入力と選択のためのコンポーネント
3. レイアウトマネージャー
    - BorderLayout、FlowLayout、GridLayoutの特徴
    - 画面サイズ変更への柔軟な対応
    - JPanelを入れ子にしたレイアウトの設計
4. 独自の描画
    - `paintComponent()`のオーバーライドによるカスタムコンポーネントの作成

本章では、画面を組み立てる方法に集中します。
ボタンのクリックなどの操作に応答するイベント処理は、第18章「基本的なイベント処理」で学びます。

### この章を始める前に

第16章までのJavaプログラミングの知識、とくにクラスの継承（第5章）とインターフェイス（第7章）を理解していれば準備完了です。
GUIを組み立てる定型では、第11章で学んだラムダ式を使います。

## 基本コンポーネントとレイアウト

### はじめてのGUIプログラム

#### Hello Swing - ウィンドウを表示してみよう

まずはもっとも基本的なGUIプログラムから始めましょう。以下は、単純なウィンドウを表示するプログラムです。


<span class="listing-number">**サンプルコード17-1**　HelloSwing.java</span>

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

実行結果：
このプログラムを実行すると、タイトルバーに「はじめてのSwingアプリケーション」と表示された400×300ピクセルのウィンドウが画面中央に表示されます。

このプログラムは、①〜⑤の順にウィンドウを準備して表示します。
各行を詳しく見ていきましょう。

①では、Swingでウィンドウを表す`JFrame`クラスのオブジェクトを作り、タイトルバーに表示する文字列を指定します。
`JFrame`は、すべてのSwingのGUIアプリケーションの土台になるクラスです。
この時点では、ウィンドウはまだ画面に表示されません。

②の`setSize()`は、ウィンドウの幅と高さをピクセル単位で指定します。
ここでは横400ピクセル、縦300ピクセルです。
この大きさは、タイトルバーやウィンドウの枠を含む全体の大きさです。

③の`setDefaultCloseOperation()`は、ユーザーがウィンドウの×ボタンをクリックしたときの動作を決めます。
`EXIT_ON_CLOSE`を指定すると、ウィンドウを閉じると同時にJavaアプリケーション全体が終了します。

④の`setLocationRelativeTo(null)`は、ウィンドウの位置を決めます。
引数に`null`を渡すと、画面全体を基準にして、ウィンドウを画面の中央に置きます。

最後に⑤の`setVisible(true)`を呼ぶと、ここまでの設定を反映したウィンドウが画面に表示されます。
この呼び出しを忘れると、ウィンドウは表示されないままです。

#### GUIの組み立てはEDTに任せる

サンプルコード17-1はこのままでも動きますが、Swingには守るべき決まりがあります。
画面の部品は、イベントディスパッチスレッド（EDT）というSwing専用のスレッドから操作する、という決まりです。
`main`メソッドを実行しているのはmainスレッドで、EDTではありません。
そこで、GUIを組み立てる処理を`SwingUtilities.invokeLater()`に渡して、EDTで実行してもらいます。

<span class="listing-number">**サンプルコード17-2**　EDTBasicExample.java</span>

```java
import javax.swing.*;

public class EDTBasicExample {
    public static void main(String[] args) {
        // GUIの組み立てをEDT（イベントディスパッチスレッド）に任せる
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    // 画面を組み立てて表示する（EDT上で呼び出される）
    private static void createAndShowGUI() {
        JFrame frame = new JFrame("EDTでGUIを組み立てる");
        frame.setSize(300, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel label = new JLabel("このGUIはEDT上で作成されました", JLabel.CENTER);
        frame.add(label);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
```

実行結果：
このプログラムを実行すると、「このGUIはEDT上で作成されました」というメッセージが中央に表示されたウィンドウが現れます。

`invokeLater()`に渡している`() -> createAndShowGUI()`は、第11章で学んだラムダ式です。
「`createAndShowGUI()`を呼び出す」という処理をEDTに渡し、EDTはその処理を順番が来たときに実行します。
`main`メソッドには決まった形の1行だけを書き、画面を組み立てるコードは`createAndShowGUI()`にまとめます。
本章のこれ以降のサンプルも、この形で書きます。
EDTの役割と、Swingがこの決まりを設けている理由は、第18章「基本的なイベント処理」で詳しく学びます。

#### ウィンドウの基本設定

`JFrame`オブジェクトのメソッドを呼び出すことで、ウィンドウのさまざまな設定が可能です。

ウィンドウの詳細設定例。

このコードは、ウィンドウの外観や動作をカスタマイズする方法を示しています。各設定はユーザーエクスペリエンスに直接影響します。

基本的なウィンドウ。

JFrameクラスは、Swingアプリケーションの基本となるトップレベルコンテナーです。すべてのGUIアプリケーションは、少なくとも1つのJFrameを持ちます。
JFrameは、タイトルバー、境界線、メニューバー、およびコンテンツペインを含む完全なウィンドウフレームを提供します。

ウィンドウの設定では、サイズ、位置、動作などをカスタマイズできます。とくに重要なのは、setDefaultCloseOperation()メソッドで、ウィンドウの閉じるボタンがクリックされたときの動作を定義します。

<span class="listing-number">**サンプルコード17-3**　WindowSettings.java</span>

```java
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class WindowSettings {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    private static void createAndShowGUI() {
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

実行結果：
このプログラムを実行すると、「カスタマイズされたウィンドウ」というタイトルの500×400ピクセルのウィンドウが、画面左上から横100ピクセル、縦50ピクセルの位置に表示されます。ウィンドウのサイズは変更できないように設定されています。

各設定オプションの説明。
- ① タイトルバー文字 - ウィンドウ上部に表示されるアプリケーション名
- ② ウィンドウサイズ - 幅500ピクセル、高さ400ピクセルに固定
- ③ リサイズ禁止 - ユーザーがウィンドウサイズを変更できないように制限
- ④ 絶対位置指定 - スクリーン左上から横100px、縦50pxの位置に配置
- ⑤ 終了動作 - ×ボタンクリック時にアプリケーションを完全終了
- ⑥ 表示実行 - ウィンドウをスクリーンに表示

### 基本的なコンポーネントの配置

<div class="figure">

![Swingコンポーネントの継承階層](images/diagrams/chapter17-swing-hierarchy.svg)

</div>

#### JLabelで文字を表示する

ボタンとラベル。

GUIアプリケーションの基本要素であるボタンとラベルは、ユーザーインターフェイスの中核をなすコンポーネントです。
JLabelはテキストやアイコンを表示するための読み取り専用コンポーネントで、JButtonはユーザーのクリック操作を受け付けるインタラクティブなコンポーネントです。

JLabelの特徴。
- テキスト、HTML、アイコンを表示可能
- テキストの配置を水平・垂直方向で細かく制御
- 他のコンポーネントのラベルとして使用可能

ウィンドウに文字を表示してみましょう。

以下のコードは、JLabelを使ってウィンドウにテキストを表示する例です。JLabelは最も基本的なGUIコンポーネントの1つで、静的なテキストやアイコンを表示するために使用されます。この例では、ラベルを中央揃えに設定し、JFrameのコンテンツペインに追加しています。

<span class="listing-number">**サンプルコード17-4**　HelloLabel.java</span>

```java
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

public class HelloLabel {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    private static void createAndShowGUI() {
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

実行結果：
このプログラムを実行すると、「ラベルの表示」というタイトルの400×300ピクセルのウィンドウが画面中央に表示され、そのなかに「Hello, Swing!」というテキストが中央揃えで表示されます。

#### 複数のコンポーネントを配置する

複数のコンポーネントを配置するには、レイアウトマネージャーを使います。

レイアウトマネージャーは、コンテナー内のコンポーネントの配置を自動的に管理する仕組みです。
ウィンドウサイズの変更や異なる画面解像度に対応するため、絶対位置ではなく相対的な配置ルールを使います。

レイアウトマネージャーは、各コンポーネントの推奨サイズを考慮して配置を決めます。
そのため、異なるOSでも同じようにレイアウトでき、画面の文字サイズやDPIが変わってもそれに合わせて配置できます。
また、ユーザーがウィンドウの大きさを変えると、コンポーネントを自動的に配置し直します。

BorderLayoutは、画面を5つの領域（北、南、東、西、中央）に分割してコンポーネントを配置するレイアウトマネージャーです。多くのアプリケーションで使用される基本的なレイアウトパターンです。

<span class="listing-number">**サンプルコード17-5**　MultipleComponents.java</span>

```java
import javax.swing.*;
import java.awt.*;

public class MultipleComponents {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    private static void createAndShowGUI() {
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

実行結果：
このプログラムを実行すると、400×300ピクセルのウィンドウが画面中央に表示され、「北」「南」「東」「西」「中央」の各ラベルがそれぞれの領域に配置されます。中央の領域が最も大きなスペースを占め、各境界の領域はラベルのサイズに応じて調整されます。

①では、フレームのレイアウトマネージャーにBorderLayoutを設定しています。
JFrameのデフォルトのレイアウトもBorderLayoutですが、明示的に設定するとコードの意図がはっきりします。

②では、`add()`の第2引数で領域を指定して、5つのラベルを配置しています。
NORTHとSOUTHのラベルは横方向にいっぱいまで広がり、高さはラベルの推奨サイズのままです。
EASTとWESTのラベルは縦方向にいっぱいまで広がり、幅は推奨サイズのままです。
CENTERのラベルは残りのスペースをすべて占めるので、ウィンドウの大きさを変えたときにもっとも大きく伸び縮みします。

この配置方法により、典型的なアプリケーション構造（ツールバー・メインエリア・ステータスバー）を簡単に実現できます。

#### 基本コンポーネントの詳細な使用例

#### JLabelとアイコンの活用

以下のコードは、JLabelの高度な機能を示す例です。JLabelはHTMLタグをサポートしており、太字、斜体、色付き文字などのリッチテキストを表示できます。また、複数行のテキストもHTMLのbrタグを使用して簡単に実現できます。これらの機能により、単純なテキスト表示を超えた豊かな表現が可能になります。

<span class="listing-number">**サンプルコード17-6**　AdvancedLabel.java</span>

```java
import javax.swing.*;
import java.awt.*;

public class AdvancedLabel {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    private static void createAndShowGUI() {
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

実行結果：
このプログラムを実行すると、3つのラベルが横に並んで表示されます。最初のラベルは通常のテキスト、2番目のラベルは太字・斜体・赤色の装飾されたテキスト、3番目のラベルは3行に分かれた複数行テキストが表示されます。

#### JButtonの高度な使用

以下のコードは、JButtonのさまざまなカスタマイズ方法を示しています。ボタンの背景色と前景色の変更、サイズの調整、無効化状態の設定など、ユーザーインターフェイスの要求に応じた外観と動作のカスタマイズが可能です。これらの技術を組み合わせることで、アプリケーションの統一感のあるデザインを実現できます。

<span class="listing-number">**サンプルコード17-7**　AdvancedButton.java</span>

```java
import javax.swing.*;
import java.awt.*;

public class AdvancedButton {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    private static void createAndShowGUI() {
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

実行結果：
このプログラムを実行すると、4つの異なるスタイルのボタンが表示されます。通常のボタン、青い背景に白い文字の色付きボタン、150×50ピクセルの大きなボタン、グレーアウトされたクリックできない無効化ボタンが並んで表示されます。

#### テキスト入力コンポーネント

テキストフィールド。

テキスト入力はGUIアプリケーションの基本的な入力手段です。Swingはさまざまなテキスト入力コンポーネントを提供しており、用途に応じて選択できます。

1. JTextField: 一行テキスト入力（名前、メールアドレスなど）
2. JPasswordField: パスワード入力（文字がマスクされる）
3. JTextArea: 複数行テキスト入力（コメント、説明文など）
4. JFormattedTextField: 書式制限付き入力（日付、数値など）

以下のコードは、Swingが提供するさまざまなテキスト入力コンポーネントの実装例です。JTextField、JPasswordField、JTextAreaなどの基本的なテキスト入力に加え、JSpinnerやJSliderなどの数値入力専用コンポーネントも示しています。各コンポーネントは特定の用途に最適化されており、適切に選択することでユーザビリティの高いインターフェイスを構築できます。

<span class="listing-number">**サンプルコード17-8**　TextInputComponents.java（4、9、15〜45、49〜50行目）</span>

```java
public class TextInputComponents {
    // ...
    private static void createAndShowGUI() {
        // ...
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
        // ...
    }
}
```

実行結果：
このプログラムを実行すると、5行2列のグリッドレイアウトでさまざまな入力コンポーネントが表示されます。一行テキストフィールドには「初期テキスト」が表示され、パスワードフィールドは入力文字がマスクされます。複数行テキストエリアにはスクロールバーが付き、数値スピナーは0から100まで調整でき、スライダーは25刻みの目盛り付きで表示されます。

#### 選択コンポーネント

チェックボックス。

チェックボックスは、複数の選択肢から任意の組み合わせを選択できるコンポーネントです。各項目は独立しており、オン/オフを自由に切り替えられます。設定画面のオプション選択や、アンケートの複数回答などに適しています。

ラジオボタン。

ラジオボタンは、複数の選択肢から1つだけを選択する場合に使用します。ButtonGroupにグループ化することで、同一グループ内では1つのラジオボタンしか選択できなくなります。

コンボボックス。

コンボボックス（ドロップダウンリスト）は、限られたスペースで多くの選択肢を提供する場合に有効です。通常は1つの項目のみ表示され、クリックするとリストが展開されます。

リスト。

JListは、複数の項目を一度に表示し、単一または複数選択を可能にするコンポーネントです。スクロール機能を追加することで、大量のデータも扱えます。

以下のコードは、チェックボックス、ラジオボタン、コンボボックス、リストなどの選択系コンポーネントの実装例です。これらのコンポーネントは、ユーザーが選択肢から項目を選ぶための重要なインターフェイス要素です。とくにButtonGroupを使ったラジオボタンのグループ化は、相互排他的な選択を実現する標準的な手法です。

<span class="listing-number">**サンプルコード17-9**　SelectionComponents.java（4、9、15〜48、52〜53行目）</span>

```java
public class SelectionComponents {
    // ...
    private static void createAndShowGUI() {
        // ...
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
        // ...
    }
}
```

実行結果：
このプログラムを実行すると、4種類の選択コンポーネントが表示されます。チェックボックスでは「オプション2」が初期選択され、複数選択が可能です。ラジオボタンでは「選択肢B」が選択され、どちらか一方のみ選択できます。コンボボックスはドロップダウンリストから1つを選択でき、リストボックスは5つのアイテムから1つを選択できるように設定されています。

`JList`の項目を入れておくモデルや、項目の表示方法を変えるレンダラーなど、`JList`の詳しい使い方は第20章「JTableとJListの基礎」で学びます。

### レイアウトマネージャーの基礎

#### BorderLayout - 5つの領域に配置

BorderLayoutは、サンプルコード17-5で使ったレイアウトマネージャーで、JFrameのデフォルトのレイアウトでもあります。
ウィンドウを方位に基づいた5つの領域に分け、各領域はサンプルコード17-5の解説で述べたとおりに広がります。
5つの領域の位置関係は、次の図のとおりです。

<div class="figure">

![BorderLayoutの5つの領域](images/diagrams/chapter17-borderlayout.svg)

</div>

以下のコードは、BorderLayoutを使用した基本的なレイアウトの例です。各領域にボタンを配置することで、BorderLayoutの動作を視覚的に理解できます。ウィンドウサイズを変更すると、CENTER領域が伸縮し、他の領域はコンポーネントの推奨サイズに応じた動作をします。

<span class="listing-number">**サンプルコード17-10**　BorderLayoutExample.java</span>

```java
import javax.swing.*;
import java.awt.*;

public class BorderLayoutExample {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    private static void createAndShowGUI() {
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

実行結果：
このプログラムを実行すると、5つのボタンがウィンドウの各領域に配置されます。北と南のボタンは水平方向に伸び、東と西のボタンは垂直方向に伸び、中央のボタンは残りのすべての領域を占めます。ウィンドウのサイズを変更すると、中央の領域が伸縮します。

#### GridLayout - 格子状に配置

GridLayoutの例。

GridLayoutは、コンポーネントを同じサイズのセルに均等に配置するレイアウトマネージャーです。電卓のボタン配置や、カレンダーの日付表示など、規則的な配置が必要な場合に最適です。

GridLayoutの特徴。
- すべてのセルが同じサイズに統一される
- コンポーネントは左から右、上から下へ順番に配置
- ウィンドウサイズに応じてセルサイズが自動調整
- セル間の間隔をピクセル単位で指定可能

<div class="figure">

![GridLayout（2行3列の例）](images/diagrams/chapter17-gridlayout.svg)

</div>

`GridLayout`は、コンポーネントを格子状（行と列）に配置します。

以下のコードは、GridLayoutを使用してボタンを格子状に配置する例です。3行2列のグリッドを作成し、コンポーネント間の間隔を指定しています。GridLayoutは、電卓のボタン配置やカレンダーの日付表示など、規則的なレイアウトが必要な場面で非常に有効です。

<span class="listing-number">**サンプルコード17-11**　GridLayoutExample.java</span>

```java
import javax.swing.*;
import java.awt.*;

public class GridLayoutExample {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    private static void createAndShowGUI() {
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

実行結果：
このプログラムを実行すると、6つのボタンが3行2列の格子状に配置されます。すべてのボタンは同じサイズになり、ボタン間には5ピクセルの間隔が設定されます。ウィンドウのサイズを変更すると、各セルのサイズも均等に調整されます。

#### FlowLayout - 流れるような配置

FlowLayoutの例。

FlowLayoutは、コンポーネントを流れるように配置するもっともシンプルなレイアウトマネージャーです。コンポーネントはコンテナーの幅に応じて自動的に折り返されるため、ウィンドウサイズの変更に柔軟に対応します。

`FlowLayout`は、コンポーネントを左から右へ並べ、行がいっぱいになったら次の行へ送ります。
各行はデフォルトでは中央揃えですが、左揃えや右揃えも指定できます。
コンポーネントどうしの間隔は、ピクセル単位で設定できます。
コンポーネントを推奨サイズのまま並べるので、ボタンやラベルなどの小さなコンポーネントの配置に向いています。

以下のコードは、FlowLayoutを使用したボタン配置の例です。コンポーネントは追加された順番に左から右へ配置され、ウィンドウ幅に収まらない場合は自動的に次の行に折り返されます。この性質は、ウィンドウサイズの変更に柔軟に対応できるため、ツールバーなどの実装に適しています。

<span class="listing-number">**サンプルコード17-12**　FlowLayoutExample.java</span>

```java
import javax.swing.*;
import java.awt.*;

public class FlowLayoutExample {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    private static void createAndShowGUI() {
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

実行結果：
このプログラムを実行すると、6つのボタンが中央揃えで左から右へ流れるように配置されます。ウィンドウの幅を狭めると、収まらないボタンは自動的に次の行に折り返されます。各ボタン間には水平方向に10ピクセル、垂直方向に5ピクセルの間隔が設定されています。

### パネルを使った複雑なレイアウト

#### JPanelで階層的なレイアウトを構築

JPanelは、Swingでもっとも頻繁に使われるコンテナーコンポーネントの1つです。
軽量なコンテナーで、ほかのコンポーネントを整理し、グループにまとめるために使います。

複雑な画面は、JPanelを使って役割ごとの区画に分けて組み立てます。
パネルに背景色や境界線を付ければ、どのコンポーネントが同じグループかが見た目でわかります。
また、`paintComponent()`メソッドをオーバーライドして独自の描画をしたり、パネル単位でイベントリスナーを設定したりもできます。
独自の描画は本章の「カスタムコンポーネントの作成」で、イベントリスナーは第18章で学びます。

複雑なレイアウトを実現するには、`JPanel`を使って階層的にコンポーネントを組み合わせます。

以下のコードは、複数のJPanelと異なるレイアウトマネージャーを組み合わせて、テキストエディター風のアプリケーションインターフェイスを構築する例です。ツールバー、サイドバー、メインエリア、ステータスバーといった一般的なアプリケーション構造を、BorderLayoutとFlowLayoutを巧みに組み合わせて実現しています。

<span class="listing-number">**サンプルコード17-13**　ComplexLayoutExample.java（4、9〜13、27〜49、59〜62行目）</span>

```java
public class ComplexLayoutExample {
    // ...
    private static void createAndShowGUI() {
        JFrame frame = new JFrame("複雑なレイアウトの例");
        frame.setSize(500, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        // ...
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
        // ...
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
```

実行結果：
このプログラムを実行すると、テキストエディター風のアプリケーションが表示されます。上部にツールバー（新規・開く・保存・切り取り・コピー・貼り付けボタン）、左側に「ファイル一覧」パネル（3つのファイル名のリスト）、中央に「エディター」パネル（テキスト入力エリア）、下部にステータスバー（「準備完了」と「行: 1, 列: 1」の表示）が配置されます。

#### レイアウトの組み合わせによる画面構築

レイアウトマネージャーを組み合わせることで、より柔軟な画面設計が可能です。

以下のコードは、さらに高度なレイアウトの組み合わせ例を示しています。メニューバー、ツールバー、コンテンツエリア、サイドパネルなど、実際のアプリケーションでよく使われるパターンを、複数のレイアウトマネージャーを使って構築しています。

<span class="listing-number">**サンプルコード17-14**　CombinedLayoutExample.java（4、9〜16、41〜46、63〜66、80〜88、97、101〜102行目）</span>

```java
public class CombinedLayoutExample {
    // ...
    private static void createAndShowGUI() {
        JFrame frame = new JFrame("レイアウト組み合わせの例");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        
        // 上部：メニューバーとツールバー
        JPanel topPanel = new JPanel(new BorderLayout());
        // ...
        frame.add(topPanel, BorderLayout.NORTH);
        
        // 中央：分割パネル
        JSplitPane splitPane = new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(200);
        // ...
        splitPane.setLeftComponent(leftPanel);
        
        // 右側パネル（タブ付き）
        JTabbedPane tabbedPane = new JTabbedPane();
        // ...
        splitPane.setRightComponent(tabbedPane);
        
        frame.add(splitPane, BorderLayout.CENTER);
        
        // 下部：ステータスバー
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(
            BorderFactory.createLoweredBevelBorder());
        statusBar.add(new JLabel("準備完了"), BorderLayout.WEST);
        // ...
        frame.add(statusBar, BorderLayout.SOUTH);
        // ...
    }
}
```

実行結果：
このプログラムを実行すると、IDEのような複雑なレイアウトのアプリケーションが表示されます。上部にはメニューバー（ファイル・編集メニュー）とツールバー（新規・開く・保存ボタン）が並びます。中央は左右に分割され、左側に「プロジェクト」パネル（src/test/resourcesのリストと追加・削除ボタン）、右側にタブ付きパネル（エディタータブと黒い背景のコンソールタブ）が配置されます。下部はステータスバーで、「準備完了」と「行: 1 列: 1 エンコーディング: UTF-8」が表示されます。分割バーをドラッグすると左右のパネルの幅を調整できます。

## カスタムコンポーネントの作成

### 基底クラスの選択

Swingに用意されたコンポーネントにない表示が必要なときは、既存のクラスを継承して独自のコンポーネントを作ります。
継承する基底クラスは、作りたいものに合わせて選びます。

- `JComponent`
    + 何もないところから独自の描画をするコンポーネントの基底クラス。背景は自分で塗る
- `JPanel`
    + 背景を塗る処理を持つ。図形やグラフを描く場所として、もっともよく使う
- `JButton`などの既存のコンポーネント
    + ボタンの見た目を変えるなど、既存の部品の機能を生かしたまま一部を変えるときに使う

### paintComponent()による独自の描画

コンポーネントに図形や文字を描くには、`paintComponent()`メソッドをオーバーライドします。
以下のコードは、`JPanel`を継承した`DrawingPanel`に、四角形・円・文字列を描く例です。

<span class="listing-number">**サンプルコード17-15**　DrawingPanel.java、DrawingPanelExample.java（9〜16行目）</span>

```java
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import javax.swing.JPanel;

public class DrawingPanel extends JPanel {
    public DrawingPanel() {
        setBackground(Color.WHITE);
    }

    @Override
    public Dimension getPreferredSize() {           // ①
        return new Dimension(300, 200);
    }

    @Override
    protected void paintComponent(Graphics g) {     // ②
        super.paintComponent(g);                    // ③

        g.setColor(Color.BLUE);                     // ④
        g.fillRect(20, 20, 100, 60);

        g.setColor(Color.RED);
        g.drawOval(150, 20, 120, 120);

        g.setColor(Color.BLACK);
        g.setFont(new Font("SansSerif", Font.BOLD, 16));
        g.drawString("Hello, Graphics!", 20, 170);
    }
}

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("独自の描画");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new DrawingPanel());
        frame.pack();  // パネルの推奨サイズに合わせてウィンドウの大きさを決める
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
```

実行結果：
このプログラムを実行すると、白い背景のウィンドウに、青く塗りつぶした四角形、赤い円の輪郭、「Hello, Graphics!」という黒い文字列が表示されます。

①の`getPreferredSize()`は、このコンポーネントの推奨サイズを返します。
レイアウトマネージャーや`pack()`は、この大きさをもとに配置とウィンドウの大きさを決めます。

②の`paintComponent()`は、コンポーネントを描く必要があるときにSwingが呼び出すメソッドです。
ウィンドウを最初に表示したときだけでなく、大きさを変えたときや、ほかのウィンドウに隠れたあとに再び表示されたときにも呼ばれます。
そのため、描く内容はすべて`paintComponent()`のなかに書き、呼ばれるたびに最初から描きます。
引数の`Graphics`は描画のための道具で、`setColor()`で色を選び、`fillRect()`（塗りつぶした四角形）、`drawOval()`（楕円の輪郭）、`drawString()`（文字列）などで描きます。
座標はコンポーネントの左上が原点（0, 0）で、xは右へ、yは下へ増えます。

③の`super.paintComponent(g)`は、親クラスの`JPanel`に背景を塗らせる呼び出しです。
これを省くと前の描画が消えずに残り、表示が乱れることがあります。

④以降は、色を選んでから図形や文字列を描いています。
`Graphics`は直前に選んだ色を覚えているので、色を変えたいときは描く前に`setColor()`を呼びます。

`paintComponent()`は自分で直接呼び出しません。
表示する内容を変えたときは`repaint()`を呼び、Swingに再描画を頼みます。
Swingは、頼まれた再描画をEDT上で`paintComponent()`を呼び出して行います。
マウスの操作に合わせて描き直すといった、イベントと組み合わせた描画は、第18章と第19章で学ぶイベント処理を使って実現します。

## よくあるエラーと対処法

GUIプログラムでは、例外が発生するよりも、画面が思ったとおりに表示されないという形で問題が現れることがほとんどです。本章の内容に関連して出会いやすい問題は次のとおりです。

- コンポーネントが表示されない
    + `setVisible(true)`のあとにコンポーネントを追加した場合は、`revalidate()`と`repaint()`を呼ぶ
- BorderLayoutでコンポーネントが重なる
    + 同じ領域に複数のコンポーネントを追加すると、最後のものだけが表示される
- JPanelの背景色が反映されない
    + 背景色を表示するには、`setOpaque(true)`でパネルを不透明にする
- JTextFieldが編集できない
    + `setEditable(false)`や`setEnabled(false)`になっていないかを確かめる
- イベントディスパッチスレッド（EDT）以外からの画面の更新
    + Swingのコンポーネントは、`SwingUtilities.invokeLater()`を使ってEDTから操作する
- コンポーネントのサイズが意図どおりにならない
    + `JTextArea`の行数の指定や、レイアウトマネージャーごとのサイズの扱いを確かめる
- 画面のちらつき
    + コンポーネントをひとつずつ追加・削除するたびに画面へ反映されることが原因になる。更新をまとめてから`revalidate()`と`repaint()`を呼ぶ

問題のあるコードと修正したコード、コンポーネントの境界やEDTを確かめるデバッグのヒントは、GitHubリポジトリにまとめています。<br>
`https://github.com/Nagatani/techbook-java-primer/blob/main/appendix/h-common-errors/chapters/chapter17.md`

## まとめ

本章では、Swingを使ったGUIプログラミングの基礎を学習しました。

- 画面の組み立て
    + JFrameでウィンドウを作り、JLabel、JButton、JTextFieldなどのコンポーネントを配置する
    + BorderLayout、GridLayout、FlowLayoutの特徴を理解し、JPanelを入れ子にして複雑なレイアウトを作る
- GUIの組み立て方
    + GUIの生成は`SwingUtilities.invokeLater()`でEDTに任せ、画面を組み立てるコードは1つのメソッドにまとめる
- カスタムコンポーネント
    + 目的に合った基底クラスを選び、`paintComponent()`をオーバーライドして独自の描画を実装する
    + 表示する内容を変えたら`repaint()`で再描画を頼む

次章では、ボタンのクリックやマウス・キーボードの操作に応答するイベント処理と、EDTの働きを学習します。

## 章末演習

本章の演習課題は準備中です。準備ができしだい、GitHubリポジトリの次の場所で提供します。<br>
`https://github.com/Nagatani/techbook-java-primer/tree/main/exercises/chapter17/`

それまでは、本文のサンプルコードを動かし、値や部品を変えて動きの違いを確かめてください。コンパイルできる全文はGitHubリポジトリの`sample-code/chapter17/`にあります。

本章の内容を理解できたら、第18章「基本的なイベント処理」に進みましょう。
