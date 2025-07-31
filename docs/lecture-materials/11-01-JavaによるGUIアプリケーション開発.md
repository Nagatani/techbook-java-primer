---
title: JavaによるGUIプログラミング
---

> オブジェクト指向プログラミングおよび演習1 第11回

## 第1部 GUIとイベント駆動の全体像

### 第1章 はじめに - GUIプログラミングの世界

#### 1-1. CUIからGUIへ

コンソール（黒い画面）に文字を出力し、キーボードから文字を入力するCUI（Character User Interface）はコンピュータの操作に慣れた人にとっては高速で効率的です。
しかし、ある程度の知識が求められる点から、一般的なユーザーからすると直感的ではありません。

私たちが普段使っているPCやスマートフォンのアプリケーションは、ウィンドウやボタン、アイコンなどをマウスや指で操作するGUI（Graphical User Interface）が主流です。
GUIは、そのグラフィカルな表現により、誰にでも直感的に操作できるという大きな利点があります。

#### 1-2. 身の回りのGUIアプリケーション

GUIアプリケーションは、大きく2種類に分けられます。

* デスクトップアプリケーション
    - PCにインストールして使用するソフトウェアです。たとえば、Windowsのメモ帳やエクスプローラー、macOSのテキストエディットやFinderなどがこれにあたります。OSに密着した機能を提供でき、オフラインでも動作するのが特徴です。
* Webアプリケーション
    - Webブラウザを通じて利用するソフトウェアです。Google検索やオンラインショッピングサイト、SNSなどが代表例です。
    - インストール不要で、常に最新のバージョンを利用できる利点があります。
* スマートフォン向けアプリケーション
    - デスクトップアプリケーションとほぼ同じです。

昨今では、使用しているPCの環境に左右されにくいWebアプリケーションが主流となりつつあります。
しかしWebアプリケーションの場合、Web特有の仕組みへの理解が必要となるほか、インターネットを通じたサービスの提供となると、セキュリティへの考慮も必要です。
同じアプリケーション開発でも動作させたいプラットフォームにより、開発の容易さが大きく異なったり、それぞれ得手不得手などがあるため事前に調査はしておきましょう。

#### 1-3. イベント駆動とは何か？

CUIのプログラムの多くは、処理の順番をプログラマが記述する「手続き型」でした。
しかし、GUIアプリケーションはユーザーがいつ、どのボタンを押すか分かりません。そのため、「ユーザーの操作（**イベント**）をきっかけに、特定の処理（**リスナー**）が動く」という**イベント駆動（Event-Driven）** というモデルを採用しています。

イベント駆動は、「**何かが起きたら、これを実行する**」というルールの集合でプログラムを構築する考え方です。

#### 1-4. JavaにおけるGUI実現技術の紹介

JavaでGUIを実現する技術には、いくつかの選択肢があります。

* デスクトップ
    * AWT（Abstract Window Toolkit）
        - Javaの初期から提供されている、OSのGUI部品を利用するライブラリです。
    * Swing
        - AWTを拡張し、Java自身がGUI部品を描画するライブラリです。
        - OSに依存しない統一された見た目（Look & Feel）を提供できます。長年の実績があり、安定しています。
    * JavaFX
        - Swingの後継として開発された、よりモダンで表現力豊かなGUIライブラリです。
        - 昨今では標準ライブラリから外れ、OSSとして別の企業が開発を継続中です。
    * SWT（Standard Widget Toolkit）
        - GUIの各コントロールの描写にJavaVMを使用せず、OSのAPI（WindowsであればWin32Api）を呼び出すようにパイプ処理を行うライブラリです。
        - JavaでネイティブなGUIアプリケーションを開発することができる反面、その環境に向けてのコードを書かなければならず汎用性はない。
* Webアプリケーション
    - JavaでWebアプリケーションを開発する場合、**Servlet/JSP**や、**Spring Framework**などのフレームワークが利用されます。これらはHTML、CSS、JavaScriptと連携してブラウザ上にGUIを構築します。
    - 本講義では扱いませんが、デスクトップGUIのイベント駆動の考え方は、Webアプリケーション開発にも通じる重要な概念です。

## 第2部 SwingによるGUI画面の作成
### 第2章 最初のウィンドウ - Swingプログラミングの第一歩
#### 2-1. プロジェクトの準備とJFrame

SwingでGUIアプリケーションを作成するには、まず土台となるウィンドウが必要です。`javax.swing.JFrame`クラスがその役割を担います。

