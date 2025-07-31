---
title: Swingカスタムコンポーネント
---

> オブジェクト指向プログラミングおよび演習1 第5回
>
> Java Swing カスタムコンポーネント作成

この資料では、Java Swingを使用してカスタムコンポーネントを作成する方法を学びます。

継承の概念を理解するために、設定されたテキストの内容に応じて文字の色が変化する特別なラベルコンポーネントを作成してみましょう。

### 1\. はじめに

#### 1.1. カスタムコンポーネントとは？

Java Swingには、`JButton`、`JLabel`、`JTextField`など、多くの標準コンポーネントが用意されています。しかし、アプリケーションの要件によっては、これらの標準コンポーネントの機能だけでは不十分な場合や、特定の見た目や振る舞いを持つコンポーネントを繰り返し使いたい場合があります。

カスタムコンポーネントとは、開発者が独自の機能や外観を持たせて作成するコンポーネントのことです。既存のSwingコンポーネントを継承して拡張したり、`JComponent`クラスを直接継承してまったく新しいコンポーネントを作成できます。

#### 1.2. なぜカスタムコンポーネントを作成するのか？

  * **再利用性**: 特定の機能やデザインを持つコンポーネントを一度作れば、アプリケーションのさまざまな場所で再利用できます。
  * **カプセル化**: コンポーネントの内部ロジックを隠蔽し、外部からはシンプルなインターフェイスで操作できるようにします。これにより、コードの見通しが良くなり、保守性が向上します。
  * **拡張性**: 既存のコンポーネントの機能を部分的に変更したり、新しい機能を追加したりすることが容易になります。
  * **独自のデザイン**: アプリケーション独自のルック＆フィールを実現できます。

#### 1.3. 今回のテーマ：テキスト内容に応じて文字色が変わるラベル

今回は、`JLabel`を拡張して、設定されるテキストの内容によって文字の色が自動的に変わるカスタムラベルを作成します。たとえば、「エラー」という単語が含まれていれば赤色、「成功」なら緑色といった具合です。

### 2\. Java Swingにおけるコンポーネントの継承

Swingのコンポーネントは、`java.awt.Component`クラスを頂点とするクラス階層構造を持っています。多くのSwingコンポーネントは`javax.swing.JComponent`クラスを直接または間接的に継承しています。

カスタムコンポーネントを作成するもっとも一般的な方法は、既存のSwingコンポーネントを継承し、その機能を拡張することです。今回は`JLabel`の表示機能はそのまま利用し、テキストが設定された際の振る舞いを変更したいため、`JLabel`クラスを継承します。

**`JLabel`を継承するメリット:**

  * `JLabel`が持つテキスト表示、アイコン表示、HTMLレンダリングなどの基本的な機能をそのまま利用できます。
  * 既存の`JLabel`とほぼ同様に扱えるため、学習コストが低く済みます。

### 3\. カスタムコンポーネントの実装手順

`ConditionalColorLabel`という名前のカスタムラベルクラスを作成します。

#### 3.1. クラスの定義

まず、`javax.swing.JLabel`を継承したクラスを定義します。

```java
import javax.swing.JLabel;
import java.awt.Color;

public class ConditionalColorLabel extends JLabel {
    // コンストラクタやメソッドをここに追加していきます
}
```

#### 3.2. コンストラクタの作成

`JLabel`と同様のコンストラクタをいくつか用意しておくと便利です。ここでは、空のコンストラクタと、初期テキストを指定できるコンストラクタを作成します。

```java
public class ConditionalColorLabel extends JLabel {

    public ConditionalColorLabel() {
        super(); // 親クラス(JLabel)のコンストラクタを呼び出す
        updateTextColorBasedOnContent(getText()); // 初期テキストに対しても色更新を行う
    }

    public ConditionalColorLabel(String text) {
        super(text); // 親クラス(JLabel)のコンストラクタを呼び出す
        updateTextColorBasedOnContent(text); // 初期テキストに対しても色更新を行う
    }

    // 他のJLabelのコンストラクタ（アイコン付きなど）も必要に応じて追加できます
}
```

