# 第19章 GUIのイベント処理

## 本章の学習目標

### 前提知識

本章を学習するための必須前提として、第18章で学んだGUIプログラミングの基礎をしっかりと習得していることが必要です。JFrame、JButton、JTextFieldなどの基本的なコンポーネントの使い方、レイアウトマネージャーによる画面構成、簡単なイベントリスナの実装などの基礎技術を理解し、実際に使用した経験があることが重要です。また、Java 8で導入されたラムダ式と関数型インターフェイスの基本的な理解も必要です。これらの機能により、イベント処理のコードをより簡潔で読みやすく記述できるようになるため、本章では積極的に活用します。インターフェイスの実装経験も重要で、特にリスナインターフェイスを実装する際の抽象メソッドのオーバーライドやアダプタクラスの使用方法を理解していることが求められます。

設計経験の前提として、基本的なGUIコンポーネントの配置経験があり、画面設計の基本的な考え方を理解していることが望ましいです。また、デザインパターンの1つであるオブザーバパターンの概念的理解があると、イベント処理のしくみをより深く理解できます。イベントソース（観察対象）がイベントリスナ（観察者）に変更を通知するという基本的なしくみが、Swingのイベント処理モデルの根幹をなしているからです。

### 学習目標

本章では、GUIプログラミングの中核をなすイベント処理について深く学習します。知識理解の面では、まずイベント駆動プログラミングモデルの本質を深く理解します。従来の手続き型プログラミングが「プログラムが処理の流れを制御する」のに対し、イベント駆動モデルでは「ユーザーの操作やシステムの状態変化がプログラムの流れを制御する」という根本的な違いを理解し、この考え方にもとづいた効果的なプログラム設計ができます。

Swingイベント処理機構の詳細についても学習します。イベントの発生から処理までの流れ、イベントディスパッチスレッド（EDT）の役割、イベントキューのしくみなど、Swingがどのようにイベントを管理し、処理しているかを理解します。また、ActionEvent、MouseEvent、KeyEvent、WindowEventなど、主要なイベントの種類とそれぞれに対応するリスナインターフェイスの特徴を学び、適切なイベント処理の選択ができます。EDTとスレッド安全性の基本概念の理解も重要で、なぜGUIの更新はEDT上で行わなければならないのか、長時間かかる処理をどのように扱うべきかを学びます。

技能習得の観点では、各種イベントリスナの適切な実装方法を習得します。単純なボタンクリックから、複雑なマウスドラッグ操作、キーボードショートカットの実装まで、さまざまなユーザー操作に対応する技術を身につけます。ラムダ式を活用した簡潔なイベント処理の記述方法も重要な学習項目で、従来の匿名内部クラスと比較して、どれだけコードが簡潔になるかを実感します。また、標準的なイベントだけでなく、カスタムイベントとリスナの作成方法も学び、独自のコンポーネントに特化したイベント処理を実装する能力を身につけます。

アプリケーション設計能力の面では、応答性の高いGUIアプリケーションの設計手法を学びます。ユーザーの操作に対して即座にフィードバックを返し、処理中であることを適切に表示し、長時間の処理でもUIがフリーズしないような設計パターンを習得します。また、イベント処理を活用した動的なUI実装、たとえばユーザーの操作に応じてコンポーネントを動的に生成・削除したり、状態に応じて表示を切り替えたりする技術も学びます。

最終的な到達レベルとして、複雑なイベント処理を含むGUIアプリケーションが実装できるようになることを目指します。これには、複数のイベントを組み合わせた高度な操作（ドラッグ&ドロップ、ジェスチャ認識など）の実装、カスタムコンポーネントと独自のイベント処理の設計・実装、そしてパフォーマンスと応答性を考慮した最適化されたイベント処理の実装が含まれます。これらのスキルにより、プロフェッショナルレベルのインタラクティブなGUIアプリケーションを開発できます。



## 章の構成

本章は、GUIのイベント処理を体系的に学習できるよう、以下のパートで構成されています：

### [Part A: イベント処理入門](chapter19a-event-basics.md)
- イベント処理の3要素
- ボタンクリックの実装
- ラムダ式による簡潔な記述
- 簡単なアプリケーション作成

### [Part B: 代表的なイベントの種類](chapter19b-event-types.md)
- マウスイベントの完全な処理
- キーボードイベントの高度な処理
- WindowListenerとウィンドウイベント

### [Part C: 高度なイベント処理](chapter19c-advanced-events.md)
- DocumentListenerによるテキスト変更の監視
- カスタムイベントとObserverパターン
- ドラッグ&ドロップの実装

### [Part D: 章末演習](chapter19d-exercises.md)
- インタラクティブ描画アプリケーション
- フォームバリデータ
- 高度なメニューシステム
- マウストラッカー

## 学習の進め方

1. Part Aでイベント処理の基本概念を理解
2. Part Bで主要なイベントタイプとリスナを学習
3. Part Cで高度なイベント処理テクニックを習得
4. Part Dの演習課題で実践的なスキルを身につける

各パートは独立して読むことも可能ですが、順番に学習することで、イベント駆動プログラミングの基礎から応用まで体系的に習得できるよう設計されています。

本章で学んだGUIイベント処理の概念を活用して、実践的な練習課題に取り組みましょう。

#### 演習の目標
高度なGUIイベント処理とリアルタイムな双方向GUIアプリケーションの作成技術を習得します。




#### 基礎レベル課題（必須）

#### 課題1: インタラクティブ描画アプリケーション

マウス操作で図形を描画できるアプリケーションを作成してください。

**技術的背景：イベント駆動グラフィックスと描画システム**

インタラクティブな描画アプリケーションは、イベント処理の実践的な応用例です：

**マウスイベントの種類と描画への応用：**

描画アプリケーションでは、マウスの操作状態に応じて異なるイベントを処理する必要があります：

1. **MouseListenerインターフェイス** - マウスボタンの状態変化を検出
2. **MouseMotionListenerインターフェイス** - マウスポインタの移動を追跡

```java
// ① MouseListener：クリックイベントの処理
mousePressed()  // ①-1
mouseReleased() // ①-2

// ② MouseMotionListener：マウス移動の処理
mouseDragged()  // ②-1
mouseMoved()    // ②-2
```

**各イベントの用途**：
- ①-1 **描画開始点の記録** - マウスボタンが押された座標を保存
- ①-2 **描画終了点の確定** - 矩形や楕円など形状の完成時に呼ばれる
- ②-1 **ドラッグ中の座標取得** - ボタンを押しながら移動中の座標を連続取得
- ②-2 **カーソル追跡** - ボタンを押さずに移動中の座標（プレビュー表示等に使用）

**描画システムのアーキテクチャ：**
- **ペイントコンポーネント**：JPanel + paintComponent()
- **ダブルバッファリング**：ちらつき防止
- **描画コマンドパターン**：アンドゥ機能実現
- **ラスタ画像**：BufferedImageへの描画

**実際のペイントアプリケーションの例：**
- **MS Paint**：シンプルで直感的
- **Photoshop**：レイヤ、ブレンドモード
- **Procreate**：タッチジェスチャ
- **Inkscape**：ベクタグラフィックス

**パフォーマンスの考慮点：**

グラフィックスアプリケーションにおけるパフォーマンス最適化の基本概念：

1. **全体再描画 vs 差分描画** - 描画効率の最適化手法
2. **BufferedImage活用** - メモリ内での画像操作による高速化
3. **アンチエイリアシング** - 滑らかな描画のための品質設定

```java
// ① 非効率的なアプローチ：毎回全体再描画
public void paintComponent(Graphics g) {
    super.paintComponent(g);
    // ①-1: すべての図形を毎回再描画
}

// ② 効率的なアプローチ：BufferedImageへの差分描画
Graphics2D g2d = bufferedImage.createGraphics(); // ②-1
g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, // ②-2
                    RenderingHints.VALUE_ANTIALIAS_ON);
```

**最適化手法の詳細**：
- ①-1 **全体再描画の問題** - 図形数に比例して処理時間が増加し、複雑な図形では描画が遅くなる
- ②-1 **BufferedImage利用** - メモリ内のビットマップに直接描画することで、表示時はコピーのみ
- ②-2 **品質設定の調整** - アンチエイリアシングにより滑らかな描画を実現（処理負荷は増加）