```java
import javax.swing.JFrame;

public class MyFirstFrame {
    public static void main(String[] args) {
        // JFrameクラスのインスタンスを作成します。これがウィンドウの実体です。
        JFrame frame = new JFrame();

        // ウィンドウのタイトルを設定します。
        frame.setTitle("はじめてのSwing");

        // ウィンドウのサイズをピクセル単位で設定します。
        frame.setSize(400, 300);

        // ウィンドウの閉じるボタン（×ボタン）をクリックしたときの動作を設定します。
        // JFrame.EXIT_ON_CLOSE を設定すると、ウィンドウを閉じたときにプログラムが終了します。
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // ウィンドウを画面中央に表示するように設定します。
        frame.setLocationRelativeTo(null);

        // 設定したウィンドウを画面に表示します。この命令がないとウィンドウは表示されません。
        frame.setVisible(true);
    }
}
```

**補足:** 上記のコードを実行すると、タイトルが「はじめてのSwing」である400x300サイズの空のウィンドウが画面中央に表示されます。

#### 2-2. ウィンドウの基本設定

前節のコードで示したように、`JFrame`オブジェクトのメソッドを呼び出すことで、ウィンドウのさまざまな設定が可能です。

  * `setTitle(String title)`: ウィンドウのタイトルバーに表示されるテキストを設定します。
  * `setSize(int width, int height)`: ウィンドウの幅と高さをピクセル単位で設定します。
  * `setDefaultCloseOperation(int operation)`: ウィンドウを閉じるボタンの動作を定義します。`JFrame.EXIT_ON_CLOSE`がもっとも一般的に使用されます。
  * `setLocationRelativeTo(Component c)`: ウィンドウの表示位置を決定します。引数に`null`を指定すると、画面中央に配置されます。
  * `setVisible(boolean b)`: ウィンドウを表示（`true`）または非表示（`false`）にします。プログラムの最後に`true`で呼び出すのが一般的です。

### 第3章 画面を構成する基本コンポーネント

`JFrame`という土台の上に、さまざまな部品（コンポーネント）を配置して画面を構築します。

#### 3-1. `JLabel` - テキストや画像を表示する

`JLabel`は、ユーザーが編集できないテキストや画像を表示するためのコンポーネントです。

```java
import javax.swing.JFrame;
import javax.swing.JLabel;

public class JLabelExample {
    public static void main(String[] args) {
        JFrame frame = new JFrame("JLabel Example");
        frame.setSize(300, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // この時点では、まだコンポーネントを自由に配置できません。
        // レイアウトマネージャを一旦無効にして、座標指定で配置します（後ほど詳しく解説します）。
        frame.setLayout(null);

        // "Hello, World!"というテキストを持つJLabelを作成します。
        JLabel label = new JLabel("Hello, World!");

        // labelの表示位置とサイズを設定します (x座標, y座標, 幅, 高さ)。
        label.setBounds(50, 50, 100, 30);

        // フレームにラベルを追加します。
        frame.add(label);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
```

**補足:** `setLayout(null)`は、レイアウト管理を無効にし、`setBounds`メソッドでコンポーネントの絶対座標とサイズを指定する方法です。柔軟性に欠けるため通常は推奨されませんが、単純な配置を学ぶ最初のステップとして用います。

#### 3-2. `JButton` - アクションの起点となるボタン

`JButton`は、ユーザーがクリックすることで何らかのアクションを発生させるためのボタンです。

```java
import javax.swing.JButton;
import javax.swing.JFrame;

public class JButtonExample {
    public static void main(String[] args) {
        JFrame frame = new JFrame("JButton Example");
        frame.setSize(300, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        // "クリックしてください"というテキストを持つJButtonを作成します。
        JButton button = new JButton("クリックしてください");

        // ボタンの表示位置とサイズを設定します。
        button.setBounds(50, 50, 150, 30);

        // フレームにボタンを追加します。
        frame.add(button);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
```

#### 3-3. `JTextField` - 1行のテキスト入力

`JTextField`は、ユーザーに1行のテキスト入力を促すためのコンポーネントです。

```java
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class JTextFieldExample {
    public static void main(String[] args) {
        JFrame frame = new JFrame("JTextField Example");
        frame.setSize(400, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        JLabel label = new JLabel("名前:");
        label.setBounds(50, 50, 50, 30);
        frame.add(label);

        // 20文字分の幅を持つ空のテキストフィールドを作成します。
        JTextField textField = new JTextField(20);

        // テキストフィールドの表示位置とサイズを設定します。
        textField.setBounds(110, 50, 200, 30);

        // テキストフィールドに初期テキストを設定することもできます。
        // textField.setText("初期値");
        
        frame.add(textField);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
```

