# 第17章 GUIプログラミングの基礎

## 本章の学習目標

### 前提知識

本章を学習するためには、第11章までに習得した総合的なJavaプログラミング能力が必須となります。特に、クラスとオブジェクトの設計、継承とインターフェイス、例外処理、コレクションフレームワーク、ファイル入出力など、これまで学んだ技術要素を組み合わせて実用的なプログラムを作成できる能力が求められます。また、イベント処理の基本概念について理解していることも重要です。ユーザーの操作（ボタンクリック、キー入力など）に応じてプログラムが反応するという、これまでのコンソールアプリケーションとは異なるプログラミングパラダイムを理解する準備ができていることが必要です。さらに、オブジェクト指向設計の実践経験があることで、GUIコンポーネントの階層構造や、イベントリスナーパターンなどの設計思想をより深く理解できます。

ユーザーインターフェイスの前提として、日常的にGUIアプリケーションを使用した経験があることが望ましいです。Windows、macOS、あるいはLinuxのデスクトップ環境でアプリケーションを使用し、メニュー、ボタン、テキストフィールド、スクロールバーなどの標準的なGUIコンポーネントの動作を体験的に理解していることで、開発者としての視点でこれらの実装方法を学ぶことができます。また、ユーザビリティに対する基本的な理解、つまり使いやすいインターフェイスとは何か、ユーザーの期待に応える操作性とは何かという問題意識を持っていることで、単に動くGUIではなく、使いやすいGUIを設計する能力の基礎を築くことができます。

### 学習目標

本章では、Javaを使ったデスクトップGUIアプリケーション開発の基礎を体系的に学習します。知識理解の面では、まずGUIプログラミングの基本概念と、それがもたらす新たな課題について理解します。コンソールアプリケーションが上から下へ順番に実行される手続き的なモデルであるのに対し、GUIアプリケーションはユーザーの操作を待ち、それに反応するイベント駆動型のモデルを採用しています。この根本的な違いを理解することが、効果的なGUIプログラミングの第一歩となります。

Swingフレームワークのアーキテクチャについても深く理解します。SwingはJavaの標準GUIツールキットとして、軽量コンポーネントアーキテクチャを採用し、プラットフォーム独立性を保ちながら豊富な機能を提供します。コンポーネントの階層構造、ペイントシステム、イベントディスパッチスレッド（EDT）など、Swingの内部動作を理解することで、より効率的で安定したGUIアプリケーションを開発できるようになります。また、BorderLayout、FlowLayout、GridLayoutなど、さまざまなレイアウトマネージャーの特徴を理解し、画面サイズの変更に柔軟に対応できる設計方法を学びます。

技能習得の観点では、JFrame、JButton、JTextField、JLabelなどの基本的なGUIコンポーネントの使用方法から始め、これらを組み合わせて実用的なインターフェイスを構築する技術を習得します。イベントリスナの実装では、ActionListener、MouseListener、KeyListenerなどのインターフェイスを使って、ユーザーの操作に応答するプログラムを作成します。ラムダ式を使った簡潔なイベントハンドラの記述方法も学び、現代的なJavaプログラミングスタイルを身につけます。

アプリケーション設計能力の面では、Model-View-Controller（MVC）パターンを意識したGUI設計を学びます。ビジネスロジックとプレゼンテーション層を適切に分離することで、テスト可能で保守性の高いアプリケーションを構築する方法を習得します。また、ユーザビリティを考慮したインターフェイス設計の基本原則、例えば一貫性のある操作性、適切なフィードバック、エラー処理などを実践的に学びます。

最終的な到達レベルとして、基本的な機能を持つGUIアプリケーションを独力で開発できるようになることを目指します。これには、ユーザーの操作に適切に応答するイベント処理の実装、画面レイアウトとコンポーネント配置の適切な設計、そしてファイルI/Oやデータベースアクセスなど、これまで学んだ技術とGUIを連携させたアプリケーションの作成能力が含まれます。これらのスキルにより、エンドユーザーに価値を提供する実用的なデスクトップアプリケーションを開発できるようになります。

---

## 章末演習

本章で学んだGUIプログラミングの基礎概念を活用して、実践的な練習課題に取り組みましょう。

### 演習の目標
Swingを使ったデスクトップGUIアプリケーションの基礎を習得します。


---

## 基礎レベル課題（必須）

### 課題1: 電卓アプリケーション

四則演算ができる電卓GUIアプリケーションを作成してください。

**技術的背景：GUIアプリケーション開発の基礎とSwing**

GUIプログラミングは、コンソールプログラミングとは根本的に異なるパラダイムです：

**イベント駆動プログラミングの特徴：**
- **イベントループ**：ユーザー入力を待機し続ける
- **非同期処理**：いつ何が起きるか予測不能
- **コールバック**：イベント発生時の処理登録
- **状態管理**：画面の現在状態を常に把握

