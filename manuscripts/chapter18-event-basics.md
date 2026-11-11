# <b>18章</b> <span>基本的なイベント処理</span> <small>ユーザー操作への応答の基礎</small>

## 本章の学習目標

### この章で学ぶこと

本章では、GUIアプリケーションの基本となるイベント処理について学習します。
具体的には以下の内容を扱います。

1. イベント駆動プログラミングの基本概念
    - イベント処理の3要素（ソース、オブジェクト、リスナ）
    - イベントディスパッチスレッド（EDT）の役割
2. 基本的なイベントリスナの実装
    - ActionListenerによるボタンクリック処理
    - ラムダ式を使った簡潔な記述方法
3. 主要なイベントの種類と使い分け
    - ActionEvent、MouseEvent、KeyEvent、WindowEvent
    - ItemEvent、FocusEvent、ChangeEventの基本
    - 各イベントリスナの基本的な使用方法

### この章を始める前に

第17章のGUIプログラミングの基礎、とくにJFrame、JButton、JTextFieldなどの基本的なコンポーネントの使い方を理解していることが前提です。
また、Java 8のラムダ式と関数型インターフェイスの基本知識があると、より効率的にイベント処理を実装できます。

## イベント駆動プログラミングの理解

### 従来のプログラミングとの違い

前章で作成した画面は、まだ見た目だけの「静的」なものでした。GUIアプリケーションを「動かす」ためには、ユーザーの操作（イベント）に応じた処理（イベントハンドリング）を実装します。

従来の手続き型プログラミングでは、プログラムが処理の流れを制御します。一方、イベント駆動プログラミングでは、ユーザーの操作やシステムの状態変化がプログラムの流れを制御します。

### イベント処理の3要素

Swingのイベント処理は、イベントソース、イベントオブジェクト、イベントリスナの3つの要素で構成されます。

イベントソース（Event Source）は、イベントを発生させる部品です。
JButtonやJTextFieldのように、ユーザーが操作するGUIコンポーネントがこれにあたります。
イベントオブジェクト（Event Object）は、発生したイベントの詳細情報を持つオブジェクトで、ActionEvent、MouseEvent、KeyEventなどがあります。
イベントリスナ（Event Listener）は、イベントの発生を監視し、通知を受け取って処理を実行します。
ActionListener、MouseListener、KeyListenerなどがあります。

3つの要素は次の順に働きます。
ユーザーがイベントソースを操作すると、イベントオブジェクトが作られ、登録済みのイベントリスナに渡されます。
通知を受けたリスナは、自分のなかに書かれた処理を実行します。

### イベントディスパッチスレッド（EDT）

Swingのイベント処理は、イベントディスパッチスレッド（EDT: Event Dispatch Thread）と呼ばれる専用のスレッドで実行されます。

発生したイベントはイベントキューに入り、EDTというただ1つのスレッドが、キューに入った順（FIFO順）に1つずつ処理します。
GUIコンポーネントを安全に更新できるのは、EDT上で操作したときだけです。
また、イベントを処理するスレッドが1つしかないため、イベント処理のなかで長時間の処理を実行すると、その間はほかのイベントを処理できません。
その結果、ボタンを押しても反応しないなど、UIがフリーズした状態になります。

## ボタンクリックの実装

### ActionListenerの基本

もっとも基本的な、ボタンクリックイベントを処理してみましょう。ActionListenerは、ボタンのクリック、メニュー項目の選択、Enterキーの押下など、「アクション」として定義されるイベントを処理するためのリスナーです。

以下のコードは、ボタンクリックイベントを処理する基本的な実装例です。ActionListenerインターフェイスを実装した匿名クラスを作成し、actionPerformed()メソッドでクリック時の処理を定義します。addActionListener()メソッドでリスナーをボタンに登録することで、クリックイベントとの関連付けが完了します。

<span class="listing-number">**サンプルコード18-1**　ButtonEventExample.java</span>