#### 3-4. `JTextArea` - 複数行のテキスト入力

`JTextArea`は、複数行のテキスト入力や表示に使用します。長い文章を扱う場合、`JScrollPane`と組み合わせるのが一般的です。

```java
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class JTextAreaExample {
    public static void main(String[] args) {
        JFrame frame = new JFrame("JTextArea Example");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        // 10行x30列のサイズを持つテキストエリアを作成します。
        JTextArea textArea = new JTextArea(10, 30);
        // 初期テキストを設定します。\n で改行できます。
        textArea.setText("ここに複数行のテキストを入力できます。\n");

        // JTextAreaは自動でスクロールバーを表示しないため、JScrollPaneでラップします。
        // これにより、テキストがエリアのサイズを超えたときにスクロールバーが自動で表示されます。
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBounds(50, 50, 300, 150);
        
        frame.add(scrollPane);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
```

#### 3-5. `JCheckBox` - ON/OFFの選択肢

`JCheckBox`は、複数の選択肢から任意個の項目を選択する場合に使用します。

```java
import javax.swing.JCheckBox;
import javax.swing.JFrame;

public class JCheckBoxExample {
    public static void main(String[] args) {
        JFrame frame = new JFrame("JCheckBox Example");
        frame.setSize(300, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        // チェックボックスを作成します。
        JCheckBox checkBox1 = new JCheckBox("Eメールで通知を受け取る");
        checkBox1.setBounds(50, 50, 200, 30);
        // デフォルトで選択状態にすることもできます。
        checkBox1.setSelected(true);

        JCheckBox checkBox2 = new JCheckBox("利用規約に同意する");
        checkBox2.setBounds(50, 90, 200, 30);
        
        frame.add(checkBox1);
        frame.add(checkBox2);
        
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
```

#### 3-6. `JRadioButton` - 1つだけの選択肢

`JRadioButton`は、複数の選択肢から1つだけを選択させたい場合に使用します。`ButtonGroup`でグループ化することが必須です。

```java
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JRadioButton;

public class JRadioButtonExample {
    public static void main(String[] args) {
        JFrame frame = new JFrame("JRadioButton Example");
        frame.setSize(300, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        // ラジオボタンを作成します。
        JRadioButton radio1 = new JRadioButton("男性");
        radio1.setBounds(50, 50, 80, 30);
        radio1.setSelected(true); // デフォルト選択

        JRadioButton radio2 = new JRadioButton("女性");
        radio2.setBounds(140, 50, 80, 30);

        // ButtonGroupを作成し、ラジオボタンを追加します。
        // これにより、このグループ内で1つしか選択できないようになります。
        ButtonGroup group = new ButtonGroup();
        group.add(radio1);
        group.add(radio2);
        
        frame.add(radio1);
        frame.add(radio2);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
```

### 第4章 コンポーネントの配置とレイアウト管理

`setLayout(null)`による絶対座標指定は、ウィンドウサイズが変わるとレイアウトが崩れるため、実用的ではありません。Swingでは**レイアウトマネージャ**を使い、コンポーネントを自動で配置します。

#### 4-1. なぜレイアウト管理が重要か

レイアウトマネージャを使うと、ウィンドウのサイズが変更されても、コンポーネントの位置やサイズがルールにしたがって自動的に調整されます。これにより、どのような環境でも意図したレイアウトを維持できます。

#### 4-2. コンテナとしての`JPanel`

`JPanel`は、コンポーネントをグループ化するための「透明なパネル」のようなものです。`JFrame`に直接部品を配置するのではなく、`JPanel`上に部品を配置し、その`JPanel`を`JFrame`に配置することで、複雑なレイアウトを構築できます。

#### 4-3. 代表的なレイアウトマネージャ

##### `FlowLayout`

コンポーネントを左から右へ、行が埋まると次の行へと、流れるように配置します。`JPanel`のデフォルトのレイアウトマネージャです。

```java
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class FlowLayoutExample {
    public static void main(String[] args) {
        JFrame frame = new JFrame("FlowLayout Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // JPanelを作成します。JPanelはデフォルトでFlowLayoutです。
        JPanel panel = new JPanel();
        // panel.setLayout(new FlowLayout()); // 明示的に設定することもできます。

        // ボタンを5つ作成し、パネルに追加します。
        panel.add(new JButton("ボタン1"));
        panel.add(new JButton("ボタン2"));
        panel.add(new JButton("ボタン3"));
        panel.add(new JButton("非常に長い名前のボタン4"));
        panel.add(new JButton("ボタン5"));
        
        // パネルをフレームに追加します。
        frame.add(panel);

        // フレーム内のコンポーネントが適切なサイズになるように調整します。
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
```

