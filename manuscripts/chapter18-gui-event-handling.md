# 第18章 GUIのイベント処理

## 📝 章末演習

本章で学んだGUIイベント処理の概念を活用して、実践的な練習課題に取り組みましょう。

### 🎯 演習の目標
- 高度なGUIイベント処理の実装
- マウスとキーボードの複合的なイベント処理
- リアルタイムな双方向性を持つGUIアプリケーション
- カスタム描画と動的なUIの作成
- イベント駆動型プログラミングの深い理解

### 📁 課題の場所
演習課題は `exercises/chapter18/` ディレクトリに用意されています：

```
exercises/chapter18/
├── basic/          # 基本課題（必須）
│   ├── README.md   # 課題の詳細説明
│   ├── InteractiveDrawing.java
│   ├── FormValidator.java
│   ├── MenuSystem.java
│   └── MouseTracker.java
├── advanced/       # 発展課題（推奨）
└── challenge/      # 挑戦課題（上級者向け）
```

### 🚀 推奨する学習の進め方

1. **基本課題**から順番に取り組む
2. 各課題のREADME.mdで詳細を確認
3. TODOコメントを参考に実装
4. 各種イベントリスナーの実装パターンを習得する
5. リアルタイムな双方向性のあるアプリケーションを体験する

基本課題が完了したら、`advanced/`の発展課題でより高度なイベント処理とカスタムコンポーネントの実装に挑戦してみましょう！

## 📋 本章の学習目標

### 前提知識
**必須前提**：
- 第17章のGUIプログラミング基礎の習得
- ラムダ式と関数型インターフェイスの基本理解
- インターフェイスの実装経験

**設計経験前提**：
- 基本的なGUIコンポーネントの配置経験
- オブザーバーパターンの概念的理解

### 学習目標
**知識理解目標**：
- イベント駆動プログラミングモデルの深い理解
- Swingイベント処理機構の詳細
- 主要なイベント種類とリスナーインターフェイス
- EDTとスレッド安全性の基本概念

**技能習得目標**：
- 各種イベントリスナーの適切な実装
- ラムダ式を活用した簡潔なイベント処理
- カスタムイベントとリスナーの作成
- 複雑なユーザーインタラクションの実装

**アプリケーション設計能力目標**：
- 応答性の高いGUIアプリケーション設計
- イベント処理を活用した動的UI実装
- ユーザー体験を重視したインターフェイス設計

**到達レベルの指標**：
- 複雑なイベント処理を含むGUIアプリケーションが実装できる
- ユーザーの多様な操作に適切に応答する処理が作成できる
- カスタムコンポーネントとイベント処理が設計・実装できる
- パフォーマンスと応答性を考慮したイベント処理ができる

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