```java
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class ButtonEventExample {
    public static void main(String[] args) {
        JFrame frame = new JFrame("ボタンイベントの例");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout());

        JButton button = new JButton("クリックしてね");

        // 1. イベントリスナーを作成（匿名クラスを使用）
        ActionListener listener = new ActionListener() {
            // 2. ボタンがクリックされた時の処理を記述
            @Override
            public void actionPerformed(ActionEvent e) {
                // 3. 簡単なダイアログを表示
                JOptionPane.showMessageDialog(frame, "ボタンがクリックされました！");
            }
        };

        // 4. ボタン（イベントソース）にリスナーを登録
        button.addActionListener(listener);

        frame.add(button);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
```

実行すると、次のように表示されます。

```
ウィンドウが表示されます（サイズ: 約300x100ピクセル）
- 「クリックしてね」というラベルのボタンが中央に配置されています
- ボタンをクリックすると、「ボタンがクリックされました！」というメッセージダイアログが表示されます
- OKボタンでダイアログを閉じることができます
```

### 匿名クラスの使用

上記の例では、ActionListenerインターフェイスを匿名クラスで実装しています。匿名クラスは、クラス名を持たないクラスで、その場でインターフェイスを実装してインスタンスを作成できます。イベントリスナーのように、一度しか使わないクラスを定義する場合に便利です。

## ラムダ式による簡潔な記述

### ラムダ式の導入

ActionListenerのように、実装すべきメソッドが1つだけのインターフェイス（関数型インターフェイス）は、ラムダ式を使って非常に簡潔に記述できます。

以下のコードは、先ほどの匿名クラスをラムダ式で書き換えた例です。ラムダ式を使用することで、コードがより簡潔になり、イベント処理の本質に集中できます。ActionEventのパラメータeはラムダ式内で参照できますが、この例では使用していません。

<span class="listing-number">**サンプルコード18-2**</span>

```java
// 上記の匿名クラスの部分をラムダ式で書き換える
button.addActionListener(e -> {
    JOptionPane.showMessageDialog(frame, "ボタンがクリックされました！");
});

// 処理が1行なら波括弧も省略可能
button.addActionListener(e -> JOptionPane.showMessageDialog(frame, "ボタンがクリックされました！"));
```

ラムダ式はJava 8で導入された機能で、関数型インターフェイスの実装を非常に簡潔に記述できます。とくにイベント処理のようなコールバック処理では、ラムダ式が標準的な記法となっています。

### 簡単なアプリケーション：挨拶プログラム

テキストフィールドに入力された名前を使って、挨拶メッセージを表示するプログラムを作成しましょう。

以下のコードは、テキストフィールドとボタン、ラベルを組み合わせたインタラクティブな挨拶アプリケーションの例です。ラムダ式を使用して、ボタンクリック時にテキストフィールドから入力を取得し、ラベルに動的にメッセージを表示します。

<span class="listing-number">**サンプルコード18-3**　GreetingApp.java</span>

```java
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class GreetingApp {
    public static void main(String[] args) {
        JFrame frame = new JFrame("挨拶プログラム");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout());

        JTextField nameField = new JTextField(15);
        JButton greetButton = new JButton("挨拶する");
        JLabel messageLabel = new JLabel("名前を入力してください");

        // ボタンにラムダ式でイベントリスナーを登録
        greetButton.addActionListener(e -> {
            // テキストフィールドから入力された文字列を取得
            String name = nameField.getText();
            // ラベルに挨拶メッセージを設定
            messageLabel.setText("こんにちは、" + name + "さん！");
        });

        frame.add(new JLabel("名前:"));
        frame.add(nameField);
        frame.add(greetButton);
        frame.add(messageLabel);
        
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
```

実行すると、次のように表示されます。

```
ウィンドウが表示されます（タイトル: 挨拶プログラム）
- テキストフィールド（15文字幅）と「挨拶する」ボタンが配置されています
- 初期状態では「名前を入力してください」というメッセージが表示されています
- テキストフィールドに「太郎」と入力して「挨拶する」ボタンをクリックすると
  「こんにちは、太郎さん！」というメッセージに変わります
```

