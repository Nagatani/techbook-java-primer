# Part E: 章末演習

本章で学んだGUIプログラミングの基礎概念を活用して、実践的な練習課題に取り組みましょう。

## 演習の目標

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
│ [0___] [.] [=]              │
└─────────────────────────────────┘

操作例:
1. 「123」入力 → ディスプレイに「123」表示
2. 「+」押下 → 演算子待機状態
3. 「456」入力 → ディスプレイに「456」表示
4. 「=」押下 → ディスプレイに「579」表示
```

### 課題2: シンプルメモ帳

基本的なテキスト編集機能を持つメモ帳アプリケーションを作成してください。

**技術的背景：テキスト処理とファイルI/O**

テキストエディタは、多くのGUIアプリケーションの基本的な機能です：

**Swingテキストコンポーネントの階層：**
```java
JTextComponent
├── JTextField (1行テキスト)
├── JPasswordField (パスワード入力)
├── JTextArea (複数行テキスト)
└── JEditorPane (リッチテキスト、HTML対応)
```

**テキストエディタの主要機能：**
- **ファイル操作**：新規作成、開く、保存、名前を付けて保存
- **編集機能**：切り取り、コピー、貼り付け、元に戻す
- **検索置換**：文字列検索、正規表現検索
- **表示設定**：フォント、文字エンコーディング

**メニューシステムの実装：**
```java
JMenuBar menuBar = new JMenuBar();
JMenu fileMenu = new JMenu("ファイル");
fileMenu.add(new JMenuItem("新規"));
fileMenu.add(new JMenuItem("開く"));
fileMenu.addSeparator(); // 区切り線
fileMenu.add(new JMenuItem("保存"));
```

**ファイルダイアログの使用：**
```java
JFileChooser fileChooser = new JFileChooser();
int result = fileChooser.showOpenDialog(parent);
if (result == JFileChooser.APPROVE_OPTION) {
    File file = fileChooser.getSelectedFile();
    // ファイル処理
}
```

**実世界のテキストエディタ例：**
- **VS Code**：拡張機能、統合ターミナル
- **Sublime Text**：高速、マルチカーソル
- **Notepad++**：プラグイン、正規表現対応

**要求仕様：**
- JTextAreaによる複数行テキスト編集
- メニューバー（ファイル、編集）
- ファイルの新規作成、開く、保存機能
- JFileChooserによるファイル選択
- 基本的な編集機能（切り取り、コピー、貼り付け）

**実行例：**
```
=== シンプルメモ帳 ===
ウィンドウ表示: "メモ帳 - untitled"
サイズ: 600x400ピクセル

メニュー構成:
ファイル(F)
├── 新規(N)     Ctrl+N
├── 開く(O)     Ctrl+O
├── 保存(S)     Ctrl+S
├── 名前を付けて保存(A)
└── 終了(X)

編集(E)
├── 元に戻す(U)   Ctrl+Z
├── ──────────
├── 切り取り(T)   Ctrl+X
├── コピー(C)     Ctrl+C
└── 貼り付け(P)   Ctrl+V

中央: スクロール可能なテキストエリア
下部: ステータスバー（行数、文字数表示）
```

### 課題3: レイアウトデモ

複数のレイアウトマネージャーを組み合わせた画面を作成してください。

**技術的背景：レイアウト管理の設計原則**

効果的なGUIレイアウトは、ユーザビリティに直結する重要な要素です：

**レイアウトマネージャーの特性比較：**

| レイアウト | 用途 | 利点 | 制限 |
|------------|------|------|------|
| BorderLayout | メイン画面構成 | 直感的な5領域分割 | 領域数が固定 |
| GridLayout | 均等配置 | すべて同サイズ | サイズ調整不可 |
| FlowLayout | ツールバー | 自然な流れ | 行折り返し |
| GridBagLayout | 複雑な配置 | 最大の柔軟性 | 設定が複雑 |
| BoxLayout | 一方向配置 | シンプル | 直線配置のみ |

**階層的レイアウト設計：**
```java
// トップレベル：BorderLayout
JFrame frame = new JFrame();
frame.setLayout(new BorderLayout());

// 中央：左右分割（JSplitPane）
JSplitPane splitPane = new JSplitPane();
frame.add(splitPane, BorderLayout.CENTER);

// 各パネル：適切なレイアウト選択
JPanel leftPanel = new JPanel(new GridLayout(0, 1));
JPanel rightPanel = new JPanel(new BorderLayout());
```

**レスポンシブデザインの考慮：**
- **最小/最大サイズ**：setMinimumSize(), setMaximumSize()
- **推奨サイズ**：getPreferredSize()のオーバーライド
- **比率維持**：JSplitPaneのdividerLocation設定

**要求仕様：**
- 最低3種類のレイアウトマネージャーを使用
- JPanelによる階層的構成
- 各レイアウトの特徴を示すコンポーネント配置
- ウィンドウリサイズに対する適切な応答

**実行例：**
```
=== レイアウトデモ ===
ウィンドウ表示: "Layout Demo"
サイズ: 800x600ピクセル

上部（BorderLayout.NORTH）：
┌─────────────────────────────────────────┐
│ FlowLayoutツールバー: [新規][開く][保存] │
└─────────────────────────────────────────┘

中央（JSplitPane - 水平分割）：
┌──────────────────┬──────────────────────┐
│左パネル(GridLayout) │右パネル(BorderLayout)  │
│┌────┬────┬────┐│┌──────────────────┐ │
││ B1 │ B2 │ B3 ││││      Center      │ │
│├────┼────┼────┤││└──────────────────┘ │
││ B4 │ B5 │ B6 ││                      │
│└────┴────┴────┘│                      │
└──────────────────┴──────────────────────┘

