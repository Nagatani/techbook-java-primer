# 第11章 GUIアプリケーション開発の基礎

## 🎯総合演習プロジェクトへのステップ

いよいよ、**総合演習プロジェクト「TODOリストアプリケーション」** の見た目と操作性を作り込む章に入ります。本章で学ぶGUI開発の知識は、プロジェクトの完成に不可欠です。

- **ウィンドウとレイアウト**: `JFrame`と`JPanel`、そして各種レイアウトマネージャを駆使して、TODOアプリのメイン画面を設計・構築します。
- **コンポーネントの配置**: `JTextField`（タスク入力）、`JButton`（追加ボタン）、`JList`（タスク一覧）、`JCheckBox`（完了チェック）といった部品を適切に配置します。
- **イベント処理**: 「追加」ボタンがクリックされたらタスクリストに新しい`Task`を追加する、といったユーザー操作に応じた処理を`ActionListener`を使って実装します。

この章で、これまで学んできたクラスやコレクションといった内部的なロジックが、ユーザーが直接触れることのできる具体的な「形」になる過程を体験します。

## 📋 本章の学習目標

### 前提知識
- **クラスとオブジェクトの理解**: `new`キーワードを使ったインスタンスの生成。
- **メソッドの呼び出し**: オブジェクトのメソッドを使って処理を実行する基本的な知識。
- **継承の概念**: `extends`を使ったクラスの拡張（特に`JFrame`を継承する場合）。
- **インターフェイスの実装**: `implements`を使ったインターフェイスの実装（特にイベントリスナー）。

### 到達目標

#### 知識理解目標
- CUIとGUIの違い、およびイベント駆動プログラミングの基本概念を理解する。
- JavaにおけるGUIライブラリ（AWT, Swing, JavaFX）の概要を知る。
- `JFrame`, `JPanel`, `JButton`, `JLabel`など、Swingの基本コンポーネントの役割を理解する。
- `FlowLayout`, `BorderLayout`, `GridLayout`など、主要なレイアウトマネージャの役割と使い方を理解する。
- イベント処理の3要素（イベントソース、イベント、イベントリスナー）の関係��を理解する。

#### 技能習得目標
- `JFrame`を使って基本的なウィンドウを表示し、設定できる。
- `JPanel`を組み合わせて、複数のレイアウトマネージャを使った画面を構築できる。
- `JButton`がクリックされたときに、`ActionListener`を使って特定の処理を実行できる。
- `JTextField`からユーザーの入力を取得し、`JLabel`の表示を更新できる。
- `JOptionPane`を使って、簡単なメッセージや確認ダイアログを表示できる。

---

## 11.1 GUIプログラミングの世界へ

これまでの学習では、コンソール（黒い画面）に文字を出力し、キーボードから入力する**CUI（Character User Interface）** を中心に扱ってきました。CUIは効率的な操作が可能ですが、直感的とは言えません。

本章からは、ウィンドウやボタン、テキストボックスなどをマウスで操作する**GUI（Graphical User Interface）** アプリケーションの開発を学びます。GUIは、その視覚的な分かりやすさから、現代のあらゆるアプリケーションの標準となっています。

### イベント駆動プログラミングとは？

CUIプログラムの多くは、処理の順番をプログラマが記述する「手続き型」で���た。しかし、GUIアプリケーションでは、ユーザーがいつ、どのボタンを押すか予測できません。

そこで、「**ユーザーの操作（イベント）** をきっかけに、**特定の処理（リスナー）** が動く」という**イベント駆動（Event-Driven）** というモデルが採用されています。これは、「**何かが起きたら、これを実行する**」というルールの集合でプログラムを構築する考え方です。

### JavaのGUIライブラリ

Javaには、GUIを実現するためのライブラリがいくつか存在します。

- **AWT (Abstract Window Toolkit)**: Java初期からあるライブラリ。OSネイティブの部品を使うため動作は軽快ですが、OSによる見た目の差異が大きいという特徴があります。
- **Swing**: AWTを拡張し、Java自身が部品を描画するライブラリ。OSに依存しない統一された見た目（Look & Feel）を提供でき、長年の実績があり安定しています。本講義ではこのSwingを中心に学習します。
- **JavaFX**: Swingの後継として開発された、よりモダンで表現力豊かなライブラリ。現在では標準ライブラリから外れ、オープンソースプロジェクトとして開発が継続されています。

## 11.2 Swingによる画面作成の第一歩

### `JFrame`: すべての土台となるウィンドウ

Swingアプリケーションは、`JFrame`クラスのインスタンス、つまり「ウィンドウ」から始まります。

```java
import javax.swing.JFrame;

public class MyFirstFrame {
    public static void main(String[] args) {
        // 1. JFrameクラスのインスタンスを作成（これがウィンドウの実体）
        JFrame frame = new JFrame();

        // 2. ウィンドウのタイトルを設定
        frame.setTitle("はじめてのSwing");

        // 3. ウィンドウのサイズをピクセル単位で設定
        frame.setSize(400, 300);

        // 4. 閉じるボタン（×）の動作を設定（プログラムを終了する）
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 5. ウィンドウを画面中央に表示
        frame.setLocationRelativeTo(null);

        // 6. ウィンドウを画面に表示（これが無いと表示されない）
        frame.setVisible(true);
    }
}
```

