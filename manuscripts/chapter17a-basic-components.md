# Part A: 基本コンポーネントとレイアウト

## 17.1 はじめてのGUIプログラム

### Hello Swing - ウィンドウを表示してみよう

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

### ウィンドウの基本設定

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

## 17.2 基本的なコンポーネントの配置

### JLabelで文字を表示する

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

### 複数のコンポーネントを配置する

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

### 基本コンポーネントの詳細な使用例

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

## 17.3 レイアウトマネージャーの基礎

### BorderLayout - 5つの領域に配置

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

### GridLayout - 格子状に配置

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

### FlowLayout - 流れるような配置

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

## 17.4 パネルを使った複雑なレイアウト

### JPanelで階層的なレイアウトを構築

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

### レイアウトの組み合わせによる画面構築

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

### まとめ

本パートでは、Swingを使った基本的なGUIプログラミングについて学習しました：

1. **JFrame**を使ったウィンドウの作成と基本設定
2. **JLabel**、**JButton**、**JTextField**などの基本コンポーネントの使用方法
3. **BorderLayout**、**GridLayout**、**FlowLayout**などのレイアウトマネージャーの特徴
4. **JPanel**を使った階層的なレイアウトの構築

これらの基礎知識により、シンプルなGUIアプリケーションの画面を構築できるようになりました。次のパートでは、これらのコンポーネントにユーザーの操作に応答する機能を追加する方法を学習します。