**Swingの特徴と選択理由：**
```java
// JavaFX vs Swing
// Swing：成熟、安定、豊富なサードパーティライブラリ
// JavaFX：モダン、CSS対応、JavaFX 11からOpenJFX化

// 電卓の基本構造
JFrame frame = new JFrame("Calculator");
JTextField display = new JTextField();
JPanel buttonPanel = new JPanel(new GridLayout(4, 4));
```

**MVC（Model-View-Controller）パターンの適用：**
- **Model**：計算ロジック（四則演算、状態管理）
- **View**：GUI部品（ボタン、ディスプレイ）
- **Controller**：イベントハンドラ（ボタンクリック処理）

**実際の電卓アプリケーションの設計考慮点：**
- **数値精度**：BigDecimalによる正確な計算
- **エラー処理**：ゼロ除算、オーバーフロー
- **ユーザビリティ**：キーボード入力対応
- **状態遷移**：数値入力→演算子→数値入力→計算

**レイアウト管理の重要性：**
```java
// GridLayoutの利点：均等配置、自動リサイズ
new GridLayout(rows, cols, hgap, vgap)
// BorderLayoutの利点：領域分割、柔軟性
new BorderLayout()
```

**実世界の電卓アプリケーション例：**
- **Windows計算機**：標準/関数電卓モード切替
- **macOS計算機**：ウィジェット対応
- **Google計算機**：単位変換、グラフ表示

この演習では、実用的なGUIアプリケーションの基礎を学びます。

**要求仕様：**
- 数字ボタン（0-9）と演算子ボタン（+、-、×、÷）
- 結果表示用のJTextField
- GridLayoutによるボタン配置
- 計算ロジックの実装
- エラーハンドリング（ゼロ除算等）

**実行例：**
```
=== 電卓アプリケーション ===
ウィンドウ表示: "Calculator"
サイズ: 300x400ピクセル

電卓レイアウト:
┌─────────────────────────────────┐
│ [123.45________________]    │ (結果表示)
├─────────────────────────────────┤
│ [C] [±] [%] [÷]             │
├─────────────────────────────────┤
│ [7] [8] [9] [×]             │
├─────────────────────────────────┤
│ [4] [5] [6] [-]             │
├─────────────────────────────────┤
│ [1] [2] [3] [+]             │
├─────────────────────────────────┤
│ [0] [.] [=]                 │
└─────────────────────────────────┘

計算テスト:
操作: 1 + 2 =
表示: 1 → 1 + → 1 + 2 → 1 + 2 = 3

操作: 10 ÷ 3 =
表示: 10 ÷ 3 = 3.333333

エラーハンドリング:
操作: 10 ÷ 0 =
表示: "エラー: ゼロ除算"
[C]ボタンでリセット
```

- ユーザビリティの考慮

**実装ヒント：**
- GridLayout(4, 4) で4×4のボタン配置
- Double型で計算精度を保つ
- try-catchでNumberFormatException処理

---

### 課題2: シンプルメモ帳

**学習目標：** メニューシステムの実装、ファイルI/Oとの連携、キーボードショートカットの実装

**問題説明：**
基本的なテキストエディタGUIアプリケーションを作成してください。

**技術的背景：メニューシステムとドキュメント中心アプリケーション**

テキストエディタは、GUIアプリケーションの基本パターンを学ぶ最適な題材です：

**メニューシステムの階層構造：**
```java
JMenuBar → JMenu → JMenuItem/JCheckBoxMenuItem/JRadioButtonMenuItem
                 → JSeparator（区切り線）
                 → JMenu（サブメニュー）
```

**アクセラレータとニーモニックの違い：**
- **アクセラレータ**：Ctrl+S（どこからでも実行可能）
- **ニーモニック**：Alt+F→S（メニュー経由）
```java
// アクセラレータ設定
menuItem.setAccelerator(KeyStroke.getKeyStroke(
    KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
// ニーモニック設定
menu.setMnemonic(KeyEvent.VK_F);
```

**ドキュメントの状態管理：**
- **変更フラグ**：DocumentListenerで検知
- **ファイルパス**：現在編集中のファイル
- **タイトルバー**："ファイル名 - アプリ名"形式
- **未保存警告**：閉じる前の確認ダイアログ

**実際のテキストエディタの機能階層：**
1. **基本機能**：開く、保存、編集
2. **標準機能**：検索・置換、印刷
3. **高度な機能**：シンタックスハイライト、自動補完
4. **統合機能**：プラグイン、マクロ

**プロフェッショナルエディタの例：**
- **Notepad++**：軽量、多機能、プラグイン対応
- **VSCode**：Electron製、拡張機能豊富
- **IntelliJ IDEA**：Swing製の大規模IDE

**ファイルI/O統合の注意点：**
- **文字エンコーディング**：UTF-8標準化
- **改行コード**：OS間の違い（\r\n vs \n）
- **大容量ファイル**：メモリ効率的な読み込み
- **自動バックアップ**：定期保存機能

この演習では、実用的なドキュメント編集アプリケーションの基礎を習得します。

**要求仕様：**
- JTextAreaによるテキスト編集エリア
- JScrollPaneによるスクロール機能
- メニューバー（ファイル、編集、ヘルプ）
- ファイルの開く・保存機能
- 変更検知とタイトルバー表示