**アンドゥ/リドゥの実装パターン：**
- **コマンドパターン**：操作の抽象化
- **メモントパターン**：状態の保存と復元
- **スナップショット方式**：画像全体の保存
- **差分記録**：メモリ効率的

**高度な機能の実装アイデア：**
- **プレビュー表示**：ドラッグ中の図形予測
- **スナップ機能**：グリッドへの吸着
- **レイヤ管理**：複数描画層の管理
- **ズーム機能**：AffineTransformによる変換

この演習では、リアルタイムグラフィックス処理の基礎を学びます。

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




#### 課題2: フォームバリデータ

リアルタイムバリデーション機能付きのフォームを作成してください。

**技術的背景：ユーザーエクスペリエンスとデータ品質の向上**

リアルタイムバリデーションは、現代のWebアプリケーションで標準的な機能です：

**バリデーションのタイミング戦略：**
```java
// 1. 即座バリデーション（キー入力毎）
document.addDocumentListener(new DocumentListener() {
    public void insertUpdate(DocumentEvent e) { validate(); }
    public void removeUpdate(DocumentEvent e) { validate(); }
    public void changedUpdate(DocumentEvent e) { validate(); }
});

// 2. 遅延バリデーション（入力停止後）
Timer delayTimer = new Timer(500, e -> validate());
delayTimer.setRepeats(false);
```

**バリデーションルールの階層：**
1. **形式検証**：正規表現パターン
2. **論理検証**：値の整合性チェック
3. **ビジネス検証**：業務ルール適合
4. **サーバ検証**：重複チェック等

**視覚的フィードバックのベストプラクティス：**
```java
// 色による状態表示
Color VALID = new Color(144, 238, 144);   // 淡い緑
Color INVALID = new Color(255, 182, 193); // 淡い赤
Color NORMAL = UIManager.getColor("TextField.background");

// アイコンによる状態表示
Icon CHECK = new ImageIcon("check.png");
Icon CROSS = new ImageIcon("cross.png");
Icon INFO = new ImageIcon("info.png");
```

**実際のアプリケーションでの例：**
- **Gmail**：メールアドレスの即座検証
- **GitHub**：ユーザー名の重複チェック
- **Amazon**：クレジットカード番号検証
- **銀行システム**：口座番号のチェックデジット

**パスワード強度の評価基準：**
- **長さ**：最低8文字以上
- **文字種**：大文字、小文字、数字、特殊文字
- **辞書攻撃耐性**：一般的な単語の検出
- **エントロピー**：予測困難性の数値化

**アクセシビリティの考慮：**
- **スクリーンリーダー対応**：適切なラベル付け
- **色覚多様性**：色以外の表示方法
- **キーボードナビゲーション**：Tabキーでの移動
- **エラーメッセージ**：明確で具体的な指示

この演習では、ユーザー中心設計の実践とデータ品質向上の手法を学びます。

**要求仕様：**
- 複数の入力フィールド（名前、メール、電話番号、パスワード、確認パスワード）
- リアルタイムバリデーション（文字入力時に即座にチェック）
- バリデーション結果の視覚的フィードバック（色変更、アイコン表示）
- 送信ボタンの有効/無効制御
- キーボードショートカット（Enterで次フィールド移動等）

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
- Timerクラスで入力完了後の遅延バリデーション
- setBorder（）やsetBackground() で視覚的フィードバック
- ActionMapとInputMapでキーバインディング設定



#### 課題3: 高度なメニューシステム

**学習目標：** JMenuBar、JMenu、JMenuItemの階層構造、Actionクラスによる統一的なアクション管理、PopupMenuの作成と表示制御

**問題説明：**
多層メニューシステムと動的メニュー生成機能を持つアプリケーションを作成してください。

**技術的背景：Actionパターンと統一的なUI操作**

Actionパターンは、同じ機能を複数のUI要素から実行できるようにする設計パターンです：

**Actionパターンの利点：**
```java
// 従来の方法：重複コード
JMenuItem saveMenuItem = new JMenuItem("保存");
saveMenuItem.addActionListener(e -> saveFile());
JButton saveButton = new JButton("保存");
saveButton.addActionListener(e -> saveFile());

// Actionパターン：一元管理
Action saveAction = new AbstractAction("保存") {
    public void actionPerformed(ActionEvent e) {
        saveFile();
    }
};
saveAction.putValue(Action.ACCELERATOR_KEY, 
    KeyStroke.getKeyStroke("ctrl S"));
saveAction.putValue(Action.SMALL_ICON, saveIcon);

// 複数のUI要素で共有
new JMenuItem(saveAction);
new JButton(saveAction);
```

**動的メニューの実装パターン：**
- **MRU（Most Recently Used）**：最近使用ファイル
- **プラグインメニュー**：動的追加/削除
- **コンテキスト依存**：状態に応じた内容
- **ロールベース**：権限にもとづく表示

**コンテキストメニューの実装：**
```java
// マウスイベントでの表示制御
component.addMouseListener(new MouseAdapter() {
    public void mousePressed(MouseEvent e) {
        if (e.isPopupTrigger()) showPopup(e);
    }
    public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger()) showPopup(e);
    }
    private void showPopup(MouseEvent e) {
        // コンテキストに応じたメニュー構築
        JPopupMenu popup = createContextMenu(e.getComponent());
        popup.show(e.getComponent(), e.getX(), e.getY());
    }
});
```

**実際のアプリケーションでのメニュー設計：**
- **Office製品**：リボンUI、コンテキストメニュー
- **IDE**：高度にカスタマイズ可能なメニュー
- **ブラウザ**：拡張機能によるメニュー追加
- **ゲーム**：状態に応じたメニュー切替

**メニューのアクセシビリティ：**
- **ニーモニック**：Alt+Fでファイルメニュー
- **アクセラレータ**：Ctrl+Sで保存
- **ツールチップ**：機能説明の表示
- **ステータス表示**：有効/無効の表現

**パフォーマンス最適化：**
- **遅延ロード**：メニュー表示時に構築
- **キャッシュ**：頻繁に使うメニューの保持
- **非同期更新**：バックグラウンドでの状態確認

この演習では、プロフェッショナルなアプリケーションレベルのメニュー設計を学びます。

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
- Actionクラスでアクションの統一管理
- MenuListenerでメニューの表示/非表示イベントを処理
- DefaultListModelでMRUリストを管理



#### 課題4: マウストラッカー

**学習目標：** MouseListener、MouseMotionListenerの詳細実装、Pointクラスと座標計算、数学的計算（距離、速度、角度）

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
- Pointクラスと座標計算
- 数学的計算（距離、速度、角度）

**実装ヒント：**
- System.currentTimeMillis() で時間測定
- Point.distance() で移動距離計算
- CircularBufferの使用でメモリ効率化



### 実装のヒント

#### 高度なイベント処理のポイント

1. **イベントの種類**: MouseEvent、KeyEvent、ActionEvent、DocumentEvent
2. **リスナの実装**: 適切なリスナインターフェイスの選択と実装
3. **イベントの伝播**: イベントの発生源から処理まで
4. **タイマー処理**: javax.swing.Timerによる定期処理
5. **カスタム描画**: paintComponent()のオーバーライド
6. **状態管理**: アプリケーション状態とUI状態の同期

#### よくある落とし穴
- EDT以外からのGUI操作（SwingUtilities.invokeLater使用）
- イベントリスナの登録忘れまたは重複登録
- メモリリークの原因となるリスナの削除忘れ
- 適切なイベント処理パフォーマンスの考慮不足

#### 設計のベストプラクティス
- イベント処理ロジックとビジネスロジックの分離
- リアルタイム性とレスポンシブネスの両立
- ユーザビリティを重視したイベント設計
- 複雑なイベント処理の段階的な実装



### 実装環境

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



### 完了確認チェックリスト

#### 基礎レベル
- [ ] インタラクティブ描画でマウスイベント処理ができている
- [ ] フォームバリデータでリアルタイム検証ができている
- [ ] メニューシステムで動的メニューが作成できている
- [ ] マウストラッカーで詳細な動作分析ができている

#### 技術要素
- [ ] 高度なイベント処理のしくみを理解している
- [ ] リアルタイムな双方向性を実装できている
- [ ] カスタム描画とアニメーションができている
- [ ] ユーザーの多様な操作に適切に応答できている

