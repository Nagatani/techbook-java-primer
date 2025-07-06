# 第17章 GUIプログラミングの基礎

## 本章の学習目標

### 前提知識
**必須前提**：
- 第11章までの総合的なJavaプログラミング能力
- イベント処理の基本概念
- オブジェクト指向設計の実践経験

**ユーザーインターフェイス前提**：
- GUIアプリケーションの使用経験
- ユーザビリティに対する基本的な理解

### 学習目標
**知識理解目標**：
- GUIプログラミングの基本概念と課題
- イベント駆動プログラミングモデルの理解
- Swingフレームワークのアーキテクチャ
- レイアウトマネージャーの種類と特徴

**技能習得目標**：
- 基本的なGUIコンポーネントの使用
- イベントリスナの実装
- レイアウトマネージャーを使った柔軟な画面設計
- 基本的なユーザーインタラクションの実装

**アプリケーション設計能力目標**：
- MVCパターンを意識したGUI設計
- ユーザビリティを考慮したインターフェイス設計
- 保守性の高いGUIアプリケーションの設計

**到達レベルの指標**：
- 基本的な機能を持つGUIアプリケーションが独力で開発できる
- ユーザーの操作に適切に応答するイベント処理が実装できる
- 画面レイアウトとコンポーネント配置を適切に設計できる
- ファイルI/Oと連携したGUIアプリケーションが作成できる

---

## 章末演習

本章で学んだGUIプログラミングの基礎概念を活用して、実践的な練習課題に取り組みましょう。

### 演習の目標
- SwingによるGUIアプリケーションの基本構築
- 基本コンポーネント（JButton、JLabel、JTextField等）の使用
- レイアウトマネージャーの理解と適用
- イベント処理の実装
- ウィンドウとダイアログの操作
- メニューシステムの構築

### 📁 課題の場所
演習課題は `exercises/chapter17/` ディレクトリに用意されています：

```
exercises/chapter17/
├── basic/          # 基本課題（必須）
│   ├── README.md   # 課題の詳細説明
│   ├── Calculator.java
│   ├── ColorPicker.java
│   ├── LayoutDemo.java
│   └── SimpleNotepad.java
├── advanced/       # 発展課題（推奨）
└── challenge/      # 挑戦課題（上級者向け）
```

### 推奨する学習の進め方

1. **基本課題**から順番に取り組む
2. 各課題のREADME.mdで詳細を確認
3. ToDoコメントを参考に実装
4. 各種レイアウトマネージャーの特徴を実際に体験する
5. イベントリスナの実装パターンを身につける

基本課題が完了したら、`advanced/`の発展課題でより複雑なGUIアプリケーション開発に挑戦してみましょう！

## 11.1 GUIプログラミングの世界へ

これまでの学習では、コンソール（黒い画面）に文字を出力し、キーボードから入力する**CUI（Character User Interface）** を中心に扱ってきました。CUIは効率的な操作が可能ですが、直感的とは言えません。

本章からは、ウィンドウやボタン、テキストボックスなどをマウスで操作する**GUI（Graphical User Interface）** アプリケーションの開発を学びます。GUIは、その視覚的な分かりやすさから、現代のあらゆるアプリケーションの標準となっています。

### イベント駆動プログラミングとは？

CUIプログラムの多くは、処理の順番をプログラマが記述する「手続き型」でした。しかし、GUIアプリケーションでは、ユーザーがいつ、どのボタンを押すか予測できません。

そこで、「**ユーザーの操作（イベント）** をきっかけに、**特定の処理（リスナ）** が動く」という**イベント駆動（Event-Driven）** というモデルが採用されています。これは、「**何かが起きたら、これを実行する**」というルールの集合でプログラムを構築する考え方です。

### JavaのGUIライブラリ

Javaには、GUIを実現するためのライブラリがいくつか存在します。

- **AWT (Abstract Window Toolkit)**: Java初期からあるライブラリ。OSネイティブの部品を使うため動作は軽快ですが、OSによる見た目の差異が大きいという特徴があります。
- **Swing**: AWTを拡張し、Java自身が部品を描画するライブラリ。OSに依存しない統一された見た目（Look & Feel）を提供でき、長年の実績があり安定しています。本書ではこのSwingを中心に学習します。
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

`JFrame`という土台の上に、さまざまな部品（コンポーネント）を配置して画面を構築していきます。しかし、直接`JFrame`に部品を置くのではなく、`JPanel`という「透明なパネル」の上に部品をグループ化して配置し、その`JPanel`を`JFrame`に配置するのが一般的です。これにより、複雑なレイアウトを整理しやすくなります。

## 11.3 画面を構成する基本コンポーネント

`JPanel`の上に配置する、代表的な画面部品を見ていきましょう。

| クラス名 | 説明 | 主な用途 |
| :--- | :--- | :--- |
| `JLabel` | 編集不可のテキストや画像を表示 | ラベル、見出し、説明文 |
| `JButton` | クリック可能なボタン | アクションの実行トリガ |
| `JTextField` | 1行のテキスト入力フィールド | ユーザー名、検索語の入力 |
| `JTextArea` | 複数行のテキスト入力・表示エリア | 長文の入力、ログの表示 |
| `JCheckBox` | オン／オフを選択できるチェックボックス | 設定の有効/無効、複数選択 |
| `JRadioButton` | 複数から1つだけ選ぶラジオボタン | 性別、配送方法の選択 |

### コンポーネントの配置とレイアウト管理

コンポーネントをどこに配置するかを決めるのが**レイアウトマネージャ**の役割です。ウィンドウサイズが変わってもレイアウトが崩れないように、ルールに従って自動で再配置してくれます。

- **`FlowLayout`**: 部品を左から右へ、行が埋まると次の行へと流れるように配置します。(`JPanel`のデフォルト）
- **`BorderLayout`**: 画面を「上下左右中央（North/South/East/West/Center)」の5領域に分割します。（`JFrame`のデフォルト）
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

## まとめ

本章では、GUIアプリケーションの「静的」な側面、つまり画面の骨格と見た目を構築する方法を学びました。`JFrame`を土台とし、`JPanel`とレイアウトマネージャーを駆使してコンポーネントを配置することで、複雑な画面も整理して作成できることを理解しました。

次章では、これらの画面に「動的」な命を吹き込むイベント処理について学び、ユーザーの操作に応答するインタラクティブなアプリケーションを作成していきます。
