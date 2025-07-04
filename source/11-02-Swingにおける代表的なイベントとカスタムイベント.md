---
title: Swingにおける代表的なイベントとカスタムイベント
---

> オブジェクト指向プログラミングおよび演習1 第11回

## Swingにおける代表的なイベントの種類

Swingのイベント駆動プログラミングでは、ユーザーのさまざまな操作が「イベント」として通知されます。イベントの種類に応じて、対応する「リスナー」をコンポーネントに登録することで、特定の操作に応じた処理を実装できます。

ここでは、代表的なイベントとそのリスナー、そして簡単なサンプルコードを紹介します。

### 1\. `ActionEvent` - アクションの発生

もっとも一般的に使用されるイベントです。ボタンのクリック、メニューの選択、テキストフィールドでEnterキーが押された時など、「何か具体的なアクションが実行された」ことを示します。

* **リスナー:** `java.awt.event.ActionListener`
* **処理メソッド:** `actionPerformed(ActionEvent e)`

```java
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ActionEventExample {
    public static void main(String[] args) {
        JFrame frame = new JFrame("ActionEvent Example");
        JButton button = new JButton("クリック！");
        JLabel label = new JLabel("ボタンが押されるのを待っています...");

        // ボタンにActionListenerを登録します（ラムダ式を使用）。
        button.addActionListener(e -> {
            // ボタンがクリックされたときにこの処理が実行されます。
            label.setText("ボタンがクリックされました！");
        });

        frame.setLayout(new java.awt.FlowLayout());
        frame.add(button);
        frame.add(label);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
```

### 2\. `MouseEvent` - マウス操作

マウスカーソルの移動やボタンのクリックなど、マウスに関連するすべての操作を扱います。

* **リスナー:** `java.awt.event.MouseListener`, `java.awt.event.MouseMotionListener`
* **アダプタクラス:** `java.awt.event.MouseAdapter`（必要なメソッドだけをオーバーライドできる便利なクラス）
* **代表的な処理メソッド:**
    * `mouseClicked(MouseEvent e)`: マウスボタンがクリックされた時。
    * `mousePressed(MouseEvent e)`: マウスボタンが押された時。
    * `mouseReleased(MouseEvent e)`: マウスボタンが離された時。
    * `mouseEntered(MouseEvent e)`: マウスカーソルがコンポーネントの領域内に入った時。
    * `mouseExited(MouseEvent e)`: マウスカーソルがコンポーネントの領域外に出た時。

```java
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MouseEventExample {
    public static void main(String[] args) {
        JFrame frame = new JFrame("MouseEvent Example");
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(200, 150));
        panel.setBackground(Color.LIGHT_GRAY);
        
        JLabel label = new JLabel("パネル上でマウスを操作してください");

        // パネルにMouseAdapterを登録します。
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                // マウスがパネルに入ったら背景色を変更します。
                panel.setBackground(Color.CYAN);
                label.setText("マウスがパネルに入りました");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // マウスがパネルから出たら背景色を元に戻します。
                panel.setBackground(Color.LIGHT_GRAY);
                label.setText("マウスがパネルから出ました");
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                // マウスがクリックされた座標を取得して表示します。
                label.setText("クリック座標: (" + e.getX() + ", " + e.getY() + ")");
            }
        });

        frame.setLayout(new FlowLayout());
        frame.add(panel);
        frame.add(label);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
```

### 3\. `KeyEvent` - キーボード操作

キーボードのキーが押された、離された、タイプされたといった操作を検知します。

  * **リスナー:** `java.awt.event.KeyListener`
  * **アダプタクラス:** `java.awt.event.KeyAdapter`
  * **代表的な処理メソッド:**
      * `keyPressed(KeyEvent e)`: キーが押された時（Shiftキーや矢印キーなども含む）。
      * `keyReleased(KeyEvent e)`: キーが離された時。
      * `keyTyped(KeyEvent e)`: 文字キーがタイプ（押されて離された）された時。

<!-- end list -->