**補足:** `frame.pack()`は、フレーム内のコンポーネントがちょうど収まるように、フレームのサイズを自動調整する便利なメソッドです。

##### `BorderLayout`

コンテナを「North」「South」「East」「West」「Center」の5つの領域に分割して配置します。`JFrame`のデフォルトのレイアウトマネージャです。

```java
import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JFrame;

public class BorderLayoutExample {
    public static void main(String[] args) {
        JFrame frame = new JFrame("BorderLayout Example");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // JFrameはデフォルトでBorderLayoutなので、 setLayout の呼び出しは不要です。
        
        // 領域を指定してコンポーネントを追加します。
        frame.add(new JButton("North (上)"), BorderLayout.NORTH);
        frame.add(new JButton("South (下)"), BorderLayout.SOUTH);
        frame.add(new JButton("West (左)"), BorderLayout.WEST);
        frame.add(new JButton("East (右)"), BorderLayout.EAST);
        frame.add(new JButton("Center (中央)"), BorderLayout.CENTER);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
```

##### `GridLayout`

コンテナを格子状（マス目）に分割し、各セルにコンポーネントを1つずつ同じサイズで配置します。

```java
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JFrame;

public class GridLayoutExample {
    public static void main(String[] args) {
        JFrame frame = new JFrame("GridLayout Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 3行2列のGridLayoutを設定します。
        frame.setLayout(new GridLayout(3, 2));

        // コンポーネントを追加すると、左上から右へ、次に下の行へと配置されます。
        frame.add(new JButton("1"));
        frame.add(new JButton("2"));
        frame.add(new JButton("3"));
        frame.add(new JButton("4"));
        frame.add(new JButton("5"));
        frame.add(new JButton("6"));
        
        frame.setSize(300, 200);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
```

#### 4-4. レイアウトの組み合わせ

`JPanel`を入れ子（ネスト）にすることで、複数のレイアウトマネージャを組み合わせた複雑な画面を構築できます。たとえば、「上部に入力フォーム、中央に結果表示エリア、下部にボタン群」といったレイアウトが実現可能です。

```java
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ComplexLayoutExample {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Complex Layout");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        // フレームのレイアウトはBorderLayout（デフォルト）のままにします。

        // --- 上部パネル (FlowLayout) ---
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT)); // 左寄せのFlowLayout
        topPanel.add(new JTextField(25));
        topPanel.add(new JButton("検索"));
        
        // --- 中央のテキストエリア ---
        // JScrollPaneでラップするのを忘れないようにします。
        JTextArea centerTextArea = new JTextArea();
        
        // --- 下部パネル (FlowLayout) ---
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT)); // 右寄せのFlowLayout
        bottomPanel.add(new JButton("OK"));
        bottomPanel.add(new JButton("キャンセル"));

        // --- フレームに各パーツを配置 ---
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(new JScrollPane(centerTextArea), BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
```

## 第3部 イベント駆動プログラミングの実装

### 第5章 イベント処理の基本メカニズム

GUIアプリケーションを「動かす」ためには、イベント処理の実装が不可欠です。

#### 5-1. イベント駆動の3要素

Swingのイベント処理は、以下の3つの要素で構成されます。

1.  **イベントソース:** イベントを発生させるコンポーネント（例: `JButton`, `JTextField`）。
2.  **イベントオブジェクト:** 発生したイベントの詳細情報を持つオブジェクト（例: `ActionEvent`はボタンがクリックされたことを示す）。
3.  **イベントリスナー:** イベントを監視し、イベントが発生したときに実行される処理を記述したオブジェクト（例: `ActionListener`）。

処理の流れは以下の通りです。

<div class="mermaid" style="margin:auto;">
graph TD;
    A["ユーザー操作<br>(例: ボタンクリック)"] --> B["イベントソース<br>(例: JButton)"];
    B --> C["イベントオブジェクトを生成<br>(例: ActionEvent)"];
    C --> D["イベントリスナーに通知<br>(登録済みのActionListener)"];
    D --> E["リスナー内の処理が実行される<br>(例: actionPerformedメソッド)"];
</div>

#### 5-2. リスナーの登録

イベントソースに「このイベントが起きたら、このリスナーを呼び出してください」とお願いすることを「リスナーの登録」と呼びます。`addXXXListener`という形式のメソッド（例: `addActionListener`）を使用します。

#### 5-3. ボタンクリックに応答する

もっとも基本的なイベント処理である、ボタンクリックの実装を見てみましょう。`ActionListener`インターフェイスを実装します。

