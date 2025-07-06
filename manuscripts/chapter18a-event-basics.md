# 第18章 GUIのイベント処理 - Part A: イベント処理入門

## 12.1 イベント処理入門

前章で作成した画面は、まだ見た目だけの「静的」なものでした。GUIアプリケーションを「動かす」ためには、ユーザーの操作（イベント）に応じた処理（イベントハンドリング）を実装する必要があります。

### イベント処理の3要素

Swingのイベント処理は、以下の3つの要素で構成されます。

1.  **イベントソース (Event Source)**: イベントを発生させる部品 (`JButton`、`JTextField`など)。
2.  **イベントオブジェクト (Event Object)**: 発生したイベントの詳細情報を持つオブジェクト (`ActionEvent`、`MouseEvent`など)。
3.  **イベントリスナ (Event Listener)**: イベントの発生を監視し、通知を受け取って実際の処理を実行するオブジェクト (`ActionListener`など)。

処理の流れは、「ユーザーが**イベントソース**を操作 → **イベントオブジェクト**が生成され、登録済みの**イベントリスナ**に通知 → リスナ内の処理が実行される」となります。

### ボタンクリックに応答する

最も基本的な、ボタンクリックイベントを処理してみましょう。

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

### ラムダ式による簡潔な記述

`ActionListener`のように、実装すべきメソッドが1つだけのインターフェイス（**関数型インターフェイス**）は、ラムダ式を使って非常に簡潔に記述できます。

```java
// 上記の匿名クラスの部分をラムダ式で書き換える
button.addActionListener(e -> {
    JOptionPane.showMessageDialog(frame, "ボタンがクリックされました！");
});

// 処理が1行なら波括弧も省略可能
button.addActionListener(e -> JOptionPane.showMessageDialog(frame, "ボタンがクリックされました！"));
```
これ以降のサンプルコードでは、主にこのラムダ式を使っていきます。

### 簡単なアプリケーション：挨拶プログラム

テキストフィールドに入力された名前を使って、挨拶メッセージを表示するプログラムを作成しましょう。

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

### より実践的な例：カウンタアプリケーション

ボタンクリックで数値を増減させるカウンタアプリケーションを作成してみましょう。

```java
import javax.swing.*;
import java.awt.*;

public class CounterApp extends JFrame {
    private int count = 0;
    private JLabel countLabel;
    
    public CounterApp() {
        setTitle("カウンターアプリケーション");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());
        
        // カウント表示用ラベル
        countLabel = new JLabel("カウント: 0");
        countLabel.setFont(new Font("Arial", Font.BOLD, 24));
        
        // ボタンの作成
        JButton incrementButton = new JButton("+1");
        JButton decrementButton = new JButton("-1");
        JButton resetButton = new JButton("リセット");
        
        // イベントリスナーの登録
        incrementButton.addActionListener(e -> {
            count++;
            updateCountLabel();
        });
        
        decrementButton.addActionListener(e -> {
            count--;
            updateCountLabel();
        });
        
        resetButton.addActionListener(e -> {
            count = 0;
            updateCountLabel();
        });
        
        // コンポーネントの配置
        add(countLabel);
        add(incrementButton);
        add(decrementButton);
        add(resetButton);
        
        pack();
        setLocationRelativeTo(null);
    }
    
    private void updateCountLabel() {
        countLabel.setText("カウント: " + count);
        // カウントに応じて色を変更
        if (count > 0) {
            countLabel.setForeground(Color.BLUE);
        } else if (count < 0) {
            countLabel.setForeground(Color.RED);
        } else {
            countLabel.setForeground(Color.BLACK);
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CounterApp().setVisible(true);
        });
    }
}
```

### イベント処理のベストプラクティス

#### 1. Event Dispatch Thread (EDT) の理解

SwingのすべてのGUI操作は、Event Dispatch Thread (EDT) という専用のスレッドで実行される必要があります。

