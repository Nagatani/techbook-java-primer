# 第17章 GUIプログラミングの基礎：よくあるエラーと対処法

書籍『Java入門』第17章「GUIプログラミングの基礎」で扱う内容に関連して、よく遭遇するエラーとその対処法をまとめています。

GUI基礎の学習において遭遇しやすい典型的なエラーとその対処法を以下にまとめます。

## レイアウトマネージャーの問題

### 問題：コンポーネントが表示されない

エラー症状。

```java
JFrame frame = new JFrame("タイトル");
frame.add(new JButton("ボタン"));
frame.setVisible(true);
// ボタンが表示されない
```

原因
- `frame.setSize()`または`frame.pack()`が呼び出されていない
- レイアウトマネージャーの制約が正しく設定されていない

対処法。

```java
JFrame frame = new JFrame("タイトル");
frame.setSize(300, 200);  // サイズを明示的に設定
frame.add(new JButton("ボタン"));
frame.setVisible(true);

// または
frame.pack();  // 推奨サイズに自動調整
```

これはコンポーネントが表示されない問題の対処法です。setSize()またはpack()を使用してウィンドウサイズを設定することで、正しくコンポーネントが表示されます。

### 問題：BorderLayoutで複数のコンポーネントが重なる

エラー症状。

```java
frame.setLayout(new BorderLayout());
frame.add(new JButton("ボタン1"));
frame.add(new JButton("ボタン2"));
// ボタン1が見えない
```

これはBorderLayoutで位置指定なしにコンポーネントを追加した例です。後から追加したボタン2がボタン1を上書きしてしまいます。

原因
- BorderLayoutの位置指定が省略されている
- 同じ位置に複数のコンポーネントが配置されている

対処法。

```java
frame.setLayout(new BorderLayout());
frame.add(new JButton("ボタン1"), BorderLayout.NORTH);
frame.add(new JButton("ボタン2"), BorderLayout.SOUTH);
```

これは正しい位置指定の例です。BorderLayout.NORTHとBorderLayout.SOUTHを指定することで、両方のボタンが正しく表示されます。

## コンポーネントの表示問題

### 問題：JLabelの背景色が反映されない

エラー症状。

```java
JLabel label = new JLabel("メッセージ");
label.setBackground(Color.RED);
// 背景色が表示されない
```

これは背景色が表示されない問題の例です。JLabelは既定で透明（`isOpaque()`が`false`）なので、`setBackground`で色を指定しても背景を塗りません。
なお、JPanelは標準のLook&Feelでは既定で不透明なので、`setBackground`だけで背景色が表示されます。

原因
- 既定で透明なコンポーネント（JLabelなど）に`setOpaque(true)`を設定していない
- コンポーネントが透明なため、親コンポーネントの背景がそのまま見えている

対処法。

```java
JLabel label = new JLabel("メッセージ");
label.setBackground(Color.RED);
label.setOpaque(true);  // 不透明に設定
```

これは背景色を正しく表示させる対処法です。setOpaque(true)により、ラベルが不透明になり、背景色が表示されます。

### 問題：JTextFieldが編集できない

エラー症状。

```java
JTextField textField = new JTextField("初期値");
// テキストが編集できない
```

これは編集不可能なテキストフィールドの問題例です。デフォルトでは編集可能ですが、何らかの理由で編集できない状態になっています。

原因
- `setEditable(false)`が設定されている
- フォーカスが取得できない状態

対処法。

```java
JTextField textField = new JTextField("初期値");
textField.setEditable(true);   // 編集可能に設定
textField.setFocusable(true);  // フォーカス取得可能に設定
```

これは編集可能にする対処法です。setEditable(true)とsetFocusable(true)により、テキストフィールドが正常に編集できるようになります。

## イベントディスパッチスレッドの問題

### 問題：GUI更新でException in thread "AWT-EventQueue-0"が発生

エラーメッセージ。
```
Exception in thread "AWT-EventQueue-0" java.lang.NullPointerException
	at SwingApplication.updateUI(SwingApplication.java:45)
```

原因
- EDT（Event Dispatch Thread）以外からGUIコンポーネントにアクセスしている
- コンポーネントがnullのまま使用されている

対処法。

```java
// 別スレッドからGUIを更新する場合
SwingUtilities.invokeLater(() -> {
    label.setText("更新されたテキスト");
});

// コンポーネントのnullチェック
if (label != null) {
    label.setText("安全な更新");
}
```

これはEDT関連エラーの対処法です。SwingUtilities.invokeLater()を使用してEDT上で実行し、nullチェックでNullPointerExceptionを防ぎます。

