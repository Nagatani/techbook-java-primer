# 第18章 GUIのイベント処理

## 章末演習

本章で学んだGUIイベント処理の概念を活用して、実践的な練習課題に取り組みましょう。

### 🎯 演習の目標
- 高度なGUIイベント処理の実装
- マウスとキーボードの複合的なイベント処理
- リアルタイムな双方向性を持つGUIアプリケーション
- カスタム描画と動的なUIの作成
- イベント駆動型プログラミングの深い理解

### 演習課題の難易度レベル

#### 🟢 基礎レベル（Basic）
- **目的**: 高度なGUIイベント処理とリアルタイム更新の実装
- **所要時間**: 60-90分/課題
- **前提**: 本章の内容と第17章のGUI基礎を理解していること
- **評価基準**: 
  - 複雑なイベント処理の適切な実装
  - ユーザーの多様な操作への応答
  - 動的なUI更新とカスタム描画
  - リアルタイム性とパフォーマンス

---

## 🟢 基礎レベル課題（必須）

### 課題1: インタラクティブ描画アプリケーション

**学習目標：** MouseListener、MouseMotionListenerの詳細実装、Graphics2Dによるカスタム描画、アンドゥ・リドゥ機能の設計

**問題説明：**
マウス操作で図形を描画できるインタラクティブな描画アプリケーションを作成してください。

**要求仕様：**
- マウスドラッグによる線・矩形・楕円の描画
- 色選択機能（パレットまたはカラーチューザー）
- 線の太さ調整スライダー
- 描画モード選択（ペン、矩形、楕円、消しゴム）
- 描画のアンドゥ・リドゥ機能
- 描画内容のファイル保存・読み込み

**実行例：**
```
=== インタラクティブ描画アプリケーション ===
ウィンドウ表示: "Interactive Drawing Application"
サイズ: 800x600ピクセル

ツールバー構成:
┌─────────────────────────────┐
│ モード: [ペン▼] 色:[■] 太さ:━━━○━━ │
│ [クリア] [戻す] [進む]         │
└─────────────────────────────┘

描画エリア:
┌─────────────────────────────┐
│                           │
│   ○ ← マウスで自由描画     │
│  /|\                      │
│  / \  □ ← 矩形描画        │
│                           │
│      ◯ ← 楕円描画         │
└─────────────────────────────┘

操作テスト:
1. ペンモード: マウスドラッグで自由描画
2. 矩形モード: ドラッグで矩形描画
3. 色変更: カラーボタンでJColorChooser表示
4. アンドゥ: 前の操作を取り消し
5. 保存: PNG形式でファイル保存
```

**評価ポイント：**
- MouseListener、MouseMotionListenerの適切な実装
- Graphics2Dによるカスタム描画の実装
- アンドゥ・リドゥ機能の設計

**実装ヒント：**
- Graphics2DのsetRenderingHint()でアンチエイリアス設定
- BufferedImageでオフスクリーン描画
- ArrayListで描画済み図形のリストを管理

---

### 課題2: フォームバリデーター

**学習目標：** DocumentListenerによるリアルタイム入力監視、正規表現を使った入力値検証、視覚的フィードバックの実装

**問題説明：**
リアルタイムバリデーション機能付きのフォームアプリケーションを作成してください。

**要求仕様：**
- 複数の入力フィールド（名前、メール、電話番号、パスワード、確認パスワード）
- リアルタイムバリデーション（文字入力時に即座にチェック）
- バリデーション結果の視覚的フィードバック（色変更、アイコン表示）
- 送信ボタンの有効/無効制御
- キーボードショートカット（Enter で次フィールド移動等）