```java
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;

public class ButtonEventExample {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Button Event Example");
        frame.setSize(300, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout());

        JButton button = new JButton("クリックしてね");

        // イベントリスナーを作成し、ボタンに登録します。
        // ここでは「匿名クラス」という手法で、その場限りのクラスを定義しています。
        button.addActionListener(new ActionListener() {
            // ボタンがクリックされたときに、この actionPerformed メソッドが呼び出されます。
            @Override
            public void actionPerformed(ActionEvent e) {
                // コンソールにメッセージを出力します。
                System.out.println("ボタンがクリックされました！");
            }
        });

        frame.add(button);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
```

**補足:** 匿名クラスは、特定の場所で一度しか使わないインターフェイスの実装やクラスの継承を行う際に便利な記法です。

### 第6章 さまざまなコンポーネントのイベント処理

#### 6-1. テキストフィールドの値を利用する

ボタンが押されたタイミングで、`JTextField`に入力されているテキストを取得してみましょう。

```java
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class TextFieldEventExample {
    public static void main(String[] args) {
        JFrame frame = new JFrame("TextField Event");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout());

        JTextField textField = new JTextField(15);
        JButton button = new JButton("表示");
        JLabel label = new JLabel("ここに結果が表示されます");

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // テキストフィールドから入力された文字列を取得します。
                String inputText = textField.getText();
                // ラベルにその文字列を設定します。
                label.setText("こんにちは、" + inputText + "さん！");
            }
        });

        frame.add(textField);
        frame.add(button);
        frame.add(label);
        
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
```

#### 6-2. 選択状態に応じた処理

`JCheckBox`や`JRadioButton`の選択状態を取得するには、`isSelected()`メソッドを使います。

```java
// ... JFrameやコンポーネントの準備は省略 ...
JButton checkButton = new JButton("状態確認");
JCheckBox checkBox = new JCheckBox("同意する");
JLabel resultLabel = new JLabel("結果");

checkButton.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        // isSelected() は boolean 値 (true または false) を返します。
        if (checkBox.isSelected()) {
            resultLabel.setText("同意済みです。");
        } else {
            resultLabel.setText("まだ同意されていません。");
        }
    }
});
```

#### 6-3. リスナー実装の現代的な方法：ラムダ式

ラムダ式を使うと、匿名クラスよりも簡潔にイベントリスナーを記述できます。`ActionListener`のように、実装すべきメソッドが1つだけのインターフェイス（関数型インターフェイス）に対して使用できます。

先ほどのボタンクリックの例をラムダ式で書き換えてみましょう。

```java
// 匿名クラスのバージョン
button.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("ボタンがクリックされました！");
    }
});

// ラムダ式バージョン
// (引数) -> { 処理 } という形式で記述します。
// 引数 e は型推論されるため、型名を省略できます。
button.addActionListener(e -> {
    System.out.println("ボタンがクリックされました！ (ラムダ式)");
});

// 処理が1行の場合は、さらに波括弧 {} も省略できます。
button.addActionListener(e -> System.out.println("ボタンがクリックされました！ (ラムダ式・省略形)"));
```

ラムダ式はコードの可読性を大幅に向上させるため、積極的に利用しましょう。

#### 6-4. `JOptionPane`によるダイアログ表示

ユーザーに簡単なメッセージを伝えたり、確認を求めたりする場合、新しい`JFrame`をわざわざ作るのは大変です。`JOptionPane`クラスを使えば、簡単にダイアログボックスを表示できます。

```java
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.awt.FlowLayout;

public class JOptionPaneExample {
    public static void main(String[] args) {
        JFrame frame = new JFrame("JOptionPane Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout());

        JButton messageBtn = new JButton("メッセージ表示");
        JButton confirmBtn = new JButton("確認表示");

        // メッセージダイアログ
        messageBtn.addActionListener(e -> {
            // JOptionPane.showMessageDialog(親コンポーネント, メッセージ);
            // 親コンポーネントにフレームを指定すると、フレーム中央に表示されます。
            JOptionPane.showMessageDialog(frame, "処理が完了しました。");
        });

        // 確認ダイアログ
        confirmBtn.addActionListener(e -> {
            // JOptionPane.showConfirmDialog(親コンポーネント, メッセージ);
            // ユーザーの選択（はい/いいえ/キャンセル）に応じて整数値が返されます。
            int result = JOptionPane.showConfirmDialog(frame, "本当に実行しますか？");

            if (result == JOptionPane.YES_OPTION) {
                System.out.println("「はい」が選択されました。");
            } else if (result == JOptionPane.NO_OPTION) {
                System.out.println("「いいえ」が選択されました。");
            } else {
                System.out.println("「キャンセル」が選択されました。");
            }
        });

        frame.add(messageBtn);
        frame.add(confirmBtn);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
```