#### 応用レベル
- [ ] 複雑なイベント処理を含むアプリケーションが構築できている
- [ ] パフォーマンスを考慮したイベント処理ができている
- [ ] カスタムコンポーネントとイベント処理が作成できている
- [ ] ユーザビリティの高いインターフェイスが設計できている

**次のステップ**: 基本課題が完了したら、advanced/の発展課題でより複雑なイベント処理とカスタムコンポーネントの実装に挑戦しましょう！



## 18.1 イベント処理入門

前章で作成した画面は、まだ見た目だけの「静的」なものでした。GUIアプリケーションを「動かす」ためには、ユーザーの操作（イベント）に応じた処理（イベントハンドリング）を実装する必要があります。

### 18.1.1 イベント処理の3要素

Swingのイベント処理は、以下の3つの要素で構成されます。

1.  **イベントソース (Event Source)**: イベントを発生させる部品 (`JButton`、`JTextField`など)。
2.  **イベントオブジェクト (Event Object)**: 発生したイベントの詳細情報を持つオブジェクト (`ActionEvent`、`MouseEvent`など)。
3.  **イベントリスナ (Event Listener)**: イベントの発生を監視し、通知を受け取って実際の処理を実行するオブジェクト (`ActionListener`など)。

処理の流れは、「ユーザーが**イベントソース**を操作 → **イベントオブジェクト**が生成され、登録済みの**イベントリスナ**に通知 → リスナ内の処理が実行される」となります。

### 18.1.2 ボタンクリックに応答する

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

### 18.1.3 ラムダ式による簡潔な記述

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

### 18.1.4 簡単なアプリケーション：挨拶プログラム

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

## 18.2 代表的なイベントの種類

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

### 18.2.1 `WindowListener`で終了確認

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

### 18.2.2 詳細なイベント処理の実装例

##### 1. マウスイベントの完全な処理

```java
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MouseEventCompleteExample extends JFrame {
    private JTextArea logArea;
    private JPanel drawPanel;
    private Point startPoint;
    private Point currentPoint;
    
    public MouseEventCompleteExample() {
        setTitle("マウスイベント完全例");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // 描画パネル
        drawPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (startPoint != null && currentPoint != null) {
                    g.setColor(Color.BLUE);
                    g.drawLine(startPoint.x, startPoint.y, currentPoint.x, currentPoint.y);
                }
            }
        };
        drawPanel.setBackground(Color.WHITE);
        drawPanel.setPreferredSize(new Dimension(400, 300));
        drawPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        
        // ログエリア
        logArea = new JTextArea(10, 40);
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);
        
        // マウスリスナーの追加
        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String button = getButtonName(e.getButton());
                int clickCount = e.getClickCount();
                log("クリック: " + button + " (回数: " + clickCount + ") at " + 
                    e.getPoint());
                
                // ダブルクリックの検出
                if (clickCount == 2) {
                    log("ダブルクリック検出！");
                }
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                startPoint = e.getPoint();
                log("マウス押下: " + getButtonName(e.getButton()) + " at " + startPoint);
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                log("マウス解放: " + getButtonName(e.getButton()) + " at " + e.getPoint());
                startPoint = null;
                currentPoint = null;
                drawPanel.repaint();
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                drawPanel.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
                log("マウスがパネルに入りました");
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                drawPanel.setCursor(Cursor.getDefaultCursor());
                log("マウスがパネルから出ました");
            }
            
            @Override
            public void mouseDragged(MouseEvent e) {
                currentPoint = e.getPoint();
                drawPanel.repaint();
                log("ドラッグ中: " + currentPoint);
            }
            
            @Override
            public void mouseMoved(MouseEvent e) {
                // 頻繁に発生するので通常はログしない
                // log("マウス移動: " + e.getPoint());
            }
            
            private String getButtonName(int button) {
                switch (button) {
                    case MouseEvent.BUTTON1: return "左ボタン";
                    case MouseEvent.BUTTON2: return "中央ボタン";
                    case MouseEvent.BUTTON3: return "右ボタン";
                    default: return "不明なボタン";
                }
            }
        };
        
        drawPanel.addMouseListener(mouseHandler);
        drawPanel.addMouseMotionListener(mouseHandler);
        
        // コンテキストメニュー（右クリックメニュー）
        JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.add(new JMenuItem("切り取り"));
        popupMenu.add(new JMenuItem("コピー"));
        popupMenu.add(new JMenuItem("貼り付け"));
        
        drawPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
        
        add(drawPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(null);
    }
    
    private void log(String message) {
        logArea.append(message + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MouseEventCompleteExample().setVisible(true);
        });
    }
}
```

##### 2. キーボードイベントの高度な処理

```java
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Set;

public class KeyboardEventAdvancedExample extends JFrame {
    private JTextArea textArea;
    private JLabel statusLabel;
    private Set<Integer> pressedKeys = new HashSet<>();
    
    public KeyboardEventAdvancedExample() {
        setTitle("高度なキーボードイベント処理");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // テキストエリア
        textArea = new JTextArea(10, 40);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(textArea);
        
        // ステータスラベル
        statusLabel = new JLabel("キーを押してください");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // キーリスナーの追加
        textArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                pressedKeys.add(e.getKeyCode());
                updateStatus(e, "押下");
                
                // ショートカットキーの検出
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_S) {
                    saveDocument();
                    e.consume(); // デフォルトの動作をキャンセル
                } else if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_A) {
                    textArea.selectAll();
                    e.consume();
                } else if (e.getKeyCode() == KeyEvent.VK_F1) {
                    showHelp();
                }
                
                // 複数キーの同時押し検出
                if (pressedKeys.contains(KeyEvent.VK_CONTROL) && 
                    pressedKeys.contains(KeyEvent.VK_SHIFT) && 
                    pressedKeys.contains(KeyEvent.VK_D)) {
                    duplicateLine();
                }
            }
            
            @Override
            public void keyReleased(KeyEvent e) {
                pressedKeys.remove(e.getKeyCode());
                updateStatus(e, "解放");
            }
            
            @Override
            public void keyTyped(KeyEvent e) {
                // 文字が入力された時
                char ch = e.getKeyChar();
                if (!Character.isISOControl(ch)) {
                    statusLabel.setText("入力文字: '" + ch + "' (Unicode: " + 
                        (int)ch + ")");
                }
            }
        });
        
        // InputMapとActionMapを使ったより高度なキーバインディング
        InputMap inputMap = textArea.getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap actionMap = textArea.getActionMap();
        
        // Ctrl+Dで行複製
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 
            InputEvent.CTRL_DOWN_MASK), "duplicate-line");
        actionMap.put("duplicate-line", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                duplicateLine();
            }
        });
        
        // F5でタイムスタンプ挿入
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), "insert-timestamp");
        actionMap.put("insert-timestamp", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textArea.insert(java.time.LocalDateTime.now().toString(), 
                    textArea.getCaretPosition());
            }
        });
        
        // メニューバーの作成（アクセラレータキー付き）
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("ファイル");
        fileMenu.setMnemonic(KeyEvent.VK_F); // Alt+F
        
        JMenuItem saveItem = new JMenuItem("保存");
        saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, 
            InputEvent.CTRL_DOWN_MASK));
        saveItem.addActionListener(e -> saveDocument());
        
        JMenuItem exitItem = new JMenuItem("終了");
        exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, 
            InputEvent.CTRL_DOWN_MASK));
        exitItem.addActionListener(e -> System.exit(0));
        
        fileMenu.add(saveItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);
        
        setJMenuBar(menuBar);
        add(scrollPane, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(null);
    }
    
    private void updateStatus(KeyEvent e, String action) {
        String modifiers = "";
        if (e.isControlDown()) modifiers += "Ctrl+";
        if (e.isAltDown()) modifiers += "Alt+";
        if (e.isShiftDown()) modifiers += "Shift+";
        
        String keyText = KeyEvent.getKeyText(e.getKeyCode());
        statusLabel.setText("キー" + action + ": " + modifiers + keyText + 
            " (コード: " + e.getKeyCode() + ")");
    }
    
    private void saveDocument() {
        JOptionPane.showMessageDialog(this, "ドキュメントを保存しました（仮想）");
    }
    
    private void showHelp() {
        JOptionPane.showMessageDialog(this, 
            "ショートカットキー:\n" +
            "Ctrl+S: 保存\n" +
            "Ctrl+A: すべて選択\n" +
            "Ctrl+D: 行複製\n" +
            "F1: このヘルプ\n" +
            "F5: タイムスタンプ挿入");
    }
    
    private void duplicateLine() {
        try {
            int caretPos = textArea.getCaretPosition();
            int lineNum = textArea.getLineOfOffset(caretPos);
            int lineStart = textArea.getLineStartOffset(lineNum);
            int lineEnd = textArea.getLineEndOffset(lineNum);
            String line = textArea.getText(lineStart, lineEnd - lineStart);
            textArea.insert(line, lineEnd);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new KeyboardEventAdvancedExample().setVisible(true);
        });
    }
}
```