**実行例：**
```
=== フォームバリデーター ===
ウィンドウ表示: "Form Validator - リアルタイムバリデーション"
サイズ: 600x400ピクセル

バリデーション状況:
┌─────────────────────────────┐
│ ✓ すべての入力が正しく完了しています │
└─────────────────────────────┘

フォーム構成:
┌─────────────────────────────┐
│ 名前:     [田中太郎_______] ✓  │
│ メール:   [tanaka@example.com] ✓ │
│ 電話番号: [090-1234-5678__] ✓  │
│ パスワード: [********____] ✓   │
│ 確認:     [********____] ✓   │
│                           │
│      [送信] [リセット]      │
└─────────────────────────────┘

バリデーション例:
❌ 名前が空 → "名前を入力してください"
❌ 無効メール → "有効なメールアドレスを入力してください"
❌ 短いパスワード → "パスワードは8文字以上で入力してください"
❌ パスワード不一致 → "パスワードが一致しません"
✓ 有効入力 → 緑色のボーダーとチェックマーク表示
```

**評価ポイント：**
- DocumentListenerによるリアルタイム入力監視
- 正規表現を使った入力値の検証
- 視覚的フィードバックの実装

**実装ヒント：**
- Timer クラスで入力完了後の遅延バリデーション
- setBorder() や setBackground() で視覚的フィードバック
- ActionMap と InputMap でキーバインディング設定

---

### 課題3: 高度なメニューシステム

**学習目標：** JMenuBar、JMenu、JMenuItemの階層構造、Actionクラスによる統一的なアクション管理、PopupMenuの作成と表示制御

**問題説明：**
多層メニューシステムと動的メニュー生成機能を持つアプリケーションを作成してください。

**要求仕様：**
- 階層的なメニュー構造（メニュー、サブメニュー、サブサブメニュー）
- 動的メニュー項目の追加・削除・変更機能
- コンテキストメニュー（右クリックメニュー）の実装
- 最近使用したファイル（MRU: Most Recently Used）メニュー
- メニューアクションの履歴管理

**実行例：**
```
=== 高度なメニューシステム ===
ウィンドウ表示: "Advanced Menu System"
サイズ: 800x600ピクセル

メニューバー構成:
ファイル(F) 編集(E) 表示(V) ツール(T) ヘルプ(H)
├── 新規作成 (Ctrl+N)
├── 開く (Ctrl+O)
├── 保存 (Ctrl+S)
├── 最近使用したファイル
│   ├── 1. document1.txt
│   ├── 2. report.txt
│   └── 履歴をクリア
└── 終了 (Ctrl+Q)

動的メニュー機能:
- メニュー項目の追加/削除
- カスタムメニュー名の設定
- メニューアクションの履歴表示

コンテキストメニュー:
右クリック → メニューカスタマイズ
           → 動的メニュー追加
           → メニューをリセット
```

**評価ポイント：**
- JMenuBar、JMenu、JMenuItemの階層構造
- Actionクラスによる統一的なアクション管理
- PopupMenuの作成と表示制御

**実装ヒント：**
- Action クラスでアクションの統一管理
- MenuListenerでメニューの表示/非表示イベントを処理
- DefaultListModel でMRUリストを管理

---

### 課題4: マウストラッカー

**学習目標：** MouseListener、MouseMotionListenerの詳細実装、Point クラスと座標計算、数学的計算（距離、速度、角度）

**問題説明：**
マウスの詳細な動作を追跡・分析できるアプリケーションを作成してください。

**要求仕様：**
- リアルタイムマウス座標表示
- マウスの移動軌跡の記録と表示
- クリック位置のマーキング（左、右、中央ボタン別）
- マウス移動速度の計算と表示
- マウスイベントログの記録と出力
- 統計情報の表示（総移動距離、クリック数等）

**実行例：**
```
=== マウストラッカー ===
ウィンドウ表示: "Advanced Mouse Tracker"
サイズ: 800x600ピクセル

リアルタイム情報:
┌─────────────────────────────┐
│ 座標: (425, 312)           │
│ 速度: 15.3 px/s           │
│ 総移動距離: 1,234 px       │
│ 左クリック: 12回           │
│ 右クリック: 3回            │
└─────────────────────────────┘

軌跡表示エリア:
┌─────────────────────────────┐
│     ●─●─●    ← マウス軌跡   │
│    /        ○ ← 左クリック  │
│   ●           ● ← 右クリック │
│              /             │
│         ●─●─●              │
└─────────────────────────────┘

イベントログ:
[12:34:56] Mouse moved to (100, 150)
[12:34:57] Left click at (120, 160)
[12:34:58] Mouse dragged to (140, 180)
[12:34:59] Right click at (160, 200)
```