**実行例：**
```
=== シンプルメモ帳 ===
ウィンドウ表示: "SimpleNotepad"
サイズ: 800x600ピクセル

メニュー構成:
ファイル(F)
├── 新規作成 (Ctrl+N)
├── 開く (Ctrl+O)
├── 保存 (Ctrl+S)
├── 名前を付けて保存
├── ──────────
└── 終了 (Alt+F4)

編集(E)
├── 切り取り (Ctrl+X)
├── コピー (Ctrl+C)
├── 貼り付け (Ctrl+V)
├── ──────────
└── すべて選択 (Ctrl+A)

ヘルプ(H)
└── バージョン情報

エディタ画面:
┌─────────────────────────────────┐
│ [メニューバー]              │
├─────────────────────────────────┤
│ ┌─スクロール可能テキストエリア─┐ │
│ │ Hello, World!           │ │
│ │ Java Swing Text Editor  │ │
│ │ 複数行のテキスト編集が    │ │
│ │ 可能です。              │ │
│ │ ■                      │ │ (カーソル)
│ └───────────────────────────────┘ │
└─────────────────────────────────┘

ファイル操作テスト:
1. [新規作成]: 空のドキュメント
2. テキスト入力: "Hello, World!"
3. [保存]: sample.txt として保存
4. [開く]: sample.txt を読み込み
```


---

### 課題3: レイアウトデモ

異なるレイアウトマネージャーの動作を比較できるデモアプリケーションを作成してください。

**技術的背景：レイアウトマネージャーの設計思想と適用場面**

Javaのレイアウトマネージャーは、画面解像度の多様性に対応する革新的な仕組みです：

**レイアウトマネージャーの必要性：**
- **解像度独立**：640×480から4K、8Kまで対応
- **動的リサイズ**：ウィンドウサイズ変更への追従
- **国際化対応**：文字列長の違いへの適応
- **プラットフォーム独立**：OS固有のUIガイドライン

**各レイアウトマネージャーの設計思想：**
```java
// BorderLayout：領域分割型（ドッキング）
// 用途：メインウィンドウ、ツールバー配置
add(toolbar, BorderLayout.NORTH);
add(content, BorderLayout.CENTER);
add(status, BorderLayout.SOUTH);

// GridBagLayout：制約ベース配置
// 用途：複雑なフォーム、ダイアログ
GridBagConstraints c = new GridBagConstraints();
c.gridx = 0; c.gridy = 0;
c.gridwidth = 2; c.fill = GridBagConstraints.HORIZONTAL;
```

**レイアウト選択の判断基準：**
| レイアウト | 使用場面 | 特徴 |
|------------|----------|------|
| BorderLayout | メインウィンドウ | 5領域分割 |
| FlowLayout | ボタン配置 | 流し込み |
| GridLayout | 電卓、カレンダー | 均等グリッド |
| BoxLayout | ツールバー | 一方向配置 |
| CardLayout | ウィザード | 画面切替 |
| GridBagLayout | 複雑なフォーム | 最も柔軟 |

**実際のアプリケーションでの組み合わせ例：**
```java
// IntelliJ IDEAのような複雑なレイアウト
BorderLayout（メイン）
├── North: BoxLayout（ツールバー）
├── Center: JSplitPane
│   ├── Left: JTree（プロジェクト構造）
│   └── Right: JTabbedPane（エディタ）
└── South: GridBagLayout（ステータスバー）
```

**レスポンシブデザインの実現：**
- **最小サイズ**：setMinimumSize()で崩れ防止
- **優先サイズ**：setPreferredSize()で理想配置
- **最大サイズ**：setMaximumSize()で拡大制限
- **重み付け**：GridBagConstraints.weightx/y

**モダンなレイアウト手法：**
- **MigLayout**：制約ベースの外部ライブラリ
- **JavaFX**：FXML、CSS対応
- **Web技術**：Electronによるレスポンシブ設計

この演習では、各レイアウトマネージャーの特性を実践的に理解します。

**要求仕様：**
- 複数のレイアウトマネージャーを試せるタブ画面
- BorderLayout、FlowLayout、GridLayout、BoxLayout、CardLayout、GridBagLayoutのデモ
- レイアウトの動的切り替え機能
- ウィンドウサイズ変更時の動作確認

**実行例：**
```
=== レイアウトマネージャーデモ ===
ウィンドウ表示: "Layout Manager Demo"
サイズ: 700x500ピクセル

タブ構成:
- BorderLayout: 5方向配置デモ
- FlowLayout: 左・中央・右配置デモ
- GridLayout: 行列数可変デモ
- BoxLayout: 水平・垂直配置デモ
- CardLayout: カード切り替えデモ
- GridBagLayout: 複雑な配置デモ
- Dynamic Layout: 動的レイアウト変更

各タブでの学習内容:
1. BorderLayout: North、South、East、West、Center配置
2. FlowLayout: LEFT、CENTER、RIGHT整列
3. GridLayout: 行列グリッド配置
4. BoxLayout: X_AXIS、Y_AXIS配置
5. CardLayout: カード切り替え操作
6. GridBagLayout: 制約を使った複雑配置
```