下部（BorderLayout.SOUTH）：
┌─────────────────────────────────────────┐
│ ステータス: GridLayout | BorderLayout    │
└─────────────────────────────────────────┘
```

### 課題4: カラーピッカー

RGB値を調整して色を選択できるカラーピッカーアプリケーションを作成してください。

**技術的背景：色彩理論とGUIコントロール**

色の操作は、グラフィックアプリケーションの基本機能です：

**RGB色空間の特性：**
```java
// RGB: Red(0-255), Green(0-255), Blue(0-255)
Color color = new Color(red, green, blue);
int rgb = color.getRGB(); // 24bit整数値として取得
```

**HSB色空間の利点：**
```java
// HSB: Hue(色相), Saturation(彩度), Brightness(明度)
Color color = Color.getHSBColor(hue, saturation, brightness);
// より直感的な色調整が可能
```

**Swingコントロールの活用：**
```java
// スライダー：連続値調整
JSlider redSlider = new JSlider(0, 255, 128);
redSlider.setPaintTicks(true);
redSlider.setPaintLabels(true);

// スピナー：数値入力
JSpinner redSpinner = new JSpinner(new SpinnerNumberModel(128, 0, 255, 1));

// 色見本パネル
JPanel colorPreview = new JPanel();
colorPreview.setBackground(currentColor);
```

**リアルタイム更新の実装：**
```java
// ChangeListenerによるリアルタイム反映
redSlider.addChangeListener(e -> updateColor());
greenSlider.addChangeListener(e -> updateColor());
blueSlider.addChangeListener(e -> updateColor());
```

**実世界のカラーピッカー例：**
- **Adobe Photoshop**：HSB、RGB、CMYK対応
- **macOS ColorSync**：色プロファイル管理
- **Windows ペイント**：パレット、カスタム色

**要求仕様：**
- RGB各成分のJSlider（0-255範囲）
- 数値入力用のJSpinner
- 選択色のプレビューパネル
- 16進カラーコード表示（#RRGGBB形式）
- リアルタイム色更新

**実行例：**
```
=== カラーピッカー ===
ウィンドウ表示: "Color Picker"
サイズ: 400x300ピクセル

上部：色プレビュー
┌─────────────────────────────────────┐
│                                     │
│            #FF6600                 │ (選択色表示)
│                                     │
└─────────────────────────────────────┘

中央：RGB調整
Red   (255): [=========|    ] [255]
Green (102): [====|         ] [102]  
Blue  (  0): [|            ] [  0]

下部：色情報
┌─────────────────────────────────────┐
│ RGB(255,102,0) HSB(24°,100%,100%) │
│ 16進: #FF6600  Webセーフ: #FF6633  │
└─────────────────────────────────────┘

操作:
- スライダードラッグで連続調整
- スピナーで数値直接入力
- リアルタイムプレビュー更新
```

---

## 実装のヒント

### GUI基礎のポイント

1. **EDTの正しい使用**
   ```java
   SwingUtilities.invokeLater(() -> {
       createAndShowGUI();
   });
   ```

2. **適切なレイアウト選択**
   - BorderLayout: 全体構成
   - GridLayout: 等間隔配置
   - FlowLayout: 自然な流れ

3. **イベント処理の分離**
   ```java
   // ラムダ式で簡潔に
   button.addActionListener(e -> handleButtonClick());
   
   // 複雑な処理は別メソッドに分離
   private void handleButtonClick() {
       // ビジネスロジック
   }
   ```

### よくある落とし穴

1. **EDT上での重い処理**
   - 長時間処理でUIフリーズ
   - SwingWorkerで解決

2. **レイアウトの競合**
   - setSize()とpack()の混在
   - setLayout(null)の使用

3. **メモリリーク**
   - リスナーの適切な除去
   - 循環参照の回避

### 設計のベストプラクティス

1. **MVCパターンの適用**
   - Model: データとビジネスロジック
   - View: UI表示
   - Controller: イベント処理

2. **コンポーネントの再利用**
   - カスタムコンポーネント作成
   - 設定の外部化

3. **ユーザビリティの考慮**
   - 適切なフィードバック
   - エラー処理とメッセージ表示
   - キーボードショートカット対応

---

## 実装環境

- **Java SE 11以降**推奨
- **IDE**: IntelliJ IDEA、Eclipse、VS Code
- **ビルドツール**: Maven、Gradleが使用可能

---

## 完了確認チェックリスト

### 基礎レベル

- [ ] JFrameでウィンドウを作成できる
- [ ] 基本コンポーネントを配置できる
- [ ] レイアウトマネージャーを使い分けできる
- [ ] ボタンクリックイベントを処理できる
- [ ] テキスト入力を取得・表示できる

### 技術要素

- [ ] EDTを理解し、適切に使用している
- [ ] ラムダ式でイベント処理を記述している
- [ ] ファイルI/Oと連携している（メモ帳課題）
- [ ] リアルタイム更新を実装している（カラーピッカー課題）
- [ ] エラーハンドリングを適切に行っている

### 応用レベル

- [ ] MVCパターンを意識した設計ができる
- [ ] カスタムコンポーネントを作成できる
- [ ] SwingWorkerで非同期処理を実装できる
- [ ] メニューシステムを構築できる
- [ ] キーボードショートカットを実装できる

これらの演習を通じて、実用的なGUIアプリケーション開発の基礎スキルを身につけることができます。