## 代表的なイベントの種類

### イベントとリスナの対応

Swingにはさまざまなイベントがあります。目的に応じて必要なリスナを使い分けましょう。

| イベント | 説明 | 主なリスナ |
| --- | --- | --- |
| ActionEvent | ボタンクリックなど、明確なアクション | ActionListener |
| MouseEvent | マウスのクリック、カーソルの出入り | MouseListener |
| KeyEvent | キーボードのキー操作 | KeyListener |
| WindowEvent | ウィンドウが開く、閉じるなどの状態変化 | WindowListener |
| ItemEvent | チェックボックスなどの項目選択状態の変化 | ItemListener |
| FocusEvent | コンポーネントがフォーカスを得る/失う | FocusListener |
| ChangeEvent | スライダーなど、コンポーネントの内部状態の連続的な変化 | ChangeListener |

### WindowListenerで終了確認

ウィンドウを閉じる際に確認ダイアログを表示する例です。WindowListenerインターフェイスには多くのメソッドがありますが、WindowAdapterクラスを継承することで、必要なメソッドだけをオーバーライドして実装できます。

以下のコードは、WindowAdapterを使用してウィンドウ閉じる際の終了確認を実装する例です。setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE)でデフォルトの閉じる動作を無効化し、windowClosing()メソッドでカスタム処理を実行します。

<span class="listing-number">**サンプルコード18-4**　WindowEventExample.java</span>

```java
import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class WindowEventExample {
    public static void main(String[] args) {
        JFrame frame = new JFrame("終了確認");
        // デフォルトの閉じる操作を無効化
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // WindowAdapterを匿名クラスで実装して登録
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int option = JOptionPane.showConfirmDialog(frame, "本当に終了しますか？");
                if (option == JOptionPane.YES_OPTION) {
                    System.exit(0); // 「はい」ならプログラムを終了
                }
            }
        });

        frame.setSize(300, 200);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
```

実行すると、次のように表示されます。

```
ウィンドウが表示されます（タイトル: 終了確認、サイズ: 300x200）
- ウィンドウの「×」ボタンをクリックすると、確認ダイアログが表示されます
- ダイアログ: 「本当に終了しますか？」
  - 「はい」を選択 → アプリケーションが終了します
  - 「いいえ」を選択 → ダイアログが閉じ、ウィンドウは開いたままです
  - 「キャンセル」を選択 → 同様にウィンドウは開いたままです
```

### 基本的なマウスイベント

マウスのクリックや移動を検出する基本的な例です。

以下のコードは、MouseListenerとMouseMotionListenerを実装して、マウスのさまざまなイベントを処理する例です。マウスのクリック、パネルへの出入り、マウスの移動などを検出し、ステータスラベルに現在の状態を表示します。

<span class="listing-number">**サンプルコード18-5**　BasicMouseEventExample.java（5、9、24〜46、53、60行目）</span>

```java
public class BasicMouseEventExample extends JFrame {
    // ...
    public BasicMouseEventExample() {
        // ...
        // マウスリスナーの追加
        clickPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String button = switch (e.getButton()) {
                    case MouseEvent.BUTTON1 -> "左";
                    case MouseEvent.BUTTON2 -> "中央";
                    case MouseEvent.BUTTON3 -> "右";
                    default -> "不明な";
                };
                statusLabel.setText(button + "ボタンがクリックされました: " + e.getPoint());
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                clickPanel.setBackground(Color.YELLOW);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                clickPanel.setBackground(Color.LIGHT_GRAY);
            }
        });
        // ...
    }
    // ...
}
```

実行すると、次のように表示されます。

```
ウィンドウが表示されます（タイトル: マウスイベントの例）
- 灰色のパネル（200x200）が中央に配置されています
- マウスの動作に応じて以下のような反応があります：
  - パネルにマウスを乗せる → 背景が黄色に変わります
  - パネルからマウスを離す → 背景が灰色に戻ります
  - パネル内でクリック → ステータスバーに「左ボタンがクリックされました: java.awt.Point[x=XX,y=YY]」と表示
  - 右クリック → 「右ボタンがクリックされました」と表示
  - 中クリック → 「中ボタンがクリックされました」と表示
```