**評価ポイント：**
- MouseListener、MouseMotionListenerの詳細な実装
- Point クラスと座標計算
- 数学的計算（距離、速度、角度）

**実装ヒント：**
- System.currentTimeMillis() で時間測定
- Point.distance() で移動距離計算
- CircularBufferの使用でメモリ効率化

---

## 💡 実装のヒント

### 高度なイベント処理のポイント

1. **イベントの種類**: MouseEvent、KeyEvent、ActionEvent、DocumentEvent
2. **リスナーの実装**: 適切なリスナーインターフェースの選択と実装
3. **イベントの伝播**: イベントの発生源から処理まで
4. **タイマー処理**: javax.swing.Timerによる定期処理
5. **カスタム描画**: paintComponent()のオーバーライド
6. **状態管理**: アプリケーション状態とUI状態の同期

### よくある落とし穴
- EDT以外からのGUI操作（SwingUtilities.invokeLater使用）
- イベントリスナーの登録忘れまたは重複登録
- メモリリークの原因となるリスナーの削除忘れ
- 適切なイベント処理パフォーマンスの考慮不足

### 設計のベストプラクティス
- イベント処理ロジックとビジネスロジックの分離
- リアルタイム性とレスポンシブネスの両立
- ユーザビリティを重視したイベント設計
- 複雑なイベント処理の段階的な実装

---

## 🔗 実装環境

演習課題の詳細な実装テンプレート、テストコード、解答例は以下のディレクトリを参照してください：

```
exercises/chapter18/
├── basic/          # 基礎レベル課題
│   ├── README.md   # 詳細な課題説明
│   ├── InteractiveDrawing.java
│   ├── FormValidator.java
│   ├── MenuSystem.java
│   └── MouseTracker.java
├── advanced/       # 応用レベル課題
├── challenge/      # 発展レベル課題
└── solutions/      # 解答例（実装完了後に参照）
```

---

## ✅ 完了確認チェックリスト

### 基礎レベル
- [ ] インタラクティブ描画でマウスイベント処理ができている
- [ ] フォームバリデーターでリアルタイム検証ができている
- [ ] メニューシステムで動的メニューが作成できている
- [ ] マウストラッカーで詳細な動作分析ができている

### 技術要素
- [ ] 高度なイベント処理の仕組みを理解している
- [ ] リアルタイムな双方向性を実装できている
- [ ] カスタム描画とアニメーションができている
- [ ] ユーザーの多様な操作に適切に応答できている

### 応用レベル
- [ ] 複雑なイベント処理を含むアプリケーションが構築できている
- [ ] パフォーマンスを考慮したイベント処理ができている
- [ ] カスタムコンポーネントとイベント処理が作成できている
- [ ] ユーザビリティの高いインターフェイスが設計できている

**次のステップ**: 基本課題が完了したら、advanced/の発展課題でより複雑なイベント処理とカスタムコンポーネントの実装に挑戦しましょう！

## 本章の学習目標

### 前提知識
**必須前提**：
- 第17章のGUIプログラミング基礎の習得
- ラムダ式と関数型インターフェイスの基本理解
- インターフェイスの実装経験

**設計経験前提**：
- 基本的なGUIコンポーネントの配置経験
- オブザーバパターンの概念的理解

### 学習目標
**知識理解目標**：
- イベント駆動プログラミングモデルの深い理解
- Swingイベント処理機構の詳細
- 主要なイベント種類とリスナインターフェイス
- EDTとスレッド安全性の基本概念

**技能習得目標**：
- 各種イベントリスナの適切な実装
- ラムダ式を活用した簡潔なイベント処理
- カスタムイベントとリスナの作成
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
- `ActionListener`をはじめとする各種リスナをイベントソースに登録することで、ユーザーの操作に応じた処理を実行できます。
- **ラムダ式**を使うことで、イベント処理の記述を大幅に簡潔にできます。

これで、見た目を作り、操作に応答する、インタラクティブなアプリケーションの基礎が固まりました。