#### 3.3. テキスト設定メソッドのオーバーライド

ラベルのテキストが変更されたときに文字色を更新したいので、`JLabel`の`setText(String text)`メソッドをオーバーライドします。

```java
public class ConditionalColorLabel extends JLabel {
    // ... (コンストラクタは省略)

    @Override
    public void setText(String text) {
        super.setText(text); // まず親クラスのsetTextメソッドを呼び出し、テキストを実際に設定する
        updateTextColorBasedOnContent(text); // テキストの内容にもとづいて文字色を更新する
    }

    private void updateTextColorBasedOnContent(String text) {
        // ここに文字色を変更するロジックを実装します
        if (text == null) {
            setForeground(Color.BLACK); // デフォルトの色 (もしnullの場合)
            return;
        }

        String lowerCaseText = text.toLowerCase(); // 大文字・小文字を区別しないように小文字に変換

        if (lowerCaseText.contains("エラー") || lowerCaseText.contains("error")) {
            setForeground(Color.RED);
        } else if (lowerCaseText.contains("警告") || lowerCaseText.contains("warning")) {
            setForeground(Color.ORANGE); // もしくは new Color(255, 165, 0) など
        } else if (lowerCaseText.contains("成功") || lowerCaseText.contains("success")) {
            setForeground(Color.GREEN.darker()); // 少し暗めの緑
        } else if (lowerCaseText.contains("情報") || lowerCaseText.contains("info")) {
            setForeground(Color.BLUE);
        } else {
            setForeground(Color.BLACK); // 上記のいずれにも該当しない場合はデフォルトの色（黒）
        }
    }
}
```

**ポイント:**

* `@Override`アノテーション: このメソッドが親クラスのメソッドを正しくオーバーライドしていることをコンパイラに伝えます。タイプミスなどを防ぐのに役立ちます。
* `super.setText(text)`: 親クラスである`JLabel`の`setText`メソッドを呼び出しています。これにより、`JLabel`本来のテキスト設定機能が実行されます。これを呼び出さないと、ラベルにテキストが表示されなくなってしまいます。
* `updateTextColorBasedOnContent(String text)`: 実際にテキスト内容を評価し、`setForeground(Color color)`メソッドを使って文字色を設定するロジックをまとめたプライベートメソッドです。`setText`メソッドとコンストラクタから呼び出されます。
* `toLowerCase()`: テキスト内容を評価する際に、大文字・小文字の区別をなくすために使用しています。これにより、「エラー」でも「ｴﾗｰ」でも「error」でも同じ条件で判定できます。
* `setForeground(Color color)`: コンポーネントの前景色（通常は文字色）を設定するメソッドです。`JComponent`から継承されています。

### 4\. サンプルコード

以下に、作成した`ConditionalColorLabel`クラスと、それを使用する簡単なサンプルアプリケーションのコードを示します。

#### 4.1. `ConditionalColorLabel.java`

```java
import javax.swing.JLabel;
import java.awt.Color;

public class ConditionalColorLabel extends JLabel {

    public ConditionalColorLabel() {
        super();
        updateTextColorBasedOnContent(getText());
    }

    public ConditionalColorLabel(String text) {
        super(text);
        updateTextColorBasedOnContent(text);
    }

    @Override
    public void setText(String text) {
        super.setText(text);
        updateTextColorBasedOnContent(text);
    }

    /**
     * テキストの内容にもとづいて文字色を更新します。
     * @param text 設定するテキスト
     */
    private void updateTextColorBasedOnContent(String text) {
        if (text == null) {
            setForeground(UIManager.getColor("Label.foreground")); // テーマに合わせたデフォルト色
            return;
        }

        String lowerCaseText = text.toLowerCase();

        if (lowerCaseText.contains("エラー") || lowerCaseText.contains("error")) {
            setForeground(Color.RED);
        } else if (lowerCaseText.contains("警告") || lowerCaseText.contains("warning")) {
            setForeground(new Color(255, 140, 0)); // DarkOrange
        } else if (lowerCaseText.contains("成功") || lowerCaseText.contains("success")) {
            setForeground(new Color(0, 128, 0)); // Green
        } else if (lowerCaseText.contains("情報") || lowerCaseText.contains("info")) {
            setForeground(Color.BLUE);
        } else {
            // UIManagerから現在のルックアンドフィールのラベルのデフォルト前景色を取得
            setForeground(UIManager.getColor("Label.foreground"));
        }
    }
}
```