**実装ヒント：**
- JTabbedPaneでタブ切り替え
- GridBagConstraintsで制約設定
- revalidate()、repaint() で再描画

---

### 課題4: カラーピッカー

**学習目標：** JSliderの効果的な使用、ColorクラスのRGB/HSB変換理解、リアルタイム更新の実装

**問題説明：**
色を選択・調整できるカラーピッカーアプリケーションを作成してください。

**技術的背景：色空間の理解とリアルタイムUI更新**

色の表現と操作は、グラフィックスプログラミングの基礎です：

**色空間（Color Space）の種類と特徴：**
```java
// RGB：加法混色（ディスプレイ向け）
Color.rgb(255, 128, 0)  // オレンジ色

// HSB/HSV：直感的な色指定
// H(Hue)：色相（0-360度）
// S(Saturation)：彩度（0-100%）
// B(Brightness)：明度（0-100%）
Color.hsb(30, 100, 100)  // 鮮やかなオレンジ

// CMYK：減法混色（印刷向け）
// JavaではColorクラスでは直接サポートせず
```

**JSliderの高度な使い方：**
```java
// カスタム目盛り表示
slider.setMajorTickSpacing(50);
slider.setMinorTickSpacing(10);
slider.setPaintTicks(true);
slider.setPaintLabels(true);

// リアルタイム更新
slider.addChangeListener(e -> {
    if (!slider.getValueIsAdjusting()) {
        updateColor();  // ドラッグ終了時のみ
    }
});
```

**パフォーマンス最適化の考慮：**
- **更新頻度**：ChangeListenerの呼び出し回数
- **再描画**：repaint()の最適化
- **スレッド**：EDTでの軽量処理
- **バッファリング**：ダブルバッファリング

**実際のカラーピッカーの実装例：**
- **Photoshop**：HSB円盤、スポイトツール
- **macOS**：システムカラーピッカー
- **Web**：HTML5 input type="color"
- **VS Code**：インラインカラーピッカー

**高度な機能の実装アイデア：**
- **カラーパレット**：よく使う色の保存
- **カラーハーモニー**：補色、類似色の提案
- **グラデーション**：2色間の補間
- **透明度**：アルファチャンネル対応

**アクセシビリティの考慮：**
- **色覚異常対応**：色名表示、パターン併用
- **キーボード操作**：矢印キーでの微調整
- **数値入力**：直接RGB値入力
- **コントラスト確認**：WCAG基準チェック

この演習では、インタラクティブなUIコンポーネントの実装を学びます。

**要求仕様：**
- RGB値を個別に調整できるスライダー
- HSB値を個別に調整できるスライダー
- 色のプレビューエリア
- 16進数カラーコード表示
- 色履歴の保存・呼び出し機能
- プリセットカラー選択

**実行例：**
```
=== カラーピッカー ===
ウィンドウ表示: "Color Picker"
サイズ: 800x600ピクセル

コンポーネント構成:
┌─────────────────────────────────┐
│ RGB スライダー              │
│ Red: [====o========] 128    │
│ Green: [====o======] 128    │
│ Blue: [====o=======] 128    │
├─────────────────────────────────┤
│ HSB スライダー              │
│ Hue: [=====o=======] 180    │
│ Saturation: [==o===] 50     │
│ Brightness: [==o===] 50     │
├─────────────────────────────────┤
│ ┌─プレビュー─┐ 16進数: #808080│
│ │           │ 色名: Gray     │
│ │   Gray    │ [Color Chooser]│
│ │           │                │
│ └───────────┘                │
├─────────────────────────────────┤
│ プリセットカラー            │
│ [■][■][■][■][■][■][■][■]│
│ [■][■][■][■][■][■][■][■]│
├─────────────────────────────────┤
│ 補色・類似色                │
│ 補色: [■] 類似色: [■][■][■]│
├─────────────────────────────────┤
│ 色履歴リスト                │
│ RGB(128, 128, 128)          │
│ RGB(255, 0, 0)              │
│ RGB(0, 255, 0)              │
└─────────────────────────────────┘

操作テスト:
1. RGBスライダー調整 → 色変更
2. HSBスライダー調整 → 色変更
3. 16進数入力 → #FF0000 → 赤色
4. プリセットクリック → 既定色選択
5. Color Chooser → 高度な色選択
```

**評価ポイント：**
- JSliderの効果的な使用
- ColorクラスのRGB/HSB変換理解
- リアルタイム更新の実装

**実装ヒント：**
- Color.RGBtoHSB（）とColor.HSBtoRGB() を使用
- JSliderの値変更リスナでリアルタイム更新
- ArrayListで色履歴を管理

---

## 実装のヒント

### GUI基礎のポイント

