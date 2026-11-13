# 第19章 高度なイベント処理パターン - 演習課題

書籍『Java入門』第19章「高度なイベント処理パターン」の演習課題です。

## 基礎課題（[basic/](basic/)）

- お絵かきパネル（MouseListenerとMouseMotionListener、右クリックのメニュー）
- 矢印キーで四角を動かす（InputMapとActionMapによるキーバインディング）
- 文字数を数えながら書く投稿画面（DocumentListenerによる入力の監視）
- 在庫の変化を知らせる（EventObjectとEventListenerによるカスタムイベント）
- このフォルダーのJavaファイルは解答例です（課題ごとに`task1-paint/`などのフォルダーに分けてあります）

## 発展課題（[advanced/](advanced/)）

- 入力が止まってから検索する（javax.swing.Timerによるデバウンス）
- 素数を数える（SwingWorkerによる時間のかかる処理、進捗の表示とキャンセル）
- このフォルダーのJavaファイルは解答例です

## チャレンジ課題（[challenge/](challenge/)）

- タスクボード（ドラッグ&ドロップ、キーバインディング、PropertyChangeSupportによる通知）
- このフォルダーのJavaファイルは解答例です

## 動作確認のプログラム

画面に依存しないクラスには、画面を開かずに動きを確かめるプログラム（`〜Check.java`）を付けています。
期待する値と実際の値を比べて「OK」か「NG」を表示します。

## 参考の例（[examples/](examples/)）

マウス・キーボード・入力の監視を組み合わせた、500〜800行ほどの大きめのプログラムです。
第18章と第19章の内容をまとめて使っているので、本章を読み終えてから読むと理解しやすくなります。
本文で扱っていないAPI（`UndoManager`、`Preferences`、`ImageIO`など）も使っています。

- `InteractiveDrawing.java`: マウスのドラッグで線や図形を描く。元に戻す・やり直す、画像の保存
- `MouseTracker.java`: マウスの座標・軌跡・速度・クリックを記録して表示する
- `FormValidator.java`: DocumentListenerで入力のたびに検査するフォーム。Enterで次の欄へ移るキーバインディング
- `KeyboardShortcuts.java`: InputMapとActionMapによるショートカットキー、UndoManagerによる元に戻す機能を持つテキストエディター
- `MenuSystem.java`: 階層のあるメニュー、右クリックのメニュー、最近使ったファイルの一覧
- `ResponsiveGUI.java`: スライダーと入力欄に合わせて表示がすぐ変わる画面、Timerによる進捗の表示
- `EventHandlingBasics.java`: マウス・キーボード・フォーカスのイベントを記録して一覧にする

どれもクラスを1つのファイルにまとめてあり、そのフォルダーで`javac`と`java`を実行すると起動できます。

```bash
cd exercises/chapter19/examples
javac InteractiveDrawing.java
java InteractiveDrawing
```