```java
// 正しい方法：EDTでGUIを作成
SwingUtilities.invokeLater(() -> {
    JFrame frame = new JFrame("EDT Example");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(300, 200);
    frame.setVisible(true);
});
```

#### 2. 長時間かかる処理の扱い

イベントハンドラ内で時間のかかる処理を実行すると、GUIがフリーズしてしまいます。

```java
public class LongTaskExample extends JFrame {
    private JButton startButton;
    private JProgressBar progressBar;
    private JLabel statusLabel;
    
    public LongTaskExample() {
        setTitle("長時間処理の例");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());
        
        startButton = new JButton("処理開始");
        progressBar = new JProgressBar(0, 100);
        statusLabel = new JLabel("待機中...");
        
        startButton.addActionListener(e -> {
            startButton.setEnabled(false);
            statusLabel.setText("処理中...");
            
            // SwingWorkerを使って別スレッドで実行
            SwingWorker<Void, Integer> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() throws Exception {
                    for (int i = 0; i <= 100; i++) {
                        Thread.sleep(50); // 重い処理をシミュレート
                        publish(i); // 進捗を通知
                    }
                    return null;
                }
                
                @Override
                protected void process(java.util.List<Integer> chunks) {
                    // EDTで実行される
                    int progress = chunks.get(chunks.size() - 1);
                    progressBar.setValue(progress);
                }
                
                @Override
                protected void done() {
                    // 処理完了時の処理
                    statusLabel.setText("完了！");
                    startButton.setEnabled(true);
                }
            };
            
            worker.execute();
        });
        
        add(startButton);
        add(progressBar);
        add(statusLabel);
        
        pack();
        setLocationRelativeTo(null);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LongTaskExample().setVisible(true);
        });
    }
}
```

### 複数のイベントソースの処理

1つのリスナで複数のイベントソースを処理することもできます。

```java
public class MultiButtonExample extends JFrame implements ActionListener {
    private JLabel displayLabel;
    
    public MultiButtonExample() {
        setTitle("複数ボタンの例");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        displayLabel = new JLabel("ボタンをクリックしてください", JLabel.CENTER);
        displayLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        
        JPanel buttonPanel = new JPanel();
        String[] colors = {"赤", "緑", "青", "黄", "黒"};
        
        for (String color : colors) {
            JButton button = new JButton(color);
            button.setActionCommand(color); // アクションコマンドを設定
            button.addActionListener(this); // 同じリスナーを登録
            buttonPanel.add(button);
        }
        
        add(displayLabel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        setSize(400, 200);
        setLocationRelativeTo(null);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        displayLabel.setText(command + "が選択されました");
        
        // 色に応じて背景色を変更
        switch (command) {
            case "赤":
                displayLabel.setForeground(Color.RED);
                break;
            case "緑":
                displayLabel.setForeground(Color.GREEN);
                break;
            case "青":
                displayLabel.setForeground(Color.BLUE);
                break;
            case "黄":
                displayLabel.setForeground(Color.YELLOW);
                displayLabel.setOpaque(true);
                displayLabel.setBackground(Color.BLACK);
                break;
            case "黒":
                displayLabel.setForeground(Color.BLACK);
                displayLabel.setBackground(null);
                displayLabel.setOpaque(false);
                break;
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MultiButtonExample().setVisible(true);
        });
    }
}
```

## まとめ

このパートでは、GUIイベント処理の基礎を学習しました：

- **イベント処理の3要素**：イベントソース、イベントオブジェクト、イベントリスナ
- **ActionListener**：ボタンクリックなどのアクションイベントの処理
- **ラムダ式**：簡潔なイベントリスナの記述方法
- **EDT**：SwingのGUI操作は必ずEvent Dispatch Threadで実行
- **SwingWorker**：長時間処理を別スレッドで実行

これらの基礎知識を元に、次のパートではより多様なイベントタイプについて学習します。

---

次のパート：[Part B - 代表的なイベントの種類](chapter18b-event-types.md)