```java
import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyEventExample {
    public static void main(String[] args) {
        JFrame frame = new JFrame("KeyEvent Example");
        JTextField textField = new JTextField(20);
        JLabel label = new JLabel("テキストフィールドにキー入力してください");
        
        // テキストフィールドにKeyAdapterを登録します。
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // 押されたキーのコードを取得します。
                int keyCode = e.getKeyCode();
                // キーコードからキーの名前を取得して表示します。
                label.setText("押されたキー: " + KeyEvent.getKeyText(keyCode));

                // 例えば、Enterキーが押されたかを判定できます。
                if (keyCode == KeyEvent.VK_ENTER) {
                    System.out.println("Enterキーが押されました！入力内容: " + textField.getText());
                }
            }
        });

        frame.setLayout(new java.awt.FlowLayout());
        frame.add(textField);
        frame.add(label);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
```

### 4\. `WindowEvent` - ウィンドウの状態変化

ウィンドウが開かれた、閉じられた、アクティブになったなど、ウィンドウ自体の状態変化を扱います。

  * **リスナー:** `java.awt.event.WindowListener`
  * **アダプタクラス:** `java.awt.event.WindowAdapter`
  * **代表的な処理メソッド:**
      * `windowOpened(WindowEvent e)`: ウィンドウが最初に開かれた時。
      * `windowClosing(WindowEvent e)`: ウィンドウの「閉じる」ボタンが押された時。
      * `windowClosed(WindowEvent e)`: ウィンドウが完全に閉じられた後。
      * `windowActivated(WindowEvent e)`: ウィンドウがアクティブになった時。

<!-- end list -->

```java
import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class WindowEventExample {
    public static void main(String[] args) {
        JFrame frame = new JFrame("WindowEvent Example");

        // デフォルトの閉じる操作を何もしないように設定します。
        // これにより、windowClosingイベントで独自の終了処理を実装できます。
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // フレームにWindowAdapterを登録します。
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // ウィンドウを閉じようとしたときに確認ダイアログを表示します。
                int option = JOptionPane.showConfirmDialog(
                    frame,
                    "本当に終了しますか？",
                    "終了確認",
                    JOptionPane.YES_NO_OPTION);
                
                // 「はい」が選択された場合のみプログラムを終了します。
                if (option == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });

        frame.setSize(300, 200);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
```

### 5\. `ItemEvent` - 項目の選択状態変化

`JCheckBox`や`JRadioButton`、`JComboBox`など、項目の選択状態が変化した時に発生します。

  * **リスナー:** `java.awt.event.ItemListener`
  * **処理メソッド:** `itemStateChanged(ItemEvent e)`

<!-- end list -->

```java
import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class ItemEventExample {
    public static void main(String[] args) {
        JFrame frame = new JFrame("ItemEvent Example");
        JCheckBox checkBox = new JCheckBox("通知を有効にする");
        JLabel label = new JLabel("通知は無効です");

        // チェックボックスにItemListenerを登録します。
        checkBox.addItemListener(e -> {
            // イベントの状態変化（選択されたか、選択解除されたか）を取得します。
            if (e.getStateChange() == ItemEvent.SELECTED) {
                label.setText("通知は有効です");
            } else {
                label.setText("通知は無効です");
            }
        });

        frame.setLayout(new java.awt.FlowLayout());
        frame.add(checkBox);
        frame.add(label);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
```

### 6\. `FocusEvent` - フォーカスの移動

GUIアプリケーションでは、一度に1つのコンポーネントだけがキーボード入力を受け付ける状態になります。この状態を「フォーカスを持つ」と言います。`FocusEvent`は、コンポーネントがフォーカスを得た時、または失った時に発生します。

入力フォームで、ユーザーが入力項目を移動したことを検知して入力チェック（バリデーション）を行ったり、入力中のフィールドを視覚的に分かりやすくしたりする際に非常に便利です。

* **リスナー:** `java.awt.event.FocusListener`
* **アダプタクラス:** `java.awt.event.FocusAdapter`
* **処理メソッド:**
    * `focusGained(FocusEvent e)`: コンポーnentがフォーカスを得た時。
    * `focusLost(FocusEvent e)`: コンポーネントがフォーカスを失った時。

