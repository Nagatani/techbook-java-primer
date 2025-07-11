# 第19章 GUIのイベント処理 - 基礎レベル課題

## 概要
本ディレクトリでは、GUIアプリケーションにおけるイベント処理の基礎を学習します。マウス操作、キーボード入力、コンポーネントの状態変化など、ユーザーとの対話を実現する技術を身につけます。

## 課題一覧

### 課題1: クリックカウンター
**ClickCounter.java**

ボタンクリックをカウントする基本的なイベント処理を実装します。

**要求仕様：**
- 複数のボタンそれぞれのクリック回数をカウント
- 合計クリック数の表示
- リセット機能
- ラムダ式を使用したイベントハンドラー実装

**実装のポイント：**
```java
// ラムダ式でのActionListener実装
JButton button = new JButton("クリック");
button.addActionListener(e -> {
    clickCount++;
    updateDisplay();
});

// メソッド参照の使用
resetButton.addActionListener(this::resetCounters);
```

**期待される動作：**
```
ウィンドウタイトル: "Click Counter"
┌─────────────────────────────┐
│ ボタンA: 5回                │
│ [ボタンA]                   │
│                             │
│ ボタンB: 3回                │
│ [ボタンB]                   │
│                             │
│ 合計: 8回                   │
│ [リセット]                  │
└─────────────────────────────┘
```

### 課題2: マウストラッカー
**MouseTracker.java**

マウスの動きとクリックを追跡するアプリケーションを作成します。

**要求仕様：**
- マウスの現在位置をリアルタイム表示
- クリック位置に印を表示
- マウスドラッグの軌跡を描画
- 右クリックでメニュー表示

**実装のポイント：**
```java
// MouseListenerとMouseMotionListenerの実装
addMouseListener(new MouseAdapter() {
    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON3) {
            showPopupMenu(e.getX(), e.getY());
        } else {
            addClickMark(e.getPoint());
        }
    }
});

addMouseMotionListener(new MouseMotionAdapter() {
    @Override
    public void mouseMoved(MouseEvent e) {
        updateCoordinates(e.getX(), e.getY());
    }
});
```

### 課題3: キーボードショートカット
**KeyboardShortcuts.java**

キーボード操作に反応するアプリケーションを実装します。

**要求仕様：**
- 文字キーの押下を検出して表示
- Ctrl+S、Ctrl+O などのショートカット実装
- 矢印キーでの図形移動
- ESCキーでのリセット機能

**実装のポイント：**
```java
// KeyListenerの実装
addKeyListener(new KeyAdapter() {
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_S) {
            saveAction();
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            moveLeft();
        }
    }
});

// InputMapとActionMapの使用（推奨）
InputMap inputMap = getInputMap(WHEN_IN_FOCUSED_WINDOW);
ActionMap actionMap = getActionMap();

inputMap.put(KeyStroke.getKeyStroke("ctrl S"), "save");
actionMap.put("save", new AbstractAction() {
    @Override
    public void actionPerformed(ActionEvent e) {
        saveAction();
    }
});
```

### 課題4: リアルタイムテキスト処理
**TextProcessor.java**

テキスト入力をリアルタイムで処理するアプリケーションです。

**要求仕様：**
- 入力文字数のリアルタイムカウント
- 禁止ワードの自動検出とハイライト
- 入力制限（最大文字数）の実装
- Undo/Redo機能

**実装のポイント：**
```java
// DocumentListenerの実装
textArea.getDocument().addDocumentListener(new DocumentListener() {
    @Override
    public void insertUpdate(DocumentEvent e) {
        processTextChange();
    }
    
    @Override
    public void removeUpdate(DocumentEvent e) {
        processTextChange();
    }
    
    @Override
    public void changedUpdate(DocumentEvent e) {
        // プレーンテキストでは発生しない
    }
});

// UndoManagerの使用
UndoManager undoManager = new UndoManager();
textArea.getDocument().addUndoableEditListener(undoManager);
```

## 学習のポイント

1. **イベントリスナーの種類と使い分け**
   - ActionListener: ボタンクリック等
   - MouseListener/MouseMotionListener: マウス操作
   - KeyListener: キーボード入力
   - DocumentListener: テキスト変更

2. **イベント処理の実装方法**
   - 匿名内部クラス
   - ラムダ式
   - メソッド参照
   - 独立したリスナークラス

3. **イベントオブジェクトの活用**
   - イベントソースの特定
   - 修飾キーの状態確認
   - マウス座標の取得

4. **スレッドセーフティ**
   - EDT上での処理
   - SwingUtilities.invokeLater()の必要性

## 提出物

1. 各課題のソースコード（*.java）
2. 実行画面のスクリーンショット
3. イベント処理フローの説明図
4. 学習内容のまとめ

## 評価基準

- **機能の完成度（40%）**: すべての要求仕様を満たしているか
- **イベント処理の適切性（30%）**: 適切なリスナーの選択と実装
- **コード品質（20%）**: 可読性、保守性
- **ユーザビリティ（10%）**: 操作の快適性

## 参考リソース

- [How to Write Event Listeners](https://docs.oracle.com/javase/tutorial/uiswing/events/)
- [Event Handling](https://docs.oracle.com/javase/tutorial/uiswing/events/intro.html)
- 本書第19章 Part A: イベント処理入門