1. **コンポーネント階層**: Container → Componentの関係
2. **レイアウトマネージャー**: BorderLayout、GridLayout、FlowLayout等
3. **イベント処理**: ActionListener、ChangeListener等
4. **スレッド**: Event Dispatch Thread（EDT）での実行
5. **Look & Feel**: プラットフォーム固有の外観
6. **ダイアログ**: JOptionPane、JFileChooser等

### よくある落とし穴
- EDT以外からのGUI操作（SwingUtilities.invokeLater使用）
- レイアウトマネージャーの不適切な選択
- イベントリスナのメモリリーク
- 適切なサイズ設定とリサイズ対応

### 設計のベストプラクティス
- MVCパターンでGUIとロジックを分離
- 再使用可能なコンポーネント設計
- ユーザーエクスペリエンスを意識したデザイン
- アクセシビリティとキーボード操作の考慮

---

## 実装環境

演習課題の詳細な実装テンプレート、テストコード、解答例は以下のディレクトリを参照してください：

```
exercises/chapter17/
├── basic/          # 基礎レベル課題
│   ├── README.md   # 詳細な課題説明
│   ├── Calculator.java
│   ├── SimpleNotepad.java
│   ├── LayoutDemo.java
│   └── ColorPicker.java
├── advanced/       # 応用レベル課題
├── challenge/      # 発展レベル課題
└── solutions/      # 解答例（実装完了後に参照）
```

---

## 完了確認チェックリスト

### 基礎レベル
- [ ] 電卓で基本的な計算機能が実装できている
- [ ] メモ帳でファイル操作とメニューシステムができている
- [ ] レイアウトデモで各レイアウトマネージャーの特徴を理解している
- [ ] カラーピッカーでスライダーと色変換ができている

### 技術要素
- [ ] Swingの基本コンポーネントを理解している
- [ ] イベント処理のしくみを把握している
- [ ] レイアウトマネージャーを適切に使い分けできている
- [ ] ユーザビリティを考慮した設計ができている

### 応用レベル
- [ ] 複雑なGUIアプリケーションが構築できている
- [ ] カスタムコンポーネントの作成ができている
- [ ] パフォーマンスを考慮したGUI設計ができている
- [ ] クロスプラットフォーム対応のGUIが作成できている

**次のステップ**: 基本課題が完了したら、advanced/の発展課題でより複雑なGUIアプリケーション開発に挑戦しましょう！

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

### 基本コンポーネントの詳細な使用例

#### JLabelとアイコンの活用

```java
import javax.swing.*;
import java.awt.*;

public class JLabelExample {
    public static void main(String[] args) {
        JFrame frame = new JFrame("JLabel Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(4, 1, 10, 10));
        
        // 基本的なテキストラベル
        JLabel textLabel = new JLabel("これは基本的なラベルです");
        frame.add(textLabel);
        
        // HTML形式のラベル（複数行、色、フォント指定）
        JLabel htmlLabel = new JLabel(
            "<html><body style='width: 200px'>" +
            "<h2 style='color: blue'>HTMLラベル</h2>" +
            "<p>HTMLタグが使えるので、<br>" +
            "<b>太字</b>や<i>斜体</i>、<u>下線</u>なども表現できます。</p>" +
            "</body></html>"
        );
        frame.add(htmlLabel);
        
        // アイコン付きラベル
        ImageIcon icon = new ImageIcon("icon.png"); // 実際の画像パスを指定
        JLabel iconLabel = new JLabel("アイコン付きラベル", icon, JLabel.LEFT);
        iconLabel.setHorizontalTextPosition(JLabel.RIGHT); // テキストの位置
        iconLabel.setVerticalTextPosition(JLabel.CENTER);   // テキストの垂直位置
        frame.add(iconLabel);
        
        // カスタマイズされたラベル
        JLabel customLabel = new JLabel("カスタムラベル", JLabel.CENTER);
        customLabel.setFont(new Font("MS Gothic", Font.BOLD, 18));
        customLabel.setForeground(Color.WHITE);
        customLabel.setBackground(Color.DARK_GRAY);
        customLabel.setOpaque(true); // 背景色を有効にする
        customLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        frame.add(customLabel);
        
        frame.setSize(400, 400);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
```

#### JButtonの高度な使用

```java
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class JButtonExample {
    public static void main(String[] args) {
        JFrame frame = new JFrame("JButton Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        
        // 基本的なボタン
        JButton basicButton = new JButton("基本ボタン");
        basicButton.addActionListener(e -> 
            JOptionPane.showMessageDialog(frame, "ボタンがクリックされました！")
        );
        frame.add(basicButton);
        
        // アイコン付きボタン
        JButton iconButton = new JButton("保存", new ImageIcon("save.png"));
        iconButton.setToolTipText("ファイルを保存します");
        frame.add(iconButton);
        
        // カスタマイズボタン
        JButton customButton = new JButton("カスタムボタン");
        customButton.setFont(new Font("Arial", Font.BOLD, 16));
        customButton.setForeground(Color.WHITE);
        customButton.setBackground(new Color(0, 123, 255));
        customButton.setBorderPainted(false);
        customButton.setFocusPainted(false);
        customButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // ホバー効果
        customButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                customButton.setBackground(new Color(0, 86, 179));
            }
            public void mouseExited(MouseEvent e) {
                customButton.setBackground(new Color(0, 123, 255));
            }
        });
        frame.add(customButton);
        
        // 無効化されたボタン
        JButton disabledButton = new JButton("無効なボタン");
        disabledButton.setEnabled(false);
        frame.add(disabledButton);
        
        // ニーモニック（Alt+キー）設定
        JButton mnemonicButton = new JButton("実行(R)");
        mnemonicButton.setMnemonic(KeyEvent.VK_R);
        frame.add(mnemonicButton);
        
        frame.setSize(600, 200);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
```