このコードを実行すると、400x300ピクセルの空のウィンドウが画面中央に表示されます。

### `JPanel`: コンポーネントをまとめる透明なパネル

`JFrame`という土台の上に、さまざまな部品（コンポー��ント）を配置して画面を構築していきます。しかし、直接`JFrame`に部品を置くのではなく、`JPanel`という「透明なパネル」の上に部品をグループ化して配置し、その`JPanel`を`JFrame`に配置するのが一般的です。これにより、複雑なレイアウトを整理しやすくなります。

## 11.3 画面を構成する基本コンポーネント

`JPanel`の上に配置する、代表的な画面部品を見ていきましょう。

| クラス名 | 説明 | 主な用途 |
| :--- | :--- | :--- |
| `JLabel` | 編集不可のテキストや画像を表示 | ラベル、見出し、説明文 |
| `JButton` | クリック可能なボタン | アクションの実行トリガー |
| `JTextField` | 1行のテキスト入力フィールド | ユーザー名、検索語の入力 |
| `JTextArea` | 複数行のテキスト入力・表示エリア | 長文の入力、ログの表示 |
| `JCheckBox` | ON/OFFを選択できるチェックボックス | 設定の有効/無効、複数選択 |
| `JRadioButton` | 複数から1つだけ選ぶラジオボタン | 性別、配送方法の選択 |

### コンポーネントの配置とレイアウト管理

コンポーネントをどこに配置するかを決めるのが**レイアウトマネージャ**の役割です。ウィンドウサ��ズが変わってもレイアウトが崩れないように、ルールに従って自動で再配置してくれます。

- **`FlowLayout`**: 部品を左から右へ、行が埋まると次の行へと流れるように配置します。(`JPanel`のデフォルト)
- **`BorderLayout`**: 画面を「上下左右中央(North/South/East/West/Center)」の5領域に分割します。(`JFrame`のデフォルト)
- **`GridLayout`**: 画面を格子状（マス目）に分割し、全部品を同じサイズで配置します。

#### レイアウトの組み合わせによる画面構築

`JPanel`を入れ子にすることで、これらのレイアウトを組み合わせて複雑な画面を構築できます。

```java
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ComplexLayoutExample {
    public static void main(String[] args) {
        JFrame frame = new JFrame("複雑なレイアウト");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        // --- 上部パネル (FlowLayout) ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JTextField(25));
        topPanel.add(new JButton("検索"));
        
        // --- 中央のテキストエリア ---
        JTextArea centerTextArea = new JTextArea();
        
        // --- 下部パネル (FlowLayout) ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(new JButton("OK"));
        bottomPanel.add(new JButton("キャンセル"));

        // --- フレームに各パーツを配置 (BorderLayout) ---
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(new JScrollPane(centerTextArea), BorderLayout.CENTER); // スクロール可能にする
        frame.add(bottomPanel, BorderLayout.SOUTH);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
```

## 11.4 イベント処理入門

GUIアプリケーションを「動かす」ためのイベント処理を実装します。

### イベント処理の3要素

1.  **イベントソース**: イベントを発生させる部品 (`JButton`など)。
2.  **イベントオブジェクト**: 発生したイベントの詳細情報 (`ActionEvent`など)。
3.  **イベントリスナー**: イベントを監視し、処理を実行するオブジェクト (`ActionListener`など)。

処理の流れは、「ユーザーが**イベントソース**を操作 ��� **イベントオブジェクト**が生成され、登録済みの**イベントリスナー**に通知 → リスナー内の処理が実行される」となります。

### ボタンクリックに応答する

もっとも基本的な、ボタンクリックイベントを処理してみましょう。

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

        // 1. イベントリスナーを作成
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

## 11.5 代表的なイベントの種類

Swingには様々なイベントがあります。目的に応じて適切なリスナーを使い分けましょう。

| イベント | 説明 | 主なリスナー |
| :--- | :--- | :--- |
| `ActionEvent` | ボタンクリックなど、明確なアクション | `ActionListener` |
| `MouseEvent` | マウスのクリック、カーソルの出入り | `MouseListener` |
| `KeyEvent` | キーボードのキー操作 | `KeyListener` |
| `WindowEvent` | ウィンドウが開く、閉じるなどの状態変化 | `WindowListener` |
| `ItemEvent` | チェックボックスなどの項目選択状態の変化 | `ItemListener` |
| `FocusEvent` | コンポーネントがフォーカスを得る/失う | `FocusListener` |
| `ChangeEvent` | スライダーなど、コンポーネントの内部状態の連続的な変化 | `ChangeListener` |

### `WindowListener`で終了確認

ウィンドウを閉じる際に確認ダイアログを表示する例です。`WindowAdapter`を使うと、必要なメソッドだけを実装できます。

```java
import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class WindowEventExample {
    public static void main(String[] args) {
        JFrame frame = new JFrame("終了確認");
        // デフォルトの閉じる操作を無効化
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // WindowAdapterを登録
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

本章では、GUIアプリケーションの基本的な考え方と、Swingを使った画面構築、そしてイベント処理の初歩を学びました。次章では、より実践的なコンポーネントの使い方や、応答性の高いアプリケーションを作成するためのテクニックを学びます。