## 第5部 応答性の高いアプリケーションへ

### 第７章 応答性の高いアプリケーションへ - GUIとスレッド

ここまでの学習で、見た目と基本的な機能を備えたGUIアプリケーションを作成できるようになりました。しかし、実際のアプリケーションでは、「ボタンを押したらデータベースに接続する」「大きなファイルを読み込む」といった時間のかかる処理がしばしば発生します。このような処理を単純に実装すると、アプリケーションは応答を停止し、ユーザーにストレスを与える「固まる（フリーズする）」現象を引き起こします。

この章では、なぜフリーズが起きるのか、その根本原因であるSwingのスレッドモデルを理解し、`SwingWorker`などの仕組みを使って常に応答性の高いアプリケーションを構築する方法を学びます。

#### 7-1. Swingのスレッドモデル：イベントディスパッチスレッド（EDT）

SwingのGUIシステムは、**シングルスレッドモデル**を採用しています。これは、GUIに関するすべての処理を、**イベントディスパッチスレッド（Event Dispatch Thread, EDT）** と呼ばれる単一の特別なスレッドで実行するというルールです。

EDTが担当する主な仕事は以下の2つです。

1.  イベント処理: ボタンのクリック、マウスの移動、キーボード入力といったすべてのユーザー操作（イベント）を検知し、登録されたイベントリスナー（`ActionListener`など）を呼び出します。
2.  画面描画: コンポーネントの再描画や、ウィンドウの更新など、画面に表示されるすべての内容を描画します。

EDTは、これらの仕事を非常に高速なループで次々と処理することで、スムーズなユーザーインタラクションを実現しています。

#### 7-2. Swingの絶対ルール：コンポーネントはEDTからのみ操作する
Swingのスレッドモデルから、SwingのGUIコンポーネント（`JFrame`, `JButton`, `JLabel`など）の生成、変更、問い合わせは、**すべてEDTの中から**行わなければならないと言うルールがあります。

もし、EDT以外のスレッド（たとえば、自前で作成したバックグラウンドスレッド）からGUIコンポーネントを操作すると、EDTが行っている描画処理と競合が発生する可能性もあります。

これにより、画面の一部が更新されない、データと表示が食い違うといったUIの不整合や、最悪の場合はデッドロック（スレッド同士が互いの処理の終了を待ち続けて停止する状態）を引き起こす危険性があります。

Swingは、このルールを破った場合に常に例外を発生させるわけではないため、問題が表面化しにくい場合もあります。しかし、それは「たまたま動いている」だけであり、非常に不安定で危険なコードです。**このルールは必ず守る必要があります。**

#### 7-3. 時間のかかる処理とフリーズ現象

では、イベントリスナーの中で時間のかかる処理を実行すると何が起きるでしょうか。イベントリスナーはEDTによって呼び出されるため、その処理が完了するまでEDTはブロック（占有）されます。

EDTがブロックされると、イベント処理も画面描画も、すべてのGUI関連の仕事が停止します。これがフリーズ現象の正体です。

```java
import javax.swing.*;
import java.awt.*;

// EDTで重い処理を実行してしまい、GUIがフリーズする悪い例
public class FreezeExample {
    public static void main(String[] args) {
        JFrame frame = new JFrame("フリーズする例");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout());

        JButton button = new JButton("5秒かかる処理を開始");
        JLabel label = new JLabel("待機中");

        button.addActionListener(e -> {
            // このリスナーはEDTで実行される
            label.setText("処理中..."); // この表示更新もすぐには反映されないことがある

            // EDTで5秒かかる重い処理を実行
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }

            label.setText("処理完了！");
        });

        frame.add(button);
        frame.add(label);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
```

上記のコードでボタンを押すと、アプリケーションは5秒間、ウィンドウの移動や他のボタンの操作を一切受け付けなくなります。

#### 7-4. 解決策(1)：`SwingUtilities.invokeLater`

バックグラウンドスレッドで何らかの処理を行った後、その結果を安全にGUIに反映させたい場合があります。そのためのもっとも基本的な方法が`SwingUtilities.invokeLater()`です。

このメソッドは、引数で渡された`Runnable`オブジェクト（処理内容）を、EDTのイベントキューの末尾に追加します。これにより、EDTが現在の仕事を終えた後に、指定された処理を実行させることができます。