#### 4.2. `MainApplication.java` (使用例)

```java
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

public class MainApplication {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("カスタムラベルデモ");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 300);

            JPanel mainPanel = new JPanel(new GridLayout(0, 1, 10, 10)); // 縦に並べるレイアウト

            ConditionalColorLabel label1 = new ConditionalColorLabel("初期メッセージ");
            ConditionalColorLabel label2 = new ConditionalColorLabel("ここにエラーが表示されます。");
            ConditionalColorLabel label3 = new ConditionalColorLabel("処理が成功しました！");
            ConditionalColorLabel label4 = new ConditionalColorLabel("警告: 注意してください。");
            ConditionalColorLabel label5 = new ConditionalColorLabel("一般的な情報テキストです。");


            mainPanel.add(label1);
            mainPanel.add(label2);
            mainPanel.add(label3);
            mainPanel.add(label4);
            mainPanel.add(label5);

            // テキスト入力とボタンで動的にラベルのテキストを変更するデモ
            JPanel controlPanel = new JPanel(new FlowLayout());
            JTextField textField = new JTextField(20);
            JButton updateButton = new JButton("ラベル1を更新");

            updateButton.addActionListener(e -> {
                label1.setText(textField.getText());
            });

            controlPanel.add(textField);
            controlPanel.add(updateButton);

            frame.getContentPane().add(mainPanel, BorderLayout.CENTER);
            frame.getContentPane().add(controlPanel, BorderLayout.SOUTH);

            frame.setLocationRelativeTo(null); // 画面中央に表示
            frame.setVisible(true);
        });
    }
}
```

### 5\. 動作確認と解説

上記の`ConditionalColorLabel.java`と`MainApplication.java`をコンパイルして実行すると、ウィンドウが表示されます。

* `label1` は初期状態では "初期メッセージ" と黒色（またはテーマのデフォルト色）で表示されます。テキストフィールドに "エラーです" と入力して「ラベル1を更新」ボタンを押すと、`label1`のテキストが赤色に変わります。
* `label2` は "ここにエラーが表示されます。" というテキストなので、最初から赤色で表示されます。
* `label3` は "処理が成功しました！" なので、緑色で表示されます。
* `label4` は "警告: 注意してください。" なので、オレンジ色で表示されます。
* `label5` は "一般的な情報テキストです。" なので、青色で表示されます。

このように、`ConditionalColorLabel`は設定されたテキストに応じて自動的に文字色を変化させます。これは、`setText`メソッドがオーバーライドされ、その中でテキスト内容を判定して`setForeground`を呼び出しているためです。

### 6\. 応用例と発展

今回のカスタムコンポーネントはシンプルなものでしたが、さらに以下のような拡張が考えられます。

* **より複雑な条件分岐**: 正規表現を使って、より複雑なパターンマッチングで色を決定する。
* **色のカスタマイズ**: 色の定義を外部ファイル（プロパティファイルなど）から読み込むようにして、アプリケーションのテーマに合わせて容易に変更できるようにする。
* **独自のプロパティ**: たとえば、`setErrorColor(Color color)`、`setWarningColor(Color color)`のようなメソッドを追加し、キーワードごとの色をプログラム実行中変更できるようにする。
* **イベント処理**: マウスがコンポーネント上にあるとき、色を変えるなど、他のイベントに応じた振る舞いを追加する。
* **`paintComponent`のオーバーライド**: 文字色だけでなく、背景色や枠線など、より高度な描画カスタマイズを行いたい場合は、`paintComponent(Graphics g)`メソッドをオーバーライドします。ただし、今回のケースでは不要です。