## コンポーネントのサイズ設定

### 問題：JTextAreaが1行しか表示されない

エラー症状。

```java
JTextArea textArea = new JTextArea();
textArea.setText("長いテキスト...");
// 1行しか見えない
```

これはJTextAreaが1行しか表示されない問題の例です。デフォルトのJTextAreaは1行分の高さしか確保されません。

原因
- JScrollPaneに配置されていない
- 必要なサイズが設定されていない

対処法。

```java
JTextArea textArea = new JTextArea(10, 30);  // 行数と列数を指定
textArea.setLineWrap(true);  // 行の折り返しを有効
textArea.setWrapStyleWord(true);  // 単語単位で折り返し

JScrollPane scrollPane = new JScrollPane(textArea);
frame.add(scrollPane);
```

これは正しい対処法です。行数と列数を指定し、JScrollPaneで囲むことで、指定した行数と列数に合った大きさで複数行のテキストが表示されます。

### 問題：JButtonのサイズが意図通りにならない

エラー症状。

```java
JButton button = new JButton("ボタン");
button.setSize(200, 100);
// サイズが反映されない
```

これはボタンサイズが反映されない問題の例です。レイアウトマネージャーがsetSize()の設定を無視します。

原因
- レイアウトマネージャーがサイズを制御している
- `setPreferredSize()`を使用していない

対処法。

```java
JButton button = new JButton("ボタン");
button.setPreferredSize(new Dimension(200, 100));

// または、レイアウトマネージャーをnullに設定
frame.setLayout(null);
button.setBounds(10, 10, 200, 100);
```

これは2つの対処法です。setPreferredSize()を使うか、レイアウトマネージャーをnullにしてsetBounds()で絶対位置とサイズを指定します。

## 画面のちらつき問題

### 問題：コンポーネントの更新時に画面がちらつく

エラー症状。

```java
// 大量のコンポーネントを動的に追加・削除する際のちらつき
panel.removeAll();
for (int i = 0; i < 100; i++) {
    panel.add(new JLabel("Item " + i));
}
panel.revalidate();
panel.repaint();
```

これは画面がちらつく問題の例です。コンポーネントの追加・削除が1つずつ画面に反映されるため、ちらつきが発生します。

原因
- 個別の更新処理が画面に反映されている
- ダブルバッファリングが無効になっている

対処法。

```java
// 更新処理をバッチ化
panel.setVisible(false);  // 一時的に非表示
panel.removeAll();
for (int i = 0; i < 100; i++) {
    panel.add(new JLabel("Item " + i));
}
panel.revalidate();
panel.repaint();
panel.setVisible(true);  // 再表示

// または、SwingUtilities.invokeLaterを使用
SwingUtilities.invokeLater(() -> {
    // 更新処理
    panel.removeAll();
    // コンポーネント追加
    panel.revalidate();
    panel.repaint();
});
```

これはちらつきを防ぐ2つの対処法です。一時的に非表示にするか、EDT上でバッチ処理することで、スムーズな更新が可能になります。

## デバッグのヒント

### 1. コンポーネントの境界を可視化する

```java
// 境界線を追加してレイアウトを確認
component.setBorder(BorderFactory.createLineBorder(Color.RED));

// または、背景色を設定
component.setBackground(Color.YELLOW);
component.setOpaque(true);
```

これはデバッグ用のコードです。境界線や背景色を設定することで、コンポーネントの実際のサイズと位置を視覚的に確認できます。

### 2. レイアウトマネージャーの確認

```java
// 現在のレイアウトマネージャーを確認
IO.println("Layout: " + container.getLayout());

// コンポーネントのサイズ情報を確認
IO.println("Size: " + component.getSize());
IO.println("Preferred Size: " + component.getPreferredSize());
```

これはレイアウト情報を確認するデバッグコードです。コンソールに現在のレイアウトマネージャーとコンポーネントのサイズ情報が出力されます。

### 3. イベントディスパッチスレッドの確認

```java
// 現在のスレッドがEDTかどうかを確認
if (SwingUtilities.isEventDispatchThread()) {
    IO.println("EDTで実行中");
} else {
    IO.println("EDTではないスレッドで実行中");
}
```

これはスレッドを確認するデバッグコードです。GUI操作が正しくEDT上で実行されているかを確認でき、スレッド関連の問題の診断に役立ちます。

これらの対処法を参考に、GUI基礎の学習を進めてください。問題が解決しない場合は、エラーメッセージを詳しく確認し、最小限の再現可能なコードで問題を特定することが重要です。