##### 3. DocumentListenerによるテキスト変更の監視

```java
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.awt.*;
import java.util.regex.*;

public class DocumentListenerExample extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextArea commentArea;
    private JLabel emailStatus;
    private JLabel passwordStatus;
    private JLabel confirmStatus;
    private JLabel charCountLabel;
    private JButton submitButton;
    
    public DocumentListenerExample() {
        setTitle("リアルタイムバリデーション");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Email入力
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Email:"), gbc);
        
        gbc.gridx = 1;
        emailField = new JTextField(20);
        add(emailField, gbc);
        
        gbc.gridx = 2;
        emailStatus = new JLabel("必須");
        emailStatus.setForeground(Color.GRAY);
        add(emailStatus, gbc);
        
        // パスワード入力
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("パスワード:"), gbc);
        
        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        add(passwordField, gbc);
        
        gbc.gridx = 2;
        passwordStatus = new JLabel("8文字以上");
        passwordStatus.setForeground(Color.GRAY);
        add(passwordStatus, gbc);
        
        // パスワード確認
        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("パスワード確認:"), gbc);
        
        gbc.gridx = 1;
        confirmPasswordField = new JPasswordField(20);
        add(confirmPasswordField, gbc);
        
        gbc.gridx = 2;
        confirmStatus = new JLabel("一致確認");
        confirmStatus.setForeground(Color.GRAY);
        add(confirmStatus, gbc);
        
        // コメント入力
        gbc.gridx = 0; gbc.gridy = 3;
        add(new JLabel("コメント:"), gbc);
        
        gbc.gridx = 1; gbc.gridwidth = 2;
        commentArea = new JTextArea(5, 30);
        commentArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(commentArea);
        add(scrollPane, gbc);
        
        gbc.gridx = 1; gbc.gridy = 4; gbc.gridwidth = 1;
        charCountLabel = new JLabel("0/200文字");
        add(charCountLabel, gbc);
        
        // 送信ボタン
        gbc.gridx = 1; gbc.gridy = 5;
        submitButton = new JButton("送信");
        submitButton.setEnabled(false);
        add(submitButton, gbc);
        
        // DocumentListenerの追加
        emailField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { validateEmail(); }
            public void removeUpdate(DocumentEvent e) { validateEmail(); }
            public void changedUpdate(DocumentEvent e) { validateEmail(); }
        });
        
        passwordField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { validatePassword(); }
            public void removeUpdate(DocumentEvent e) { validatePassword(); }
            public void changedUpdate(DocumentEvent e) { validatePassword(); }
        });
        
        confirmPasswordField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { validatePasswordConfirm(); }
            public void removeUpdate(DocumentEvent e) { validatePasswordConfirm(); }
            public void changedUpdate(DocumentEvent e) { validatePasswordConfirm(); }
        });
        
        // 文字数制限付きDocumentListenter
        commentArea.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateCharCount(); }
            public void removeUpdate(DocumentEvent e) { updateCharCount(); }
            public void changedUpdate(DocumentEvent e) { updateCharCount(); }
            
            private void updateCharCount() {
                int length = commentArea.getText().length();
                charCountLabel.setText(length + "/200文字");
                if (length > 200) {
                    charCountLabel.setForeground(Color.RED);
                    // 文字数制限を超えた場合の処理
                    SwingUtilities.invokeLater(() -> {
                        try {
                            commentArea.setText(commentArea.getText(0, 200));
                        } catch (BadLocationException ex) {
                            ex.printStackTrace();
                        }
                    });
                } else {
                    charCountLabel.setForeground(Color.BLACK);
                }
                checkFormValidity();
            }
        });
        
        pack();
        setLocationRelativeTo(null);
    }
    
    private void validateEmail() {
        String email = emailField.getText();
        Pattern pattern = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
        Matcher matcher = pattern.matcher(email);
        
        if (email.isEmpty()) {
            emailStatus.setText("必須");
            emailStatus.setForeground(Color.GRAY);
            emailField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        } else if (matcher.matches()) {
            emailStatus.setText("✓ OK");
            emailStatus.setForeground(Color.GREEN);
            emailField.setBorder(BorderFactory.createLineBorder(Color.GREEN));
        } else {
            emailStatus.setText("✗ 無効");
            emailStatus.setForeground(Color.RED);
            emailField.setBorder(BorderFactory.createLineBorder(Color.RED));
        }
        checkFormValidity();
    }
    
    private void validatePassword() {
        String password = new String(passwordField.getPassword());
        boolean hasUpperCase = !password.equals(password.toLowerCase());
        boolean hasLowerCase = !password.equals(password.toUpperCase());
        boolean hasDigit = password.matches(".*\\d.*");
        
        if (password.length() >= 8 && hasUpperCase && hasLowerCase && hasDigit) {
            passwordStatus.setText("✓ 強い");
            passwordStatus.setForeground(Color.GREEN);
            passwordField.setBorder(BorderFactory.createLineBorder(Color.GREEN));
        } else if (password.length() >= 8) {
            passwordStatus.setText("△ 普通");
            passwordStatus.setForeground(Color.ORANGE);
            passwordField.setBorder(BorderFactory.createLineBorder(Color.ORANGE));
        } else {
            passwordStatus.setText("✗ 弱い");
            passwordStatus.setForeground(Color.RED);
            passwordField.setBorder(BorderFactory.createLineBorder(Color.RED));
        }
        validatePasswordConfirm();
        checkFormValidity();
    }
    
    private void validatePasswordConfirm() {
        String password = new String(passwordField.getPassword());
        String confirm = new String(confirmPasswordField.getPassword());
        
        if (confirm.isEmpty()) {
            confirmStatus.setText("一致確認");
            confirmStatus.setForeground(Color.GRAY);
            confirmPasswordField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        } else if (password.equals(confirm)) {
            confirmStatus.setText("✓ 一致");
            confirmStatus.setForeground(Color.GREEN);
            confirmPasswordField.setBorder(BorderFactory.createLineBorder(Color.GREEN));
        } else {
            confirmStatus.setText("✗ 不一致");
            confirmStatus.setForeground(Color.RED);
            confirmPasswordField.setBorder(BorderFactory.createLineBorder(Color.RED));
        }
        checkFormValidity();
    }
    
    private void checkFormValidity() {
        boolean emailValid = emailStatus.getText().contains("✓");
        boolean passwordValid = passwordStatus.getText().contains("✓") || 
                              passwordStatus.getText().contains("△");
        boolean confirmValid = confirmStatus.getText().contains("✓");
        
        submitButton.setEnabled(emailValid && passwordValid && confirmValid);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new DocumentListenerExample().setVisible(true);
        });
    }
}
```

##### 4. カスタムイベントとObserverパターン