#### テキスト入力コンポーネント

```java
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;

public class TextComponentExample {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Text Component Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        
        // JTextField - 1行入力
        JPanel textFieldPanel = new JPanel(new FlowLayout());
        textFieldPanel.add(new JLabel("名前:"));
        JTextField nameField = new JTextField(20);
        nameField.setToolTipText("お名前を入力してください");
        
        // リアルタイムで入力を監視
        nameField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { checkInput(); }
            public void removeUpdate(DocumentEvent e) { checkInput(); }
            public void changedUpdate(DocumentEvent e) { checkInput(); }
            
            void checkInput() {
                System.out.println("入力: " + nameField.getText());
            }
        });
        textFieldPanel.add(nameField);
        frame.add(textFieldPanel);
        
        // JPasswordField - パスワード入力
        JPanel passwordPanel = new JPanel(new FlowLayout());
        passwordPanel.add(new JLabel("パスワード:"));
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setEchoChar('●'); // 表示文字を変更
        passwordPanel.add(passwordField);
        frame.add(passwordPanel);
        
        // JFormattedTextField - フォーマット付き入力
        JPanel formattedPanel = new JPanel(new FlowLayout());
        formattedPanel.add(new JLabel("郵便番号:"));
        try {
            JFormattedTextField zipField = new JFormattedTextField(
                new javax.swing.text.MaskFormatter("###-####")
            );
            zipField.setColumns(10);
            formattedPanel.add(zipField);
        } catch (Exception e) {
            e.printStackTrace();
        }
        frame.add(formattedPanel);
        
        // JTextArea - 複数行入力
        JPanel textAreaPanel = new JPanel(new BorderLayout());
        textAreaPanel.setBorder(BorderFactory.createTitledBorder("コメント"));
        JTextArea textArea = new JTextArea(5, 30);
        textArea.setLineWrap(true); // 自動改行
        textArea.setWrapStyleWord(true); // 単語単位で改行
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        textAreaPanel.add(scrollPane);
        frame.add(textAreaPanel);
        
        // 送信ボタン
        JButton submitButton = new JButton("送信");
        submitButton.addActionListener(e -> {
            String name = nameField.getText();
            String password = new String(passwordField.getPassword());
            String comment = textArea.getText();
            
            JOptionPane.showMessageDialog(frame,
                "名前: " + name + "\n" +
                "パスワード: " + (password.isEmpty() ? "未入力" : "****") + "\n" +
                "コメント: " + comment
            );
        });
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(submitButton);
        frame.add(buttonPanel);
        
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
```

#### 選択コンポーネント