### 基本的なキーボードイベント

キーボード入力を検出する基本的な例です。

以下のコードは、KeyListenerを使用してキーボードイベントを処理する例です。キーの押下を検出し、押されたキーの情報を表示します。また、Ctrl+Sのようなショートカットキーの検出方法も示しています。consume()メソッドを使用することで、デフォルトのキー動作をキャンセルできます。

<span class="listing-number">**サンプルコード18-6**　BasicKeyEventExample.java（5、9、23〜37、44、51行目）</span>

```java
public class BasicKeyEventExample extends JFrame {
    // ...
    public BasicKeyEventExample() {
        // ...
        // キーリスナーの追加
        inputArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                String keyText = KeyEvent.getKeyText(e.getKeyCode());
                statusLabel.setText("押されたキー: " + keyText);
                
                // Ctrl+Sのショートカット検出
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_S) {
                    JOptionPane.showMessageDialog(BasicKeyEventExample.this, 
                        "保存ショートカットが押されました");
                    e.consume(); // デフォルトの動作をキャンセル
                }
            }
        });
        // ...
    }
    // ...
}
```

### ItemListenerによる選択状態の監視

チェックボックスやラジオボタンの選択状態の変化を監視する例です。

以下のコードは、ItemListenerを使用してチェックボックスとラジオボタンの選択状態を監視する例です。ItemEventを通じて、選択がされたか解除されたかを判定し、現在の選択状態をリアルタイムで更新・表示します。

<span class="listing-number">**サンプルコード18-7**　ItemListenerExample.java（5、10、22〜34、52〜61、82、97行目）</span>

```java
public class ItemListenerExample extends JFrame {
    // ...
    public ItemListenerExample() {
        // ...
        ItemListener checkListener = new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                JCheckBox source = (JCheckBox) e.getSource();
                String status = e.getStateChange() == ItemEvent.SELECTED ? "選択" : "解除";
                statusLabel.setText(source.getText() + " が" + status + "されました");
                updateSelection();
            }
        };
        
        checkBox1.addItemListener(checkListener);
        checkBox2.addItemListener(checkListener);
        checkBox3.addItemListener(checkListener);
        // ...
        ItemListener radioListener = e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                JRadioButton source = (JRadioButton) e.getSource();
                statusLabel.setText("サイズ: " + source.getText() + " が選択されました");
            }
        };
        
        radio1.addItemListener(radioListener);
        radio2.addItemListener(radioListener);
        radio3.addItemListener(radioListener);
        // ...
    }
    // ...
}
```

### FocusListenerによるフォーカス管理

コンポーネントがフォーカスを得る・失うタイミングを検出する例です。

以下のコードは、FocusListenerを使用してテキストフィールドのフォーカス状態を管理する例です。フォーカスを取得したフィールドの背景色を変更し、フォーカスを失った際には入力内容の検証を行います。この仕組みを使用することで、ユーザーフレンドリーなフォーム入力インターフェイスを実装できます。

<span class="listing-number">**サンプルコード18-8**　FocusListenerExample.java（5、9、21〜45、71、85行目）</span>

```java
public class FocusListenerExample extends JFrame {
    // ...
    public FocusListenerExample() {
        // ...
        // FocusListenerの作成
        FocusListener focusListener = new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                JTextField source = (JTextField) e.getSource();
                source.setBackground(Color.YELLOW);
                statusLabel.setText("フォーカスを取得: " + getFieldName(source));
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                JTextField source = (JTextField) e.getSource();
                source.setBackground(Color.WHITE);
                
                // バリデーションの例
                if (source == field1 && source.getText().isEmpty()) {
                    statusLabel.setText("名前を入力してください");
                    source.setBackground(Color.PINK);
                }
            }
        };
        
        field1.addFocusListener(focusListener);
        field2.addFocusListener(focusListener);
        field3.addFocusListener(focusListener);
        // ...
    }
    // ...
}
```

