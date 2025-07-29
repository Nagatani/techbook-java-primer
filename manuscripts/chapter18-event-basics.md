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
    - 各イベントリスナの基本的な使用方法

### この章を始める前に

第18章のGUIプログラミングの基礎、とくにJFrame、JButton、JTextFieldなどの基本的なコンポーネントの使い方を理解していることが前提です。
また、Java 8のラムダ式と関数型インターフェイスの基本知識があると、より効率的にイベント処理を実装できます。

## イベント駆動プログラミングの理解

### 従来のプログラミングとの違い

前章で作成した画面は、まだ見た目だけの「静的」なものでした。GUIアプリケーションを「動かす」ためには、ユーザーの操作（イベント）に応じた処理（イベントハンドリング）を実装します。

従来の手続き型プログラミングでは、プログラムが処理の流れを制御します。一方、イベント駆動プログラミングでは、ユーザーの操作やシステムの状態変化がプログラムの流れを制御します。

### イベント処理の3要素

Swingのイベント処理は、以下の3つの要素で構成されます。

1. **イベントソース（Event Source）**
   - イベントを発生させる部品（JButton、JTextFieldなど）
   - ユーザーが操作するGUIコンポーネント

2. **イベントオブジェクト（Event Object）**
   - 発生したイベントの詳細情報を持つオブジェクト
   - ActionEvent、MouseEvent、KeyEventなど

3. **イベントリスナ（Event Listener）**
   - イベントの発生を監視し、通知を受け取って処理を実行
   - ActionListener、MouseListener、KeyListenerなど

処理の流れは、「ユーザーがイベントソースを操作 → イベントオブジェクトが生成され、登録済みのイベントリスナに通知 → リスナ内の処理が実行される」となります。

### イベントディスパッチスレッド（EDT）

Swingのイベント処理は、イベントディスパッチスレッド（EDT: Event Dispatch Thread）と呼ばれる専用のスレッドで実行されます。

EDTの特徴:
- 単一スレッド: すべてのGUIイベントは1つのスレッドで順番に処理
- スレッドセーフ: GUIコンポーネントの更新はEDT上でのみ安全
- 非ブロッキング: 長時間の処理はUIをフリーズさせる
- イベントキュー: イベントはFIFO順で処理

## ボタンクリックの実装

### ActionListenerの基本

もっとも基本的な、ボタンクリックイベントを処理してみましょう。ActionListenerは、ボタンのクリック、メニュー項目の選択、Enterキーの押下など、「アクション」として定義されるイベントを処理するためのリスナーです。

<span class="listing-number">**サンプルコード19-1**</span>

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

### 匿名クラスの使用

上記の例では、ActionListenerインターフェイスを匿名クラスで実装しています。匿名クラスは、クラス名を持たないクラスで、その場でインターフェイスを実装してインスタンスを作成できます。イベントリスナーのように、一度しか使わないクラスを定義する場合に便利です。

## ラムダ式による簡潔な記述

### ラムダ式の導入

ActionListenerのように、実装すべきメソッドが1つだけのインターフェイス（関数型インターフェイス）は、ラムダ式を使って非常に簡潔に記述できます。

<span class="listing-number">**サンプルコード19-2**</span>

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

<span class="listing-number">**サンプルコード19-3**</span>

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

<span class="listing-number">**サンプルコード19-4**</span>

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

### 基本的なマウスイベント

マウスのクリックや移動を検出する基本的な例です。

<span class="listing-number">**サンプルコード19-5**</span>

```java
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class BasicMouseEventExample extends JFrame {
    private JLabel statusLabel;
    private JPanel clickPanel;
    
    public BasicMouseEventExample() {
        setTitle("基本的なマウスイベント");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // クリック用パネル
        clickPanel = new JPanel();
        clickPanel.setBackground(Color.LIGHT_GRAY);
        clickPanel.setPreferredSize(new Dimension(400, 300));
        clickPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        
        // ステータスラベル
        statusLabel = new JLabel("パネルをクリックしてください");
        statusLabel.setHorizontalAlignment(JLabel.CENTER);
        
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
        
        add(clickPanel, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(null);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new BasicMouseEventExample().setVisible(true);
        });
    }
}
```

### 基本的なキーボードイベント

キーボード入力を検出する基本的な例です。

<span class="listing-number">**サンプルコード19-6**</span>

```java
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class BasicKeyEventExample extends JFrame {
    private JTextArea inputArea;
    private JLabel statusLabel;
    
    public BasicKeyEventExample() {
        setTitle("基本的なキーボードイベント");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // 入力エリア
        inputArea = new JTextArea(5, 30);
        inputArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(inputArea);
        
        // ステータスラベル
        statusLabel = new JLabel("キーを押してください");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
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
        
        add(scrollPane, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(null);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new BasicKeyEventExample().setVisible(true);
        });
    }
}
```

## イベント処理のベストプラクティス

### EDT上での実行

SwingのすべてのGUI操作はイベントディスパッチスレッド上で実行します。

<span class="listing-number">**サンプルコード19-7**</span>

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

<span class="listing-number">**サンプルコード19-8**</span>

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
基本的なイベント処理を理解したので、次章では、より高度なイベント処理パターンについて学習します。複雑なマウス操作、カスタムイベントの作成、パフォーマンスを考慮したイベント処理などを扱います。

## 章末演習

### 演習課題へのアクセス

本章の理解を深めるための演習課題を用意しています。以下のGitHubリポジトリで、実践的な問題に挑戦してください。

**演習課題URL**: https://github.com/Nagatani/techbook-java-primer/tree/main/exercises/chapter19/

### 課題構成

演習は難易度別に以下の3つのレベルで構成されています。

- 基礎演習: 基本的なイベントリスナの実装
- 応用演習: 複数のイベントを組み合わせた処理
- 発展演習: イベント処理の最適化とカスタマイズ

各演習には、詳細な要件と期待される実装のヒントが含まれています。