```java
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SelectionComponentExample {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Selection Component Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        
        // JCheckBox - 複数選択
        JPanel checkBoxPanel = new JPanel();
        checkBoxPanel.setBorder(BorderFactory.createTitledBorder("趣味（複数選択可）"));
        
        JCheckBox readingCB = new JCheckBox("読書", true); // 初期選択
        JCheckBox movieCB = new JCheckBox("映画鑑賞");
        JCheckBox sportsCB = new JCheckBox("スポーツ");
        JCheckBox gamingCB = new JCheckBox("ゲーム");
        
        // チェック状態の変更を監視
        ItemListener checkListener = e -> {
            JCheckBox cb = (JCheckBox)e.getSource();
            System.out.println(cb.getText() + ": " + cb.isSelected());
        };
        
        readingCB.addItemListener(checkListener);
        movieCB.addItemListener(checkListener);
        sportsCB.addItemListener(checkListener);
        gamingCB.addItemListener(checkListener);
        
        checkBoxPanel.add(readingCB);
        checkBoxPanel.add(movieCB);
        checkBoxPanel.add(sportsCB);
        checkBoxPanel.add(gamingCB);
        frame.add(checkBoxPanel);
        
        // JRadioButton - 単一選択
        JPanel radioPanel = new JPanel();
        radioPanel.setBorder(BorderFactory.createTitledBorder("性別"));
        
        JRadioButton maleRB = new JRadioButton("男性");
        JRadioButton femaleRB = new JRadioButton("女性");
        JRadioButton otherRB = new JRadioButton("その他", true);
        
        // ButtonGroupで排他制御
        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(maleRB);
        genderGroup.add(femaleRB);
        genderGroup.add(otherRB);
        
        radioPanel.add(maleRB);
        radioPanel.add(femaleRB);
        radioPanel.add(otherRB);
        frame.add(radioPanel);
        
        // JComboBox - ドロップダウンリスト
        JPanel comboPanel = new JPanel();
        comboPanel.setBorder(BorderFactory.createTitledBorder("都道府県"));
        
        String[] prefectures = {"東京都", "大阪府", "愛知県", "福岡県", "北海道"};
        JComboBox<String> prefectureCombo = new JComboBox<>(prefectures);
        prefectureCombo.setSelectedIndex(0); // 初期選択
        
        // 編集可能なコンボボックス
        JComboBox<String> editableCombo = new JComboBox<>(new String[]{"選択1", "選択2", "その他"});
        editableCombo.setEditable(true);
        
        comboPanel.add(new JLabel("居住地:"));
        comboPanel.add(prefectureCombo);
        comboPanel.add(new JLabel("  カスタム:"));
        comboPanel.add(editableCombo);
        frame.add(comboPanel);
        
        // JList - リスト選択
        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBorder(BorderFactory.createTitledBorder("言語（複数選択可）"));
        
        String[] languages = {"Java", "Python", "JavaScript", "C++", "Ruby", "Go", "Rust"};
        JList<String> languageList = new JList<>(languages);
        languageList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        languageList.setVisibleRowCount(4);
        
        JScrollPane listScrollPane = new JScrollPane(languageList);
        listPanel.add(listScrollPane);
        frame.add(listPanel);
        
        // 結果表示ボタン
        JButton showButton = new JButton("選択内容を表示");
        showButton.addActionListener(e -> {
            StringBuilder result = new StringBuilder("選択内容:\n");
            
            // チェックボックス
            result.append("趣味: ");
            if (readingCB.isSelected()) result.append("読書 ");
            if (movieCB.isSelected()) result.append("映画 ");
            if (sportsCB.isSelected()) result.append("スポーツ ");
            if (gamingCB.isSelected()) result.append("ゲーム ");
            result.append("\n");
            
            // ラジオボタン
            result.append("性別: ");
            if (maleRB.isSelected()) result.append("男性");
            else if (femaleRB.isSelected()) result.append("女性");
            else result.append("その他");
            result.append("\n");
            
            // コンボボックス
            result.append("居住地: ").append(prefectureCombo.getSelectedItem()).append("\n");
            
            // リスト
            result.append("言語: ").append(languageList.getSelectedValuesList());
            
            JOptionPane.showMessageDialog(frame, result.toString());
        });
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(showButton);
        frame.add(buttonPanel);
        
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
```

### コンポーネントの配置とレイアウト管理

コンポーネントをどこに配置するかを決めるのが**レイアウトマネージャ**の役割です。ウィンドウサイズが変わってもレイアウトが崩れないように、ルールに従って自動で再配置してくれます。

#### 基本的なレイアウトマネージャー

##### FlowLayout：流れるような配置

```java
import javax.swing.*;
import java.awt.*;

public class FlowLayoutExample {
    public static void main(String[] args) {
        JFrame frame = new JFrame("FlowLayout Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // FlowLayoutの設定（整列方法、水平間隔、垂直間隔）
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 15));
        
        // 複数のボタンを追加
        for (int i = 1; i <= 10; i++) {
            panel.add(new JButton("Button " + i));
        }
        
        frame.add(panel);
        frame.setSize(400, 200);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
```

##### BorderLayout：5方向の配置

```java
import javax.swing.*;
import java.awt.*;

public class BorderLayoutExample {
    public static void main(String[] args) {
        JFrame frame = new JFrame("BorderLayout Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // BorderLayoutはJFrameのデフォルト
        frame.add(new JButton("North (上部)"), BorderLayout.NORTH);
        frame.add(new JButton("South (下部)"), BorderLayout.SOUTH);
        frame.add(new JButton("East (右側)"), BorderLayout.EAST);
        frame.add(new JButton("West (左側)"), BorderLayout.WEST);
        frame.add(new JButton("Center (中央)"), BorderLayout.CENTER);
        
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
```

##### GridLayout：格子状の配置

```java
import javax.swing.*;
import java.awt.*;

public class GridLayoutExample {
    public static void main(String[] args) {
        JFrame frame = new JFrame("GridLayout Example - 計算機");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // 4行4列のグリッド（行数、列数、水平間隔、垂直間隔）
        JPanel panel = new JPanel(new GridLayout(4, 4, 5, 5));
        
        // 計算機のボタン配置
        String[] buttons = {
            "7", "8", "9", "÷",
            "4", "5", "6", "×",
            "1", "2", "3", "-",
            "0", ".", "=", "+"
        };
        
        for (String label : buttons) {
            JButton button = new JButton(label);
            button.setFont(new Font("Arial", Font.BOLD, 20));
            panel.add(button);
        }
        
        frame.add(panel);
        frame.setSize(300, 300);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
```

##### BoxLayout：一方向への配置