### ChangeListenerによる値の変更監視

スライダーやスピナーなどの値の変化を監視する例です。

以下のコードは、ChangeListenerを使用してスライダーとスピナーの値の変化を監視し、連動させる例です。getValueIsAdjusting()メソッドを使用することで、スライダーのドラッグ中とドラッグ終了時を区別し、パフォーマンスを最適化できます。

<span class="listing-number">**サンプルコード18-9**　ChangeListenerExample.java（5、11、28〜40、50〜55、84、91行目）</span>

```java
public class ChangeListenerExample extends JFrame {
    // ...
    public ChangeListenerExample() {
        // ...
        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int value = slider.getValue();
                valueLabel.setText("現在の値: " + value);
                progressBar.setValue(value);
                
                // ドラッグ終了時のみ処理
                if (!slider.getValueIsAdjusting()) {
                    IO.println("最終値: " + value);
                }
            }
        });
        // ...
        spinner.addChangeListener(e -> {
            int value = (Integer) spinner.getValue();
            slider.setValue(value);
            progressBar.setValue(value);
            valueLabel.setText("現在の値: " + value);
        });
        // ...
    }
    // ...
}
```

## イベント処理のベストプラクティス

### EDT上での実行

SwingのすべてのGUI操作はイベントディスパッチスレッド上で実行します。

以下のコードは、SwingUtilities.invokeLater()を使用してGUIをEDT上で初期化する正しい方法を示しています。このパターンは、Swingアプリケーションを開発する際の基本であり、スレッドセーフティを保証します。

<span class="listing-number">**サンプルコード18-10**</span>

```java
// メインメソッドからGUIを初期化する正しい方法
public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        JFrame frame = new JFrame("EDTの例");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);
        frame.setVisible(true);
    });
}
```

### 長時間処理の回避

イベントハンドラ内で長時間かかる処理を実行すると、UIがフリーズします。

以下のコードは、イベントハンドラ内で長時間の処理を実行した場合の問題と、SwingWorkerを使用した解決法を示しています。長時間の処理は別スレッドで実行し、UIの応答性を維持することが重要です。

<span class="listing-number">**サンプルコード18-11**</span>

```java
// 悪い例：UIがフリーズする
button.addActionListener(e -> {
    // 長時間かかる処理（3秒待機）
    try {
        Thread.sleep(3000);
    } catch (InterruptedException ex) {
        ex.printStackTrace();
    }
    label.setText("完了");
});

// 良い例：別スレッドで実行
button.addActionListener(e -> {
    new Thread(() -> {
        try {
            Thread.sleep(3000);
            // UI更新はEDT上で行う
            SwingUtilities.invokeLater(() -> label.setText("完了"));
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }).start();
});
```

## まとめ

本章では、GUIプログラミングの基本となるイベント処理について学習しました。

### 重要なポイント
- イベント駆動プログラミングの3要素（ソース、オブジェクト、リスナ）
- ActionListenerによる基本的なボタンクリック処理
- ラムダ式を使った簡潔なイベントハンドラの記述
- 各種イベントリスナの基本的な使い方

### 次のステップ
基本的なイベント処理を理解したので、次章では、より高度なイベント処理パターンについて学習します。DocumentListenerによるテキスト監視、カスタムイベントの作成、パフォーマンスを考慮したイベント処理などを扱います。

## 章末演習

### 演習課題へのアクセス

本章の演習課題は、GitHubリポジトリで提供されています。<br>
`https://github.com/Nagatani/techbook-java-primer/tree/main/exercises/chapter18/`

### 課題構成

演習は、本章で学んだイベント処理を組み合わせて使う次の課題で構成されています。

- インタラクティブ描画アプリケーション（マウスイベント処理）
- フォームバリデータ（入力のリアルタイム検証）
- メニューシステム（メニューとキーボードショートカット）
- マウストラッカー（マウス動作の記録と表示）

各課題には、詳細な要件と実装のヒントが含まれています。