### 7\. まとめ

この資料では、Java Swingの`JLabel`を継承して、テキスト内容に応じて文字色が変わるカスタムコンポーネント`ConditionalColorLabel`を作成しました。

* 既存コンポーネントの継承は、カスタムコンポーネントを作成する強力な手段です。
* メソッドのオーバーライド（とくに`setText`や描画関連のメソッド）を利用することで、コンポーネントの振る舞いを効果的に変更できます。
* カスタムコンポーネントは、コードの再利用性を高め、特定の機能をカプセル化するのに役立ちます。


----

# カスタムコンポーネントサンプル集

## JTextFieldの入力内容が確定するアクションで共通の処理を実装する

JTextFieldの拡張です。
JTextFieldにて、エンターキーが押されるなどの確定する動作を行った際に共通の処理を差し込みたい要件がある場合に使用できます。

### `EnterActionTextField.java`

```java
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EnterActionTextField extends JTextField {

    private Runnable onEnterAction;

    public EnterActionTextField() {
        super();
        initialize();
    }

    public EnterActionTextField(int columns) {
        super(columns);
        initialize();
    }

    public EnterActionTextField(String text) {
        super(text);
        initialize();
    }

    public EnterActionTextField(String text, int columns) {
        super(text, columns);
        initialize();
    }

    private void initialize() {
        this.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (onEnterAction != null) {
                    onEnterAction.run();
                }
            }
        });
    }

    /**
     * Enterキーが押されたときに実行する処理を設定します。
     *
     * @param onEnterAction 実行する処理 (Runnable)
     */
    public void setOnEnterAction(Runnable onEnterAction) {
        this.onEnterAction = onEnterAction;
    }

    /**
     * Enterキーが押されたときに実行する処理 (テキストフィールドの現在のテキストを引数に取る) を設定します。
     *
     * @param onEnterActionWithText 実行する処理 (Consumer<String>)
     */
    public void setOnEnterAction(java.util.function.Consumer<String> onEnterActionWithText) {
        this.onEnterAction = () -> onEnterActionWithText.accept(getText());
    }
}
```

### `MainApplication.java`

```java
import javax.swing.*;
import java.awt.*;

public class MainApplication {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("EnterActionTextField Demo");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new FlowLayout());

            JLabel label = new JLabel("結果: ");
            EnterActionTextField textField1 = new EnterActionTextField(10);
            EnterActionTextField textField2 = new EnterActionTextField("テキストを入力してEnter", 10);

            // 処理1: ラムダ式でRunnableを渡す
            textField1.setOnEnterAction(() -> {
                System.out.println("CustomAction: TextField1でEnterが押されました！ テキスト: " + textField1.getText());
                label.setText("結果: TextField1 -> " + textField1.getText());
                textField1.setText(textField1.getText().toUpperCase());
                System.out.println("TextField1の内容を大文字化しました！ テキスト: " + textField1.getText());
                frame.pack();
            });

            // 確定処理はそのまま使用できる
            textField1.addActionListener(e -> {
                System.out.println("ActionEvent: TextField1でEnterが押されました！ テキスト: " + textField1.getText());
                // この処理の後、処理1の内容が実行される
            });

            // 処理2: Consumer<String> を使ってテキストフィールドの内容を受け取る
            textField2.setOnEnterAction((text) -> {
                System.out.println("TextField2でEnterが押されました！ テキスト: " + text);
                // 小文字化
                textField2.setText(text.toUpperCase());
                label.setText("結果: TextField2 -> " + text);
                System.out.println("TextField2の内容を小文字化しました！ テキスト: " + textField2.getText());
                frame.pack();
            });


            frame.add(new JLabel("入力してEnter: "));
            frame.add(textField1);
            frame.add(textField2);
            frame.add(label);

            frame.pack();
            frame.setLocationRelativeTo(null); //画面中央に表示
            frame.setVisible(true);
        });
    }
}
```

