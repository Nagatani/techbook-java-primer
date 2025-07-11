# 第18章 GUIプログラミング基礎 - 基礎レベル課題

## 概要
本ディレクトリでは、JavaのSwingフレームワークを使用したGUIプログラミングの基礎を学習します。基本的なコンポーネントの配置から、簡単なイベント処理まで、デスクトップアプリケーション開発の基礎を身につけます。

## 課題一覧

### 課題1: 基本的なウィンドウとレイアウト
**BasicWindowLayout.java**

JFrameとレイアウトマネージャーを使った基本的なウィンドウ設計を実践します。

**要求仕様：**
- JFrameを使用したメインウィンドウの作成
- BorderLayout、FlowLayout、GridLayoutの使い分け
- JPanelを使った複合的なレイアウト構成
- 適切なウィンドウサイズとコンポーネント配置

**実装のポイント：**
```java
// メインウィンドウの設定
JFrame frame = new JFrame("レイアウトデモ");
frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
frame.setSize(600, 400);

// 複合レイアウトの例
JPanel topPanel = new JPanel(new FlowLayout());
JPanel centerPanel = new JPanel(new GridLayout(2, 2));
JPanel bottomPanel = new JPanel(new FlowLayout());

frame.add(topPanel, BorderLayout.NORTH);
frame.add(centerPanel, BorderLayout.CENTER);
frame.add(bottomPanel, BorderLayout.SOUTH);
```

**期待される成果物：**
- ヘッダー、メインコンテンツ、フッターを持つアプリケーション
- リサイズに対応した柔軟なレイアウト
- 適切なコンポーネント間隔とパディング

### 課題2: 基本的なフォーム作成
**SimpleFormApplication.java**

JLabel、JTextField、JButtonを組み合わせた入力フォームを作成します。

**要求仕様：**
- ユーザー情報入力フォーム（名前、メールアドレス、年齢）
- 入力フィールドの適切なラベル付け
- 送信ボタンとクリアボタンの実装
- 基本的な入力値の表示機能

**実装のポイント：**
```java
// フォームの作成
JPanel formPanel = new JPanel(new GridBagLayout());
GridBagConstraints gbc = new GridBagConstraints();

// ラベルとテキストフィールドの配置
gbc.gridx = 0; gbc.gridy = 0;
formPanel.add(new JLabel("名前:"), gbc);

gbc.gridx = 1;
JTextField nameField = new JTextField(20);
formPanel.add(nameField, gbc);

// ボタンのアクションリスナー
submitButton.addActionListener(e -> {
    String name = nameField.getText();
    // 処理...
});
```

### 課題3: 簡易電卓の実装
**SimpleCalculator.java**

基本的な四則演算ができる電卓アプリケーションを作成します。

**要求仕様：**
- 数字ボタン（0-9）と演算子ボタン（+、-、*、/）
- 結果表示用のディスプレイ（JTextField）
- クリアボタンと等号ボタン
- 基本的な計算ロジックの実装

**実装のポイント：**
```java
// ボタンパネルの作成
JPanel buttonPanel = new JPanel(new GridLayout(4, 4, 5, 5));

// 数字ボタンの作成
for (int i = 1; i <= 9; i++) {
    JButton btn = new JButton(String.valueOf(i));
    btn.addActionListener(e -> {
        display.setText(display.getText() + btn.getText());
    });
    buttonPanel.add(btn);
}
```

### 課題4: 色選択アプリケーション
**ColorChooserApp.java**

JSliderやJComboBoxを使った色選択インターフェイスを実装します。

**要求仕様：**
- RGB値をJSliderで調整
- 選択した色のプレビュー表示
- プリセット色のJComboBox
- 選択した色の16進数表示

**実装のポイント：**
```java
// RGBスライダーの設定
JSlider redSlider = new JSlider(0, 255, 128);
redSlider.addChangeListener(e -> updateColor());

// 色のプレビューパネル
JPanel colorPreview = new JPanel();
colorPreview.setPreferredSize(new Dimension(100, 100));
colorPreview.setBackground(new Color(r, g, b));
```

## 学習のポイント

1. **コンポーネントの基本操作**
   - JFrame、JPanel、JLabel、JButton、JTextField の使い方
   - コンポーネントの階層構造の理解
   - 適切なコンテナの選択

2. **レイアウトマネージャーの理解**
   - BorderLayout: 5つの領域への配置
   - FlowLayout: 左から右への流し込み配置
   - GridLayout: 格子状の均等配置
   - GridBagLayout: 柔軟な格子配置（応用）

3. **イベント処理の基礎**
   - ActionListenerの実装
   - ラムダ式を使った簡潔な記述
   - イベントソースの識別

4. **Swingの基本原則**
   - コンポーネントの追加順序
   - setVisible(true)のタイミング
   - スレッドセーフティの基礎（EDTの存在を意識）

## 提出物

1. 各課題のソースコード（*.java）
2. 実行画面のスクリーンショット
3. 各課題で工夫した点のメモ
4. 遭遇した問題と解決方法のドキュメント

## 評価基準

- **機能の実装（40%）**: 要求仕様を満たしているか
- **UIの完成度（25%）**: 見た目の美しさ、使いやすさ
- **コード品質（25%）**: 可読性、適切な命名、構造化
- **創意工夫（10%）**: 追加機能や改善点

## 参考リソース

- [Oracle Swing Tutorial](https://docs.oracle.com/javase/tutorial/uiswing/)
- [Java Swing Components](https://docs.oracle.com/javase/8/docs/api/javax/swing/package-summary.html)
- 本書第18章 Part A: 基本コンポーネントとレイアウト