```java
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

// カスタムイベントクラス
class TemperatureChangeEvent extends EventObject {
    private final double temperature;
    
    public TemperatureChangeEvent(Object source, double temperature) {
        super(source);
        this.temperature = temperature;
    }
    
    public double getTemperature() {
        return temperature;
    }
}

// カスタムリスナーインターフェース
interface TemperatureChangeListener extends EventListener {
    void temperatureChanged(TemperatureChangeEvent e);
}

// 温度センサーモデル
class TemperatureSensor {
    private double temperature = 20.0;
    private List<TemperatureChangeListener> listeners = new ArrayList<>();
    
    public void addTemperatureChangeListener(TemperatureChangeListener listener) {
        listeners.add(listener);
    }
    
    public void removeTemperatureChangeListener(TemperatureChangeListener listener) {
        listeners.remove(listener);
    }
    
    public void setTemperature(double temperature) {
        this.temperature = temperature;
        fireTemperatureChanged();
    }
    
    private void fireTemperatureChanged() {
        TemperatureChangeEvent event = new TemperatureChangeEvent(this, temperature);
        for (TemperatureChangeListener listener : listeners) {
            listener.temperatureChanged(event);
        }
    }
    
    public double getTemperature() {
        return temperature;
    }
}

// メインアプリケーション
public class CustomEventExample extends JFrame {
    private TemperatureSensor sensor;
    private JSlider temperatureSlider;
    private JLabel temperatureLabel;
    private JProgressBar temperatureBar;
    private JPanel colorPanel;
    private List<JLabel> observerLabels;
    
    public CustomEventExample() {
        setTitle("カスタムイベントとObserverパターン");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        sensor = new TemperatureSensor();
        
        // コントロールパネル
        JPanel controlPanel = new JPanel();
        temperatureSlider = new JSlider(-20, 50, 20);
        temperatureSlider.setMajorTickSpacing(10);
        temperatureSlider.setPaintTicks(true);
        temperatureSlider.setPaintLabels(true);
        
        temperatureSlider.addChangeListener(e -> {
            if (!temperatureSlider.getValueIsAdjusting()) {
                sensor.setTemperature(temperatureSlider.getValue());
            }
        });
        
        controlPanel.add(new JLabel("温度設定:"));
        controlPanel.add(temperatureSlider);
        
        // 表示パネル
        JPanel displayPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        displayPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // 温度表示
        temperatureLabel = new JLabel("現在の温度: 20.0°C");
        temperatureLabel.setFont(new Font("Arial", Font.BOLD, 20));
        displayPanel.add(temperatureLabel);
        
        // 温度バー
        temperatureBar = new JProgressBar(-20, 50);
        temperatureBar.setValue(20);
        temperatureBar.setStringPainted(true);
        displayPanel.add(temperatureBar);
        
        // 色表示パネル
        colorPanel = new JPanel();
        colorPanel.setPreferredSize(new Dimension(200, 50));
        colorPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        displayPanel.add(colorPanel);
        
        // 複数のオブザーバー
        JPanel observerPanel = new JPanel(new GridLayout(3, 1));
        observerLabels = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            JLabel label = new JLabel("Observer " + (i+1) + ": 待機中");
            observerLabels.add(label);
            observerPanel.add(label);
        }
        displayPanel.add(observerPanel);
        
        // リスナーの登録
        sensor.addTemperatureChangeListener(new TemperatureChangeListener() {
            @Override
            public void temperatureChanged(TemperatureChangeEvent e) {
                double temp = e.getTemperature();
                temperatureLabel.setText(String.format("現在の温度: %.1f°C", temp));
            }
        });
        
        sensor.addTemperatureChangeListener(new TemperatureChangeListener() {
            @Override
            public void temperatureChanged(TemperatureChangeEvent e) {
                int value = (int)e.getTemperature();
                temperatureBar.setValue(value);
                temperatureBar.setString(value + "°C");
            }
        });
        
        sensor.addTemperatureChangeListener(new TemperatureChangeListener() {
            @Override
            public void temperatureChanged(TemperatureChangeEvent e) {
                double temp = e.getTemperature();
                Color color;
                if (temp < 0) {
                    color = Color.BLUE;
                } else if (temp < 15) {
                    color = Color.CYAN;
                } else if (temp < 25) {
                    color = Color.GREEN;
                } else if (temp < 35) {
                    color = Color.ORANGE;
                } else {
                    color = Color.RED;
                }
                colorPanel.setBackground(color);
            }
        });
        
        // 個別のオブザーバー
        for (int i = 0; i < observerLabels.size(); i++) {
            final int index = i;
            sensor.addTemperatureChangeListener(new TemperatureChangeListener() {
                @Override
                public void temperatureChanged(TemperatureChangeEvent e) {
                    double temp = e.getTemperature();
                    String status;
                    if (index == 0) {
                        status = temp > 30 ? "警告: 高温！" : "正常";
                    } else if (index == 1) {
                        status = temp < 5 ? "警告: 低温！" : "正常";
                    } else {
                        status = String.format("%.1f°C 受信", temp);
                    }
                    observerLabels.get(index).setText("Observer " + (index+1) + ": " + status);
                }
            });
        }
        
        // 自動温度変化シミュレーション
        JButton simulateButton = new JButton("温度変化シミュレーション");
        simulateButton.addActionListener(e -> {
            Timer timer = new Timer(100, null);
            timer.addActionListener(evt -> {
                double current = sensor.getTemperature();
                double random = (Math.random() - 0.5) * 2;
                double newTemp = Math.max(-20, Math.min(50, current + random));
                sensor.setTemperature(newTemp);
                temperatureSlider.setValue((int)newTemp);
                
                if (timer.isRunning() && 
                    ((Timer)evt.getSource()).getDelay() * 
                    ((Timer)evt.getSource()).getActionListeners().length > 5000) {
                    timer.stop();
                }
            });
            timer.start();
            
            // 5秒後に停止
            Timer stopTimer = new Timer(5000, evt -> timer.stop());
            stopTimer.setRepeats(false);
            stopTimer.start();
        });
        
        controlPanel.add(simulateButton);
        
        add(controlPanel, BorderLayout.NORTH);
        add(displayPanel, BorderLayout.CENTER);
        
        pack();
        setLocationRelativeTo(null);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CustomEventExample().setVisible(true);
        });
    }
}
```

#### カスタムイベントとJavaBeansパターンの詳細

カスタムイベントの実装において、JavaBeansの仕様に従ったより洗練されたアプローチを学習しましょう。JavaBeansパターンを使用することで、再利用可能で保守性の高いコンポーネントを作成できます。

##### JavaBeansイベントモデルの基本構造

JavaBeansの仕様では、イベント処理を以下のパターンで実装することが推奨されています：