```java
import javax.swing.*;

public class InvokeLaterExample {
    public static void main(String[] args) {
        JFrame frame = new JFrame("invokeLater Example");
        JLabel label = new JLabel("3秒後にメッセージが変わります");
        frame.add(label);
        frame.setSize(300, 100);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // メインスレッドとは別の、新しいバックグラウンドスレッドを開始
        new Thread(() -> {
            System.out.println("バックグラウンドスレッド: 処理を開始します。");
            try {
                // 時間のかかる処理をシミュレート
                Thread.sleep(3000); 
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("バックグラウンドスレッド: 処理が完了しました。GUIの更新を依頼します。");

            // GUIの更新処理をinvokeLaterを使ってEDTに依頼する
            // これにより、スレッドセーフなGUI更新が保証される
            SwingUtilities.invokeLater(() -> {
                System.out.println("EDT: GUIの更新処理を実行します。");
                label.setText("こんにちは、Swing！");
            });

        }).start(); // スレッドを開始
    }
}
```

**補足:** `invokeLater`は「後で実行してね（結果は待たない）」という非同期の依頼です。これに対し、GUIの更新完了を待ちたい特殊なケースでは`invokeAndWait`がありますが、デッドロックのリスクがあるため使用には注意が必要です。

#### 7-5. 解決策(2)：`SwingWorker`による高度な非同期処理

`invokeLater`はGUIの更新を依頼するだけですが、時間のかかる処理そのものと、その前後のGUI操作をまとめて管理したい場合、`SwingWorker`が非常に強力なツールとなります。`SwingWorker`は、バックグラウンド処理とEDTでのGUI更新をカプセル化し、安全に連携させるためのクラスです。

`SwingWorker`の基本的な使い方は以下の通りです。

1.  `doInBackground()`: バックグラウンドスレッドで実行したい重い処理をここに記述します。
2.  `done()`: `doInBackground()`が完了した後に、EDTで実行したい後処理（結果の表示など）をここに記述します。


```java
import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ExecutionException;

public class SwingWorkerExample extends JFrame {

    private final JLabel statusLabel;
    private final JButton startButton;

    public SwingWorkerExample() {
        setTitle("SwingWorker Example");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        startButton = new JButton("重い処理を開始");
        statusLabel = new JLabel("待機中...");

        startButton.addActionListener(e -> {
            // ボタンが押されたら、処理を開始する
            startHeavyTask();
        });

        add(startButton);
        add(statusLabel);
        pack();
        setLocationRelativeTo(null);
    }

    private void startHeavyTask() {
        // ボタンを無効化し、ステータスを更新
        startButton.setEnabled(false);
        statusLabel.setText("処理を実行中...");

        // SwingWorkerのインスタンスを作成
        // 第1ジェネリクス: doInBackgroundの戻り値の型
        // 第2ジェネリクス: publish/processで使う中間結果の型 (今回は使わないのでVoid)
        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() throws Exception {
                // ここはバックグラウンドスレッドで実行される
                // GUIを直接操作してはいけない
                System.out.println("doInBackground: 処理開始");
                // 3秒かかる重い処理をシミュレート
                Thread.sleep(3000); 
                System.out.println("doInBackground: 処理完了");
                // 処理結果を文字列として返す
                return "処理が正常に完了しました。";
            }

            @Override
            protected void done() {
                // ここはEDTで実行される
                // doInBackgroundが終わった後に呼び出される
                try {
                    // get()でdoInBackgroundの戻り値を取得
                    String result = get(); 
                    statusLabel.setText(result);
                } catch (InterruptedException | ExecutionException ex) {
                    statusLabel.setText("エラーが発生しました。");
                    ex.printStackTrace();
                }
                // ボタンを再度有効化
                startButton.setEnabled(true);
            }
        };

        // SwingWorkerを実行
        worker.execute();
    }

    public static void main(String[] args) {
        // EDTでGUIを生成・表示することを保証する
        SwingUtilities.invokeLater(() -> new SwingWorkerExample().setVisible(true));
    }
}
```

`SwingWorker`を使うことで、重い処理中もGUIはフリーズせず、ユーザーは他の操作を続けることができます。これが応答性の高いアプリケーションの秘訣です。

#### 7-6. `SwingWorker`で処理の進捗を伝える

`SwingWorker`は、処理の進捗状況をGUIにフィードバックする仕組みも提供します。`JProgressBar`（進捗バー）と組み合わせることで、ユーザーに処理がどのくらい進んでいるか視覚的に示すことができます。

  * `publish(V... chunks)`: `doInBackground`の中から呼び出し、中間結果（進捗値など）をEDTに送信します。
  * `process(List<V> chunks)`: `publish`で送信された中間結果をEDTで受け取り、GUIを更新します。