## java.awt.Graphicsを使用した描写サンプル

Swingのコンポーネントには、描写処理を行う`paintComponent(Graphics g)`メソッドがあります。
このメソッドをオーバーライドして、`Graphics g`に対して描写を指示すると、さまざまな表現が可能です。

### `DrawingPanel.java`

```java
import javax.swing.*;
import java.awt.*;

public class DrawingPanel extends JPanel {

    public DrawingPanel() {
        // 推奨サイズをここで指定
        setPreferredSize(new Dimension(500, 400));
        setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // --- Graphics2D g2d を使った描画処理 (より高度な機能) ---
        Graphics2D g2d = (Graphics2D) g; // Graphics を Graphics2D にキャスト

        // アンチエイリアシングを有効にして、描画を滑らかにする
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // テキスト描画のアンチエイリアシングも設定 (任意)
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);


        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("SansSerif", Font.BOLD, 12));
        g2d.drawString("--- Graphics2D API (キャストあり) ---", 20, 15);


        // 1. 線を描画 (太さ指定あり)
        g2d.setColor(Color.BLUE);
        g2d.setStroke(new BasicStroke(3)); // 線の太さを3ピクセルに設定
        g2d.drawLine(20, 30, 150, 30);

        // 2. 長方形を描画 (枠線のみ、太さ指定あり)
        g2d.setColor(Color.RED);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRect(20, 50, 100, 60);

        // 3. 楕円を描画 (塗りつぶし)
        g2d.setColor(new Color(0, 150, 0)); // RGBで緑色を指定
        g2d.fillOval(150, 60, 80, 80);

        // 4. 多角形を描画 (塗りつぶし)
        g2d.setColor(Color.ORANGE);
        int[] xPoints = {250, 300, 350, 325, 275};
        int[] yPoints = {60, 30, 60, 90, 90};
        g2d.fillPolygon(xPoints, yPoints, 5);

        // 5. 文字列を描画 (アンチエイリアスあり)
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("SansSerif", Font.BOLD, 20));
        g2d.drawString("こんにちは、Swing!", 30, 160);

        g2d.setColor(new Color(100, 50, 150));
        g2d.setFont(new Font("Serif", Font.ITALIC | Font.BOLD, 24));
        g2d.drawString("図形と文字の描画テスト", 30, 200);

        // 6. 角丸長方形を描画
        g2d.setColor(Color.CYAN);
        g2d.setStroke(new BasicStroke(1));
        g2d.drawRoundRect(30, 220, 150, 50, 20, 20);

        g2d.setColor(Color.MAGENTA);
        g2d.fillRoundRect(200, 220, 150, 50, 30, 30);


        // --- Graphics g を直接使った描画処理 ---
        // 文字の描画 (アンチエイリアスなし)
        g.setColor(Color.BLACK);
        Font plainFont = new Font("Dialog", Font.PLAIN, 12);
        g.setFont(plainFont);
        g.drawString("--- Graphics API (キャストなし) ---", 20, 280);

        // 四角形 (塗りつぶし)
        g.setColor(Color.BLUE);
        g.fillRect(20, 300, 80, 50); // (x, y, width, height)

        // 円 (枠線のみ)
        g.setColor(Color.RED);
        g.drawOval(120, 300, 50, 50); // (x, y, width, height)

        // 線
        g.setColor(new Color(100, 100, 100)); // 暗めの灰色
        g.drawLine(20, 360, 170, 360); // (x1, y1, x2, y2)

    }
}
```

### `MainApplication.java`

```java
import javax.swing.*;
import java.awt.*;

public class MainApplication {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Graphics描画"); // タイトル変更
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            DrawingPanel drawingPanel = new DrawingPanel();
            frame.add(drawingPanel);

            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
```