```java
import java.beans.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;

// 1. イベントクラスの定義（PropertyChangeEventを継承または独自実装）
class TemperatureEvent extends PropertyChangeEvent {
    public TemperatureEvent(Object source, String propertyName, 
                           Object oldValue, Object newValue) {
        super(source, propertyName, oldValue, newValue);
    }
    
    public double getOldTemperature() {
        return ((Number)getOldValue()).doubleValue();
    }
    
    public double getNewTemperature() {
        return ((Number)getNewValue()).doubleValue();
    }
}

// 2. リスナーインターフェースの定義
interface TemperatureListener extends EventListener {
    void temperatureChanged(TemperatureEvent e);
}

// 3. JavaBeans準拠のモデルクラス
class TemperatureSensorBean {
    private double temperature = 20.0;
    private String sensorName = "DefaultSensor";
    private PropertyChangeSupport propertySupport;
    private List<TemperatureListener> temperatureListeners;
    
    public TemperatureSensorBean() {
        propertySupport = new PropertyChangeSupport(this);
        temperatureListeners = new ArrayList<>();
    }
    
    // --- JavaBeans準拠のプロパティ ---
    
    public double getTemperature() {
        return temperature;
    }
    
    public void setTemperature(double newTemperature) {
        double oldTemperature = this.temperature;
        this.temperature = newTemperature;
        
        // PropertyChangeEventを発行
        propertySupport.firePropertyChange("temperature", oldTemperature, newTemperature);
        
        // カスタムイベントを発行
        fireTemperatureChanged(oldTemperature, newTemperature);
    }
    
    public String getSensorName() {
        return sensorName;
    }
    
    public void setSensorName(String sensorName) {
        String oldName = this.sensorName;
        this.sensorName = sensorName;
        propertySupport.firePropertyChange("sensorName", oldName, sensorName);
    }
    
    // --- PropertyChangeListener関連（JavaBeans標準） ---
    
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(listener);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(listener);
    }
    
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(propertyName, listener);
    }
    
    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(propertyName, listener);
    }
    
    // --- カスタムリスナー関連 ---
    
    public void addTemperatureListener(TemperatureListener listener) {
        temperatureListeners.add(listener);
    }
    
    public void removeTemperatureListener(TemperatureListener listener) {
        temperatureListeners.remove(listener);
    }
    
    private void fireTemperatureChanged(double oldTemperature, double newTemperature) {
        TemperatureEvent event = new TemperatureEvent(this, "temperature", 
                                                     oldTemperature, newTemperature);
        for (TemperatureListener listener : temperatureListeners) {
            listener.temperatureChanged(event);
        }
    }
    
    // --- ユーティリティメソッド ---
    
    public void simulateTemperatureChange() {
        double change = (Math.random() - 0.5) * 4; // -2℃ から +2℃ の変化
        setTemperature(Math.max(-30, Math.min(50, temperature + change)));
    }
}

// 4. 複数の通知方式を実装したGUIアプリケーション
public class JavaBeansEventExample extends JFrame {
    private TemperatureSensorBean sensor;
    private JSlider temperatureSlider;
    private JLabel temperatureLabel;
    private JLabel sensorNameLabel;
    private JTextField sensorNameField;
    private JTextArea eventLogArea;
    private JPanel visualIndicator;
    private Timer simulationTimer;
    
    public JavaBeansEventExample() {
        setTitle("JavaBeansイベントモデルの実装");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        initializeComponents();
        setupSensorBean();
        layoutComponents();
        
        pack();
        setLocationRelativeTo(null);
    }
    
    private void initializeComponents() {
        // 温度制御
        temperatureSlider = new JSlider(-30, 50, 20);
        temperatureSlider.setMajorTickSpacing(10);
        temperatureSlider.setMinorTickSpacing(5);
        temperatureSlider.setPaintTicks(true);
        temperatureSlider.setPaintLabels(true);
        
        // 表示要素
        temperatureLabel = new JLabel("温度: 20.0°C");
        temperatureLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        sensorNameLabel = new JLabel("センサー名: DefaultSensor");
        sensorNameField = new JTextField("DefaultSensor", 15);
        
        visualIndicator = new JPanel();
        visualIndicator.setPreferredSize(new Dimension(100, 50));
        visualIndicator.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        
        eventLogArea = new JTextArea(10, 40);
        eventLogArea.setEditable(false);
        eventLogArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
    }
    
    private void setupSensorBean() {
        sensor = new TemperatureSensorBean();
        
        // PropertyChangeListenerの登録（JavaBeans標準）
        sensor.addPropertyChangeListener("temperature", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                double newTemp = (Double)evt.getNewValue();
                temperatureLabel.setText(String.format("温度: %.1f°C", newTemp));
                temperatureSlider.setValue((int)newTemp);
                
                // 視覚的インジケータの更新
                Color color = getTemperatureColor(newTemp);
                visualIndicator.setBackground(color);
                
                logEvent("PropertyChange", String.format("温度変更: %.1f°C → %.1f°C", 
                    (Double)evt.getOldValue(), newTemp));
            }
        });
        
        sensor.addPropertyChangeListener("sensorName", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                String newName = (String)evt.getNewValue();
                sensorNameLabel.setText("センサー名: " + newName);
                logEvent("PropertyChange", String.format("センサー名変更: %s → %s", 
                    evt.getOldValue(), newName));
            }
        });
        
        // カスタムTemperatureListenerの登録
        sensor.addTemperatureListener(new TemperatureListener() {
            @Override
            public void temperatureChanged(TemperatureEvent e) {
                double temp = e.getNewTemperature();
                String message = "";
                
                if (temp > 35) {
                    message = "⚠️ 高温警告!";
                } else if (temp < 0) {
                    message = "❄️ 氷点下警告!";
                } else if (temp >= 20 && temp <= 25) {
                    message = "✅ 適温です";
                } else {
                    message = "🌡️ 温度監視中";
                }
                
                logEvent("TemperatureListener", String.format("温度監視: %.1f°C - %s", 
                    temp, message));
            }
        });
        
        // スライダーのイベントリスナー
        temperatureSlider.addChangeListener(e -> {
            if (!temperatureSlider.getValueIsAdjusting()) {
                sensor.setTemperature(temperatureSlider.getValue());
            }
        });
        
        // センサー名フィールドのイベントリスナー
        sensorNameField.addActionListener(e -> {
            sensor.setSensorName(sensorNameField.getText());
        });
    }
    
    private void layoutComponents() {
        // 上部コントロールパネル
        JPanel controlPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        gbc.gridx = 0; gbc.gridy = 0;
        controlPanel.add(new JLabel("温度設定:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        controlPanel.add(temperatureSlider, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        controlPanel.add(new JLabel("センサー名:"), gbc);
        gbc.gridx = 1;
        controlPanel.add(sensorNameField, gbc);
        gbc.gridx = 2;
        JButton updateButton = new JButton("更新");
        updateButton.addActionListener(e -> sensor.setSensorName(sensorNameField.getText()));
        controlPanel.add(updateButton, gbc);
        
        // 中央表示パネル
        JPanel displayPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        displayPanel.setBorder(BorderFactory.createTitledBorder("センサー情報"));
        
        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        infoPanel.add(temperatureLabel);
        infoPanel.add(sensorNameLabel);
        
        JPanel indicatorPanel = new JPanel(new BorderLayout());
        indicatorPanel.add(new JLabel("温度インジケータ", JLabel.CENTER), BorderLayout.NORTH);
        indicatorPanel.add(visualIndicator, BorderLayout.CENTER);
        
        // シミュレーションコントロール
        JPanel simPanel = new JPanel(new GridLayout(3, 1));
        JButton simButton = new JButton("シミュレーション開始");
        JButton stopButton = new JButton("シミュレーション停止");
        JButton resetButton = new JButton("リセット");
        
        simButton.addActionListener(e -> startSimulation());
        stopButton.addActionListener(e -> stopSimulation());
        resetButton.addActionListener(e -> resetSensor());
        
        simPanel.add(simButton);
        simPanel.add(stopButton);
        simPanel.add(resetButton);
        
        displayPanel.add(infoPanel);
        displayPanel.add(indicatorPanel);
        displayPanel.add(simPanel);
        
        // イベントログエリア
        JScrollPane logScrollPane = new JScrollPane(eventLogArea);
        logScrollPane.setBorder(BorderFactory.createTitledBorder("イベントログ"));
        
        add(controlPanel, BorderLayout.NORTH);
        add(displayPanel, BorderLayout.CENTER);
        add(logScrollPane, BorderLayout.SOUTH);
    }
    
    private Color getTemperatureColor(double temperature) {
        if (temperature < 0) return Color.BLUE;
        if (temperature < 10) return Color.CYAN;
        if (temperature < 20) return Color.GREEN;
        if (temperature < 30) return Color.YELLOW;
        if (temperature < 40) return Color.ORANGE;
        return Color.RED;
    }
    
    private void logEvent(String source, String message) {
        String timestamp = java.time.LocalTime.now().toString().substring(0, 8);
        eventLogArea.append(String.format("[%s] %s: %s\n", timestamp, source, message));
        eventLogArea.setCaretPosition(eventLogArea.getDocument().getLength());
    }
    
    private void startSimulation() {
        if (simulationTimer != null && simulationTimer.isRunning()) {
            simulationTimer.stop();
        }
        
        logEvent("System", "自動シミュレーション開始");
        simulationTimer = new Timer(500, e -> sensor.simulateTemperatureChange());
        simulationTimer.start();
    }
    
    private void stopSimulation() {
        if (simulationTimer != null && simulationTimer.isRunning()) {
            simulationTimer.stop();
            logEvent("System", "自動シミュレーション停止");
        }
    }
    
    private void resetSensor() {
        stopSimulation();
        sensor.setTemperature(20.0);
        sensor.setSensorName("DefaultSensor");
        sensorNameField.setText("DefaultSensor");
        eventLogArea.setText("");
        logEvent("System", "センサーをリセットしました");
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new JavaBeansEventExample().setVisible(true);
        });
    }
}
```

##### 高度なJavaBeansパターンの実装

より複雑な業務アプリケーションで使用される高度なJavaBeansパターンを学習しましょう：