```java
import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class FocusEventExample {
    public static void main(String[] args) {
        JFrame frame = new JFrame("FocusEvent Example");
        JTextField field1 = new JTextField("field1");
        JTextField field2 = new JTextField("field2");
        JLabel label = new JLabel("テキストフィールドをクリックしてフォーカスを移動させてください");

        // フォーカスを得たときに背景色を変更するFocusAdapterを作成します。
        FocusAdapter focusHighlighter = new FocusAdapter() {
            private final Color HIGHLIGHT_COLOR = new Color(255, 255, 180); // 薄い黄色
            private final Color DEFAULT_COLOR = field1.getBackground(); // デフォルトの背景色を保存

            @Override
            public void focusGained(FocusEvent e) {
                // フォーカスを得たコンポーネントの背景色を変更します。
                Component component = e.getComponent();
                component.setBackground(HIGHLIGHT_COLOR);
                label.setText(component.getName() + " がフォーカスを得ました");
            }

            @Override
            public void focusLost(FocusEvent e) {
                // フォーカスを失ったコンポーネントの背景色を元に戻します。
                Component component = e.getComponent();
                component.setBackground(DEFAULT_COLOR);
                label.setText(component.getName() + " がフォーカスを失いました");
            }
        };

        // 各テキストフィールドに名前を設定し、同じリスナーを登録します。
        field1.setName("テキストフィールド1");
        field2.setName("テキストフィールド2");
        field1.addFocusListener(focusHighlighter);
        field2.addFocusListener(focusHighlighter);

        frame.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        frame.add(field1);
        frame.add(field2);
        frame.add(label);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
```

このサンプルでは、クリックして入力対象となったテキストフィールドの背景色が変わり、どのフィールドがアクティブかが一目で分かります。

### 7\. `ChangeEvent` - コンポーネントの内部状態変化

`ChangeEvent`は、`JSlider`や`JSpinner`など、コンポーネントの内部的な値が変化したことを通知するための汎用的なイベントです。`ActionEvent`が「アクションの実行」という1回限りの操作を示すのに対し、`ChangeEvent`はスライダーのつまみをドラッグするような連続的な状態変化を捉えるのに適しています。

* **リスナー:** `javax.swing.event.ChangeListener`
* **処理メソッド:** `stateChanged(ChangeEvent e)`


```java
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class ChangeEventExample {
    public static void main(String[] args) {
        JFrame frame = new JFrame("ChangeEvent Example");
        
        // JSliderを作成します。最小値0, 最大値100, 初期値50に設定します。
        JSlider slider = new JSlider(0, 100, 50);
        slider.setPreferredSize(new Dimension(250, 50));
        slider.setMajorTickSpacing(20); // 大きい目盛りの間隔
        slider.setMinorTickSpacing(5);  // 小さい目盛りの間隔
        slider.setPaintTicks(true);     // 目盛りを表示
        slider.setPaintLabels(true);    // 目盛りの数値を表示

        JLabel label = new JLabel("現在の値: 50");
        label.setFont(new Font("Serif", Font.BOLD, 16));

        // スライダーにChangeListenerを登録します。
        slider.addChangeListener(e -> {
            // イベントソース（この場合はスライダー自身）を取得します。
            JSlider source = (JSlider) e.getSource();
            
            // isGetValueAdjusting()は、ユーザーがまだつまみをドラッグ中かどうかを返します。
            // ドラッグが完了した（つまみを離した）時にだけ処理を行いたい場合は、この判定が役立ちます。
            // 今回はリアルタイムに値を反映させたいので、この判定は使いません。
            // if (!source.getValueIsAdjusting()) { ... }

            // スライダーの現在の値を取得し、ラベルに表示します。
            int value = source.getValue();
            label.setText("現在の値: " + value);
        });

        frame.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        frame.add(slider);
        frame.add(label);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
```