```java
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ProgressBarExample extends JFrame {
    private final JProgressBar progressBar;
    private final JButton startButton;
    private final JLabel statusLabel;

    public ProgressBarExample() {
        setTitle("JProgressBar with SwingWorker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        startButton = new JButton("ダウンロード開始");
        // 進捗バーの最小値は0、最大値は100に設定
        progressBar = new JProgressBar(0, 100);
        statusLabel = new JLabel("待機中");
        
        startButton.addActionListener(e -> startDownload());

        add(startButton);
        add(progressBar);
        add(statusLabel);
        pack();
        setLocationRelativeTo(null);
    }

    private void startDownload() {
        startButton.setEnabled(false);
        progressBar.setValue(0); // プログレスバーをリセット
        statusLabel.setText("ダウンロード中...");
        
        // 第1ジェネリクス: doInBackgroundの戻り値 (今回は最終結果はないのでVoid)
        // 第2ジェネリクス: publish/processで扱う中間結果 (今回は進捗値なのでInteger)
        SwingWorker<Void, Integer> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                // 100msごとに進捗を1ずつ進めるシミュレーション
                for (int i = 0; i <= 100; i++) {
                    Thread.sleep(50); // 重い処理をシミュレート
                    // 中間結果（進捗値）を送信
                    publish(i);
                }
                return null;
            }

            @Override
            protected void process(List<Integer> chunks) {
                // publishで送られたデータ（のリスト）をEDTで受け取る
                // 最後の進捗値を取得
                int latestProgress = chunks.get(chunks.size() - 1);
                progressBar.setValue(latestProgress);
            }


            @Override
            protected void done() {
                // 処理完了後の後始末
                statusLabel.setText("ダウンロード完了！");
                startButton.setEnabled(true);
            }
        };
        worker.execute();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ProgressBarExample().setVisible(true));
    }
}
```

#### 7-7. `SwingWorker`の処理をキャンセルする

ユーザーが途中で処理を中断したくなることもあります。`SwingWorker`はキャンセル処理にも対応しています。

  * `cancel(boolean mayInterruptIfRunning)`: 外部からこの`SwingWorker`のタスクをキャンセルしようと試みます。
  * `isCancelled()`: `doInBackground`の内部で、タスクがキャンセルされたかどうかをチェックします。

```java
// ProgressBarExampleにキャンセル機能を追加した例
public class CancellableWorkerExample extends JFrame {
    private final JProgressBar progressBar;
    private final JButton startButton;
    private final JButton cancelButton;
    private SwingWorker<Void, Integer> worker; // workerをフィールドとして保持

    public CancellableWorkerExample() {
        // ... (GUIコンポーネントの初期化はProgressBarExampleと同様) ...
        startButton = new JButton("開始");
        cancelButton = new JButton("キャンセル");
        cancelButton.setEnabled(false); // 最初は無効

        startButton.addActionListener(e -> startTask());
        cancelButton.addActionListener(e -> {
            if (worker != null) {
                // workerにキャンセルを要求する
                worker.cancel(true);
            }
        });
        // ... (addコンポーネント) ...
    }

    private void startTask() {
        startButton.setEnabled(false);
        cancelButton.setEnabled(true);
        progressBar.setValue(0);
        
        worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                for (int i = 0; i <= 100; i++) {
                    // キャンセル要求があったか定期的にチェックする
                    if (isCancelled()) {
                        break; // ループを抜けて処理を中断
                    }
                    Thread.sleep(50);
                    publish(i);
                }
                return null;
            }
            
            @Override
            protected void process(List<Integer> chunks) {
                progressBar.setValue(chunks.get(chunks.size() - 1));
            }

            @Override
            protected void done() {
                try {
                    if (isCancelled()) {
                        System.out.println("タスクはキャンセルされました。");
                    } else {
                        // 正常に完了した場合の処理
                        get(); // 例外が発生していないかチェック
                        System.out.println("タスクは正常に完了しました。");
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                startButton.setEnabled(true);
                cancelButton.setEnabled(false);
            }
        };
        worker.execute();
    }
    // ... mainメソッド ...
}
```

この章で学んだスレッドの知識は、単にGUIのフリーズを防ぐだけでなく、ユーザーにとって快適でプロフェッショナルなアプリケーションを開発するための必須スキルです。

時間のかかる処理を実装する際は、常にEDTをブロックしない方法、とくに`SwingWorker`の活用を検討してください。

