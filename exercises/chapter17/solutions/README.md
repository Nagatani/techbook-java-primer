# 第17章 解答例

## 🎯 学習目標
- SwingによるGUIアプリケーションの基本構築
- 基本コンポーネント（JButton、JLabel、JTextField等）の使用
- レイアウトマネージャーの理解と適用
- イベント処理の実装
- ウィンドウとダイアログの操作
- メニューシステムの構築

## 📝 解答例一覧

### 解答例1: 電卓アプリケーション
**ファイル名**: `Calculator.java`

四則演算ができる電卓GUIアプリケーションの解答例です。

**実装内容**:
- GridLayoutを使用した電卓らしいボタン配置
- 四則演算（+、-、×、÷）の実装
- 小数点演算対応
- エラーハンドリング（ゼロ除算、不正入力）
- クリア機能（C、CE）
- 連続計算対応

**技術ポイント**:
- JButtonの配列でボタンを管理
- ActionListenerでイベント処理
- 計算状態をenum で管理
- NumberFormatを使った数値フォーマット

---

### 解答例2: シンプルメモ帳
**ファイル名**: `SimpleNotepad.java`

基本的なテキストエディタの解答例です。

**実装内容**:
- JTextAreaとJScrollPaneを使ったテキスト編集
- メニューバー（ファイル、編集、ヘルプ）
- ファイルの新規作成、開く、保存、名前を付けて保存
- 編集機能（切り取り、コピー、貼り付け、全選択）
- 変更検知とタイトルバー表示（*マーク）
- 終了時の保存確認ダイアログ

**技術ポイント**:
- JMenuBarとJMenuItemの使用
- JFileChooserでファイル選択
- DocumentListenerで文書変更検知
- WindowAdapterでウィンドウクローズ処理
- KeyStrokeでキーボードショートカット

---

### 解答例3: レイアウトデモ
**ファイル名**: `LayoutDemo.java`

各レイアウトマネージャーの特徴を比較できるデモアプリケーションの解答例です。

**実装内容**:
- JTabbedPaneによるタブ切り替え
- BorderLayout、FlowLayout、GridLayout、BoxLayout、CardLayout、GridBagLayoutのデモ
- レイアウトの動的切り替え機能
- 各レイアウトの特徴を分かりやすく表示

**技術ポイント**:
- 各レイアウトマネージャーの特徴と使い分け
- GridBagConstraintsによる複雑な配置
- CardLayoutによるカード切り替え
- BoxLayoutによる柔軟な配置
- revalidate()とrepaint()による再描画

---

### 解答例4: カラーピッカー
**ファイル名**: `ColorPicker.java`

色を選択・調整できるカラーピッカーアプリケーションの解答例です。

**実装内容**:
- RGBスライダーによる色調整
- HSBスライダーによる色調整
- 色のプレビューエリア
- 16進数カラーコード表示・入力
- 色履歴の保存・呼び出し
- プリセットカラー選択
- 補色・類似色表示
- JColorChooserとの連携

**技術ポイント**:
- JSliderとChangeListenerによるリアルタイム更新
- Color.RGBtoHSB()とColor.HSBtoRGB()による色変換
- DefaultListModelによるリスト管理
- カスタムCellRendererによる色表示
- 色相環に基づく類似色計算

## 🔧 実装の工夫点

### 1. エラーハンドリング
- 各アプリケーションで適切な例外処理を実装
- ユーザーにとって分かりやすいエラーメッセージ
- 異常状態からの安全な復帰

### 2. ユーザビリティ
- 直感的な操作性
- キーボードショートカット対応
- 適切なデフォルト値の設定

### 3. イベント処理
- 適切なリスナーの選択と実装
- イベントの無限ループ回避
- EDT（Event Dispatch Thread）での安全な実行

### 4. レイアウト設計
- 各コンポーネントに適したレイアウトマネージャーの選択
- ウィンドウサイズ変更への対応
- コンポーネント間の適切な余白設定

## 📚 参考資料

### Swing コンポーネント
- **JFrame**: メインウィンドウ
- **JPanel**: コンテナパネル
- **JButton**: ボタン
- **JLabel**: ラベル
- **JTextField**: 単行テキスト入力
- **JTextArea**: 複数行テキスト入力
- **JSlider**: スライダー
- **JMenuBar**: メニューバー
- **JMenuItem**: メニュー項目
- **JTabbedPane**: タブペイン

### レイアウトマネージャー
- **BorderLayout**: 5方向配置（North、South、East、West、Center）
- **FlowLayout**: 流れるように左から右へ配置
- **GridLayout**: 格子状配置
- **BoxLayout**: 一方向への配置
- **CardLayout**: カード形式の切り替え
- **GridBagLayout**: 柔軟なグリッド配置

### イベント処理
- **ActionListener**: ボタンクリック、メニュー選択
- **ChangeListener**: スライダー値変更
- **DocumentListener**: テキスト変更
- **WindowListener**: ウィンドウイベント
- **ListSelectionListener**: リスト選択

## ✅ 学習成果確認

この章の解答例を学習することで、以下の技術を習得できます：

- [ ] SwingによるGUIアプリケーション開発の基本
- [ ] 各種レイアウトマネージャーの特徴と使い分け
- [ ] イベント処理の実装方法
- [ ] ファイルI/Oとの連携
- [ ] メニューシステムの構築
- [ ] カスタムコンポーネントの作成
- [ ] 色の扱いとリアルタイム更新

**次のステップ**: これらの基本的なGUI技術を習得したら、第18章の高度なイベント処理やカスタム描画に進みましょう！