このサンプルでは、スライダーのつまみを左右に動かすと、その値がリアルタイムでラベルに反映されます。音量調整や明度の設定など、インタラクティブなUIで広く応用されています。

----

## 独自のイベントを定義する方法

長い処理中に、特定のメッセージを通知させたい場合など、独自でイベントを定義し、そのイベントごとに利用者側が何らかの処理を差し込みたいシーンでカスタムイベントが活躍します。

カスタムイベント作成には、以下の2つの方法があります。

1. 独自のイベントクラスとリスナーを定義する方法（正攻法）
2. 既存の`PropertyChangeEvent`を利用する方法（とくに`SwingWorker`との連携を行う）


### 方法1：独自のイベントクラスとリスナーを定義する

これは、イベント駆動の仕組みを根本から実装する方法で、再利用性の高い独立したコンポーネントを作成する際などに有効です。

#### 手順

1.  **イベントクラスの作成:** `java.util.EventObject`を継承します。このクラスに、通知したい情報（進捗率やメッセージなど）を持たせます。
2.  **リスナーインターフェイスの作成:** `java.util.EventListener`を継承します。イベント発生時に呼び出されるメソッドを定義します。
3.  **イベント発生元の実装:** 長い処理を行うクラスに、リスナーを登録・削除する仕組みと、イベントを発火（fire）させるメソッドを実装します。
4.  **イベント受信側の実装:** GUIクラスなどでリスナーを実装し、イベントを受け取って画面を更新します。

#### サンプルコード

以下は、1秒ごとに進捗を進めるタスクを模擬し、その進捗をカスタムイベントでGUIに通知する例です。

##### Step 1 & 2: イベントとリスナーの定義

```java
import java.util.EventObject;
import java.util.EventListener;

// 1. 進捗情報を保持するカスタムイベントクラス
class ProgressEvent extends EventObject {
    private final int progress;
    private final String message;

    public ProgressEvent(Object source, int progress, String message) {
        super(source);
        this.progress = progress;
        this.message = message;
    }

    public int getProgress() { return progress; }
    public String getMessage() { return message; }
}

// 2. イベントを受け取るためのカスタムリスナーインターフェース
interface ProgressListener extends EventListener {
    void progressUpdated(ProgressEvent e);
}
```

##### Step 3 & 4: イベント発生元と受信側の実装

```java
import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.*;

// 3. 長い処理を行い、イベントを発生させるクラス
class LongTask implements Runnable {
    // リスナーを管理するためのリスト。スレッドセーフで推奨される方法。
    private final EventListenerList listenerList = new EventListenerList();

    // リスナーを追加するメソッド
    public void addProgressListener(ProgressListener listener) {
        listenerList.add(ProgressListener.class, listener);
    }
    
    // リスナーを削除するメソッド
    public void removeProgressListener(ProgressListener listener) {
        listenerList.remove(ProgressListener.class, listener);
    }
    
    // イベントを発火させ、登録されている全リスナーに通知するメソッド
    protected void fireProgressEvent(int progress, String message) {
        Object[] listeners = listenerList.getListenerList();
        ProgressEvent event = null;
        // 2つおきに実際のリスナーオブジェクトが格納されている
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ProgressListener.class) {
                if (event == null) {
                    event = new ProgressEvent(this, progress, message);
                }
                ((ProgressListener) listeners[i + 1]).progressUpdated(event);
            }
        }
    }

    @Override
    public void run() {
        for (int i = 0; i <= 100; i++) {
            try {
                Thread.sleep(50); // 重い処理をシミュレート
            } catch (InterruptedException ignored) {}

            // 10%進むごとにイベントを発火させる
            if (i % 10 == 0) {
                fireProgressEvent(i, i + "% 完了...");
            }
        }
        fireProgressEvent(100, "処理が完了しました！");
    }
}

// 4. GUI（イベント受信側）
public class CustomEventApp extends JFrame implements ProgressListener {
    private final JProgressBar progressBar;
    private final JLabel label;

    public CustomEventApp() {
        setTitle("カスタムイベントの例");
        progressBar = new JProgressBar(0, 100);
        label = new JLabel("待機中...");
        
        JButton startButton = new JButton("開始");
        startButton.addActionListener(e -> {
            startButton.setEnabled(false);
            LongTask task = new LongTask();
            // GUI（this）をリスナーとしてタスクに登録
            task.addProgressListener(this);
            // タスクをバックグラウンドスレッドで実行
            new Thread(task).start();
        });

        setLayout(new FlowLayout());
        add(startButton);
        add(progressBar);
        add(label);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
    }

    // イベントを受け取ったときに実行されるメソッド
    @Override
    public void progressUpdated(ProgressEvent e) {
        // 重要：イベント発生元は別スレッドなので、GUI更新は必ずEDTで行う
        SwingUtilities.invokeLater(() -> {
            progressBar.setValue(e.getProgress());
            label.setText(e.getMessage());
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CustomEventApp().setVisible(true));
    }
}
```

