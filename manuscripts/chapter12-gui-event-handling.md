# 第12章 GUIのイベント処理

## 🎯総合演習プロジェクトへのステップ

本章で学ぶ「イベント処理」は、**総合演習プロジェクト「ToDoリストアプリケーション」** を、単なる表示物から実際に「使える」アプリケーションへと進化させるための核心部分です。

- **タスクの追加**:「追加」ボタンがクリックされたら、`JTextField`から入力内容を取得し、新しい`Task`オブジェクトを生成してリストに追加する処理を実装します。
- **タスクの完了**: 各タスクの横にある`JCheckBox`がチェックされたら、対応する`Task`オブジェクトの完了状態を更新し、見た目に反映させる（例：取り消し線を引く）処理を実装します。

ユーザーのあらゆる操作（イベント）に応答するロジックを記述することで、アプリケーションに命を吹き込みます。

## 📋 本章の学習目標

### 前提知識
- **第11章の知識**: `JFrame`, `JButton`, `JTextField`などの基本的なコンポーネントを配置できる。
- **インターフェイスの実装**: `implements`を使ったインターフェイスの実装。
- **ラムダ式の基本**: 簡単なラムダ式を読み書きできる。

### 到達目標

#### 知識理解目標
- イベント処理の3要素（イベントソース、イベント、イベントリスナ）の関係性を説明できる。
- `ActionEvent`や`MouseEvent`など、代表的なイベントの種類と用途を理解する。
- `ActionListener`や`WindowAdapter`などの主要なリスナの役割を理解する。

#### 技能習得目標
- `JButton`がクリックされたときに、`ActionListener`を使って特定の処理を実行できる。
- `JTextField`からユーザーの入力を取得し、`JLabel`の表示を更新できる。
- `JOptionPane`を使って、簡単なメッセージや確認ダイアログを表示できる。
- ラムダ式を使ってイベントリスナを簡潔に記述できる。
- ウィンドウを閉じる際の確認処理を実装できる。

---

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

## 12.2 代表的なイベントの種類

Swingに挟まざまなイベントがあります。目的に応じて適切なリスナを使い分けましょう。

| イベント | 説明 | 主なリスナ |
| :--- | :--- | :--- |
| `ActionEvent` | ボタンクリックなど、明確なアクション | `ActionListener` |
| `MouseEvent` | マウスのクリック、カーソルの出入り | `MouseListener` |
| `KeyEvent` | キーボードのキー操作 | `KeyListener` |
| `WindowEvent` | ウィンドウが開く、閉じるなどの状態変化 | `WindowListener` |
| `ItemEvent` | チェックボックスなどの項目選択状態の変化 | `ItemListener` |
| `FocusEvent` | コンポーネントがフォーカスを得る/失う | `FocusListener` |
| `ChangeEvent` | スライダーなど、コンポーネントの内部状態の連続的な変化 | `ChangeListener` |

### `WindowListener`で終了確認

ウィンドウを閉じる際に確認ダイアログを表示する例です。`WindowListener`インターフェイスには多くのメソッドがありますが、`WindowAdapter`クラスを継承することで、必要なメソッドだけをオーバーライドして実装できます。

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

## まとめ

本章では、GUIアプリケーションに命を吹き込むイベント処理の基本を学びました。

- イベント処理は「**イベントソース**」「**イベントオブジェクト**」「**イベントリスナ**」の3要素で構成されます。
- `ActionListener`を始めとする各種リスナをイベントソースに登録することで、ユーザーの操作に応じた処理を実行できます。
- **ラムダ式**を使うことで、イベント処理の記述を大幅に簡潔にできます。

これで、見た目を作り、操作に応答する、インタラクティブなアプリケーションの基礎が固まりました。
