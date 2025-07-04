# 第12章 イベント駆動プログラミング

## 🎯総合演習プロジェクトへのステップ

本章で学ぶイベント処理は、**総合演習プロジェクト「TODOリストアプリケーション」** に命を吹き込むための重要なステップです。ユーザーの操作とアプリケーションのロジックを結びつけます。

- **`ActionListener`**: 「追加」ボタンがクリックされたら、`JTextField`からテキストを取得し、新しい`Task`オブジェクトを作成して`ArrayList`に追加する、という一連の処理を実装します。
- **イベントソースとリスナー**: `JButton`（イベントソース）に`ActionListener`（イベントリスナー）を登録することで、クリック操作を検知し、対応する処理を呼び出します。
- **ラムダ式の活用**: イベントリスナーの実装をラムダ式で記述することで、コードを大幅に簡潔化し、可読性を高めます。

この章を終える頃には、ユーザーが操作できる、対話的なアプリケーションの基本的な仕組みを実装できるように���ります。

## 📋 本章の学習目標

### 前提知識
- **第11章のGUI画面構築**: `JFrame`, `JButton`, `JTextField`などの基本的なコンポーネントを配置できる。
- **ラムダ式の基本**: 簡単なラムダ式を読み書きできる。

### 到達目標

#### 知識理解目標
- イベント処理の3要素（イベントソース、イベントオブジェクト、イベントリスナー）の関係性を説明できる。
- `ActionListener`がボタンクリックなどのアクションを処理するためのインターフェイスであることを理解する。
- `ActionEvent`が、発生したアクションに関する情報を持っていることを理解する。
- `WindowListener`, `MouseListener`など、代表的なイベントリスナーの種類と用途を知る。

#### 技能習得目標
- `JButton`がクリックされたときに、`ActionListener`を使って特定の処理を実行できる。
- `JTextField`からユーザーの入力を取得し、`JLabel`の表示を更新する、といったコンポーネント間の連携を実装できる。
- `JOptionPane`を使って、簡単なメッセージや確認ダイアログを表示できる。
- ラムダ式を使ってイベントリスナーを簡潔に記述できる。

---

## 12.1 イベント処理の仕組��

GUIアプリケーションを「動かす」ためには、イベント処理の実装が不可欠です。Swingのイベント処理は、以下の3つの要素で構成されます。

1.  **イベントソース (Event Source)**: イベントを発生させる部品（例: `JButton`, `JTextField`）。
2.  **イベントオブジェクト (Event Object)**: 発生したイベントの詳細情報を持つオブジェクト（例: `ActionEvent`はボタンがクリックされたことを示す）。
3.  **イベントリスナー (Event Listener)**: イベントを監視し、イベントが発生したときに実行される処理を記述したオブジェクト（例: `ActionListener`）。

処理の流れは、「ユーザーが**イベントソース**を操作 → **イベントオブジェクト**が生成され、登録済みの**イベントリスナー**に通知 → リスナー内の処理が実行される」となります。

## 12.2 アクションイベントの処理

もっとも基本的で頻繁に使われるのが、ボタンのクリックなどを処理する`ActionEvent`です。

### `ActionListener`によるイベント処理

`JButton`がクリックされたことを検知するには、`ActionListener`インターフェイスを実装したクラス（リスナー）を、ボタン（ソース）に登録します。

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
            // 2. ボタンがクリックされた時の処理を actionPerformed メソッドに記述
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

`ActionListener`は、実装すべきメソッドが`actionPerformed`一つだけの**関数型インターフェイス**です。そのため、ラムダ式を使うことで、上記の冗長な匿名クラスの記述を大幅に簡潔にできます。

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

テキストフィールドに入力された名前を使って、挨拶メッセージを表示するプログラムを作成しましょう。これは、コンポーネント間の連携の基本です。

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

## 12.3 その他の代表的なイベント

Swingには様々なイベントがあります。目的に応じて適切なリスナーを使い分けましょう。

| イベント | 説明 | 主なリスナー |
| :--- | :--- | :--- |
| `ActionEvent` | ボタンクリックなど、明確なアクション | `ActionListener` |
| `MouseEvent` | マウスのクリック、カーソルの出入り | `MouseListener` |
| `KeyEvent` | キ��ボードのキー操作 | `KeyListener` |
| `WindowEvent` | ウィンドウが開く、閉じるなどの状態変化 | `WindowListener` |
| `ItemEvent` | チェックボックスなどの項目選択状態の変化 | `ItemListener` |
| `FocusEvent` | コンポーネントがフォーカスを得る/失う | `FocusListener` |
| `ChangeEvent` | スライダーなど、コンポーネントの内部状態の連続的な変化 | `ChangeListener` |

### `WindowListener`で終了確認

ウィンドウを閉じる際に確認ダイアログを表示する例です。`WindowListener`には多くのメソッドがありますが、`WindowAdapter`クラスを継承することで、必要なメソッドだけをオーバーライドして実装できます。

```java
import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class WindowEventExample {
    public static void main(String[] args) {
        JFrame frame = new JFrame("終了確認");
        // デフォルトの閉じる操作を無効化し、自前の処理を実装する
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // WindowAdapterを匿名クラスで実装し、リスナーとして登録
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

- **イベント処理の3要素**: イベントソース、イベントオブジェクト、イベントリスナーの関係を理解しました。
- **`ActionListener`**: ボタンクリックなどのアクションを処理する最も基本的な方法を学びました。
- **ラムダ式**: イベントリスナーの記述を劇的に簡潔にする方法を習得しました。
- **コンポーネント連携**: あるコンポーネントのイベントが、別のコンポーネントの状態を変化させる、という対話的なプログラムの基礎を実装しました。

次章では、`JList`や`JTable`といった、より高度なコンポーネントの使い方を学び、さらにリッチなアプリケーション開発に挑戦します。