```java
import javax.swing.*;
import java.awt.*;

public class BoxLayoutExample {
    public static void main(String[] args) {
        JFrame frame = new JFrame("BoxLayout Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // 垂直配置のBoxLayout
        JPanel verticalPanel = new JPanel();
        verticalPanel.setLayout(new BoxLayout(verticalPanel, BoxLayout.Y_AXIS));
        verticalPanel.setBorder(BorderFactory.createTitledBorder("垂直配置"));
        
        verticalPanel.add(new JButton("ボタン 1"));
        verticalPanel.add(Box.createRigidArea(new Dimension(0, 10))); // 固定サイズの空白
        verticalPanel.add(new JButton("ボタン 2"));
        verticalPanel.add(Box.createVerticalGlue()); // 伸縮可能な空白
        verticalPanel.add(new JButton("ボタン 3"));
        
        // 水平配置のBoxLayout
        JPanel horizontalPanel = new JPanel();
        horizontalPanel.setLayout(new BoxLayout(horizontalPanel, BoxLayout.X_AXIS));
        horizontalPanel.setBorder(BorderFactory.createTitledBorder("水平配置"));
        
        horizontalPanel.add(new JButton("左"));
        horizontalPanel.add(Box.createHorizontalGlue());
        horizontalPanel.add(new JButton("中央"));
        horizontalPanel.add(Box.createHorizontalGlue());
        horizontalPanel.add(new JButton("右"));
        
        frame.setLayout(new GridLayout(1, 2));
        frame.add(verticalPanel);
        frame.add(horizontalPanel);
        
        frame.setSize(500, 300);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
```

##### CardLayout：カード型の切り替え

```java
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class CardLayoutExample {
    public static void main(String[] args) {
        JFrame frame = new JFrame("CardLayout Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // CardLayoutの作成
        CardLayout cardLayout = new CardLayout();
        JPanel cardPanel = new JPanel(cardLayout);
        
        // 各カード（パネル）の作成
        JPanel card1 = new JPanel();
        card1.setBackground(Color.RED);
        card1.add(new JLabel("カード 1 - 赤"));
        
        JPanel card2 = new JPanel();
        card2.setBackground(Color.GREEN);
        card2.add(new JLabel("カード 2 - 緑"));
        
        JPanel card3 = new JPanel();
        card3.setBackground(Color.BLUE);
        card3.add(new JLabel("カード 3 - 青"));
        
        // カードの追加（名前付き）
        cardPanel.add(card1, "red");
        cardPanel.add(card2, "green");
        cardPanel.add(card3, "blue");
        
        // 制御ボタン
        JPanel controlPanel = new JPanel();
        JButton prevButton = new JButton("前へ");
        JButton nextButton = new JButton("次へ");
        
        prevButton.addActionListener(e -> cardLayout.previous(cardPanel));
        nextButton.addActionListener(e -> cardLayout.next(cardPanel));
        
        controlPanel.add(prevButton);
        controlPanel.add(nextButton);
        
        frame.add(cardPanel, BorderLayout.CENTER);
        frame.add(controlPanel, BorderLayout.SOUTH);
        
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
```

##### GridBagLayout：最も柔軟な配置

```java
import javax.swing.*;
import java.awt.*;

public class GridBagLayoutExample {
    public static void main(String[] args) {
        JFrame frame = new JFrame("GridBagLayout Example - ログインフォーム");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // コンポーネント間の余白
        
        // ラベル：ユーザー名
        gbc.gridx = 0; // 列位置
        gbc.gridy = 0; // 行位置
        gbc.anchor = GridBagConstraints.EAST; // 右寄せ
        panel.add(new JLabel("ユーザー名:"), gbc);
        
        // テキストフィールド：ユーザー名
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL; // 水平方向に伸縮
        gbc.weightx = 1.0; // 余白の配分比率
        panel.add(new JTextField(15), gbc);
        
        // ラベル：パスワード
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(new JLabel("パスワード:"), gbc);
        
        // パスワードフィールド
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(new JPasswordField(15), gbc);
        
        // チェックボックス：ログイン状態を保持
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2; // 2列分を使用
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.WEST; // 左寄せ
        panel.add(new JCheckBox("ログイン状態を保持"), gbc);
        
        // ボタンパネル
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(new JButton("ログイン"));
        buttonPanel.add(new JButton("キャンセル"));
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER; // 中央配置
        panel.add(buttonPanel, gbc);
        
        frame.add(panel);
        frame.pack(); // コンポーネントに合わせてサイズ調整
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
```

- **`FlowLayout`**: 部品を左から右へ、行が埋まると次の行へと流れるように配置します。(`JPanel`のデフォルト）
- **`BorderLayout`**: 画面を「上下左右中央（North/South/East/West/Center)」の5領域に分割します。（`JFrame`のデフォルト）
- **`GridLayout`**: 画面を格子状（マス目）に分割し、全部品を同じサイズで配置します。
- **`BoxLayout`**: 一方向（水平または垂直）に部品を配置し、柔軟な余白設定が可能です。
- **`CardLayout`**: 複数のパネルを重ねて、一度に1つだけ表示する切り替え型レイアウトです。
- **`GridBagLayout`**: 最も柔軟で複雑なレイアウトが可能ですが、設定も複雑です。

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