```java
import java.beans.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;

// 1. 制約付きプロパティ（Constrained Properties）の実装
class SmartThermostatBean {
    private double temperature = 20.0;
    private double targetTemperature = 22.0;
    private boolean heatingEnabled = false;
    private PropertyChangeSupport propertySupport;
    private VetoableChangeSupport vetoSupport;
    
    public SmartThermostatBean() {
        propertySupport = new PropertyChangeSupport(this);
        vetoSupport = new VetoableChangeSupport(this);
    }
    
    // 制約付きプロパティ：設定できる温度に制限がある
    public void setTargetTemperature(double newTarget) throws PropertyVetoException {
        double oldTarget = this.targetTemperature;
        
        // 制約チェック：範囲外の値は拒否
        if (newTarget < 10 || newTarget > 40) {
            throw new PropertyVetoException(
                "目標温度は10℃〜40℃の範囲で設定してください", 
                new PropertyChangeEvent(this, "targetTemperature", oldTarget, newTarget));
        }
        
        // VetoableChangeListenerに変更許可を確認
        vetoSupport.fireVetoableChange("targetTemperature", oldTarget, newTarget);
        
        // 変更を適用
        this.targetTemperature = newTarget;
        propertySupport.firePropertyChange("targetTemperature", oldTarget, newTarget);
        
        // 自動制御の実行
        updateHeatingStatus();
    }
    
    public double getTargetTemperature() {
        return targetTemperature;
    }
    
    public void setTemperature(double temperature) {
        double oldTemperature = this.temperature;
        this.temperature = temperature;
        propertySupport.firePropertyChange("temperature", oldTemperature, temperature);
        
        // 自動制御の実行
        updateHeatingStatus();
    }
    
    public double getTemperature() {
        return temperature;
    }
    
    public boolean isHeatingEnabled() {
        return heatingEnabled;
    }
    
    private void updateHeatingStatus() {
        boolean oldStatus = this.heatingEnabled;
        this.heatingEnabled = temperature < targetTemperature - 1.0;
        propertySupport.firePropertyChange("heatingEnabled", oldStatus, heatingEnabled);
    }
    
    // VetoableChangeListener関連
    public void addVetoableChangeListener(VetoableChangeListener listener) {
        vetoSupport.addVetoableChangeListener(listener);
    }
    
    public void removeVetoableChangeListener(VetoableChangeListener listener) {
        vetoSupport.removeVetoableChangeListener(listener);
    }
    
    public void addVetoableChangeListener(String propertyName, VetoableChangeListener listener) {
        vetoSupport.addVetoableChangeListener(propertyName, listener);
    }
    
    public void removeVetoableChangeListener(String propertyName, VetoableChangeListener listener) {
        vetoSupport.removeVetoableChangeListener(propertyName, listener);
    }
    
    // PropertyChangeListener関連
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(listener);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(listener);
    }
    
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(propertyName, listener);
    }
    
    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(propertyName, listener);
    }
}

// 2. 複合イベントとバッチ更新の実装
public class AdvancedJavaBeansExample extends JFrame {
    private SmartThermostatBean thermostat;
    private JSlider currentTempSlider;
    private JSlider targetTempSlider;
    private JLabel currentTempLabel;
    private JLabel targetTempLabel;
    private JLabel heatingStatusLabel;
    private JTextArea logArea;
    private JCheckBox vetoCheckBox;
    
    public AdvancedJavaBeansExample() {
        setTitle("高度なJavaBeansパターン - スマートサーモスタット");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initializeComponents();
        setupThermostatBean();
        layoutComponents();
        pack();
        setLocationRelativeTo(null);
    }
    
    private void initializeComponents() {
        currentTempSlider = new JSlider(0, 50, 20);
        currentTempSlider.setMajorTickSpacing(10);
        currentTempSlider.setPaintTicks(true);
        currentTempSlider.setPaintLabels(true);
        
        targetTempSlider = new JSlider(10, 40, 22);
        targetTempSlider.setMajorTickSpacing(5);
        targetTempSlider.setPaintTicks(true);
        targetTempSlider.setPaintLabels(true);
        
        currentTempLabel = new JLabel("現在温度: 20.0°C");
        targetTempLabel = new JLabel("目標温度: 22.0°C");
        heatingStatusLabel = new JLabel("暖房: OFF");
        
        vetoCheckBox = new JCheckBox("制約チェックを有効にする", true);
        
        logArea = new JTextArea(12, 50);
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
    }
    
    private void setupThermostatBean() {
        thermostat = new SmartThermostatBean();
        
        // PropertyChangeListenerの設定
        thermostat.addPropertyChangeListener("temperature", evt -> {
            double temp = (Double)evt.getNewValue();
            currentTempLabel.setText(String.format("現在温度: %.1f°C", temp));
            currentTempSlider.setValue((int)temp);
            log("温度変更", String.format("%.1f°C → %.1f°C", 
                (Double)evt.getOldValue(), temp));
        });
        
        thermostat.addPropertyChangeListener("targetTemperature", evt -> {
            double target = (Double)evt.getNewValue();
            targetTempLabel.setText(String.format("目標温度: %.1f°C", target));
            targetTempSlider.setValue((int)target);
            log("目標温度変更", String.format("%.1f°C → %.1f°C", 
                (Double)evt.getOldValue(), target));
        });
        
        thermostat.addPropertyChangeListener("heatingEnabled", evt -> {
            boolean enabled = (Boolean)evt.getNewValue();
            heatingStatusLabel.setText("暖房: " + (enabled ? "ON" : "OFF"));
            heatingStatusLabel.setForeground(enabled ? Color.RED : Color.BLUE);
            log("暖房状態", enabled ? "ON" : "OFF");
        });
        
        // VetoableChangeListenerの設定
        thermostat.addVetoableChangeListener("targetTemperature", evt -> {
            if (vetoCheckBox.isSelected()) {
                double newTarget = (Double)evt.getNewValue();
                double currentTemp = thermostat.getTemperature();
                
                // 制約：現在温度との差が15℃以上の場合は拒否
                if (Math.abs(newTarget - currentTemp) > 15) {
                    String message = String.format(
                        "目標温度と現在温度の差が大きすぎます（差: %.1f℃）", 
                        Math.abs(newTarget - currentTemp));
                    log("制約違反", message);
                    throw new PropertyVetoException(message, evt);
                }
            }
        });
        
        // スライダーイベントの設定
        currentTempSlider.addChangeListener(e -> {
            if (!currentTempSlider.getValueIsAdjusting()) {
                thermostat.setTemperature(currentTempSlider.getValue());
            }
        });
        
        targetTempSlider.addChangeListener(e -> {
            if (!targetTempSlider.getValueIsAdjusting()) {
                try {
                    thermostat.setTargetTemperature(targetTempSlider.getValue());
                } catch (PropertyVetoException ex) {
                    // 拒否された場合は元の値に戻す
                    SwingUtilities.invokeLater(() -> {
                        targetTempSlider.setValue((int)thermostat.getTargetTemperature());
                        JOptionPane.showMessageDialog(this, ex.getMessage(), 
                            "設定エラー", JOptionPane.WARNING_MESSAGE);
                    });
                }
            }
        });
    }
    
    private void layoutComponents() {
        setLayout(new BorderLayout());
        
        // コントロールパネル
        JPanel controlPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        gbc.gridx = 0; gbc.gridy = 0;
        controlPanel.add(new JLabel("現在温度:"), gbc);
        gbc.gridx = 1;
        controlPanel.add(currentTempSlider, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        controlPanel.add(new JLabel("目標温度:"), gbc);
        gbc.gridx = 1;
        controlPanel.add(targetTempSlider, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        controlPanel.add(vetoCheckBox, gbc);
        
        // ステータスパネル
        JPanel statusPanel = new JPanel(new GridLayout(3, 1));
        statusPanel.setBorder(BorderFactory.createTitledBorder("サーモスタット状態"));
        statusPanel.add(currentTempLabel);
        statusPanel.add(targetTempLabel);
        statusPanel.add(heatingStatusLabel);
        
        // ボタンパネル
        JPanel buttonPanel = new JPanel();
        JButton presetButton = new JButton("プリセット設定");
        JButton batchButton = new JButton("バッチ更新");
        JButton clearLogButton = new JButton("ログクリア");
        
        presetButton.addActionListener(e -> setPresetTemperature());
        batchButton.addActionListener(e -> performBatchUpdate());
        clearLogButton.addActionListener(e -> logArea.setText(""));
        
        buttonPanel.add(presetButton);
        buttonPanel.add(batchButton);
        buttonPanel.add(clearLogButton);
        
        // 上部パネル
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(controlPanel, BorderLayout.CENTER);
        topPanel.add(statusPanel, BorderLayout.EAST);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(logArea), BorderLayout.CENTER);
    }
    
    private void setPresetTemperature() {
        String[] presets = {"夏季設定 (26℃)", "冬季設定 (22℃)", "節約設定 (20℃)"};
        String selected = (String)JOptionPane.showInputDialog(this, 
            "プリセット設定を選択してください", "プリセット", 
            JOptionPane.QUESTION_MESSAGE, null, presets, presets[1]);
        
        if (selected != null) {
            try {
                if (selected.contains("26")) {
                    thermostat.setTargetTemperature(26);
                } else if (selected.contains("22")) {
                    thermostat.setTargetTemperature(22);
                } else if (selected.contains("20")) {
                    thermostat.setTargetTemperature(20);
                }
                log("プリセット", selected + " を適用しました");
            } catch (PropertyVetoException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), 
                    "プリセット設定エラー", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void performBatchUpdate() {
        // バッチ更新の例：現在温度と目標温度を同時に変更
        double newCurrent = 18.0;
        double newTarget = 24.0;
        
        try {
            log("バッチ更新", "開始");
            thermostat.setTemperature(newCurrent);
            thermostat.setTargetTemperature(newTarget);
            currentTempSlider.setValue((int)newCurrent);
            targetTempSlider.setValue((int)newTarget);
            log("バッチ更新", "完了");
        } catch (PropertyVetoException ex) {
            log("バッチ更新", "失敗: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "バッチ更新に失敗しました: " + ex.getMessage(), 
                "更新エラー", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void log(String category, String message) {
        String timestamp = java.time.LocalTime.now().toString().substring(0, 8);
        logArea.append(String.format("[%s] %s: %s\n", timestamp, category, message));
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new AdvancedJavaBeansExample().setVisible(true);
        });
    }
}
```