### 方法2：`PropertyChangeEvent`と`SwingWorker`を利用する

方法1は柔軟ですが、コードの記述量が多くなります。

とくに「バックグラウンド処理の進捗通知」というシナリオでは、Swingに標準で備わっている仕組みを利用する方がはるかに簡単で実用的です。

`SwingWorker`は、内部で`PropertyChangeEvent`というイベントを発火させる仕組みを標準で持っています。`SwingWorker`の`setProgress(int progress)`メソッドを呼ぶと、`"progress"`という名前のプロパティが変更されたと見なされ、`PropertyChangeEvent`が自動的に発火します。

これを利用すれば、独自のイベントクラスやリスナーを一切定義することなく、目的を達成できます。

#### サンプルコード

```java
import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class SwingWorkerEventApp extends JFrame {
    private final JProgressBar progressBar;
    private final JLabel label;
    private final JButton startButton;

    public SwingWorkerEventApp() {
        setTitle("SwingWorkerとPropertyChangeEventの例");
        progressBar = new JProgressBar(0, 100);
        label = new JLabel("待機中...");
        startButton = new JButton("開始");
        
        startButton.addActionListener(e -> {
            startButton.setEnabled(false);
            executeTask();
        });
        
        setLayout(new FlowLayout());
        add(startButton);
        add(progressBar);
        add(label);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
    }

    private void executeTask() {
        // SwingWorkerを作成
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                for (int i = 0; i <= 100; i++) {
                    Thread.sleep(50);
                    // このメソッドを呼ぶと、内部でPropertyChangeEventが発火する
                    setProgress(i);
                }
                return null;
            }

            @Override
            protected void done() {
                label.setText("処理が完了しました！");
                startButton.setEnabled(true);
            }
        };

        // workerにPropertyChangeListenerを登録
        worker.addPropertyChangeListener(evt -> {
            // プロパティ名が "progress" の場合のみ処理する
            if ("progress".equals(evt.getPropertyName())) {
                // getNewValue()で新しい進捗率を取得できる
                int progress = (Integer) evt.getNewValue();
                progressBar.setValue(progress);
                label.setText(progress + "% 完了...");
            }
        });
        
        worker.execute();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SwingWorkerEventApp().setVisible(true));
    }
}
```

### まとめ

| 方法 | メリット | デメリット | オススメのケース |
| :--- | :--- | :--- | :--- |
| **1. 独自イベント作成** | ・イベント駆動の仕組みを深く学べる<br>・完全に独立したコンポーネント設計が可能 | ・コードの記述量が多い<br>・スレッドセーフティを自前で考慮する必要がある | Swingに依存しないライブラリや、非常に複雑なカスタムコンポーネントを作成する場合。 |
| **2. `SwingWorker`利用** | ・コードが非常にシンプルで簡潔<br>・`SwingWorker`がスレッド管理を自動で行う<br>・Swingの非同期処理のベストプラクティス | ・`SwingWorker`の知識が必要<br>・Swing環境に依存する | **GUIを持つアプリケーションでの非同期処理や、進捗通知のようなシナリオでは、こちらの方法を強く推奨します。** |