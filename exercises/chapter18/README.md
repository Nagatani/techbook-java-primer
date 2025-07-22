# 第18章: イベント処理 - 練習課題

この章では、Java Swingにおけるイベント処理の基本概念から高度な応用まで、実践的なGUIアプリケーション開発を通じて学習します。

## 📚 学習目標

- 各種イベントリスナーの実装方法を理解する
- リアルタイムでのUI更新技術を習得する
- キーボードショートカットの実装方法を学ぶ
- レスポンシブなユーザーインターフェイスの設計を身につける
- イベント駆動プログラミングの理念を理解する

## 🎯 練習課題一覧

### 基本課題

#### 課題1: キーボードショートカット
**ファイル**: `basic/KeyboardShortcuts.java`

プロフェッショナルなテキストエディタの基本機能を実装：
- **主要機能**: Ctrl+N(新規)、Ctrl+O(開く)、Ctrl+S(保存)、Ctrl+Z(元に戻す)
- **UI要素**: メニューバー、ステータスバー、アンドゥ機能
- **学習ポイント**: KeyStroke、ActionMap、UndoManager の使用方法

#### 課題2: レスポンシブGUI
**ファイル**: `basic/ResponsiveGUI.java`

リアルタイムに応答するインタラクティブなUI：
- **主要機能**: スライダー連動色変更、リアルタイム検索、自動プログレス表示
- **UI要素**: カラーピッカー、検索フィルタ、動的レイアウト変更
- **学習ポイント**: ChangeListener、DocumentListener、Timer の活用

#### 課題3: イベントハンドリングの基礎
**ファイル**: `basic/EventHandlingBasics.java`

包括的なイベント処理システム：
- **主要機能**: 全種類のマウス・キーボードイベント検出、詳細ログ表示
- **UI要素**: イベントログ、リアルタイム統計、インタラクティブテストパネル
- **学習ポイント**: MouseListener、KeyListener、FocusListener の実装

## 💡 実装のベストプラクティス

### 1. イベントリスナーの効率的な実装

```java
// ラムダ式を活用した簡潔なリスナー実装
button.addActionListener(e -> {
    logEvent("ActionEvent", "ボタンがクリックされました");
    updateUI();
});

// アダプタークラスを使用した必要なメソッドのみの実装
textField.addKeyListener(new KeyAdapter() {
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            processInput();
        }
    }
});
```

### 2. リアルタイム更新の最適化

```java
// DocumentListener でのリアルタイム検索
searchField.getDocument().addDocumentListener(new DocumentListener() {
    public void insertUpdate(DocumentEvent e) { filterData(); }
    public void removeUpdate(DocumentEvent e) { filterData(); }
    public void changedUpdate(DocumentEvent e) { filterData(); }
});

// Timer を使用した定期更新
Timer updateTimer = new Timer(100, e -> {
    updateProgressBar();
    refreshDisplay();
});
```

### 3. キーボードショートカットの体系的実装

```java
// InputMap と ActionMap の組み合わせ
InputMap inputMap = component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
ActionMap actionMap = component.getActionMap();

inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK), "save");
actionMap.put("save", new AbstractAction() {
    public void actionPerformed(ActionEvent e) {
        saveDocument();
    }
});
```

## 🔧 技術的特徴

### イベント処理アーキテクチャ
- **Observer パターン**: Java Swing のイベントモデルの基礎
- **リスナーインターフェイス**: 各種イベントタイプに対応した専用インターフェイス
- **イベントディスパッチ**: EDT (Event Dispatch Thread) での安全な UI 更新

### パフォーマンス最適化
- **イベントフィルタリング**: 不要なイベントの早期除外
- **バッチ更新**: 複数の変更をまとめて処理
- **遅延評価**: 必要なタイミングでの計算実行

### ユーザビリティ向上
- **視覚的フィードバック**: ホバー効果、状態変化の明示
- **キーボードアクセシビリティ**: マウス操作に依存しないインターフェイス
- **レスポンシブデザイン**: ウィンドウサイズや設定変更への動的対応

## 📈 学習の進め方

### ステップ1: 基本イベント処理の理解
1. `EventHandlingBasics.java` で各種リスナーの動作を観察
2. ログ出力を通じてイベントの発生タイミングを理解
3. マウス・キーボード・フォーカスイベントの特性を把握

### ステップ2: リアルタイム処理の実装
1. `ResponsiveGUI.java` でライブ更新機能を構築
2. ChangeListener と DocumentListener の使い分けを学習
3. Timer クラスを使用した定期処理の実装

### ステップ3: 高度なUI機能の開発
1. `KeyboardShortcuts.java` でプロフェッショナルなエディタ機能を実装
2. メニューシステムとショートカットの連携
3. アンドゥ・リドゥ機能の実装

## 🎨 UI/UX 設計原則

### 直感的な操作性
- **標準的なショートカット**: Ctrl+C、Ctrl+V等の一般的なキーバインド
- **視覚的手がかり**: ツールチップ、ハイライト、アニメーション
- **即座のフィードバック**: 操作に対する瞬時の応答

### アクセシビリティ
- **キーボードナビゲーション**: タブ移動、ショートカットキー
- **色覚対応**: 色だけに依存しない情報伝達
- **スクリーンリーダー対応**: 適切なラベル設定

## 🚀 発展的な学習課題

### 高度なイベント処理
- **カスタムイベント**: 独自のイベントクラス作成
- **イベントフィルタリング**: EventQueue の活用
- **非同期処理**: SwingWorker との連携

### 現代的なUI技術
- **アニメーション**: Swing Timer を使用したスムーズな遷移
- **ドラッグ&ドロップ**: TransferHandler の実装
- **コンテキストメニュー**: JPopupMenu の活用

### パフォーマンス最適化
- **仮想化**: 大量データの効率的表示
- **メモリ管理**: リスナーの適切な削除
- **レンダリング最適化**: カスタムペイント処理

## ⚡ よくある問題と解決策

### 問題1: EDT (Event Dispatch Thread) 違反
**解決策**: SwingUtilities.invokeLater() の使用
```java
SwingUtilities.invokeLater(() -> {
    // UI更新処理
    updateGUI();
});
```

### 問題2: メモリリーク
**解決策**: リスナーの適切な削除
```java
component.removeActionListener(listener);
timer.stop();
```

### 問題3: 過度なイベント発生
**解決策**: イベントのデバウンス処理
```java
Timer debounceTimer = new Timer(300, e -> {
    performSearch();
    debounceTimer.stop();
});
debounceTimer.setRepeats(false);
debounceTimer.restart();
```

この章を通じて、現代的なGUIアプリケーション開発に必要なイベント処理技術を総合的に習得し、ユーザーフレンドリーなインターフェイスの設計能力を身につけることができます。