これらの例により、JavaBeansパターンに従ったイベント処理の実装方法を学習できます：

1. **PropertyChangeSupport**: 標準的なプロパティ変更通知
2. **VetoableChangeSupport**: 制約付きプロパティの実装  
3. **カスタムイベント**: 業務固有のイベント処理
4. **複合イベント**: 複数のプロパティ変更の協調

##### 5. ドラッグ&ドロップの実装

```java
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.io.File;
import java.util.List;

public class DragAndDropExample extends JFrame {
    private JList<String> sourceList;
    private JList<String> targetList;
    private DefaultListModel<String> sourceModel;
    private DefaultListModel<String> targetModel;
    private JTextArea dropArea;
    
    public DragAndDropExample() {
        setTitle("ドラッグ＆ドロップの実装");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // リスト間のドラッグ＆ドロップ
        JPanel listsPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        listsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // ソースリスト
        sourceModel = new DefaultListModel<>();
        sourceModel.addElement("アイテム 1");
        sourceModel.addElement("アイテム 2");
        sourceModel.addElement("アイテム 3");
        sourceModel.addElement("アイテム 4");
        sourceModel.addElement("アイテム 5");
        
        sourceList = new JList<>(sourceModel);
        sourceList.setDragEnabled(true);
        sourceList.setTransferHandler(new ListTransferHandler());
        
        // ターゲットリスト
        targetModel = new DefaultListModel<>();
        targetList = new JList<>(targetModel);
        targetList.setTransferHandler(new ListTransferHandler());
        
        JScrollPane sourceScroll = new JScrollPane(sourceList);
        sourceScroll.setBorder(BorderFactory.createTitledBorder("ソース"));
        JScrollPane targetScroll = new JScrollPane(targetList);
        targetScroll.setBorder(BorderFactory.createTitledBorder("ターゲット"));
        
        listsPanel.add(sourceScroll);
        listsPanel.add(targetScroll);
        
        // ファイルドロップエリア
        dropArea = new JTextArea(10, 40);
        dropArea.setText("ここにファイルをドロップしてください...");
        dropArea.setEditable(false);
        JScrollPane dropScroll = new JScrollPane(dropArea);
        dropScroll.setBorder(BorderFactory.createTitledBorder("ファイルドロップエリア"));
        
        // ドロップターゲットの設定
        new DropTarget(dropArea, new DropTargetListener() {
            @Override
            public void dragEnter(DropTargetDragEvent dtde) {
                dropArea.setBackground(Color.LIGHT_GRAY);
            }
            
            @Override
            public void dragOver(DropTargetDragEvent dtde) {
                // 必要に応じて処理
            }
            
            @Override
            public void dropActionChanged(DropTargetDragEvent dtde) {
                // 必要に応じて処理
            }
            
            @Override
            public void dragExit(DropTargetEvent dte) {
                dropArea.setBackground(Color.WHITE);
            }
            
            @Override
            public void drop(DropTargetDropEvent dtde) {
                try {
                    dtde.acceptDrop(DnDConstants.ACTION_COPY);
                    Transferable transferable = dtde.getTransferable();
                    
                    if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                        @SuppressWarnings("unchecked")
                        List<File> files = (List<File>) transferable.getTransferData(
                            DataFlavor.javaFileListFlavor);
                        
                        dropArea.setText("ドロップされたファイル:\n");
                        for (File file : files) {
                            dropArea.append("- " + file.getAbsolutePath() + "\n");
                            dropArea.append("  サイズ: " + file.length() + " bytes\n");
                            dropArea.append("  更新日時: " + java.time.LocalDateTime.ofInstant(
                                java.time.Instant.ofEpochMilli(file.lastModified()), 
                                java.time.ZoneId.systemDefault()) + "\n\n");
                        }
                    }
                    
                    dtde.dropComplete(true);
                    dropArea.setBackground(Color.WHITE);
                } catch (Exception e) {
                    e.printStackTrace();
                    dtde.dropComplete(false);
                }
            }
        });
        
        add(listsPanel, BorderLayout.CENTER);
        add(dropScroll, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(null);
    }
    
    // カスタムTransferHandler
    class ListTransferHandler extends TransferHandler {
        @Override
        public boolean canImport(TransferSupport support) {
            return support.isDataFlavorSupported(DataFlavor.stringFlavor);
        }
        
        @Override
        public boolean importData(TransferSupport support) {
            if (!canImport(support)) {
                return false;
            }
            
            JList.DropLocation dl = (JList.DropLocation) support.getDropLocation();
            int index = dl.getIndex();
            
            try {
                String data = (String) support.getTransferable().getTransferData(
                    DataFlavor.stringFlavor);
                
                if (support.getComponent() == targetList) {
                    if (index == -1) {
                        targetModel.addElement(data);
                    } else {
                        targetModel.add(index, data);
                    }
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            return false;
        }
        
        @Override
        protected Transferable createTransferable(JComponent c) {
            @SuppressWarnings("unchecked")
            JList<String> list = (JList<String>) c;
            String value = list.getSelectedValue();
            return new StringSelection(value);
        }
        
        @Override
        public int getSourceActions(JComponent c) {
            return COPY;
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new DragAndDropExample().setVisible(true);
        });
    }
}
```

## 18.3 まとめ

本章では、GUIアプリケーションに命を吹き込むイベント処理の基本を学びました。

- イベント処理は「**イベントソース**」「**イベントオブジェクト**」「**イベントリスナ**」の3要素で構成されます。
- `ActionListener`を始めとする各種リスナをイベントソースに登録することで、ユーザーの操作に応じた処理を実行できます。
- **ラムダ式**を使うことで、イベント処理の記述を大幅に簡潔にできます。

これで、見た目を作り、操作に応答する、インタラクティブなアプリケーションの基礎が固まりました。

## 章末演習

本章で学んだGUIイベント処理を実践的な課題で確認しましょう。

### 演習課題へのアクセス

本書の演習課題は、以下のGitHubリポジトリで提供されています：

**リポジトリ**: `https://github.com/[your-repo]/java-primer-exercises`

### 第19章の課題構成

```
exercises/chapter19/
├── basic/              # 基礎課題（必須）
│   ├── README.md       # 詳細な課題説明
│   └── EventHandling.java
├── advanced/           # 発展課題（推奨）
├── challenge/          # チャレンジ課題（任意）
└── solutions/          # 解答例（実装後に参照）
```

### 学習の目標

本章の演習を通じて以下のスキルを習得します：
- 各種イベントリスナの実装方法
- イベント処理の設計パターン
- インタラクティブなGUIアプリケーションの構築

### 課題の概要

1. **基礎課題**: インタラクティブ描画アプリケーションの作成
2. **発展課題**: フォームバリデータの実装
3. **チャレンジ課題**: ドラッグ&ドロップ機能を持つタスク管理アプリ

詳細な課題内容と実装のヒントは、GitHubリポジトリの各課題フォルダ内のREADME.mdを参照してください。

**次のステップ**: 基礎課題が完了したら、第20章「高度なGUIコンポーネント」に進みましょう。

